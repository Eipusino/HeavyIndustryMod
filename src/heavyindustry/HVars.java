package heavyindustry;

import arc.Core;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;
import arc.graphics.g2d.TextureRegion;
import heavyindustry.core.HeavyIndustryMod;
import heavyindustry.files.InternalFileTree;
import heavyindustry.input.InputAggregator;
import mindustry.content.TechTree;
import mindustry.type.Planet;
import mindustry.type.Sector;

/**
 * I didn't want my Mod main class to look too messy, so I created this class.
 *
 * @since 1.0.6
 */
public final class HVars {
	/** Commonly used static read-only String. Do not change unless you know what you're doing. */
	public static final String modName = "heavy-industry";
	/** jar internal navigation. */
	public static final InternalFileTree internalTree;

	/** Modules present in both servers and clients. */
	public static InputAggregator inputAggregator;

	/** Blank Texture Region, used for Kotlin code, can avoid {@code lateinit var}. */
	public static final Texture whiteTexture;
	public static final TextureRegion whiteRegion;

	public static float pressTimer = 30f;
	public static float longPress = 30f;
	public static float iconSize = 40f, buttonSize = 24f, sliderWidth = 140f, fieldWidth = 80f;

	static {
		internalTree = new InternalFileTree(HeavyIndustryMod.class);

		whiteTexture = new Texture(internalTree.child("sprites/white.png"));
		whiteRegion = new AtlasRegion(new TextureRegion(whiteTexture));
	}

	/** Don't let anyone instantiate this class. */
	private HVars() {}

	/** Omitting longer mod names is generally used to load mod sprites. */
	public static String name(String add) {
		return modName + "-" + add;
	}

	/** Delta time that is unaffected by time control. */
	public static float graphicsDelta() {
		return Core.graphics.getDeltaTime() * 60f;
	}

	/** Clear all occupied sectors of the specified Planet. Use with caution, as this will completely disrupt the player's game progress. */
	public static void resetSaves(Planet planet) {
		for (Sector sector : planet.sectors) {
			if (sector.hasSave()) {
				sector.save.delete();
				sector.save = null;
			}
		}
	}

	/** Clear all tech nodes under the specified root node. Use with caution, as this will completely disrupt the player's game progress. */
	public static void resetTree(TechTree.TechNode root) {
		root.reset();
		root.content.clearUnlock();
		for (TechTree.TechNode node : root.children) {
			resetTree(node);
		}
	}
}
