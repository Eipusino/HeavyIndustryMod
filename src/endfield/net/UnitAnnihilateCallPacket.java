package endfield.net;

import arc.util.io.Reads;
import arc.util.io.Writes;
import endfield.entities.Entitys2;
import mindustry.net.Packet;

public class UnitAnnihilateCallPacket extends Packet {
	private byte[] data;

	public int uid;

	public UnitAnnihilateCallPacket() {
		data = NODATA;
	}

	@Override
	public void write(Writes write) {
		write.i(uid);
	}

	@Override
	public void read(Reads read, int length) {
		data = read.b(length);
	}

	@Override
	public void handled() {
		BAIS.setBytes(data);
		uid = READ.i();
	}

	@Override
	public void handleClient() {
		Entitys2.unitAnnihilate(uid);
	}
}
