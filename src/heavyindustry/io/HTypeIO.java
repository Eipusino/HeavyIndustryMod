package heavyindustry.io;

import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.input.InputAggregator.TapResult;
import heavyindustry.util.Utils;
import mindustry.content.TechTree.TechNode;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Itemsc;
import mindustry.io.TypeIO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static mindustry.Vars.content;

public final class HTypeIO {
	/** Don't let anyone instantiate this class. */
	private HTypeIO() {}

	public static void writePoint2(Writes write, Point2 p) {
		write.i(p.x);
		write.i(p.y);
	}

	public static Point2 readPoint2(Reads read) {
		return new Point2(read.i(), read.i());
	}

	public static void writePoint2s(Writes write, Point2[] p) {
		write.b(p.length);
		for (Point2 point2 : p) {
			write.i(point2.pack());
		}
	}

	public static Point2[] readPoint2s(Reads read) {
		byte len = read.b();
		Point2[] out = new Point2[len];

		for (int i = 0; i < len; i++) out[i] = Point2.unpack(read.i());
		return out;
	}

	public static void writeTechNode(Writes write, TechNode map) {
		write.i(map.content.getContentType().ordinal());
		write.s(map.content.id);
	}

	public static TechNode readTechNode(Reads read) {
		return content.<UnlockableContent>getByID(ContentType.all[read.i()], read.s()).techNode;
	}

	public static void writeStrings(Writes write, Seq<String> array) {
		write.i(array.size);
		for (String s : array) write.str(s);
	}

	public static Seq<String> readStrings(Reads read) {
		int size = read.i();
		Seq<String> out = new Seq<>(true, size, String.class);

		for (int i = 0; i < size; i++) out.add(read.str());
		return out;
	}

	public static void writeItemConsumers(Writes writes, Itemsc[] itemscs) {
		writes.i(itemscs.length);
		for (Itemsc itemsc : itemscs) {
			TypeIO.writeObject(writes, itemsc);
		}
	}

	public static Itemsc[] readItemConsumers(Reads read) {
		int amount = read.i();
		Itemsc[] itemscs = new Itemsc[amount];
		for (int i = 0; i < itemscs.length; i++) {
			itemscs[i] = TypeIO.readEntity(read);
		}
		return itemscs;
	}

	public static <T extends Enum<T>> void writeEnums(Writes write, Seq<T> array) {
		write.i(array.size);
		for (T t : array) write.b(t.ordinal());
	}

	public static <T extends Enum<T>> Seq<T> readEnums(Reads read, FromOrdinal<T> prov, Class<T> type) {
		int size = read.i();
		Seq<T> out = new Seq<>(true, size, type);

		for (int i = 0; i < size; i++) out.add(prov.get(read.b()));
		return out;
	}

	public static void writeTaps(Writes write, Seq<TapResult> array) {
		writeEnums(write, array);
	}

	public static Seq<TapResult> readTaps(Reads read) {
		return readEnums(read, ordinal -> TapResult.all[ordinal], TapResult.class);
	}

	public static void writeObject(Writes write, Object object) {
		try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); ObjectOutputStream out = new ObjectOutputStream(bout)) {
			out.writeObject(object);
			byte[] bytes = bout.toByteArray();
			write.i(bytes.length);
			write.b(bytes);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T readObject(Reads read, Class<T> type) {
		int length = read.i();
		byte[] bytes = read.b(length);
		try (ByteArrayInputStream bin = new ByteArrayInputStream(bytes); ObjectInputStream in = new ObjectInputStream(bin)) {
			Object object = in.readObject();
			return Utils.cast(object, type, null);
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public interface FromOrdinal<T extends Enum<T>> {
		T get(int ordinal);
	}
}
