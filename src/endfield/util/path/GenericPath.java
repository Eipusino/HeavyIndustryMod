package endfield.util.path;

import arc.func.Cons;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Universal path implementation based on {@link LinkedList},
 * which can meet the storage requirements of general path information.
 *
 * @since 1.0.5
 */
public class GenericPath<V> implements IPath<V> {
	final LinkedList<V> path = new LinkedList<>();

	@Override
	public void addFirst(V next) {
		path.addFirst(next);
	}

	@Override
	public void addLast(V next) {
		path.addLast(next);
	}

	@Override
	public V origin() {
		return path.getFirst();
	}

	@Override
	public V destination() {
		return path.getLast();
	}

	@Override
	public Iterator<V> iterator() {
		return path.iterator();
	}

	@Override
	public void each(Cons<? super V> cons) {
		Iterator<V> iterator = path.iterator();
		while (iterator.hasNext()) cons.get(iterator.next());
	}
}
