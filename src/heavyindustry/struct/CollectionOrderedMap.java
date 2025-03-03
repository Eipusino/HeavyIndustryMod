package heavyindustry.struct;

import arc.struct.ObjectMap;
import arc.struct.OrderedMap;
import arc.struct.Seq;

/**
 * Implementation of Ordered Map based on {@link OrderedMap} wrapper for Java collection framework
 * used in places where Java specifications are required and OrderedMap does not create nodes.
 */
public class CollectionOrderedMap<K, V> extends CollectionObjectMap<K, V> {
	public Seq<K> orderedKeys;

	public CollectionOrderedMap() {
		setMap(16, 0.75f);
	}

	public CollectionOrderedMap(int capacity) {
		setMap(capacity, 0.75f);
	}

	public CollectionOrderedMap(int capacity, float loadFactor) {
		setMap(capacity, loadFactor);
	}

	public CollectionOrderedMap(ObjectMap<? extends K, ? extends V> map) {
		setMap(16, 0.75f);
		this.map.putAll(map);
	}

	@Override
	protected void setMap(int capacity, float loadFactor) {
		map = new OrderedMap<>(capacity, loadFactor);
		orderedKeys = ((OrderedMap<K, V>) map).orderedKeys();
	}
}
