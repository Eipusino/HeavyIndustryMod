package heavyindustry.util.path;

/**
 * The preliminary implementation of pathfinding based on breadth first search requires the provision of necessary container entry points to implement this interface.
 * <p>This search<strong> has no weight value</strong>, and the generated path should be the equal shortest path. Generally, the path found in a graph without weights must be the optimal solution or one of the optimal solutions.
 *
 * @since 1.0.5
 */
public interface BFSPathFinder<V> extends PathFinder<V> {
	/** Reset search state or (and) temporary cache, including resetting already traversed vertices and edges. */
	void reset();

	/**
	 * The method of associating a vertex with a new backtracking pointer that stores parameter information should perform the following actions:
	 * <p>1.If this vertex is not associated with a backtracking pointer, create a new backtracking pointer instance and set the vertex and pointer's previous destination for this pointer using the passed parameters.
	 * <p>2.If the vertex is already associated with a backtracking pointer, then do nothing.
	 * <p>3.If vertex association is performed in this operation, it should return true; otherwise, it should return false.
	 *
	 * @param vert	 Vertex for traversal check
	 * @param previous The previous backtracking pointer of this pointer can be empty
	 * @return If the vertex has not been associated with a pointer, return true; otherwise, return false
	 */
	boolean relateToPointer(V vert, PathPointer<V> previous);

	/**
	 * Check if the current incoming node is an excluded node, and if so, skip that node
	 * <p>The implementation of this method requires rewriting and returning based on the actual situation. By default, vertices are never excluded.
	 *
	 * @param vert The vertex currently being checked
	 * @return Should this node be excluded
	 */
	default boolean exclude(V vert) {
		return false;
	}

	/**
	 * Retrieve the backtracking pointer associated with the vertex. If the vertex has not been associated yet, null should be returned.
	 *
	 * @param vert Get the vertex of the pointer
	 * @return A pointer associated with a vertex, null if not associated
	 */
	PathPointer<V> getPointer(V vert);

	/**
	 * Read the next vertex from the search queue and pop it out of the queue. If the queue is empty, return null.
	 * <p>This method, when used with {@link #queueAdd(Object)}, should satisfy the stack implementation, <strong>and vertices that are added later should be retrieved first</strong>.
	 *
	 * @return A vertex at the top of the stack, if there are no vertices, returns null
	 */
	V queueNext();

	/**
	 * Add a vertex to the search queue.
	 * <p>This method, when used with {@link #queueNext()}, should meet the stack implementation, <strong>and vertices that are added later should be retrieved first</strong>.
	 *
	 * @param next Add the next vertex to the queue
	 */
	void queueAdd(V next);

	/**
	 * Create a path object that should be able to return a blank path during implementation.
	 * You may also need to implement the {@link Pathc} interface and return its instance, or use the default implementation {@link GenericPath} directly.
	 *
	 * @return An empty path
	 * @see Pathc
	 * @see GenericPath
	 */
	Pathc<V> createPath();

	/**
	 * A standard BFS pathfinding implementation typically finds the shortest or one of the shortest paths in an unweighted graph.
	 *
	 * @see #findPath(Object, PathFindFunc.PathAcceptor)
	 */
	@Override
	default void findPath(V origin, PathFindFunc.PathAcceptor<V> pathConsumer) {
		reset();
		queueAdd(origin);
		relateToPointer(origin, null);

		V next;
		while ((next = queueNext()) != null) {
			PathPointer<V> pointer = getPointer(next);
			for (V vert : getLinkVertices(next)) {
				if (!exclude(vert) && relateToPointer(vert, pointer)) {
					queueAdd(vert);
				}
			}

			if (isDestination(origin, next)) {
				PathPointer<V> tracePointer = pointer;
				Pathc<V> path = createPath();
				path.addFirst(pointer.self);

				while ((tracePointer = tracePointer.previous) != null) {
					path.addFirst(tracePointer.self);
				}

				pathConsumer.accept(next, path);
			}
		}
	}

	/**
	 * Implementation of graph traversal based on BFS, with traversal order spreading outward from the given starting point until every vertex is traversed.
	 *
	 * @see PathFinder#eachVertices(Object, PathFindFunc.VerticesAcceptor)
	 */
	@Override
	default void eachVertices(V origin, PathFindFunc.VerticesAcceptor<V> vertConsumer) {
		reset();
		queueAdd(origin);
		relateToPointer(origin, null);

		V v;
		while ((v = queueNext()) != null) {
			for (V vert : getLinkVertices(v)) {
				if (!exclude(vert) && relateToPointer(vert, null)) {
					queueAdd(vert);
				}
			}
			vertConsumer.accept(v);
		}
	}

	/**
	 * Path backtracking pointers, each pointer storing information about a vertex and its previous pointer, iteratively backtracking in a linked list like form.
	 * If the previous pointer does not exist, it indicates that the starting point has been reached.
	 */
	class PathPointer<W> {
		public PathPointer<W> previous;

		public W self;

		/** Construct a pointer object that carries given parameter information. */
		public PathPointer(W self) {
			this.self = self;
		}

		/** Construct a pointer object that carries given parameter information. */
		public PathPointer(W self, PathPointer<W> previous) {
			this.previous = previous;
			this.self = self;
		}
	}
}
