package heavyindustry.world;

import arc.Core;
import arc.Events;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Structs;
import heavyindustry.game.TeamPayloadData;
import heavyindustry.game.WorldData;
import heavyindustry.util.Pair;
import heavyindustry.world.blocks.defense.CommandableBlock;
import mindustry.Vars;
import mindustry.game.EventType.ResetEvent;
import mindustry.io.SaveVersion;
import mindustry.world.Block;

import static heavyindustry.HVars.MOD_NAME;

public final class Worlds {
	public static final Seq<CommandableBlock.CommandableBuild> commandableBuilds = new Seq<>(CommandableBlock.CommandableBuild.class);

	public static WorldData worldData = new WorldData();
	public static TeamPayloadData teamPayloadData = new TeamPayloadData();

	/** Don't let anyone instantiate this class. */
	private Worlds() {}

	public static void load() {
		Events.on(ResetEvent.class, event -> commandableBuilds.clear());
	}

	public static void init() {
		SaveVersion.addCustomChunk(MOD_NAME + "-world-data", worldData);
		SaveVersion.addCustomChunk(MOD_NAME + "-team-payload-data", teamPayloadData);
	}

	public static void exportBlockData() {
		StringBuilder data = new StringBuilder();

		Seq<Pair<String, Block>> blocks = new Seq<>(Pair.class);

		for (Block block : Vars.content.blocks()) {
			blocks.add(new Pair<>(block.name, block));
		}

		for (ObjectMap.Entry<String, String> entry : SaveVersion.fallback) {
			Block block = Vars.content.block(entry.value);
			if (block != null) {
				blocks.add(new Pair<>(entry.key, block));
			}
		}

		blocks.sort(Structs.comparingInt(pair -> pair.value.id))
				.each(pair -> write(data, pair.value, pair.key));

		Vars.platform.showFileChooser(false, Core.bundle.get("hi-export-data"), "dat", file -> {
			if (file == null) return;

			file.writeString(data.toString(), false);
			Core.app.post(() -> Vars.ui.showInfo(Core.bundle.format("hi-export-data-format", file.name())));
		});
	}

	static void write(StringBuilder data, Block block, String name) {
		data.append(name);
		data.append(' ');
		data.append(block.synthetic() ? '1' : '0');
		data.append(' ');
		data.append(block.solid ? '1' : '0');
		data.append(' ');
		data.append(block.size);
		data.append(' ');
		data.append(block.mapColor.rgba() >>> 8);
		data.append('\n');
	}
}
