package endfield.io;

import mindustry.io.SaveFileReader.CustomChunk;

import java.io.DataInput;
import java.io.IOException;

public interface ICustomChunk extends CustomChunk {
	@Override
	default void read(DataInput stream, int length) throws IOException {
		read(stream);
	}

	@Override
	default boolean shouldWrite() {
		return true;
	}

	@Override
	default boolean writeNet() {
		return true;
	}
}
