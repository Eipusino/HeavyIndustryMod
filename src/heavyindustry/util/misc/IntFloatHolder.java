package heavyindustry.util.misc;

import org.jetbrains.annotations.NotNull;

/** @since 1.0.7 */
public class IntFloatHolder implements Cloneable, Comparable<IntFloatHolder> {
	public int key;
	public float value;

	public IntFloatHolder() {}

	public IntFloatHolder(int k, float v) {
		key = k;
		value = v;
	}

	public IntFloatHolder set(int k, float v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof IntFloatHolder that && key == that.key && value == that.value;
	}

	@Override
	public int hashCode() {
		return key ^ Float.floatToIntBits(value);
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	public IntFloatHolder copy() {
		try {
			return (IntFloatHolder) super.clone();
		} catch (CloneNotSupportedException awful) {
			return new IntFloatHolder(key, value);
		}
	}

	@Override
	public int compareTo(@NotNull IntFloatHolder o) {
		return Integer.compare(key, o.key);
	}
}
