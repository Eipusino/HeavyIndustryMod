package heavyindustry.world.blocks.storage;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Rect;
import arc.util.Tmp;
import mindustry.game.Team;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Stat;
import mindustry.world.modules.ItemModule;

import static mindustry.Vars.indexer;
import static mindustry.Vars.player;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

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
	protected TextureRegion[] icons() {
		return teamRegion.found() ? new TextureRegion[]{region, teamRegions[Team.sharded.id]} : new TextureRegion[]{region};
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
				() -> Core.bundle.format("bar.items", tile.items.total()),
				() -> Pal.items,
				() -> (float) (tile.items.total() / ((tmpCoreBuild = tile.core()) == null ? Integer.MAX_VALUE : tmpCoreBuild.storageCapacity))));
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);

		if (state.rules.infiniteResources) return;

		if (world.tile(x, y) != null && !canPlaceOn(world.tile(x, y), player.team(), rotation)) {
			drawPlaceText(Core.bundle.get((player.team().core() != null && player.team().core().items.has(requirements, state.rules.buildCostMultiplier)) || state.rules.infiniteResources ? "bar.hi-close" : "bar.noresources"), x, y, valid);
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

		CoreBuild core = team.core();
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
				items = ItemModule.empty;
			}
		}

		@Override
		public boolean canPickup() {
			return false;
		}

		@Override
		public void drawSelect() {}
	}
}
