package heavyindustry.world.blocks.storage;

import arc.math.geom.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.storage.CoreBlock.*;
import mindustry.world.meta.*;
import mindustry.world.modules.*;

import static arc.Core.*;
import static mindustry.Vars.*;

/**
 * Connect the core warehouse.
 *
 * @author Eipusino
 */
public class CoreStorageBlock extends StorageBlock {
    public int range = 15;

    protected CoreBuild tmpCoreBuild;

    public CoreStorageBlock(String name) {
        super(name);
        update = true;
        hasItems = true;
        itemCapacity = 0;
        configurable = true;
        replaceable = false;
    }

    @Override
    public boolean isAccessible() {
        return true;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.itemCapacity);
    }

    @Override
    public void setBars() {
        super.setBars();
        removeBar("items");
        addBar("items", (CoreStorageBuild tile) -> new Bar(
                () -> bundle.format("bar.items", tile.items.total()),
                () -> Pal.items,
                () -> (float) (tile.items.total() / ((tmpCoreBuild = tile.core()) == null ? Integer.MAX_VALUE : tmpCoreBuild.storageCapacity))));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        if (state.rules.infiniteResources) return;

        if (world.tile(x, y) != null && !canPlaceOn(world.tile(x, y), player.team(), rotation)) {
            drawPlaceText(bundle.get((player.team().core() != null && player.team().core().items.has(requirements, state.rules.buildCostMultiplier)) || state.rules.infiniteResources ? "bar.hi-close" : "bar.noresources"), x, y, valid);
        }
        x *= tilesize;
        y *= tilesize;

        Drawf.square(x, y, range * tilesize * 1.414f, 90, player.team().color);
    }

    public Rect getRect(Rect rect, float x, float y, float range) {
        rect.setCentered(x, y, range * 2 * tilesize);

        return rect;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if (state.rules.infiniteResources) return true;

        CoreBlock.CoreBuild core = team.core();
        if (core == null || (!state.rules.infiniteResources && !core.items.has(requirements, state.rules.buildCostMultiplier)))
            return false;

        Rect rect = getRect(Tmp.r1, tile.worldx() + offset, tile.worldy() + offset, range).grow(0.1f);
        return !indexer.getFlagged(team, BlockFlag.storage).contains(b -> {
            if (b instanceof CoreStorageBuild build && b.block instanceof CoreStorageBlock block) {
                return getRect(Tmp.r2, build.x, build.y, block.range).overlaps(rect);
            }
            return false;
        });
    }

    public class CoreStorageBuild extends StorageBuild {
        @Override
        public void updateTile() {
            if (core() != null) {
                if (linkedCore == null || !linkedCore.isValid()) {
                    linkedCore = core();
                    items = linkedCore.items;
                }
            } else {
                linkedCore = null;
                items = new ItemModule();
            }
        }

        @Override
        public boolean canPickup() {
            return false;
        }

        @Override
        public void drawSelect() {
        }
    }
}
