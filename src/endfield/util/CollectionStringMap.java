package endfield.util;

import arc.util.Strings;

public class CollectionStringMap extends CollectionObjectMap<String, String> {
	public CollectionStringMap() {
		super(String.class, String.class);
	}

	public CollectionStringMap(int initialCapacity) {
		super(String.class, String.class, initialCapacity);
	}

	public CollectionStringMap(int initialCapacity, float loadFactor) {
		super(String.class, String.class, initialCapacity, loadFactor);
	}

	public CollectionStringMap(CollectionStringMap map) {
		super(map);
	}

	public static CollectionStringMap of(String... values) {
		CollectionStringMap map = new CollectionStringMap();

		for (int i = 0; i < values.length / 2; i++) {
			map.put(values[i * 2], values[i * 2 + 1]);
		}

		return map;
	}

	public boolean getBool(String name) {
		return getDefault(name, "").equals("true");
	}

	public int getInt(String name) {
		return getInt(name, 0);
	}

	public float getFloat(String name) {
		return getFloat(name, 0f);
	}

	public long getLong(String name) {
		return getLong(name, 0l);
	}

	public int getInt(String name, int def) {
		return containsKey(name) ? Strings.parseInt(get(name), def) : def;
	}

	public float getFloat(String name, float def) {
		return containsKey(name) ? Strings.parseFloat(get(name), def) : def;
	}

	public long getLong(String name, long def) {
		return containsKey(name) ? Strings.parseLong(get(name), def) : def;
	}
}
