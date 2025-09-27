package heavyindustry.util;

import arc.math.Mathf;
import arc.util.ArcRuntimeException;
import heavyindustry.func.Charc;

import java.util.NoSuchElementException;

import static heavyindustry.util.Constant.EMPTY;
import static heavyindustry.util.Constant.PRIME2;
import static heavyindustry.util.Constant.PRIME3;

/**
 * An unordered set that uses char keys. This implementation uses cuckoo hashing using 3 hashes, random walking, and a small stash
 * for problematic keys. No allocation is done except when growing the table size. <br>
 * <br>
 * This set performs very fast contains and remove (typically O(1), worst case O(log(n))). Add may be a bit slower, depending on
 * hash collisions. Load factors greater than 0.91 greatly increase the chances the set will have to rehash to the next higher POT
 * size.
 *
 * @author Nathan Sweet
 */
public class CharSet {
	public int size;

	char[] keyTable;
	int capacity, stashSize;
	boolean hasZeroValue;

	float loadFactor;
	int hashShift, mask, threshold;
	int stashCapacity;
	int pushIterations;

	CharSetIterator iterator1, iterator2;

	/** Creates a new set with an initial capacity of 51 and a load factor of 0.8. */
	public CharSet() {
		this(51, 0.8f);
	}

	/**
	 * Creates a new set with a load factor of 0.8.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 */
	public CharSet(int initialCapacity) {
		this(initialCapacity, 0.8f);
	}

	/**
	 * Creates a new set with the specified initial capacity and load factor. This set will hold initialCapacity items before
	 * growing the backing table.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 */
	public CharSet(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0) throw new IllegalArgumentException("initialCapacity must be >= 0: " + initialCapacity);
		initialCapacity = Mathf.nextPowerOfTwo((int) Math.ceil(initialCapacity / loadFactor));
		if (initialCapacity > 1 << 30)
			throw new IllegalArgumentException("initialCapacity is too large: " + initialCapacity);
		capacity = initialCapacity;

		if (loadFactor <= 0) throw new IllegalArgumentException("loadFactor must be > 0: " + loadFactor);
		this.loadFactor = loadFactor;

		threshold = (int) (capacity * loadFactor);
		mask = capacity - 1;
		hashShift = 31 - Integer.numberOfTrailingZeros(capacity);
		stashCapacity = Math.max(3, (int) Math.ceil(Math.log(capacity)) * 2);
		pushIterations = Math.max(Math.min(capacity, 8), (int) Math.sqrt(capacity) / 8);

