package endfield.net;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.io.TypeIO;
import mindustry.net.Packet;
import mindustry.world.Tile;
import mindustry.world.blocks.UnitTetherBlock;

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
		if (tile != null && tile.build instanceof UnitTetherBlock miner) {
			miner.spawned(id);
		}
	}
}
