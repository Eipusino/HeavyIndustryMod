package heavyindustry.world.blocks.sandbox;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.graphics.HPal;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

public class EverythingLiquidSource extends Block {
	public float ticksPerItemColor = 90f;

	public TextureRegion centerRegion;

	public EverythingLiquidSource(String name) {
		super(name);

		update = true;
		solid = true;
		hasLiquids = true;
		liquidCapacity = 100f;
		outputsLiquid = true;
		noUpdateDisabled = true;
		displayFlow = false;
		group = BlockGroup.liquids;
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

		removeBar("liquid");
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = EverythingLiquidSourceBuild::new;
	}

	public class EverythingLiquidSourceBuild extends Building {
		@Override
		public void draw() {
			super.draw();

			Draw.color(Tmp.c1.lerp(HPal.liquidColors, Time.time / (ticksPerItemColor * HPal.liquidColors.length) % 1f));
			Draw.rect(centerRegion, x, y);
			Draw.color();
		}

		@Override
		public void updateTile() {
			Vars.content.liquids().each(l -> {
				liquids.add(l, liquidCapacity);
				dumpLiquid(l);
				liquids.clear();
			});
		}
	}
}
