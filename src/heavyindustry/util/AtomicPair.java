package heavyindustry.util;

import java.util.Map;

/** @since 1.0.7 */
public class AtomicPair<K, V> implements Map.Entry<K, V>, Cloneable {
	public volatile K key;
	public volatile V value;

	public AtomicPair() {
		this(null, null);
	}

	public AtomicPair(K k, V v) {
		key = k;
		value = v;
	}

	public void reset() {
		key = null;
		value = null;
	}

	@SuppressWarnings("unchecked")
	public AtomicPair<K, V> copy() {
		try {
			return (AtomicPair<K, V>) super.clone();
		} catch (CloneNotSupportedException e) {
			//this shouldn't happen, since we are Cloneable
			return new AtomicPair<>(key, value);
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
	public V setValue(V v) {
		value = v;

		return value;
	}
}
