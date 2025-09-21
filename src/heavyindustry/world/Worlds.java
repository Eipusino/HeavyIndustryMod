package heavyindustry.world;

import arc.Core;
import arc.Events;
import arc.util.Log;
import arc.util.Strings;
import arc.util.Structs;
import heavyindustry.game.TeamPayloadData;
import heavyindustry.util.CollectionList;
import heavyindustry.util.Pair;
import heavyindustry.world.blocks.defense.CommandableBlock;
import mindustry.Vars;
import mindustry.game.EventType.ResetEvent;
import mindustry.io.SaveVersion;
import mindustry.world.Block;

import static heavyindustry.HVars.MOD_NAME;

public final class Worlds {
	public static final CollectionList<CommandableBlock.CommandableBuild> commandableBuilds = new CollectionList<>(CommandableBlock.CommandableBuild.class);

	public static TeamPayloadData teamPayloadData = new TeamPayloadData();

	/** Don't let anyone instantiate this class. */
	private Worlds() {}

	public static void load() {
		Events.on(ResetEvent.class, event -> {
			commandableBuilds.clear();
		});
	}

	/** @deprecated Not needed for now. */
	@Deprecated
	public static void init() {
		SaveVersion.addCustomChunk(MOD_NAME + "-team-payload-data", teamPayloadData);
	}

	public static void exportBlockData() {
		StringBuilder data = new StringBuilder();

		CollectionList<Pair<String, Block>> blocks = new CollectionList<>(Pair.class);

		for (Block block : Vars.content.blocks()) {
			blocks.add(new Pair<>(block.name, block));
		}

		for (var entry : SaveVersion.fallback) {
			Block block = Vars.content.block(entry.value);
			if (block != null) {
				blocks.add(new Pair<>(entry.key, block));
			}
		}

		blocks.sort(Structs.comparingInt(pair -> pair.value.id));
		blocks.each(pair -> {
			String name = pair.key;
			Block block = pair.value;

			data.append(name).append(' ').append(block.synthetic() ? 1 : 0).append(' ').append(block.solid ? 1 : 0).append(' ').append(block.size).append(' ').append(block.mapColor.rgba() >>> 8).append('\n');
		});

		Vars.platform.showFileChooser(false, Core.bundle.get("hi-export-data"), "dat", file -> {
			try {
				file.writeBytes(data.toString().getBytes(Strings.utf8), false);
				Core.app.post(() -> Vars.ui.showInfo(Core.bundle.format("hi-export-data-format", file.name())));
			} catch (Throwable e) {
				Log.err(e);

				Vars.ui.showException(e);
			}
		});
	}
}
