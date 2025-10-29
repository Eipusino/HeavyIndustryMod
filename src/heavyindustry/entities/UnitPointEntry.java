package heavyindustry.entities;

import arc.math.geom.Vec2;
import mindustry.gen.Unit;

public class UnitPointEntry {
	public Unit unit;
	public Vec2 vec;

	public UnitPointEntry(Unit u, Vec2 v) {
		unit = u;
		vec = v;
	}
}
