package heavyindustry.struct;

import heavyindustry.util.Structf;

public class Pair <K, V> implements Cloneable {

	public final K key;
	public final V value;

	public static <K, V> Pair<K, V> of(K key, V value) {
		return new Pair<>(key, value);
	}

	public Pair(K k, V v) {
		key = k;
		value = v;
	}

	@Override
	public String toString() {
		return "Pair [key=" + key + ", value=" + value + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Pair<?, ?> pair)
			return Structf.equals(key, pair.key) &&
					Structf.equals(value, pair.value);
		return false;
	}

	@Override
	public int hashCode() {
		return Structf.hashCode(key) ^ Structf.hashCode(value);
	}

	@SuppressWarnings("unchecked")
	public Pair<K, V> copy() {
		try {
			return (Pair<K, V>) super.clone();
		} catch (CloneNotSupportedException e) {
			return new Pair<>(key, value);
		}
	}
}
