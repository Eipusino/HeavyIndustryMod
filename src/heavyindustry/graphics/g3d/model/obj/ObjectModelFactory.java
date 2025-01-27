package heavyindustry.graphics.g3d.model.obj;

import arc.files.*;
import arc.graphics.*;
import arc.graphics.gl.*;
import arc.struct.*;
import arc.util.*;
import heavyindustry.graphics.g3d.model.obj.mtl.*;
import heavyindustry.graphics.g3d.model.obj.obj.*;

import java.util.*;

/** Use it for models parsing. */
public class ObjectModelFactory {
	private static final MaterialCache loadedMTLs = new MaterialCache();

	/**
	 * Loads models from given object file.
	 *
	 * @param objFile .obj file
	 * @param shader  model's shaders
	 */
	public static Seq<OBJModel> create(Fi objFile, Shader shader) {
		Seq<OBJModel> out = new Seq<>();
		Seq<OBJ> objs = OBJParser.parse(objFile);
		for (int i = 0; i < objs.size; i++) {
			OBJ obj = objs.get(i);
			MTL found = loadedMTLs.get(obj.mtlFile, obj.mtlName);
			if (found == null) {
				Seq<MTL> mtlz = MTLParser.parse(obj.mtlFile);
				loadedMTLs.register(obj.mtlFile, mtlz);
				found = Objects.requireNonNull(loadedMTLs.get(obj.mtlFile, obj.mtlName), "null material");
			}
			Texture objMtlTexture = new Texture(found.file.parent().child(found.get("map_Kd")));
			out.add(new OBJModel(obj, found, objMtlTexture, shader));
		}

		return out;
	}

	private static class MaterialCache {
		private static final ObjectMap<String, ObjectMap<String, MTL>> map = new ObjectMap<>();

		private static String getKey(MTL it) {
			return it.name;
		}

		@Nullable
		public MTL get(Fi file, String name) {
			ObjectMap<String, MTL> entries = map.get(file.absolutePath());
			if (entries == null) return null;
			return entries.get(name);
		}

		public void register(Fi file, Seq<MTL> materials) {
			map.put(file.absolutePath(), materials.asMap(MaterialCache::getKey));
		}
	}
}
