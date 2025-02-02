package heavyindustry;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.util.*;
import heavyindustry.core.*;
import heavyindustry.files.*;
import heavyindustry.input.*;
import mindustry.content.*;
import mindustry.type.*;

/**
 * I didn't want my Mod main class to look too messy, so I created this class.
 *
 * @since 1.0.6
 */
public final class HIVars {
	/** Commonly used static read-only String. do not change unless you know what you're doing. */
	public static final String modName = "heavy-industry";
	/** jar internal navigation. */
	public static final InternalFileTree internalTree = new InternalFileTree(HeavyIndustryMod.class);
	/** Modules present in both servers and clients. */
	public static InputAggregator inputAggregator;

	public static float pressTimer = 30f;
	public static float longPress = 30f;
	public static float iconSize = 40f, buttonSize = 24f, sliderWidth = 140f, fieldWidth = 80f;

	/** Blank single pixel texture, commonly used as a placeholder and to prevent null pointer anomalies in Kotlin. */
	public static final TextureRegion whiteRegion = new AtlasRegion(new TextureRegion(new Texture(internalTree.child("sprites/white.png"))));

	/** Don't let anyone instantiate this class. */
	private HIVars() {}

	/** Omitting longer mod names is generally used to load mod sprites. */
	public static String name(String add) {
		return modName + "-" + add;
	}

	/** Delta time that is unaffected by time control. */
	public static float graphicsDelta() {
		return Core.graphics.getDeltaTime() * 60f;
	}

	public static void resetSaves(Planet planet) {
		planet.sectors.each(sector -> {
			if (sector.hasSave()) {
				sector.save.delete();
				sector.save = null;
			}
		});
	}

	public static void resetTree(TechTree.TechNode root) {
		root.reset();
		root.content.clearUnlock();
		root.children.each(HIVars::resetTree);
	}
}
