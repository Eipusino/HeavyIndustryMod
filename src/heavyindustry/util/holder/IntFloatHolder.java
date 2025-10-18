package heavyindustry.util.holder;

public class IntFloatHolder implements Cloneable {
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
}
