package heavyindustry.util.pair;

import arc.func.Cons2;
import arc.func.Func;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.io.TypeIO;
import mindustry.type.Item;
import mindustry.type.ItemStack;

public class Pairs<T> {
	public T item;
	public int value;

	public Pairs() {}

	public Pairs(T item, int value) {
		this.item = item;
		this.value = value;
	}

	public ItemStack convert() {
		return item instanceof Item i ? new ItemStack(i, value) : new ItemStack();
	}

	@SuppressWarnings("unchecked")
	public static <T> Pairs<T>[] with(Object... items) {
		Pairs<T>[] stacks = new Pairs[items.length / 2];
		for (int i = 0; i < items.length; i += 2) {
			stacks[i / 2] = new Pairs<>((T) items[i], ((Number) items[i + 1]).intValue());
		}
		return stacks;
	}

	@SuppressWarnings("unchecked")
	public static <T> Seq<Pairs<T>> seqWith(Object... items) {
		Seq<Pairs<T>> stacks = new Seq<>(items.length / 2);
		for (int i = 0; i < items.length; i += 2) {
			stacks.add(new Pairs<>((T) items[i], ((Number) items[i + 1]).intValue()));
		}
		return stacks;
	}

	public int getValue() {
		return value;
	}

	public Pairs<T> setValue(int value) {
		this.value = value;

		return this;
	}

	public Pairs(T item) {
		this.item = item;
	}

	public T getItem() {
		return item;
	}

	public void setItem(T item) {
		this.item = item;
	}

	public boolean has(Pairs<T> other) {
		return other.value >= value;
	}

	public boolean below(Pairs<T> other) {
		return other.value < value;
	}

	public boolean positive() {
		return value > 0;
	}

	public boolean nonNegative() {
		return value >= 0;
	}

	public Pairs<T> sum(Pairs<T> other) {
		return setValue(value + other.value);
	}

	public Pairs<T> sub(Pairs<T> other) {
		return setValue(value - other.value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Pairs<?> that)) return false;

		if (value != that.value) return false;
		return item.equals(that.item);
	}

	@Override
	public int hashCode() {
		int result = item.hashCode();
		result = 31 * result + value;
		return result;
	}

	public void write(Writes writes) {
		TypeIO.writeObject(writes, item);
		writes.i(value);
	}

	@SuppressWarnings("unchecked")
	public void read(Reads reads) {
		item = (T) TypeIO.readObject(reads);
		value = reads.i();
	}

	@SuppressWarnings("unchecked")
	public static <E> Pairs<E> readToNew(Reads reads) {
		return new Pairs<>((E) TypeIO.readObject(reads), reads.i());
	}

	public static void writeArr(Pairs<?>[] pairs, Writes writes) {
		writes.i(pairs.length);
		for (Pairs<?> pair : pairs) {
			pair.write(writes);
		}
	}

	public static <T> void writeArr(Seq<Pairs<T>> pairs, Writes writes) {
		writes.i(pairs.size);
		for (Pairs<T> pair : pairs) {
			pair.write(writes);
		}
	}

	public static <T> void writeArr(Seq<Pairs<T>> pairs, Writes writes, Cons2<Writes, Pairs<T>> writer) {
		writes.i(pairs.size);

		for (Pairs<T> pair : pairs) {
			writer.get(writes, pair);
		}
	}

	public static <T> Seq<Pairs<T>> readSeq(Reads reads) {
		int length = reads.i();
		Seq<Pairs<T>> out = new Seq<>(length);
		for (int i = 0; i < length; i++) out.add(readToNew(reads));

		return out;
	}

	public static <T> Seq<Pairs<T>> readSeq(Reads reads, Func<Reads, Pairs<T>> reader) {
		int length = reads.i();
		Seq<Pairs<T>> out = new Seq<>(length);
		for (int i = 0; i < length; i++) out.add(reader.get(reads));

		return out;
	}

	@SuppressWarnings("unchecked")
	public static <E> Pairs<E>[] readArr(Reads reads) {
		int length = reads.i();
		Pairs<E>[] out = new Pairs[length];
		for (int i = 0; i < length; i++) out[i] = readToNew(reads);

		return out;
	}

	@Override
	public String toString() {
		return "[" + item + ": " + value + ']';
	}
}
