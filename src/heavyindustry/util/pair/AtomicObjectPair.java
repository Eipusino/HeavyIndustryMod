package heavyindustry.util.pair;

import java.util.Map.Entry;

/** @since 1.0.7 */
public class AtomicObjectPair<K, V> implements Entry<K, V>, Cloneable {
	public volatile K key;
	public volatile V value;

	public AtomicObjectPair() {}

	public AtomicObjectPair(K k, V v) {
		key = k;
		value = v;
	}

	public void reset() {
		key = null;
		value = null;
	}

	@SuppressWarnings("unchecked")
	public AtomicObjectPair<K, V> copy() {
		try {
			return (AtomicObjectPair<K, V>) super.clone();
		} catch (CloneNotSupportedException e) {
			//this shouldn't happen, since we are Cloneable
			return new AtomicObjectPair<>(key, value);
		}
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V newValue) {
		V oldValue = value;
		value = newValue;
		return oldValue;
	}
}
