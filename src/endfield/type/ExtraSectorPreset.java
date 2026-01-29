package endfield.type;

import arc.Events;
import endfield.util.CollectionObjectMap;
import endfield.util.Constant;
import mindustry.Vars;
import mindustry.game.EventType.Trigger;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.type.SectorPreset;

public class ExtraSectorPreset extends SectorPreset {
	public static CollectionObjectMap<Sector, Runnable> scripts = new CollectionObjectMap<>(Sector.class, Runnable.class);

	static {
		Events.run(Trigger.update, () -> {
			if (Vars.state.getSector() != null) scripts.getDefault(Vars.state.getSector(), Constant.RUNNABLE_NOTHING).run();
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
