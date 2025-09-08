package heavyindustry.util;

import arc.func.Func;
import arc.func.Func2;

import java.util.ConcurrentModificationException;
import java.util.Map;

public interface BaseMap<K, V> extends Map<K, V> {
	default V asComputeIfAbsent(K key, Func<? super K, ? extends V> mappingFunction) {
		V v;
		if ((v = get(key)) == null) {
			V newValue;
			if ((newValue = mappingFunction.get(key)) != null) {
				put(key, newValue);
				return newValue;
			}
		}

		return v;
	}

	default void asReplaceAll(Func2<? super K, ? super V, ? extends V> function) {
		for (Map.Entry<K, V> entry : entrySet()) {
			K k;
			V v;
			try {
				k = entry.getKey();
				v = entry.getValue();
			} catch (IllegalStateException ise) {
				// this usually means the entry is no longer in the map.
				throw new ConcurrentModificationException(ise);
			}

			// ise thrown from function is not a cme.
			v = function.get(k, v);

			try {
				entry.setValue(v);
			} catch (IllegalStateException ise) {
				// this usually means the entry is no longer in the map.
				throw new ConcurrentModificationException(ise);
			}
		}
	}

	default V asComputeIfPresent(K key, Func2<? super K, ? super V, ? extends V> remappingFunction) {
		V oldValue;
		if ((oldValue = get(key)) != null) {
			V newValue = remappingFunction.get(key, oldValue);
			if (newValue != null) {
				put(key, newValue);
				return newValue;
			} else {
				remove(key);
				return null;
			}
		} else {
			return null;
		}
	}

	default V asCompute(K key, Func2<? super K, ? super V, ? extends V> remappingFunction) {
		V oldValue = get(key);

		V newValue = remappingFunction.get(key, oldValue);
		if (newValue == null) {
			// delete mapping
			if (oldValue != null || containsKey(key)) {
				// something to remove
				remove(key);
				return null;
			} else {
				// nothing to do. Leave things as they were.
				return null;
			}
		} else {
			// add or replace old mapping
			put(key, newValue);
			return newValue;
		}
	}

	default V asMerge(K key, V value, Func2<? super V, ? super V, ? extends V> remappingFunction) {
		V oldValue = get(key);
		V newValue = (oldValue == null) ? value :
				remappingFunction.get(oldValue, value);
		if (newValue == null) {
			remove(key);
		} else {
			put(key, newValue);
		}
		return newValue;
	}
}
