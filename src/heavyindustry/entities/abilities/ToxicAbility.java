package heavyindustry.entities.abilities;

import arc.graphics.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.type.*;

public class ToxicAbility extends Ability {
    public float damage = 1f;
    public float reload = 60f;
    public float range = 30f;

    public StatusEffect status = StatusEffects.corroded;

    public float i, j = 60f;

    public ToxicAbility() {}

    public ToxicAbility(float a, float b, float c) {
        damage = a;
        reload = b;
        range = c;
    }

    @Override
    public void update(Unit unit) {
        i += Time.delta;
        j += Time.delta;

        if (i >= reload) {
            Units.nearby(null, unit.x, unit.y, range, other -> {
                other.health -= damage;
                other.apply(status, 60f * 15f);
            });
            Units.nearbyBuildings(unit.x, unit.y, range, b -> {
                b.health -= damage / 4f;
                if (b.health <= 0f) {
                    b.kill();
                }
            });
            i = 0f;
        }
        if (j >= 15f) {
            Fx.titanSmoke.at(
                    unit.x + Mathf.range(range * 0.7071f),
                    unit.y + Mathf.range(range * 0.7071f),
                    Color.valueOf("92ab117f")
            );
            j -= 15f;
        }
    }
}