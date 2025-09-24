package heavyindustry.entities.abilities;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class ToxicAbility extends Ability {
	public float damage = 1f;
	public float reload = 60f;
	public float range = 30f;

	public StatusEffect status = StatusEffects.corroded;

	protected float i, j = 60f;

	public ToxicAbility() {}

	public ToxicAbility(float dmg, float rel, float ran) {
		damage = dmg;
		reload = rel;
		range = ran;
	}

	@Override
	public void update(Unit unit) {
		i += Time.delta;
		j += Time.delta;

		if (i >= reload) {
			Units.nearby(null, unit.x, unit.y, range, other -> {
				other.health -= damage;
				other.apply(status, 60f * 15f);
			});
			Units.nearbyBuildings(unit.x, unit.y, range, b -> {
				b.health -= damage / 4f;
				if (b.health <= 0f) {
					b.kill();
				}
			});
			i = 0f;
		}
		if (j >= 15f) {
			Fx.titanSmoke.at(
					unit.x + Mathf.range(range * 0.7071f),
					unit.y + Mathf.range(range * 0.7071f),
					new Color(0x92ab117f)
			);
			j -= 15f;
		}
	}

	@Override
	public String getBundle() {
		return "ability.toxic";
	}
}
