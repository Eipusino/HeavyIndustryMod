package heavyindustry.world.blocks.heat;

import arc.*;
import arc.math.*;
import arc.util.io.*;
import heavyindustry.world.blocks.production.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.heat.*;

/**
 * Why is the heat separated separately? Mainly to avoid various strange small problems.
 * <p>At present, there is no testing. If there are any bugs, please make sure to report them to the author.
 *
 * @author Eipusino
 */
public class HeatMultiCrafter extends MultiCrafter {
    protected boolean consumeHeat, outputHeat;

    public HeatMultiCrafter(String name) {
        super(name);

        rotateDraw = false;
        rotate = true;
        canOverdrive = false;
        drawArrow = true;
    }

    @Override
    public void init() {
        for (Formula product : products) {
            if (product.heatOutput > 0f) {
                outputHeat = true;
            }
            if (product.heatRequirement > 0f) {
                consumeHeat = true;
            }
        }
        super.init();
    }

    @Override
    public void setBars() {
        super.setBars();

        if (outputHeat) addBar("heatoutput", (HeatMultiCrafterBuild tile) -> new Bar("bar.heat", Pal.lightOrange, () -> tile.formula != null ? tile.heat / tile.formula.heatOutput : 0));
        if (consumeHeat) addBar("heatconsume", (HeatMultiCrafterBuild tile) -> new Bar(
                () -> Core.bundle.format("bar.heatpercent", tile.heatRequirement, tile.formula != null ? Math.min((tile.heatRequirement / tile.formula.heatRequirement * 100), tile.formula.maxHeatEfficiency) : 0),
                () -> Pal.lightOrange,
                () -> tile.formula != null ? tile.heatRequirement / tile.formula.heatRequirement : 0)
        );
    }

    public class HeatMultiCrafterBuild extends MultiCrafterBuild implements HeatBlock, HeatConsumer {
        public float heat;
        public float heatRequirement;
        public float[] sideHeat = new float[4];

        @Override
        public void updateTile() {
            super.updateTile();
            if (formula == null) return;

            heat = Mathf.approachDelta(heat, formula.heatOutput * efficiency, formula.warmupRate * delta());

            if (formula.heatRequirement > 0) {
                heatRequirement = calculateHeat(sideHeat);
            }
        }

        @Override
        public void updateEfficiencyMultiplier() {
            super.updateEfficiencyMultiplier();
            if (formula == null) return;

            if (formula.heatRequirement > 0) {
                efficiency *= Math.min(Math.max(heatRequirement / formula.heatRequirement, cheating() ? formula.maxHeatEfficiency : 0f), formula.maxHeatEfficiency);
            }
        }

        @Override
        public float heat() {
            return heat;
        }

        @Override
        public float heatFrac() {
            return formula != null ? heat / formula.heatOutput : 0f;
        }

        @Override
        public float[] sideHeat() {
            return sideHeat;
        }

        @Override
        public float heatRequirement() {
            return formula != null ? formula.heatRequirement : 0f;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(heat);
            write.f(heatRequirement);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            heat = read.f();
            heatRequirement = read.f();
        }
    }
}
