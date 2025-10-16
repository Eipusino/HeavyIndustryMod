package heavyindustry.util.holder;

import heavyindustry.util.ObjectUtils;

public class ObjectFloatHolder<K> implements Cloneable {
	public K key;
	public float value;

	public ObjectFloatHolder() {}

	public ObjectFloatHolder(K k, float v) {
		key = k;
		value = v;
	}

	public ObjectFloatHolder<K> set(K k, float v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof ObjectFloatHolder<?> that && ObjectUtils.equals(key, that.key) && value == that.value;
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(key) ^ Float.floatToIntBits(value);
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	@SuppressWarnings("unchecked")
	public ObjectFloatHolder<K> copy() {
		try {
			return (ObjectFloatHolder<K>) super.clone();
		} catch (CloneNotSupportedException suck) {
			return new ObjectFloatHolder<>(key, value);
		}
	}
}
