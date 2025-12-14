package heavyindustry.util.concurrent.holder;

import heavyindustry.util.Objects2;

import java.util.Map.Entry;
import java.util.Objects;

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
			//this shouldn't happen, since we are Cloneable
			return new AtomicObjectHolder<>(key, value);
		}
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof AtomicObjectHolder<?, ?> that && Objects.equals(key, that.key) && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(key) ^ Objects.hashCode(value);
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
