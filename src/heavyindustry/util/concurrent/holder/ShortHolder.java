package heavyindustry.util.concurrent.holder;

import java.util.Objects;

public class ShortHolder<V> implements Cloneable {
	public short key;
	public V value;

	public ShortHolder() {}

	public ShortHolder(short k, V v) {
		key = k;
		value = v;
	}

	public ShortHolder<V> set(short k, V v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof ShortHolder<?> that && key == that.key && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return key ^ Objects.hashCode(value);
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	@SuppressWarnings("unchecked")
	public ShortHolder<V> copy() {
		try {
			return (ShortHolder<V>) super.clone();
		} catch (CloneNotSupportedException awful) {
			return new ShortHolder<>(key, value);
		}
	}
}
