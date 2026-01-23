package endfield.util.path;

import endfield.util.path.PathFindFunc.PathAcceptor;
import endfield.util.path.PathFindFunc.VerticesAcceptor;

/**
 * The base class, subclass, or interface of the basic path searcher requires the use of one or several algorithms to implement the path search method.
 * In this model, the vertices and the vertices they point to are represented as a directed graph.
 *
 * @since 1.0.5
 */
public interface PathFinder<V> {
	/**
	 * Retrieve the iterable object of other vertices linked to a given vertex, which should correctly provide the other vertices linked to that vertex.
	 * <p>The element iterated from the object returned by this method is the child node connected to the point, for example:
	 * <pre>{@code
	 * ┌───┐   ┌───┐   ┌───┐
	 * │ A ├───► B ├───► E │
	 * └┬─┬┘   └───┘   └───┘
	 *  │ │
	 *  │ └────▲───┐   ┌───┐
	 *  │      │ D ├───► F │
	 * ┌▼──┐   └─┬─┘   └───┘
	 * │ C │     │
	 * └───┘     │     ┌───┐
	 *           └─────► G │
	 *                 └───┘
	 * }</pre>
	 * The method for node A should be iterable for nodes B, C, and D. Similarly, B can iterate for E, and D can iterate for F and G.
	 * <p><strong>Note:</strong>This method represents a unidirectional connection with child nodes. If two nodes are connected bidirectionally, they need to be able to iterate over each other.
	 *
	 * @param curr The node currently obtaining child nodes
	 * @return All child nodes connected to the current node
	 */
	Iterable<V> getLinkVertices(V curr);

	/**
	 * Determine whether the target vertex is an endpoint of the given starting point, which determines the search results of the network.
	 *
	 * @param origin Starting point of search
	 * @param vert   To determine whether it is a vertex of a destination
	 * @return If vert is an endpoint for this starting point, return true; otherwise, return false
	 */
	boolean isDestination(V origin, V vert);

	/**
	 * Starting from a starting point to find the path in the graph, it should be possible to reach all endpoints and call back the search results through a callback function, which requires a specific implementation of the algorithm.
	 * <p>In terms of implementation, for a starting point, it should be able to generate paths for all reachable endpoints and return path and endpoint information through callback functions.
	 *
	 * @param origin	   The origin of the search, starting from this point to search the entire graph
	 * @param pathConsumer The data callback function for path search will pass each generated path and its endpoint as parameters to this callback function
	 */
	void findPath(V origin, PathAcceptor<V> pathConsumer);

	/**
	 * Traverse all vertices in the graph from the given original position and receive each vertex from the callback function.
	 * <p>The implementation should correctly output all vertices, and each vertex should perform a callback output on the function.
	 *
	 * @param origin	   Starting point of traversal
	 * @param vertConsumer The callback function for vertices, from which each vertex receives a callback
	 */
	void eachVertices(V origin, VerticesAcceptor<V> vertConsumer);
}
