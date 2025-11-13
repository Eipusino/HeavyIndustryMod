package heavyindustry.gen;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.IntSet;
import arc.util.Time;
import arc.util.pooling.Pool.Poolable;
import arc.util.pooling.Pools;
import heavyindustry.entities.HEntity;
import heavyindustry.graphics.Drawn;
import heavyindustry.math.Mathm;
import heavyindustry.util.Get;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Sized;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.graphics.Layer;

public class ShrapnelEntity extends BaseEntity implements Poolable {
	public IntSet collided = new IntSet();
	public TextureRegion region = new TextureRegion();
	public float[] verts = new float[16];
	public float damage;
	public float hitSize;

	public float vx, vy, vr;
	public float rotation;
	public float z = Layer.flyingUnitLow;
	public Team team = Team.derelict;
	public Effect explosion = Fx.none;

	public float time, lifetime;

	public static ShrapnelEntity create(float x, float y, float rotation, float size, TextureRegion region) {
		ShrapnelEntity s = Pools.obtain(ShrapnelEntity.class, ShrapnelEntity::new);
		s.x = x;
		s.y = y;
		s.rotation = rotation;
		s.region.set(region);
		s.lifetime = 60f;
		s.hitSize = 0f;

		float rangeX = Mathf.range((region.width / 2f) / 2f);
		float rangeY = Mathf.range((region.height / 2f) / 2f);

		for (int i = 0; i < 4; i++) {
			Vec2 v = Get.v.trns(i * 90f + Mathf.range(45f), (s.hitSize += size * Mathf.random(0.5f, 1f)));
			float tu = v.x / Draw.scl;
			float tv = v.y / Draw.scl;

			int idx = i * 4;

			s.verts[idx] = v.x;
			s.verts[idx + 1] = v.y;
			s.verts[idx + 2] = Mathf.lerp(region.u, region.u2, (tu + rangeX + region.width / 2f) / region.width);
			s.verts[idx + 3] = Mathf.lerp(region.v, region.v2, (tv + rangeY + region.height / 2f) / region.height);
		}
		s.hitSize /= 4f;
		s.add();

		return s;
	}

	@Override
	public int classId() {
		return Entitys.getId(ShrapnelEntity.class);
	}

	@Override
	public boolean serialize() {
		return false;
	}

	@Override
	public void update() {
		x += vx * Time.delta;
		y += vy * Time.delta;
		rotation += vr * Time.delta;

		float len = Mathf.sqrt(vx * vx + vy * vy);

		time += Time.delta;

		//Teamc teamc = Units.closestTarget(team, x, y, 8f, Flyingc::isGrounded);
		if (damage > 0) {
			HEntity.scanEnemies(team, x, y, hitSize / 1.5f, true, true, t -> {
				if (collided.add(t.id())) {
					Healthc h = (Healthc) t;
					Sized size = (Sized) t;

					float scl = len / 5f;
					float damage = this.damage * scl;

					h.damage(damage);
					explosion.at(x, y, hitSize / 2f);

					float drag = 1f - Mathm.clamp(((size.hitSize() / 2f) / hitSize) / 30f);

					vx *= drag;
					vy *= drag;
					vr *= drag;
				}
			});
		}

		float nlen = (vx * vx + vy * vy);

		if (time >= lifetime || nlen < 0.01f) {
			explosion.at(x, y, hitSize);
			remove();
		}
	}

	@Override
	public void draw() {
		float[] vert = Drawn.verts;
		float color = Color.whiteFloatBits, mix = Color.clearFloatBits;

		float sin = Mathf.sinDeg(rotation), cos = Mathf.cosDeg(rotation);

		int seg = 0;
		for (int i = 0; i < 16; i += 4) {
			float dx = verts[i], dy = verts[i + 1];

			vert[seg] = dx * cos - dy * sin + x;
			vert[seg + 1] = dx * sin + dy * cos + y;
			vert[seg + 2] = color;
			vert[seg + 3] = verts[i + 2];
			vert[seg + 4] = verts[i + 3];
			vert[seg + 5] = mix;

			seg += 6;
		}

		Draw.z(z);
		//Fill.circle(x, y, 8f);
		Draw.vert(region.texture, vert, 0, 24);
	}

	@Override
	public float clipSize() {
		return 60f;
	}

	@Override
	public void reset() {
		collided.clear();
		damage = vx = vy = vr = rotation = 0f;
		time = lifetime = 0f;
		hitSize = 0f;
		z = Layer.flyingUnitLow;
		region.set(Core.atlas.white());

		team = Team.derelict;
		explosion = Fx.none;
	}

	@Override
	public void remove() {
		if (!added) return;
		Groups.all.remove(this);
		Groups.draw.remove(this);
		Groups.queueFree(this);
		added = false;
	}
}
