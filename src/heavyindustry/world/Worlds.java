package heavyindustry.world;

import arc.Events;
import arc.struct.Seq;
import heavyindustry.game.TeamPayloadData;
import heavyindustry.game.WorldData;
import heavyindustry.world.blocks.defense.CommandableBlock;
import mindustry.game.EventType.ResetEvent;
import mindustry.io.SaveVersion;

import static heavyindustry.core.HeavyIndustryMod.MOD_NAME;

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
}
