package heavyindustry.mod;

import arc.files.Fi;
import arc.struct.ObjectSet;
import arc.util.ArcRuntimeException;
import arc.util.Log;
import arc.util.Reflect;
import arc.util.serialization.Jval;
import mindustry.Vars;
import mindustry.mod.Mods;

public final class LoadMod {
	public static final String[] metaFiles = {"mod.json", "mod.hjson", "plugin.json", "plugin.hjson"};
	/**
	 * The following mods have stolen other mod textures or engaged in even worse behavior. If
	 * attempting to use reflection to remove elements from {@code blacklistedMods} to play HeavyIndustry
	 * Mod, this itself contradicts the author's advocacy of protecting individual labor achievements.
	 */
	private static final String[] blacklistedMods = {"mfxiao2"};

	/** Don't let anyone instantiate this class. */
	private LoadMod() {}

	/**
	 * @param file the compressed file path of mod
	 * @return path to mod meta
	 * @throws NullPointerException If file is null
	 * @throws ArcRuntimeException If the path file is not available
	 */
	public static Jval getMeta(Fi file) {
		Fi metaFile = null;
		for (String name : metaFiles) {
			if ((metaFile = file.child(name)).exists()) {
				break;
			}
		}

		if (!metaFile.exists()) {
			return Jval.NULL;
		}

		return Jval.read(metaFile.reader());
	}

	/** Add {@link #blacklistedMods} to the {@code Mods.blacklistedMods} list. */
	public static void addBlacklistedMods() {
		try {
			ObjectSet<String> bm = Reflect.get(Mods.class, "blacklistedMods");
			bm.addAll(blacklistedMods);
		} catch (Exception e) {
			Log.err(e);
		}
	}

	@Deprecated
	public static void removeBlacklistedMods() {
		for (String name : blacklistedMods) {
			var mod = Vars.mods.getMod(name);
			if (mod != null) Vars.mods.removeMod(mod);
		}
	}
}
