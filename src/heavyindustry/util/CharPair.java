package heavyindustry.util;

public class CharPair<V> implements Cloneable {
	public char key;
	public V value;

	public CharPair() {}

	public CharPair(char k, V v) {
		key = k;
		value = v;
	}

	public CharPair<V> set(char k, V v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof CharPair<?> pair && key == pair.key && ObjectUtils.equals(value, pair.value);
	}

	@Override
	public int hashCode() {
		return (int) key ^ ObjectUtils.hashCode(value);
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	@SuppressWarnings("unchecked")
	public CharPair<V> copy() {
		try {
			return (CharPair<V>) super.clone();
		} catch (CloneNotSupportedException suck) {
			return new CharPair<>(key, value);
		}
	}
}
