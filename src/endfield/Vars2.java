package endfield;

import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.serialization.Jval;
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
import endfield.util.CollectionObjectSet;
import endfield.util.FieldAccessHelper;
import endfield.util.MethodInvokeHelper;
import endfield.util.PlatformImpl;
import endfield.util.Reflects;
import mindustry.content.TechTree.TechNode;
import mindustry.type.Sector;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;

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

	public static final CollectionObjectSet<String> packages = new CollectionObjectSet<>(String.class);
	public static final CollectionObjectSet<Class<?>> classes = new CollectionObjectSet<>(Class.class);

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

	public static void init() {
		Fi other = internalTree.child("other");

		try (Reader reader = other.child("classes-core.json").reader()/*;
		     Reader platform = other.child(OS.isAndroid ? "classes-android.json" : "classes-desktop.json").reader()*/) {
			Jval meta = Jval.read(reader)/*, meta2 = Jval.read(platform)*/;

			getPackages(meta, packages);
			//getPackages(meta2, packages);

			getClasses(meta, classes);
			//getClasses(meta2, classes);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void getPackages(Jval meta, Collection<String> packages) {
		for (Jval value : meta.get("packages").asArray()) {
			packages.add(value.asString());
		}
	}

	public static void getClasses(Jval meta, Collection<Class<?>> classes) {
		for (Jval value : meta.get("classes").asArray()) {
			String name = value.asString();
			Class<?> type = Reflects.findClass(name);
			if (type == null) Log.warn("Class '@' not found.", name);
			else classes.add(type);
		}
	}
}
