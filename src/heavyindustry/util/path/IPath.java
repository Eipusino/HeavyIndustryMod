package heavyindustry.util.path;

/**
 * The basic interface of the path storage type should be assigned corresponding functions to the class, and the iterable interface has been extended.
 * The path should traverse all vertices of the path in order from the starting point to the ending point.
 * <p>Usually, you can use the {@link java.util.LinkedList} implemented based on {@link GenericPath} to provide a universal path storage.
 *
 * @see GenericPath
 * @since 1.0.5
 */
public interface IPath<V> extends Iterable<V> {
	/**
	 * Insert a vertex from the starting point of the path and use it as the starting point.
	 *
	 * @param next Added vertices
	 */
	void addFirst(V next);

	/**
	 * Insert a vertex from the endpoint of the path and use it as the starting point.
	 *
	 * @param next Added vertices
	 */
	void addLast(V next);

	/**
	 * Obtain the starting point of the path, which should correctly return the node at the top of the iteration order.
	 *
	 * @return Vertex located at the starting point position
	 */
	V origin();

	/**
	 * Obtain the endpoint of the path, which should correctly return the node at the end of the iteration sequence.
	 *
	 * @return Node located at the end position
	 */
	V destination();
}
