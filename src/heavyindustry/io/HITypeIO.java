package heavyindustry.io;

import arc.struct.*;
import arc.util.io.*;
import heavyindustry.input.InputAggregator.*;

import java.io.*;

public class HITypeIO {
	/** Don't let anyone instantiate this class. */
	private HITypeIO() {}

	public static void writeStrings(Writes write, Seq<String> array) {
		write.i(array.size);
		array.each(write::str);
	}

	public static Seq<String> readStrings(Reads read) {
		int size = read.i();
		Seq<String> out = new Seq<>(size);

		for (int i = 0; i < size; i++) out.add(read.str());
		return out;
	}

	public static <T extends Enum<T>> void writeEnums(Writes write, Seq<T> array) {
		write.i(array.size);
		array.each(e -> write.b(e.ordinal()));
	}

	public static <T extends Enum<T>> Seq<T> readEnums(Reads read, FromOrdinal<T> prov) {
		int size = read.i();
		Seq<T> out = new Seq<>(size);

		for (int i = 0; i < size; i++) out.add(prov.get(read.b()));
		return out;
	}

	public static void writeTaps(Writes write, Seq<TapResult> array) {
		writeEnums(write, array);
	}

	public static Seq<TapResult> readTaps(Reads read) {
		return readEnums(read, ordinal -> TapResult.all[ordinal]);
	}

	public static void writeObject(Writes write, Object object) {
		try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); ObjectOutputStream out = new ObjectOutputStream(bout)) {
			out.writeObject(object);
			byte[] bytes = bout.toByteArray();
			write.i(bytes.length);
			write.b(bytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T readObject(Reads read, Class<T> type) {
		int length = read.i();
		byte[] bytes = read.b(length);
		try (ByteArrayInputStream bin = new ByteArrayInputStream(bytes); ObjectInputStream in = new ObjectInputStream(bin)) {
			Object object = in.readObject();
			return type.isAssignableFrom(object.getClass()) ? type.cast(object) : null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public interface FromOrdinal<T extends Enum<T>> {
		T get(int ordinal);
	}
}
