package heavyindustry.world.blocks.liquid;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.graphics.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class TubeConduit extends BeltConduit {
    protected static final float[][] rotateOffsets = new float[][]{{0.75f, 0.75f}, {-0.75f, 0.75f}, {-0.75f, -0.75f}, {0.75f, -0.75f}};

    public TextureRegion[][] regions;

    public TubeConduit(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        regions = atlas.find(name + "-full").split(32, 32);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        int[] bits = getTiling(plan, list);

        if (bits == null) return;

        Draw.scl(bits[1], bits[2]);
        Draw.color(botColor);
        Draw.alpha(0.5f);
        Draw.rect(botParts[bits[0]], plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.reset();

        int blendbits2 = bits[0];
        if (blendbits2 == 1 && (bits[1] != 1 || bits[2] != 1)) { //Flips corner conduits
            blendbits2 = 5;
        }
        if (blendbits2 == 2 && (bits[1] != 1 || bits[2] != 1)) { //Flips T junction conduits
            blendbits2 = 6;
        }
        Draw.rect(regions[plan.rotation][blendbits2], plan.drawx(), plan.drawy());
    }

    public class TubeConduitBuild extends BeltConduitBuild {
        @Override
        public void draw() {
            int r = rotation;

            //draw extra conduits facing this one for tiling purposes
            Draw.z(Layer.blockUnder);
            for (int i = 0; i < 4; i++) {
                if ((blending & (1 << i)) != 0) {
                    int dir = r - i;
                    drawBt(x + Geometry.d4x(dir) * tilesize * 0.75f, y + Geometry.d4y(dir) * tilesize * 0.75f, 0, i == 0 ? r : dir, i != 0 ? SliceMode.bottom : SliceMode.top);

                    float rot = i == 0 ? r * 90 : (dir) * 90;
                    if (Mathf.mod(dir, 4) == 1 || Mathf.mod(dir, 4) == 2) Draw.yscl = -1;
                    Draw.rect(sliced(regions[0][0], i != 0 ? SliceMode.bottom : SliceMode.top), x + Geometry.d4x(dir) * tilesize * 0.75f, y + Geometry.d4y(dir) * tilesize * 0.75f, rot);
                }
            }

            Draw.z(Layer.block);

            Draw.scl(xscl, yscl);
            drawBt(x, y, blendbits, r, SliceMode.none);
            Draw.reset();

            int blendbits2 = blendbits;
            if (blendbits2 == 1 && (xscl != 1 || yscl != 1)) { //Flips corner conveyors
                blendbits2 = 5;
            }
            if (blendbits2 == 2 && (xscl != 1 || yscl != 1)) { //Flips T-junction conveyors
                blendbits2 = 6;
            }
            Draw.rect(regions[rotation][blendbits2], x, y);

            if (r == 1 || r == 2) Draw.yscl = -1;
            else Draw.yscl = 1;

            if (capped && capRegion.found()) Draw.rect(capRegion, x, y, rotdeg());

            if (Mathf.mod(r + 2, 4) == 1 || Mathf.mod(r + 2, 4) == 2) Draw.yscl = -1;
            else Draw.yscl = 1;

            if (backCapped && capRegion.found()) Draw.rect(capRegion, x, y, rotdeg() + 180);
        }

        @Override
        protected void drawAt(float x, float y, int bits, int rotation, SliceMode slice) {}

        protected void drawBt(float x, float y, int bits, int rotation, SliceMode slice) {
            float angle = rotation * 90f;
            Draw.color(botColor);
            Draw.rect(sliced(botParts[bits], slice), x, y, angle);

            int offset = yscl == -1 ? 3 : 0;

            int frame = liquids.current().getAnimationFrame();
            int gas = liquids.current().gas ? 1 : 0;
            float ox = 0f, oy = 0f;
            int wrapRot = (rotation + offset) % 4;
            TextureRegion liquidr = bits == 1 && padCorners ? rotateRegions[wrapRot][gas][frame] : renderer.fluidFrames[gas][frame];

            if (bits == 1 && padCorners) {
                ox = rotateOffsets[wrapRot][0];
                oy = rotateOffsets[wrapRot][1];
            }

            Drawf.liquid(sliced(liquidr, slice), x + ox, y + oy, smoothLiquid, liquids.current().color.write(Tmp.c1).a(1f));
        }
    }
}
