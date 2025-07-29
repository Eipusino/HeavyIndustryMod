package heavyindustry.gen;

import heavyindustry.type.unit.CopterUnitType.RotorMount;

public interface Copterc extends BaseUnitc {
	RotorMount[] rotors();

	float rotorSpeedScl();

	void rotors(RotorMount[] value);

	void rotorSpeedScl(float value);
}
