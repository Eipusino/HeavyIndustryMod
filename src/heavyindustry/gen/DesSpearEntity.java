package heavyindustry.gen;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.content.HFx;
import heavyindustry.entities.HEntity;
import heavyindustry.util.Utils;
import mindustry.Vars;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;

import static heavyindustry.HVars.MOD_NAME;

public class DesSpearEntity extends BaseEntity {
	public boolean collided;
	public float cx, cy, cr;
	public Unit unit;

	public float time;
	public float rotation;
	public float size;
	public float targetSize;

	public float vx, vy;
	public float dx, dy;
	public float tx, ty;

	public boolean main = true;
	public float forceScl = 1f, damageScl = 1f;
	public DesSpearEntity last;
	public boolean crySound = true;
	public boolean draw;

	public static TextureRegion region;
	public static float lifetime = 15f * 60f;
	public static float offset = -40f;

	public static DesSpearEntity create(Unit unit, float x, float y, float rotation, boolean draw) {
		if (!Vars.headless && region == null) region = Core.atlas.find(MOD_NAME + "-despondency-spear");

		float ts = Vars.headless ? unit.hitSize / 2f : Math.max(unit.hitSize / 2f, ((Math.min(unit.type.region.width, unit.type.region.height) * Draw.scl) / 2f) * 0.8f);
		float ts2 = Vars.headless ? unit.hitSize / 2f : Math.max(unit.hitSize / 2f, ((Math.min(unit.type.region.width, unit.type.region.height) * Draw.scl) / 2f)) * 1.6f;
		//float scl = Mathf.clamp(ts / (region.height * Draw.scl)) * (1f + ts / 100f);
		float scl = Vars.headless ? 1f : Math.max(Mathf.clamp(ts2 / (region.height * Draw.scl)), (ts / (region.height * Draw.scl)) / 3.5f) * (1f + ts / 70f);

		DesSpearEntity e = new DesSpearEntity();
		e.x = x;
		e.y = y;
		e.rotation = rotation;
		e.size = scl;
		e.targetSize = ts;
		e.unit = unit;
		e.draw = draw;

		e.add();

		return e;
	}

	@Override
	public boolean serialize() {
		return false;
	}

	@Override
	public int classId() {
		return Entitys.getId(DesSpearEntity.class);
	}

	@Override
	public void update() {
		time += Time.delta;

		if (unit == null) {
			remove();

			return;
		}

		if (!collided) {
			rotation = Angles.moveToward(rotation, Angles.angle(x, y, unit.x + tx, unit.y + ty), 180f * Mathf.clamp(time / 120f));
		}

		if (time > 20f && unit.isAdded() && !collided) {
			float speed = (time - 20f) / 3f + Mathf.pow(Mathf.clamp((time - 20f) / 30f), 2f) * 12f + 20f;
			float lx = x, ly = y;

			Vec2 v = Utils.v.trns(rotation, speed * Time.delta);
			x += v.x;
			y += v.y;

			Vec2 col = HEntity.intersectCircle(lx, ly, x, y, unit.x, unit.y, targetSize);

			if (col != null) {
				x = col.x;
				y = col.y;

				collided = true;
				cx = unit.x;
				cy = unit.y;
				cr = unit.rotation;

				vx = (v.x / (1f + targetSize / 15f)) * forceScl;
				vy = (v.y / (1f + targetSize / 15f)) * forceScl;
				if (last != null) {
					vx += last.vx / 3f;
					vy += last.vy / 3f;
					last.main = false;
				}

				//x -= unit.x;
				//y -= unit.y;

				dx = x - unit.x;
				dy = y - unit.y;

				HSounds.desSpearHit.at(col.x, col.y, Mathf.random(0.93f, 1.07f), 1.5f);

				HFx.desRailHit.at(col.x, col.y, rotation, (targetSize / 40f) * 0.3f);

				unit.health -= (unit.maxHealth / 2f) * damageScl;
			}
		}
		if (collided) {
			cx += vx;
			cy += vy;
			vx *= 1f - 0.075f * Time.delta;
			vy *= 1f - 0.075f * Time.delta;

			if (main) {
				unit.x = cx;
				unit.y = cy;
				unit.rotation = cr;
			}

			x = unit.x + dx;
			y = unit.y + dy;

			if (main && !HEntity.containsExclude(unit.id)) {
				for (WeaponMount m : unit.mounts) {
					m.reload = Mathf.lerpDelta(m.reload, m.weapon.reload, 0.4f);
					m.shoot = false;
				}
			}
		}
		if (!unit.isAdded() && time < (lifetime - 15f)) {
			time = lifetime - 15f;
		}

		if (time >= lifetime) {
			remove();
		}
	}

	public boolean fading() {
		return time > (lifetime - 15f);
	}

	@Override
	public float clipSize() {
		return 10f * size;
	}

	@Override
	public void add() {
		if (added) return;
		Groups.all.add(this);
		if (draw) Groups.draw.add(this);
		added = true;
	}

	@Override
	public void remove() {
		if (!added) return;
		Groups.all.remove(this);
		if (draw) Groups.draw.remove(this);
		added = false;
	}

	@Override
	public void draw() {
		if (region == null) region = Core.atlas.find(MOD_NAME + "-despondency-spear");

		float z = getZ();
		Draw.z(z);
		Draw.color();

		drawRaw();
	}

	public void drawRaw() {
		if (region == null) region = Core.atlas.find(MOD_NAME + "-despondency-spear");
		float fin = Mathf.clamp(time / 15f) * Mathf.clamp((lifetime - time) / 15f);
		Vec2 v = Tmp.v2.trns(rotation, offset * size).add(x, y);

		//unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);
		//UnitType type = unit.type;
		//float z = (unit.elevation > 0.5f ? (type.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : type.groundLayer + Mathf.clamp(type.hitSize / 4000f, 0f, 0.01f)) - 0.01f;

		TextureRegion r = Tmp.tr1;
		r.set(region);
		r.setU2(Mathf.lerp(region.u, region.u2, fin));
		Draw.rect(r, v.x, v.y, r.width * Draw.scl * size, r.height * Draw.scl * size, rotation);
	}

	public float getZ() {
		UnitType type = unit.type;
		return (unit.elevation > 0.5f ? (type.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : type.groundLayer + Mathf.clamp(type.hitSize / 4000f, 0f, 0.01f)) - 0.01f;
	}
}
