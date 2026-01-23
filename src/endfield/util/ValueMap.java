package endfield.util;

public class ValueMap implements Cloneable {
	protected float floatValue;
	protected int intValue;
	protected Object value;
	protected CollectionObjectMap<String, ValueMap> map = new CollectionObjectMap<>(String.class, ValueMap.class);

	public ValueMap() {}

	public ValueMap(float v) {
		floatValue = v;
	}

	public ValueMap(int v) {
		intValue = v;
	}

	public ValueMap(Object v) {
		value = v;
	}

	public ValueMap copy() {
		try {
			ValueMap out = (ValueMap) super.clone();
			out.map = map.copy();
			return out;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public ValueMap put(String s, float t) {
		map.put(s, new ValueMap(t));
		return this;
	}

	public float getFloat(String s) {
		return map.get(s).floatValue;
	}

	public void put(String s, int t) {
		map.put(s, new ValueMap(t));
	}

	public int getInt(String s) {
		return map.get(s).intValue;
	}

	public void put(String s, Object t) {
		map.put(s, new ValueMap(t));
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String s) {
		return (T) map.get(s).value;
	}

	public ValueList getList(String s) {
		return (ValueList) map.get(s);
	}

	public void put(String s, ValueMap t) {
		map.put(s, t);
	}

	public ValueMap getValueMap(String s) {
		return map.get(s);
	}

	public boolean has(String name) {
		return map.containsKey(name);
	}
}
