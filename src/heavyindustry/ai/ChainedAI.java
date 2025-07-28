package heavyindustry.ai;

import arc.util.Tmp;
import heavyindustry.gen.Chainedc;
import mindustry.entities.units.AIController;
import mindustry.gen.Player;
import mindustry.type.Weapon;

public class ChainedAI extends AIController {
	@Override
	public void updateWeapons() {
		if (unit instanceof Chainedc cast && cast.head().controller() instanceof Player p) {
			unit.isShooting = p.unit().isShooting;

			for (var mount : unit.mounts) {
				Weapon weapon = mount.weapon;

				//let uncontrollable weapons do their own thing
				if (!weapon.controllable || weapon.noAttack) continue;

				if (!weapon.aiControllable) {
					mount.rotate = false;
					continue;
				}

				mount.rotate = true;
				Tmp.v1.trns(unit.rotation + mount.weapon.baseRotation, 5f);
				mount.aimX = p.unit().aimX();
				mount.aimY = p.unit().aimY();

				unit.aimX = mount.aimX;
				unit.aimY = mount.aimY;
			}
		} else {
			super.updateWeapons();
		}
	}
}
