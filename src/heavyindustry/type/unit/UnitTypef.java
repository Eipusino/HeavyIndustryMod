package heavyindustry.type.unit;

import arc.*;
import arc.util.*;
import heavyindustry.world.meta.*;
import mindustry.content.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.ammo.*;
import mindustry.world.meta.*;

public class UnitTypef extends UnitType {
	public float damageMultiplier = 1f;

	public UnitTypef(String name) {
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
