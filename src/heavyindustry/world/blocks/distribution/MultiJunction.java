package heavyindustry.world.blocks.distribution;

import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.BufferItem;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.type.Item;
import mindustry.world.DirectionalItemBuffer;
import mindustry.world.blocks.liquid.LiquidJunction;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.content;

/** junction which allow both liquid and item to go through. */
public class MultiJunction extends LiquidJunction {
	/** frames taken to go through this junction. */
	public float speed = 26;
	public int capacity = 6;

	public MultiJunction(String name) {
		super(name);
		placeableLiquid = true;
		update = true;
		solid = false;
		underBullets = true;
		group = BlockGroup.transportation;
		unloadable = false;
		floating = true;
		noUpdateDisabled = true;
	}

	@Override
	public boolean outputsItems() {
		return true;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = MultiJunctionBuild::new;
	}

	public class MultiJunctionBuild extends LiquidJunctionBuild {
		public DirectionalItemBuffer buffer = new DirectionalItemBuffer(capacity);

		@Override
		public int acceptStack(Item item, int amount, Teamc source) {
			return 0;
		}

		@Override
		public void updateTile() {
			super.updateTile();

			for (int i = 0; i < 4; i++) {
				if (buffer.indexes[i] > 0) {
					if (buffer.indexes[i] > capacity) buffer.indexes[i] = capacity;
					long buf = buffer.buffers[i][0];
					float time = BufferItem.time(buf);

					if (Time.time >= time + speed / timeScale || Time.time < time) {
						Item item = content.item(BufferItem.item(buf));
						Building dest = nearby(i);

						//skip blocks that don't want the item, keep waiting until they do
						if (item == null || dest == null || !dest.acceptItem(this, item) || dest.team != team) {
							continue;
						}

						dest.handleItem(this, item);
						System.arraycopy(buffer.buffers[i], 1, buffer.buffers[i], 0, buffer.indexes[i] - 1);
						buffer.indexes[i]--;
					}
				}
			}
		}

		@Override
		public void handleItem(Building source, Item item) {
			int relative = source.relativeTo(tile);
			buffer.accept(relative, item);
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			int relative = source.relativeTo(tile);

			if (relative == -1 || !buffer.accepts(relative)) return false;
			Building to = nearby(relative);
			return to != null && to.team == team;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			buffer.write(write);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			buffer.read(read);
		}
	}
}
