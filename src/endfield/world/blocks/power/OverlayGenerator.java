package endfield.world.blocks.power;

import mindustry.Vars;
import mindustry.game.Team;
import mindustry.world.Tile;
import mindustry.world.blocks.power.ThermalGenerator;
import mindustry.world.meta.Attribute;

/**
 * The environmental values of floor and overlay floor are superimposed.
 *
 * @since 1.0.8
 */
public class OverlayGenerator extends ThermalGenerator {
	public OverlayGenerator(String name) {
		super(name);
	}

	@Override
	public float sumAttribute(Attribute attr, int x, int y) {
		if (attr == null) return 0f;
		Tile tile = Vars.world.tile(x, y);
		if (tile == null) return 0f;
		return tile.getLinkedTilesAs(this, tempTiles).sumf(other -> !floating && other.floor().isDeep() ? 0f : other.floor().attributes.get(attr) + other.overlay().attributes.get(attr));
	}

	@Override
	public boolean canPlaceOn(Tile tile, Team team, int rotation) {
		return tile.getLinkedTilesAs(this, tempTiles).sumf(other -> other.floor().attributes.get(attribute) + other.overlay().attributes.get(attribute)) > minEfficiency;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ThermalGeneratorBuild::new;
	}
}
