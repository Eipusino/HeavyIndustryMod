package heavyindustry.entities.abilities;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import heavyindustry.graphics.*;
import mindustry.gen.*;
import mindustry.type.*;

import static arc.Core.*;

public class MirrorFieldAbility extends BaseMirrorShieldAbility {
	public boolean rotation;
	public Seq<ShieldShape> shapes = new Seq<>(ShieldShape.class);

	/**
	 * Search for the range of bullets, if not set, it will be the default value.
	 * If a part of the shield moves, this data must be manually set to the maximum range of the covering part.
	 */
	public float nearRadius = -1f;

	@Override
	public String localized() {
		return Core.bundle.get("ability.mirror-shield");
	}

	@Override
	public void init(UnitType type) {
		super.init(type);
		if (nearRadius < 0f) {
			for (ShieldShape shape : shapes) {
				nearRadius = Math.max(nearRadius, Mathf.dst(shape.x, shape.y) + shape.radius);
			}
		}
	}

	@Override
	public MirrorFieldAbility copy() {
		try {//the dumbest behavior of java
			return copy((MirrorFieldAbility) super.clone());
		} catch (CloneNotSupportedException e) {
			return copy(new MirrorFieldAbility());
		}
	}

	protected MirrorFieldAbility copy(MirrorFieldAbility res) {
		for (int i = 0; i < shapes.size; i++) {
			if (res.shapes.get(i) != null) res.shapes.set(i, shapes.get(i).copy());
		}
		return res;
	}

	@Override
	public boolean shouldReflect(Unit unit, Bullet bullet) {
		for (ShieldShape shape : shapes) {
			if ((rotation && shape.inlerp(unit, unit.rotation() - 90, bullet, radScl))
					|| (!rotation && shape.inlerp(unit, 0, bullet, radScl))) return true;
		}
		return false;
	}

	@Override
	public void eachNearBullets(Unit unit, Cons<Bullet> cons) {
		Groups.bullet.intersect(unit.x - nearRadius, unit.y - nearRadius, nearRadius * 2, nearRadius * 2, b -> {
			if (unit.team != b.team) cons.get(b);
		});
	}

	@Override
	public void update(Unit unit) {
		super.update(unit);

		for (ShieldShape shape : shapes) {
			shape.flushMoves(unit);
		}
	}

	@Override
	public void draw(Unit unit) {
		if (unit.shield > 0) {
			Draw.color(unit.team.color, Color.white, Mathf.clamp(alpha));
			Draw.z(Draws.mirrorField + 0.001f * alpha);

			for (ShieldShape shape : shapes) {
				if (rotation) shape.draw(unit, unit.rotation() - 90, alpha, radScl);
				else shape.draw(unit, 0, alpha, radScl);
			}
		}
	}

	public static class ShieldShape implements Cloneable {
		public @Nullable ShapeMove movement;

		protected float x, y, angle;
		protected int sides;
		protected float radius;

		protected float moveOffsetX, moveOffsetY, moveOffsetRot;

		public ShieldShape(int side, float a, float b, float rad, float ang) {
			sides = side;
			x = a;
			y = b;
			radius = ang;
			angle = rad;
		}

		public boolean inlerp(Unit unit, float rotation, Bullet bullet, float scl) {
			return Intersector.isInRegularPolygon(sides,
					unit.x + moveOffsetX + Angles.trnsx(rotation, x * scl, y * scl),
					unit.y + moveOffsetY + Angles.trnsy(rotation, x * scl, y * scl),
					radius * scl,
					rotation + angle + moveOffsetRot,
					bullet.x(), bullet.y());
		}

		public void draw(Unit unit, float rotation, float alpha, float scl) {
			float drawX = unit.x + moveOffsetX + Angles.trnsx(rotation, x * scl, y * scl);
			float drawY = unit.y + moveOffsetY + Angles.trnsy(rotation, x * scl, y * scl);

			Draw.color(unit.team.color, Color.white, Mathf.clamp(alpha));

			if (settings.getBool("hi-animated-shields")) {
				Draw.z(Draws.mirrorField + 0.001f * alpha);
				Fill.poly(drawX, drawY, sides, radius * scl, rotation + angle + moveOffsetRot);
			} else {
				Draw.z(Draws.mirrorField + 1);
				Lines.stroke(1.5f);
				Draw.alpha(0.09f);
				Fill.poly(drawX, drawY, sides, radius * scl, rotation + angle + moveOffsetRot);

				for (int i = 1; i <= 4; i++) {
					Draw.alpha(i / 4f);
					Lines.poly(drawX, drawY, sides, radius * scl * (i / 4f), rotation + angle + moveOffsetRot);
				}
			}
		}

		public void flushMoves(Unit unit) {
			if (movement == null) return;

			Vec2 off = movement.offset(unit);
			moveOffsetX = off.x;
			moveOffsetY = off.y;
			moveOffsetRot = movement.rotation(unit);
		}

		public ShieldShape copy() {
			try {//fuck java
				return (ShieldShape) super.clone();
			} catch (CloneNotSupportedException e) {
				return new ShieldShape(sides, x, y, radius, angle);
			}
		}
	}

	public static class ShapeMove {
		private static final Vec2 tmp = new Vec2();

		private final Vec2 vec2 = new Vec2();

		public float x, y, angle;
		public float moveX, moveY, moveRot;
		public float rotateSpeed = 0;
		public Interp interp = Interp.linear;
		public Floatf<Unit> lerp = e -> 1;

		public @Nullable ShapeMove childMoving;

		protected float lerp(Unit unit) {
			return interp.apply(lerp.get(unit));
		}

		public Vec2 offset(Unit unit) {
			vec2.set(x, y).add(tmp.set(moveX, moveY).scl(lerp(unit))).rotate(rotateSpeed == 0 ? moveRot * lerp(unit) : Time.time * rotateSpeed);

			return childMoving != null ? vec2.add(childMoving.offset(unit)) : vec2;
		}

		public float rotation(Unit unit) {
			return angle + (childMoving != null ? childMoving.rotation(unit) : rotateSpeed == 0 ? moveRot * lerp(unit) : Time.time * rotateSpeed);
		}
	}
}
