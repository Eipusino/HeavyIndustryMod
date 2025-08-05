package heavyindustry.gen;

import mindustry.gen.UnitEntityLegacyAlpha;

public class InvincibleShipUnit extends UnitEntityLegacyAlpha implements BaseUnitc {
	@Override
	public int classId() {
		return Entitys.getId(InvincibleShipUnit.class);
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
