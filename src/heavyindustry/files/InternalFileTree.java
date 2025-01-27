package heavyindustry.files;

import arc.files.*;
import arc.util.*;

/**
 * Use for jar internal navigation.
 * <p>Replace {@code Vars.mods.getMod(mod).root} with a more secure method of obtaining the jar root directory based on the class path as internal navigation for the jar.
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

		//noinspection DataFlowIssue
		String classPath = owner.getResource("").getFile().replaceAll("%20", " ");
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
}
