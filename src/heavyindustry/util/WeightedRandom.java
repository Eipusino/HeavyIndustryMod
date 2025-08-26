package heavyindustry.util;

import arc.math.Mathf;
import arc.struct.FloatSeq;
import arc.struct.Seq;

public class WeightedRandom<T> {
	float lastValue = 0f;
	Seq<T> items;
	FloatSeq weights = new FloatSeq();

	public WeightedRandom() {
		items = new Seq<>();
	}

	public WeightedRandom(Class<T> componentType) {
		items = new Seq<>(componentType);
	}

	public void add(T t, float weight) {
		if (weight <= 0f) return;
		items.add(t);
		weights.add(lastValue + weight);
		lastValue += weight;
	}

	public T get() {
		double rnd = Mathf.rand.nextDouble() * lastValue;
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
