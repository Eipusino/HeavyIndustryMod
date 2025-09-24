package heavyindustry.gen;

import heavyindustry.entities.skill.ParrySkill;

public interface Parryc {
	boolean clockwise();

	ParrySkill skill();

	void clockwise(boolean value);

	void skill(ParrySkill value);
}
