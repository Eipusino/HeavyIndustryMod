package endfield.world.blocks.defense;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.math.Mathf;
import arc.util.Align;
import arc.util.Time;
import mindustry.Vars;
import mindustry.graphics.Layer;
import mindustry.ui.Fonts;
import mindustry.world.blocks.defense.Wall;

import static endfield.Vars2.boardTimeTotal;

public class DPSWall extends Wall {
	public DPSWall(String name) {
		super(name);

		update = true;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = DPSWallBuild::new;
	}

	public class DPSWallBuild extends WallBuild {
		public float totalDamage = 0f, hits = 0f, firstHitTime = 0f, lastHitTime = 0f, showBoardTime = 0f;

		@Override
		public void health(float health) {
			super.health(health);

			damage(health);
		}

		@Override
		public void damage(float damage) {
			totalDamage += damage;
			hits += 1;
			if (firstHitTime == 0) {
				firstHitTime = Time.time;
			}
			showBoardTime = boardTimeTotal;
			lastHitTime = Time.time;
		}

		@Override
		public void updateTile() {
			super.updateTile();

			showBoardTime = Math.max(showBoardTime - Time.delta, 0);
			if (showBoardTime == 0 && totalDamage > 0) {
				totalDamage = 0f;
				hits = 0f;
				firstHitTime = 0f;
				lastHitTime = 0f;
				showBoardTime = 0f;
			}
		}

		@Override
		public void draw() {
			super.draw();

			if (showBoardTime > 0) {
				Font font = Fonts.def;
				Color color = Color.yellow.cpy();
				float fontSize = 12f / 60f;
				float gap = Vars.mobile ? fontSize / 0.04f : fontSize / 0.06f;
				float dx = x - 13f;
				float dy = y + (Vars.mobile ? 29f : 17f);

				float gameDuration = lastHitTime - firstHitTime;
				float realDuration = gameDuration / 60;
				float damage = totalDamage;
				float dps = damage / (realDuration == 0 ? 1 : realDuration);

				Draw.z(Layer.weather + 1);
				color.a = Math.min(showBoardTime / boardTimeTotal * 3, 1);
				font.draw(Core.bundle.format("text.dps-info-hits", hits), dx, (dy -= gap), color, fontSize, false, Align.left);
				font.draw(Core.bundle.format("text.dps-info-damage", Mathf.round(damage)), dx, (dy -= gap), color, fontSize, false, Align.left);
				font.draw(Core.bundle.format("text.dps-info-dps", Mathf.round(dps)), dx, dy - gap, color, fontSize, false, Align.left);

				Draw.reset();
			}
		}
	}
}
