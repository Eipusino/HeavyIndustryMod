package heavyindustry.mod;

import arc.Core;
import arc.files.Fi;
import arc.files.ZipFi;
import arc.func.Boolf2;
import arc.struct.Seq;
import arc.util.ArcRuntimeException;
import arc.util.serialization.Jval;
import mindustry.mod.Mod;

public final class ModGetter {
	/** Mod folder location. */
	public static final Fi modDirectory = Core.settings.getDataDirectory().child("mods");

	private ModGetter() {}

	/**
	 * Pass in a file and check if it is a mod file. If it is, return the mod's meta file. If not, throw {@link ArcRuntimeException}.
	 *
	 * @param modFile Check the file, which can be a directory
	 * @return The main meta file of this mod
	 * @throws ArcRuntimeException If this file is not a mod
	 */
	public static Fi checkModFormat(Fi modFile) throws ArcRuntimeException {
		try {
			if (!(modFile instanceof ZipFi) && !modFile.isDirectory()) modFile = new ZipFi(modFile);
		} catch (Throwable e) {
			throw new ArcRuntimeException("file was not a valid zipped file");
		}

		Fi meta = modFile.child("mod.json").exists() ? modFile.child("mod.json") :
				modFile.child("mod.hjson").exists() ? modFile.child("mod.hjson") :
						modFile.child("plugin.json").exists() ? modFile.child("plugin.json") : modFile.child("plugin.hjson");

		if (!meta.exists()) throw new ArcRuntimeException("mod format error: mod meta was not found");

		return meta;
	}

	/**
	 * Determine whether the incoming file is a mod
	 *
	 * @param modFile Checked files
	 * @return The result represented by Boolean value
	 */
	public static boolean isMod(Fi modFile) {
		try {
			checkModFormat(modFile);
			return true;
		} catch (ArcRuntimeException e) {
			return false;
		}
	}

	public static Seq<ModInfo> getModsWithFilter(Boolf2<Fi, Jval> filter) {
		Seq<ModInfo> result = new Seq<>(ModInfo.class);

		for (Fi file : modDirectory.list()) {
			try {
				Jval info = Jval.read(checkModFormat(file).reader());
				if (filter.get(file, info)) {
					result.add(new ModInfo(file));
				}
			} catch (ArcRuntimeException ignored) {}
		}

		return result;
	}

	public static Seq<ModInfo> getModsWithName(String name) {
		return getModsWithFilter((fi, jval) -> jval.getString("name").equals(name));
	}

	public static Seq<ModInfo> getModsWithClass(Class<? extends Mod> mainClass) {
		return getModsWithFilter((fi, jval) -> jval.getString("main").equals(mainClass.getCanonicalName()));
	}

	public static ModInfo getModWithName(String name) {
		Seq<ModInfo> seq = getModsWithName(name);

		return seq.isEmpty() ? null : seq.first();
	}

	public static ModInfo getModWithClass(Class<? extends Mod> mainClass) {
		Seq<ModInfo> seq = getModsWithClass(mainClass);

		return seq.isEmpty() ? null : seq.first();
	}
}
