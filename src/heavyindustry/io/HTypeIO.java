package heavyindustry.io;

import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.input.InputAggregator.TapResult;
import mindustry.Vars;
import mindustry.content.TechTree.TechNode;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static mindustry.Vars.content;

public final class HTypeIO {
	/** Don't let anyone instantiate this class. */
	private HTypeIO() {}

	public static void writeContent(Writes write, Content map) {
		write.i(map.getContentType().ordinal());
		write.i(map.id);
	}

	public static <T extends Content> T readContent(Reads read) {
		return Vars.content.getByID(ContentType.all[read.i()], read.i());
	}

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
		write.b(map.content.getContentType().ordinal());
		write.s(map.content.id);
	}

	public static TechNode readTechNode(Reads read) {
		return content.<UnlockableContent>getByID(ContentType.all[read.b()], read.s()).techNode;
	}

	public static void writeStrings(Writes write, Seq<String> array) {
		write.i(array.size);
		for (String s : array) write.str(s);
	}

	public static Seq<String> readStrings(Reads read) {
		int size = read.i();
		Seq<String> out = new Seq<>(size);

		for (int i = 0; i < size; i++) out.add(read.str());
		return out;
	}

	public static <T extends Enum<T>> void writeEnums(Writes write, Seq<T> array) {
		write.i(array.size);
		for (T t : array) write.b(t.ordinal());
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
