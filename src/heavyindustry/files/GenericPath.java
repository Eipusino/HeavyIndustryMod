package heavyindustry.files;

import java.util.*;

/** Universal path implementation based on {@link LinkedList}, which can meet the storage requirements of general path information. */
public class GenericPath<V> implements IPath<V> {
    private final LinkedList<V> path = new LinkedList<>();

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
}
