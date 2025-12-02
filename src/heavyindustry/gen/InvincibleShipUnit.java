package heavyindustry.gen;

public class InvincibleShipUnit extends Unit2 {
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
