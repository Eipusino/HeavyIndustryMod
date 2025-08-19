package heavyindustry.world.blocks.production;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.blocks.production.Drill;

public class LaserBeamDrill extends Drill {
	public float shooterOffset = 12f;
	public float shooterExtendOffset = 1.8f;
	public float shooterMoveRange = 5.2f;
	public float shootY = 1.55f;

	public float moveScale = 60f;
	public float moveScaleRand = 20f;

	public float laserScl = 0.2f;

	public TextureRegion baseRegion;

	public TextureRegion laser;
	public TextureRegion laserEnd;

	public Color laserColor = Color.valueOf("f58349");
	public Color arcColor = Color.valueOf("f2d585");

	public float laserAlpha = 0.75f;
	public float laserAlphaSine = 0.2f;

	public float coolSpeed = 0.03f;

	public int particles = 25;

	public float particleLife = 40f, particleRad = 9.75f, particleStroke = 1.8f, particleLen = 4f;

	public LaserBeamDrill(String name) {
		super(name);
		solid = true;
		hasItems = true;
		drawMineItem = false;
		ambientSound = Sounds.minebeam;
		ambientSoundVolume = 0.18f;
		drillEffect = Fx.none;
		updateEffectChance = 0.2f;
	}

	@Override
	public void load() {
		super.load();

		baseRegion = Core.atlas.find(name + "-bottom");

		laser = Core.atlas.find(name + "-laser", "laser");
		laserEnd = Core.atlas.find(name + "-laser-end", "laser-end");
	}

	@Override
	public TextureRegion[] icons() {
		return teamRegion.found() ? new TextureRegion[]{baseRegion, topRegion, teamRegions[Team.sharded.id]} : new TextureRegion[]{region};
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = LaserBeamDrillBuild::new;
	}

	public class LaserBeamDrillBuild extends DrillBuild {
		public Rand rand = new Rand();

		@Override
		public void updateTile() {
			if (!(items.total() < itemCapacity && dominantItems > 0 && efficiency > 0))
				warmup = Mathf.lerpDelta(warmup, 0, coolSpeed);
			super.updateTile();
		}

		@Override
		public void draw() {
			Draw.rect(baseRegion, x, y);

			if (warmup > 0f) {
				drawMining();
			}

			Draw.z(Layer.blockOver - 4f);
			Draw.rect(topRegion, x, y);

			if (drawMineItem && dominantItem != null) {
				Draw.color(dominantItem.color);
				Draw.rect(itemRegion, x, y);
				Draw.color();
			}

			drawTeamTop();
			/*float s = 0.3f;
			float ts = 0.6f;

			Draw.rect(baseRegion, x, y);

			if (drawRim) {
				Draw.color(heatColor);
				Draw.alpha(warmup * ts * (1f - s + Mathf.absin(Time.time, 3f, s)));
				Draw.blend(Blending.additive);
				Draw.rect(rimRegion, x, y);
				Draw.blend();
				Draw.color();
			}

			float moveX = Mathf.sin(timeDrilled, moveScale + Mathf.randomSeed(id, -moveScaleRand, moveScaleRand), shooterMoveRange) + x;
			float moveY = Mathf.sin(timeDrilled + Mathf.randomSeed(id >> 1, moveScale), moveScale + Mathf.randomSeed(id >> 2, -moveScaleRand, moveScaleRand), shooterMoveRange) + y;

			for (int i : Mathf.signs) {
				Draw.rect(rotatorRegion, x + (-shooterOffset + warmup * shooterExtendOffset) * i, moveY, -90 * i);
				Draw.rect(rotatorRegion, moveX, y + (-shooterOffset + warmup * shooterExtendOffset) * i, 90 * i - 90);
			}

			float stroke = laserScl * Mathf.curve(warmup, 0, (items.total() < itemCapacity && dominantItems > 0 && efficiency > 0) ? efficiency : 1);
			Draw.mixcol(laserColor, Mathf.absin(4f, 0.6f));
			Draw.alpha(laserAlpha + Mathf.absin(8f, laserAlphaSine));
			Draw.blend(Blending.additive);
			Drawf.laser(laser, laserEnd, x + (-shooterOffset + warmup * shooterExtendOffset + shootY), moveY, x - (-shooterOffset + warmup * shooterExtendOffset + shootY), moveY, stroke);
			Drawf.laser(laser, laserEnd, moveX, y + (-shooterOffset + warmup * shooterExtendOffset + shootY), moveX, y - (-shooterOffset + warmup * shooterExtendOffset + shootY), stroke);

			Draw.color(arcColor);

			float sine = 1f + Mathf.sin(6f, 0.f);

			Fill.circle(moveX, moveY, stroke * 8f * sine);
			Lines.stroke(stroke / laserScl / 2f);
			Lines.circle(moveX, moveY, stroke * 12f * sine);

			rand.setSeed(id);
			float base = (Time.time / particleLife);
			for (int i = 0; i < particles; i++) {
				float fin = (rand.random(1f) + base) % 1f, fout = 1f - fin;
				float angle = rand.random(360f);
				float len = Mathf.randomSeed(rand.nextLong(), particleRad * 0.8f, particleRad * 1.1f) * Interp.pow2Out.apply(fin);
				Lines.lineAngle(moveX + Angles.trnsx(angle, len), moveY + Angles.trnsy(angle, len), angle, particleLen * fout * stroke / laserScl);
			}

			Draw.blend();
			Draw.reset();
			Draw.rect(topRegion, x, y);

			drawTeamTop();

			if (drawMineItem && dominantItem != null) {
				Draw.color(dominantItem.color);
				Draw.rect(itemRegion, x, y);
				Draw.color();
			}

			drawCracks();*/
		}

