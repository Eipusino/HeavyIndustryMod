package heavyindustry.files;

import arc.files.Fi;

public final class HFiles {
	private HFiles() {}

	/**
	 * Move the specified named subdirectories and their contents to the new subdirectories.
	 * <p>If the new subdirectories do not exist, they will be created.
	 *
	 * @param parent   Original parent directory object
	 * @param newName  Name of the new subdirectories
	 * @param oldNames Names of one or more old subdirectories to be moved
	 * @return Return a new subdirectories object, regardless of whether the move operation is successful
	 *         or not
	 */
	public static Fi child(Fi parent, String newName, String... oldNames) {
		// Create or retrieve new subdirectories
		Fi child = parent.child(newName);

		// Traverse the array of old subdirectories' names
		for (String oldName : oldNames) {
			// Retrieve old subdirectories objects
			Fi old = parent.child(oldName);

			// If the old subdirectories exist and are directories, move them to the new subdirectories
			if (old.exists() && old.isDirectory()) old.moveTo(child);
		}

		// Return new subdirectories object
		return child;
	}

	/** @return {@code true} if the file be deleted successfully */
	public static boolean delete(Fi fi) {
		return fi.exists() && (fi.isDirectory() ? fi.deleteDirectory() : fi.delete());
	}
}
