package endfield.net;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.io.TypeIO;
import mindustry.net.NetConnection;
import mindustry.net.Packet;
import mindustry.type.Item;

public class RemoveStackPacket extends Packet {
	private byte[] data;

	public Building build;
	public Item item;
	public int amount;

	public RemoveStackPacket() {
		data = NODATA;
	}

	@Override
	public void write(Writes write) {
		TypeIO.writeBuilding(write, build);
		TypeIO.writeItem(write, item);
		write.i(amount);
	}

	@Override
	public void read(Reads read, int length) {
		data = read.b(length);
	}

	@Override
	public void handled() {
		BAIS.setBytes(data);

		build = TypeIO.readBuilding(READ);
		item = TypeIO.readItem(READ);
		amount = READ.i();
	}

	@Override
	public void handleServer(NetConnection con) {
		if (con.player != null && !con.kicked) {
			build.removeStack(item, amount);
			Call2.removeStack(con, build, item, amount);
		}
	}

	@Override
	public void handleClient() {
		build.removeStack(item, amount);
	}
}
