package heavyindustry.entities.abilities;

import arc.audio.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import heavyindustry.world.meta.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.meta.*;

public class SafeAbility extends Ability {
	public float threshold = 0.5f, reload, trailChance = 0.3f;
	public float safeShield = 0, safeHeal = 0, safeDamage = 0, safeDamageRadius, statusDur;
	public StatusEffect selfStatus;
	public Effect safeUseFx = Fx.none;
	public Effect readyTrail = Fx.none;
	public Sound useSound = Sounds.none;
	public float safeTimer = 5000 * 60;

	public SafeAbility() {}

	public SafeAbility(float thr, float sld, float rel, Effect tfx) {
		threshold = thr;
		safeShield = sld;
		reload = rel;
		readyTrail = tfx;
	}

	public SafeAbility(float dmg, float rad, Effect fx, Effect tfx) {
		safeDamage = dmg;
		safeDamageRadius = rad;
		safeUseFx = fx;
		readyTrail = tfx;
	}

	public void addStats(Table t) {
		if (safeShield != 0) {
			t.add("[lightgray]" + Stat.shieldHealth.localized() + ": [white]" + Strings.autoFixed(safeShield, 0));
			t.row();
		}
		if (safeShield != 0) {
			t.add("[lightgray]" + Stat.healing.localized() + ": [white]" + Strings.autoFixed(safeHeal, 0));
			t.row();
		}
		if (safeDamage != 0) {
			t.add("[lightgray]" + Stat.damage.localized() + ": [white]" + Strings.autoFixed(safeDamage, 0));
			t.row();
			t.add("[lightgray]" + Stat.range.localized() + ": [white]" + Strings.autoFixed(safeDamageRadius / 8, 2) + " " + StatUnit.blocks.localized());
			t.row();
		}
		t.add("[lightgray]" + Stat.cooldownTime.localized() + ": [white]" + Strings.autoFixed(reload / 60, 2) + " " + StatUnit.seconds.localized());
		t.row();
		t.add("[lightgray]" + HIStatUnit.threshold.localized() + ": [white]" + Strings.autoFixed(threshold * 100, 0) + " " + StatUnit.percent.localized());
		if (selfStatus != null) {
			t.row();
			t.add(selfStatus.emoji() + " " + selfStatus.localizedName);
		}
	}

	public void displayBars(Unit unit, Table bars) {
		bars.add(new Bar("stat.cooldownsafe", Pal.slagOrange, () -> Math.min(safeTimer / reload, 1))).row();
	}

	public void update(Unit unit) {
		if (unit.health / unit.maxHealth <= threshold && safeTimer >= reload && unit.shield == 0) {
			if (safeShield != 0) {
				unit.shield(safeShield);
			}
			if (safeHeal != 0) {
				unit.heal(safeHeal);
			}
			if (safeDamage != 0) {
				Damage.damage(unit.team, unit.x, unit.y, safeDamageRadius, safeDamage);
			}
			safeUseFx.at(unit.x, unit.y);
			useSound.at(unit.x, unit.y);
			safeTimer = 0;
			if (selfStatus != null) {
				unit.apply(selfStatus, statusDur);
			}
		}
		if (safeTimer <= reload) {
			safeTimer++;
		}
		if (safeTimer >= reload && Mathf.chanceDelta(trailChance)) {
			readyTrail.at(unit.x, unit.y);
		}
	}
}
