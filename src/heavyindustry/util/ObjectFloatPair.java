package heavyindustry.util;

public class ObjectFloatPair<K> implements Cloneable {
	public K key;
	public float value;

	public ObjectFloatPair() {}

	public ObjectFloatPair(K k, float v) {
		key = k;
		value = v;
	}

	public ObjectFloatPair<K> set(K k, float v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(key) ^ Float.floatToIntBits(value);
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	@SuppressWarnings("unchecked")
	public ObjectFloatPair<K> copy() {
		try {
			return (ObjectFloatPair<K>) super.clone();
		} catch (CloneNotSupportedException suck) {
			return new ObjectFloatPair<>(key, value);
		}
	}
}
