package heavyindustry.world.blocks.sandbox;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.graphics.Drawn;
import heavyindustry.graphics.HPal;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import static heavyindustry.HVars.MOD_NAME;

public class EverythingItemSource extends Block {
	public float ticksPerItemColor = 90f;

	public TextureRegion strobeRegion, centerRegion;

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
		strobeRegion = Core.atlas.find(name + "-strobe", MOD_NAME + "-source-strobe");
		centerRegion = Core.atlas.find(name + "-center", "center");
	}

	@Override
	public void setBars() {
		super.setBars();

		removeBar("items");
	}

	public class EverythingItemSourceBuild extends Building {
		@Override
		public void draw() {
			super.draw();

			Drawn.setStrobeColor();
			Draw.rect(strobeRegion, x, y);

			Draw.color(Tmp.c1.lerp(HPal.itemColors, Time.time / (ticksPerItemColor * HPal.itemColors.length) % 1f));
			Draw.rect(centerRegion, x, y);
			Draw.color();
		}

		@Override
		public void updateTile() {
			Vars.content.items().each(i -> {
				items.set(i, 1);
				dump(i);
				items.set(i, 0);
			});
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			return false;
		}
	}
}
