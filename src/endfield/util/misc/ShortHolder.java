package endfield.util.misc;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/** @since 1.0.7 */
public class ShortHolder<V> implements Cloneable, Comparable<ShortHolder<?>> {
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

	@Override
	public int compareTo(@NotNull ShortHolder<?> o) {
		return Short.compare(key, o.key);
	}
}
