package heavyindustry.gen;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.util.Interval;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.content.HFx;
import heavyindustry.graphics.Drawn;
import heavyindustry.math.Mathm;
import heavyindustry.type.unit.EnergyUnitType;
import heavyindustry.util.Constant;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.graphics.Layer;
import mindustry.graphics.Trail;
import mindustry.type.UnitType;

public class EnergyUnit extends Unit2 implements Energyc {
	protected transient Vec2 lastPos = new Vec2();
	protected float reloadValue = 0;
	protected float lastHealth = 0;
	protected Interval timer = new Interval(5);

	protected Trail[] trails = {};

	@Override
	public int classId() {
		return Entitys.getId(EnergyUnit.class);
	}

	@Override
	public void destroy() {
		super.destroy();

		for (int i = 0; i < trails.length; i++) {
			Tmp.c1.set(team.color).mul(i * 0.045f).lerp(Color.white, 0.075f * i);
			Fx.trailFade.at(x, y, type.trailScl, team.color, trails[i].copy());
		}

		HFx.energyUnitBlast.at(x, y, hitSize * 4, team.color);

		Vec2 v = new Vec2().set(this);

		for (int i = 0; i < HFx.energyUnitBlast.lifetime / 6; i++) {
			Time.run(i * 6, () -> {
				for (int j = 0; j < 3; j++) {
					Lightning.create(team, team.color, 120f, v.x, v.y, Mathf.random(360), Mathf.random(12, 28));
					Drawn.randFadeLightningEffect(v.x, v.y, Mathf.random(360), Mathf.random(12, 28), team.color, Mathf.chance(0.5));
				}
			});
		}
	}

	@Override
	public void setType(UnitType type) {
		super.setType(type);

		if (trails.length != 3) {
			trails = new Trail[3];
			for (int i = 0; i < trails.length; i++) {
				trails[i] = new Trail(type.trailLength);
			}
		}
	}

	@Override
	public void add() {
		if (added) return;
		index__all = Groups.all.addIndex(this);
		index__unit = Groups.unit.addIndex(this);
		index__sync = Groups.sync.addIndex(this);
		index__draw = Groups.draw.addIndex(this);

		added = true;

		updateLastPosition();

		team.data().updateCount(type, 1);

		//check if over unit cap
		if (type.useUnitCap && count() > cap() && !spawnedByCore && !dead && !Vars.state.rules.editor) {
			Call.unitCapDeath(this);
			team.data().updateCount(type, -1);
		}

		lastPos.set(this);
	}

	@Override
	public void draw() {
		Draw.z(Layer.bullet);

		for (int i = 0; i < trails.length; i++) {
			Tmp.c1.set(team.color).mul(1 + i * 0.005f).lerp(Color.white, 0.015f * i + Mathf.absin(4f, 0.3f) + Mathm.clamp(hitTime) / 5f);
			trails[i].drawCap(Tmp.c1, type.trailScl);
			trails[i].draw(Tmp.c1, type.trailScl);
		}

		super.draw();
	}

	protected void updateTeleport() {
		if (!isPlayer() && type instanceof EnergyUnitType eType) {
			reloadValue += Time.delta;

			Teamc target = Units.closestEnemy(team, x, y, eType.teleportRange * 2f, Constant.BOOLF_UNIT_TRUE);
			int[] num = {0};
			float[] damage = {0};

			reloadValue += Math.max(lastHealth - health, 0) / 2f;
			lastHealth = health;

			if (timer.get(5f))
				Groups.bullet.intersect(x - eType.teleportRange, y - eType.teleportRange, eType.teleportRange * 2f, eType.teleportRange * 2f, bullet -> {
					if (bullet.team == team) return;
					num[0]++;
					damage[0] += bullet.damage();
				});

			if (teleportValid(eType) && (target != null || ((hitTime > 0 || num[0] > 4 || damage[0] > eType.reload / 2))) && (!isLocal() || Vars.mobile)) {
				float dst = target == null ? eType.teleportRange + eType.teleportMinRange : dst(target) / 2f;
				float angle = target == null ? rotation : angleTo(target);
				Tmp.v2.trns(angle + Mathf.range(1) * 45, dst * Mathf.random(1, 2), Mathf.range(0.2f) * dst).clamp(eType.teleportMinRange, eType.teleportRange).add(this).clamp(-Vars.finalWorldBounds, -Vars.finalWorldBounds, Vars.world.unitHeight() + Vars.finalWorldBounds, Vars.world.unitWidth() + Vars.finalWorldBounds);

				teleport(Tmp.v2.x, Tmp.v2.y);
			}
		}
	}

