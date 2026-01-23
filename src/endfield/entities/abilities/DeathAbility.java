package endfield.entities.abilities;

import mindustry.entities.abilities.Ability;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import org.jetbrains.annotations.Nullable;

public class DeathAbility extends Ability {
	protected @Nullable Unit ownerUnit;
	protected @Nullable Building ownerBuild;

	public float length = 200f;

	@Override
	public void update(Unit unit) {
		Groups.bullet.intersect(unit.x - length, unit.y - length, length * 2, length * 2, bullet -> {
			if (bullet.team != unit.team && unit.within(bullet, 200)) {
				ownerUnit = null;
				if (bullet.owner instanceof Unit u) ownerUnit = u;
				if (bullet.type.damage > unit.maxHealth / 2f || bullet.type.splashDamage > unit.maxHealth / 2f || bullet.type.lightningDamage > unit.maxHealth / 2f) {
					if (ownerUnit != null) ownerUnit.kill();
					bullet.remove();
				}
				if (ownerUnit != null && (ownerUnit.maxHealth > unit.maxHealth * 2 || ownerUnit.type.armor >= unit.type.armor * 2)) ownerUnit.kill();

				ownerBuild = null;
				if (bullet.owner instanceof Building b) ownerBuild = b;
				if (bullet.type.damage > unit.maxHealth / 2f || bullet.type.splashDamage > unit.maxHealth / 2f || bullet.type.lightningDamage > unit.maxHealth / 2f) {
					if (ownerBuild != null) ownerBuild.kill();
					bullet.remove();
				}
				if (ownerBuild != null && ownerBuild.health > unit.maxHealth * 2) ownerBuild.kill();
			}
		});
	}

	@Override
	public String getBundle() {
		return "ability.death";
	}
}
