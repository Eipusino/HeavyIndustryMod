package heavyindustry.gen;

import heavyindustry.type.unit.CopterUnitType.*;
import mindustry.gen.*;

public interface Copterc extends Unitc {
	RotorMount[] rotors();

	float rotorSpeedScl();

	void rotors(RotorMount[] value);

	void rotorSpeedScl(float value);
}
