package endfield.util.holder;

public class IntBoolHolder implements Cloneable, Comparable<IntBoolHolder> {
	public int key;
	public boolean value;

	public IntBoolHolder() {}

	public IntBoolHolder(int k, boolean v) {
		key = k;
		value = v;
	}

	public IntBoolHolder set(int k, boolean v) {
		key = k;
		value = v;

		return this;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof IntBoolHolder that && key == that.key && value == that.value;
	}

	@Override
	public int hashCode() {
		return key ^ Boolean.hashCode(value);
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	public IntBoolHolder copy() {
		try {
			return (IntBoolHolder) super.clone();
		} catch (CloneNotSupportedException awful) {
			return new IntBoolHolder(key, value);
		}
	}

	@Override
	public int compareTo(IntBoolHolder o) {
		return Integer.compare(key, o.key);
	}
}
