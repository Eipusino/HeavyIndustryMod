package heavyindustry.content;

import arc.graphics.Texture;
import heavyindustry.HVars;

/// Defines the {@linkplain mindustry.type.Planet planets} and other celestial objects this mod offers.
public final class HPlanets {
	/// Don't let anyone instantiate this class.
	private HPlanets() {}

	/// Instantiates all contents. Called in the main thread in {@code HeavyIndustryMod.loadContent()}.
	public static void load() {
		// not
	}

	public static Texture rings(String name) {
		return new Texture(HVars.internalTree.resolves("sprites", "planets", "rings", name + ".png"));
	}
}
