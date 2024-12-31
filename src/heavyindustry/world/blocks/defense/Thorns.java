package heavyindustry.world.blocks.defense;

import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.meta.*;

/**
 * Causing damage to units walking on it.
 *
 * @author Eipusino
 */
public class Thorns extends Block {
    public int timerDamage = timers++;
    public float cooldown = 30f, damage = 8f;

    public Thorns(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.damage, 60f / cooldown * damage, StatUnit.perSecond);
    }

    public class ThornsBuild extends Building {
        @Override
        public void draw() {
            Draw.color(team.color);
            Draw.alpha(0.22f);
            Fill.rect(x, y, 2f, 2f);
            Draw.color();
        }

        @Override
        public void unitOn(Unit unit) {
            if (timer.get(timerDamage, cooldown)) {
                unit.damage(damage);
                damage(damage);
            }
        }
    }
}
