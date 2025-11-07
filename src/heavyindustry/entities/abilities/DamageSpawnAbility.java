package heavyindustry.entities.abilities;

import arc.scene.ui.layout.Table;
import arc.util.Strings;
import mindustry.entities.Units;
import mindustry.entities.abilities.UnitSpawnAbility;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class DamageSpawnAbility extends UnitSpawnAbility {
	public float damage;
	public float range = 120;
	public float charge;

	public DamageSpawnAbility(UnitType uni, float spaTim, float spaX, float spaY, float dmg) {
		unit = uni;
		spawnTime = spaTim;
		spawnX = spaX;
		spawnY = spaY;
		damage = dmg;
	}

	public DamageSpawnAbility() {}

	@Override
	public void addStats(Table t) {
		t.add("[lightgray]" + Stat.damage.localized() + ": [white]" + Strings.autoFixed(damage, 2));
		t.row();
		t.add("[lightgray]" + Stat.range.localized() + ": [white]" + Strings.autoFixed(range / 8, 2) + " " + StatUnit.blocks.localized());
		t.row();
		t.add("[lightgray]" + Stat.buildTime.localized() + ": [white]" + Strings.autoFixed(spawnTime / 60f, 2) + " " + StatUnit.seconds.localized());
		t.row();
		t.add(unit.emoji() + " " + unit.localizedName);
	}

	@Override
	public void update(Unit unit) {
		Unit target = Units.closestEnemy(unit.team, unit.x, unit.y, range, u -> !u.dead());
		Building targetBuild = Units.findEnemyTile(unit.team, unit.x, unit.y, range, t -> !t.dead);

		if (target != null && charge == 0) {
			target.damage(damage);
			spawnEffect.at(target);
			charge += 20;
		} else if (targetBuild != null && charge == 0) {
			targetBuild.damage(damage);
			spawnEffect.at(targetBuild);
			charge += 20;
		}
		if (charge > 0) {
			super.update(unit);
			charge--;
		}
	}

	@Override
	public String getBundle() {
		return "ability.damage-spawn";
	}
}
