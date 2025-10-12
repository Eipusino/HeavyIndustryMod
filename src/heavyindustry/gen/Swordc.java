package heavyindustry.gen;

import arc.struct.IntSeq;
import heavyindustry.graphics.HTrails.DriftTrail;

public interface Swordc extends Unitc2 {
	void unitRayCast(float x1, float y1, float x2, float y2);

	void tileRayCast(int x1, int y1, int x2, int y2);

	void clearCollided();

	boolean hasCollided(int id);

	float damage();

	IntSeq collided();

	float lastBaseX();

	float lastBaseY();

	int orbitPos();

	float heat();

	DriftTrail[] driftTrails();

	void collided(IntSeq value);

	void lastBaseX(float value);

	void lastBaseY(float value);

	void orbitPos(int value);

	void heat(float value);

	void driftTrails(DriftTrail[] value);
}
