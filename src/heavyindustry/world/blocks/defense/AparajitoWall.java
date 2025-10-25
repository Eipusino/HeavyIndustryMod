package heavyindustry.world.blocks.defense;

import arc.graphics.Color;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.Wall;

public class AparajitoWall extends Wall {
	public float hitHealAmount = 0.05f, hitHealReload = 0.5f * 60;
	public float healTimer = 3 * 60f, healRadio = 0.03f;

	public Color healColor = Pal.heal;

	public AparajitoWall(String name) {
		super(name);
		update = true;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = AparajitoWallBuild::new;
	}

	public class AparajitoWallBuild extends WallBuild {
		public boolean wasInReload = false;
		public float timerHit = 0f, timerHeal = 0f;

		@Override
		public boolean damaged() {
			return false;
		}

		@Override
		public void updateTile() {
			if (wasInReload) {
				timerHit -= Time.delta;
			}
			if (timerHit <= 0) {
				wasInReload = false;
			}

			timerHeal = Math.max(0, timerHeal - Time.delta);
			if (timerHeal <= 0 && health < maxHealth - 1e-5f) {
				if (!Vars.net.client()) {
					heal(maxHealth() * healRadio / 60f * Time.delta);
				}
				if (timer.get(60)) {
					Fx.healBlockFull.at(x, y, 0, healColor, block);
				}
			}
		}

		@Override
		public void damage(float damage) {
			timerHeal = healTimer;
			if (!wasInReload) {
				if (!Vars.net.client()) {
					heal(maxHealth() * hitHealAmount);
				}
				Fx.healBlockFull.at(x, y, 0, healColor, block);
				timerHit = hitHealReload;
				wasInReload = true;
			}
			super.damage(damage);
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.bool(wasInReload);
			write.f(timerHit);
			write.f(timerHeal);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			wasInReload = read.bool();
			timerHit = read.f();
			timerHeal = read.f();
		}
	}
}
