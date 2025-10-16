package heavyindustry.util.holder;

import heavyindustry.util.ObjectUtils;

public class ObjectIntHolder<K> implements Cloneable {
	public K key;
	public int value;

	public ObjectIntHolder() {}

	public ObjectIntHolder(K k, int v) {
		key = k;
		value = v;
	}

	public ObjectIntHolder<K> set(K k, int v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof ObjectIntHolder<?> that && ObjectUtils.equals(key, that.key) && value == that.value;
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(key) ^ value;
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	@SuppressWarnings("unchecked")
	public ObjectIntHolder<K> copy() {
		try {
			return (ObjectIntHolder<K>) super.clone();
		} catch (CloneNotSupportedException suck) {
			return new ObjectIntHolder<>(key, value);
		}
	}
}
