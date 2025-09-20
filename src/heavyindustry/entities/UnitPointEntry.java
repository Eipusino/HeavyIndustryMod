package heavyindustry.entities;

import arc.math.geom.Vec2;
import mindustry.gen.Unit;

public class UnitPointEntry {
	public final Unit unit;
	public final Vec2 vec;

	public UnitPointEntry(Unit u, Vec2 v) {
		unit = u;
		vec = v;
	}
}
