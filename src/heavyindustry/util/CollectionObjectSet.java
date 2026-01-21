package heavyindustry.util;

import arc.func.Boolf;
import arc.func.Cons;
import arc.math.Mathf;
import heavyindustry.math.Mathm;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import static heavyindustry.util.Constant.PRIME2;
import static heavyindustry.util.Constant.PRIME3;

/**
 * An unordered set where the keys are objects. This implementation uses cuckoo hashing using 3 hashes, random walking, and a
 * small stash for problematic keys. Null keys are not allowed. No allocation is done except when growing the table size.<br>
 * <br>This set performs very fast contains and remove (typically O(1), worst case O(log(n))). Add may be a bit slower, depending on
 * hash collisions. Load factors greater than 0.91 greatly increase the chances the set will have to rehash to the next higher POT
 * size.<br><br>Iteration can be very slow for a set with a large capacity. {@link #clear(int)} and {@link #shrink(int)} can be used to reduce
 * the capacity. {@link CollectionOrderedSet} provides much faster iteration.
 *
 * @author Nathan Sweet
 */
public class CollectionObjectSet<E> extends AbstractSet<E> implements ISet<E>, Cloneable {
	public int size;

	public final Class<?> componentType;

	public E[] keyTable;
	public int capacity, stashSize;

	protected float loadFactor;
	protected int hashShift, mask, threshold;
	protected int stashCapacity;
	protected int pushIterations;

	protected transient @Nullable Iter iterator1, iterator2;

	/** Creates a new set with an initial capacity of 51 and a load factor of 0.8. */
	public CollectionObjectSet(Class<?> type) {
		this(type, 51, 0.8f);
	}

	/**
	 * Creates a new set with a load factor of 0.8.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 */
	public CollectionObjectSet(Class<?> type, int initialCapacity) {
		this(type, initialCapacity, 0.8f);
	}

	/**
	 * Creates a new set with the specified initial capacity and load factor. This set will hold initialCapacity items before
	 * growing the backing table.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 * @param type This value must be equal to generic E, otherwise a ClassCastException will be thrown at runtime.
	 */
	@SuppressWarnings("unchecked")
	public CollectionObjectSet(Class<?> type, int initialCapacity, float loadFactor) {
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
		pushIterations = Mathm.clamp(capacity, 8, (int) Math.sqrt(capacity) / 8);

		componentType = type;
		keyTable = (E[]) Array.newInstance(type, capacity + stashCapacity);
	}

	/** Creates a new set identical to the specified set. */
	public CollectionObjectSet(CollectionObjectSet<? extends E> set) {
		this(set.componentType, (int) Math.floor(set.capacity * set.loadFactor), set.loadFactor);
		stashSize = set.stashSize;
		System.arraycopy(set.keyTable, 0, keyTable, 0, set.keyTable.length);
		size = set.size;
	}

	public CollectionObjectSet(Collection<? extends E> collection, Class<?> type) {
		this(type, collection.size());
		addAll(collection);
	}

	@SafeVarargs
	public static <T> CollectionObjectSet<T> with(T... array) {
		CollectionObjectSet<T> set = new CollectionObjectSet<>(array.getClass().getComponentType());
		set.addAll(array);
		return set;
	}

	public static <T> CollectionObjectSet<T> with(CollectionList<T> list) {
		CollectionObjectSet<T> set = new CollectionObjectSet<>(list.componentType);
		set.addAll(list);
		return set;
	}

	/** Allocates a new set with all elements that match the predicate. */
	public CollectionObjectSet<E> select(Boolf<E> predicate) {
		CollectionObjectSet<E> arr = new CollectionObjectSet<>(componentType);
		for (E e : this) {
			if (predicate.get(e)) arr.add(e);
		}
		return arr;
	}

	@SuppressWarnings("unchecked")
	public CollectionObjectSet<E> copy() {
		try {
			CollectionObjectSet<E> out = (CollectionObjectSet<E>) super.clone();
			out.keyTable = Arrays.copyOf(keyTable, keyTable.length);

			out.iterator1 = out.iterator2 = null;

			return out;
		} catch (CloneNotSupportedException e) {
			return new CollectionObjectSet<>(this);
		}
	}

