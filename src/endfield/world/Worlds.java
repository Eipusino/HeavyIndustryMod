package endfield.world;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Strings;
import arc.util.Structs;
import endfield.Vars2;
import endfield.entities.Entitys2;
import endfield.game.TeamPayloadData;
import endfield.graphics.PositionLightning;
import endfield.util.CollectionList;
import endfield.world.blocks.defense.CommandableBlock;
import kotlin.Pair;
import mindustry.Vars;
import mindustry.game.EventType.ResetEvent;
import mindustry.io.SaveFileReader;
import mindustry.io.SaveVersion;
import mindustry.world.Block;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import static endfield.Vars2.MOD_NAME;

public final class Worlds {
	public static final CollectionList<CommandableBlock.CommandableBuild> commandableBuilds = new CollectionList<>(CommandableBlock.CommandableBuild.class);

	public static TeamPayloadData teamPayloadData = new TeamPayloadData();

	/** Don't let anyone instantiate this class. */
	private Worlds() {}

	public static void load() {
		Events.on(ResetEvent.class, event -> {
			commandableBuilds.clear();
			teamPayloadData.teamPayloadData.clear();

			PositionLightning.reset();

			Entitys2.reset();
		});
	}

	/** Not needed for now. */
	public static void init() {
		SaveVersion.addCustomChunk(MOD_NAME + "-team-payload-data", teamPayloadData);
	}

	public static void addAll() {
		// These properties should not be included in the code
		Fi file = Vars2.internalTree.child("other").child("fallback.properties");

		Properties properties = new Properties(348);
		try (Reader reader = file.reader(512)) {
			properties.load(reader);
		} catch (IOException e) {
			Log.err(e);
		}

		var fallback = SaveFileReader.fallback;

		for (var entry : properties.entrySet()) {
			String key = entry.getKey().toString(), value = entry.getValue().toString();
			fallback.put(key, value);
		}
	}

	public static void exportBlockData() {
		StringBuilder data = new StringBuilder();

		CollectionList<Pair<String, Block>> blocks = new CollectionList<>(Pair.class);

		Seq<Block> seq = Vars.content.blocks();
		for (Block block : seq) {
			blocks.add(new Pair<>(block.name, block));
		}

		for (var entry : SaveFileReader.fallback) {
			Block block = Vars.content.block(entry.value);
			if (block != null) {
				blocks.add(new Pair<>(entry.key, block));
			}
		}

		blocks.sort(Structs.comparingInt(pair -> pair.getSecond().id));

		for (Pair<String, Block> pair : blocks) {
			String name = pair.getFirst();
			Block block = pair.getSecond();

			data
					.append(name).append(' ')//name
					.append(block.synthetic() ? '1' : '0').append(' ')//synthetic
					.append(block.solid ? '1' : '0').append(' ')//solid
					.append(block.size).append(' ')//size
					.append(block.mapColor.rgba() >>> 8).append('\n');//mapColor
		}

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
