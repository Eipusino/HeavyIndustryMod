package endfield.util.misc;

import java.util.Objects;

/** @since 1.0.7 */
public class ObjectIntHolder<K> implements Cloneable {
	public K key;
	public int value;

	public ObjectIntHolder() {}

	public ObjectIntHolder(K k, int v) {
		key = k;
		value = v;
	}

	public ObjectIntHolder<K> set(K k, int v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof ObjectIntHolder<?> that && Objects.equals(key, that.key) && value == that.value;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(key) ^ value;
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	@SuppressWarnings("unchecked")
	public ObjectIntHolder<K> copy() {
		try {
			return (ObjectIntHolder<K>) super.clone();
		} catch (CloneNotSupportedException awful) {
			return new ObjectIntHolder<>(key, value);
		}
	}
}
