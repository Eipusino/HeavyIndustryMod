package heavyindustry.util;

public class ObjectIntPair<K> implements Cloneable {
	public K key;
	public int value;

	public ObjectIntPair() {}

	public ObjectIntPair(K k, int v) {
		key = k;
		value = v;
	}

	public ObjectIntPair<K> set(K k, int v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ObjectIntPair<?> pair && ObjectUtils.equals(key, pair.key) && value == pair.value;
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(key) ^ value;
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	@SuppressWarnings("unchecked")
	public ObjectIntPair<K> copy() {
		try {
			return (ObjectIntPair<K>) super.clone();
		} catch (CloneNotSupportedException suck) {
			return new ObjectIntPair<>(key, value);
		}
	}
}
