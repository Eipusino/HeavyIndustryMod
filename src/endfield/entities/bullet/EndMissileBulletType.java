package endfield.entities.bullet;

import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import endfield.content.Fx2;
import endfield.entities.Entitys2;
import endfield.graphics.Pal2;
import endfield.math.Mathm;
import endfield.type.weapons.EndLauncherWeapon.EndLauncherData;
import endfield.util.Constant;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;

import static endfield.Vars2.MOD_NAME;

public class EndMissileBulletType extends BasicBulletType {
	public EndMissileBulletType() {
		super(14f, 1000f, MOD_NAME + "-missile");

		backColor = trailColor = hitColor = Pal2.red;
		frontColor = Pal2.red.cpy().mul(2f);

		shrinkY = 0f;
		width = 9f;
		height = 22f;

		trailLength = 9;
		trailWidth = 3f;

		lifetime = 90f;

		despawnHit = true;

		hitEffect = Fx.none;
		despawnEffect = Fx.none;
	}

	@Override
	public void updateHoming(Bullet b) {
		if (!(b.data instanceof EndLauncherData data)) {
			super.updateHoming(b);

			return;
		}

		data.ret -= Time.delta;

		if (data.ret <= 0f) {
			Teamc target = Units.closestTarget(b.team, b.x, b.y, 520f, u -> !data.mount.targets.containsKey(u), bl -> !data.mount.targets.containsKey(bl));
			if (target == null) target = Units.closestTarget(b.team, b.x, b.y, 520f, Constant.BOOLF_UNIT_TRUE, Constant.BOOLF_BUILDING_TRUE);

			if (target instanceof Building build) {
				b.aimTile = build.tile;
			} else {
				b.aimTile = null;
			}

			if (data.current != null && target != null) {
				data.mount.removeTarget(data.current);
			}
			data.current = target;
			data.ret = target == null ? 5f : 17f;

			if (data.current != null) {
				data.mount.addTarget(target);
			}
		}
		if (data.current != null && Units.invalidateTarget(data.current, b.team, b.x, b.y, 530f)) {
			data.mount.removeTarget(data.current);
			data.current = null;
		}

		if (data.current != null) {
			b.aimX = data.current.x();
			b.aimY = data.current.y();
		} else {
			b.aimX = data.mount.aimX;
			b.aimY = data.mount.aimY;
		}

		float ang = Angles.angle(b.x, b.y, b.aimX, b.aimY);

		b.vel.setAngle(Angles.moveToward(b.rotation(), ang, (10f + b.fin() * 8f) * (1f + Mathf.absin(b.time, 4f, 0.5f)) * Interp.pow2.apply(Mathm.clamp(b.time / 30f)) * Time.delta));
	}

	@Override
	public void hit(Bullet b, float x, float y) {
		super.hit(b, x, y);

		Fx2.desMissileHit.at(x, y, b.rotation());

		Entitys2.scanEnemies(b.team, x, y, 40f, true, true, t -> {
			float dam = Mathf.chance(0.333f) ? 1000f : 200f;

			if (t instanceof Healthc h) {
				h.health(h.health() - dam + h.maxHealth() / 110f);

				if (h instanceof Building build && build.health <= 0f) build.kill();
			}
		});
	}

	@Override
	public void removed(Bullet b) {
		super.removed(b);

		if (b.data instanceof EndLauncherData data) {
			if (data.current != null) data.mount.removeTarget(data.current);
		}
	}
}
