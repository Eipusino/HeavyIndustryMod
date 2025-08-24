package heavyindustry.game;

import mindustry.io.SaveFileReader.CustomChunk;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface BaseCustomChunk extends CustomChunk {
	short version();

	@Override
	default void write(DataOutput stream) throws IOException {
		stream.writeShort(version());
	}

	@Override
	default void read(DataInput stream) throws IOException {
		short version = stream.readShort();
		read(stream, version);
	}

	void read(DataInput stream, short version) throws IOException;
}
