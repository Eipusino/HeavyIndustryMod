package heavyindustry.util.concurrent.holder;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CharHolder<V> implements Cloneable, Comparable<CharHolder<?>> {
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
		return o instanceof CharHolder<?> that && key == that.key && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return (int) key ^ Objects.hashCode(value);
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

	@Override
	public int compareTo(@NotNull CharHolder<?> o) {
		return Character.compare(key, o.key);
	}
}
