package heavyindustry;

import arc.graphics.Texture;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import heavyindustry.annotations.ListClasses;
import heavyindustry.annotations.ListPackages;
import heavyindustry.core.HeavyIndustryListener;
import heavyindustry.core.HeavyIndustryMod;
import heavyindustry.files.InternalFileTree;
import heavyindustry.graphics.g2d.CutBatch;
import heavyindustry.graphics.g2d.DevastationBatch;
import heavyindustry.graphics.g2d.FragmentationBatch;
import heavyindustry.graphics.g2d.VaporizeBatch;
import heavyindustry.input.InputAggregator;
import heavyindustry.util.PlatformImpl;
import mindustry.Vars;
import mindustry.content.TechTree.TechNode;
import mindustry.type.Sector;

/**
 * I didn't want my Mod main class to look too messy, so I created this class.
 *
 * @since 1.0.6
 */
public final class HVars {
	/** Commonly used static read-only String. Do not change unless you know what you're doing. */
	public static final String MOD_NAME = "heavy-industry";
	public static final String MOD_PREFIX = "heavy-industry-";
	/** The author of this mod. */
	public static final String AUTHOR = "Eipusino";
	/** The GitHub address of this mod. */
	public static final String LINK_GIT_HUB = "https://github.com/Eipusino/HeavyIndustryMod";

	/** Lists all the mod's classes by their canonical names. Generated at compile-time. */
	public static final @ListClasses String[] classes = {};
	/** Lists all the mod's packages by their canonical names. Generated at compile-time. */
	public static final @ListPackages String[] packages = {};

	public static PlatformImpl platformImpl;

	/** Is the Unsafe class available. */
	public static boolean hasUnsafe = false;

	/** jar internal navigation. */
	public static final InternalFileTree internalTree;

	public static InputAggregator inputAggregator;

	public static Texture white;
	public static TextureRegion whiteRegion;
	public static AtlasRegion whiteAtlas;

	public static FragmentationBatch fragBatch;
	public static CutBatch cutBatch;
	public static VaporizeBatch vaporBatch;
	public static DevastationBatch devasBatch;

	public static HeavyIndustryListener listener;

	public static final float boardTimeTotal = 60 * 6;

	public static float pressTimer = 30f;
	public static float longPress = 30f;
	public static float iconSize = 40f, buttonSize = 24f, sliderWidth = 140f, fieldWidth = 80f;

	static {
		internalTree = new InternalFileTree(HeavyIndustryMod.class);

		if (!Vars.headless) {
			whiteAtlas = new AtlasRegion(whiteRegion = new TextureRegion(white = new Texture(internalTree.child("other/textures/white.png"))));
			whiteAtlas.name = "white";
		}
	}

	/** Don't let anyone instantiate this class. */
	private HVars() {}

	/** Clear all occupied sectors of the specified Planet. Use with caution, as this will completely disrupt the player's game progress. */
	public static void resetSaves(Seq<Sector> sectors) {
		sectors.each(sector -> {
			if (sector.hasSave()) {
				sector.save.delete();
				sector.save = null;
			}
		});
	}

	/** Clear all tech nodes under the specified root node. Use with caution, as this will completely disrupt the player's game progress. */
	public static void resetTree(TechNode root) {
		root.reset();
		root.content.clearUnlock();
		root.children.each(HVars::resetTree);
	}
}
