package heavyindustry.util.holder;

import heavyindustry.util.ObjectUtils;

import java.util.Map.Entry;

/** @since 1.0.7 */
public class AtomicObjectHolder<K, V> implements Entry<K, V>, Cloneable {
	public volatile K key;
	public volatile V value;

	public AtomicObjectHolder() {}

	public AtomicObjectHolder(K k, V v) {
		key = k;
		value = v;
	}

	public void reset() {
		key = null;
		value = null;
	}

	@SuppressWarnings("unchecked")
	public AtomicObjectHolder<K, V> copy() {
		try {
			return (AtomicObjectHolder<K, V>) super.clone();
		} catch (CloneNotSupportedException awful) {
			//this shouldn't happen, since we are Cloneable
			return new AtomicObjectHolder<>(key, value);
		}
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof AtomicObjectHolder<?, ?> that && ObjectUtils.equals(key, that.key) && ObjectUtils.equals(value, that.value);
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
