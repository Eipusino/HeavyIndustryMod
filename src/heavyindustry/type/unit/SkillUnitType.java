package heavyindustry.type.unit;

import arc.struct.Seq;
import heavyindustry.entities.skill.Skill;

public class SkillUnitType extends BaseUnitType {
	public Seq<Skill> skills = new Seq<>(Skill.class);

	public SkillUnitType(String name) {
		super(name);
	}
}
