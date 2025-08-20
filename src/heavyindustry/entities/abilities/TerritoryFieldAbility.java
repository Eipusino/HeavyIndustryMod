package heavyindustry.entities.abilities;

import arc.Core;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import heavyindustry.content.HStatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

import static mindustry.Vars.tilesize;

public class TerritoryFieldAbility extends Ability {
	public float range;
	public float healAmount;
	public float damageAmount;

	public float reload = 60f * 1.5f;

	public boolean active = true;
	public boolean open = false;

	public float applyParticleChance = 13f;

	protected float timer;

	public TerritoryFieldAbility() {}

	public TerritoryFieldAbility(float ran, float helAmo, float dmgAmo) {
		range = ran;
		healAmount = helAmo;
		damageAmount = dmgAmo;
	}

	@Override
	public String getBundle() {
		return "ability.territory-field-ability";
	}

	@Override
	public void addStats(Table t) {
		super.addStats(t);
		t.add(Core.bundle.format("ability.territory-field-ability-range", range / tilesize));
		if (healAmount > 0) {
			t.row();
			t.add(Core.bundle.format("ability.territory-field-ability-heal", healAmount));
		}
		if (damageAmount > 0) {
			t.row();
			t.add(Core.bundle.format("ability.territory-field-ability-damage", damageAmount));
		}
		if (active) {
			t.row();
			t.add(Core.bundle.get("ability.territory-field-ability-suppression"));
		}
	}

	@Override
	public void update(Unit unit) {
		Units.nearby(unit.team, unit.x, unit.y, range, u -> {
			if (u != unit) {
				u.apply(HStatusEffects.territoryFieldIncrease, 60);
				if (healAmount > 0 && !u.dead && u.health < u.maxHealth) u.heal((healAmount / 60f) * Time.delta);
			}
		});
		Units.nearbyEnemies(unit.team, unit.x, unit.y, range, u -> {
			u.apply(HStatusEffects.territoryFieldSuppress, 60);
			if (damageAmount > 0 && !u.dead && u.targetable(unit.team)) u.damage((damageAmount / 60f) * Time.delta);
		});

		if (open) {
			Units.nearbyEnemies(unit.team, unit.x, unit.y, range * 2, u -> {
				if (!u.dead && u.type != null && (u.health > unit.maxHealth * 2 || u.type.armor >= unit.type.armor * 2)) {
					u.health -= u.health;
					u.remove();
				}
			});
		}

		if (!active) return;

		if ((timer += Time.delta) >= reload) {
			Damage.applySuppression(unit.team, unit.x, unit.y, range, reload, reload, applyParticleChance, unit);
			timer = 0f;
		}
	}
}
