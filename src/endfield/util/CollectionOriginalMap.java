package endfield.util;

import arc.math.Mathf;

import java.util.Map;

/**
 * This class behavior differs from the standard {@link Map} implementation requirements.
 * <br>Retrieve the object hash value through {@code System.identityHashCode} and compare the objects using {@code ==}.
 *
 * @see Object#equals
 * @see System#identityHashCode
 * @since 1.0.9
 */
public class CollectionOriginalMap<K, V> extends CollectionObjectMap<K, V> {
	public CollectionOriginalMap(Class<?> keyType, Class<?> valueType) {
		super(keyType, valueType);
	}

	public CollectionOriginalMap(Class<?> keyType, Class<?> valueType, int initialCapacity) {
		super(keyType, valueType, initialCapacity);
	}

	public CollectionOriginalMap(Class<?> keyType, Class<?> valueType, int initialCapacity, float loadFactor) {
		super(keyType, valueType, initialCapacity, loadFactor);
	}

	public CollectionOriginalMap(CollectionObjectMap<? extends K, ? extends V> map) {
		super(map);
	}

	public CollectionOriginalMap(Map<? extends K, ? extends V> map, Class<?> keyType, Class<?> valueType) {
		super(map, keyType, valueType);
	}

	/** Returns the old value associated with the specified key, or null. */
	@Override
	public V put(K key, V value) {
		if (key == null) return null;

		// Check for existing keys.
		int hashCode = System.identityHashCode(key);
		int index1 = hashCode & mask;
		K key1 = keyTable[index1];
		if (key == key1) {
			V oldValue = valueTable[index1];
			valueTable[index1] = value;
			return oldValue;
		}

		int index2 = hash2(hashCode);
		K key2 = keyTable[index2];
		if (key == key2) {
			V oldValue = valueTable[index2];
			valueTable[index2] = value;
			return oldValue;
		}

		int index3 = hash3(hashCode);
		K key3 = keyTable[index3];
		if (key == key3) {
			V oldValue = valueTable[index3];
			valueTable[index3] = value;
			return oldValue;
		}

		// Update key in the stash.
		for (int i = capacity, n = i + stashSize; i < n; i++) {
			if (key == keyTable[i]) {
				V oldValue = valueTable[i];
				valueTable[i] = value;
				return oldValue;
			}
		}

		// Check for empty buckets.
		if (key1 == null) {
			keyTable[index1] = key;
			valueTable[index1] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return null;
		}

		if (key2 == null) {
			keyTable[index2] = key;
			valueTable[index2] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return null;
		}

		if (key3 == null) {
			keyTable[index3] = key;
			valueTable[index3] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return null;
		}

		push(key, value, index1, key1, index2, key2, index3, key3);
		return null;
	}

