package endfield.type.weapons;

import arc.struct.Seq;
import arc.util.Time;
import mindustry.entities.Effect;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.type.Weapon;

public class EffectWeapon extends Weapon {
	// the list of effects
	protected Seq<Effect> effects = new Seq<>(Effect.class);
	// interval between showup of effects
	public float effectInterval = 60;
	// X of displayed effects
	public float effectX = 0;
	// Y of displayed effects
	public float effectY = 0;

	protected float effectTimer = 0f;

	public EffectWeapon(String name) {
		super(name);
	}

	public void addEffects(Effect... effect) {
		for (Effect eff : effect) {
			effects.add(eff);
		}
	}

	@Override
	public void update(Unit unit, WeaponMount mount) {
		super.update(unit, mount);
		effectTimer += Time.delta;

		if (effectTimer >= effectInterval) {
			effectTimer = 0f;
			for (Effect eff : effects) {
				eff.at(unit.x + effectX, unit.y + effectY, unit.rotation + mount.rotation);
			}
		}
	}
}
