package heavyindustry.entities.bullet;

import arc.audio.Sound;
import arc.math.Angles;
import arc.math.geom.Position;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.world.blocks.ControlBlock;

public class CtrlMissileBulletType extends BasicBulletType {
	public boolean autoHoming = false;

	public Sound loopSound = Sounds.missileTrail;
	public float loopSoundVolume = 0.1f;

	public CtrlMissileBulletType() {
		this(1, 1, "missile");
	}

	public CtrlMissileBulletType(String bulletSprite) {
		this(1, 1, bulletSprite);
	}

	public CtrlMissileBulletType(float speed, float damage, String bulletSprite) {
		super(speed, damage, bulletSprite);
		homingPower = 2.5f;
		homingRange = 8 * 8;
		trailWidth = 3;
		trailLength = 7;
		lifetime = 60 * 1.7f;
		buildingDamageMultiplier = 0.8f;
		hitSound = despawnSound = Sounds.bang;
		absorbable = false;
		keepVelocity = false;
		reflectable = false;
	}

	public void lookAt(float angle, Bullet b) {
		b.rotation(Angles.moveToward(b.rotation(), angle, homingPower * Time.delta));
	}

	public void lookAt(float x, float y, Bullet b) {
		lookAt(b.angleTo(x, y), b);
	}

	@Override
	public void update(Bullet b) {
		super.update(b);

		if (!Vars.headless && loopSound != Sounds.none) {
			Vars.control.sound.loop(loopSound, b, loopSoundVolume);
		}
	}

	@Override
	public void updateHoming(Bullet b) {
		if (homingPower > 0.0001f && b.time >= homingDelay) {
			float realAimX = b.aimX < 0 ? b.data instanceof Position p ? p.getX() : b.x : b.aimX;
			float realAimY = b.aimY < 0 ? b.data instanceof Position p ? p.getY() : b.y : b.aimY;

			Teamc target;
			if (heals()) {
				target = Units.closestTarget(null, realAimX, realAimY, homingRange,
						e -> e.checkTarget(collidesAir, collidesGround) && e.team != b.team && !b.hasCollided(e.id),
						t -> collidesGround && (t.team != b.team || t.damaged()) && !b.hasCollided(t.id)
				);
			} else {
				if (b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)) {
					target = b.aimTile.build;
				} else {
					target = Units.closestTarget(b.team, realAimX, realAimY, homingRange, e -> e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id), t -> collidesGround && !b.hasCollided(t.id));
				}
			}

			if (reflectable) return;
			if (target != null && autoHoming) {
				b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), homingPower * Time.delta));
			} else {
				Unit shooter = null;
				if (b.owner instanceof Unit unit) shooter = unit;
				if (b.owner instanceof ControlBlock control) shooter = control.unit();
				if (shooter != null) {
					if (shooter.isPlayer()) {
						lookAt(shooter.aimX, shooter.aimY, b);
					} else {
						if (b.data instanceof Position p) {
							lookAt(p.getX(), p.getY(), b);
						} else {
							lookAt(realAimX, realAimY, b);
						}
					}
				}
			}
		}
	}
}
