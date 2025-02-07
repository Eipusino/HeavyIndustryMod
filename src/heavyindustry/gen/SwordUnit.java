package heavyindustry.gen;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import heavyindustry.content.*;
import heavyindustry.graphics.Trails.*;
import heavyindustry.type.unit.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.gen.*;

import static mindustry.Vars.*;

public class SwordUnit extends Unitf implements Swordc {
	public IntSeq collided = new IntSeq();
	public float lastBaseX = Float.NEGATIVE_INFINITY, lastBaseY;
	public int orbitPos;
	public float heat;
	public transient DriftTrail[] driftTrails;

	protected SwordUnit() {}

	@Override
	public int classId() {
		return EntityRegister.getId(SwordUnit.class);
	}

	@Override
	public void update() {
		super.update();
		if (type instanceof SwordUnitType sType) {
			if (lastBaseX != Float.NEGATIVE_INFINITY) {
				float
						tipX = x + Angles.trnsx(rotation, sType.tipY),
						tipY = y + Angles.trnsy(rotation, sType.tipY);

				if (type.targetGround)
					tileRayCast(World.toTile(lastBaseX), World.toTile(lastBaseY), World.toTile(tipX), World.toTile(tipY));
				unitRayCast(lastBaseX, lastBaseY, tipX, tipY);

				if (driftTrails != null) {
					float
							trailX = x + Angles.trnsx(rotation, sType.trailY),
							trailY = y + Angles.trnsy(rotation, sType.trailY);
					Tmp.v1.set(vel).scl(sType.trailInheritVel);
					for (int i = 0; i < 2; i++) {
						float tRot = rotation + sType.trailAngle * Mathf.signs[i];
						Tmp.v2.trns(tRot, sType.trailVel).add(Tmp.v1);
						driftTrails[i].update(trailX, trailY, Tmp.v2);
					}
				}

				heat = Math.min(heat + Time.delta / sType.heatUpTime, 1f);
			} else {
				if (driftTrails != null && driftTrails[0].size() > 0) {
					for (DriftTrail trail : driftTrails) {
						Fxf.driftTrailFade.at(x, y, type.trailScl, type.trailColor, trail.copy());
						trail.clear();
					}
				}

				heat = Math.max(heat - Time.delta / sType.cooldownTime, 0f);
			}

			lastBaseX = x + Angles.trnsx(rotation, sType.baseY);
			lastBaseY = y + Angles.trnsy(rotation, sType.baseY);
		}
	}

	@Override
	public void remove() {
		super.remove();
		if (driftTrails != null) {
			for (DriftTrail trail : driftTrails) {
				if (trail.size() > 0) Fxf.driftTrailFade.at(x, y, type.trailScl, type.trailColor, trail.copy());
			}
		}
	}

	@Override
	public void unitRayCast(float x1, float y1, float x2, float y2) {
		if (type instanceof SwordUnitType sType) {
			Vec2 tr = Tmp.v1;
			Rect rect = Tmp.r1;
			float angle = Angles.angle(x1, y1, x2, y2);

			tr.trnsExact(angle, Mathf.dst(x1, y1, x2, y2));

			rect.setPosition(x, y).setSize(tr.x, tr.y);

			if (rect.width < 0) {
				rect.x += rect.width;
				rect.width *= -1;
			}

			if (rect.height < 0) {
				rect.y += rect.height;
				rect.height *= -1;
			}

			float expand = 3f;

			rect.y -= expand;
			rect.x -= expand;
			rect.width += expand * 2;
			rect.height += expand * 2;

			Cons<Unit> cons = e -> {
				e.hitbox(Tmp.r2);

				Vec2 vec = Geometry.raycastRect(x, y, x2, y2, Tmp.r2.grow(expand * 2));

				if (vec != null && sType.damage > 0) {
					sType.hitEffect.at(vec.x, vec.y, rotation, team.color);
					e.damage(damage());
					e.apply(sType.status, sType.statusDuration);
					collided.add(e.id);
				}
			};

			Units.nearbyEnemies(team, rect, u -> {
				if (u.checkTarget(sType.targetAir, sType.targetGround) && !collided.contains(u.id)) cons.get(u);
			});
		}
	}

	//copy-paste of BulletComp#tileRaycast, because I don't know what I'm doing;
	@Override
	public void tileRayCast(int x1, int y1, int x2, int y2) {
		if (type instanceof SwordUnitType sType) {
			int x = x1, dx = Math.abs(x2 - x), sx = x < x2 ? 1 : -1;
			int y = y1, dy = Math.abs(y2 - y), sy = y < y2 ? 1 : -1;
			int e2, err = dx - dy;
			int ww = world.width(), wh = world.height();

			while (x >= 0 && y >= 0 && x < ww && y < wh) {
				Building build = world.build(x, y);

				if (build != null && isAdded() && build.team != team && !hasCollided(build.id)) {
					collided.add(build.id);
					sType.hitEffect.at(World.unconv(x), World.unconv(y), rotation, team.color);
					build.damage(damage());
				}

				if (x == x2 && y == y2) break;

				e2 = 2 * err;
				if (e2 > -dy) {
					err -= dy;
					x += sx;
				}

				if (e2 < dx) {
					err += dx;
					y += sy;
				}
			}
		}
	}

	@Override
	public void clearCollided() {
		collided.clear();
	}

	@Override
	public boolean hasCollided(int id) {
		return collided.size != 0 && collided.contains(id);
	}

	@Override
	public float damage() {
		if (type instanceof SwordUnitType sType) {
			return sType.damage * state.rules.blockDamage(team);
		}
		return 0f;
	}

	@Override
	public IntSeq collided() {
		return collided;
	}

	@Override
	public float lastBaseX() {
		return lastBaseX;
	}

	@Override
	public float lastBaseY() {
		return lastBaseY;
	}

	@Override
	public int orbitPos() {
		return orbitPos;
	}

	@Override
	public float heat() {
		return heat;
	}

	@Override
	public DriftTrail[] driftTrails() {
		return driftTrails;
	}

	@Override
	public void collided(IntSeq value) {
		collided = value;
	}

	@Override
	public void lastBaseX(float value) {
		lastBaseX = value;
	}

	@Override
	public void lastBaseY(float value) {
		lastBaseY = value;
	}

	@Override
	public void orbitPos(int value) {
		orbitPos = value;
	}

	@Override
	public void heat(float value) {
		heat = value;
	}

	@Override
	public void driftTrails(DriftTrail[] value) {
		driftTrails = value;
	}

	public static SwordUnit create() {
		return new SwordUnit();
	}
}
