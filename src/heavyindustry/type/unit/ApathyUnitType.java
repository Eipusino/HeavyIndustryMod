package heavyindustry.type.unit;

import arc.struct.Seq;
import heavyindustry.ai.BaseCommandAI;
import heavyindustry.content.HUnitCommands;
import heavyindustry.entities.shift.ShiftHandler;
import mindustry.Vars;
import mindustry.type.StatusEffect;

public class ApathyUnitType extends BaseUnitType {
	public Seq<ShiftHandler> handlers = new Seq<>(ShiftHandler.class);

	public ApathyUnitType(String name) {
		super(name);
		controller = u -> !playerControllable || (u.team.isAI() && !u.team.rules().rtsAi) ? aiController.get() : new BaseCommandAI();
	}

	@Override
	public void init() {
		super.init();
		for (StatusEffect s : Vars.content.statusEffects()) {
			immunities.add(s);
		}

		commands.add(HUnitCommands.apathyUnitCommand);
	}

	@Override
	public void load() {
		super.load();
		for (ShiftHandler h : handlers) {
			h.load();
		}
	}
}
