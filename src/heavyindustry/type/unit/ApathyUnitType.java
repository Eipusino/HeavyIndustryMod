package heavyindustry.type.unit;

import arc.struct.Seq;
import heavyindustry.entities.shift.ShiftHandler;
import mindustry.Vars;
import mindustry.type.StatusEffect;

public class ApathyUnitType extends BaseUnitType {
	public Seq<ShiftHandler> handlers = new Seq<>(ShiftHandler.class);

	public ApathyUnitType(String name) {
		super(name);
	}

	@Override
	public void init() {
		super.init();
		for (StatusEffect s : Vars.content.statusEffects()) {
			immunities.add(s);
		}
	}

	@Override
	public void load() {
		super.load();
		for (ShiftHandler h : handlers) {
			h.load();
		}
	}
}
