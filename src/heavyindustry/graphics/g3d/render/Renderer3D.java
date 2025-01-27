package heavyindustry.graphics.g3d.render;

import arc.math.geom.*;

/** Interface for 3D renderers. */
public interface Renderer3D {
	/**
	 * Get camera projection matrix.
	 *
	 * @return camera projection
	 */
	Mat3D getProjMat();

	/** Initialize renderer. */
	void init();

	// TODO remove this?
	/** Should renderer render. */
	boolean shouldRender();

	/** Render function. */
	void render();
}
