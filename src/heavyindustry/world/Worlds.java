package heavyindustry.world;

import arc.Core;
import arc.Events;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Strings;
import arc.util.Structs;
import heavyindustry.entities.HEntity;
import heavyindustry.game.TeamPayloadData;
import heavyindustry.graphics.PositionLightning;
import heavyindustry.util.CollectionList;
import heavyindustry.util.ref.Pair;
import heavyindustry.world.blocks.defense.CommandableBlock;
import mindustry.Vars;
import mindustry.game.EventType.ResetEvent;
import mindustry.io.SaveFileReader;
import mindustry.io.SaveVersion;
import mindustry.world.Block;
import org.jetbrains.annotations.ApiStatus.Internal;

import static heavyindustry.HVars.MOD_NAME;

public final class Worlds {
	public static final CollectionList<CommandableBlock.CommandableBuild> commandableBuilds = new CollectionList<>(CommandableBlock.CommandableBuild.class);

	public static TeamPayloadData teamPayloadData = new TeamPayloadData();

	/** Don't let anyone instantiate this class. */
	private Worlds() {}

	@Internal
	public static void load() {
		Events.on(ResetEvent.class, event -> {
			commandableBuilds.clear();
			teamPayloadData.teamPayloadData.clear();

			PositionLightning.reset();

			HEntity.reset();
		});
	}

	/** Not needed for now. */
	@Internal
	public static void init() {
		SaveVersion.addCustomChunk(MOD_NAME + "-team-payload-data", teamPayloadData);
	}

	public static void exportBlockData() {
		StringBuilder data = new StringBuilder();

		CollectionList<Pair<String, Block>> blocks = new CollectionList<>(Pair.class);

		Seq<Block> seq = Vars.content.blocks();
		for (int i = 0; i < seq.size; i++) {
			Block block = seq.get(i);
			blocks.add(new Pair<>(block.name, block));
		}

		for (var entry : SaveFileReader.fallback) {
			Block block = Vars.content.block(entry.value);
			if (block != null) {
				blocks.add(new Pair<>(entry.key, block));
			}
		}

		blocks.sort(Structs.comparingInt(pair -> pair.right.id));
		blocks.each(pair -> {
			String name = pair.left;
			Block block = pair.right;

			data
					.append(name).append(' ')//name
					.append(block.synthetic() ? '1' : '0').append(' ')//synthetic
					.append(block.solid ? '1' : '0').append(' ')//solid
					.append(block.size).append(' ')//size
					.append(block.mapColor.rgba() >>> 8).append('\n');//mapColor
		});

		Vars.platform.showFileChooser(false, Core.bundle.get("text.export-data"), "dat", file -> {
			try {
				file.writeBytes(data.toString().getBytes(Strings.utf8), false);
				Core.app.post(() -> Vars.ui.showInfo(Core.bundle.format("text.export-data-format", file.name())));
			} catch (Throwable e) {
				Log.err(e);

				Vars.ui.showException(e);
			}
		});
	}
}
