package endfield.gen;

import arc.math.geom.Vec2;
import mindustry.gen.TimedKillc;

public interface Sentryc extends TimedKillc {
	Vec2 anchorVel();

	float anchorDrag();

	float anchorRot();

	float anchorX();

	float anchorY();

	void anchorVel(Vec2 value);

	void anchorDrag(float value);

	void anchorRot(float value);

	void anchorX(float value);

	void anchorY(float value);
}
