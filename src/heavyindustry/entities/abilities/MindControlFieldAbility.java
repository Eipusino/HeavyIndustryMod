package heavyindustry.entities.abilities;

import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

public class MindControlFieldAbility extends Ability {
	public float damage = 1f, threshold = 1000f, reload = 180f, range = 60f;
	public Effect interfereEffect = Fx.none;

	public boolean hasInterfere = false;
	public float time = 0f;

	public MindControlFieldAbility(float dmg, float thr, float rel, float ran) {
		damage = dmg;
		threshold = thr;
		reload = rel;
		range = ran;
	}

	public MindControlFieldAbility() {}

	@Override
	public void update(Unit unit) {
		if ((time += Time.delta) >= reload) {
			Units.nearbyEnemies(unit.team, unit.x, unit.y, range, other -> {
				hasInterfere = true;
				if (other.health <= threshold) {
					other.team = unit.team;
					other.heal();
				} else {
					other.health -= damage;
				}
			});
			Units.nearbyBuildings(unit.x, unit.y, range, other -> {
				if (other.team != unit.team) {
					hasInterfere = true;
					if (other.health <= threshold) {
						other.team = unit.team;
						other.heal();
						interfereEffect.at(other);
					} else {
						other.health -= damage;
					}
				}
			});
			if (hasInterfere) {
				interfereEffect.at(unit);
			}
			hasInterfere = false;
			time = 0;
		}
	}
}
