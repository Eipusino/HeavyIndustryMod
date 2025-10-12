package heavyindustry.gen;

import arc.func.Cons;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.math.Mathm;
import heavyindustry.type.unit.ChainedUnitType;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.EntityCollisions;
import mindustry.entities.EntityCollisions.SolidPred;
import mindustry.entities.Units;
import mindustry.entities.units.UnitController;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Legsc;
import mindustry.gen.Mechc;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;
import mindustry.gen.WaterMovec;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

import static mindustry.Vars.net;
import static mindustry.Vars.state;

public class ChainedChainMechUnit extends Unit2 implements ChainMechc {
	public transient Unit head, tail, parent, child;

	public transient float growTime = 0, chainTime = 0;

	public transient float walk;

	public float baseRotation;

	// internal values that i wish i had the entityanno knowledge to not make it generate io
	int parentID = -1, childID = -1;
	boolean grown;

	@Override
	public int classId() {
		return Entitys.getId(ChainedChainMechUnit.class);
	}

	/** Add first segments if this unit is not grown. */
	@Override
	public void add() {
		if (added) return;
		index__all = Groups.all.addIndex(this);
		index__unit = Groups.unit.addIndex(this);
		index__sync = Groups.sync.addIndex(this);
		index__draw = Groups.draw.addIndex(this);

		added = true;

		updateLastPosition();

		team.data().updateCount(type, 1);
		if (type.useUnitCap && count() > cap() && !spawnedByCore && !dead && !state.rules.editor) {
			Call.unitCapDeath(this);
			team.data().updateCount(type, -1);
		}

		head = tail = this;

		if (!grown) {
			for (int i = 0; i < checkType().minSegments - 1; i++) grow();
			// i need this because again, i don't know how to not genio.
			grown = true;
		}
	}

	@Override
	public void aim(float x, float y) {
		if (isHead()) {
			final float finalX = x, finalY = y;
			consBackwards(u -> {
				if (!u.isHead()) u.aim(finalX, finalY);
			});
		}
	}

	@Override
	public int cap() {
		ChainedUnitType ct = checkType();
		return Math.max(Units.getCap(team) * Math.max(ct.growLength, ct.maxSegments), Units.getCap(team));
	}

	/** Wrong cast errors are way too long. So long in fact that the crash box is too small for it. */
	@Override
	public ChainedUnitType checkType(UnitType value) {
		return (ChainedUnitType) value;
	}

	@Override
	public ChainedUnitType checkType() {
		return (ChainedUnitType) type;
	}

	@Override
	public void chainTime(float value) {
		chainTime = value;
	}

	@Override
	public void child(Unit value) {
		child = value;
	}

	/**
	 * Connects this centipede with another one.
	 * <li>
	 * If this unit is not the chain unit's tail, it'll call this method on the tail unit.
	 * </li>
	 * <li>
	 * If Parameter "to" is not the chain unit's head, "to" will become its head() method.
	 * </li>
	 */
	@Override
	public void connect(Unit to) {
		Chainedc cast = ((Chainedc) to).head().as();
		if (isTail()) {
			cast.parent(this);
			child = to;

			((Chainedc) head).consBackwards(u -> {
				u.head(head);
				u.tail(cast.tail());
			});

			((Chainedc) head).consBackwards(u -> {
				u.setupWeapons(u.type);
				if (u.controller() instanceof Player) {
					head.controller(u.controller());
				} else {
					u.resetController();
				}
			});
		} else {
			((Chainedc) tail).connect(to);
		}
	}

	@Override
	public void growTime(float value) {
		growTime = value;
	}

	@Override
	public void grown(boolean value) {
		grown = value;
	}

	@Override
	public void head(Unit value) {
		head = value;
	}

	@Override
	public void parent(Unit value) {
		parent = value;
	}

	@Override
	public void tail(Unit value) {
		tail = value;
	}

	/** @param cons will run through this unit and it's children recursively. */
	public <T extends Unit & Chainedc> void consBackwards(Cons<T> cons) {
		T current = as();
		cons.get(current);
		while (current.child() != null) {
			cons.get(current.child().as());
			current = current.child().as();
		}
	}

	/** @param cons will run through this unit and it's parents recursively. */
	public <T extends Unit & Chainedc> void consForward(Cons<T> cons) {
		T current = as();
		cons.get(current);
		while (current.parent() != null) {
			cons.get(current.parent().as());
			current = current.parent().as();
		}
	}

