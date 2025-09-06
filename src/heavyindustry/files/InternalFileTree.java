package heavyindustry.files;

import arc.files.Fi;
import arc.files.ZipFi;
import arc.util.OS;

/**
 * Use for jar internal navigation.
 *
 * @since 1.0.6
 */
public class InternalFileTree {
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
		Fi out = root;
		for (String s : name.split("/")) {
			if (!s.isEmpty())
				out = out.child(s);
		}
		return out;
	}

	public Fi children(String... name) {
		Fi out = root;
		for (String s : name) {
			if (!s.isEmpty())
				out = out.child(s);
		}
		return out;
	}
}
