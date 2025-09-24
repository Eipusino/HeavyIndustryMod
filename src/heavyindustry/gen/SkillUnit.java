package heavyindustry.gen;

import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.entities.skill.Skill;
import heavyindustry.entities.skill.Skill.SkillState;
import heavyindustry.io.HTypeIO;
import heavyindustry.type.unit.SkillUnitType;
import heavyindustry.util.Utils;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.units.StatusEntry;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;

public class SkillUnit extends UnitEntity implements Skillc {
	/** Compared by-reference to see if the unit hasn't setup its skills. */
	private static final SkillState[] noSkills = {};

	public SkillState[] skills = noSkills;

	@Override
	public int classId() {
		return Entitys.getId(SkillUnit.class);
	}

	@Override
	public void setupSkills(SkillUnitType type) {
		if (type.skills == null) type.skills = new Seq<>(Skill.class);

		// If `skills` isn't null, then it is already filled from `setType(UnitType)` and synced from network code.
		if (skills == null || skills == noSkills) {
			skills = new SkillState[type.skills.size];
			for (int i = 0; i < skills.length; i++) skills[i] = type.skills.get(i).create();
		}

		if (isAdded()) attachSkills();
	}

	@Override
	public void attachSkills() {
		for (SkillState skill : skills) {
			if (skill.unit == null) {
				skill.unit = self();
				skill.added();
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

		attachSkills();
	}

	@Override
	public void remove() {
		if (!added) return;

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
		if (trail != null && trail.size() > 0) {
			Fx.trailFade.at(x, y, trail.width(), type.trailColor == null ? team.color : type.trailColor, trail.copy());
		}

		for (WeaponMount mount : mounts) {
			if (mount.weapon.continuous && mount.bullet != null && mount.bullet.owner == this) {
				mount.bullet.time = mount.bullet.lifetime - 10f;
				mount.bullet = null;
			}
			if (mount.sound != null) {
				mount.sound.stop();
			}
		}

		for (SkillState skill : skills) {
			if (skill.unit != null) {
				skill.removed();
				skill.unit = null;
			}
		}
	}

	//@Insert(value = "update()", block = Statusc.class, after = false)
	@Override
	public void updateSkillStats() {
		StatusEntry stat = applyDynamicStatus();
		stat.speedMultiplier = Utils.reducef(skills, 1f, (skill, out) -> out * skill.speedMultiplier());
		stat.reloadMultiplier = Utils.reducef(skills, 1f, (skill, out) -> out * skill.reloadMultiplier());
	}

	@Override
	public void update() {
		super.update();

		updateSkillStats();

		for (SkillState skill : skills) {
			// I don't know why, but when players respawn to cores using the keybind (ctrl-clicking doesn't do this!),
			// `update()` is still called even after `remove()` is called. I'm not sure if this is worth reporting...
			if (skill.unit == null) return;
			skill.update();
		}
	}

	@Override
	public void draw() {
		super.draw();

		for (SkillState skill : skills) skill.draw();
	}

	@Override
	public void setType(UnitType type) {
		super.setType(type);

		if (!(type instanceof SkillUnitType c))
			throw new IllegalArgumentException("'" + type + "' isn't an instance of `CUnitType`.");
		setupSkills(c);
	}

	@Override
	public void write(Writes write) {
		HTypeIO.writeSkills(write, skills);

		super.write(write);
	}

	@Override
	public void read(Reads read) {
		if (!isLocal()) skills = HTypeIO.readSkills(read, skills);
		else HTypeIO.readSkills(read);

		super.read(read);
	}

	@Override
	public void writeSync(Writes write) {
		HTypeIO.writeSkills(write, skills);

		super.writeSync(write);
	}

	@Override
	public void readSync(Reads read) {
		if (!isLocal()) skills = HTypeIO.readSkills(read, skills);
		else HTypeIO.readSkills(read);

		super.readSync(read);
	}

	@Override
	public SkillState[] skills() {
		return skills;
	}

	@Override
	public void skills(SkillState[] value) {
		skills = value;
	}
}
