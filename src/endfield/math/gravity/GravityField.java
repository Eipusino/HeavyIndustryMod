package endfield.math.gravity;

import arc.func.Boolf;
import arc.func.Func;
import arc.math.geom.Vec2;
import arc.util.Time;
import endfield.util.CollectionObjectMap;
import endfield.util.CollectionObjectSet;

/**
 * A container for simulating the gravitational field,
 * which records some behaviors of the gravitational field
 *
 * @since 1.0.4
 */
public class GravityField {
	public static final float GRAV_CONST = 0.667259f;

	static final Vec2 tmp = new Vec2();

	public final GravitySystem system;

	final CollectionObjectSet<GravityField> otherFields = new CollectionObjectSet<>(GravityField.class);
	final CollectionObjectMap<GravityField, Vec2> bufferAccelerations = new CollectionObjectMap<>(GravityField.class, Vec2.class);

	public GravityField(GravitySystem sys) {
		system = sys;
	}

	/**
	 * Set another associated field, which will reset the current field state and then create a new association relationship with the given field output criteria.
	 *
	 * @param itr	This iterator needs to iterate over all the objects that need to be processed to bind to a gravitational field.
	 * @param filter Element filter
	 * @param getter Method for creating an associated gravitational field for a certain target
	 */
	public <T> void setAssociatedFields(Iterable<T> itr, Boolf<T> filter, Func<T, GravityField> getter) {
		for (GravityField field : otherFields) {
			remove(field);
		}
		for (T t : itr) {
			if (filter.get(t)) add(getter.get(t));
		}
	}

	/**
	 * Set associations with all other fields,
	 * which will reset the current field state and create new associations with the given field output criteria.
	 *
	 * @param itr	This iterator needs to iterate over all other gravitational fields that need to be processed
	 * @param filter Element filter
	 */
	public <T extends GravityField> void setAssociatedFields(Iterable<T> itr, Boolf<T> filter) {
		for (GravityField field : otherFields) {
			remove(field);
		}
		for (T t : itr) {
			if (filter.get(t)) add(t);
		}
	}

	/** Add a gravitational field to the associated field. */
	public void add(GravityField field) {
		if (field == null) return;
		otherFields.add(field);
		field.otherFields.add(this);
	}

	/** Cancel association with the target field. */
	public void remove(GravityField field) {
		if (field == null) return;
		otherFields.remove(field);
		field.otherFields.remove(this);
	}

	/** Unlink oneself from all other gravitational fields associated with it. */
	public void remove() {
		for (GravityField field : otherFields) {
			remove(field);
		}
	}

	/**
	 * Update the gravity system and pass the results through the gravity system method gravityUpdate.
	 * Do not call this method multiple times within one game refresh.
	 */
	public void update() {
		Vec2 speedDelta;
		float distance;
		float force;
		float delta;

		tmp.setZero();
		for (GravityField field : otherFields) {
			if ((speedDelta = bufferAccelerations.get(field)) == null || speedDelta.isZero()) {
				if (speedDelta == null) speedDelta = new Vec2();
				GravitySystem sys = field.system;

				distance = speedDelta.set(sys.position()).sub(system.position()).len();
				force = GRAV_CONST * sys.mass() * system.mass() / (distance * distance);
				delta = 60 / Time.delta;
				bufferAccelerations.put(field, speedDelta.setLength(force / system.mass() / delta));
				field.bufferAccelerations.get(this, Vec2::new).set(speedDelta).setLength(force / sys.mass() / delta).scl(-1);
			}
			tmp.add(speedDelta);
		}

		system.gravityUpdate(tmp);
		clearBuffer(false);
	}

	public void clearBuffer(boolean all) {
		if (all) {
			for (GravityField field : otherFields) {
				field.clearBuffer(false);
			}
		}
		for (Vec2 vec2 : bufferAccelerations.values()) {
			vec2.setZero();
		}
	}
}
