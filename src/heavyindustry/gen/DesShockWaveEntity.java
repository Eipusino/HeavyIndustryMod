package heavyindustry.gen;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Circle;
import arc.math.geom.Intersector;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.entities.HEntity;
import heavyindustry.entities.HEntity.QuadTreeHandler;
import heavyindustry.graphics.HPal;
import heavyindustry.type.weapons.EndDespondencyWeapon;
import heavyindustry.util.Utils;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.game.Teams.TeamData;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

public class DesShockWaveEntity extends BaseEntity {
	public static final float range = 2000f;

	protected static final Circle c1 = new Circle();

	public float time = 0f;
	public Team team = Team.derelict;
	public Teamc targeting;

	public static void create(Team team, Teamc target, float x, float y) {
		DesShockWaveEntity w = new DesShockWaveEntity();
		w.x = x;
		w.y = y;
		w.team = team;
		w.targeting = target;

		w.add();
	}

	@Override
	public void update() {
		float lrange = time > 0 ? getRange() : 0f;
		time += Time.delta / 120f;
		float crange = getRange();

		for (TeamData data : Vars.state.teams.active) {
			if (data.team != team) {
				QuadTreeHandler handler = (rect, tree) -> {
					if (tree) {
						c1.set(x, y, crange);
						return (lrange <= 0f || !HEntity.circleContainsRect(x, y, lrange, rect)) && Intersector.overlaps(c1, rect);
					}
					float min = Math.min(rect.width, rect.height) / 2f;
					float mx = rect.x + rect.width / 2f;
					float my = rect.y + rect.height / 2f;

					return Mathf.within(x, y, mx, my, crange + min) && !Mathf.within(x, y, mx, my, lrange - min);
				};

				if (data.unitTree != null) {
					HEntity.scanQuadTree(data.unitTree, handler, u -> {
						if (EndDespondencyWeapon.isStray(u) && u != targeting) {
							u.health -= u.maxHealth + 1000f;
						}
					});
				}
				if (data.turretTree != null) {
					Seq<Building> seq = HEntity.buildings.clear();
					HEntity.scanQuadTree(data.turretTree, handler, bl -> {
						if (bl != targeting) {
							seq.add(bl);
						}
					});
					for (Building bl : seq) {
						bl.health -= bl.maxHealth + 1000f;
						if (bl.health <= 0f) bl.kill();
					}
					seq.clear();
				}
			}
		}

		if (time >= 1f) {
			remove();
		}
	}

	public float getRange() {
		return 100f + Interp.pow2Out.apply(time) * range;
	}

	@Override
	public void draw() {
		float fout = 1f - time;
		Draw.z(Layer.darkness + 0.1f);
		Draw.color(HPal.redLight);
		Lines.stroke(30f * fout);
		Lines.circle(x, y, getRange());

		Rand r = Utils.rand;
		r.setSeed(id);
		for (int i = 0; i < 20; i++) {
			float rot = r.random(360f);
			Vec2 v = Tmp.v1.trns(rot, getRange()).add(x, y);
			Drawf.tri(v.x, v.y, 30f * fout, 100f + 500f * time, rot + 180f);
		}
		Draw.reset();
	}

	@Override
	public float clipSize() {
		return 114514.191981f;
	}
}
