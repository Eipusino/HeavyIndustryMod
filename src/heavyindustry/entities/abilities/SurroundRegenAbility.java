package heavyindustry.entities.abilities;

import arc.Core;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;

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
