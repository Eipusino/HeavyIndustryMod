package heavyindustry.files;

import arc.files.Fi;
import arc.files.ZipFi;
import arc.util.OS;
import heavyindustry.util.ObjectUtils;

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

		String classPath = ObjectUtils.requireNonNull(anchorClass.getResource(""), "Unable to retrieve class resource.").getFile().replaceAll("%20", " ");
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

	@Override
	public String toString() {
		return anchorClass + ": " + file.toString();
	}
}
