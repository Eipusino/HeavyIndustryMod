package heavyindustry.entities.abilities;

import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class StoreDamageAbility extends Ability {
	public float nowHealth = 0, storedDamage, maxStored, mod;
	public Effect applyEffect = Fx.shieldApply;
	public float heal, healReload, timer = 0, shield, shieldRange;

	public void addStats(Table t) {
		t.add("[lightgray]" + Stat.repairSpeed.localized() + ": [white]" + Strings.autoFixed(heal / healReload * 60, 0));
		t.row();
		t.add("[lightgray]" + Stat.shieldHealth.localized() + ": [white]" + Strings.autoFixed(shield, 0));
		t.row();
		t.add("[lightgray]" + Stat.range.localized() + ": [white]" + Strings.autoFixed(shieldRange / 8, 2) + " " + StatUnit.blocks.localized());
	}

	public void update(Unit unit) {
		if (nowHealth == 0) {
			nowHealth = unit.health;
		}
		if (unit.health != nowHealth) {
			storedDamage = Math.min((nowHealth - unit.health) * mod, maxStored);
		}
		if (timer < healReload) {
			timer++;
		} else if (storedDamage > heal) {
			storedDamage -= heal;
			unit.heal(heal);
			timer = 0;
		}
	}

	public void death(Unit u) {
		Units.nearby(u.team, u.x, u.y, shieldRange, other -> {
			if (other.shield < shield) {
				other.shield = Math.min(other.shield + shield * storedDamage / maxStored, shield);
				applyEffect.at(other.x, other.y, 0f, Color.valueOf("8deebb"));
			}
		});
	}
}
