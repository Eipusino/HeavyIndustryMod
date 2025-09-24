package heavyindustry.gen;

import arc.math.geom.Position;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.EntityGroup;
import mindustry.gen.Building;
import mindustry.gen.Entityc;
import mindustry.gen.Posc;
import mindustry.gen.Rotc;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

public class EffectData implements Posc, Rotc {
	public Posc delegate;
	public Object data;

	public int id = EntityGroup.nextId();

	protected EffectData() {}

	public static EffectData create() {
		return new EffectData();
	}

	@Override
	public Floor floorOn() {
		return delegate.floorOn();
	}

	@Override
	public Building buildOn() {
		return delegate.buildOn();
	}

	@Override
	public boolean onSolid() {
		return delegate.onSolid();
	}

	@Override
	public float getX() {
		return delegate.getX();
	}

	@Override
	public float getY() {
		return delegate.getY();
	}

	@Override
	public float x() {
		return delegate.getX();
	}

	@Override
	public float y() {
		return delegate.getY();
	}

	@Override
	public float rotation() {
		return delegate instanceof Rotc rot ? rot.rotation() : 0f;
	}

	@Override
	public int tileX() {
		return delegate.tileX();
	}

	@Override
	public int tileY() {
		return delegate.tileY();
	}

	@Override
	public Block blockOn() {
		return delegate.blockOn();
	}

	@Override
	public Tile tileOn() {
		return delegate.tileOn();
	}

	// These setters do nothing; why would anybody want to modify state from effects?
	@Override
	public void set(Position position) {}

	@Override
	public void set(float x, float y) {}

	@Override
	public void trns(Position position) {}

	@Override
	public void trns(float x, float y) {}

	@Override
	public void x(float x) {}

	@Override
	public void y(float y) {}

	@Override
	public void rotation(float rotation) {}

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
	public boolean isAdded() {
		return delegate.isAdded();
	}

	@Override
	public boolean isLocal() {
		return delegate.isLocal();
	}

	@Override
	public boolean isRemote() {
		return delegate.isRemote();
	}

	@Override
	public boolean serialize() {
		return false;
	}

	@Override
	public int classId() {
		return Entitys.getId(EffectData.class);
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public void id(int value) {
		id = value;
	}

	@Override
	public void add() {}

	@Override
	public void remove() {}

	@Override
	public void update() {}

	@Override
	public void write(Writes writes) {}

	@Override
	public void read(Reads reads) {}

	@Override
	public void afterRead() {}

	@Override
	public void afterReadAll() {}

	@Override
	public void beforeWrite() {}
}
