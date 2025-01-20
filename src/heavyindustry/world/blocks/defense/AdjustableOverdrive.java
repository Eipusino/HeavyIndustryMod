package heavyindustry.world.blocks.defense;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class AdjustableOverdrive extends Block {
    protected static final float MAX = 100000f, MIN = 1f, INIT_MASK = 1000000f;

    protected static final float[] commandMap = {1f, 10f, 100f, 1000f};

    public TextureRegion topRegion;

    public float lastNumber = 200f;
    public float range = 120f, reload = 30f;
    public Color baseColor = Color.valueOf("feb380"), phaseColor = Color.valueOf("ff9ed5");

    public AdjustableOverdrive(String name) {
        super(name);
        update = true;
        solid = true;
        configurable = true;
        canOverdrive = false;
    }

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top");
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.accent);
    }

    public class AdjustableOverdriveBuild extends Building {
        public float heat, phaseHeat;

        //It stores integers multiplied by 100
        public float speedTo = 300f;

        @Override
        public void playerPlaced(Object config) {
            //Calculate the minimum number of times needed to reach lastNumber and send the specified number of configurations.
            Core.app.post(() -> configure(lastNumber + INIT_MASK));
        }

        @Override
        public void configured(Unit builder, Object value) {
            //Less than 100 is considered a decrease command, and greater than 1000000 (seven digits) is considered initialization.
            if (value instanceof Number number) {
                int in = number.intValue();
                if (in > INIT_MASK) {
                    speedTo = in - INIT_MASK;
                } else if (in >= 100) {
                    speedTo = Math.max(MIN, speedTo - commandMap[in - 100]);
                    lastNumber = speedTo;
                } else {
                    speedTo = Math.min(MAX, speedTo + commandMap[in]);
                    lastNumber = speedTo;
                }
            }
        }

        @Override
        public void drawLight() {
            Drawf.light(x, y, 50f * efficiency(), baseColor, 0.7f * efficiency());
        }

        @Override
        public void drawSelect() {
            indexer.eachBlock(this, range, other -> other.block.canOverdrive, other -> {
                Color tmp = Tmp.c1.set(baseColor);
                tmp.a = Mathf.absin(4f, 1f);
                Drawf.selected(other, tmp);
            });
            Drawf.dashCircle(x, y, range, baseColor);
        }

        @Override
        public void draw() {
            float f = 1 - (Time.time / 100) % 1;
            Draw.color(baseColor, phaseColor, phaseHeat);
            Draw.alpha(heat * Mathf.absin(Time.time, 10f, 1f) * 0.5f);
            Draw.rect(topRegion, tile.drawx(), tile.drawy());
            Draw.alpha(1f);
            Lines.stroke((2 * f + 0.2f) * heat);
            Lines.square(tile.drawx(), tile.drawy(), (1 - f) * 8);
            Draw.reset();
        }

        @Override
        public void updateTile() {
            indexer.eachBlock(this, range, pared -> true, other -> other.applySlowdown(1f, reload + 1f));
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(heat);
            write.f(phaseHeat);
            write.f(speedTo);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            heat = read.f();
            phaseHeat = read.f();
            speedTo = read.f();
        }
    }
}
