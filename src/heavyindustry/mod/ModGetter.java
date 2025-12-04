package heavyindustry.mod;

import arc.Core;
import arc.files.Fi;
import arc.files.ZipFi;
import arc.func.Boolf2;
import arc.func.Cons;
import arc.struct.Seq;
import arc.util.ArcRuntimeException;
import arc.util.Log;
import arc.util.serialization.Jval;
import mindustry.mod.Mod;

public final class ModGetter {
	static final String[] metaFiles = {"mod.json", "mod.hjson", "plugin.json", "plugin.hjson"};

	/** Mod folder location. */
	public static final Fi modDirectory = Core.settings.getDataDirectory().child("mods");

	private ModGetter() {}

	/**
	 * Pass in a file and check if it is a mod file. If it is, return the mod's meta file. If not, throw {@link ArcRuntimeException}.
	 *
	 * @param file Check the file, which can be a directory
	 * @return The main meta file of this mod
	 * @throws ArcRuntimeException If this file is not a mod
	 */
	public static Fi checkModFormat(Fi file) throws ArcRuntimeException {
		try {
			if (!(file instanceof ZipFi) && !file.isDirectory()) file = new ZipFi(file);
		} catch (Throwable e) {
			throw new ArcRuntimeException("file was not a valid zipped file");
		}

		Fi meta = null;
		for (String name : metaFiles) {
			if ((meta = file.child(name)).exists()) {
				break;
			}
		}

		if (!meta.exists()) throw new ArcRuntimeException("mod format error: mod meta was not found");

		return meta;
	}

	public static void checkModFormat(Fi file, Cons<Fi> cons) {
		try {
			if (!(file instanceof ZipFi) && !file.isDirectory()) file = new ZipFi(file);
		} catch (Throwable e) {
			Log.err("file was not a valid zipped file", e);

			return;
		}

		Fi meta = null;
		for (String name : metaFiles) {
			if ((meta = file.child(name)).exists()) {
				break;
			}
		}

		if (meta.exists()) cons.get(meta);
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
		return getModsWithFilter((fi, jval) -> {
			String main = jval.getString("main");
			return main != null && main.equals(mainClass.getCanonicalName());
		});
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
