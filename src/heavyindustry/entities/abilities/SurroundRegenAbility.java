package heavyindustry.entities.abilities;

import arc.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class SurroundRegenAbility extends Ability {
	public float healAmount = 1, reload = 100, healRange = 60;

	protected float timer;
	protected boolean wasHealed = false;

	public SurroundRegenAbility(float helAmo, float rel, float helRan) {
		healAmount = helAmo;
		reload = rel;
		healRange = helRan;
	}

	public SurroundRegenAbility() {}

	@Override
	public void addStats(Table t) {
		t.add(("[lightgray]" + Stat.range.localized() + ": [white]" + Strings.autoFixed(healRange / tilesize, 2)) + StatUnit.blocks.localized());
		t.add(("[lightgray]" + Stat.healing.localized() + ": [white]" + Strings.autoFixed(healAmount * 60f, 2)) + StatUnit.perSecond.localized());
	}

	@Override
	public String localized() {
		return Core.bundle.format("ability.surroundregenability", healAmount, healRange);
	}

	@Override
	public void update(Unit unit) {
		timer += Time.delta;

		if (timer >= reload) {
			wasHealed = false;

			Units.nearby(unit.team(), unit.x, unit.y, healRange, other -> {
				if (other.health < other.maxHealth()) {
					Fx.heal.at(unit);
					wasHealed = true;
				}
				other.heal(healAmount);
			});

			if (wasHealed) {
				Fx.healWave.at(unit);
			}

			timer = 0f;
		}
	}
}
