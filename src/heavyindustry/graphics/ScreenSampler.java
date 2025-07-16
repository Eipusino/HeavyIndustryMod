package heavyindustry.graphics;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import mindustry.game.EventType.Trigger;

public final class ScreenSampler {
	private static final FrameBuffer worldBuffer = new FrameBuffer(), uiBuffer = new FrameBuffer();

	private static FrameBuffer currBuffer;
	private static boolean activity = false;

	private ScreenSampler() {}

	/**
	 * Load Events for ScreenSampler.
	 * If you try to load it a second time, nothing will happen.
	 */
	public static void setup() {
		if (activity) return;//forbid setup sampler twice.

		Events.run(Trigger.preDraw, ScreenSampler::beginWorld);
		Events.run(Trigger.postDraw, ScreenSampler::endWorld);

		Events.run(Trigger.uiDrawBegin, ScreenSampler::beginUI);
		Events.run(Trigger.uiDrawEnd, ScreenSampler::endUI);
		activity = true;
	}

	/** @return Has whether set up. */
	public static boolean isActivity() {
		return activity;
	}

	private static void beginWorld() {
		currBuffer = worldBuffer;
		worldBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
		worldBuffer.begin(Color.clear);
	}

	private static void endWorld() {
		currBuffer = null;
		worldBuffer.end();
	}

	private static void beginUI() {
		currBuffer = uiBuffer;
		uiBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
		uiBuffer.begin(Color.clear);
		worldBuffer.blit(HShaders.distBase);
	}

	private static void endUI() {
		currBuffer = null;
		uiBuffer.end();
		uiBuffer.blit(HShaders.distBase);
	}

	/** Draw the current screen texture onto the screen using the passed shader. */
	public static void blit(Shader shader) {
		blit(shader, 0);
	}

	/**
	 * Draw the current screen texture onto the screen using the passed shader.
	 *
	 * @param unit Texture units bound to screen sampling textures
	 */
	public static void blit(Shader shader, int unit) {
		if (currBuffer == null) throw new IllegalStateException("currently no buffer bound");

		currBuffer.getTexture().bind(unit);
		Draw.blit(shader);
	}

	/**
	 * Transfer the current screen texture to a{@linkplain FrameBuffer frame buffer}, This will become a copy that can be used to temporarily store screen content.
	 *
	 * @param target Target buffer for transferring screen textures.
	 * @param clear  Is the frame buffer cleared before transferring.
	 */
	public static void getToBuffer(FrameBuffer target, boolean clear) {
		if (currBuffer == null) throw new IllegalStateException("currently no buffer bound");

		if (clear) {
			target.begin(Color.clear);
		} else target.begin();

		currBuffer.blit(HShaders.distBase);
		target.end();
	}
}