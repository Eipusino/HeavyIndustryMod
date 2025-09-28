package heavyindustry.graphics.g2d;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Texture.TextureFilter;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.util.Time;
import heavyindustry.graphics.HShaders;

public class RangeExtractor {
	public static RangeExtractor rangeExtractor, lowRangeExtractor;

	public FrameBuffer buffer;
	public boolean low;
	public float stroke = 2f;
	public float alpha = 0.075f;
	public Color boundColor = new Color(1f, 1f, 1f, 1f);
	public Shader extractShader;

	private boolean capturing = false, loaded = false;

	public RangeExtractor(boolean isLow) {
		low = isLow;

		buffer = new FrameBuffer();
		buffer.getTexture().setFilter(TextureFilter.nearest);
	}

	public static void load() {
		rangeExtractor = new RangeExtractor(false);
		lowRangeExtractor = new RangeExtractor(true);
	}

	public void setupShader() {
		if (loaded) return;

		loaded = true;

		extractShader = new Shader(HShaders.msv("general-highp"), HShaders.msf(low ? "range-low" : "range"));

		if (low) buffer.resize(Core.graphics.getWidth() / 2, Core.graphics.getHeight() / 2);
		else buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
	}

	public void capture() {
		if (capturing) return;

		if (low) buffer.resize(Core.graphics.getWidth() / 2, Core.graphics.getHeight() / 2);
		else buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
		buffer.begin(Color.clear);

		capturing = true;
	}

	public void render() {
		if (!capturing) return;

		capturing = false;

		buffer.end();
		extractShader.bind();
		extractShader.apply();
		extractShader.setUniformf("u_time", Time.time);
		extractShader.setUniformf("u_resolution", Core.camera.width, Core.camera.height);
		extractShader.setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
		extractShader.setUniformf("u_stroke", stroke);
		extractShader.setUniformf("u_alpha", alpha);
		extractShader.setUniformf("u_color", boundColor);
		extractShader.setUniformi("u_texture", 0);
		buffer.blit(extractShader);
	}
}
