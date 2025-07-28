package heavyindustry.gen;

import arc.Core;
import arc.Events;
import arc.struct.Seq;
import heavyindustry.world.blocks.defense.CommandableBlock.CommandableBuild;
import mindustry.core.GameState;
import mindustry.game.EventType.ResetEvent;
import mindustry.game.EventType.StateChangeEvent;
import mindustry.game.EventType.WorldLoadEvent;

public final class Worlds {
	public static final Seq<Runnable> afterLoad = new Seq<>();

	public static final Seq<CommandableBuild> commandableBuilds = new Seq<>();

	public static boolean worldLoaded = false;

	/** Don't let anyone instantiate this class. */
	private Worlds() {}

	public static void postAfterLoad(Runnable runnable) {
		if (worldLoaded) afterLoad.add(runnable);
	}

	public static void load() {
		Events.on(ResetEvent.class, event -> {
			commandableBuilds.clear();

			worldLoaded = true;
		});

		Events.on(WorldLoadEvent.class, event -> {
			Core.app.post(() -> {
				worldLoaded = false;
			});
		});

		Events.on(StateChangeEvent.class, event -> {
			if (event.to == GameState.State.menu) {
				worldLoaded = true;
			}
		});
	}
}
