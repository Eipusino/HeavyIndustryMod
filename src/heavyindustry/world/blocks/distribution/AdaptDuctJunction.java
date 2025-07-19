package heavyindustry.world.blocks.distribution;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.TargetPriority;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.content;
import static mindustry.Vars.itemSize;
import static mindustry.Vars.tilesize;

/**
 * Will Anuken actually install it in V8?
 *
 * @author Eipusino
 * @see mindustry.world.blocks.distribution.DuctJunction
 */
public class AdaptDuctJunction extends Block {
	public float speed = 5f;
	public Color transparentColor = new Color(0.4f, 0.4f, 0.4f, 0.1f);
	public TextureRegion bottomRegion;

	public AdaptDuctJunction(String name) {
		super(name);

		group = BlockGroup.transportation;
		update = true;
		solid = false;
		unloadable = false;
		itemCapacity = 1;
		noUpdateDisabled = true;
		underBullets = true;
		priority = TargetPriority.transport;
		envEnabled = Env.space | Env.terrestrial | Env.underwater;
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.add(Stat.itemsMoved, 60f / speed * itemCapacity, StatUnit.itemsSecond);
	}

	@Override
	public void load() {
		super.load();

		bottomRegion = Core.atlas.find(name + "-bottom");
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{bottomRegion, region};
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		Draw.rect(bottomRegion, plan.drawx(), plan.drawy());
		Draw.rect(region, plan.drawx(), plan.drawy());
	}

	@Override
	public boolean outputsItems() {
		return true;
	}

	public class LogisticsDuctJunctionBuild extends Building {
		public Item[] current = new Item[2];
		public float[] progress = new float[2];
		public int[] from = new int[2];

		@Override
		public void draw() {
			Draw.z(Layer.blockUnder);
			Draw.rect(bottomRegion, x, y);

			Draw.z(Layer.blockUnder + 0.1f);
			for (int i = 0; i < 2; i++) {
				if (current[i] == null) continue;

				Tmp.v1.trns(from[i] * 90f, tilesize / 2f * Mathf.lerp(-1, 1, Mathf.clamp((progress[i] + 1f) / 2f)));
				Draw.rect(current[i].fullIcon, x + Tmp.v1.x, y + Tmp.v1.y, itemSize, itemSize);
			}

			Draw.z(Layer.blockUnder + 0.2f);
			Draw.color(transparentColor);
			Draw.rect(bottomRegion, x, y);
			Draw.color();
			Draw.rect(region, x, y);
		}

		@Override
		public void payloadDraw() {
			Draw.rect(fullIcon, x, y);
		}

		@Override
		public void updateTile() {
			for (int i = 0; i < 2; i++) {
				progress[i] += edelta() / speed * 2f;
				Building next = nearby(from[i]);

				if (current[i] != null && next != null) {
					if (progress[i] >= (1f - 1f / speed) && moveItem(next, current[i])) {
						current[i] = null;
						progress[i] %= (1f - 1f / speed);
					}
				} else {
					progress[i] = 0;
				}
			}
		}

		public boolean moveItem(Building other, Item item) {
			if (other != null && other.team == team && other.acceptItem(this, item)) {
				other.handleItem(this, item);
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void handleItem(Building source, Item item) {
			int relative = source.relativeTo(tile);
			if (current[relative % 2] != null) return;

			current[relative % 2] = item;
			progress[relative % 2] = -1;
			from[relative % 2] = relative;
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			int relative = source.relativeTo(tile);
			return current[relative % 2] == null;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			for (int i = 0; i < 2; i++) {
				write.i(current[i] == null ? -1 : current[i].id);
				write.i(from[i]);
			}
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			for (int i = 0; i < 2; i++) {
				current[i] = content.item(read.i());
				from[i] = read.i();
			}
		}
	}
}
