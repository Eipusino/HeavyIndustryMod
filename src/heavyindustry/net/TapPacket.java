package heavyindustry.net;

import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.input.InputAggregator.TapResult;
import heavyindustry.io.HTypeIO;
import mindustry.gen.Player;
import mindustry.io.TypeIO;
import mindustry.net.NetConnection;
import mindustry.net.Packet;

import static heavyindustry.HVars.inputAggregator;
import static mindustry.Vars.net;

public class TapPacket extends Packet {
	public @Nullable Player player;
	public float x, y;

	public Seq<String> targets;
	public @Nullable Seq<TapResult> results;

	@Override
	public void write(Writes write) {
		if (net.server()) {
			TypeIO.writeEntity(write, player);
			HTypeIO.writeTaps(write, results);
		}

		write.f(x);
		write.f(y);
		HTypeIO.writeStrings(write, targets);
	}

	@Override
	public void read(Reads read, int length) {
		BAIS.setBytes(read.b(length), 0, length);
	}

	@Override
	public void handled() {
		if (net.client()) {
			player = TypeIO.readEntity(READ);
			results = HTypeIO.readTaps(READ);
		}

		x = READ.f();
		y = READ.f();
		targets = HTypeIO.readStrings(READ);
	}

	@Override
	public void handleServer(NetConnection con) {
		// On servers, handle the try packet sent from a client and send the result packet to all connected clients.
		if (con.player == null || con.kicked) return;
		HCall.tap(con.player, x, y, targets);
	}

	@Override
	public void handleClient() {
		// On clients, handle the result packet from the server and don't send anything else.
		inputAggregator.tap(player, x, y, targets, results);
	}
}
