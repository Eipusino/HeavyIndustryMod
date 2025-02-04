package heavyindustry.world.blocks.defense.turrets;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.entities.bullet.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

/** Realize muzzle rotation and firing of Minigun. */
public class MinigunTurret extends ItemTurret {
	public float windupSpeed = 0.2f, windDownSpeed = 0.1f, minFiringSpeed = 3f, logicSpeedScl = 0.25f, maxSpeed = 30f;
	public float barX, barY, barStroke, barLength;
	public float barWidth = 1.5f, barHeight = 0.75f;

	public MinigunTurret(String name) {
		super(name);

		drawer = new DrawMinigunTurret();
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.remove(Stat.reload);
		stats.add(Stat.reload, Strings.autoFixed(minFiringSpeed / 90f * 60f * shoot.shots, 2) + " - " + Strings.autoFixed(maxSpeed / 90f * 60f * shoot.shots, 2) + StatUnit.perSecond.localized());
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("hi-minigun-speed", (MinigunTurretBuild tile) -> new Bar(() -> Core.bundle.format("bar.hi-minigun-speed", Strings.autoFixed(tile.speedf() * 100f, 2)), tile::barColor, tile::speedf));
	}

	public class MinigunTurretBuild extends ItemTurretBuild {
		protected float[] heats = {0f, 0f, 0f, 0f};
		protected float spinSpeed, spin;

		public Color barColor() {
			return spinSpeed > minFiringSpeed ? team.color : team.palette[2];
		}

		@Override
		public void updateTile() {
			boolean notShooting = !hasAmmo() || !isShooting() || !isActive();
			if (notShooting) {
				spinSpeed = Mathf.approachDelta(spinSpeed, 0, windDownSpeed);
			}

			if (spinSpeed > getMaxSpeed()) {
				spinSpeed = Mathf.approachDelta(spinSpeed, getMaxSpeed(), windDownSpeed);
			}

			for (int i = 0; i < 4; i++) {
				heats[i] = Math.max(heats[i] - Time.delta / cooldownTime, 0);
			}

			super.updateTile();
		}

		@Override
		protected void updateShooting() {
			if (!hasAmmo()) return;

			spinSpeed = Mathf.approachDelta(spinSpeed, getMaxSpeed(), windupSpeed * peekAmmo().reloadMultiplier * timeScale);

			if (reloadCounter >= 90 && spinSpeed > minFiringSpeed) {
				BulletType type = peekAmmo();

				shoot(type);

				reloadCounter = spin % 90;

				heats[Mathf.floor(spin - 90) % 360 / 90] = 1f;
			}
		}

		@Override
		protected void updateReload() {
			boolean shooting = hasAmmo() && isShooting() && isActive();
			float multiplier = hasAmmo() ? peekAmmo().reloadMultiplier : 1f;
			float add = spinSpeed * multiplier * Time.delta;
			if (shooting && coolant != null && coolant.efficiency(this) > 0 && efficiency > 0) {
				float capacity = coolant instanceof ConsumeLiquidFilter filter ? filter.getConsumed(this).heatCapacity : 1f;
				coolant.update(this);
				add += coolant.amount * edelta() * capacity * coolantMultiplier;

				if (Mathf.chance(0.06 * coolant.amount)) {
					coolEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
				}
			}
			spin += add;
			reloadCounter += add;
		}

		protected float getMaxSpeed() {
			return maxSpeed * (!isControlled() && logicControlled() && logicShooting ? logicSpeedScl : 1f);
		}

		protected float speedf() {
			return spinSpeed / maxSpeed;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(spinSpeed);
			write.f(spin % 360f);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			if (revision >= 2) {
				spinSpeed = read.f();

				if (revision >= 3) {
					spin = read.f();
				}
			}
		}

		@Override
		public byte version() {
			return 3;
		}
	}

	public static class DrawMinigunTurret extends DrawTurret {
		public TextureRegion barrel, barrelOutline;

		@Override
		public void getRegionsToOutline(Block block, Seq<TextureRegion> out) {
			super.getRegionsToOutline(block, out);
			out.add(barrel);
		}

		@Override
		public void load(Block block) {
			super.load(block);

			barrel = Core.atlas.find(block.name + "-barrel");
			barrelOutline = Core.atlas.find(block.name + "-barrel-outline");
		}

		@Override
		public void drawTurret(Turret block, TurretBuild build) {
			if (!(block instanceof MinigunTurret bl && build instanceof MinigunTurretBuild bu)) return;

			Vec2 v = Tmp.v1;

			Draw.z(Layer.turret - 0.01f);
			Draw.rect(outline, build.x + bu.recoilOffset.x, build.y + bu.recoilOffset.y, build.drawrot());
			for (int i = 0; i < 4; i++) {
				Draw.z(Layer.turret - 0.01f);
				v.trns(bu.rotation - 90f, bl.barWidth * Mathf.cosDeg(bu.spin - 90 * i), bl.barHeight * Mathf.sinDeg(bu.spin - 90 * i)).add(bu.recoilOffset);
				Draw.rect(barrelOutline, bu.x + v.x, bu.y + v.y, bu.drawrot());
				Draw.z(Layer.turret - 0.005f - Mathf.sinDeg(bu.spin - 90 * i) / 1000f);
				Draw.rect(barrel, bu.x + v.x, bu.y + v.y, bu.drawrot());
				if (bu.heats[i] > 0.001f) {
					Drawf.additive(heat, bl.heatColor.write(Tmp.c1).a(bu.heats[i]), bu.x + v.x, bu.y + v.y, bu.drawrot(), Draw.z());
				}
			}

			Draw.z(Layer.turret);
			super.drawTurret(block, build);

			if (bu.speedf() > 0.0001f) {
				Draw.color(bu.barColor());
				Lines.stroke(bl.barStroke);
				for (int i = 0; i < 2; i++) {
					v.trns(bu.drawrot(), bl.barX * Mathf.signs[i], bl.barY).add(bu.recoilOffset);
					Lines.lineAngle(bu.x + v.x, bu.y + v.y, bu.rotation, bl.barLength * Mathf.clamp(bu.speedf()), false);
				}
			}
		}

		@Override
		public void drawHeat(Turret block, TurretBuild build) {
			//Don't
		}
	}
}
