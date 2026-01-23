package endfield.io;

import arc.func.Prov;
import arc.math.geom.Point2;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.TechTree.TechNode;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Team;
import mindustry.gen.EntityMapping;
import mindustry.gen.Itemsc;
import mindustry.gen.Unit;
import mindustry.io.TypeIO;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;

public final class TypeIO2 {
	/** Don't let anyone instantiate this class. */
	private TypeIO2() {}

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
		return Vars.content.<UnlockableContent>getByID(ContentType.all[read.i()], read.s()).techNode;
	}

	public static void writeItemConsumers(Writes writes, Itemsc[] itemscs) {
		writes.i(itemscs.length);
		for (Itemsc itemsc : itemscs) {
			TypeIO.writeEntity(writes, itemsc);
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

	public static <T extends Enum<T>> void writeEnums(Writes write, T[] array) {
		write.i(array.length);
		for (T t : array) write.b(t.ordinal());
	}

	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> T[] readEnums(Reads read, FromOrdinal<T> prov, Class<T> type) {
		int size = read.i();
		T[] out = (T[]) Array.newInstance(type, size);

		for (int i = 0; i < size; i++) out[i] = prov.get(read.b());
		return out;
	}

	public static void write(@Nullable Payload payload, Writes write) {
		if (payload == null) {
			write.bool(false);
		} else {
			write.bool(true);
			payload.write(write);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Payload> @Nullable T read(Reads read) {
		boolean exists = read.bool();
		if (!exists) return null;

		byte type = read.b();
		if (type == Payload.payloadBlock) {
			Block block = Vars.content.block(read.s());
			BuildPayload payload = new BuildPayload(block, Team.derelict);
			byte version = read.b();
			payload.build.readAll(read, version);
			payload.build.tile = Vars.emptyTile;
			return (T) payload;
		} else if (type == Payload.payloadUnit) {
			byte id = read.b();
			Prov<?> map = EntityMapping.map(id);
			if (map == null) throw new RuntimeException("No type with ID " + id + " found.");
			Unit unit = (Unit) map.get();
			unit.read(read);
			return (T) new UnitPayload(unit);
		}
		throw new IllegalArgumentException("Unknown payload type: " + type);
	}

	@FunctionalInterface
	public interface FromOrdinal<T extends Enum<T>> {
		T get(int ordinal);
	}
}
