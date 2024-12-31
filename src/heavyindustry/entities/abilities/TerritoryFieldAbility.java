package heavyindustry.entities.abilities;

import arc.scene.ui.layout.*;
import arc.util.*;
import heavyindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class TerritoryFieldAbility extends Ability {
    public float range;
    public float healAm;
    public float damageAm;

    public float reload = 60f * 1.5f;

    public boolean active = true;
    public boolean open = false;

    public float applyParticleChance = 13f;

    protected float timer;

    public TerritoryFieldAbility() {}

    public TerritoryFieldAbility(float range, float healAm, float damageAm) {
        this.range = range;
        this.healAm = healAm;
        this.damageAm = damageAm;
    }

    @Override
    public String localized() {
        return bundle.get("ability.territory-field-ability");
    }

    @Override
    public void addStats(Table t) {
        super.addStats(t);
        t.add(bundle.format("ability.territory-field-ability-range", range / tilesize));
        if (healAm > 0) {
            t.row();
            t.add(bundle.format("ability.territory-field-ability-heal", healAm));
        }
        if (damageAm > 0) {
            t.row();
            t.add(bundle.format("ability.territory-field-ability-damage", damageAm));
        }
        if (active) {
            t.row();
            t.add(bundle.get("ability.territory-field-ability-suppression"));
        }
    }

    @Override
    public void update(Unit unit) {
        Units.nearby(unit.team, unit.x, unit.y, range, u -> {
            if (u != unit) {
                u.apply(HIStatusEffects.territoryFieldIncrease, 60);
                if (healAm > 0 && !u.dead && u.health < u.maxHealth) u.heal((healAm / 60f) * Time.delta);
            }
        });
        Units.nearbyEnemies(unit.team, unit.x, unit.y, range, u -> {
            u.apply(HIStatusEffects.territoryFieldSuppress, 60);
            if (damageAm > 0 && !u.dead && u.targetable(unit.team)) u.damage((damageAm / 60f) * Time.delta);
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
