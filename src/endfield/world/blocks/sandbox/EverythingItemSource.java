package endfield.world.blocks.sandbox;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Time;
import arc.util.Tmp;
import endfield.graphics.Pal2;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

public class EverythingItemSource extends Block {
	public float ticksPerItemColor = 90f;

	public TextureRegion centerRegion;

	public EverythingItemSource(String name) {
		super(name);

		hasItems = true;
		update = true;
		solid = true;
		group = BlockGroup.transportation;
		noUpdateDisabled = true;
		envEnabled = Env.any;
	}

	@Override
	public void load() {
		super.load();

		centerRegion = Core.atlas.find(name + "-center", "center");
	}

	@Override
	public void setBars() {
		super.setBars();

		removeBar("items");
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = EverythingItemSourceBuild::new;
	}

	public class EverythingItemSourceBuild extends Building {
		@Override
		public void draw() {
			super.draw();

			Draw.color(Tmp.c1.lerp(Pal2.itemColors, Time.time / (ticksPerItemColor * Pal2.itemColors.length) % 1f));
			Draw.rect(centerRegion, x, y);
			Draw.color();
		}

		@Override
		public void updateTile() {
			for (Item i : Vars.content.items()) {
				items.set(i, 1);
				dump(i);
				items.set(i, 0);
			}
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			return false;
		}
	}
}
