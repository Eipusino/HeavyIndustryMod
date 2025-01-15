package heavyindustry.gen;

import arc.*;
import arc.struct.*;
import heavyindustry.world.blocks.defense.CommandableBlock.*;
import mindustry.core.*;
import mindustry.game.EventType.*;

import static arc.Core.*;

public final class WorldRegister {
    public static final Seq<Runnable> afterLoad = new Seq<>();

    public static final Seq<CommandableBuild> commandableBuilds = new Seq<>();

    public static boolean worldLoaded = false;

    /** Don't let anyone instantiate this class. */
    private WorldRegister() {}

    public static void postAfterLoad(Runnable runnable) {
        if (worldLoaded) afterLoad.add(runnable);
    }

    public static void load() {
        Events.on(ResetEvent.class, event -> {
            commandableBuilds.clear();

            worldLoaded = true;
        });

        Events.on(WorldLoadEvent.class, event -> {
            app.post(() -> {
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
