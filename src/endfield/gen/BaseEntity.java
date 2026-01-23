package endfield.gen;

import arc.math.geom.Position;
import arc.util.io.Reads;
import arc.util.io.Writes;
import endfield.math.IPosition;
import endfield.util.CollectionObjectMap;
import endfield.util.ExtraVariable;
import mindustry.Vars;
import mindustry.entities.EntityGroup;
import mindustry.gen.Building;
import mindustry.gen.Drawc;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Unitc;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

import java.util.Map;

public abstract class BaseEntity implements Drawc, IPosition, ExtraVariable {
	public Map<String, Object> extraVar = new CollectionObjectMap<>(String.class, Object.class);

	public float x = 0, y = 0, drawSize = 40;
	public boolean added;
	public transient int id = EntityGroup.nextId();

	@Override
	public void afterReadAll() {}

	@Override
	public void beforeWrite() {}

	@Override
	public float clipSize() {
		return drawSize * 2;
	}

	@Override
	public void remove() {
		if (!added) return;
		Groups.all.remove(this);
		Groups.draw.remove(this);
		added = false;
	}

	@Override
	public void add() {
		if (added) return;
		Groups.all.add(this);
		Groups.draw.add(this);
		added = true;
	}

	@Override
	public boolean isLocal() {
		return this instanceof Unitc unit && unit.controller() == Vars.player;
	}

	@Override
	public boolean isRemote() {
		return this instanceof Unitc unit && unit.isPlayer() && !isLocal();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Entityc> T self() {
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T as() {
		return (T) this;
	}

	@Override
	public void set(float v1, float v2) {
		x = v1;
		y = v2;
	}

	@Override
	public void set(Position pos) {
		set(pos.getX(), pos.getY());
	}

	@Override
	public void trns(float v1, float v2) {
		set(x + v1, y + v2);
	}

	@Override
	public void trns(Position pos) {
		trns(pos.getX(), pos.getY());
	}

	@Override
	public int tileX() {
		return 0;
	}

	@Override
	public int tileY() {
		return 0;
	}

	@Override
	public Floor floorOn() {
		return null;
	}

	@Override
	public Block blockOn() {
		return null;
	}

	@Override
	public boolean onSolid() {
		return false;
	}

	@Override
	public Tile tileOn() {
		return null;
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public float x() {
		return x;
	}

	@Override
	public void x(float v) {
		x = v;
	}

	@Override
	public float y() {
		return y;
	}

	@Override
	public void y(float v) {
		y = v;
	}

	@Override
	public boolean isAdded() {
		return added;
	}

	@Override
	public boolean serialize() {
		return true;
	}

	@Override
	public void read(Reads read) {
		x = read.f();
		y = read.f();
	}

	@Override
	public void write(Writes write) {
		write.f(x);
		write.f(y);
	}

	@Override
	public int classId() {
		return 0;
	}

	@Override
	public void afterRead() {}

	@Override
	public int id() {
		return id;
	}

	@Override
	public void id(int v) {
		id = v;
	}

	@Override
	public Building buildOn() {
		return Vars.world.buildWorld(x, y);
	}

	@Override
	public String toString() {
		return "CommandEntity{" + "added=" + added + ", id=" + id + ", x=" + x + ", y=" + y + '}';
	}

	@Override
	public Map<String, Object> extra() {
		return extraVar;
	}
}
