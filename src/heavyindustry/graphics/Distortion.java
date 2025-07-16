package heavyindustry.graphics;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.util.Disposable;
import heavyindustry.graphics.gl.Gl30Shader;

import static heavyindustry.graphics.Drawn.c1;
import static heavyindustry.graphics.Drawn.v1;
import static heavyindustry.graphics.Drawn.v2;
import static heavyindustry.graphics.Drawn.v3;
import static heavyindustry.graphics.Drawn.v4;
import static heavyindustry.graphics.Drawn.v5;
import static heavyindustry.graphics.Drawn.v6;

public class Distortion implements Disposable {
	static final FrameBuffer tmpBuffer = new FrameBuffer();

	Shader distortion;
	FrameBuffer buffer;
	boolean capturing, disposed;

	public Distortion() {
		distortion = new Gl30Shader(HShaders.msv("distortion"), HShaders.msf("distortion"));

		buffer = new FrameBuffer();

		init();
	}

	public static void drawVoidDistortion(float x, float y, float radius, float len) {
		drawVoidDistortion(x, y, radius, len, true, Lines.circleVertices(radius));
	}

	public static void drawVoidDistortion(float x, float y, float radius, float len, boolean inside) {
		drawVoidDistortion(x, y, radius, len, inside, Lines.circleVertices(radius));
	}

	public static void drawVoidDistortion(float x, float y, float radius, float len, boolean inside, int sides) {
		v1.set(radius, 0);
		v2.set(radius, 0);
		v3.set(radius + len, 0);
		v4.set(radius + len, 0);
		v5.set(inside ? -1 : 1, 0);
		v6.set(inside ? -1 : 1, 0);

		float step = 360f / sides;
		for (int i = 0; i < sides; i++) {
			v1.setAngle(step * i);
			v2.setAngle(step * (i + 1));
			v3.setAngle(step * i);
			v4.setAngle(step * (i + 1));
			v5.setAngle(step * i);
			v6.setAngle(step * (i + 1));

			float cf1 = c1.set((v5.x + 1) / 2, (v5.y + 1) / 2, inside ? 1 : 0, inside ? 1 : 0).toFloatBits();
			float cf2 = c1.set((v6.x + 1) / 2, (v6.y + 1) / 2, inside ? 1 : 0, inside ? 1 : 0).toFloatBits();
			float cf3 = c1.set((v5.x + 1) / 2, (v5.y + 1) / 2, inside ? 0 : 1, inside ? 0 : 1).toFloatBits();
			float cf4 = c1.set((v6.x + 1) / 2, (v6.y + 1) / 2, inside ? 0 : 1, inside ? 0 : 1).toFloatBits();

			Fill.quad(x + v1.x, y + v1.y, cf1, x + v2.x, y + v2.y, cf2, x + v4.x, y + v4.y, cf4, x + v3.x, y + v3.y, cf3);
		}
	}

	public void init() {
		distortion.bind();
		distortion.setUniformi("u_texture0", 0);
		distortion.setUniformi("u_texture1", 1);
		distortion.setUniformf("strength", -64);

		resize();
	}

	public void resize() {
		buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
	}

	public void capture() {
		if (capturing) return;

		capturing = true;
		buffer.begin(Color.clear);
	}

	public void render() {
		capturing = false;

		buffer.end();

		tmpBuffer.resize(buffer.getWidth(), buffer.getHeight());
		ScreenSampler.getToBuffer(tmpBuffer, false);
		tmpBuffer.getTexture().bind(1);
		distortion.bind();
		distortion.setUniformf("width", buffer.getWidth());
		distortion.setUniformf("height", buffer.getHeight());
		buffer.blit(distortion);
	}

	public void setStrength(float strength) {
		distortion.bind();
		distortion.setUniformf("strength", strength);
	}

	@Override
	public void dispose() {
		buffer.dispose();
		distortion.dispose();
		disposed = true;
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}
}