package heavyindustry.entities.part;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import heavyindustry.graphics.*;
import mindustry.entities.part.*;
import mindustry.graphics.*;

import static heavyindustry.HIVars.*;
import static mindustry.Vars.*;

public class ArcCharge extends DrawPart {
	protected static final Rand rand = new Rand();
	protected static final Vec2 tr = new Vec2(), tr2 = new Vec2();
	public float size = 13.75f;
	public Color color;
	public PartProgress progress;
	public float chargeCircleFrontRad = 18;
	public float chargeCircleBackRad = 8;
	public Interp curve = Interp.pow3;
	public float lightningCircleInScl = 0.85f, lightningCircleOutScl = 1.1f;
	public Interp lightningCircleCurve = Interp.pow3Out;
	public Floatf<PartParams> chargeY = t -> 1;
	public Floatf<PartParams> shootY = t -> 1;

	@Override
	public void draw(PartParams params) {
		Draw.z(Layer.effect - 1f);

		Draw.color(color);
		float x = params.x, y = params.y, rotation = params.rotation;

		float fin = progress.getClamp(params);

		Lines.stroke(3f * Mathf.curve(fin, 0.1f, 0.2f));
		tr2.trns(rotation, chargeY.get(params));
		tr.trns(rotation, shootY.get(params));
		Tmp.v2.set(tr).sub(tr2);
		float length = Tmp.v2.len();
		Tmp.v2.set(tr).add(tr2);

		Drawn.circlePercent(x + Tmp.v2.x / 2, y + Tmp.v2.y / 2, length / 2f, Mathf.curve(fin, 0.1f, 1f), rotation - Mathf.curve(fin, 0.1f, 1f) * 180f - 180f);

		float scl = size * tilesize * lightningCircleCurve.apply(fin);
		float fin_9 = Mathf.curve(fin, 0.95f, 1f);
		float sclSign = size * tilesize * lightningCircleCurve.apply(fin_9);
		Lines.stroke(fin * lightningCircleInScl * 4.5f);
		Lines.circle(x, y, scl * lightningCircleInScl);

		TextureRegion arrowRegion = Core.atlas.find(name("jump-gate-arrow"));

		for (int i = 0; i < 4; i++) {
			float rot = Time.time + i * 90;
			Tmp.v1.trns(rot, sclSign * lightningCircleInScl + Lines.getStroke() * 2f).add(x, y);
			Draw.rect(arrowRegion, Tmp.v1.x, Tmp.v1.y, arrowRegion.width * Draw.scl * fin_9, arrowRegion.height * Draw.scl * fin_9, rot + 90);
		}

		TextureRegion pointerRegion = Core.atlas.find(name("jump-gate-pointer"));

		Lines.stroke(fin * lightningCircleOutScl * 4.5f);
		Lines.circle(x, y, scl * lightningCircleOutScl);
		for (int i = 0; i < 4; i++) {
			float rot = -Time.time * 1.5f + i * 90;
			Tmp.v1.trns(rot, sclSign * lightningCircleOutScl + Lines.getStroke() * 3f).add(x, y);
			Draw.rect(pointerRegion, Tmp.v1.x, Tmp.v1.y, pointerRegion.width * Draw.scl * fin_9, pointerRegion.height * Draw.scl * fin_9, rot + 90);
		}

		fin = Mathf.curve(fin, 0.25f, 1f);

		if (fin < 0.01f) return;
		Fill.circle(x + tr2.x, y + tr2.y, fin * chargeCircleBackRad);
		Lines.stroke(fin * 3f - 1f);
		Drawn.circlePercentFlip(x + tr2.x, y + tr2.y, fin * (chargeCircleBackRad + 5), Time.time, 20f);
		Draw.color(Color.white);
		Fill.circle(x + tr2.x, y + tr2.y, fin * chargeCircleBackRad * 0.7f);

		float cameraFin = (1 + 2 * Drawn.cameraDstScl(x + tr.x, y + tr.y, mobile ? 200 : 320)) / 3f;
		float triWidth = fin * chargeCircleFrontRad / 3.5f * cameraFin;

		Draw.color(color);
		for (int i : Mathf.signs) {
			Fill.tri(x + tr.x, y + tr.y + triWidth, x + tr.x, y + tr.y - triWidth, x + tr.x + i * cameraFin * chargeCircleFrontRad * (23 + Mathf.absin(10f, 0.75f)) * (fin * 1.25f + 1f), y + tr.y);
			Drawf.tri(x + tr.x, y + tr.y, (fin + 1) / 2 * chargeCircleFrontRad / 1.5f, chargeCircleFrontRad * 10 * fin, i * 90 + Time.time * 1.25f);
			Drawf.tri(x + tr.x, y + tr.y, (fin + 1) / 2 * chargeCircleFrontRad / 2f, chargeCircleFrontRad * 6.5f * fin, i * 90 - Time.time);
		}

		Fill.circle(x + tr.x, y + tr.y, fin * chargeCircleFrontRad);
		Drawn.circlePercentFlip(x + tr.x, y + tr.y, fin * (chargeCircleFrontRad + 5), Time.time, 20f);
		Draw.color(Color.white);
		Fill.circle(x + tr.x, y + tr.y, fin * chargeCircleFrontRad * 0.7f);
	}

	@Override
	public void load(String name) {}
}
