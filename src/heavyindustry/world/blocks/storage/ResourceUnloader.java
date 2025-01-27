package heavyindustry.world.blocks.storage;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;

public class ResourceUnloader extends Unloader {
	public float range = 160;

	public ResourceUnloader(String name) {
		super(name);
		acceptsItems = true;
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);
		Drawf.dashCircle(x * Vars.tilesize + offset, y * Vars.tilesize + offset, range, Pal.accent);
	}

	@Override
	public boolean outputsItems() {
		return false;
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stat.range, range / 8, StatUnit.blocks);
	}

	public class ResourceUnloaderBuild extends UnloaderBuild {
		@Override
		public void updateTile() {
			if (sortItem != null && items.get(sortItem) < getMaximumAccepted(sortItem) && team.core().items.get(sortItem) > 0) {
				team.core().items.remove(sortItem, 1);
				items.add(sortItem, 1);
			}
			Vars.indexer.eachBlock(this, range, other -> other.block.hasItems, other -> {
				if (sortItem != null && items.get(sortItem) > 0 && other.acceptItem(this, sortItem)) {
					other.handleItem(this, sortItem);
					Fx.itemTransfer.at(x, y, 0, sortItem.color, other);
					items.remove(sortItem, 1);
				}
			});
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			return item == sortItem && items.get(item) < getMaximumAccepted(item);
		}

		@Override
		public void drawConfigure() {
			super.drawConfigure();
			Vars.indexer.eachBlock(this, range, other -> other.block.hasItems, other -> {
				if (sortItem != null && other.acceptItem(this, sortItem)) {
					Draw.color(sortItem.color);
					Lines.square(other.x, other.y, other.block.size * Vars.tilesize / 2f + 1, 45);
				}
			});
			Drawf.dashCircle(x, y, range, Pal.accent);
		}

		@Override
		public void draw() {
			super.draw();
			Draw.z(Layer.effect);

			if (sortItem == null) Draw.color(Color.white);
			else Draw.color(sortItem.color);

			Draw.alpha(Mathf.sin(0.05f * Time.time));
			Lines.square(x, y, 8 * Mathf.sin(0.025f * Time.time), 45);
		}
	}
}
