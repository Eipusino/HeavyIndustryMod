package heavyindustry.io;

import mindustry.io.SaveVersion;

public final class WorldData {
	public static WorldTileData worldTileData;

	private WorldData() {}

	public static void init() {
		worldTileData = new WorldTileData();

		SaveVersion.addCustomChunk("hi-world-tile-data", worldTileData);
	}
}
