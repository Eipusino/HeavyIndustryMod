package heavyindustry.type.unit;

import arc.util.Time;

public class NucleoidUnitType extends AncientUnitType {
	public float maxDamagedPerSec = 30000;
	public float recentDamageResume = maxDamagedPerSec / 60f;

	public float maxOnceDamage = 3000;
	public float reinforcementsSpacing = Time.toMinutes * 2;

	public float mass = 800000f;

	public boolean drawArrow = true;

	public NucleoidUnitType(String name) {
		super(name);
	}
}
