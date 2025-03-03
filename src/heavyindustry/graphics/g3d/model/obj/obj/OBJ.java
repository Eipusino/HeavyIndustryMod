package heavyindustry.graphics.g3d.model.obj.obj;

import arc.files.Fi;

/**
 * Object data container.
 **/
public class OBJ {
	public final Fi file;

	public Fi mtlFile;
	public String mtlName;

	public float[] vertices;

	public String name;

	public OBJ(Fi file) {
		this.file = file;
	}
}
