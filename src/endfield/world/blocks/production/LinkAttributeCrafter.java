package endfield.world.blocks.production;

import arc.Core;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Stat;
import org.jetbrains.annotations.Nullable;

//and also the code are duplicated, its silly but for qol mod reason i have to extend AttributeCrafter instead of MultiBlockCrafter
public class LinkAttributeCrafter extends LinkGenericCrafter {
	public Attribute attribute = Attribute.heat;
	public float baseEfficiency = 1f;
	public float boostScale = 1f;
	public float maxBoost = 1f;
	public float minEfficiency = -1f;
	public float displayEfficiencyScale = 1f;
	public boolean displayEfficiency = true;
	public boolean scaleLiquidConsumption = false;

	public LinkAttributeCrafter(String name) {
		super(name);
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);

		if (!displayEfficiency) return;

		drawPlaceText(Core.bundle.format("bar.efficiency", (int) ((baseEfficiency + Math.min(maxBoost, boostScale * sumAttribute(attribute, x, y))) * 100f)), x, y, valid);
	}

	@Override
	public void setBars() {
		super.setBars();

		if (!displayEfficiency) return;

		addBar("efficiency", (LinkAttributeCrafterBuild tile) -> new Bar(
				() -> Core.bundle.format("bar.efficiency", (int) (tile.efficiencyMultiplier() * 100 * displayEfficiencyScale)),
				() -> Pal.lightOrange,
				tile::efficiencyMultiplier
		));
	}

	@Override
	public boolean canPlaceOn(Tile tile, Team team, int rotation) {
		//make sure there's enough efficiency at this location
		return baseEfficiency + linkTiles(tile.x, tile.y, size, rotation).sumf(other -> other.floor().attributes.get(attribute)) >= minEfficiency;
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.add(baseEfficiency <= 0.0001f ? Stat.tiles : Stat.affinities, attribute, floating, boostScale * size * size, !displayEfficiency);
	}

	public float sumAttribute(@Nullable Attribute attr, int x, int y, int rotation) {
		if (attr == null) return 0;
		Tile tile = Vars.world.tile(x, y);
		if (tile == null) return 0;
		return linkTiles(x, y, size, rotation).sumf(other -> !floating && other.floor().isDeep() ? 0 : other.floor().attributes.get(attr));
	}

	public class LinkAttributeCrafterBuild extends LinkGenericCrafterBuild {
		public float attrsum;

		@Override
		public float getProgressIncrease(float base) {
			return super.getProgressIncrease(base) * efficiencyMultiplier();
		}

		public float efficiencyMultiplier() {
			return baseEfficiency + Math.min(maxBoost, boostScale * attrsum) + attribute.env();
		}

		@Override
		public float efficiencyScale() {
			return scaleLiquidConsumption ? efficiencyMultiplier() : super.efficiencyScale();
		}

		@Override
		public void pickedUp() {
			attrsum = 0f;
			warmup = 0f;
		}

		@Override
		public void onProximityUpdate() {
			super.onProximityUpdate();
			attrsum = sumAttribute(attribute, tile.x, tile.y, rotation);
		}
	}
}
