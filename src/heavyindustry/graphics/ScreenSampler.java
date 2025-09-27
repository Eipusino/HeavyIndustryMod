package heavyindustry.graphics;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.GL30;
import arc.graphics.Gl;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.GLFrameBuffer;
import arc.graphics.gl.Shader;
import arc.util.serialization.Jval;
import heavyindustry.util.Lazy;
import mindustry.Vars;
import mindustry.game.EventType.Trigger;
import mindustry.graphics.Layer;
import mindustry.graphics.Pixelator;

import java.lang.reflect.Field;

public final class ScreenSampler {
	private static final Field lastBoundFramebufferField, bufferField;

	private static FrameBuffer worldBuffer, uiBuffer, currBuffer;

	private static final Lazy<FrameBuffer> pixelatorBuffer;

	private static boolean activity = false;

	static {
		try {
			lastBoundFramebufferField = GLFrameBuffer.class.getDeclaredField("lastBoundFramebuffer");
			lastBoundFramebufferField.setAccessible(true);
			bufferField = Pixelator.class.getDeclaredField("buffer");
			bufferField.setAccessible(true);

			pixelatorBuffer = new Lazy<>(() -> {
				try {
					return (FrameBuffer) bufferField.get(Vars.renderer.pixelator);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	private ScreenSampler() {}

	public static void resetMark() {
		Core.settings.remove("sampler.setup");
	}

	/**
	 * Load Events for ScreenSampler.
	 * If you try to load it a second time, nothing will happen.
	 */
	public static void setup() {
		if (activity) return;

		Jval jval = Jval.read(Core.settings.getString("sampler.setup", "{enabled: false}"));

		if (!jval.getBool("enabled", false)) {
			jval = Jval.newObject();
			jval.put("enabled", true);
			jval.put("className", ScreenSampler.class.getName());
			jval.put("worldBuffer", "worldBuffer");
			jval.put("uiBuffer", "uiBuffer");

			worldBuffer = new FrameBuffer();
			uiBuffer = new FrameBuffer();

			Core.settings.put("sampler.setup", jval.toString());

			Events.run(Trigger.draw, () -> {
				Draw.draw(Layer.min - 0.001f, ScreenSampler::beginWorld);
				Draw.draw(Layer.end + 0.001f, ScreenSampler::endWorld);
			});

			Events.run(Trigger.uiDrawBegin, ScreenSampler::beginUI);
			Events.run(Trigger.uiDrawEnd, ScreenSampler::endUI);
		} else {
			try {
				String className = jval.getString("className");
				String worldBufferName = jval.getString("worldBuffer");
				String uiBufferName = jval.getString("uiBuffer");
				Class<?> clazz = Class.forName(className);
				Field worldBufferField = clazz.getDeclaredField(worldBufferName);
				Field uiBufferField = clazz.getDeclaredField(uiBufferName);

				worldBufferField.setAccessible(true);
				uiBufferField.setAccessible(true);
				worldBuffer = (FrameBuffer) worldBufferField.get(null);
				uiBuffer = (FrameBuffer) uiBufferField.get(null);

				Events.run(Trigger.preDraw, () -> currBuffer = worldBuffer);
				Events.run(Trigger.postDraw, () -> currBuffer = null);
				Events.run(Trigger.uiDrawBegin, () -> currBuffer = uiBuffer);
				Events.run(Trigger.uiDrawEnd, () -> currBuffer = null);
			} catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		activity = true;
	}

	/** @return Has whether set up. */
	public static boolean isActivity() {
		return activity;
	}

	private static void beginWorld() {
		if (Vars.renderer.pixelate) {
			currBuffer = pixelatorBuffer.get();
		} else {
			currBuffer = worldBuffer;

			if (currBuffer.isBound()) return;

			worldBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
			worldBuffer.begin(Color.clear);
		}
	}

	private static void endWorld() {
		if (!Vars.renderer.pixelate) {
			worldBuffer.end();
			blitBuffer(worldBuffer, null);
		}
	}

	private static void beginUI() {
		currBuffer = uiBuffer;

		if (uiBuffer.isBound()) return;

		uiBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
		uiBuffer.begin(Color.clear);

		if (!Vars.renderer.pixelate) blitBuffer(worldBuffer, uiBuffer);
		else blitBuffer(pixelatorBuffer.get(), uiBuffer);
	}

	private static void endUI() {
		currBuffer = null;
		uiBuffer.end();
		blitBuffer(uiBuffer, null);
	}

	/**
	 * Draw the current screen texture onto the screen using the passed shader.
	 *
	 * @param unit Texture units bound to screen sampling textures
	 */
	public static void blit(Shader shader, int unit) {
		if (currBuffer != null) {
			currBuffer.getTexture().bind(unit);
			Draw.blit(shader);
		}
	}

	private static void blitBuffer(FrameBuffer from, FrameBuffer to) {
		if (Core.gl30 == null) {
			from.blit(HShaders.distBase);

			return;
		}

		try {
			GLFrameBuffer<?> target = to == null ? (GLFrameBuffer<?>) lastBoundFramebufferField.get(from) : to;
			Gl.bindFramebuffer(GL30.GL_READ_FRAMEBUFFER, from.getFramebufferHandle());
			Gl.bindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, target == null ? 0 : target.getFramebufferHandle());
			Core.gl30.glBlitFramebuffer(
					0, 0,
					from.getWidth(), from.getHeight(),
					0, 0,
					target == null ? Core.graphics.getWidth() : target.getWidth(),
					target == null ? Core.graphics.getHeight() : target.getHeight(),
					Gl.colorBufferBit, Gl.nearest);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Transfer the current screen texture to a{@linkplain FrameBuffer frame buffer}, This will become a copy that can be used to temporarily store screen content.
	 *
	 * @param target Target buffer for transferring screen textures.
	 * @param clear  Is the frame buffer cleared before transferring.
	 */
	public static void getToBuffer(FrameBuffer target, boolean clear) {
		if (currBuffer == null) return;

		if (clear) target.begin(Color.clear);
		else target.begin();

		if (Core.gl30 == null) {
			currBuffer.blit(HShaders.distBase);

			return;
		}

		Gl.bindFramebuffer(GL30.GL_READ_FRAMEBUFFER, currBuffer.getFramebufferHandle());
		Gl.bindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, target.getFramebufferHandle());
		Core.gl30.glBlitFramebuffer(0, 0,
				currBuffer.getWidth(), currBuffer.getHeight(),
				0, 0,
				target.getWidth(), target.getHeight(),
				Gl.colorBufferBit, Gl.nearest);
		target.end();
	}
}