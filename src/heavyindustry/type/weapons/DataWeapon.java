package heavyindustry.type.weapons;

import heavyindustry.util.CollectionObjectMap;
import heavyindustry.util.ExtraVariable;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.type.Weapon;

import java.util.Map;

/**
 * A weapon with an independent variable space that automatically replaces the corresponding
 * {@link WeaponMount} of a unit with {@link DataWeaponMount} that implements the {@link ExtraVariable}
 * interface, and provides a series of behaviors for accessing the independent variable area.
 * <p>This is usually very useful for highly customizable weapons.
 */
public class DataWeapon extends Weapon {
	public DataWeapon() {
		super("");
	}

	public DataWeapon(String name) {
		super(name);
	}

	@Override
	public void update(Unit unit, WeaponMount mount) {
		if (!(mount instanceof DataWeaponMount)) {
			for (int i = 0; i < unit.mounts.length; i++) {
				if (unit.mounts[i] == mount) {
					DataWeaponMount m;
					unit.mounts[i] = m = new DataWeaponMount(this);
					init(unit, m);
					break;
				}
			}
		}

		super.update(unit, mount);

		if (mount instanceof DataWeaponMount m) {
			update(unit, m);
		}
	}

	/** Initialization of weapons, this method is typically used to allocate initial variables for weapons. */
	public void init(Unit unit, DataWeaponMount mount) {}

	/**
	 * The behavior of accessing variables should be described in the coverage of the update method that
	 * has undergone type checking and conversion.
	 */
	public void update(Unit unit, DataWeaponMount mount) {}

	@Override
	public void draw(Unit unit, WeaponMount mount) {
		super.draw(unit, mount);
		if (mount instanceof DataWeaponMount m) {
			draw(unit, m);
		}
	}

	/**
	 * The draw method that has undergone type checking and conversion should describe the drawing
	 * behavior of accessing variables in its coverage.
	 */
	public void draw(Unit unit, DataWeaponMount mount) {}

	@Override
	protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation) {
		super.shoot(unit, mount, shootX, shootY, rotation);

		if (mount instanceof DataWeaponMount m) {
			shoot(unit, m, shootX, shootY, rotation);
		}
	}

	protected void shoot(Unit unit, DataWeaponMount mount, float shootX, float shootY, float rotation) {}

	public static class DataWeaponMount extends WeaponMount implements ExtraVariable {
		public Map<String, Object> extraVar = new CollectionObjectMap<>(String.class, Object.class);

		public DataWeaponMount(Weapon weapon) {
			super(weapon);
		}

		@Override
		public Map<String, Object> extra() {
			return extraVar;
		}
	}
}
