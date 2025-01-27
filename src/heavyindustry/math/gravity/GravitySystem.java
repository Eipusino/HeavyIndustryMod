package heavyindustry.math.gravity;

import arc.math.geom.*;

/**
 * Gravity system interface, processing unit for gravity operations.
 *
 * @since 1.0.4
 */
public interface GravitySystem {
	/** The gravitational field excited by the system. */
	GravityField field();

	/**
	 * The total mass of the system, measured in kilotons (kt), is allowed to be negative.
	 * Two systems with the same mass symbol attract each other, otherwise they repel each other.
	 */
	float mass();

	/** Position vector of system center of gravity. */
	Vec2 position();

	/** Gravity update calls this method, passing the calculated acceleration as a parameter. */
	void gravityUpdate(Vec2 acceleration);
}
