package endfield.net;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.io.TypeIO;
import mindustry.net.Packet;
import mindustry.ui.dialogs.BaseDialog;

public class LongInfoMessageCallPacket extends Packet {
	public String message;
	private byte[] data;

	public LongInfoMessageCallPacket() {
		data = NODATA;
	}

	@Override
	public void write(Writes write) {
		TypeIO.writeString(write, message);
	}

	@Override
	public void read(Reads read, int length) {
		data = read.b(length);
	}

	@Override
	public void handled() {
		BAIS.setBytes(data);
		message = TypeIO.readString(READ);
	}

	@Override
	public void handleClient() {
		new BaseDialog("@message") {{
			addCloseButton();
			cont.margin(6f);
			cont.pane(t -> {
				t.add(message).expandX().fillY();
			}).grow();
		}}.show();
	}
}
