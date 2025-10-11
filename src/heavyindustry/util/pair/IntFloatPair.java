package heavyindustry.util.pair;

public class IntFloatPair implements Cloneable {
	public int key;
	public float value;

	public IntFloatPair() {}

	public IntFloatPair(int k, float v) {
		key = k;
		value = v;
	}

	public IntFloatPair set(int k, float v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public int hashCode() {
		return key ^ Float.floatToIntBits(value);
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	public IntFloatPair copy() {
		try {
			return (IntFloatPair) super.clone();
		} catch (CloneNotSupportedException e) {
			return new IntFloatPair(key, value);
		}
	}
}
