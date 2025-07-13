package heavyindustry.graphics.gl;

import arc.Core;
import arc.files.Fi;
import arc.graphics.gl.Shader;

/**
 * Shader that works only on and expects GL 3.0-compatible shaders instead of GL 2.0.
 * @since 1.0.6
 */
public class Gl30Shader extends Shader {
	public Gl30Shader(Fi vertexShader, Fi fragmentShader) {
		super(vertexShader, fragmentShader);
	}

	public Gl30Shader(String vertexShader, String fragmentShader) {
		super(vertexShader, fragmentShader);
	}

	@Override
	protected String preprocess(String source, boolean fragment) {
		if (source.contains("#ifdef GL_ES")) {
			throw new IllegalArgumentException("Shader contains GL_ES specific code; this should be handled by the preprocessor. Code: \n```\n" + source + "\n```");
		}

		if (source.contains("#version")) {
			throw new IllegalArgumentException("Shader contains explicit version requirement; this should be handled by the preprocessor. Code: \n```\n" + source + "\n```");
		}

		if (fragment) {
			source =
					"#ifdef GL_ES\n" +
					"precision " + (source.contains("#define HIGHP") && !source.contains("//#define HIGHP") ? "highp" : "mediump") + " float;\n" +
					"precision mediump int;\n" +
					"#else\n" +
					"#define lowp   \n" +
					"#define mediump   \n" +
					"#define highp   \n" +
					"#endif\n" + source;
		} else {
			source =
					"#ifndef GL_ES\n" +
					"#define lowp   \n" +
					"#define mediump   \n" +
					"#define highp   \n" +
					"#endif\n" + source;
		}

		return
				"#version " + (Core.app.isDesktop() ? (Core.graphics.getGLVersion().atLeast(3, 2) ? "150" : "130") : "300 es") +
				"\n" + source;
	}
}
