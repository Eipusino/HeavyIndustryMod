package endfield.util.holder;

import java.util.Objects;

/** @since 1.0.7 */
public class IntHolder<V> implements Cloneable, Comparable<IntHolder<?>> {
	public int key;
	public V value;

	public IntHolder() {}

	public IntHolder(int k, V v) {
		key = k;
		value = v;
	}

	public IntHolder<V> set(int k, V v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof IntHolder<?> that && key == that.key && Objects.equals(value, that.value);
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
	public IntHolder<V> copy() {
		try {
			return (IntHolder<V>) super.clone();
		} catch (CloneNotSupportedException awful) {
			return new IntHolder<>(key, value);
		}
	}

	@Override
	public int compareTo(IntHolder<?> o) {
		return Integer.compare(key, o.key);
	}
}
