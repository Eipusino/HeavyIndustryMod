
package heavyindustry.game;

import mindustry.Vars;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class WorldData implements BaseCustomChunk {
	public float eventReloadSpeed = -1;
	public boolean jumpGateUsesCoreItems = true;
	public boolean applyEventTriggers = false;

	@Override
	public void write(DataOutput stream) throws IOException {
		BaseCustomChunk.super.write(stream);

		stream.writeFloat(eventReloadSpeed);
		stream.writeBoolean(jumpGateUsesCoreItems);
		stream.writeBoolean(applyEventTriggers);
	}

	@Override
	public void read(DataInput stream, short version) throws IOException {
		eventReloadSpeed = stream.readFloat();

		if (version == 1) {
			jumpGateUsesCoreItems = stream.readBoolean();
			applyEventTriggers = stream.readBoolean();
		}

		afterRead();
	}

	public void afterRead() {
		if (Vars.headless && (Float.isNaN(eventReloadSpeed) || eventReloadSpeed > 5.55f)) {
			eventReloadSpeed = -1;
		}
	}

	@Override
	public short version() {
		return 1;
	}
}