	/** Force a specific controller on certain parts of the chain. */
	@Override
	public void controller(UnitController next) {
		if (!isHead()) {
			if (next instanceof Player) {
				head.controller(next);
				return;
			}

			controller = checkType().segmentAI.get(this);
			if (controller.unit() != this) controller.unit(this);
			return;
		}

		super.controller(next);
	}

	@Override
	public void controlWeapons(boolean rotate, boolean shoot) {
		if (isHead()) consBackwards(unit -> {
			if (!unit.isHead()) unit.controlWeapons(rotate, shoot);
		});

		super.controlWeapons(rotate, shoot);
	}

	/** Counts the amount of children from this unit recursively. */
	@Override
	public <T extends Unit & Chainedc> int countBackwards() {
		int out = 0;

		T current = as();
		while (current.child() != null) {
			out++;
			current = current.child().as();
		}

		return out;
	}

	/** Counts the amount of parents from this unit recursively. */
	@Override
	public <T extends Unit & Chainedc> int countForward() {
		int out = 0;

		T current = as();
		while (current.parent() != null) {
			out++;
			current = current.parent().as();
		}

		return out;
	}

	/** Adds an extra segment to the chain. */
	@Override
	public <T extends Unit & Chainedc> void grow() {
		if (!isTail()) {
			((Chainedc) tail).grow();
		} else {
			ChainedUnitType ct = checkType();

			T tail = type.create(team).self();
			tail.grown(true);
			tail.set(
					x + Angles.trnsx(rotation + 90, 0, ct.segmentOffset),
					y + Angles.trnsy(rotation + 90, 0, ct.segmentOffset)
			);
			tail.rotation = rotation;
			tail.add();
			connect(tail);
		}
	}

	@Override
	public boolean grown() {
		return grown;
	}

	/** Self explanatory, they'll return true if it is the head, if it isn't the head nor the tail, or if it is tje tail. Respectively. */
	@Override
	public boolean isHead() {
		return head == this;
	}

	@Override
	public boolean isSegment() {
		return head != this && tail != this;
	}

	@Override
	public boolean isTail() {
		return tail == this;
	}

	@Override
	public float chainTime() {
		return chainTime;
	}

	@Override
	public float growTime() {
		return growTime;
	}

	@Override
	public Unit child() {
		return child;
	}

	@Override
	public Unit head() {
		return head;
	}

	@Override
	public Unit parent() {
		return parent;
	}

	@Override
	public Unit tail() {
		return tail;
	}

	/** It moves whenever the head moves. */
	@Override
	public boolean moving() {
		return head.vel.len() > 0.01f;
	}

	/** Read parent and child id. */
	@Override
	public void read(Reads read) {
		parentID = read.i();
		childID = read.i();

		super.read(read);
	}

	/** Save parent and child id to be read later. */
	@Override
	public void write(Writes write) {
		write.i(parent == null ? -1 : parent.id);
		write.i(child == null ? -1 : child.id);

		super.write(write);
	}

	@Override
	public void readSync(Reads read) {
		parentID = read.i();
		childID = read.i();

		super.readSync(read);
	}

	@Override
	public void writeSync(Writes write) {
		write.i(parent == null ? -1 : parent.id);
		write.i(child == null ? -1 : child.id);

		super.writeSync(write);
	}

	/** Split the chain or kill the whole chain if an unit is removed. */
	@Override
	public void remove() {
		if (!added) return;

		Groups.all.removeIndex(this, index__all);
		index__all = -1;
		Groups.unit.removeIndex(this, index__unit);
		index__unit = -1;
		Groups.sync.removeIndex(this, index__sync);
		index__sync = -1;
		Groups.draw.removeIndex(this, index__draw);
		index__draw = -1;

		added = false;

		if (Vars.net.client()) {
			Vars.netClient.addRemovedEntity(id());
		}

		team.data().updateCount(type, -1);
		controller.removed(this);
		if (trail != null && trail.size() > 0) {
			Fx.trailFade.at(x, y, trail.width(), type.trailColor == null ? team.color : type.trailColor, trail.copy());
		}

		for (WeaponMount mount : mounts) {
			if (mount.weapon.continuous && mount.bullet != null && mount.bullet.owner == this) {
				mount.bullet.time = mount.bullet.lifetime - 10f;
				mount.bullet = null;
			}
			if (mount.sound != null) {
				mount.sound.stop();
			}
		}

		ChainedUnitType cu = checkType();
		if (cu.splittable) {
			if (parent != null) {
				((Chainedc) parent).child(null);
				((Chainedc) parent).consForward(u -> {
					u.tail(parent);
					u.setupWeapons(u.type);
				});
			}
			if (child != null) {
				((Chainedc) child).parent(null);
				((Chainedc) child).consBackwards(u -> {
					u.head(child);
					u.setupWeapons(u.type);
				});
			}
			if (parent != null && child != null) cu.splitSound.at(x, y);
		} else {
			if (parent != null) ((Chainedc) parent).consForward(Unitc::kill);
			if (child != null) ((Chainedc) child).consBackwards(Unitc::kill);
		}
	}

