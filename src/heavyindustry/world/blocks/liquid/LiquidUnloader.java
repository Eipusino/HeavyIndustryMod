package heavyindustry.world.blocks.liquid;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.liquid.LiquidBlock.*;
import mindustry.world.meta.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class LiquidUnloader extends Block {
    public TextureRegion centerRegion;
    public float speed = 3f;

    public LiquidUnloader(String name) {
        super(name);

        update = true;
        solid = true;
        hasLiquids = true;
        liquidCapacity = 10f;
        configurable = true;
        outputsLiquid = true;
        saveConfig = true;
        noUpdateDisabled = true;
        displayFlow = false;
        group = BlockGroup.liquids;
        envEnabled = Env.any;
        clearOnDoubleTap = true;

        config(Liquid.class, (LiquidUnloaderBuild tile, Liquid liquid) -> tile.sortLiquid = liquid);
        configClear((LiquidUnloaderBuild tile) -> tile.sortLiquid = null);
    }

    @Override
    public void load() {
        super.load();
        centerRegion = atlas.find(name + "-center");
    }

    @Override
    public void setBars() {
        super.setBars();

        removeBar("liquid");
    }

    @Override
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list) {
        drawPlanConfigCenter(plan, plan.config, name + "-center", true);
    }

    public class LiquidUnloaderBuild extends Building {
        public Liquid sortLiquid = null;
        public Liquid lastSort = null;
        public Building dumpingTo = null;
        public float offset = 0f;

        @Override
        public void updateTile() {
            if (lastSort != sortLiquid) {
                liquids.clear();
                lastSort = sortLiquid;
            }
            for (int i = 0; i < proximity.size; i++) {
                int pos = (int) (offset + i) % proximity.size;
                Building other = proximity.get(pos);

                if (other.interactable(team) && other.block.hasLiquids && !(other instanceof LiquidBuild && other.block.size == 1) && sortLiquid != null && other.liquids.get(sortLiquid) > 0) {
                    dumpingTo = other;
                    if (liquids.get(sortLiquid) < block.liquidCapacity) {
                        float amount = Math.min(speed, other.liquids.get(sortLiquid));
                        liquids.add(sortLiquid, amount);
                        other.liquids.remove(sortLiquid, amount);
                    }
                }
            }
            if (proximity.any()) {
                offset++;
                offset %= proximity.size;
            }
            dumpLiquid(liquids.current());
        }

        @Override
        public boolean canDumpLiquid(Building to, Liquid liquid) {
            return to != dumpingTo;
        }

        @Override
        public void draw() {
            super.draw();
            Draw.color(sortLiquid == null ? Color.clear : sortLiquid.color);
            Draw.rect(centerRegion, x, y);
            Draw.color();
        }

        @Override
        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(LiquidUnloader.this, table, content.liquids(), () -> sortLiquid, this::configure);
        }

        @Override
        public Liquid config() {
            return sortLiquid;
        }

        @Override
        public byte version() {
            return 1;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.s(sortLiquid == null ? -1 : sortLiquid.id);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            int id = revision == 1 ? read.s() : read.b();
            sortLiquid = id == -1 ? null : content.liquid(id);
        }
    }
}
