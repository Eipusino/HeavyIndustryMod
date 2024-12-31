package heavyindustry.world.draw;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import heavyindustry.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.draw.*;

import static arc.Core.*;
import static heavyindustry.util.SpriteUtils.*;
import static heavyindustry.util.Utils.*;

@SuppressWarnings("unchecked")
public class DrawDirSpliceBlock<E extends Building> extends DrawBlock {
    public TextureRegion[] regions = new TextureRegion[16];
    public Intf<E> spliceBits = e -> 0;
    public Boolf2<BuildPlan, BuildPlan> planSplicer = (plan, other) -> false;

    public int size = 1;
    public float layerOffset = 0.0001f;
    public boolean layerRec = true;

    public boolean split = false;
    public boolean simpleSpliceRegion = false;
    public String suffix = "-splice";

    @Override
    public void load(Block block) {
        if (split) {
            regions = split(block.name + suffix, size * 32, 0);
        } else {
            Pixmap[] splicers = new Pixmap[4];

            if (simpleSpliceRegion) {
                PixmapRegion region = atlas.getPixmap(block.name + suffix);
                Pixmap pixmap = region.crop();
                for (int i = 0; i < 4; i++) {
                    Pixmap m = i == 1 || i == 2 ? rotatePixmap90(pixmap.flipY(), i) : rotatePixmap90(pixmap, i);
                    splicers[i] = m;
                }
            } else {
                for (int i = 0; i < splicers.length; i++) {
                    splicers[i] = atlas.getPixmap(block.name + suffix + "-" + i).crop();
                }
            }

            for (int i = 0; i < regions.length; i++) {
                regions[i] = getSpliceRegion(splicers, i);
            }

            for (Pixmap pixmap : splicers) {
                pixmap.dispose();
            }
        }
    }

    protected TextureRegion getSpliceRegion(Pixmap[] splicers, int i) {
        int move = 0;

        Pixmap map = new Pixmap(splicers[move].width, splicers[move].height);
        while (1 << move <= i) {
            int bit = 1 << move;
            if ((i & bit) != 0) {
                map.draw(splicers[move], true);
            }
            move++;
        }

        Pixmaps.bleed(map, 2);
        Texture tex = new Texture(map);
        tex.setFilter(Texture.TextureFilter.linear);
        return new TextureRegion(tex);
    }

    @Override
    public void draw(Building build) {
        float z = Draw.z();
        Draw.z(z + layerOffset);
        Draw.rect(regions[spliceBits.get((E) build)], build.x, build.y);
        if (layerRec) Draw.z(z);
        else Draw.z(z + 0.0001f);

    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        int bits = 0;
        Block planBlock = plan.block;

        t:
        for (int i = 0; i < 4; i++) {
            Block other = null;
            for (Point2 p : DirEdges.get(plan.block.size, i)) {
                int x = plan.x + p.x;
                int y = plan.y + p.y;
                BuildPlan[] target = {null};

                list.each(pl -> {
                    if (target[0] != null) return;
                    if (pl.x == x && pl.y == y) {
                        target[0] = pl;
                    }
                });

                if (target[0] == null) continue t;

                if (other == null) {
                    if (planSplicer.get(plan, target[0])) {
                        other = target[0].block;
                    } else {
                        bits &= ~(1 << i);
                        continue t;
                    }
                } else if (other != planBlock || !planSplicer.get(plan, target[0])) {
                    bits &= ~(1 << i);
                    continue t;
                }
            }
            bits |= 1 << i;
        }

        Draw.rect(regions[bits], plan.drawx(), plan.drawy());
    }
}
