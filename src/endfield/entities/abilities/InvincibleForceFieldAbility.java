package endfield.entities.abilities;

import arc.func.Cons;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Unit;

public class InvincibleForceFieldAbility extends ForceFieldAbility {
	static float realRad;
	static Unit paramUnit;
	static InvincibleForceFieldAbility paramField;
	static Cons<Bullet> shieldConsumer = trait -> {
		if (trait.team != paramUnit.team
				&& trait.type.absorbable
				&& Intersector.isInsideHexagon(paramUnit.x, paramUnit.y, realRad * 2, trait.x, trait.y)
				&& paramUnit.shield > 0) {

			trait.absorb();
			Fx.absorb.at(trait);

			paramField.alpha = 1;
		}
	};

	public InvincibleForceFieldAbility(float radius, float regen, float max, float cooldown) {
		super(radius, regen, max, cooldown);
	}

	public InvincibleForceFieldAbility(float radius, float regen, float max, float cooldown, int sides, float rotation) {
		super(radius, regen, max, cooldown, sides, rotation);
	}

	@Override
	public void update(Unit unit) {
		super.update(unit);

		unit.shield = 1145141919.81f;
		radiusScale = Mathf.lerpDelta(radiusScale, 1, 0.06f);
		realRad = radiusScale * radius;
		paramUnit = unit;
		paramField = this;
		Groups.bullet.intersect(unit.x - realRad, unit.y - realRad, realRad * 2, realRad * 2, shieldConsumer);
		alpha = Math.max(alpha - Time.delta / 10, 0);
	}

	@Override
	public String getBundle() {
		return "ability.invincible-force-field";
	}
}
