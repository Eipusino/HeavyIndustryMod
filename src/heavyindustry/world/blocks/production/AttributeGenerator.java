package heavyindustry.world.blocks.production;

import arc.math.*;
import arc.util.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;

import static arc.Core.*;

/**
 * Same as GeneratorCrafter, but power output is affected by Attribute.
 * <p>Please do not set {@link AttributeGenerator#consumePower(float)} for it, it is foolish.
 *
 * @author Eipusino
 */
public class AttributeGenerator extends AttributeCrafter {
    public float powerProduction = 1f;

    public AttributeGenerator(String name) {
        super(name);
        hasPower = true;
        consumesPower = false;
        outputsPower = true;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.basePowerGeneration, powerProduction * 60f, StatUnit.powerSecond);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("power", (AttributeGeneratorBuild tile) -> new Bar(
                () -> bundle.format("bar.poweroutput", Strings.fixed(tile.getPowerProduction() * 60f * tile.timeScale(), 1)),
                () -> Pal.powerBar,
                () -> Mathf.num(tile.efficiency > 0f)
        ));
    }

    public class AttributeGeneratorBuild extends AttributeCrafterBuild {
        @Override
        public float getPowerProduction() {
            return efficiency > 0f ? powerProduction * efficiencyMultiplier() : 0f;
        }
    }
}
