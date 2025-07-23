package heavyindustry.entities.abilities;

import arc.Core;
import arc.func.Cons;
import arc.math.Angles;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.Time;
import heavyindustry.content.HFx;
import heavyindustry.math.Mathm;
import heavyindustry.world.meta.HStat;
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
 * @see CollideBlockerAbility
 * @see MirrorFieldAbility
 * @see MirrorArmorAbility
 * @since 1.0.6
 */
public abstract class BaseMirrorShieldAbility extends Ability implements CollideBlockerAbility {
	public Effect breakEffect = HFx.mirrorShieldBreak;
	public Effect reflectEffect = Fx.none;
	public Effect refrectEffect = Fx.absorb;

	public float shieldArmor = 0f;
	public float maxShield = 1200f;
	public float cooldown = 900f;
	public float recoverSpeed = 2f;
	public float minAlbedo = 1f, maxAlbedo = 1f;
	public float strength = 200f;
	public float refractAngleRange = 30f;

	protected float alpha;
	protected float radScl;

	private boolean lastBreak;

	public abstract boolean shouldReflect(Unit unit, Bullet bullet);

	public abstract void eachNearBullets(Unit unit, Cons<Bullet> cons);

	@Override
	public void addStats(Table t) {
		t.add("[lightgray]" + Stat.health.localized() + ": [white]" + Math.round(maxShield));
		t.row();
		t.add("[lightgray]" + Stat.armor.localized() + ": [white]" + Math.round(shieldArmor));
		t.row();
		t.add("[lightgray]" + HStat.fieldStrength.localized() + ": [white]" + Math.round(strength));
		t.row();
		t.add("[lightgray]" + HStat.albedo.localized() + ": [white]" + (Mathf.equal(minAlbedo, maxAlbedo) ?
				Mathf.round(minAlbedo * 100) + "%" :
				Mathf.round(minAlbedo * 100) + "% - " + Mathf.round(maxAlbedo * 100) + "%"));
		t.row();
		t.add("[lightgray]" + Stat.repairSpeed.localized() + ": [white]" + Strings.autoFixed(recoverSpeed * 60f, 2) + StatUnit.perSecond.localized());
		t.row();
		t.add("[lightgray]" + Stat.cooldownTime.localized() + ": [white]" + Strings.autoFixed(cooldown / recoverSpeed / 60, 2) + " " + StatUnit.seconds.localized());
		t.row();
	}

	@Override
	public void displayBars(Unit unit, Table bars) {
		bars.add(new Bar(
				() -> Core.bundle.format("bar.mirror-shield-health", Mathf.round(Mathf.maxZero(unit.shield))),
				() -> Pal.accent,
				() -> unit.shield / maxShield
		)).row();
	}

	@Override
	public boolean blockedCollides(Unit unit, Hitboxc other) {
		if (!(other instanceof Bullet bullet)) return true;

		boolean blocked = unit.shield > 0 && bullet.type.reflectable && !bullet.hasCollided(unit.id) && shouldReflect(unit, bullet);

		if (blocked) doCollide(unit, bullet);

		return blocked;
	}

	@Override
	public void update(Unit unit) {
		alpha = Mathf.lerpDelta(alpha, 0, 0.06f);

		lastBreak = false;
		if (unit.shield > 0) {
			radScl = Mathf.lerpDelta(radScl, 1f, 0.06f);

			eachNearBullets(unit, bullet -> {
				if (!bullet.type.reflectable || bullet.hasCollided(unit.id) || !shouldReflect(unit, bullet)) return;

				doCollide(unit, bullet);
			});
		} else {
			radScl = 0f;
		}

		if (!lastBreak && unit.shield < maxShield)
			unit.shield = Math.min(maxShield, unit.shield + recoverSpeed * Time.delta * unit.reloadMultiplier);
	}

	public void doCollide(Unit unit, Bullet bullet) {
		if (bullet.damage < Math.min(strength, unit.shield)) {
			doReflect(unit, bullet);
		} else {
			alpha = 1;
			bullet.collided.add(unit.id);
			damageShield(unit, bullet.damage());

			bullet.damage -= Math.min(strength, unit.shield);
			float rot;
			bullet.rotation(rot = bullet.rotation() + Mathf.range(refractAngleRange));
			refrectEffect.at(bullet.x, bullet.y, rot, bullet.type.hitColor);
		}
	}

	public void damageShield(Unit unit, float damage) {
		if (unit.shield <= damage - shieldArmor) {
			unit.shield = -cooldown;

			breakEffect.at(unit.x, unit.y, unit.rotation(), unit.team.color, this);
			lastBreak = true;
		} else {
			unit.shield -= Mathf.maxZero(damage - shieldArmor);
		}
	}

	public void doReflect(Unit unit, Bullet bullet) {
		alpha = 1;

		float albedo = Mathf.equal(minAlbedo, maxAlbedo) ? minAlbedo : Mathf.random(minAlbedo, maxAlbedo);

		damageShield(unit, bullet.damage() * albedo);

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
