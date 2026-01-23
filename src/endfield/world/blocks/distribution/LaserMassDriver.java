package endfield.world.blocks.distribution;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.bullet.MassDriverBolt;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.distribution.MassDriver;

import static mindustry.Vars.world;

public class LaserMassDriver extends MassDriver {
	public float circularInterval = 32 * 2f, circularSize = 0.8f;

	public float lightStroke = 40f;
	public int divisions = 13;

	public Color[] colors = new Color[]{Pal.heal.cpy().a(.2f), Pal.heal.cpy().a(.5f), Pal.heal.cpy().mul(1.2f), Color.white};
	public Color laserColor = new Color(0xb2ffe1ff);

	public float strokeFrom = 2f, strokeTo = 0.5f;
	public float backLength = 7f, frontLength = 7f;
	public float width = 4f, oscScl = 2f, oscMag = 1.5f;

	public LaserMassDriver(String name) {
		super(name);
		clipSize = 500 * 8;
		itemCapacity = 120;
		range = 440f;
		reload = 3f;//launch delay
		size = 4;
		shake = knockback = 0;
		minDistribute = 2;//minimum number of launches
		shootEffect = receiveEffect = Fx.none;
		shootSound = Sounds.none;
		bulletLifetime = 10 * 60f;//bullet existence time
		bullet = new MassDriverBolt() {{
			damage = 150;
			pierce = true;
			despawnEffect = hitEffect = Fx.none;
		}};
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = LaserMassDriverBuilding::new;
	}

	public class LaserMassDriverBuilding extends MassDriverBuild {
		public int timer;
		public float prv, alpha;

		@Override
		public void updateTile() {
			super.updateTile();
			if (state == DriverState.idle || state == DriverState.accepting) {
				int times = 20;//the output speed multiplier is 2, and the total output speed is the default 200%
				for (int i = 0; i < times; i++) {
					dumpAccumulate();
				}
			}

			if (!linkValid()) return;
			alpha = Mathf.approachDelta(alpha, 1, 0.03f);
			//lightning
			int t = (int) (Time.time / 1000 * 60) % 6;
			if (t != timer) {
				timer = t;
				Building built = world.build(link);
				float angle = Mathf.angle(built.x - x, built.y - y);
				float offX = (float) (Math.sin(Mathf.degreesToRadians * angle) * size * 8 / 2f / 2f);
				float offY = (float) (Math.cos(Mathf.degreesToRadians * angle) * size * 8 / 2f / 2f);
				float offX2 = (float) (Math.sin(Mathf.degreesToRadians * angle) * size * 8 / 2f / 2f / 2f);
				float offY2 = (float) (Math.cos(Mathf.degreesToRadians * angle) * size * 8 / 2f / 2f / 2f);

				Vec2 vec21 = new Vec2(built.x + offX, built.y + offY);
				Vec2 vec22 = new Vec2(built.x - offX, built.y - offY);
				Vec2 vec23 = new Vec2(built.x + offX2, built.y + offY2);
				Vec2 vec24 = new Vec2(built.x - offX2, built.y - offY2);

				Fx.chainLightning.at(x + offX, y + offY, rotation, Color.pink, vec21);
				Fx.chainLightning.at(x - offX, y - offY, rotation, Color.pink, vec22);
				Fx.chainLightning.at(x + offX2, y + offY2, rotation, Color.pink, vec23);
				Fx.chainLightning.at(x - offX2, y - offY2, rotation, Color.pink, vec24);
			}

			if (prv != link) {
				alpha = 0;
				prv = link;
			}
		}

		@Override
		public void draw() {
			super.draw();
			if (!linkValid()) return;
			Building built = world.build(link);

			//laser
			Draw.z(Layer.effect);
			float baseLen = Mathf.dst(built.x - x, built.y - y);
			float rot = Mathf.angle(built.x - x, built.y - y);

			for (int i = 0; i < colors.length; i++) {
				Draw.color(Tmp.c1.set(colors[i]).mul(1f + Mathf.absin(Time.time, 1f, 0.1f)));

				float colorFin = i / (float) (colors.length - 1);
				float baseStroke = Mathf.lerp(strokeFrom, strokeTo, colorFin);
				float stroke = (width + Mathf.absin(Time.time, oscScl, oscMag)) * baseStroke * efficiency;

				Lines.stroke(stroke);
				Lines.lineAngle(x, y, rot, baseLen - frontLength, false);

				//back ellipse
				Drawf.flameFront(x, y, divisions, rot + 180f, backLength, stroke / 2f);

				//front ellipse
				Tmp.v1.trnsExact(rot, baseLen - frontLength);
				Drawf.flameFront(x + Tmp.v1.x, y + Tmp.v1.y, divisions, rot, frontLength, stroke / 2f);
			}

			Drawf.light(x, y, built.x, built.y, lightStroke, lightColor, 0.7f);
			Draw.reset();

			Lines.stroke(1.7f);
			Draw.color(laserColor);
			float fin = (Time.time % 60f) / 60f;
			int amount = (int) (baseLen / (circularInterval)) + 2;
			float len = baseLen / amount;

			for (int i = 0; i < amount; i++) {
				float scl = 1;
				if (i == 0) scl = fin;
				else if (i == amount - 1) scl = 1 - fin;

				float fx = (float) (x + (i + fin) * len * Math.cos(Mathf.degreesToRadians * rot));
				float fy = (float) (y + (i + fin) * len * Math.sin(Mathf.degreesToRadians * rot));

				Draw.alpha(alpha);
				Lines.ellipse(fx, fy, scl * circularSize, size * 8 / 3f, size * 8 / 2f, rotation);
			}
			Draw.reset();
		}
	}
}
