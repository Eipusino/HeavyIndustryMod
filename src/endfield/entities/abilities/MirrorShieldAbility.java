package endfield.entities.abilities;

import arc.func.Cons;
import arc.math.Angles;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.Time;
import endfield.content.Fx2;
import endfield.math.Mathm;
import endfield.world.meta.EStat;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

/**
 * Base mirror shield ability
 * <p>abstract class, Not directly usable.
 *
 * @see MirrorFieldAbility
 * @see MirrorArmorAbility
 * @since 1.0.6
 */
public abstract class MirrorShieldAbility extends Ability implements ICollideBlockerAbility {
	public Effect breakEffect = Fx2.mirrorShieldBreak;
	public Effect reflectEffect = Fx.none;
	public Effect refrectEffect = Fx.absorb;

	public float shieldArmor = 0f;
	public float max = 1200f;
	public float cooldown = 600f;
	public float regen = 2f;
	public float minAlbedo = 1f, maxAlbedo = 1f;
	public float strength = 200f;
	public float refractAngleRange = 30f;

	protected float alpha;
	protected float radiusScale;

	protected boolean wasBroken;

	public abstract boolean shouldReflect(Unit unit, Bullet bullet);

	public abstract void eachNearBullets(Unit unit, Cons<Bullet> cons);

	@Override
	public void addStats(Table t) {
		t.add("[lightgray]" + Stat.health.localized() + ": [white]" + Math.round(max));
		t.row();
		t.add("[lightgray]" + Stat.armor.localized() + ": [white]" + Math.round(shieldArmor));
		t.row();
		t.add("[lightgray]" + EStat.fieldStrength.localized() + ": [white]" + Math.round(strength));
		t.row();
		t.add("[lightgray]" + EStat.albedo.localized() + ": [white]" + (Mathf.equal(minAlbedo, maxAlbedo) ?
				Mathf.round(minAlbedo * 100) + "%" :
				Mathf.round(minAlbedo * 100) + "% - " + Mathf.round(maxAlbedo * 100) + "%"));
		t.row();
		t.add("[lightgray]" + Stat.repairSpeed.localized() + ": [white]" + Strings.autoFixed(regen * 60f, 2) + StatUnit.perSecond.localized());
		t.row();
		t.add("[lightgray]" + Stat.cooldownTime.localized() + ": [white]" + Strings.autoFixed(cooldown / 60, 2) + " " + StatUnit.seconds.localized());
		t.row();
	}

	@Override
	public void displayBars(Unit unit, Table bars) {
		bars.add(new Bar("bar.mirror-shield-health", Pal.accent, () -> Math.max(unit.shield, 0f) / max)).row();
	}

	@Override
	public boolean blockedCollides(Unit unit, Hitboxc other) {
		if (other instanceof Bullet bullet) {
			boolean blocked = unit.shield > 0 && bullet.type.reflectable && !bullet.hasCollided(unit.id) && shouldReflect(unit, bullet);

			if (blocked) doCollide(unit, bullet);

			return blocked;
		}

		return true;
	}

	@Override
	public void created(Unit unit) {
		unit.shield = max;
	}

	@Override
	public void update(Unit unit) {
		if (unit.shield <= 0f && !wasBroken) {
			unit.shield -= cooldown * regen;

			breakEffect.at(unit.x, unit.y, unit.rotation(), unit.team.color, this);
		}

		wasBroken = unit.shield <= 0f;

		if (unit.shield < max) {
			unit.shield += Time.delta * regen;
		}

		alpha = Mathf.lerpDelta(alpha, 0, 0.06f);

		if (unit.shield > 0) {
			radiusScale = Mathf.lerpDelta(radiusScale, 1f, 0.06f);

			eachNearBullets(unit, bullet -> {
				if (!bullet.type.reflectable || bullet.hasCollided(unit.id) || !shouldReflect(unit, bullet)) return;

				doCollide(unit, bullet);
			});
		} else {
			radiusScale = 0f;
		}
	}

	@Override
	public void death(Unit unit) {
		//self-destructing units can have a shield on death
		if (unit.shield > 0f && !wasBroken) {
			breakEffect.at(unit.x, unit.y, unit.rotation(), unit.team.color, this);
		}
	}

	public void doCollide(Unit unit, Bullet bullet) {
		if (bullet.damage < Math.min(strength, unit.shield)) {
			doReflect(unit, bullet);
		} else {
			alpha = 1;
			bullet.collided.add(unit.id);
			damageShield(unit, bullet.type().shieldDamage(bullet));

			bullet.damage -= Math.min(strength, unit.shield);
			float rot;
			bullet.rotation(rot = bullet.rotation() + Mathf.range(refractAngleRange));
			refrectEffect.at(bullet.x, bullet.y, rot, bullet.type.hitColor);
		}
	}

	public void damageShield(Unit unit, float damage) {
		if (unit.shield <= Math.max(damage - shieldArmor, 0f)) {
			breakEffect.at(unit.x, unit.y, unit.rotation(), unit.team.color, this);

			wasBroken = true;
		} else {
			unit.shield -= Math.max(damage - shieldArmor, 0f);
		}
	}

	public void doReflect(Unit unit, Bullet bullet) {
		alpha = 1;

		float albedo = Mathf.equal(minAlbedo, maxAlbedo) ? minAlbedo : Mathf.random(minAlbedo, maxAlbedo);

		damageShield(unit, bullet.type().shieldDamage(bullet) * albedo);

		float baseAngel = Angles.angle(bullet.x - unit.x, bullet.y - unit.y);
		float diffAngel = Mathm.innerAngle(bullet.rotation(), baseAngel);

		float reflectAngel = baseAngel + 180 + diffAngel;
		if (albedo >= 0.99f) {
			bullet.team = unit.team;
			bullet.rotation(reflectAngel);
			bullet.collided.add(unit.id);
		} else {
			bullet.type.create(
					unit, unit.team,
					bullet.x, bullet.y, reflectAngel,
					bullet.damage * albedo,
					1,
					(bullet.type.splashDamage > bullet.damage ? albedo : 1) * (1 - bullet.time / bullet.lifetime),
					bullet.data,
					bullet.mover,
					unit.aimX,
					unit.aimY
			);

			float rotation;
			bullet.rotation(rotation = bullet.rotation() + Mathf.range(refractAngleRange));
			bullet.damage *= 1 - albedo;
			bullet.collided.add(unit.id);

			refrectEffect.at(bullet.x, bullet.y, rotation, bullet.type.hitColor);

			reflectEffect.at(bullet.x, bullet.y, baseAngel, bullet.type.hitColor);
		}
	}
}
