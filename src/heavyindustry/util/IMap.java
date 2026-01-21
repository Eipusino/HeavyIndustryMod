package heavyindustry.util;

import arc.func.Cons2;
import arc.func.Func;
import arc.func.Func2;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface IMap<K, V> extends Map<K, V> {
	Class<?> keyComponentType();

	Class<?> valueComponentType();

	default void each(Cons2<? super K, ? super V> cons) {
		for (Map.Entry<K, V> entry : entrySet()) {
			cons.get(entry.getKey(), entry.getValue());
		}
	}

	default void forEach(Cons2<? super K, ? super V> action) {
		each(action);
	}

	boolean containsValue(V value, boolean identity);

	default void replaceAll(@NotNull Func2<? super K, ? super V, ? extends V> func) {
		for (Map.Entry<K, V> entry : entrySet()) {
			K key = entry.getKey();
			V value = entry.getValue();

			value = func.get(key, value);

			entry.setValue(value);
		}
	}

	default V computeIfAbsent(K key, @NotNull Func<? super K, ? extends V> func) {
		V v;
		if ((v = get(key)) == null) {
			V newValue;
			if ((newValue = func.get(key)) != null) {
				put(key, newValue);
				return newValue;
			}
		}

		return v;
	}

	default V computeIfPresent(K key, @NotNull Func2<? super K, ? super V, ? extends V> func) {
		V oldValue;
		if ((oldValue = get(key)) != null) {
			V newValue = func.get(key, oldValue);
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

	default V compute(K key, @NotNull Func2<? super K, ? super V, ? extends V> func) {
		V oldValue = get(key);

		V newValue = func.get(key, oldValue);
		if (newValue == null) {
			if (oldValue != null || containsKey(key)) {
				remove(key);
			}
			return null;
		} else {
			put(key, newValue);
			return newValue;
		}
	}

	default V merge(K key, @NotNull V value, @NotNull Func2<? super V, ? super V, ? extends V> func) {
		V oldValue = get(key);
		V newValue = (oldValue == null) ? value :
				func.get(oldValue, value);
		if (newValue == null) {
			remove(key);
		} else {
			put(key, newValue);
		}
		return newValue;
	}
}
