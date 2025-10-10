package heavyindustry.util;

public class IntFloatPair {
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
}
