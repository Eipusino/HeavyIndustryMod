package endfield.util;

import arc.math.Mathf;
import arc.struct.FloatSeq;

public class WeightedRandom<E> {
	protected float lastValue = 0f;
	protected CollectionList<E> items;
	protected FloatSeq weights = new FloatSeq();

	public WeightedRandom(Class<?> componentType) {
		items = new CollectionList<>(componentType);
	}

	public void add(E value, float weight) {
		if (weight <= 0f) return;
		items.add(value);
		weights.add(lastValue + weight);
		lastValue += weight;
	}

	public E get() {
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
