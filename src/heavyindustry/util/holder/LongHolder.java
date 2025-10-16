package heavyindustry.util.holder;

import heavyindustry.util.ObjectUtils;

public class LongHolder<V> implements Cloneable {
	public long key;
	public V value;

	public LongHolder() {}

	public LongHolder(long k, V v) {
		key = k;
		value = v;
	}

	public LongHolder<V> set(long k, V v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof LongHolder<?> that && key == that.key && ObjectUtils.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCodeLong(key) ^ ObjectUtils.hashCode(value);
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	@SuppressWarnings("unchecked")
	public LongHolder<V> copy() {
		try {
			return (LongHolder<V>) super.clone();
		} catch (CloneNotSupportedException suck) {
			return new LongHolder<>(key, value);
		}
	}
}
