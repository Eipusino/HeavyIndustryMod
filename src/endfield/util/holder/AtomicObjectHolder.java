package endfield.util.holder;

import arc.util.pooling.Pool.Poolable;

import java.util.Map.Entry;

/** @since 1.0.7 */
public class AtomicObjectHolder<K, V> implements Entry<K, V>, Cloneable, Poolable {
	public volatile K key;
	public volatile V value;

	public AtomicObjectHolder() {}

	public AtomicObjectHolder(K k, V v) {
		key = k;
		value = v;
	}

	@Override
	public void reset() {
		key = null;
		value = null;
	}

	public AtomicObjectHolder<K, V> set(K k, V v) {
		key = k;
		value = v;

		return this;
	}

	@SuppressWarnings("unchecked")
	public AtomicObjectHolder<K, V> copy() {
		try {
			return (AtomicObjectHolder<K, V>) super.clone();
		} catch (CloneNotSupportedException awful) {
			return new AtomicObjectHolder<>(key, value);
		}
	}

	@Override
	public String toString() {
		return key + "=" + value;
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
