package heavyindustry.entities.bullet;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.QuadTree;
import arc.math.geom.QuadTree.QuadTreeObject;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Tmp;
import arc.util.pooling.Pool.Poolable;
import heavyindustry.content.HFx;
import heavyindustry.entities.HDamage.BasicPool;
import heavyindustry.entities.HEntity;
import heavyindustry.graphics.Drawn;
import heavyindustry.graphics.HPal;
import heavyindustry.util.Utils;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Teams.TeamData;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.world.blocks.defense.turrets.Turret.TurretBuild;

import static heavyindustry.HVars.MOD_NAME;

public class ApathyAoEBulletType extends BulletType {
	public float length = 1200f;
	public TextureRegion region;

	protected static BasicPool<ExplosionPoint> exPool = new BasicPool<>(ExplosionPoint::new);
	protected static QuadTree<ExplosionPoint> exTree;
	protected static int s = 0;

	public ApathyAoEBulletType() {
		speed = 0f;
		collides = collidesTiles = false;
		absorbable = false;
		hittable = false;
		keepVelocity = false;

		removeAfterPierce = false;
		pierce = true;
		pierceArmor = true;
		pierceCap = -1;
		impact = true;

		lifetime = 8f;

		despawnEffect = Fx.none;
		shootEffect = Fx.none;

		damage = 5000f;

		drawSize = 2400f;

		//instantDisappear = true;
	}

	public static void initTree() {
		//Groups.resize(-Vars.finalWorldBounds, -Vars.finalWorldBounds, tiles.width * Vars.tilesize + Vars.finalWorldBounds * 2, tiles.height * Vars.tilesize + Vars.finalWorldBounds * 2);

		exTree = new QuadTree<>(new Rect(-Vars.finalWorldBounds, -Vars.finalWorldBounds, Vars.world.width() * Vars.tilesize + Vars.finalWorldBounds * 2, Vars.world.height() * Vars.tilesize + Vars.finalWorldBounds * 2));
	}

	@Override
	public void load() {
		super.load();
		region = Core.atlas.find(MOD_NAME + "-flash");
	}

	@Override
	public void init(Bullet b) {
		Seq<ExplosionPoint> data = new Seq<>(ExplosionPoint.class);
		b.data = data;

		exTree.clear();

		for (TeamData td : Vars.state.teams.present) {
			if (td.team != b.team) {
				if (td.unitTree != null) {
					HEntity.scanCone(td.unitTree, b.x, b.y, b.rotation(), length, 17f, u -> {
						s = 0;
						Rect r = Tmp.r1.setCentered(u.x, u.y, 40f, 40f);
						exTree.intersect(r, e -> s++);

						if (u.isGrounded() && (s <= 0 || Mathf.chance(0.05f / s))) {
							ExplosionPoint p = exPool.obtain();
							Vec2 v = Tmp.v1.rnd(40f * Mathf.random());
							p.x = u.x + v.x;
							p.y = u.y + v.y;
							p.size = Mathf.random(0.75f, 2f);
							data.add(p);
							exTree.insert(p);
						}
					});
				}
				if (td.buildingTree != null) {
					HEntity.scanCone(td.buildingTree, b.x, b.y, b.rotation(), length, 17f, t -> {
						s = 0;
						Rect r = Tmp.r1.setCentered(t.x, t.y, 40f, 40f);
						exTree.intersect(r, e -> s++);

						if (s <= 0 || Mathf.chance(0.05f / s)) {
							ExplosionPoint p = exPool.obtain();
							Vec2 v = Tmp.v1.rnd(40f * Mathf.random());
							p.x = t.x + v.x;
							p.y = t.y + v.y;
							p.size = Mathf.random(0.75f, 1.25f);
							data.add(p);
							exTree.insert(p);
						}
					});
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void draw(Bullet b) {
		if (b.data instanceof Seq<?>) {
			Seq<ExplosionPoint> data = (Seq<ExplosionPoint>) b.data;
			float spark = (1f + Mathf.absin(b.fin(Interp.pow2In), 1f / 50f, 1.5f)) * b.fin();
			Draw.color(HPal.primary);
			Draw.blend(Blending.additive);

			Drawf.tri(b.x, b.y, 40f * b.fout(), length, b.rotation());
			Drawf.tri(b.x, b.y, 40f * b.fout(), 55f, b.rotation() + 180f);

			for (ExplosionPoint ex : data) {
				float s = spark * ex.size;
				float ang = Angles.angle(b.x, b.y, ex.x, ex.y);
				Draw.color(HPal.primary);
				Drawn.tri(ex.x, ex.y, b.x, b.y, (1f + spark / 2f) * 4f * b.fout(), ang);
				Draw.color();

				Draw.rect(region, ex.x, ex.y, region.width * Draw.scl * s, region.height * Draw.scl * s);
			}
			Draw.blend();
			Draw.color();
		}
	}

	@Override
	public void drawLight(Bullet b) {}

	public void explode(Bullet b, float x, float y, float size, int count) {
		//Damage.damage();
		float s = size * 120f;
		float exDamage = (damage * 10f) / (1 + (count - 1) * 8f);
		//float exDamage = 200000f * Mathf.clamp(1f - count / 15f);
		float exScl = Math.max(1f, 5f * (1f - count / 10f));
		Damage.damage(b.team, x, y, s, damage + exDamage, true, true, true, true, null);
		Rect r = Utils.r;
		r.setCentered(x, y, s * 2);

		for (TeamData data : Vars.state.teams.present) {
			if (data.team != b.team) {
				if (data.unitTree != null) {
					data.unitTree.intersect(r, u -> {
						if (u.within(x, y, s + u.hitSize / 2f) && u.isGrounded()) {
							float dst = 1f - Interp.pow3In.apply(Mathf.clamp(u.dst(x, y) / (s + u.hitSize / 2f)));

							u.damagePierce(dst * (u.maxHealth / 35f) * exScl);
							u.apply(StatusEffects.disarmed, 2f * 60f);
						}
					});
				}
				if (data.turretTree != null) {
					data.turretTree.intersect(r, t -> {
						if (t.within(x, y, s + t.hitSize() / 2f)) {
							float dst = 1f - Interp.pow3In.apply(Mathf.clamp(t.dst(x, y) / (s + t.hitSize() / 2f)));

							t.damagePierce(dst * (t.maxHealth / 35f) * exScl);
							if (t instanceof TurretBuild tr) {
								tr.ammo.clear();
							}
						}
					});
				}
			}
		}

		HFx.aoeExplosion2.at(x, y, s);
		Sounds.largeExplosion.at(x, y, Mathf.random(0.9f, 1.1f));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void removed(Bullet b) {
		if (b.data instanceof Seq<?>) {
			Seq<ExplosionPoint> data = (Seq<ExplosionPoint>) b.data;
			int size = data.size;
			for (ExplosionPoint exp : data) {
				explode(b, exp.x, exp.y, exp.size, size);
				exPool.free(exp);
			}
		}
	}

	public static class ExplosionPoint implements QuadTreeObject, Poolable {
		public float x = 0;
		public float y = 0;
		public float size = 0f;

		@Override
		public void hitbox(Rect out) {
			out.setCentered(x, y, 90f * size);
		}

		@Override
		public void reset() {
			x = 0f;
			y = 0f;
			size = 0f;
		}
	}
}
