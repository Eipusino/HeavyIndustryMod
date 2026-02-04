package endfield.net;

import endfield.entities.Entitys2;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.net.Net;
import mindustry.net.NetConnection;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.UnitTetherBlock;

/**
 * Handles various modded client-server synchronizations.
 *
 * @since 1.0.8
 */
public final class Call2 {
	/** Don't let anyone instantiate this class. */
	private Call2() {}

	public static void init() {
		Net.registerPacket(DroneSpawnedCallPacket::new);
		Net.registerPacket(ReleaseShieldWallBuildSyncPacket::new);
		Net.registerPacket(RemoveStackPacket::new);
		Net.registerPacket(UnitAnnihilateCallPacket::new);
	}

	public static void releaseShieldWallBuildSync(Tile tile, float damage) {
		if (Vars.net.server()) {
			ReleaseShieldWallBuildSyncPacket packet = new ReleaseShieldWallBuildSyncPacket();
			packet.tile = tile;
			packet.damage = damage;
			Vars.net.send(packet, true);
		}
	}

	public static void minerPointDroneSpawned(Tile tile, int id) {
		if ((Vars.net.server() || !Vars.net.active()) && tile != null && tile.build instanceof UnitTetherBlock build) {
			build.spawned(id);
		}

		if (Vars.net.server()) {
			DroneSpawnedCallPacket packet = new DroneSpawnedCallPacket();
			packet.tile = tile;
			packet.id = id;
			Vars.net.send(packet, true);
		}
	}

	public static void annihilateUnit(int uid) {
		if (Vars.net.server() || !Vars.net.active()) {
			Entitys2.unitAnnihilate(uid);
		}

		if (Vars.net.server()) {
			UnitAnnihilateCallPacket packet = new UnitAnnihilateCallPacket();
			packet.uid = uid;
			Vars.net.send(packet, true);
		}
	}

	public static void removeStack(Building build, Item item, int amount) {
		buildRemoveStack(build, item, amount);
		if (Vars.net.server() || Vars.net.client()) {
			RemoveStackPacket packet = new RemoveStackPacket();

			packet.build = build;
			packet.item = item;
			packet.amount = amount;
			Vars.net.send(packet, true);
		}
	}

	public static void removeStack(NetConnection con, Building build, Item item, int amount) {
		if (Vars.net.server() || Vars.net.client()) {
			RemoveStackPacket packet = new RemoveStackPacket();

			packet.build = build;
			packet.item = item;
			packet.amount = amount;
			Vars.net.sendExcept(con, packet, true);
		}
	}

	public static void buildRemoveStack(Building build, Item item, int amount) {
		if (build != null && item != null && amount > 0) {
			build.removeStack(item, amount);
		}
	}
}
