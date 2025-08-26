package heavyindustry.type.unit;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.util.Time;

import static heavyindustry.HVars.MOD_NAME;

public class NucleoidUnitType extends AncientUnitType {
	public TextureRegion arrowRegion;

	public float maxDamagedPerSec = 30000;
	public float recentDamageResume = maxDamagedPerSec / 60f;

	public float maxOnceDamage = 3000;
	public float reinforcementsSpacing = Time.toMinutes * 2;

	public float mass = 800000f;

	public boolean drawArrow = true;

	public NucleoidUnitType(String name) {
		super(name);
	}

	@Override
	public void load() {
		super.load();

		arrowRegion = Core.atlas.find(MOD_NAME + "-jump-gate-arrow");
	}
}
