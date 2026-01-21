package heavyindustry.util;

import arc.func.Cons;
import arc.func.Prov;
import arc.util.Eachable;
import heavyindustry.util.ref.ObjectHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ObjectTable<K, V> implements Iterable<ObjectHolder<K, V>>, Eachable<ObjectHolder<K, V>>, Cloneable {
	public final Class<?> keyComponentType;
	public final Class<?> valueComponentType;

	protected CollectionObjectMap<K, V> map12;
	protected CollectionObjectMap<V, K> map21;

	public ObjectTable(Class<?> keyType, Class<?> valueType) {
		keyComponentType = keyType;
		valueComponentType = valueType;

		map12 = new CollectionObjectMap<>(keyComponentType, valueComponentType);
		map21 = new CollectionObjectMap<>(valueComponentType, keyComponentType);
	}

	@SuppressWarnings("unchecked")
	public ObjectTable<K, V> copy() {
		try {
			ObjectTable<K, V> out = (ObjectTable<K, V>) super.clone();
			out.map12 = map12.copy();
			out.map21 = map21.copy();
			return out;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public int size() {
		return map12.size;
	}

	public K getKey(Object key) {
		return map21.get(key);
	}

	public K getKey(Object key, Prov<ObjectHolder<K, V>> supplier) {
		K res = getKey(key);
		if (res == null) {
			put(supplier.get());
			return getKey(key);
		}
		return res;
	}

	public V getValue(Object key) {
		return map12.get(key);
	}

	public V getValue(Object key, Prov<ObjectHolder<K, V>> supplier) {
		V res = getValue(key);
		if (res == null) {
			put(supplier.get());
			return getValue(key);
		}
		return res;
	}

	public void put(K a1, V a2) {
		if (map12.containsKey(a1)) return;
		map12.put(a1, a2);
		map21.put(a2, a1);
	}

	public void put(ObjectHolder<K, V> entry) {
		put(entry.key, entry.value);
	}

	public void removeByKey(K arg) {
		if (!map12.containsKey(arg)) return;
		map12.remove(arg);
		map21.remove(getValue(arg));
	}

	public void removeByValue(V arg) {
		if (!map21.containsKey(arg)) return;
		map21.remove(arg);
		map12.remove(getKey(arg));
	}

	public Object get(Object key, Prov<ObjectHolder<K, V>> supplier, boolean argT1) {
		return argT1 ? getValue(key, supplier) : getKey(key, supplier);
	}

	public boolean containsKey(K arg) {
		return map12.containsKey(arg);
	}

	public boolean containsValue(V arg) {
		return map21.containsKey(arg);
	}

	@Override
	public @NotNull Iterator<ObjectHolder<K, V>> iterator() {
		return map12.iterator();
	}

	@Override
	public void each(Cons<? super ObjectHolder<K, V>> cons) {
		map12.each(cons);
	}

	@Override
	public String toString() {
		return "ObjectTable{" + map12 +
				", size=" + size() +
				'}';
	}

	public void clear() {
		map12.clear();
		map21.clear();
	}
}
