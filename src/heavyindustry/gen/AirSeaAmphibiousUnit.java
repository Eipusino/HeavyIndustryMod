package heavyindustry.gen;

import mindustry.entities.EntityCollisions.SolidPred;

public class AirSeaAmphibiousUnit extends UnitWaterMove2 {
	public AirSeaAmphibiousUnit() {}

	@Override
	public SolidPred solidity() {
		return null;
	}

	@Override
	public boolean canShoot() {
		return !disarmed && (!type.canBoost || elevation < 0.09f || elevation > 0.9f);
	}

	@Override
	public int classId() {
		return Entitys.getId(AirSeaAmphibiousUnit.class);
	}
}