	@Override
	public boolean teleportValid(EnergyUnitType eType) {
		return reloadValue > eType.reload;
	}

	@Override
	public void teleport(float x, float y) {
		Drawn.teleportUnitNet(this, x, y, angleTo(x, y), isPlayer() ? getPlayer() : null);
		reloadValue = 0;
	}

	@Override
	public void update() {
		super.update();
		if (type instanceof EnergyUnitType eType) {
			if (!Vars.headless && lastPos.dst(this) > eType.effectTriggerLen) {
				Sounds.plasmaboom.at(this);
				Sounds.plasmaboom.at(lastPos);

				eType.teleport.at(x, y, hitSize / 2, team.color);
				eType.teleport.at(lastPos.x, lastPos.y, hitSize / 2, team.color);
				eType.teleportTrans.at(lastPos.x, lastPos.y, hitSize / 2, team.color, new Vec2().set(this));

				for (Trail t : trails) {
					Fx.trailFade.at(lastPos.x, lastPos.y, type.trailScl, team.color, t.copy());
					t.clear();
				}
			}

			lastPos.set(this);

			Rand rand = HFx.rand1;
			rand.setSeed(id);

			if (!Vars.headless) {
				for (int i = 0; i < trails.length; i++) {
					Trail trail = trails[i];

					float scl = rand.random(0.75f, 1.5f) * Mathf.sign(rand.range(1)) * (i + 1) / 1.25f;
					float s = rand.random(0.75f, 1.25f);

					Tmp.v1.trns(
							Time.time * scl * rand.random(0.5f, 1.5f) + i * 360f / trails.length + rand.random(360),
							hitSize * (1.1f + 0.5f * i) * 0.75f
					).add(this).add(
							Mathf.sinDeg(Time.time * scl * rand.random(0.75f, 1.25f) * s) * hitSize / 3 * (i * 0.125f + 1) * rand.random(-1.5f, 1.5f),
							Mathf.cosDeg(Time.time * scl * rand.random(0.75f, 1.25f) * s) * hitSize / 3 * (i * 0.125f + 1) * rand.random(-1.5f, 1.5f)
					);
					trail.update(Tmp.v1.x, Tmp.v1.y, 1 + Mathf.absin(4f, 0.2f));
				}
			}

			if (Mathf.chanceDelta(0.15) && healthf() < 0.6f)
				Drawn.randFadeLightningEffect(x, y, Mathf.range(hitSize, hitSize * 4), Mathf.range(hitSize / 4, hitSize / 2), team.color, Mathf.chance(0.5));

			if (!Vars.net.client() || isLocal()) updateTeleport();
		}
	}

	@Override
	public void damage(float amount, boolean withEffect) {
		super.damage(amount, withEffect);
	}

	@Override
	public Vec2 lastPos() {
		return lastPos;
	}

	@Override
	public float reloadValue() {
		return reloadValue;
	}

	@Override
	public float lastHealth() {
		return lastHealth;
	}

	@Override
	public Interval timer() {
		return timer;
	}

	@Override
	public Trail[] trails() {
		return trails;
	}

	@Override
	public void lastPos(Vec2 value) {
		lastPos = value;
	}

	@Override
	public void reloadValue(float value) {
		reloadValue = value;
	}

	@Override
	public void lastHealth(float value) {
		lastHealth = value;
	}

	@Override
	public void timer(Interval value) {
		timer = value;
	}

	@Override
	public void trails(Trail[] value) {
		trails = value;
	}
}
