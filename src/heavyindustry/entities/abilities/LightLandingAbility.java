package heavyindustry.entities.abilities;


import arc.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.meta.*;

public class LightLandingAbility extends Ability {
	public float damage = 1f;

	protected boolean onLand;

	public LightLandingAbility(float dmg) {
		damage = dmg;
	}

	public LightLandingAbility() {}

	@Override
	public void addStats(Table t) {
		t.add("[lightgray]" + Stat.damage.localized() + ": [white]" + damage);
	}

	@Override
	public void update(Unit unit) {
		super.update(unit); //Make like one time mechanism
		if (!unit.isFlying()) {
			if (!onLand) {
				Fx.landShock.at(unit);
				Effect.shake(1f, 1f, unit);
				for (int i = 0; i < 8; i++) {
					Time.run(Mathf.random(8f), () -> Lightning.create(unit.team(), Pal.lancerLaser, damage * Vars.state.rules.unitDamageMultiplier, unit.x, unit.y, Mathf.random(360f), 14));
				}
				onLand = true;
			}
		} else {
			onLand = false;
		}
	}

	@Override
	public String localized() {
		return Core.bundle.format("ability.lightlandability", damage);
	}
}
