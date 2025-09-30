package heavyindustry.net;

import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.world.blocks.production.UnitMinerPoint.UnitMinerPointBuild;
import mindustry.io.TypeIO;
import mindustry.net.Packet;
import mindustry.world.Tile;

public class DroneSpawnedCallPacket extends Packet {
	public Tile tile;
	public int id;
	private byte[] data;

	public DroneSpawnedCallPacket() {
		data = NODATA;
	}

	@Override
	public void write(Writes write) {
		TypeIO.writeTile(write, tile);
		write.i(id);
	}

	@Override
	public void read(Reads read, int length) {
		data = read.b(length);
	}

	@Override
	public void handled() {
		BAIS.setBytes(data);
		tile = TypeIO.readTile(READ);
		id = READ.i();
	}

	@Override
	public void handleClient() {
		if (tile != null && tile.build instanceof UnitMinerPointBuild miner) {
			miner.spawned(id);
		}
	}
}
