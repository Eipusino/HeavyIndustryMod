package heavyindustry.graphics.g3d.model;

import arc.graphics.gl.Shader;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.util.Disposable;
import heavyindustry.graphics.g3d.render.Renderer3D;

/**
 * Interface for models.
 **/
public interface Model extends Disposable {
	/**
	 * Render model.
	 *
	 * @param renderer renderer that's renders this model
	 **/
	void render(Renderer3D renderer);

	/** Clone this models. */
	Model cloneModel();


	Shader getShader();

	void setShader(Shader shader);

	Mat3D getTransformation();

	void setTransformation(Mat3D transformation);

	Vec3 getTranslation();

	void setTranslation(Vec3 translation);
}
