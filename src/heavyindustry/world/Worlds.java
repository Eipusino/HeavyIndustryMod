package heavyindustry.world;

import arc.Events;
import arc.struct.Seq;
import heavyindustry.world.blocks.defense.CommandableBlock;
import mindustry.game.EventType.ResetEvent;

public final class Worlds {
	public static final Seq<CommandableBlock.CommandableBuild> commandableBuilds = new Seq<>(CommandableBlock.CommandableBuild.class);

	/** Don't let anyone instantiate this class. */
	private Worlds() {}

	public static void load() {
		Events.on(ResetEvent.class, event -> commandableBuilds.clear());
	}
}