		keyTable = new char[capacity + stashCapacity];
	}

	/** Creates a new set identical to the specified set. */
	public CharSet(CharSet set) {
		this((int) Math.floor(set.capacity * set.loadFactor), set.loadFactor);
		stashSize = set.stashSize;
		System.arraycopy(set.keyTable, 0, keyTable, 0, set.keyTable.length);
		size = set.size;
		hasZeroValue = set.hasZeroValue;
	}

	public static CharSet with(char... array) {
		CharSet set = new CharSet();
		set.addAll(array);
		return set;
	}

	public void each(Charc cons) {
		CharSetIterator iter = iterator();
		while (iter.hasNext) {
			cons.get(iter.next());
		}
	}

	/** Returns true if the key was not already in the set. */
	public boolean add(char key) {
		if (key == 0) {
			if (hasZeroValue) return false;
			hasZeroValue = true;
			size++;
			return true;
		}

		// Check for existing keys.
		int index1 = key & mask;
		char key1 = keyTable[index1];
		if (key1 == key) return false;

		int index2 = hash2(key);
		char key2 = keyTable[index2];
		if (key2 == key) return false;

		int index3 = hash3(key);
		char key3 = keyTable[index3];
		if (key3 == key) return false;

		// Find key in the stash.
		for (int i = capacity, n = i + stashSize; i < n; i++)
			if (keyTable[i] == key) return false;

		// Check for empty buckets.
		if (key1 == EMPTY) {
			keyTable[index1] = key;
			if (size++ >= threshold) resize(capacity << 1);
			return true;
		}

		if (key2 == EMPTY) {
			keyTable[index2] = key;
			if (size++ >= threshold) resize(capacity << 1);
			return true;
		}

		if (key3 == EMPTY) {
			keyTable[index3] = key;
			if (size++ >= threshold) resize(capacity << 1);
			return true;
		}

		push(key, index1, key1, index2, key2, index3, key3);
		return true;
	}

	public void addAll(CharSeq array) {
		addAll(array.items, 0, array.size);
	}

	public void addAll(CharSeq array, int offset, int length) {
		if (offset + length > array.size)
			throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
		addAll(array.items, offset, length);
	}

	public void addAll(char... array) {
		addAll(array, 0, array.length);
	}

	public void addAll(char[] array, int offset, int length) {
		ensureCapacity(length);
		for (int i = offset, n = i + length; i < n; i++)
			add(array[i]);
	}

	public void addAll(CharSet set) {
		ensureCapacity(set.size);
		CharSetIterator iterator = set.iterator();
		while (iterator.hasNext)
			add(iterator.next());
	}

	/** Skips checks for existing keys. */
	void addResize(char key) {
		if (key == 0) {
			hasZeroValue = true;
			return;
		}

		// Check for empty buckets.
		int index1 = key & mask;
		char key1 = keyTable[index1];
		if (key1 == EMPTY) {
			keyTable[index1] = key;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		int index2 = hash2(key);
		char key2 = keyTable[index2];
		if (key2 == EMPTY) {
			keyTable[index2] = key;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		int index3 = hash3(key);
		char key3 = keyTable[index3];
		if (key3 == EMPTY) {
			keyTable[index3] = key;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		push(key, index1, key1, index2, key2, index3, key3);
	}

	void push(char insertKey, int index1, char key1, int index2, char key2, int index3, char key3) {
		// Push keys until an empty bucket is found.
		char evictedKey;
		int i = 0;
		do {
			// Replace the key and value for one of the hashes.
			switch (Mathf.random(2)) {
				case 0:
					evictedKey = key1;
					keyTable[index1] = insertKey;
					break;
				case 1:
					evictedKey = key2;
					keyTable[index2] = insertKey;
					break;
				default:
					evictedKey = key3;
					keyTable[index3] = insertKey;
					break;
			}

			// If the evicted key hashes to an empty bucket, put it there and stop.
			index1 = evictedKey & mask;
			key1 = keyTable[index1];
			if (key1 == EMPTY) {
				keyTable[index1] = evictedKey;
				if (size++ >= threshold) resize(capacity << 1);
				return;
			}

			index2 = hash2(evictedKey);
			key2 = keyTable[index2];
			if (key2 == EMPTY) {
				keyTable[index2] = evictedKey;
				if (size++ >= threshold) resize(capacity << 1);
				return;
			}

			index3 = hash3(evictedKey);
			key3 = keyTable[index3];
			if (key3 == EMPTY) {
				keyTable[index3] = evictedKey;
				if (size++ >= threshold) resize(capacity << 1);
				return;
			}

			if (++i == pushIterations) break;

			insertKey = evictedKey;
		} while (true);

		addStash(evictedKey);
	}

	void addStash(char key) {
		if (stashSize == stashCapacity) {
			// Too many pushes occurred and the stash is full, increase the table size.
			resize(capacity << 1);
			addResize(key);
			return;
		}
		// Store key in the stash.
		int index = capacity + stashSize;
		keyTable[index] = key;
		stashSize++;
		size++;
	}

	/** Returns true if the key was removed. */
	public boolean remove(char key) {
		if (key == 0) {
			if (!hasZeroValue) return false;
			hasZeroValue = false;
			size--;
			return true;
		}

		int index = key & mask;
		if (keyTable[index] == key) {
			keyTable[index] = EMPTY;
			size--;
			return true;
		}

		index = hash2(key);
		if (keyTable[index] == key) {
			keyTable[index] = EMPTY;
			size--;
			return true;
		}

		index = hash3(key);
		if (keyTable[index] == key) {
			keyTable[index] = EMPTY;
			size--;
			return true;
		}

		return removeStash(key);
	}

	boolean removeStash(char key) {
		for (int i = capacity, n = i + stashSize; i < n; i++) {
			if (keyTable[i] == key) {
				removeStashIndex(i);
				size--;
				return true;
			}
		}
		return false;
	}

	void removeStashIndex(int index) {
		// If the removed location was not last, move the last tuple to the removed location.
		stashSize--;
		int lastIndex = capacity + stashSize;
		if (index < lastIndex) keyTable[index] = keyTable[lastIndex];
	}

	/** Returns true if the set is empty. */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Reduces the size of the backing arrays to be the specified capacity or less. If the capacity is already less, nothing is
	 * done. If the set contains more items than the specified capacity, the next highest power of two capacity is used instead.
	 */
	public void shrink(int maximumCapacity) {
		if (maximumCapacity < 0) throw new IllegalArgumentException("maximumCapacity must be >= 0: " + maximumCapacity);
		if (size > maximumCapacity) maximumCapacity = size;
		if (capacity <= maximumCapacity) return;
		maximumCapacity = Mathf.nextPowerOfTwo(maximumCapacity);
		resize(maximumCapacity);
	}

	/** Clears the set and reduces the size of the backing arrays to be the specified capacity if they are larger. */
	public void clear(int maximumCapacity) {
		if (capacity <= maximumCapacity) {
			clear();
			return;
		}
		hasZeroValue = false;
		size = 0;
		resize(maximumCapacity);
	}

	public void clear() {
		if (size == 0) return;
		for (int i = capacity + stashSize; i-- > 0; )
			keyTable[i] = EMPTY;
		size = 0;
		stashSize = 0;
		hasZeroValue = false;
	}

	public boolean contains(char key) {
		if (key == 0) return hasZeroValue;
		int index = key & mask;
		if (keyTable[index] != key) {
			index = hash2(key);
			if (keyTable[index] != key) {
				index = hash3(key);
				if (keyTable[index] != key) return containsKeyStash(key);
			}
		}
		return true;
	}

	boolean containsKeyStash(char key) {
		for (int i = capacity, n = i + stashSize; i < n; i++)
			if (keyTable[i] == key) return true;
		return false;
	}

	public int first() {
		if (hasZeroValue) return 0;
		for (int i = 0, n = capacity + stashSize; i < n; i++)
			if (keyTable[i] != EMPTY) return keyTable[i];
		throw new IllegalStateException("IntSet is empty.");
	}

	/**
	 * Increases the size of the backing array to accommodate the specified number of additional items. Useful before adding many
	 * items to avoid multiple backing array resizes.
	 */
	public void ensureCapacity(int additionalCapacity) {
		if (additionalCapacity < 0)
			throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
		int sizeNeeded = size + additionalCapacity;
		if (sizeNeeded >= threshold) resize(Mathf.nextPowerOfTwo((int) Math.ceil(sizeNeeded / loadFactor)));
	}

	void resize(int newSize) {
		int oldEndIndex = capacity + stashSize;

		capacity = newSize;
		threshold = (int) (newSize * loadFactor);
		mask = newSize - 1;
		hashShift = 31 - Integer.numberOfTrailingZeros(newSize);
		stashCapacity = Math.max(3, (int) Math.ceil(Math.log(newSize)) * 2);
		pushIterations = Math.max(Math.min(newSize, 8), (int) Math.sqrt(newSize) / 8);

		char[] oldKeyTable = keyTable;

		keyTable = new char[newSize + stashCapacity];

		int oldSize = size;
		size = hasZeroValue ? 1 : 0;
		stashSize = 0;
		if (oldSize > 0) {
			for (int i = 0; i < oldEndIndex; i++) {
				char key = oldKeyTable[i];
				if (key != EMPTY) addResize(key);
			}
		}
	}

	int hash2(int h) {
		h *= PRIME2;
		return (h ^ h >>> hashShift) & mask;
	}

	int hash3(int h) {
		h *= PRIME3;
		return (h ^ h >>> hashShift) & mask;
	}

	@Override
	public int hashCode() {
		int h = 0;
		for (int i = 0, n = capacity + stashSize; i < n; i++)
			if (keyTable[i] != EMPTY) h += keyTable[i];
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CharSet other)) return false;
		if (other.size != size) return false;
		if (other.hasZeroValue != hasZeroValue) return false;
		for (int i = 0, n = capacity + stashSize; i < n; i++)
			if (keyTable[i] != EMPTY && !other.contains(keyTable[i])) return false;
		return true;
	}

	@Override
	public String toString() {
		if (size == 0) return "[]";
		StringBuilder buffer = new StringBuilder(32);
		buffer.append('[');
		int i = keyTable.length;
		if (hasZeroValue)
			buffer.append("0");
		else {
			while (i-- > 0) {
				int key = keyTable[i];
				if (key == EMPTY) continue;
				buffer.append(key);
				break;
			}
		}
		while (i-- > 0) {
			int key = keyTable[i];
			if (key == EMPTY) continue;
			buffer.append(", ");
			buffer.append(key);
		}
		buffer.append(']');
		return buffer.toString();
	}

	/**
	 * Returns an iterator for the keys in the set. Remove is supported. Note that the same iterator instance is returned each time
	 * this method is called. Use the {@link CharSetIterator} constructor for nested or multithreaded iteration.
	 */
	public CharSetIterator iterator() {
		if (iterator1 == null) {
			iterator1 = new CharSetIterator(this);
			iterator2 = new CharSetIterator(this);
		}
		if (!iterator1.valid) {
			iterator1.reset();
			iterator1.valid = true;
			iterator2.valid = false;
			return iterator1;
		}
		iterator2.reset();
		iterator2.valid = true;
		iterator1.valid = false;
		return iterator2;
	}

	public static class CharSetIterator {
		static final int INDEX_ILLEGAL = -2;
		static final int INDEX_ZERO = -1;
		final CharSet set;
		public boolean hasNext;
		int nextIndex, currentIndex;
		boolean valid = true;

		public CharSetIterator(CharSet set) {
			this.set = set;
			reset();
		}

		public void reset() {
			currentIndex = INDEX_ILLEGAL;
			nextIndex = INDEX_ZERO;
			if (set.hasZeroValue)
				hasNext = true;
			else
				findNextIndex();
		}

		void findNextIndex() {
			hasNext = false;
			char[] keyTable = set.keyTable;
			for (int n = set.capacity + set.stashSize; ++nextIndex < n; ) {
				if (keyTable[nextIndex] != EMPTY) {
					hasNext = true;
					break;
				}
			}
		}

		public void remove() {
			if (currentIndex == INDEX_ZERO && set.hasZeroValue) {
				set.hasZeroValue = false;
			} else if (currentIndex < 0) {
				throw new IllegalStateException("next must be called before remove.");
			} else if (currentIndex >= set.capacity) {
				set.removeStashIndex(currentIndex);
				nextIndex = currentIndex - 1;
				findNextIndex();
			} else {
				set.keyTable[currentIndex] = EMPTY;
			}
			currentIndex = INDEX_ILLEGAL;
			set.size--;
		}

		public char next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			char key = nextIndex == INDEX_ZERO ? 0 : set.keyTable[nextIndex];
			currentIndex = nextIndex;
			findNextIndex();
			return key;
		}

		/** Returns a new array containing the remaining keys. */
		public CharSeq toArray() {
			CharSeq array = new CharSeq(true, set.size);
			while (hasNext)
				array.add(next());
			return array;
		}
	}
}
