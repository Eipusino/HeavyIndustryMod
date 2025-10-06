package heavyindustry.entities.bullet;

import arc.math.geom.Geometry;
import arc.util.Tmp;
import heavyindustry.entities.HEntity;
import heavyindustry.util.Utils;
import mindustry.Vars;
import mindustry.entities.Mover;
import mindustry.entities.Units;
import mindustry.entities.bullet.PointBulletType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;

public class AnnihilatePointBulletType extends PointBulletType {
	protected static float cdist = 0f;
	protected static Unit result;

	public AnnihilatePointBulletType() {
		reflectable = false;
	}

	@Override
	public void hitEntity(Bullet b, Hitboxc entity, float health) {
		HEntity.annihilate(entity, false);
	}

	@Override
	public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
		if (build != null && build.team != b.team) {
			HEntity.annihilate(build, false);
		}
	}

	@Override
	public void init(Bullet b) {
		float px = b.x + b.lifetime * b.vel.x, py = b.y + b.lifetime * b.vel.y, rot = b.rotation();

		Geometry.iterateLine(0f, b.x, b.y, px, py, trailSpacing, (x, y) -> {
			trailEffect.at(x, y, rot);
		});

		b.time = b.lifetime;
		b.set(px, py);

		//calculate hit entity

		cdist = 0f;
		result = null;
		float range = 1f;

		Units.nearbyEnemies(b.team, px - range, py - range, range * 2f, range * 2f, e -> {
			if (e.dead()) return;

			e.hitbox(Tmp.r1);
			if (!Tmp.r1.contains(px, py)) return;

			float dst = e.dst(px, py) - e.hitSize;
			if ((result == null || dst < cdist)) {
				result = e;
				cdist = dst;
			}
		});

		if (result != null) {
			HEntity.annihilate(result, false);
		} else if (collidesTiles) {
			Building build = Vars.world.buildWorld(px, py);
			if (build != null && build.team != b.team) {
				HEntity.annihilate(build, false);
				b.hit = true;
			}
		}

		b.remove();

		b.vel.setZero();
	}

	@Override
	public Bullet create(Entityc owner, Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY, Teamc target) {
		Bullet bullet = Bullet.create();

		return Utils.anyOtherCreate(bullet, this, null, null, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target);
	}
}
