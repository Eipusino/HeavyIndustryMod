package heavyindustry.gen;

import heavyindustry.type.unit.UnitType2;
import mindustry.gen.UnitEntityLegacyAlpha;

public class InvincibleShipUnit extends UnitEntityLegacyAlpha implements Unitc2 {
	@Override
	public int classId() {
		return Entitys.getId(InvincibleShipUnit.class);
	}

	@Override
	public UnitType2 checkType() {
		return (UnitType2) type;
	}

	@Override
	public void damage(float amount) {}

	@Override
	public void damage(float amount, boolean withEffect) {}

	@Override
	public void rawDamage(float amount) {}

	@Override
	public void damagePierce(float amount) {}

	@Override
	public void damagePierce(float amount, boolean withEffect) {}
}
