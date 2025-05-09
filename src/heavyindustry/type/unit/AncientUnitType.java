package heavyindustry.type.unit;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.graphics.HPal;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;

public class AncientUnitType extends ExtraUnitType {
	public AncientUnitType(String name) {
		super(name);

		outlineColor = Pal.darkOutline;
		healColor = HPal.ancientLightMid;
		lightColor = HPal.ancientLightMid;
	}

	public void addEngine(float x, float y, float relativeRot, float rad, boolean flipAdd) {
		if (flipAdd) {
			for (int i : Mathf.signs) {
				engines.add(new AncientEngine(x * i, y, rad, -90 + relativeRot * i, 0));
				engines.add(new AncientEngine(x * i, y, rad * 1.85f, -90 + relativeRot * i, Mathf.random(2f)).a(0.3f));
			}
		} else {
			engines.add(new AncientEngine(x, y, rad, -90 + relativeRot, 0));
			engines.add(new AncientEngine(x, y, rad * 1.85f, -90 + relativeRot, Mathf.random(2f)).a(0.3f));
		}
	}

	public static class AncientEngine extends UnitEngine {
		//public Color engineColor;
		public float forceZ = -1;
		public float phaseOffset = Mathf.random(5);
		public float alphaBase = 0.8f;
		public float scl = 0.825f;
		public float sizeSclPlus = 0.4f;
		public float sizeSclMin = 0.95f;
		public float alphaSclMin = 0.88f;

		public AncientEngine(float x, float y, float radius, float rotation) {
			super(x, y, radius, rotation);
		}

		public AncientEngine(float x, float y, float radius, float rotation, float phaOff) {
			this(x, y, radius, rotation);
			phaseOffset = phaOff;
		}

		public AncientEngine(float x, float y, float radius, float rotation, float alpBas, float sizSclPlu, float sizSclMin) {
			this(x, y, radius, rotation);
			alphaBase = alpBas;
			sizeSclPlus = sizSclPlu;
			sizeSclMin = sizSclMin;
		}

		public AncientEngine a(float f) {
			alphaBase = f;
			return this;
		}

		@Override
		public void draw(Unit unit) {
			UnitType type = unit.type;
			if (unit.vel.len2() > 0.001f) {
				float z = Draw.z();

				if (forceZ > 0) Draw.z(forceZ);

				float rot = unit.rotation - 90.0F;
				Color c = type.engineColor == null ? unit.team.color : type.engineColor;
				Tmp.v1.set(x, y).rotate(rot).add(unit.x, unit.y);
				float ex = Tmp.v1.x;
				float ey = Tmp.v1.y;
				float rad = Mathf.curve(unit.vel.len2(), 0.001f, type.speed * type.speed) * radius * (sizeSclMin + Mathf.absin(Time.time + phaseOffset, scl, sizeSclPlus));
				float a = alphaBase * (alphaSclMin + Mathf.absin(Time.time * 1.3f - phaseOffset, scl, 0.13f));

				Draw.blend(Blending.additive);
				Draw.alpha(a);
				Tmp.c2.set(c).a(a);
				Fill.light(ex, ey, Lines.circleVertices(rad), rad, Tmp.c2, Color.clear);
				Tmp.c1.set(Tmp.c2).lerp(Color.white, Mathf.absin(Time.time * 1.1f + phaseOffset, scl * 1.12512f, 0.14f));
				Draw.color(Tmp.c1);
				Draw.alpha(a * 0.5f);
				Fill.light(ex, ey, Lines.circleVertices(rad), rad * 1.125f, Tmp.c1, Color.clear);

				Drawf.light(ex, ey, rad * 1.9f, Tmp.c1, a);

				Draw.reset();
				Draw.blend();

				Draw.z(z);
			}
		}
	}
}
