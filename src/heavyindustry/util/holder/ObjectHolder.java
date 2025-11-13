package heavyindustry.util.holder;

import heavyindustry.util.Objects2;

import java.util.Map.Entry;

/**
 * An immutable container for a key and a value, suitable for use
 * in creating and populating {@code Map} instances.
 *
 * <p>This is a <a href="{@docRoot}/java.base/java/lang/doc-files/ValueBased.html">value-based</a>
 * class; programmers should treat instances that are
 * {@linkplain #equals(Object) equal} as interchangeable and should not
 * use instances for synchronization, or unpredictable behavior may
 * occur. For example, in a future release, synchronization may fail.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @apiNote This class is not public. Instances can be created using the
 * {@link java.util.Map#entry Map.entry(k, v)} factory method, which is public.
 *
 * <p>This class differs from AbstractMap.SimpleImmutableEntry in the following ways:
 * it is not serializable, it is final, and its key and value must be non-null.
 * @see java.util.Map#ofEntries Map.ofEntries()
 * @since 1.0.7
 */
public class ObjectHolder<K, V> implements Entry<K, V>, Cloneable {
	public K key;
	public V value;

	public ObjectHolder() {}

	public ObjectHolder(K k, V v) {
		key = k;
		value = v;
	}

	/**
	 * Gets the key from this holder.
	 *
	 * @return the key
	 */
	@Override
	public K getKey() {
		return key;
	}

	/**
	 * Gets the value from this holder.
	 *
	 * @return the value
	 */
	@Override
	public V getValue() {
		return value;
	}

	/**
	 * Replaces the value corresponding to this entry with the specified value.
	 *
	 * @return old value corresponding to the entry
	 */
	@Override
	public V setValue(V newValue) {
		V oldValue = value;
		value = newValue;
		return oldValue;
	}

	public ObjectHolder<K, V> set(K k, V v) {
		key = k;
		value = v;

		return this;
	}

	@SuppressWarnings("unchecked")
	public ObjectHolder<K, V> copy() {
		try {
			return (ObjectHolder<K, V>) super.clone();
		} catch (CloneNotSupportedException awful) {
			//this shouldn't happen, since we are Cloneable
			return new ObjectHolder<>(key, value);
		}
	}

	/**
	 * Compares the specified object with this entry for equality.
	 * Returns {@code true} if the given object is also a map entry and
	 * the two entries' keys and values are equal. Note that key and
	 * value are non-null, so equals() can be called safely on them.
	 */
	@Override
	public boolean equals(Object o) {
		return o instanceof Entry<?, ?> that && Objects2.equals(key, that.getKey()) && Objects2.equals(value, that.getValue());
	}

	@Override
	public int hashCode() {
		return Objects2.hashCode(key) ^ Objects2.hashCode(value);
	}

	/**
	 * Returns a String representation of this map entry.  This
	 * implementation returns the string representation of this
	 * entry's key followed by the equals character ("{@code =}")
	 * followed by the string representation of this entry's value.
	 *
	 * @return a String representation of this map entry
	 */
	@Override
	public String toString() {
		return key + "=" + value;
	}
}
