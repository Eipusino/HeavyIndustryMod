package heavyindustry.mod;

import arc.files.Fi;
import arc.util.ArcRuntimeException;
import arc.util.serialization.Jval;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.mod.Mods.LoadedMod;

import static heavyindustry.HVars.internalTree;
import static heavyindustry.HVars.MOD_NAME;

public final class HMods {
	static final String[] metaFiles = {"mod.json", "mod.hjson", "plugin.json", "plugin.hjson"};

	/** If needed, please call {@link #loaded()} for the LoadedMod of this mod. */
	static LoadedMod loaded;

	/** The meta of this mod. */
	static Jval modMeta;

	static {
		modMeta = getMeta(internalTree.root);
	}

	/** Don't let anyone instantiate this class. */
	private HMods() {}

	public static Jval getMeta() {
		return modMeta;
	}

	/**
	 * @param file the compressed file path of mod
	 * @return path to mod meta
	 * @throws NullPointerException If file is null
	 * @throws ArcRuntimeException  If the path file is not available
	 */
	public static Jval getMeta(Fi file, Jval def) {
		Fi metaFile = null;
		for (String name : metaFiles) {
			if ((metaFile = file.child(name)).exists()) {
				break;
			}
		}

		if (!metaFile.exists()) {
			return def;
		}

		return Jval.read(metaFile.reader());
	}

	public static Jval getMeta(Fi file) {
		return getMeta(file, null);
	}

	public static boolean isHeavyIndustry(Content content) {
		return content != null && isHeavyIndustry(content.minfo.mod);
	}

	public static boolean isHeavyIndustry(LoadedMod mod) {
		return mod != null && mod == loaded();
	}

	/** Safely obtain the {@code LoadedMod} for this mod. */
	public static LoadedMod loaded() {
		if (loaded == null) loaded = Vars.mods.getMod(MOD_NAME);
		return loaded;
	}

	public static boolean isEnabled(String name) {
		LoadedMod mod = Vars.mods.getMod(name);
		return mod != null && mod.isSupported() && mod.enabled();
	}
}
