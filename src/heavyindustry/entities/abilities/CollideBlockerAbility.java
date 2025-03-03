package heavyindustry.entities.abilities;

import mindustry.gen.Hitboxc;
import mindustry.gen.Unit;

/**
 * interface, Not directly usable.
 *
 * @since 1.0.6
 */
public interface CollideBlockerAbility {
	boolean blockedCollides(Unit unit, Hitboxc other);
}
