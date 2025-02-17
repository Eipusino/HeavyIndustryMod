package heavyindustry.input;

import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import heavyindustry.input.InputAggregator.*;
import heavyindustry.io.*;
import heavyindustry.net.*;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.net.*;

import static heavyindustry.HVars.*;
import static mindustry.Vars.*;

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
		Reads read = READ;
		if (net.client()) {
			player = TypeIO.readEntity(read);
			results = HTypeIO.readTaps(read);
		}

		x = read.f();
		y = read.f();
		targets = HTypeIO.readStrings(read);
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
