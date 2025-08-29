package heavyindustry;

import arc.Core;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import heavyindustry.annotations.ListClasses;
import heavyindustry.annotations.ListPackages;
import heavyindustry.core.HeavyIndustryMod;
import heavyindustry.files.InternalFileTree;
import heavyindustry.graphics.SizedGraphics;
import heavyindustry.input.InputAggregator;
import heavyindustry.util.AccessibleHelper;
import heavyindustry.util.FieldAccessHelper;
import heavyindustry.util.MethodInvokeHelper;
import heavyindustry.util.ReflectImpl;
import heavyindustry.util.aspect.AspectManager;
import heavyindustry.util.handler.ClassHandler;
import heavyindustry.util.handler.ClassHandlerFactory;
import mindustry.content.TechTree.TechNode;
import mindustry.type.Sector;

import static mindustry.Vars.headless;

/**
 * I didn't want my Mod main class to look too messy, so I created this class.
 *
 * @since 1.0.6
 */
public final class HVars {
	/** Commonly used static read-only String. Do not change unless you know what you're doing. */
	public static final String MOD_NAME = "heavy-industry";
	/** The author of this mod. */
	public static final String AUTHOR = "Eipusino";
	/** The GitHub address of this mod. */
	public static final String LINK_GIT_HUB = "https://github.com/Eipusino/HeavyIndustryMod";

	/** Lists all the mod's classes by their canonical names. Generated at compile-time. */
	public static final @ListClasses String[] classes = with();
	/** Lists all the mod's packages by their canonical names. Generated at compile-time. */
	public static final @ListPackages String[] packages = with();

	public static AccessibleHelper accessibleHelper;
	public static ClassHandlerFactory classesFactory;
	public static FieldAccessHelper fieldAccessHelper;
	public static MethodInvokeHelper methodInvokeHelper;

	public static ClassHandler classHandler;
	public static ReflectImpl reflectImpl;

	/**切面管理器实例*/
	public static AspectManager aspects;

	/** jar internal navigation. */
	public static final InternalFileTree internalTree;

	/** Modules present in both servers and clients. */
	public static InputAggregator inputAggregator;

	/** Modules only present in clients, rendering. */
	public static SizedGraphics sizedGraphics;

	public static Texture whiteTexture;
	public static TextureRegion whiteRegion;

	public static final float boardTimeTotal = 60 * 6;

	public static float pressTimer = 30f;
	public static float longPress = 30f;
	public static float iconSize = 40f, buttonSize = 24f, sliderWidth = 140f, fieldWidth = 80f;

	static {
		internalTree = new InternalFileTree(HeavyIndustryMod.class);

		if (!headless) {
			whiteTexture = new Texture(internalTree.child("other/textures/white.png"));
			whiteRegion = new AtlasRegion(new TextureRegion(whiteTexture));
		}

		aspects = AspectManager.getDefault();
	}

	/** Don't let anyone instantiate this class. */
	private HVars() {}

	/** Delta time that is unaffected by time control. */
	public static float graphicsDelta() {
		return Core.graphics.getDeltaTime() * 60f;
	}

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

	static String[] with(String... arg) {
		return arg;
	}
}
