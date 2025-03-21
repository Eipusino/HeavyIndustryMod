package heavyindustry.files;

import arc.files.Fi;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;
import arc.graphics.g2d.TextureRegion;
import arc.struct.ObjectSet;
import arc.util.Log;
import heavyindustry.HVars;

import java.util.HashMap;

public final class FileTree {
	private static final HashMap<String, Fi> files = new HashMap<>();
	private static final HashMap<String, Fi> directory = new HashMap<>();
	private static final ObjectSet<String> filter = ObjectSet.with("heavyindustry", "kotlin", "org", "META-INF");

	static {
		for (Fi it : HVars.internalTree.root.list()) {
			if (!filter.contains(it.name())) {
				loop(it);
			}
		}
	}

	/** Don't let anyone instantiate this class. */
	private FileTree() {}

	private static void loop(Fi fi) {
		if (fi.isDirectory()) {
			directory.put(fi.name(), fi);

			for (Fi it : fi.list()) {
				loop(it);
			}
		} else {
			if (files.containsKey(fi.name())) {
				Log.warn("included files:" + files.get(fi.name() + ", not included files:" + fi.path()));
			} else {
				files.put(fi.name(), fi);
			}
		}
	}

	public static Fi get(String name) {
		return files.get(name);
	}

	public static Fi dir(String name) {
		return directory.get(name);
	}

	/**
	 * Retrieve Region directly from the path within the module
	 *
	 * @param name The name of the texture, without the suffix {@code .png}
	 */
	public static AtlasRegion region(String name) {
		return new AtlasRegion(new TextureRegion(texture(name)));
	}

	public static Texture texture(String name) {
		Fi fi = get((name == null ? "error" : name) + ".png");
		return new Texture(fi == null ? HVars.internalTree.child("sprites-override/effects/error.png") : fi);
	}
}
