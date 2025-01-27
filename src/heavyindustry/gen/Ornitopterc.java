package heavyindustry.gen;

import heavyindustry.type.unit.OrnitopterUnitType.*;
import mindustry.gen.*;
import mindustry.type.*;

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
