package heavyindustry.world.blocks.distribution;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Time;
import heavyindustry.util.Utils;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.world.blocks.distribution.DirectionalUnloader;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.content;

public class RailDirectionalUnloader extends DirectionalUnloader {
	public TextureRegion[] regions;

	public RailDirectionalUnloader(String name) {
		super(name);
	}

	@Override
	public void load() {
		super.load();

		regions = Utils.split(name + "-base", 32, 0);
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		Draw.rect(regions[4], plan.drawx(), plan.drawy());
		Draw.rect(regions[plan.rotation], plan.drawx(), plan.drawy());
		drawPlanConfig(plan, list);
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region};
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.remove(Stat.speed);
		stats.add(Stat.speed, 15, StatUnit.itemsSecond);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = RailDirectionalUnloaderBuild::new;
	}

	public class RailDirectionalUnloaderBuild extends DirectionalUnloaderBuild {
		@Override
		public void updateTile() {
			float inc = unloadItem == null ? edelta() : (edelta() / 16.5f * 30f);
			if ((unloadTimer += inc) >= speed) {
				Building front = front(), back = back();

				if (front != null && back != null && back.items != null && front.team == team && back.team == team && back.canUnload()) {
					if (unloadItem == null) {
						Seq<Item> itemseq = content.items();
						int itemc = itemseq.size;
						for (int i = 0; i < itemc; i++) {
							Item item = itemseq.get((i + offset) % itemc);
							if (back.items.has(item) && front.acceptItem(this, item)) {
								front.handleItem(this, item);
								back.items.remove(item, 1);
								back.itemTaken(item);
								offset++;
								offset %= itemc;
								break;
							}
						}
					} else if (back.items.has(unloadItem) && front.acceptItem(this, unloadItem)) {
						front.handleItem(this, unloadItem);
						back.items.remove(unloadItem, 1);
						back.itemTaken(unloadItem);
					}
				}

				unloadTimer %= speed;
			}
		}

		@Override
		public void draw() {
			Draw.rect(regions[4], x, y);

			if (unloadItem != null) {
				Draw.color(unloadItem.color);
				Draw.rect(regions[5], x, y);
				Draw.color();
			}

			Draw.rect(regions[rotation], x, y);
		}

		@Override
		public void drawSelect() {
			drawIO();

			Draw.reset();
		}

		private void drawIO() {
			Building front = front(), back = back();

			if (unloadItem != null && front != null && front.items != null && back != null && back.items != null && back.items.has(unloadItem.id)) {
				float alpha = Math.abs(100f - (Time.time * 2f) % 100f) / 100f;

				float ix = front.x;
				float iy = front.y;
				float ox = back.x;
				float oy = back.y;
				float px = Mathf.lerp(ix, ox, alpha);
				float py = Mathf.lerp(iy, oy, alpha);

				//background
				Draw.z(Layer.blockOver);
				Draw.color(Pal.gray);
				Lines.stroke(2.5f);
				Fill.square(ix, iy, 2.5f, 45);
				Fill.square(ox, oy, 2.5f, 45);
				Lines.stroke(4f);

				Lines.line(ix, iy, ox, oy);
				//Colored
				Draw.z(Layer.blockOver + 0.0001f);
				Draw.color(unloadItem == null ? Pal.gray : unloadItem.color);
				Fill.square(ix, iy, 1f, 45);
				Fill.square(ox, oy, 1f, 45);
				Lines.stroke(1f);

				Lines.line(ix, iy, ox, oy);

				//Point
				Draw.z(Layer.blockOver + 0.0002f);
				Draw.mixcol(Draw.getColor(), 1f);
				Draw.color();
				Fill.square(px, py, 1f, 45);
				Draw.mixcol();
			}
		}
	}
}
