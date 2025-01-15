package heavyindustry.world.blocks.distribution;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.meta.*;

import static arc.Core.*;
import static mindustry.Vars.*;

/**
 * literally nothing special here unoriginal ass.
 *
 * @since 1.0.6
 */
public class CoveredRouter extends Router {
    public TextureRegion topRegion;

    public CoveredRouter(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.itemsMoved, 60f / speed * itemCapacity, StatUnit.itemsSecond);
    }

    @Override
    public void load() {
        super.load();
        topRegion = atlas.find(name + "-top");
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{region, topRegion};
    }

    public class CoveredRouterBuild extends RouterBuild {
        public float progress;
        public @Nullable Item current;
        public int recDir = 0;
        public @Nullable Tile lastInput;
        public int r = 0;
        public @Nullable Building target;

        @Override
        public void updateTile() {
            if (lastItem == null && items.any()) {
                lastItem = items.first();
            }
            progress += edelta() / speed * 2f;

            if (lastItem != null) {
                // Reset target to null at the beginning of each cycle
                target = null;
                target = getTileTarget(lastItem, lastInput, false);
                if (target != null) {
                    r = relativeToEdge(target.tile);

                    // handle item
                    if (progress >= (1f - 1f / speed)) {
                        getTileTarget(lastItem, lastInput, true);
                        target.handleItem(this, lastItem);
                        items.remove(lastItem, 1);
                        lastItem = null;
                        progress = 0;
                    }
                }
            } else {
                progress = 0;
            }
        }

        @Override
        public void draw() {
            Draw.z(Layer.blockUnder + 0.3f);
            Draw.rect(topRegion, x, y);

            Draw.z(Layer.blockUnder + 0.1f);
            Draw.rect(region, x, y);

            // Draw the current item
            if (current != null) {
                Draw.z(Layer.blockUnder + 0.2f);

                Draw.z(Layer.blockUnder + 0.1f);
                Tmp.v1.set(Geometry.d4x(recDir) * tilesize / 2f, Geometry.d4y(recDir) * tilesize / 2f)
                        .lerp(Geometry.d4x(r) * tilesize / 2f, Geometry.d4y(r) * tilesize / 2f,
                                Mathf.clamp((progress + 1f) / 2f));

                Draw.rect(current.fullIcon, x + Tmp.v1.x, y + Tmp.v1.y, itemSize, itemSize);
            }
        }

        @Override
        public void handleItem(Building source, Item item) {
            recDir = relativeToEdge(source.tile);
            lastInput = source.tile();
            current = item;
            progress = -1f;
            items.add(item, 1);
            noSleep();
        }

        @Override
        public byte version() {
            return 1;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.b(recDir);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            if (revision >= 1) {
                recDir = read.b();
            }
            current = items.first();
        }
    }
}
