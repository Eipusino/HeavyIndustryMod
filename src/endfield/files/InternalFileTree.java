package endfield.files;

import arc.assets.loaders.FileHandleResolver;
import arc.files.Fi;
import arc.files.ZipFi;
import arc.util.OS;

/**
 * Use for jar internal navigation.
 *
 * @since 1.0.6
 */
public class InternalFileTree implements FileHandleResolver {
	public final Class<?> anchorClass;

	public final ZipFi root;
	public final Fi file;

	/** @param owner navigation anchor */
	public InternalFileTree(Class<?> owner) {
		anchorClass = owner;

		String classPath = anchorClass.getResource("").getFile().replaceAll("%20", " ");
		classPath = classPath.substring(classPath.indexOf(":") + 2);
		String jarPath = (OS.isLinux ? "/" : "") + classPath.substring(0, classPath.indexOf("!"));

		file = new Fi(jarPath);
		root = new ZipFi(file);
	}

	public Fi child(String name) {
		return root.child(name);
	}

	public Fi children(String... splitName) {
		Fi out = root;
		for (String s : splitName) {
			if (!s.isEmpty())
				out = out.child(s);
		}
		return out;
	}

	@Override
	public Fi resolve(String fileName) {
		return children(fileName.split("/"));
	}

	@Override
	public String toString() {
		return anchorClass + ": " + file;
	}
}
