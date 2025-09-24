package heavyindustry.gen;

import heavyindustry.entities.skill.Skill.SkillState;
import heavyindustry.type.unit.SkillUnitType;
import mindustry.gen.Unitc;

public interface Skillc extends Unitc {
	SkillState[] skills();

	void skills(SkillState[] value);

	void setupSkills(SkillUnitType type);

	void attachSkills();

	void updateSkillStats();
}
