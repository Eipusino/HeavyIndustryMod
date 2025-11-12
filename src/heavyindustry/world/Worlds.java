package heavyindustry.world;

import arc.Core;
import arc.Events;
import arc.util.Log;
import arc.util.Strings;
import arc.util.Structs;
import heavyindustry.entities.HEntity;
import heavyindustry.game.TeamPayloadData;
import heavyindustry.graphics.PositionLightning;
import heavyindustry.util.CollectionList;
import heavyindustry.util.holder.ObjectHolder;
import heavyindustry.world.blocks.defense.CommandableBlock;
import mindustry.Vars;
import mindustry.game.EventType.ResetEvent;
import mindustry.io.SaveFileReader;
import mindustry.io.SaveVersion;
import mindustry.world.Block;

import static heavyindustry.HVars.MOD_NAME;

public final class Worlds {
	public static final CollectionList<CommandableBlock.CommandableBuild> commandableBuilds = new CollectionList<>(CommandableBlock.CommandableBuild.class);

	public static TeamPayloadData teamPayloadData = new TeamPayloadData();

	/// Don't let anyone instantiate this class.
	private Worlds() {}

	public static void load() {
		Events.on(ResetEvent.class, event -> {
			commandableBuilds.clear();
			teamPayloadData.teamPayloadData.clear();

			PositionLightning.reset();

			HEntity.reset();
		});
	}

	/** @deprecated Not needed for now. */
	@Deprecated
	public static void init() {
		SaveVersion.addCustomChunk(MOD_NAME + "-team-payload-data", teamPayloadData);
	}

	public static void exportBlockData() {
		StringBuilder data = new StringBuilder();

		CollectionList<ObjectHolder<String, Block>> blocks = new CollectionList<>(ObjectHolder.class);

		for (Block block : Vars.content.blocks()) {
			blocks.add(new ObjectHolder<>(block.name, block));
		}

		for (var entry : SaveFileReader.fallback) {
			Block block = Vars.content.block(entry.value);
			if (block != null) {
				blocks.add(new ObjectHolder<>(entry.key, block));
			}
		}

		blocks.sort(Structs.comparingInt(holder -> holder.value.id));
		blocks.each(holder -> {
			String name = holder.key;
			Block block = holder.value;

			data.append(name).append(' ')
					.append(block.synthetic() ? '1' : '0').append(' ')
					.append(block.solid ? '1' : '0').append(' ')
					.append(block.size).append(' ')
					.append(block.mapColor.rgba() >>> 8).append('\n');
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
