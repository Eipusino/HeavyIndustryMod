package heavyindustry.graphics.g3d.model.obj.mtl;

import arc.files.Fi;
import arc.struct.ObjectMap;

/**
 * Material data container.
 **/
public class MTL {
	public final Fi file;
	public final String name;

	public final ObjectMap<String, String> data = new ObjectMap<>();

	public MTL(Fi file, String name) {
		this.file = file;
		this.name = name;
	}

	public String get(String key) {
		return data.get(key);
	}

	public void set(String key, String value) {
		data.put(key, value);
	}
}
