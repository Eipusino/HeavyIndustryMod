package heavyindustry.util.holder;

import heavyindustry.util.ObjectUtils;

public class CharHolder<V> implements Cloneable {
	public char key;
	public V value;

	public CharHolder() {}

	public CharHolder(char k, V v) {
		key = k;
		value = v;
	}

	public CharHolder<V> set(char k, V v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof CharHolder<?> that && key == that.key && ObjectUtils.equals(value, that.value);
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
	public CharHolder<V> copy() {
		try {
			return (CharHolder<V>) super.clone();
		} catch (CloneNotSupportedException awful) {
			return new CharHolder<>(key, value);
		}
	}
}
