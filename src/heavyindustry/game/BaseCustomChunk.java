package heavyindustry.game;

import mindustry.io.SaveFileReader.CustomChunk;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface BaseCustomChunk extends CustomChunk {
	byte version();

	@Override
	default void write(DataOutput stream) throws IOException {
		stream.writeByte(version());
	}

	@Override
	default void read(DataInput stream) throws IOException {
		byte version = stream.readByte();
		read(stream, version);
	}

	void read(DataInput stream, byte version) throws IOException;
}
