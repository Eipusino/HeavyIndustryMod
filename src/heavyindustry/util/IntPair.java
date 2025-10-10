package heavyindustry.util;

public class IntPair<V> implements Cloneable {
	public int key;
	public V value;

	public IntPair() {}

	public IntPair(int k, V v) {
		key = k;
		value = v;
	}

	public IntPair<V> set(int k, V v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof IntPair<?> pair && key == pair.key && ObjectUtils.equals(value, pair.value);
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
	public IntPair<V> copy() {
		try {
			return (IntPair<V>) super.clone();
		} catch (CloneNotSupportedException suck) {
			return new IntPair<>(key, value);
		}
	}
}
