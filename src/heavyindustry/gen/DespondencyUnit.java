package heavyindustry.gen;

import arc.util.Time;
import heavyindustry.entities.HEntity;
import heavyindustry.math.Mathm;
import mindustry.entities.EntityCollisions;
import mindustry.entities.EntityCollisions.SolidPred;
import mindustry.type.UnitType;

public class DespondencyUnit extends BaseLegsUnit {
	public float trueHealth, trueMaxHealth;
	public float invFrames;
	public float lastDamage = 0f;

	@Override
	public int classId() {
		return Entitys.getId(DespondencyUnit.class);
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
	public void setType(UnitType type) {
		super.setType(type);
		trueMaxHealth = type.health;
	}

	@Override
	public void add() {
		if (!added) {
			trueHealth = type.health;
			HEntity.exclude(this);
		}
		super.add();
	}

	@Override
	public void remove() {
		if (trueHealth > 0 && HEntity.containsExclude(id)) return;
		if (added) {
			boolean valid = HEntity.removeExclude(this);
			if (valid) {
				super.remove();
			} else {
				trueHealth = trueMaxHealth = type.health;
			}
		}
	}
}
