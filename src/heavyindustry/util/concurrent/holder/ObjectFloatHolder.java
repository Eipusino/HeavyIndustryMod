package heavyindustry.util.concurrent.holder;

import heavyindustry.util.Objects2;

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
		return o instanceof ObjectFloatHolder<?> that && Objects2.equals(key, that.key) && value == that.value;
	}

	@Override
	public int hashCode() {
		return Objects2.hashCode(key) ^ Float.floatToIntBits(value);
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	@SuppressWarnings("unchecked")
	public ObjectFloatHolder<K> copy() {
		try {
			return (ObjectFloatHolder<K>) super.clone();
		} catch (CloneNotSupportedException awful) {
			return new ObjectFloatHolder<>(key, value);
		}
	}
}