		public void drawMining() {
			float timeDrilled = Time.time / 2.5f;
			float
					moveX = Mathf.sin(timeDrilled, moveScale + Mathf.randomSeed(id, -moveScaleRand, moveScaleRand), shooterMoveRange) + x,
					moveY = Mathf.sin(timeDrilled + Mathf.randomSeed(id >> 1, moveScale), moveScale + Mathf.randomSeed(id >> 2, -moveScaleRand, moveScaleRand), shooterMoveRange) + y;

			float stroke = laserScl * warmup;
			Draw.mixcol(laserColor, Mathf.absin(4f, 0.6f));
			Draw.alpha(laserAlpha + Mathf.absin(8f, laserAlphaSine));
			Draw.blend(Blending.additive);
			Drawf.laser(laser, laserEnd, x + (-shooterOffset + warmup * shooterExtendOffset + shootY), moveY, x - (-shooterOffset + warmup * shooterExtendOffset + shootY), moveY, stroke);
			Drawf.laser(laser, laserEnd, moveX, y + (-shooterOffset + warmup * shooterExtendOffset + shootY), moveX, y - (-shooterOffset + warmup * shooterExtendOffset + shootY), stroke);

			Draw.color(dominantItem.color);

			float sine = 1f + Mathf.sin(6f, 0.1f);

			Lines.stroke(stroke / laserScl / 2f);
			Lines.circle(moveX, moveY, stroke * 12f * sine);
			Fill.circle(moveX, moveY, stroke * 8f * sine);

			rand.setSeed(id);
			float base = (Time.time / particleLife);
			for (int i = 0; i < particles; i++) {
				float fin = (rand.random(1f) + base) % 1f, fout = 1f - fin;
				float angle = rand.random(360f);
				float len = Mathf.randomSeed(rand.nextLong(), particleRad * 0.8f, particleRad * 1.1f) * Interp.pow2Out.apply(fin);
				Lines.lineAngle(moveX + Angles.trnsx(angle, len), moveY + Angles.trnsy(angle, len), angle, particleLen * fout * stroke / laserScl);
			}

			Draw.blend();
			Draw.reset();
		}
	}
}
