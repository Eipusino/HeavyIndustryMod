package heavyindustry.type;

import arc.Core;
import mindustry.graphics.g3d.PlanetGrid.Ptile;
import mindustry.type.Planet;
import mindustry.type.Sector;

// it is not threatening . threat level None .
public class NonThreateningSector extends Sector {
	public NonThreateningSector(Planet planet, Ptile tile) {
		super(planet, tile);
	}

	@Override
	public String displayThreat() {
		return "[white]" + Core.bundle.get("threat.none");
	}
}
