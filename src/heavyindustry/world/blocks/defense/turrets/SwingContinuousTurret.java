package heavyindustry.world.blocks.defense.turrets;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import arc.util.io.*;
import heavyindustry.math.*;
import mindustry.entities.part.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.draw.*;

public class SwingContinuousTurret extends ContinuousTurret {
	public float rotateSpeedAccel = 0.5f, rotateSpeedDrag = 0.3f;
	public float aimChangeSpeedAccel = 0.5f, aimChangeSpeedDrag = 0.3f;
	public float rotateSpeedfSpeed = 0.1f;

	public SwingContinuousTurret(String name) {
		super(name);

		drawer = new DrawSwingTurret();
	}

	public class SwingContinuousTurretBuild extends ContinuousTurretBuild {
		public float realRotateSpeed, rotateSpeedf;
		public boolean slowing;

		@Override
		public void updateTile() {
			super.updateTile();

			realRotateSpeed *= Math.max(1f - rotateSpeedDrag * Time.delta, 0);
			rotation = Mathf.mod(rotation + realRotateSpeed * Time.delta, 360f);

			rotateSpeedf = Mathf.approachDelta(rotateSpeedf, (slowing && isShooting() && hasAmmo()) ? (1f - Math.abs(realRotateSpeed / rotateSpeed)) : 0f, rotateSpeedfSpeed);
		}

		@Override
		protected void turnToTarget(float targetRot) {
			float targetSpeed = rotateSpeed * Mathm.angleMoveDirection(rotation, targetRot);
			realRotateSpeed = Mathf.approachDelta(realRotateSpeed, targetSpeed, rotateSpeedAccel * efficiency);

			slowing = Mathf.sign(targetSpeed) != Mathf.sign(realRotateSpeed);

		}

		protected void updateBullet(BulletEntry entry) {
			if (!(entry instanceof SwingBulletEntry s)) return;

			float
					bulletX = x + Angles.trnsx(rotation - 90, shootX + entry.x, shootY + entry.y),
					bulletY = y + Angles.trnsy(rotation - 90, shootX + entry.x, shootY + entry.y),
					angle = rotation + entry.rotation;

			entry.bullet.rotation(angle);
			entry.bullet.set(bulletX, bulletY);

			//target length of laser
			float shootLength = Math.min(dst(targetPos), range);
			//current length of laser
			float curLength = dst(entry.bullet.aimX, entry.bullet.aimY);
			float resultLength;
			if (aimChangeSpeed == Float.POSITIVE_INFINITY) {
				resultLength = Math.max(shootLength, shootY);
			} else {
				//update aim change speed
				float targetSpeed = aimChangeSpeed * Mathf.sign(shootLength - curLength);
				s.aimChangeSpeed = Mathf.approachDelta(s.aimChangeSpeed, targetSpeed, aimChangeSpeedAccel * efficiency);
				s.aimChangeSpeed *= Math.max(1f - aimChangeSpeedDrag * Time.delta, 0);
				//resulting length of the bullet (smoothed)
				resultLength = Math.max(curLength + s.aimChangeSpeed, shootY);
			}
			//actual aim end point based on length
			Tmp.v1.trns(rotation, lastLength = resultLength).add(x, y);

			entry.bullet.aimX = Tmp.v1.x;
			entry.bullet.aimY = Tmp.v1.y;

			if (isShooting() && hasAmmo()) {
				entry.bullet.time = entry.bullet.lifetime * entry.bullet.type.optimalLifeFract * shootWarmup;
				entry.bullet.keepAlive = true;
			}
		}

		@Override
		protected void handleBullet(Bullet bullet, float offsetX, float offsetY, float angleOffset) {
			if (bullet != null) {
				bullets.add(new SwingBulletEntry(bullet, offsetX, offsetY, angleOffset, 0f));

				//make sure the length updates to the last set value
				Tmp.v1.trns(rotation, shootY + lastLength).add(x, y);
				bullet.aimX = Tmp.v1.x;
				bullet.aimY = Tmp.v1.y;
			}
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.f(realRotateSpeed);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			if (revision >= 4) realRotateSpeed = read.f();
		}

		@Override
		public byte version() {
			return 4;
		}
	}

	public static class SwingBulletEntry extends BulletEntry {
		public float aimChangeSpeed;

		public SwingBulletEntry(Bullet bullet, float x, float y, float rotation, float life) {
			super(bullet, x, y, rotation, life);
		}
	}

	/** Passes the rotation speed fract into the life param for draw parts. */
	public static class DrawSwingTurret extends DrawTurret {
		public DrawSwingTurret(String basePrefix) {
			this.basePrefix = basePrefix;
		}

		public DrawSwingTurret() {}

		@Override
		public void draw(Building build) {
			if (!(build instanceof SwingContinuousTurretBuild tb) || !(build.block instanceof SwingContinuousTurret turret))
				return;

			Draw.rect(base, build.x, build.y);
			Draw.color();

			Draw.z(Layer.turret - 0.5f);

			Drawf.shadow(preview, build.x + tb.recoilOffset.x - turret.elevation, build.y + tb.recoilOffset.y - turret.elevation, tb.drawrot());

			Draw.z(Layer.turret);

			drawTurret(turret, tb);
			drawHeat(turret, tb);

			if (parts.size > 0) {
				if (outline.found()) {
					//draw outline under everything when parts are involved
					Draw.z(Layer.turret - 0.01f);
					Draw.rect(outline, build.x + tb.recoilOffset.x, build.y + tb.recoilOffset.y, tb.drawrot());
					Draw.z(Layer.turret);
				}

				float progress = tb.progress();

				//TODO no smooth reload
				var params = DrawPart.params.set(build.warmup(), 1f - progress, 1f - progress, tb.heat, tb.curRecoil, tb.charge, tb.x + tb.recoilOffset.x, tb.y + tb.recoilOffset.y, tb.rotation);
				params.life = tb.rotateSpeedf;

				for (DrawPart part : parts) {
					part.draw(params);
				}
			}
		}
	}
}
