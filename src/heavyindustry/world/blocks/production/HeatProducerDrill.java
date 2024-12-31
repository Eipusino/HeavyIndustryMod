package heavyindustry.world.blocks.production;

import arc.math.*;
import arc.util.io.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

public class HeatProducerDrill extends DrawerDrill {
    public float heatOutput = 5f;

    public boolean needHeatDissipation = false;
    public float heatDamage = 1f;

    public HeatProducerDrill(String name) {
        super(name);
        drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput());
        rotateDraw = false;
        rotate = true;
        canOverdrive = false;
        drawArrow = true;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.output, heatOutput, StatUnit.heatUnits);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("heat", (HeatProducerDrillBuild tile) -> new Bar("bar.heat", Pal.lightOrange, tile::heatFrac));
    }

    public class HeatProducerDrillBuild extends DrawerDrillBuild implements HeatBlock {
        public float heat;

        @Override
        public void updateTile() {
            super.updateTile();
            heat = lastDrillSpeed == 0 ? Mathf.approachDelta(heat, 0f, 0.3f * delta()) : Mathf.approachDelta(heat, heatOutput, 0.3f * delta());

            if (heat == heatOutput && needHeatDissipation && !nearbyHeatConsumers()) damage(heatDamage);
        }

        @Override
        public float heatFrac() {
            return heat / heatOutput;
        }

        @Override
        public float heat() {
            return heat;
        }

        public boolean nearbyHeatConsumers() {
            return proximity.contains(b -> b instanceof HeatConsumer);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(heat);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            heat = read.f();
        }
    }
}
