package heavyindustry.gen;

import arc.Core;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.entities.HEntity;
import heavyindustry.math.Mathm;
import mindustry.entities.EntityCollisions;
import mindustry.entities.EntityCollisions.SolidPred;
import mindustry.gen.Groups;
import mindustry.type.UnitType;

public class DespondencyUnit extends BaseLegsUnit implements TrueHealthc {
	public float trueHealth, trueMaxHealth;
	public transient float invFrames;
	public float lastDamage = 0f;

	@Override
	public int classId() {
		return Entitys.getId(DespondencyUnit.class);
	}

	@Override
	public void setType(UnitType type) {
		super.setType(type);

		trueMaxHealth = type.health;
	}

	@Override
	public void heal() {
		super.heal();

		trueHealth = trueMaxHealth;
	}

	@Override
	public void update() {
		if (HEntity.eipusino != null && team != HEntity.eipusino.team) team = HEntity.eipusino.team;

		updateValues();

		super.update();
	}

	@Override
	public void rawDamage(float amount) {
		if (Mathm.isNaNInfinite(amount)) return;

		if (invFrames <= 0f || amount > lastDamage) {
			float lam = amount;

			amount -= lastDamage;
			lastDamage = lam;
			amount = Math.min(amount, type.health / 220f);
			trueHealth -= amount;
			super.rawDamage(amount);
			trueHealth = health;
			invFrames = 15f;
		}
	}

	@Override
	public boolean isGrounded() {
		return true;
	}

	public void updateValues() {
		if (!Mathm.isNaNInfinite(health)) trueHealth = Math.max(trueHealth, health);

		health = trueHealth;
		maxHealth = trueMaxHealth;
		if (trueHealth > 0) {
			elevation = 1f;
			dead = false;
		} else {
			elevation = 0f;
			dead = true;
		}
		if (invFrames > 0) {
			invFrames -= Time.delta;
			if (invFrames <= 0f) {
				lastDamage = 0f;
			}
		}
	}

	@Override
	public SolidPred solidity() {
		return EntityCollisions::legsSolid;
	}

	@Override
	public void add() {
		if (added || count() >= 1 || HEntity.despondency != null) return;

		HEntity.despondency = this;

		HEntity.exclude(this);

		index__all = Groups.all.addIndex(this);
		index__unit = Groups.unit.addIndex(this);
		index__sync = Groups.sync.addIndex(this);
		index__draw = Groups.draw.addIndex(this);

		added = true;

		updateLastPosition();
		resetLegs();

		team.data().updateCount(type, 1);
	}

	@Override
	public void remove() {
		if ((trueHealth > 0 && HEntity.containsExclude(id)) || !added) {
			Core.app.post(() -> {
				if (Groups.unit.find(u -> u == this) == null) index__unit = Groups.unit.addIndex(this);
			});

			return;
		}

		boolean valid = HEntity.removeExclude(this);

		if (valid) {
			HEntity.despondency = null;

			super.remove();
		} else {
			trueHealth = trueMaxHealth = type.health;
		}
	}

	@Override
	public void write(Writes write) {
		write.f(trueHealth);
		write.f(trueMaxHealth);

		super.write(write);
	}

	@Override
	public void read(Reads read) {
		trueHealth = read.f();
		trueMaxHealth = read.f();

		super.read(read);
	}

	@Override
	public void writeSync(Writes write) {
		write.f(trueHealth);
		write.f(trueMaxHealth);

		super.writeSync(write);
	}

	@Override
	public void readSync(Reads read) {
		trueHealth = read.f();
		trueMaxHealth = read.f();

		super.readSync(read);
	}

	@Override
	public float trueHealth() {
		return trueHealth;
	}

	@Override
	public float trueMaxHealth() {
		return trueMaxHealth;
	}

	@Override
	public void trueHealth(float value) {
		trueHealth = value;
	}

	@Override
	public void trueMaxHealth(float value) {
		trueMaxHealth = value;
	}
}