	@Override
	public Class<?> componentType() {
		return componentType;
	}

	@Override
	public void each(Cons<? super E> cons) {
		for (E e : this) {
			cons.get(e);
		}
	}

	/**
	 * Returns true if the key was not already in the set. If this set already contains the key, the call leaves the set unchanged
	 * and returns false.
	 */
	@Override
	@Contract(value = "null -> false")
	public boolean add(@Nullable E key) {
		if (key == null) return false;

		// Check for existing keys.
		int hashCode = key.hashCode();
		int index1 = hashCode & mask;
		E key1 = keyTable[index1];
		if (key.equals(key1)) return false;

		int index2 = hash2(hashCode);
		E key2 = keyTable[index2];
		if (key.equals(key2)) return false;

		int index3 = hash3(hashCode);
		E key3 = keyTable[index3];
		if (key.equals(key3)) return false;

		// Find key in the stash.
		for (int i = capacity, n = i + stashSize; i < n; i++)
			if (key.equals(keyTable[i])) return false;

		// Check for empty buckets.
		if (key1 == null) {
			keyTable[index1] = key;
			if (size++ >= threshold) resize(capacity << 1);
			return true;
		}

		if (key2 == null) {
			keyTable[index2] = key;
			if (size++ >= threshold) resize(capacity << 1);
			return true;
		}

		if (key3 == null) {
			keyTable[index3] = key;
			if (size++ >= threshold) resize(capacity << 1);
			return true;
		}

		push(key, index1, key1, index2, key2, index3, key3);
		return true;
	}

	public void addAll(CollectionList<? extends E> array) {
		addAll(array.items, 0, array.size);
	}

