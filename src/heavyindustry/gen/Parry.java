package heavyindustry.gen;

import arc.math.geom.Position;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arc.util.pooling.Pool.Poolable;
import arc.util.pooling.Pools;
import heavyindustry.entities.skill.ParrySkill;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.core.World;
import mindustry.entities.EntityGroup;
import mindustry.gen.Building;
import mindustry.gen.Drawc;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Posc;
import mindustry.gen.Rotc;
import mindustry.gen.Timedc;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

public class Parry implements Poolable, Parryc, Drawc, Entityc, Posc, Rotc, Timedc {
	private transient boolean added;

	public boolean clockwise;

	public transient int id = EntityGroup.nextId();

	public float lifetime;
	public float rotation;

	public ParrySkill skill;

	public float time;
	public float x;
	public float y;

	protected Parry() {}

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
	public boolean clockwise() {
		return clockwise;
	}

	@Override
	public boolean isAdded() {
		return added;
	}

	@Override
	public boolean isLocal() {
		return false;
	}

	@Override
	public boolean isRemote() {
		return false;
	}

	@Override
	public boolean onSolid() {
		Tile tile = tileOn();
		return tile == null || tile.solid();
	}

	@Override
	public boolean serialize() {
		return false;
	}

	@Override
	public ParrySkill skill() {
		return skill;
	}

	@Override
	public float clipSize() {
		return skill.clipSize;
	}

	@Override
	public float fin() {
		return time / lifetime;
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
	public float lifetime() {
		return lifetime;
	}

	@Override
	public float rotation() {
		return rotation;
	}

	@Override
	public float time() {
		return time;
	}

	@Override
	public float x() {
		return x;
	}

	@Override
	public float y() {
		return y;
	}

	@Override
	public int classId() {
		return Entitys.getId(Parry.class);
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public int tileX() {
		return World.toTile(x);
	}

	@Override
	public int tileY() {
		return World.toTile(y);
	}

	@Override
	public String toString() {
		return "Parry#" + id;
	}

	@Override
	public Building buildOn() {
		return Vars.world.buildWorld(x, y);
	}

	@Override
	public Block blockOn() {
		Tile tile = tileOn();
		return tile == null ? Blocks.air : tile.block();
	}

	@Override
	public Tile tileOn() {
		return Vars.world.tileWorld(x, y);
	}

	@Override
	public Floor floorOn() {
		Tile tile = tileOn();
		return tile != null && tile.block() == Blocks.air ? tile.floor() : (Floor) Blocks.air;
	}

	@Override
	public void add() {
		if (!added) {
			Groups.draw.add(this);
			Groups.all.add(this);
			added = true;
		}
	}

	@Override
	public void afterRead() {}

	@Override
	public void afterReadAll() {}

	@Override
	public void beforeWrite() {}

	@Override
	public void clockwise(boolean value) {
		clockwise = value;
	}

	@Override
	public void draw() {
		skill.draw(this);
	}

	@Override
	public void id(int value) {
		id = value;
	}

	@Override
	public void lifetime(float value) {
		lifetime = value;
	}

	@Override
	public void read(Reads read) {
		afterRead();
	}

	@Override
	public void remove() {
		if (added) {
			Groups.draw.remove(this);
			Groups.all.remove(this);
			added = false;
			Groups.queueFree(this);
		}
	}

	@Override
	public void reset() {
		added = false;
		clockwise = false;
		id = EntityGroup.nextId();
		lifetime = 0.0F;
		rotation = 0.0F;
		skill = null;
		time = 0.0F;
		x = 0.0F;
		y = 0.0F;
	}

	@Override
	public void rotation(float value) {
		rotation = value;
	}

	@Override
	public void set(Position pos) {
		set(pos.getX(), pos.getY());
	}

	@Override
	public void set(float valueX, float valueY) {
		x = valueX;
		y = valueY;
	}

	@Override
	public void skill(ParrySkill value) {
		skill = value;
	}

	@Override
	public void time(float value) {
		time = value;
	}

	@Override
	public void trns(Position pos) {
		trns(pos.getX(), pos.getY());
	}

	@Override
	public void trns(float valueX, float valueY) {
		set(x + valueX, y + valueY);
	}

	@Override
	public void update() {
		skill.update(this);
		time = Math.min(time + Time.delta, lifetime);
		if (time >= lifetime) {
			remove();
		}

	}

	@Override
	public void write(Writes write) {}

	@Override
	public void x(float value) {
		x = value;
	}

	@Override
	public void y(float value) {
		y = value;
	}

	public static Parry create() {
		return Pools.obtain(Parry.class, Parry::new);
	}
}
