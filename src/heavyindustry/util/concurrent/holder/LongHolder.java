package heavyindustry.util.concurrent.holder;

import heavyindustry.util.Objects2;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LongHolder<V> implements Cloneable, Comparable<LongHolder<?>> {
	public long key;
	public V value;

	public LongHolder() {}

	public LongHolder(long k, V v) {
		key = k;
		value = v;
	}

	public LongHolder<V> set(long k, V v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof LongHolder<?> that && key == that.key && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects2.longToHash(key) ^ Objects.hashCode(value);
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	@SuppressWarnings("unchecked")
	public LongHolder<V> copy() {
		try {
			return (LongHolder<V>) super.clone();
		} catch (CloneNotSupportedException awful) {
			return new LongHolder<>(key, value);
		}
	}

	@Override
	public int compareTo(@NotNull LongHolder<?> o) {
		return Long.compare(key, o.key);
	}
}
