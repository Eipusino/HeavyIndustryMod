package heavyindustry.gen;

import heavyindustry.util.CollectionObjectMap;
import heavyindustry.util.comp.ExtraVariablec;
import mindustry.entities.EntityCollisions.SolidPred;

import java.util.Map;

public class AirSeaUnit extends UnitWaterMove2 implements ExtraVariablec {
	public Map<String, Object> extraVar = new CollectionObjectMap<>(String.class, Object.class);

	public AirSeaUnit() {}

	@Override
	public SolidPred solidity() {
		return null;
	}

	public boolean canShoot() {
		return !disarmed && (!type.canBoost || elevation < 0.09f || elevation > 0.9f);
	}

	@Override
	public int classId() {
		return Entitys.getId(AirSeaUnit.class);
	}

	@Override
	public Map<String, Object> extra() {
		return extraVar;
	}
}
