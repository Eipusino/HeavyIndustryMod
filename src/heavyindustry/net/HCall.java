package heavyindustry.net;

import arc.struct.Seq;
import heavyindustry.entities.HEntity;
import heavyindustry.input.InputAggregator.TapResult;
import heavyindustry.world.blocks.production.UnitMinerPoint.UnitMinerPointBuild;
import mindustry.Vars;
import mindustry.gen.Player;
import mindustry.net.Net;
import mindustry.world.Tile;

import static heavyindustry.HVars.inputAggregator;

/**
 * Handles various modded client-server synchronizations.
 *
 * @since 1.0.8
 */
public final class HCall {
	/** Don't let anyone instantiate this class. */
	private HCall() {}

	public static void init() {
		Net.registerPacket(TapPacket::new);
	}

	/**
	 * Client-server synchronization for {@linkplain heavyindustry.input.InputAggregator#tryTap(Player, float, float, Seq) player taps}.
	 * This method is called on either clients or self-hosts:
	 * <ol>
	 *     <li>On clients, send a try-tap packet to servers to retrieve which listeners accepts the tap.</li>
	 *     <li>On self-hosts or servers, handle the try-tap packet (sent by either a client on servers, or itself on
	 *     self-hosts) and forward the resulting listeners' acceptance of the tap to all connected clients.</li>
	 *     <li>Forwarded result packet is received on clients and is handled accordingly, without sending any more packets.</li>
	 * </ol>
	 *
	 * @param player  The player who commited the tap. Retrieved from {@link mindustry.net.NetConnection#player} on servers.
	 * @param x       X position of the tap.
	 * @param y       Y position of the tap.
	 * @param targets Which listeners to tap against; may be inspected with {@link heavyindustry.input.InputAggregator#each(arc.func.Cons)}.
	 */
	public static void tap(Player player, float x, float y, Seq<String> targets) {
		// If this is a local game, just handle it immediately.
		if (!Vars.net.active()) {
			Seq<TapResult> accepted = inputAggregator.tryTap(player, x, y, targets);
			inputAggregator.tap(player, x, y, targets, accepted);
		} else {
			// Otherwise, send a try packet to the server from a client, and result packet from the server to connected clients.
			TapPacket packet = new TapPacket();
			packet.x = x;
			packet.y = y;
			packet.targets = targets;

			// If this is a server, handle the request (either from a client on headless servers, or the host itself on
			// self-hosted games), then send the result packet to all connected clients.
			if (Vars.net.server()) {
				packet.player = player;
				Seq<TapResult> accepted = packet.results = inputAggregator.tryTap(player, x, y, targets);
				inputAggregator.tap(player, x, y, targets, accepted);
			}

			Vars.net.send(packet, true);
		}
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
		if ((Vars.net.server() || !Vars.net.active()) && tile != null && tile.build instanceof UnitMinerPointBuild build) {
			build.spawned(id);
		}

		if (Vars.net.server()) {
			DroneSpawnedCallPacket packet = new DroneSpawnedCallPacket();
			packet.tile = tile;
			packet.id = id;
			Vars.net.send(packet, true);
		}
	}

	public static void removeUnit(int uid) {
		if (Vars.net.server() || !Vars.net.active()) {
			HEntity.unitRemove(uid);
		}

		if (Vars.net.server()) {
			UnitRemoveCallPacket packet = new UnitRemoveCallPacket();
			packet.uid = uid;
			Vars.net.send(packet, true);
		}
	}
}
