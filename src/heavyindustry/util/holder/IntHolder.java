package heavyindustry.util.holder;

import heavyindustry.util.ObjectUtils;

public class IntHolder<V> implements Cloneable {
	public int key;
	public V value;

	public IntHolder() {}

	public IntHolder(int k, V v) {
		key = k;
		value = v;
	}

	public IntHolder<V> set(int k, V v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof IntHolder<?> that && key == that.key && ObjectUtils.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return key ^ ObjectUtils.hashCode(value);
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	@SuppressWarnings("unchecked")
	public IntHolder<V> copy() {
		try {
			return (IntHolder<V>) super.clone();
		} catch (CloneNotSupportedException awful) {
			return new IntHolder<>(key, value);
		}
	}
}
