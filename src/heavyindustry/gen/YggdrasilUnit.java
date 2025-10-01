package heavyindustry.gen;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.entities.HEntity;
import mindustry.Vars;
import mindustry.entities.EntityCollisions;
import mindustry.entities.EntityCollisions.SolidPred;
import mindustry.gen.Groups;
import mindustry.graphics.Layer;

public class YggdrasilUnit extends BaseUnit {
	public Seq<YggdrasilLeg> legs = new Seq<>(YggdrasilLeg.class);
	public Seq<YggdrasilTentacle> tentacles = new Seq<>(YggdrasilTentacle.class);
	public int group;
	public float smoothProgress;

	public int tentacleIdx = 0;
	public float tentacleReload = 0f;

	public static final int groupSize = 4;

	@Override
	public int classId() {
		return Entitys.getId(YggdrasilUnit.class);
	}

	@Override
	public SolidPred solidity() {
		return EntityCollisions::legsSolid;
	}

	@Override
	public void update() {
		if (HEntity.eipusino != null && team != HEntity.eipusino.team) team = HEntity.eipusino.team;

		float maxProg = 0f;
		//int lastGroup = Mathf.mod(group - (groupSize / 2 + 1), groupSize);
		int cgroup = Mathf.mod(group, groupSize);

		for (YggdrasilLeg leg : legs) {
			//float adst = Mathf.clamp((Angles.angleDist(rotation, Angles.angle(x, y, leg.base.x, leg.base.y)) - (90f - 15f)) / (15f * 2f));

			/*if (leg.group == lastGroup) {
				maxProg = Math.max(maxProg, leg.getProgress(this) * adst);
			}*/

			if (leg.group != cgroup) {
				maxProg = Math.max(maxProg, leg.getProgress(this));
			}
		}
		smoothProgress = Math.max(Mathf.lerpDelta(smoothProgress, Math.min(maxProg, 2f), 0.25f), smoothProgress);
		if (smoothProgress > 1) smoothProgress = 1f;
		int ir = 0;
		for (YggdrasilLeg leg : legs) {
			if (leg.group == cgroup) {
				float c = 0.5f;
				float f = (ir % 4) / 3f;
				float cur = Mathf.curve(smoothProgress, c * f, (1f - c) + c * f);

				leg.updateMovement(this, cur);
				ir++;
			}
			leg.updateIK(this);
		}
		if (smoothProgress >= (1f - 0.001f)) {
			for (YggdrasilLeg leg : legs) {
				if (leg.group == cgroup) {
					//leg.sourceX = leg.base.x;
					//leg.sourceY = leg.base.y;

					//leg.walkLength = Mathf.dst(x, y, leg.sourceX, leg.sourceY);
					leg.end(this);
				}
			}
			group++;
			smoothProgress = 0f;
		}

		super.update();

		for (YggdrasilTentacle t : tentacles) {
			t.updateTargetPosition(aimX, aimY);
			t.update(this);
		}
		if (isShooting) {
			/*for (YggdrasilTentacle t : tentacles) {
				t.shoot(aimX, aimY);
			}*/
			YggdrasilTentacle t = tentacles.get(tentacleIdx);
			if (tentacleReload <= 0f && t.canShoot()) {
				t.shoot(aimX, aimY);

				tentacleIdx = (tentacleIdx + 1) % tentacles.size;
				tentacleReload = 140f / tentacles.size + 3f;
			}
		}
		if (tentacleReload > 0) tentacleReload -= Time.delta;

		//Log.info(controller);
	}

	@Override
	public void draw() {
		float z = !isAdded() ? Draw.z() : elevation > 0.5f ? (type.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : type.groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);
		Draw.z(z - 0.02f);
		type.applyColor(this);
		for (YggdrasilLeg leg : legs) {
			leg.draw(this);
		}
		Draw.z(z);
		for (YggdrasilTentacle t : tentacles) {
			t.draw(this);
		}
		Draw.reset();

		super.draw();
	}

	@Override
	public void add() {
		if (added || (count() > cap() && !spawnedByCore && !dead && !Vars.state.rules.editor)) return;

		legs.clear();
		for (int i = 0; i < 32; i++) {
			YggdrasilLeg leg = new YggdrasilLeg();
			leg.set(this);
			legs.add(leg);
		}
		legs.sort(l -> l.length);

		for (int i = 0; i < groupSize; i++) {
			for (int j = i; j < legs.size; j += groupSize) {
				YggdrasilLeg l = legs.get(j);
				l.group = i;
			}
		}

		for (int i = 0; i < 24; i++) {
			YggdrasilTentacle t = new YggdrasilTentacle();
			//t.set(this, (360f / 12f) * i);
			t.set(this, Mathf.random(360f));
			tentacles.add(t);
		}

		index__all = Groups.all.addIndex(this);
		index__unit = Groups.unit.addIndex(this);
		index__sync = Groups.sync.addIndex(this);
		index__draw = Groups.draw.addIndex(this);

		added = true;

		updateLastPosition();

		team.data().updateCount(type, 1);
	}

	@Override
	public void write(Writes write) {
		write.f(smoothProgress);
		write.i(tentacleIdx);

		super.write(write);
	}

	@Override
	public void read(Reads read) {
		smoothProgress = read.f();
		tentacleIdx = read.i();

		super.read(read);
	}

	@Override
	public void writeSync(Writes write) {
		write.f(smoothProgress);
		write.i(tentacleIdx);

		super.writeSync(write);
	}

	@Override
	public void readSync(Reads read) {
		smoothProgress = read.f();
		tentacleIdx = read.i();

		super.readSync(read);
	}
}
