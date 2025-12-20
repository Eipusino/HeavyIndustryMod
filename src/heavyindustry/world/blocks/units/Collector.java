package heavyindustry.world.blocks.units;

import arc.Events;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.io.Reads;
import heavyindustry.math.Mathm;
import heavyindustry.util.CollectionList;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.game.EventType.ResetEvent;
import mindustry.game.EventType.UnitDestroyEvent;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.world.Block;

import java.util.List;

import static mindustry.Vars.tilesize;

public class Collector extends Block {
	public List<Building> existing = new CollectionList<>(Building.class);
	public float range = 120f;

	public Collector(String name) {
		super(name);

		solid = true;
		update = true;
		destructible = true;

		Events.on(UnitDestroyEvent.class, event -> {
			Unit unit = event.unit;
			Building build = Geometry.findClosest(unit.x, unit.y, existing);

			if (build != null) {
				if (unit.within(build, range)) {
					float amount = Mathm.clamp(unit.hitSize, 0f, build.getMaximumAccepted(Items.scrap));
					Fx.itemTransfer.at(unit.x, unit.y, amount, Pal.scrapAmmoBack, build);
					build.items.add(Items.scrap, Mathf.ceil(amount));
				}
			}
		});

		Events.on(ResetEvent.class, event -> {
			existing.clear();
		});
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.placing);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = CollectorBuild::new;
	}

	public class CollectorBuild extends Building {
		@Override
		public void updateTile() {
			dump(Items.scrap);
		}

		@Override
		public void created() {
			super.created();

			existing.add(this);
		}

		@Override
		public void onRemoved() {
			existing.remove(this);

			super.onRemoved();
		}

		@Override
		public void drawSelect() {
			Drawf.dashCircle(x, y, range, team.color);
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			return false;
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			existing.add(this);
		}
	}
}
