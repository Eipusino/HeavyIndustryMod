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
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

public class EverythingSourceVoid extends Block {
	public float ticksPerItemColor = 90f;

	public TextureRegion strobeRegion;
	public TextureRegion[] center = new TextureRegion[2];

	public EverythingSourceVoid(String name) {
		super(name);

		update = solid = true;
		hasItems = hasLiquids = acceptsItems = true;
		displayFlow = false;
		liquidCapacity = 10000f;
		group = BlockGroup.transportation;
		noUpdateDisabled = true;
		envEnabled = Env.any;
	}

	@Override
	public void load() {
		super.load();
		strobeRegion = Core.atlas.find(name + "-strobe", "extra-sand-redux-source-strobe");
		center[0] = Core.atlas.find(name + "-center-0");
		center[1] = Core.atlas.find(name + "-center-1");
	}

	@Override
	public void setBars() {
		super.setBars();

		removeBar("items");
		removeBar("liquid");
	}

	public class MaterialSourceVoidBuild extends Building {
		@Override
		public void draw() {
			super.draw();

			Drawn.setStrobeColor();
			Draw.rect(strobeRegion, x, y);

			Draw.color(Tmp.c1.lerp(HPal.itemColors, Time.time / (ticksPerItemColor * HPal.itemColors.length) % 1f));
			Draw.rect(center[0], x, y);
			Draw.color(Tmp.c1.lerp(HPal.liquidColors, Time.time / (ticksPerItemColor * HPal.liquidColors.length) % 1f));
			Draw.rect(center[1], x, y);
			Draw.color();
		}

		@Override
		public void updateTile() {
			Vars.content.items().each(i -> {
				items.set(i, 1);
				dump(i);
				items.set(i, 0);
			});

			Vars.content.liquids().each(l -> {
				liquids.add(l, liquidCapacity);
				dumpLiquid(l);
				liquids.clear();
			});
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			return enabled;
		}

		@Override
		public void handleItem(Building source, Item item) {
		}

		@Override
		public boolean acceptLiquid(Building source, Liquid liquid) {
			return enabled;
		}

		@Override
		public void handleLiquid(Building source, Liquid liquid, float amount) {
		}
	}
}
