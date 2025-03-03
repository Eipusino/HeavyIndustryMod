package heavyindustry.graphics.g3d.render;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.g2d.Draw;
import arc.graphics.g3d.Camera3D;
import arc.graphics.g3d.PlaneBatch3D;
import arc.graphics.g3d.VertexBatch3D;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.geom.Mat3D;
import arc.struct.Seq;
import heavyindustry.graphics.g3d.model.Model;

/** Example 3D renderer. */
public class GenericRenderer3D implements Renderer3D {
	public final Camera3D cam = new Camera3D();
	public final VertexBatch3D batch = new VertexBatch3D(20000, false, true, 0);

	public final PlaneBatch3D projector = new PlaneBatch3D();

	public final FrameBuffer buffer = new FrameBuffer(2, 2, true);
	public Shader bufferShader;

	/** Models list. */
	public Seq<Model> models = new Seq<>();

	public GenericRenderer3D() {

	}

	/** @return shader for frame buffer drawing. */
	public static Shader createShader() {
		return new Shader("""
				attribute vec4 a_position;
				attribute vec2 a_texCoord0;
				
				varying vec2 v_texCoords;
				
				void main() {
					v_texCoords = a_texCoord0;
					gl_Position = a_position;
				}
				""", """
				uniform sampler2D u_texture;
				
				varying vec2 v_texCoords;
				
				void main() {
					gl_FragColor = texture2D(u_texture, v_texCoords);
				}
				""");
	}

	@Override
	public Mat3D getProjMat() {
		return cam.combined;
	}

	@Override
	public void init() {
		cam.near = 0.1f;
		cam.far = 10000f;
		cam.fov = 100f;

		projector.setScaling(1 / 10000f);

		bufferShader = createShader();
	}

	@Override
	public boolean shouldRender() {
		return true;
	}

	@Override
	public void render() {
		cam.update();

		projector.proj(cam.combined);
		batch.proj(cam.combined);

		buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());

		Gl.clear(Gl.depthBufferBit);
		Gl.enable(Gl.depthTest);
		Gl.depthMask(true);
		Gl.enable(Gl.cullFace);
		Gl.cullFace(Gl.back);

		buffer.begin(Color.clear);

		models.each(model -> {
			model.render(this);
		});

		batch.flush(Gl.triangles);

		buffer.end();

		Gl.disable(Gl.cullFace);
		Gl.disable(Gl.depthTest);
		Gl.depthMask(false);

		cam.update();

		Draw.blit(buffer, bufferShader);
	}
}
