package heavyindustry.gen;

import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.entities.HEntity;
import heavyindustry.math.Mathm;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.type.UnitType;

public class EipusinoUnit extends NucleoidUnit implements TrueHealthc {
	public float trueHealth, trueMaxHealth;
	public float damageTaken, maxDamageTaken, damageDelay;
	public transient float invFrames;

	@Override
	public int classId() {
		return Entitys.getId(EipusinoUnit.class);
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
		Team t = Vars.state.rules.defaultTeam;
		if (team() != t) team(t);

		float hd = trueHealth - health;
		if (hd > 0) {
			updateDamageTaken(hd);
		}

		health = trueHealth;
		maxHealth = trueMaxHealth;

		if (trueHealth > 0) dead = false;

		updateDamageTaken();

		if (invFrames > 0) invFrames -= Time.delta;

		super.update();
	}

	protected void updateDamageTaken(float amount) {
		//private float damageTaken, maxDamageTaken;
		damageTaken += amount;
		maxDamageTaken = Math.max(maxDamageTaken, damageTaken);
		damageDelay = 60f;

		if (damageTaken >= 2000f || Mathm.isNaNInfinities(damageTaken, maxDamageTaken)) {
			damageTaken = 0f;
			maxDamageTaken = 0f;
		}
	}

	protected void updateDamageTaken() {
		if (damageDelay <= 0f) {
			if (damageTaken > 0 && maxDamageTaken > 0) {
				damageTaken = Math.max(0f, damageTaken - (maxDamageTaken / (5f * 60f)));
				if (damageTaken <= 0f) {
					maxDamageTaken = 0f;
				}
			}
		} else {
			damageDelay -= Time.delta;
		}
	}

	protected void trueDamage(float amount) {
		amount = Math.min(amount, checkType().maxOnceDamage) * checkType().damageMultiplier;
		amount = Math.min(recentDamage / healthMultiplier, amount);
		recentDamage -= amount * 1.5f * healthMultiplier;

		updateDamageTaken(Math.max(0f, amount));

		if (Mathm.isNaNInfinite(amount)) amount = 0f;

		if (invFrames <= 0f) {
			trueHealth -= amount;
			health = trueHealth;
			invFrames = 30f;
			hitTime = 1.0f;
		}
	}

	@Override
	public void kill() {
		if (trueHealth <= 0f && !dead && !Vars.net.client() && killable()) {
			Call.unitDeath(id);
		}
	}

	@Override
	public void killed() {
		if (trueHealth > 0f ) return;

		super.killed();
	}

	@Override
	public void damage(float amount) {
		trueDamage(amount);
	}

	@Override
	public void damagePierce(float amount, boolean withEffect) {
		float pre = hitTime;
		trueDamage(amount);
		if (!withEffect) {
			hitTime = pre;
		}
	}

	@Override
	public void rawDamage(float amount) {
		trueDamage(amount);
	}

	@Override
	public void add() {
		// Only for use by the default team, and the quantity is limited to one.
		if (!added && HEntity.eipusino == null && team == Vars.state.rules.defaultTeam && count() < 1) {
			HEntity.exclude(this);
			HEntity.eipusino = this;

			index__all = Groups.all.addIndex(this);
			index__unit = Groups.unit.addIndex(this);
			index__sync = Groups.sync.addIndex(this);
			index__draw = Groups.draw.addIndex(this);

			added = true;

			updateLastPosition();

			team.data().updateCount(type, 1);
		}
	}

	@Override
	public void remove() {
		if (!added) return;

		HEntity.removeExclude(this);
		HEntity.eipusino = null;

		Groups.all.removeIndex(this, index__all);
		index__all = -1;
		Groups.unit.removeIndex(this, index__unit);
		index__unit = -1;
		Groups.sync.removeIndex(this, index__sync);
		index__sync = -1;
		Groups.draw.removeIndex(this, index__draw);
		index__draw = -1;

		added = false;

		if (Vars.net.client()) {
			Vars.netClient.addRemovedEntity(id());
		}

		team.data().updateCount(type, -1);
		controller.removed(this);

		//make sure trail doesn't just go poof
		if (trail != null && trail.size() > 0) {
			Fx.trailFade.at(x, y, trail.width(), type.trailColor == null ? team.color : type.trailColor, trail.copy());
		}
	}

	@Override
	public void write(Writes write) {
		write.f(trueHealth);
		write.f(trueMaxHealth);
		write.f(damageTaken);
		write.f(maxDamageTaken);
		write.f(damageDelay);

		super.write(write);
	}

	@Override
	public void read(Reads read) {
		trueHealth = read.f();
		trueMaxHealth = read.f();
		damageTaken = read.f();
		maxDamageTaken = read.f();
		damageDelay = read.f();

		super.read(read);
	}

	@Override
	public void writeSync(Writes write) {
		write.f(trueHealth);
		write.f(trueMaxHealth);
		write.f(damageTaken);
		write.f(maxDamageTaken);
		write.f(damageDelay);

		super.writeSync(write);
	}

	@Override
	public void readSync(Reads read) {
		trueHealth = read.f();
		trueMaxHealth = read.f();
		damageTaken = read.f();
		maxDamageTaken = read.f();
		damageDelay = read.f();

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
