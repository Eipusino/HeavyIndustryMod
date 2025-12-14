package heavyindustry.graphics;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Tmp;

import static mindustry.Vars.renderer;

public final class SlashRenderer {
	private static final Seq<SlashData> slashes = new Seq<>(SlashData.class);
	private static int maxCount = 4;
	private static SlashShader slashShader;
	private static int slashIndex = 0;
	private static FrameBuffer buffer;

	private SlashRenderer() {}

	private static void createShader() {
		if (maxCount >= 1021) return; //Exceeds maximum number of registers for a single shader

		if (slashShader != null) {
			maxCount = Math.min(1021, maxCount * 2);
			slashShader.dispose();
		}

		String prevFrag = Shader.prependFragmentCode;
		Shader.prependFragmentCode = "#define MAX_COUNT " + maxCount + "\n";

		slashShader = new SlashShader();

		Shader.prependFragmentCode = prevFrag;
	}

	public static void init() {
		createShader();
		buffer = new FrameBuffer();
	}

	public static void addSlash(float x, float y, float a, float off, float length, float width, float color) {
		if (off <= 0.001f) return;
		if (slashes.size <= slashIndex) slashes.add(new SlashData());

		//Pool slashes
		SlashData slash = slashes.items[slashIndex];
		slash.set(x, y, a, off, length, width, color);

		slashIndex++;
	}

	public static void draw() {
		Draw.draw(HLayer.begin - 0.1f, () -> {
			buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
			buffer.begin();
		});

		Draw.draw(HLayer.end + 1f, () -> {
			buffer.end();

			while (slashes.size > maxCount) createShader();

			float[] slashArray = new float[slashIndex * 4];
			for (int i = 0; i < slashIndex; i++) {
				SlashData slash = slashes.get(i);
				slashArray[i * 4] = slash.x;
				slashArray[i * 4 + 1] = slash.y;
				slashArray[i * 4 + 2] = Mathf.mod(Mathf.halfPi - slash.angle, Mathf.PI2);
				slashArray[i * 4 + 3] = slash.offset;
			}
			slashShader.slashes = slashArray;
			buffer.blit(slashShader);

			if (renderer.bloom != null) {
				renderer.bloom.capture();
				drawSlashes();
				renderer.bloom.render();
			} else {
				drawSlashes();
			}

			slashIndex = 0;
		});
	}

	private static void drawSlashes() {
		for (int i = 0; i < slashIndex; i++) {
			SlashData slash = slashes.items[i];
			float ang = slash.angle * Mathf.radDeg;
			Tmp.v1.trns(ang, slash.length);
			Tmp.v2.trns(ang + 90f, slash.width);

			Draw.color(slash.color);
			Fill.quad(
					slash.x + Tmp.v1.x, slash.y + Tmp.v1.y,
					slash.x + Tmp.v2.x, slash.y + Tmp.v2.y,
					slash.x - Tmp.v1.x, slash.y - Tmp.v1.y,
					slash.x - Tmp.v2.x, slash.y - Tmp.v2.y
			);
		}
	}

	static class SlashShader extends Shader {
		public float[] slashes;

		SlashShader() {
			super(HShaders.shadersDir.child("general-highp.vert"), HShaders.shadersDir.child("slash.frag"));
		}

		@Override
		public void apply() {
			setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
			setUniformf("u_resolution", Core.camera.width, Core.camera.height);

			setUniformi("u_slashescount", slashes.length / 4);
			setUniform4fv("u_slashes", slashes, 0, slashes.length);
		}
	}

	static class SlashData {
		public float x, y, angle, offset;
		public float length, width, color;

		public void set(float x, float y, float angle, float offset, float length, float width, float color) {
			this.x = x;
			this.y = y;
			this.angle = angle;
			this.offset = offset;
			this.length = length;
			this.width = width;
			this.color = color;
		}
	}
}
