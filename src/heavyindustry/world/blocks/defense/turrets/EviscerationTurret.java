package heavyindustry.world.blocks.defense.turrets;

import arc.Core;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Strings;
import arc.util.Tmp;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.draw.DrawTurret;
import mindustry.world.meta.Env;

public class EviscerationTurret extends PowerTurret {
	protected Interp pow = Interp.pow5In;

	public EviscerationTurret(String name) {
		super(name);

		envEnabled = Env.any;

		drawer = new DrawTurret() {
			@Override
			public void drawHeat(Turret block, TurretBuild build) {
				if (build.heat <= 0.00001f || !heat.found()) return;

				float r = Interp.pow2Out.apply(build.heat);
				float g = Interp.pow3In.apply(build.heat) + ((1f - Interp.pow3In.apply(build.heat)) * 0.12f);
				float b = pow.apply(build.heat);
				float a = Interp.pow2Out.apply(build.heat);
				Tmp.c1.set(r, g, b, a);

				Drawf.additive(heat, Tmp.c1, build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.drawrot(), Layer.turretHeat);
			}
		};
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("hi-reload", (ChaosTurretBuild tile) -> new Bar(
				() -> Core.bundle.format("bar.hi-reload", Strings.autoFixed(Mathf.clamp(tile.reloadCounter / reload) * 100f, 2)),
				() -> tile.team.color,
				() -> Mathf.clamp(tile.reloadCounter / reload)
		));
	}

	public class ChaosTurretBuild extends PowerTurretBuild {
		protected Bullet bullet;

		@Override
		public void updateTile() {
			super.updateTile();

			if (active()) {
				heat = 1f;
				curRecoil = 1f;
				wasShooting = true;
			}
		}

		@Override
		public boolean shouldTurn() {
			return super.shouldTurn() && !active() && !charging();
		}

		@Override
		protected void updateCooling() {
			if (!active() && !charging()) super.updateCooling();
		}

		@Override
		protected void updateReload() {
			if (!active() && !charging()) super.updateReload();
		}

		@Override
		protected void handleBullet(Bullet bullet, float offsetX, float offsetY, float angleOffset) {
			if (bullet != null) {
				this.bullet = bullet;
			}
		}

		public boolean active() {
			return bullet != null && bullet.isAdded();
		}
	}
}
