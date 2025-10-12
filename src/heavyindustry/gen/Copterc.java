package heavyindustry.gen;

import heavyindustry.type.unit.CopterUnitType.RotorMount;

public interface Copterc extends Unitc2 {
	RotorMount[] rotors();

	float rotorSpeedScl();

	void rotors(RotorMount[] value);

	void rotorSpeedScl(float value);
}
