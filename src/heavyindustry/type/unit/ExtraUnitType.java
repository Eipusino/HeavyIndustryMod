package heavyindustry.type.unit;

import arc.Core;
import arc.util.Strings;
import heavyindustry.world.meta.HIStat;
import mindustry.content.Items;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.world.meta.Env;

public class ExtraUnitType extends UnitType {
	public float damageMultiplier = 1f;

	public ExtraUnitType(String name) {
		super(name);
	}

	@Override
	public void setStats() {
		super.setStats();
		if (damageMultiplier < 1f) {
			stats.add(HIStat.damageReduction, Core.bundle.format("hi-sin", Strings.autoFixed((1f - damageMultiplier) * 100, 2)));
		}
	}

	public void erekir() {
		outlineColor = Pal.darkOutline;
		envDisabled = Env.space;
		ammoType = new ItemAmmoType(Items.beryllium);
		researchCostMultiplier = 10f;
	}

	public void tank() {
		squareShape = true;
		omniMovement = false;
		rotateMoveFirst = true;
		rotateSpeed = 1.3f;
		envDisabled = Env.none;
		speed = 0.8f;
	}
}