	/** Updates the mounts to be based on the unit's position in the chain. Called when the chain connects. */
	@Override
	public void setupWeapons(UnitType def) {
		ChainedUnitType cu = checkType();
		Seq<Weapon> weapons = cu.chainWeapons.get(cu.weaponsIndex.get(this));
		mounts = new WeaponMount[weapons.size];
		for (int i = 0; i < mounts.length; i++) {
			mounts[i] = weapons.get(i).mountType.get(weapons.get(i));
		}
	}

	/** Add proper solidity for everything because i can't do it better than this. */
	@Override
	public SolidPred solidity() {
		if (!isHead()) return null;
		if (this instanceof Mechc) return EntityCollisions::solid;
		if (this instanceof Legsc) return type.allowLegStep ? EntityCollisions::legsSolid : EntityCollisions::solid;
		if (this instanceof WaterMovec) return EntityCollisions::waterSolid;
		return null;
	}

	/** Connect the units together after read. */
	@Override
	public void update() {
		super.update();

		if ((moving() || net.client())) {
			float len = deltaLen();
			walk += len;
			baseRotation = Angles.moveToward(baseRotation, deltaAngle(), type().baseRotateSpeed * Mathf.clamp(len / type().speed / Time.delta) * Time.delta);
		}

		if (parentID != -1) {
			if (parent == null && Groups.unit.getByID(parentID) != null) ((Chainedc) Groups.unit.getByID(parentID)).connect(this);
			parentID = -1;
		}
		if (childID != -1) {
			if (child == null && Groups.unit.getByID(childID) != null) connect(Groups.unit.getByID(childID));
			childID = -1;
		}

		if (isTail()) {
			ChainedUnitType cu = checkType();

			if (countForward() + 1 < cu.growLength && cu.regenTime > 0) {
				growTime += Time.delta;
				if (growTime > cu.regenTime) {
					grow();
					cu.chainSound.at(x, y);
					growTime %= cu.regenTime;
				}
			}

			Tmp.r1.setCentered(
					x + Angles.trnsx(rotation + 90, 0, cu.segmentOffset),
					y + Angles.trnsy(rotation + 90, 0, cu.segmentOffset),
					cu.segmentOffset
			);
			Units.nearby(Tmp.r1, u -> {
				if (u instanceof Chainedc chain && chain.isHead() && u != head && countForward() + chain.countBackwards() + 2 <= cu.maxSegments && cu.chainTime > 0) {
					chainTime += Time.delta;
					if (chainTime > cu.chainTime) {
						connect(u);
						cu.chainSound.at(x, y);
						chainTime %= cu.chainTime;
					}
				}
			});

			if (countForward() + 1 < cu.minSegments) {
				consBackwards(Unitc::kill);
			}
		}

		updateChain();
	}

	/** Updates the position and rotation of each segment in the chain. */
	public void updateChain() {
		ChainedUnitType cu = checkType();
		if (isHead()) consBackwards(c -> {
			if (c.parent() != null) {
				Tmp.v1.set(c).sub(c.parent()).nor().scl(cu.segmentOffset);
				float angleDst = Mathm.angleDistSigned(Tmp.v1.angle(), c.parent().rotation + 180);
				if (Math.abs(angleDst) > cu.angleLimit) {
					Tmp.v1.rotate(-Tmp.v1.angle() + c.parent().rotation + 180 + (angleDst > 0 ? cu.angleLimit : -cu.angleLimit));
				}
				Tmp.v1.add(c.parent());
				c.set(Tmp.v1.x, Tmp.v1.y);
				c.rotation = c.angleTo(c.parent());
			}
		});
	}

	@Override
	public float baseRotation() {
		return baseRotation;
	}

	@Override
	public float walk() {
		return walk;
	}

	@Override
	public void baseRotation(float value) {
		baseRotation = value;
	}

	@Override
	public void walk(float value) {
		walk = value;
	}
}
