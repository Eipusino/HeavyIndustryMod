package heavyindustry.entities;

import arc.math.geom.Vec2;
import arc.util.pooling.Pool.Poolable;
import mindustry.gen.Unit;

public class UnitPointEntry implements Poolable {
	public Unit unit;
	public Vec2 vec;

	public UnitPointEntry() {}

	public UnitPointEntry(Unit u, Vec2 v) {
		unit = u;
		vec = v;
	}

	@Override
	public void reset() {
		unit = null;
		vec = null;
	}
}
