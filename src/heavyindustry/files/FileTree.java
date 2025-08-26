package heavyindustry.files;

import arc.files.Fi;

/** File tree class. **/
public class FileTree {
	public final Fi root;

	/** @param fi file tree root **/
	public FileTree(Fi fi) {
		root = fi;
	}

	/**
	 * @param childPath file path
	 * @return file
	 **/
	public Fi child(String childPath) {
		Fi out = root;
		for (String s : childPath.split("/")) {
			out = out.child(s);
		}
		return out;
	}
}
