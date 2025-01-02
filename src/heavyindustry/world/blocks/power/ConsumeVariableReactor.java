package heavyindustry.world.blocks.power;

import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;

public class ConsumeVariableReactor extends VariableReactor {
    public float itemDuration = 120f;

    public ConsumeVariableReactor(String name) {
        super(name);
        hasItems = true;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.productionTime, itemDuration / 60, StatUnit.seconds);
    }

    public class ConsumeVariableReactorBuild extends VariableReactorBuild {
        public float consumeItemTimer;

        @Override
        public void update() {
            super.update();

            heat = calculateHeat(sideHeat);
            productionEfficiency = efficiency;
            warmup = Mathf.lerpDelta(warmup, productionEfficiency > 0 ? 1f : 0f, warmupSpeed);
            if (instability >= 1) kill();

            totalProgress += productionEfficiency * Time.delta;
            if (Mathf.chanceDelta(effectChance * warmup)) {
                effect.at(x, y, effectColor);
                Damage.damage(team, x, y, 40f, 100f, true, true, true, true, null);
            }

            consumeItemTimer += Time.delta * efficiency;
            if (efficiency > 0 && consumeItemTimer >= itemDuration) {
                consumeItemTimer %= itemDuration;
                consume();
            }
        }
    }
}
