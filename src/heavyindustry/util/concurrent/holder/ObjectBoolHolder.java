package heavyindustry.util.concurrent.holder;

import heavyindustry.util.Objects2;

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
		return o instanceof ObjectBoolHolder<?> that && value == that.value && Objects2.equals(key, that.key);
	}

	@Override
	public int hashCode() {
		return Objects2.hashCode(key) ^ Objects2.hashCodeBool(value);
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	@SuppressWarnings("unchecked")
	public ObjectBoolHolder<K> copy() {
		try {
			return (ObjectBoolHolder<K>) super.clone();
		} catch (CloneNotSupportedException awful) {
			return new ObjectBoolHolder<>(key, value);
		}
	}
}
