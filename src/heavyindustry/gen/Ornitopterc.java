package heavyindustry.gen;

import heavyindustry.type.unit.OrnitopterUnitType.BladeMount;
import mindustry.gen.Unitc;
import mindustry.type.UnitType;

public interface Ornitopterc extends Unitc {
	float bladeMoveSpeedScl();

	float driftAngle();

	long drawSeed();

	BladeMount[] blades();

	void bladeMoveSpeedScl(float value);

	void blades(BladeMount[] value);

	void drawSeed(long value);

	void setBlades(UnitType value);
}
