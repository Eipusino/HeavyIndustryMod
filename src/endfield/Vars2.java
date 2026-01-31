package endfield;

import arc.struct.Seq;
import arc.util.Log;
import endfield.annotations.Annotations.ListPackages;
import endfield.core.EndFieldListener;
import endfield.core.EndFieldMod;
import endfield.files.InternalFileTree;
import endfield.graphics.SizedGraphics;
import endfield.graphics.g2d.CutBatch;
import endfield.graphics.g2d.DevastationBatch;
import endfield.graphics.g2d.FragmentationBatch;
import endfield.graphics.g2d.VaporizeBatch;
import endfield.mod.ModInfo;
import endfield.util.AccessibleHelper;
import endfield.util.ClassHelper;
import endfield.util.FieldAccessHelper;
import endfield.util.MethodInvokeHelper;
import endfield.util.PlatformImpl;
import mindustry.content.TechTree.TechNode;
import mindustry.type.Sector;

/**
 * I didn't want my Mod main class to look too messy, so I created this class.
 *
 * @since 1.0.6
 */
public final class Vars2 {
	/** Commonly used static read-only String. Do not change unless you know what you're doing. */
	public static final String MOD_NAME = "endfield";
	public static final String MOD_PREFIX = MOD_NAME + '-';
	/** The author of this mod. */
	public static final String AUTHOR = "Eipusino";
	/** The GitHub address of this mod. */
	public static final String LINK_GIT_HUB = "https://github.com/Eipusino/HeavyIndustryMod";

	/** Lists all the mod's packages by their canonical names. Generated at compile-time. */
	@ListPackages
	public static final String[] packages = {};

	public static PlatformImpl platformImpl;

	public static ModInfo modInfo;

	/** Whether the mod is running in hidden mode. */
	public static boolean isPlugin;

	/** jar internal navigation. */
	public static final InternalFileTree internalTree;

	public static AccessibleHelper accessibleHelper;
	public static ClassHelper classHelper;
	public static FieldAccessHelper fieldAccessHelper;
	public static MethodInvokeHelper methodInvokeHelper;

	public static SizedGraphics sizedGraphics;

	public static FragmentationBatch fragBatch;
	public static CutBatch cutBatch;
	public static VaporizeBatch vaporBatch;
	public static DevastationBatch devasBatch;

	public static EndFieldListener listener;

	public static final float boardTimeTotal = 60 * 6;

	public static float pressTimer = 30f;
	public static float longPress = 30f;
	public static float iconSize = 40f, buttonSize = 24f, sliderWidth = 140f, fieldWidth = 80f;

	static {
		internalTree = new InternalFileTree(EndFieldMod.class);

		try {
			modInfo = new ModInfo(internalTree.file);
			isPlugin = modInfo.info.getBool("hidden", false);
		} catch (Exception e) {
			Log.err(e);
		}
	}

	/** Don't let anyone instantiate this class. */
	private Vars2() {}

	/**
	 * Clear all occupied sectors of the specified Planet. Use with caution, as this will completely disrupt the
	 * player's game progress.
	 */
	public static void resetSaves(Seq<Sector> sectors) {
		for (Sector sector : sectors) {
			if (sector.hasSave()) {
				sector.save.delete();
				sector.save = null;
			}
		}
	}

	/**
	 * Clear all tech nodes under the specified root node. Use with caution, as this will completely disrupt
	 * the player's game progress.
	 */
	public static void resetTree(TechNode root) {
		root.reset();
		root.content.clearUnlock();
		for (TechNode node : root.children) {
			resetTree(node);
		}
	}
}
