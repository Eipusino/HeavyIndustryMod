package endfield.net;

import arc.util.io.Reads;
import arc.util.io.Writes;
import endfield.world.blocks.defense.ReleaseShieldWall;
import mindustry.io.TypeIO;
import mindustry.net.Packet;
import mindustry.world.Tile;

public class ReleaseShieldWallBuildSyncPacket extends Packet {
	private byte[] data;

	public Tile tile;
	public float damage;

	@Override
	public void write(Writes write) {
		TypeIO.writeTile(write, tile);
		write.f(damage);
	}

	@Override
	public void read(Reads read, int length) {
		data = read.b(length);
	}

	@Override
	public void handled() {
		BAIS.setBytes(data);
		tile = TypeIO.readTile(READ);
		damage = READ.f();
	}

	@Override
	public void handleClient() {
		ReleaseShieldWall.setDamage(tile, damage);
	}
}
