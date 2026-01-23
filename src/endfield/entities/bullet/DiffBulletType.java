package endfield.entities.bullet;

import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import endfield.content.Fx2;
import endfield.gen.DiffBullet;
import endfield.math.Mathm;
import endfield.util.Get;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Mover;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.graphics.Pal;

public class DiffBulletType extends BulletType {
	public float cont;

	public int damageType;

	public Color color = Pal.accent;

	public boolean pfin = true;

	public DiffBulletType() {
		this(1f, 1);
	}

	public DiffBulletType(float con, int damType) {
		cont = con / 2;
		damageType = damType;

		collides = collidesAir = collidesGround = collidesTiles = absorbable = hittable = keepVelocity = false;
		despawnEffect = hitEffect = Fx.none;

		speed = 0;
	}

	@Override
	public void update(Bullet b) {
		super.update(b);
		if (b instanceof DiffBullet d) {
			float r = splashDamageRadius * (1 - b.foutpow());
			Vars.indexer.allBuildings(b.x, b.y, r, bd -> {
				if (bd.team != b.team && bd.block != null && bd.block.targetable && Angles.within(b.rotation(), b.angleTo(bd), cont))
					d.healthcs.addUnique(bd);
			});
			Units.nearbyEnemies(b.team, b.x - r, b.y - r, r * 2, r * 2, u -> {
				if (u.type != null && u.type.targetable && b.within(u, r) && Angles.within(b.rotation(), b.angleTo(u), cont))
					d.healthcs.addUnique(u);
			});
			for (int i = 0; i < d.healthcs.size; i++) {
				Healthc hc = d.healthcs.get(i);
				if (hc != null && !hc.dead()) {
					if (!b.hasCollided(hc.id())) {

						switch (damageType) {
							case 1 -> hc.damage(damage);
							case 2 -> hc.damagePierce(damage);
							case 3 -> {
								if (hc.health() <= damage) hc.kill();
								else hc.health(hc.health() - damage);
							}
						}
						Fx2.diffHit.at(hc.getX(), hc.getY(), 0, color, hc);
						b.collided.add(hc.id());
					}
				}
			}
		}
	}

	@Override
	public void draw(Bullet b) {
		super.draw(b);
		float pin = (1 - b.foutpow());
		Lines.stroke(5 * (pfin ? pin : b.foutpow()), color);

		for (float i = b.rotation() - cont; i < b.rotation() + cont; i++) {
			float lx = Mathm.dx(b.x, splashDamageRadius * pin, i);
			float ly = Mathm.dy(b.y, splashDamageRadius * pin, i);
			Lines.lineAngle(lx, ly, i - 90, splashDamageRadius / (cont * 2) * pin);
			Lines.lineAngle(lx, ly, i + 90, splashDamageRadius / (cont * 2) * pin);
		}
	}

	@Override
	public Bullet create(Entityc owner, Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY, Teamc target) {
		DiffBullet bullet = DiffBullet.createDiff();
		if (bullet.healthcs.size > 0) bullet.healthcs.clear();
		return Get.anyOtherCreate(bullet, this, shooter, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target);
	}
}
