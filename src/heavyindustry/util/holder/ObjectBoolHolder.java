package heavyindustry.util.holder;

import heavyindustry.util.ObjectUtils;

public class ObjectBoolHolder<K> implements Cloneable {
	public K key;
	public boolean value;

	public ObjectBoolHolder() {}

	public ObjectBoolHolder(K k, boolean v) {
		key = k;
		value = v;
	}

	public ObjectBoolHolder<K> set(K k, boolean v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof ObjectBoolHolder<?> that && value == that.value && ObjectUtils.equals(key, that.key);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(key) ^ ObjectUtils.hashCodeBool(value);
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	@SuppressWarnings("unchecked")
	public ObjectBoolHolder<K> copy() {
		try {
			return (ObjectBoolHolder<K>) super.clone();
		} catch (CloneNotSupportedException suck) {
			return new ObjectBoolHolder<>(key, value);
		}
	}
}
