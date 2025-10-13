package heavyindustry.mod;

import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.mod.Mods.LoadedMod;

import static heavyindustry.HVars.MOD_NAME;

public final class ModUtils {
	/** If needed, please call {@link #loaded()} for the LoadedMod of this mod. */
	static LoadedMod loaded;

	/** Don't let anyone instantiate this class. */
	private ModUtils() {}

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

	/** @return Check if the mod is enabled based on its name */
	public static boolean isEnabled(String name) {
		LoadedMod mod = Vars.mods.getMod(name);
		return mod != null && mod.isSupported() && mod.enabled();
	}

	/** MindustryX exists in the form of mod, so we can directly search for it in {@code Vars.mods.getMod(name)}. */
	public static boolean isX() {
		return isEnabled("mindustryx");
	}

	public static boolean isArc() {
		return false;
	}
}
