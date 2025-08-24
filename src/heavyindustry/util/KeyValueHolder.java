package heavyindustry.util;

import java.util.Map;

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
 * {@link Map#entry Map.entry(k, v)} factory method, which is public.
 *
 * <p>This class differs from AbstractMap.SimpleImmutableEntry in the following ways:
 * it is not serializable, it is final, and its key and value must be non-null.
 * @see Map#ofEntries Map.ofEntries()
 * @since 1.0.7
 */
public class KeyValueHolder<K, V> implements Map.Entry<K, V>, Cloneable {
	protected K key;
	protected V value;

	public KeyValueHolder(K k, V v) {
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
	 * If modifiable is true, replace the given value with the current value.
	 *
	 * @param v value
	 * @return the value
	 */
	@Override
	public V setValue(V v) {
		value = v;
		return value;
	}

	@SuppressWarnings("unchecked")
	public KeyValueHolder<K, V> copy() {
		try {
			return (KeyValueHolder<K, V>) super.clone();
		} catch (CloneNotSupportedException e) {
			//this shouldn't happen, since we are Cloneable
			return new KeyValueHolder<>(key, value);
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
		return o instanceof Map.Entry<?, ?> e
				&& key.equals(e.getKey())
				&& value.equals(e.getValue());
	}

	/**
	 * Returns the hash code value for this map entry. The hash code
	 * is {@code key.hashCode() ^ value.hashCode()}. Note that key and
	 * value are non-null, so hashCode() can be called safely on them.
	 */
	@Override
	public int hashCode() {
		return key.hashCode() ^ value.hashCode();
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
