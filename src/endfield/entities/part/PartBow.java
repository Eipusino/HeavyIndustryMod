package endfield.entities.part;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import endfield.math.Mathm;
import mindustry.entities.part.DrawPart;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

public class PartBow extends DrawPart {
	public float bowMoveY = -40 + 8, bowFY = -8;
	public float bowWidth = 36, bowHeight = 15, bowTk = 8, turretTk = 15;
	public float visRad = 12;

	public Color color = Pal.surge;
	public String arrowSp = null;

	public PartProgress progressWarmup = PartProgress.warmup;
	public PartProgress progressReload = PartProgress.reload;

	@Override
	public void draw(PartParams params) {
		float warmup = Mathm.clamp(progressWarmup.get(params));
		float p = 1f - Mathm.clamp(progressReload.get(params));
		float rot = params.rotation - 90f;
		if (warmup < 0.001) return;
		float rp = Math.min(p * 1.2f, 1);
		float bx = params.x + Angles.trnsx(rot, 0f, bowHeight), by = params.y + Angles.trnsy(rot, 0f, bowHeight);
		float rx = bx + Angles.trnsx(rot, 0f, bowMoveY * rp + bowFY), ry = by + Angles.trnsy(rot, 0f, bowMoveY * rp + bowFY);
		Draw.z(Layer.bullet - 5f + 15f * warmup);
		Draw.color(color);
		Fill.circle(rx, ry, 3 * warmup);
		Lines.stroke(3 * warmup);
		for (int i : new int[]{-1, 1}) {
			Tmp.v2.set(turretTk * i, bowHeight).rotate(rot);
			Tmp.v3.set(turretTk * i, bowHeight + bowTk).rotate(rot);
			float x1 = params.x + Tmp.v2.x, x2 = params.x + Tmp.v3.x, y1 = params.y + Tmp.v2.y, y2 = params.y + Tmp.v3.y;
			float dx = Mathm.dx(x1, bowWidth, rot + 270f + (70f * warmup - visRad * rp * warmup) * i), dy = Mathm.dy(y1, bowWidth, rot + 270f + (70f * warmup - visRad * rp * warmup) * i);
			Tmp.v1.set(bowTk / 2f * warmup * i, 0).rotate(rot);
			float dx1 = dx + Tmp.v1.x, dy1 = dy + Tmp.v1.y;
			Fill.tri(x1, y1, x2, y2, dx, dy);
			Fill.tri(x2, y2, dx, dy, dx1, dy1);
			Lines.line(dx, dy, rx, ry);
		}
		Draw.z(Layer.effect);
		Tmp.v1.set(0f, bowMoveY + bowFY).rotate(rot);
		float pullx = bx + Tmp.v1.x, pully = by + Tmp.v1.y;
		Lines.stroke(2 * warmup);
		Lines.circle(pullx, pully, 4);
		float sin = Mathf.absin(Time.time, 6, 1.5f);
		for (int i = 0; i < 3; i++) {
			float angle = i * 360f / 3;
			Drawf.tri(pullx + Angles.trnsx(angle - Time.time, 5f + sin), pully + Angles.trnsy(angle - Time.time, 5f + sin), 4f, -2f * warmup, angle - Time.time);
		}

		float arx = rx + Angles.trnsx(rot, 0, -bowMoveY / 2), ary = ry + Angles.trnsy(rot, 0, -bowMoveY / 2);
		//arrow
		Draw.color(color.cpy().a(p));
		if (arrowSp != null) Draw.rect(Core.atlas.find(arrowSp), arx, ary, 32 * warmup, 50, rot);
		else Drawf.tri(arx, ary, 16 * warmup, 20, rot + 90);
		Drawf.tri(arx, ary, 12 * warmup, 8, rot - 90);
		Draw.reset();
	}

	@Override
	public void load(String s) {
	}
}
