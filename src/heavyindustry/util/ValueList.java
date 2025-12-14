package heavyindustry.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ValueList extends ValueMap implements Iterable<ValueMap> {
	CollectionList<ValueMap> list = new CollectionList<>(ValueMap.class);

	public ValueMap getMap(int index) {
		return list.get(index);
	}

	public void add(ValueMap f) {
		list.add(f);
	}

	public void addFloat(float f) {
		list.add(new ValueMap(f));
	}

	public float getFloat(int t) {
		return list.get(t).floatValue;
	}

	public void addInt(int f) {
		list.add(new ValueMap(f));
	}

	public float getInt(int t) {
		return list.get(t).intValue;
	}

	public void add(Object f) {
		list.add(new ValueMap(f));
	}

	@SuppressWarnings("unchecked")
	public <T> T get(int t) {
		return (T) list.get(t).value;
	}

	public void set(int index, ValueMap f) {
		list.set(index, f);
	}

	@Override
	public @NotNull Iterator<ValueMap> iterator() {
		return list.iterator();
	}

	public int length() {
		return list.size();
	}

	@Override
	public @NotNull ValueList copy() {
		try {
			ValueList out = (ValueList) super.clone();
			out.map = map.copy();
			out.list = list.copy();
			return out;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
