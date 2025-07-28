package heavyindustry.gen;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.util.Tmp;
import heavyindustry.content.HFx;
import heavyindustry.entities.effect.VapourizeEffect;
import heavyindustry.util.Utils;
import mindustry.gen.EffectState;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Hitboxc;
import mindustry.gen.Posc;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;

public class VapourizeEffectState extends EffectState {
	public float extraAlpha = 0f;
	protected Entityc influence;

	public VapourizeEffectState() {
		lifetime = 50f;
	}

	public VapourizeEffectState(float a, float b, Unit par, Entityc inf) {
		this();
		x = a;
		y = b;
		parent = par;
		influence = inf;
	}

	@Override
	public int classId() {
		return Entitys.getId(VapourizeEffectState.class);
	}

	@Override
	public void add() {
		if (added) return;
		Groups.all.add(this);
		Groups.draw.add(this);
		added = true;
	}

	@Override
	public void update() {
		if (!(parent instanceof Hitboxc hit) || !(influence instanceof Posc temp)) return;
		float hitSize = hit.hitSize();
		if (Mathf.chanceDelta(0.2f * (1f - fin()) * hitSize / 10f)) {
			Tmp.v1.trns(Angles.angle(x, y, temp.x(), temp.y()) + 180f, 65f + Mathf.range(0.3f));
			Tmp.v1.add(parent);
			Tmp.v2.trns(Mathf.random(360f), Mathf.random(hitSize / 1.25f));
			HFx.vaporation.at(parent.x(), parent.y(), 0f, new Position[]{parent, Tmp.v1.cpy(), Tmp.v2.cpy()});
		}
		super.update();
	}

	@Override
	public float clipSize() {
		return parent instanceof Hitboxc hit ? hit.hitSize() * 2f : super.clipSize();
	}

	@Override
	public void draw() {
		if (!(parent instanceof Unit unit)) return;
		UnitType type = unit.type;
		float oz = Draw.z();
		float z = (unit.elevation > 0.5f ? (type.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : type.groundLayer + Mathf.clamp(type.hitSize / 4000f, 0, 0.01f)) + 0.001f;
		float slope = (0.5f - Math.abs(fin() - 0.5f)) * 2f;
		Draw.z(z);
		Tmp.c1.set(Color.black);
		Tmp.c1.a = Mathf.clamp(slope * ((1 - unit.healthf()) + extraAlpha) * 1.4f);
		Draw.color(Tmp.c1);
		Utils.simpleUnitDrawer(unit);
		Draw.z(oz);
	}

	@Override
	public void remove() {
		if (!added) return;
		Groups.all.remove(this);
		Groups.draw.remove(this);
		VapourizeEffect.removeEvaporation(parent.id());
		added = false;
	}
}
