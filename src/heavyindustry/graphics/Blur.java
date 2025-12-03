package heavyindustry.graphics;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.util.Disposable;

public class Blur implements Disposable {
	public static final float[] default1 = {
			0.0086973240159f, 0.0359949776755f, 0.1093610049784f,
			0.2129658870149f, 0.2659615230194f, 0.2129658870149f,
			0.1093610049784f, 0.0359949776755f, 0.0086973240159f,
	};
	public static final float[] default2 = {
			0.0444086447005f, 0.0779944219933f, 0.1159966211046f,
			0.1673080561213f, 0.1885769121606f, 0.1673080561213f,
			0.1159966211046f, 0.0779944219933f, 0.0444086447005f,
	};
	public static final float[] default3 = {
			0.0045418484119f, 0.0539998665132f, 0.2419867245191f,
			0.3989431211116f,
			0.2419867245191f, 0.0539998665132f, 0.0045418484119f,
	};
	public static final float[] default4 = {
			0.0245418484119f, 0.0639998665132f, 0.2519867245191f,
			0.3189431211116f,
			0.2519867245191f, 0.0639998665132f, 0.0245418484119f,
	};
	public static final float[] default5 = {
			0.019615710072f, 0.2054255182127f,
			0.5599175434306f,
			0.2054255182127f, 0.019615710072f,
	};
	public static final float[] default6 = {
			0.0702702703f, 0.3162162162f,
			0.2270270270f,
			0.3162162162f, 0.0702702703f,
	};
	public static final float[] default7 = {
			0.2079819330264f,
			0.6840361339472f,
			0.2079819330264f,
	};
	public static final float[] default8 = {
			0.2561736558128f,
			0.4876526883744f,
			0.2561736558128f,
	};

	public static final String vertTemplate =
			"""
			attribute vec4 a_position;
			attribute vec2 a_texCoord0;
			
			uniform vec2 dir;
			uniform vec2 size;
			
			varying vec2 v_texCoords;
			
			%varying%
			
			void main() {
				vec2 len = dir / size;
			
				v_texCoords = a_texCoord0;
				%assignVar%
				gl_Position = a_position;
			}
			""";

	public static final String fragmentTemplate =
			"""
			uniform lowp sampler2D u_texture0;
			uniform lowp sampler2D u_texture1;
			
			uniform lowp float def_alpha;
			
			varying vec2 v_texCoords;
			
			%varying%
			
			void main() {
				vec4 blur = texture2D(u_texture0, v_texCoords);
				vec3 color = texture2D(u_texture1, v_texCoords).rgb;
			
				if (blur.a > 0.0) {
					vec3 blurColor =
						%convolution%
			
					gl_FragColor.rgb = mix(color, blurColor, blur.a);
					gl_FragColor.a = 1.0;
				} else {
					gl_FragColor.rgb = color;
					gl_FragColor.a = def_alpha;
				}
			}
			""";

	protected Shader blur;
	protected FrameBuffer buffer, pingpong;

	protected boolean capturing, disposed;

	public int blurScl = 4;
	public float blurSpace = 2.16f;

	public Blur() {
		this(default6);
	}

	public Blur(float... convolutions) {
		blur = genShader(convolutions);

		buffer = new FrameBuffer();
		pingpong = new FrameBuffer();

		blur.bind();
		blur.setUniformi("u_texture0", 0);
		blur.setUniformi("u_texture1", 1);
	}

	protected Shader genShader(float... convolutions) {
		if (convolutions.length % 2 != 1)
			throw new IllegalArgumentException("convolution numbers length must be odd number!");

		int convLen = convolutions.length;

		StringBuilder varyings = new StringBuilder();
		StringBuilder assignVar = new StringBuilder();
		StringBuilder convolution = new StringBuilder();

		int c = 0;
		int half = convLen / 2;
		for (float v : convolutions) {
			varyings.append("varying vec2 v_texCoords")
					.append(c)
					.append(";")
					.append(System.lineSeparator());

			assignVar.append("v_texCoords")
					.append(c)
					.append(" = ")
					.append("a_texCoord0");
			if (c - half != 0) {
				assignVar.append(c - half > 0 ? " + " : " - ")
						.append(Math.abs((float) c - half))
						.append(" * len");
			}
			assignVar.append(";")
					.append(System.lineSeparator()).append("  ");

			if (c > 0) convolution.append("        + ");
			convolution.append(v)
					.append(" * texture2D(u_texture1, v_texCoords")
					.append(c)
					.append(")")
					.append(".rgb")
					.append(System.lineSeparator());

			c++;
		}
		convolution.append(";");

		String vertexShader = vertTemplate.replace("%varying%", varyings.toString()).replace("%assignVar%", assignVar);
		String fragmentShader = fragmentTemplate.replace("%varying%", varyings.toString()).replace("%convolution%", convolution);

		return new Shader(vertexShader, fragmentShader);
	}

	public void resize(int width, int height) {
		width /= blurScl;
		height /= blurScl;

		buffer.resize(width, height);
		pingpong.resize(width, height);

		blur.bind();
		blur.setUniformf("size", width, height);
	}

	public void capture() {
		if (!capturing) {
			buffer.begin(Color.clear);

			capturing = true;
		}
	}

	public void render() {
		if (!capturing) return;
		capturing = false;
		buffer.end();

		Gl.disable(Gl.blend);
		Gl.disable(Gl.depthTest);
		Gl.depthMask(false);

		pingpong.begin();
		blur.bind();
		blur.setUniformf("dir", blurSpace, 0f);
		blur.setUniformi("def_alpha", 1);
		buffer.getTexture().bind(0);
		ScreenSampler.blit(blur, 1);
		pingpong.end();

		blur.bind();
		blur.setUniformf("dir", 0f, blurSpace);
		blur.setUniformf("def_alpha", 0);
		pingpong.getTexture().bind(1);

		Gl.enable(Gl.blend);
		Gl.blendFunc(Gl.srcAlpha, Gl.oneMinusSrcAlpha);
		buffer.blit(blur);
	}

	public void directDraw(Runnable draw) {
		resize(Core.graphics.getWidth(), Core.graphics.getHeight());
		capture();
		draw.run();
		render();
	}

	@Override
	public void dispose() {
		buffer.dispose();
		pingpong.dispose();
		blur.dispose();
		disposed = true;
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}
}