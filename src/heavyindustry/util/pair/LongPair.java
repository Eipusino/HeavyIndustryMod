package heavyindustry.util.pair;

import heavyindustry.util.ObjectUtils;

public class LongPair<V> implements Cloneable {
	public long key;
	public V value;

	public LongPair() {}

	public LongPair(long k, V v) {
		key = k;
		value = v;
	}

	public LongPair<V> set(long k, V v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof LongPair<?> pair && key == pair.key && ObjectUtils.equals(value, pair.value);
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
	public LongPair<V> copy() {
		try {
			return (LongPair<V>) super.clone();
		} catch (CloneNotSupportedException suck) {
			return new LongPair<>(key, value);
		}
	}
}
