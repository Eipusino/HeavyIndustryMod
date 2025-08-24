package heavyindustry.util;

import java.util.Map;

/** @since 1.0.7 */
public class AtomicKeyValueHolder<K, V> implements Map.Entry<K, V>, Cloneable {
	public volatile K key;
	public volatile V value;

	public AtomicKeyValueHolder() {
		this(null, null);
	}

	public AtomicKeyValueHolder(K k, V v) {
		key = k;
		value = v;
	}

	public void reset() {
		key = null;
		value = null;
	}

	@SuppressWarnings("unchecked")
	public AtomicKeyValueHolder<K, V> copy() {
		try {
			return (AtomicKeyValueHolder<K, V>) super.clone();
		} catch (CloneNotSupportedException e) {
			//this shouldn't happen, since we are Cloneable
			return new AtomicKeyValueHolder<>(key, value);
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
