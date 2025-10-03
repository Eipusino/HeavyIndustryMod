package heavyindustry.gen;

import mindustry.gen.Healthc;

public interface TrueHealthc extends Healthc {
	float trueHealth();

	float trueMaxHealth();

	void trueHealth(float value);

	void trueMaxHealth(float value);
}
