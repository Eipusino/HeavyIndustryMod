package heavyindustry.util;

import java.util.Map;

public class ValueMap {
	float floatval;
	int intval;
	Object val;
	Map<String, ValueMap> map = new CollectionObjectMap<>(String.class, ValueMap.class);

	public ValueMap() {}

	public ValueMap(float value) {
		floatval = value;
	}

	public ValueMap(int value) {
		intval = value;
	}

	public ValueMap(Object value) {
		val = value;
	}

	public ValueMap put(String s, float t) {
		map.put(s, new ValueMap(t));
		return this;
	}

	public float getFloat(String s) {
		return map.get(s).floatval;
	}

	public void put(String s, int t) {
		map.put(s, new ValueMap(t));
	}

	public int getInt(String s) {
		return map.get(s).intval;
	}

	public void put(String s, Object t) {
		map.put(s, new ValueMap(t));
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String s) {
		return (T) map.get(s).val;
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
