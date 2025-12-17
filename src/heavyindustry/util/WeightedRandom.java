package heavyindustry.util;

import arc.math.Mathf;
import arc.struct.FloatSeq;
import org.jetbrains.annotations.Contract;

public class WeightedRandom<T> {
	protected float lastValue = 0f;
	protected CollectionList<T> items;
	protected FloatSeq weights = new FloatSeq();

	public WeightedRandom(Class<?> componentType) {
		items = new CollectionList<>(componentType);
	}

	public void add(T t, float weight) {
		if (weight <= 0f) return;
		items.add(t);
		weights.add(lastValue + weight);
		lastValue += weight;
	}

	@Contract(pure = true)
	public T get() {
		float rnd = Mathf.rand.nextFloat() * lastValue;
		int size = items.size;
		for (int i = 0; i < size; i++) {
			float lw = i == 0 ? -1f : weights.items[i - 1];
			float w = weights.items[i];
			if (rnd > lw && rnd <= w) {
				return items.items[i];
			}
		}
		return items.random();
	}

	public void clear() {
		items.clear();
		weights.clear();
		lastValue = 0f;
	}
}
