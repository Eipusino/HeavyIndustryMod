package heavyindustry.graphics.g3d.model.obj;

import arc.graphics.Gl;
import arc.graphics.Mesh;
import arc.graphics.Texture;
import arc.graphics.VertexAttribute;
import arc.graphics.gl.Shader;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import heavyindustry.graphics.g3d.model.Model;
import heavyindustry.graphics.g3d.model.obj.mtl.MTL;
import heavyindustry.graphics.g3d.model.obj.obj.OBJ;
import heavyindustry.graphics.g3d.render.Renderer3D;

public class OBJModel implements Model {
	private final OBJ obj;
	private final MTL mtl;
	public Texture texture;
	public Mesh mesh;
	public Shader shader;
	/** Transformation matrix. */
	public Mat3D transformation = new Mat3D();
	/** Translation vector. */
	public Vec3 translation = new Vec3();

	public OBJModel(OBJ obj, MTL mtl, Texture texture, Shader shader) {
		this.mtl = mtl;
		this.obj = obj;
		this.texture = texture;
		this.shader = shader;
		rebuildMesh();
	}

	@Override
	public void render(Renderer3D renderer) {
		texture.bind();
		shader.bind();

		shader.apply();
		shader.setUniformMatrix4("u_proj", renderer.getProjMat().val);
		shader.setUniformMatrix4("u_transf", transformation.val);
		shader.setUniformf("u_transl", translation);

		mesh.render(shader, Gl.triangles);
	}

	public void rebuildMesh() {
		disposeMesh();
		mesh = new Mesh(true, obj.vertices.length, 0,
				VertexAttribute.position3, VertexAttribute.texCoords, VertexAttribute.normal);

		mesh.getVerticesBuffer().limit(obj.vertices.length);
		mesh.getVerticesBuffer().put(obj.vertices, 0, obj.vertices.length);
	}

	public void disposeMesh() {
		if (mesh != null)
			mesh.dispose();
	}

	public void disposeShader() {
		if (shader != null)
			shader.dispose();
	}

	public void disposeTexture() {
		if (texture != null)
			texture.dispose();
	}

	@Override
	public void dispose() {
		disposeTexture();
		disposeShader();
		disposeMesh();
	}

	// TODO maybe... use reflection?
	@Override
	public OBJModel cloneModel() {
		return new OBJModel(obj, mtl, texture, shader);
	}

	@Override
	public Shader getShader() {
		return shader;
	}

	@Override
	public void setShader(Shader shader) {
		this.shader = shader;
	}

	@Override
	public Mat3D getTransformation() {
		return transformation;
	}

	@Override
	public void setTransformation(Mat3D transformation) {
		this.transformation = transformation;
	}

	@Override
	public Vec3 getTranslation() {
		return translation;
	}

	@Override
	public void setTranslation(Vec3 translation) {
		this.translation = translation;
	}
}
