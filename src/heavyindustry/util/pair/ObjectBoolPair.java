package heavyindustry.util.pair;

import heavyindustry.util.ObjectUtils;

public class ObjectBoolPair<K> implements Cloneable {
	public K key;
	public boolean value;

	public ObjectBoolPair() {}

	public ObjectBoolPair(K k, boolean v) {
		key = k;
		value = v;
	}

	public ObjectBoolPair<K> set(K k, boolean v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ObjectBoolPair<?> that && value == that.value && ObjectUtils.equals(key, that.key);
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
	public ObjectBoolPair<K> copy() {
		try {
			return (ObjectBoolPair<K>) super.clone();
		} catch (CloneNotSupportedException suck) {
			return new ObjectBoolPair<>(key, value);
		}
	}
}
