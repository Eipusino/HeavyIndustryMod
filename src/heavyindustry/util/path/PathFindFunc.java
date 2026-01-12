package heavyindustry.util.path;

/**
 * Saved the callback function types used during path search, which are used to construct transitive
 * functions for lambda.
 */
public final class PathFindFunc {
	private PathFindFunc() {}

	/**
	 * Path receiver function, used to pass the endpoint and path object to the function for callback when
	 * searching for a path.
	 */
	@FunctionalInterface
	public interface PathAcceptor<V> {
		/**
		 * The callback function entry receives a vertex and a path object, representing the endpoint and
		 * path information of the path, respectively.
		 */
		void accept(V destination, IPath<V> path);
	}

	/**
	 * Vertex receiver function, used to pass vertex objects to the function for callback when searching for
	 * paths.
	 */
	@FunctionalInterface
	public interface VerticesAcceptor<V> {
		/** The callback function entry can receive a vertex object. */
		void accept(V vert);
	}
}
