package heavyindustry.ui;

import arc.func.Cons;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.type.Item;

import java.util.Iterator;

import static mindustry.Vars.content;

public class MultiItemData implements Iterable<Item>, Eachable<Item> {
	private final Seq<Item> items = new Seq<>(Item.class);

	public int length() {
		return items.size;
	}

	public IntSet asIntSet() {
		IntSet seq = new IntSet();
		items.each(item -> seq.add(item.id));
		return seq;
	}

	public void write(Writes writes) {
		writes.i(items.size);
		items.each(item -> writes.str(item.name));
	}

	public void read(Reads reads) {
		int length = reads.i();
		for (int i = 0; i < length; i++) {
			toggle(reads.str());
		}
	}

	public int[] config() {
		int[] config = new int[items.size];
		for (int i = 0; i < config.length; i++) {
			config[i] = items.get(i).id;
		}
		return config;
	}

	public boolean isToggled(Item item) {
		return items.contains(item);
	}

	public boolean isToggled(String name) {
		return isToggled(content.item(name));
	}

	public boolean isToggled(int id) {
		return isToggled(content.item(id));
	}

	public void toggle(Item item) {
		if (item != null) {
			if (items.contains(item)) {
				items.remove(item);
			} else {
				items.add(item);
			}
		}
	}

	public void toggle(String name) {
		toggle(content.item(name));
	}

	public void toggle(int id) {
		toggle(content.item(id));
	}

	public void clear() {
		items.clear();
	}

	public void enable(Item item) {
		if (!items.contains(item)) {
			items.add(item);
		}
	}

	public void disable(Item item) {
		if (items.contains(item)) {
			items.remove(item);
		}
	}

	public Item getItem(int index) {
		return items.get(index);
	}

	@Override
	public void each(Cons<? super Item> cons) {
		items.each(cons);
	}

	@Override
	public Iterator<Item> iterator() {
		return items.iterator();
	}
}