	/** Skips checks for existing keys. */
	@Override
	protected void putResize(K key, V value) {
		// Check for empty buckets.
		int hashCode = System.identityHashCode(key);
		int index1 = hashCode & mask;
		K key1 = keyTable[index1];
		if (key1 == null) {
			keyTable[index1] = key;
			valueTable[index1] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		int index2 = hash2(hashCode);
		K key2 = keyTable[index2];
		if (key2 == null) {
			keyTable[index2] = key;
			valueTable[index2] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		int index3 = hash3(hashCode);
		K key3 = keyTable[index3];
		if (key3 == null) {
			keyTable[index3] = key;
			valueTable[index3] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		push(key, value, index1, key1, index2, key2, index3, key3);
	}

	@Override
	protected void push(K insertKey, V insertValue, int index1, K key1, int index2, K key2, int index3, K key3) {
		// Push keys until an empty bucket is found.
		K evictedKey;
		V evictedValue;
		int i = 0;
		do {
			// Replace the key and value for one of the hashes.
			switch (Mathf.random(2)) {
				case 0:
					evictedKey = key1;
					evictedValue = valueTable[index1];
					keyTable[index1] = insertKey;
					valueTable[index1] = insertValue;
					break;
				case 1:
					evictedKey = key2;
					evictedValue = valueTable[index2];
					keyTable[index2] = insertKey;
					valueTable[index2] = insertValue;
					break;
				default:
					evictedKey = key3;
					evictedValue = valueTable[index3];
					keyTable[index3] = insertKey;
					valueTable[index3] = insertValue;
					break;
			}

			// If the evicted key hashes to an empty bucket, put it there and stop.
			int hashCode = System.identityHashCode(evictedKey);
			index1 = hashCode & mask;
			key1 = keyTable[index1];
			if (key1 == null) {
				keyTable[index1] = evictedKey;
				valueTable[index1] = evictedValue;
				if (size++ >= threshold) resize(capacity << 1);
				return;
			}

			index2 = hash2(hashCode);
			key2 = keyTable[index2];
			if (key2 == null) {
				keyTable[index2] = evictedKey;
				valueTable[index2] = evictedValue;
				if (size++ >= threshold) resize(capacity << 1);
				return;
			}

			index3 = hash3(hashCode);
			key3 = keyTable[index3];
			if (key3 == null) {
				keyTable[index3] = evictedKey;
				valueTable[index3] = evictedValue;
				if (size++ >= threshold) resize(capacity << 1);
				return;
			}

			if (++i == pushIterations) break;

			insertKey = evictedKey;
			insertValue = evictedValue;
		} while (true);

		putStash(evictedKey, evictedValue);
	}

	/** Returns the value for the specified key, or null if the key is not in the map. */
	@Override
	public V get(Object key) {
		if (key == null) return null;

		int hashCode = System.identityHashCode(key);
		int index = hashCode & mask;
		if (key != keyTable[index]) {
			index = hash2(hashCode);
			if (key != keyTable[index]) {
				index = hash3(hashCode);
				if (key != keyTable[index]) return getStash(key, null);
			}
		}
		return valueTable[index];
	}

	/** Returns the value for the specified key, or the default value if the key is not in the map. */
	@Override
	public V get(K key, V defaultValue) {
		if (key == null) return defaultValue;

		int hashCode = System.identityHashCode(key);
		int index = hashCode & mask;
		if (key != keyTable[index]) {
			index = hash2(hashCode);
			if (key != keyTable[index]) {
				index = hash3(hashCode);
				if (key != keyTable[index]) return getStash(key, defaultValue);
			}
		}
		return valueTable[index];
	}

	@Override
	protected V getStash(Object key, V defaultValue) {
		for (int i = capacity, n = i + stashSize; i < n; i++)
			if (key == keyTable[i]) return valueTable[i];
		return defaultValue;
	}

	/** Returns the value associated with the key, or null. */
	@Override
	public V remove(Object key) {
		if (key == null) return null;

		int hashCode = System.identityHashCode(key);
		int index = hashCode & mask;
		if (key == keyTable[index]) {
			keyTable[index] = null;
			V oldValue = valueTable[index];
			valueTable[index] = null;
			size--;
			return oldValue;
		}

		index = hash2(hashCode);
		if (key == keyTable[index]) {
			keyTable[index] = null;
			V oldValue = valueTable[index];
			valueTable[index] = null;
			size--;
			return oldValue;
		}

		index = hash3(hashCode);
		if (key == keyTable[index]) {
			keyTable[index] = null;
			V oldValue = valueTable[index];
			valueTable[index] = null;
			size--;
			return oldValue;
		}

		return removeStash(key);
	}

	@Override
	protected V removeStash(Object key) {
		for (int i = capacity, n = i + stashSize; i < n; i++) {
			if (key == keyTable[i]) {
				V oldValue = valueTable[i];
				removeStashIndex(i);
				size--;
				return oldValue;
			}
		}
		return null;
	}

	@Override
	public boolean containsKey(Object key) {
		if (key == null) return false;

		int hashCode = System.identityHashCode(key);
		int index = hashCode & mask;
		if (key != keyTable[index]) {
			index = hash2(hashCode);
			if (key != keyTable[index]) {
				index = hash3(hashCode);
				if (key != keyTable[index]) return containsKeyStash(key);
			}
		}
		return true;
	}

	@Override
	protected boolean containsKeyStash(Object key) {
		for (int i = capacity, n = i + stashSize; i < n; i++)
			if (key == keyTable[i]) return true;
		return false;
	}

	@Override
	public int hashCode() {
		int h = 0;
		for (int i = 0, n = capacity + stashSize; i < n; i++) {
			K key = keyTable[i];
			if (key != null) {
				h += System.identityHashCode(key) * 31;

				V value = valueTable[i];
				if (value != null) {
					h += System.identityHashCode(value);
				}
			}
		}
		return h;
	}

	@Override
	public Keys keySet() {
		if (keys1 == null) {
			keys1 = new OriginalMapKeys();
			keys2 = new OriginalMapKeys();
		}
		if (!keys1.valid) {
			keys1.reset();
			keys1.valid = true;
			keys2.valid = false;
			return keys1;
		}
		keys2.reset();
		keys2.valid = true;
		keys1.valid = false;
		return keys2;
	}

	public class OriginalMapKeys extends Keys {
		@Override
		public int hashCode() {
			int hashCode = 0;
			for (K obj : this) {
				if (obj != null)
					hashCode += System.identityHashCode(obj);
			}
			return hashCode;
		}
	}
}
