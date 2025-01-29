package heavyindustry.entities.bullet;

import arc.*;
import arc.audio.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.*;

public class CtrlMissileBulletType extends BasicBulletType {
	public String sprite;
	public float width, height;
	public boolean autoHoming = false, low = false;

	public Sound loopSound = Sounds.missileTrail;
	public float loopSoundVolume = 0.1f;

	public CtrlMissileBulletType(String spr, float wid, float hei) {
		sprite = spr;
		width = wid;
		height = hei;
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
			float realAimX = b.aimX < 0 ? b.data instanceof Position ? ((Position) b.data).getX() : b.x : b.aimX;
			float realAimY = b.aimY < 0 ? b.data instanceof Position ? ((Position) b.data).getY() : b.y : b.aimY;

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
					if (shooter.isPlayer()) lookAt(shooter.aimX, shooter.aimY, b);
					else {
						if (b.data instanceof Position p) lookAt(p.getX(), p.getY(), b);
						else lookAt(realAimX, realAimY, b);
					}
				}
			}
		}
	}

	@Override
	public void draw(Bullet b) {
		super.draw(b);
		Draw.z(low ? Layer.flyingUnitLow : Layer.flyingUnit);
		if (width > 0 && height > 0) Draw.rect(Core.atlas.find(sprite), b.x, b.y, width, height, b.rotation() - 90);
		else Draw.rect(Core.atlas.find(sprite), b.x, b.y, b.rotation() - 90);
		Draw.reset();
	}
}
