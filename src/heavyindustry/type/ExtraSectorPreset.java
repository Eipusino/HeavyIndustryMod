package heavyindustry.type;

import arc.Events;
import heavyindustry.util.CollectionObjectMap;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.type.SectorPreset;

import static heavyindustry.func.FuncInte.RUNNABLE_NOTHING;

public class ExtraSectorPreset extends SectorPreset {
	public static CollectionObjectMap<Sector, Runnable> scripts = new CollectionObjectMap<>(Sector.class, Runnable.class);

	static {
		Events.run(EventType.Trigger.update, () -> {
			if (Vars.state.getSector() != null) scripts.get(Vars.state.getSector(), RUNNABLE_NOTHING).run();
		});
	}

	public ExtraSectorPreset(String name, Planet planet, int sec, Runnable run) {
		this(name, planet, sec);
		scripts.put(sector, run);
	}

	public ExtraSectorPreset(String name, Planet planet, int sec) {
		super(name, planet, sec);
	}

	/** returns true if a flag is present. */
	public static boolean getFlag(String flag, boolean remove) {
		if (Vars.state.rules.objectiveFlags.isEmpty()) return false;
		if (remove) return Vars.state.rules.objectiveFlags.remove(flag);
		return Vars.state.rules.objectiveFlags.contains(flag);
	}
}