	public void addAll(CollectionList<? extends E> array, int offset, int length) {
		if (offset + length > array.size)
			throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
		addAll(array.items, offset, length);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addAll(E... array) {
		addAll(array, 0, array.length);
	}

	@Override
	public void addAll(E[] array, int offset, int length) {
		ensureCapacity(length);
		for (int i = offset, n = i + length; i < n; i++)
			add(array[i]);
	}

	@Override
	public void addAll(Set<? extends E> set) {
		ensureCapacity(set.size());
		for (E key : set)
			add(key);
	}

	@Override
	public void removeAll(E[] array, int offset, int length) {
		for (int i = offset, n = i + length; i < n; i++)
			remove(array[i]);
	}

	@Override
	public void removeAll(E[] array) {
		for (E e : array) {
			remove(e);
		}
	}

	public void removeAll(CollectionList<? extends E> array) {
		removeAll(array.items, 0, array.size);
	}

	/** Skips checks for existing keys. */
	protected void addResize(E key) {
		// Check for empty buckets.
		int hashCode = key.hashCode();
		int index1 = hashCode & mask;
		E key1 = keyTable[index1];
		if (key1 == null) {
			keyTable[index1] = key;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		int index2 = hash2(hashCode);
		E key2 = keyTable[index2];
		if (key2 == null) {
			keyTable[index2] = key;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		int index3 = hash3(hashCode);
		E key3 = keyTable[index3];
		if (key3 == null) {
			keyTable[index3] = key;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		push(key, index1, key1, index2, key2, index3, key3);
	}

	protected void push(E insertKey, int index1, E key1, int index2, E key2, int index3, E key3) {
		// Push keys until an empty bucket is found.
		E evictedKey;
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
			int hashCode = evictedKey.hashCode();
			index1 = hashCode & mask;
			key1 = keyTable[index1];
			if (key1 == null) {
				keyTable[index1] = evictedKey;
				if (size++ >= threshold) resize(capacity << 1);
				return;
			}

			index2 = hash2(hashCode);
			key2 = keyTable[index2];
			if (key2 == null) {
				keyTable[index2] = evictedKey;
				if (size++ >= threshold) resize(capacity << 1);
				return;
			}

			index3 = hash3(hashCode);
			key3 = keyTable[index3];
			if (key3 == null) {
				keyTable[index3] = evictedKey;
				if (size++ >= threshold) resize(capacity << 1);
				return;
			}

			if (++i == pushIterations) break;

			insertKey = evictedKey;
		} while (true);

		addStash(evictedKey);
	}

	protected void addStash(E key) {
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
	@Override
	public boolean remove(Object key) {
		int hashCode = key.hashCode();
		int index = hashCode & mask;
		if (key.equals(keyTable[index])) {
			keyTable[index] = null;
			size--;
			return true;
		}

		index = hash2(hashCode);
		if (key.equals(keyTable[index])) {
			keyTable[index] = null;
			size--;
			return true;
		}

		index = hash3(hashCode);
		if (key.equals(keyTable[index])) {
			keyTable[index] = null;
			size--;
			return true;
		}

		return removeStash(key);
	}

	protected boolean removeStash(Object key) {
		for (int i = capacity, n = i + stashSize; i < n; i++) {
			if (key.equals(keyTable[i])) {
				removeStashIndex(i);
				size--;
				return true;
			}
		}
		return false;
	}

	protected void removeStashIndex(int index) {
		// If the removed location was not last, move the last tuple to the removed location.
		stashSize--;
		int lastIndex = capacity + stashSize;
		if (index < lastIndex) {
			keyTable[index] = keyTable[lastIndex];
			keyTable[lastIndex] = null;
		}
	}

	@Override
	public int size() {
		return size;
	}

	/** Returns true if the set is empty. */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean any() {
		return size > 0;
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

	/**
	 * Clears the set and reduces the size of the backing arrays to be the specified capacity, if they are larger. The reduction
	 * is done by allocating new arrays, though for large arrays this can be faster than clearing the existing array.
	 */
	public void clear(int maximumCapacity) {
		if (capacity <= maximumCapacity) {
			clear();
			return;
		}
		size = 0;
		resize(maximumCapacity);
	}

	/**
	 * Clears the set, leaving the backing arrays at the current capacity. When the capacity is high and the population is low,
	 * iteration can be unnecessarily slow. {@link #clear(int)} can be used to reduce the capacity.
	 */
	@Override
	public void clear() {
		if (size == 0) return;
		for (int i = capacity + stashSize; i-- > 0; )
			keyTable[i] = null;
		size = 0;
		stashSize = 0;
	}

	@Override
	@Contract(value = "null -> false")
	public boolean contains(@Nullable Object key) {
		if (key == null || size == 0) return false;

		int hashCode = key.hashCode();
		int index = hashCode & mask;
		if (!key.equals(keyTable[index])) {
			index = hash2(hashCode);
			if (!key.equals(keyTable[index])) {
				index = hash3(hashCode);
				if (!key.equals(keyTable[index])) return getKeyStash(key) != null;
			}
		}
		return true;
	}

	/** @return May be null. */
	@Contract(value = "null -> null")
	public E get(@Nullable E key) {
		if (key == null) return null;

		int hashCode = key.hashCode();
		int index = hashCode & mask;
		E found = keyTable[index];
		if (!key.equals(found)) {
			index = hash2(hashCode);
			found = keyTable[index];
			if (!key.equals(found)) {
				index = hash3(hashCode);
				found = keyTable[index];
				if (!key.equals(found)) return getKeyStash(key);
			}
		}
		return found;
	}

	protected E getKeyStash(Object key) {
		for (int i = capacity, n = i + stashSize; i < n; i++)
			if (key.equals(keyTable[i])) return keyTable[i];
		return null;
	}

	@Override
	public E first() {
		for (int i = 0, n = capacity + stashSize; i < n; i++)
			if (keyTable[i] != null) return keyTable[i];
		throw new IllegalStateException("ObjectSet is empty.");
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

	@SuppressWarnings("unchecked")
	protected void resize(int newSize) {
		int oldEndIndex = capacity + stashSize;

		capacity = newSize;
		threshold = (int) (newSize * loadFactor);
		mask = newSize - 1;
		hashShift = 31 - Integer.numberOfTrailingZeros(newSize);
		stashCapacity = Math.max(3, (int) Math.ceil(Math.log(newSize)) * 2);
		pushIterations = Mathm.clamp(newSize, 8, (int) Math.sqrt(newSize) / 8);

		E[] oldKeyTable = keyTable;

		keyTable = (E[]) Array.newInstance(componentType, newSize + stashCapacity);

		int oldSize = size;
		size = 0;
		stashSize = 0;
		if (oldSize > 0) {
			for (int i = 0; i < oldEndIndex; i++) {
				E key = oldKeyTable[i];
				if (key != null) addResize(key);
			}
		}
	}

	protected int hash2(int h) {
		h *= PRIME2;
		return (h ^ h >>> hashShift) & mask;
	}

	protected int hash3(int h) {
		h *= PRIME3;
		return (h ^ h >>> hashShift) & mask;
	}

	@Override
	public int hashCode() {
		int h = 0;
		for (int i = 0, n = capacity + stashSize; i < n; i++)
			if (keyTable[i] != null) h += keyTable[i].hashCode();
		return h;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CollectionObjectSet<?> other)) return false;
		if (other.size != size) return false;
		for (int i = 0, n = capacity + stashSize; i < n; i++)
			if (keyTable[i] != null && !other.contains(keyTable[i])) return false;
		return true;
	}

	@Override
	public String toString() {
		return '{' + toString(", ") + '}';
	}

	public String toString(String separator) {
		if (size == 0) return "";
		StringBuilder buffer = new StringBuilder(32);
		int i = keyTable.length;
		while (i-- > 0) {
			E key = keyTable[i];
			if (key == null) continue;
			buffer.append(key);
			break;
		}
		while (i-- > 0) {
			E key = keyTable[i];
			if (key == null) continue;
			buffer.append(separator);
			buffer.append(key);
		}
		return buffer.toString();
	}

	/**
	 * Returns an iterator for the keys in the set. Remove is supported. Note that the same iterator instance is returned each
	 * time this method is called. Use the {@link Iter} constructor for nested or multithreaded iteration.
	 */
	@Override
	public @NotNull Iter iterator() {
		if (iterator1 == null) iterator1 = new Iter();

		if (iterator1.done) {
			iterator1.reset();
			return iterator1;
		}

		if (iterator2 == null) iterator2 = new Iter();

		if (iterator2.done) {
			iterator2.reset();
			return iterator2;
		}
		// no finished iterators
		return new Iter();
	}

	public class Iter implements Iterable<E>, Iterator<E> {
		public boolean hasNext;

		public int nextIndex, currentIndex;
		public boolean done;

		public Iter() {
			reset();
			done = true;
		}

		public void reset() {
			currentIndex = -1;
			nextIndex = -1;
			findNextIndex();
			done = false;
		}

		protected void findNextIndex() {
			hasNext = false;
			for (int n = capacity + stashSize; ++nextIndex < n; ) {
				if (keyTable[nextIndex] != null) {
					hasNext = true;
					break;
				}
			}
		}

		@Override
		public void remove() {
			if (currentIndex < 0) throw new IllegalStateException("next must be called before remove.");
			if (currentIndex >= capacity) {
				removeStashIndex(currentIndex);
				nextIndex = currentIndex - 1;
				findNextIndex();
			} else {
				keyTable[currentIndex] = null;
			}
			currentIndex = -1;
			size--;
		}

		@Override
		public boolean hasNext() {
			if (!hasNext) {
				done = true;
			}
			return hasNext;
		}

		@Override
		public E next() {
			if (!hasNext) throw new NoSuchElementException();
			E key = keyTable[nextIndex];
			currentIndex = nextIndex;
			findNextIndex();
			return key;
		}

		@Override
		public @NotNull Iter iterator() {
			return this;
		}

		/** Adds the remaining values to the array. */
		public CollectionList<E> toList(CollectionList<E> array) {
			while (hasNext)
				array.add(next());
			return array;
		}

		/** Returns a new array containing the remaining values. */
		public CollectionList<E> toList() {
			return toList(new CollectionList<>(true, size, componentType));
		}
	}
}
