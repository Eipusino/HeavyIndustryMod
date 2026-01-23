package endfield.mod;

import arc.Core;
import arc.files.Fi;
import arc.files.ZipFi;
import arc.func.Boolf2;
import arc.util.ArcRuntimeException;
import arc.util.serialization.Jval;
import endfield.util.CollectionList;
import endfield.util.CollectionObjectSet;
import mindustry.mod.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public final class ModGetter {
	public static final Set<Throwable> errors = new CollectionObjectSet<>(Throwable.class);

	public static final String[] metaFiles = {"mod.json", "mod.hjson", "plugin.json", "plugin.hjson"};

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
	public static Fi getModFormat(Fi file) throws ArcRuntimeException {
		if (!(file instanceof ZipFi) && !file.isDirectory()) file = new ZipFi(file);

		Fi meta = null;
		for (String name : metaFiles) {
			if ((meta = file.child(name)).exists()) {
				break;
			}
		}

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
			getModFormat(modFile);
			return true;
		} catch (ArcRuntimeException e) {
			errors.add(e);
			return false;
		}
	}

	public static List<ModInfo> getModsWithFilter(Boolf2<Fi, Jval> filter) {
		List<ModInfo> result = new CollectionList<>(ModInfo.class);

		for (Fi file : modDirectory.list()) {
			try {
				Jval info = Jval.read(getModFormat(file).reader());
				if (filter.get(file, info)) {
					result.add(new ModInfo(file));
				}
			} catch (ArcRuntimeException e) {
				errors.add(e);
			}
		}

		return result;
	}

	public static List<ModInfo> getModsWithName(String name) {
		return getModsWithFilter((fi, jval) -> jval.getString("name").equals(name));
	}

	public static List<ModInfo> getModsWithClass(Class<? extends Mod> mainClass) {
		return getModsWithFilter((fi, jval) -> {
			String main = jval.getString("main");
			return main != null && main.equals(mainClass.getCanonicalName());
		});
	}

	public static @Nullable ModInfo getModWithName(String name) {
		List<ModInfo> list = getModsWithName(name);

		return list.isEmpty() ? null : list.get(0);
	}

	public static @Nullable ModInfo getModWithClass(Class<? extends Mod> mainClass) {
		List<ModInfo> list = getModsWithClass(mainClass);

		return list.isEmpty() ? null : list.get(0);
	}
}
