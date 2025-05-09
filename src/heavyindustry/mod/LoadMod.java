package heavyindustry.mod;

import arc.files.Fi;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.ArcRuntimeException;
import arc.util.Log;
import arc.util.Reflect;
import arc.util.serialization.Jval;
import heavyindustry.mod.ExtraContentParser.ExtraParseListener;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.mod.Mod;
import mindustry.mod.Mods;
import mindustry.mod.Mods.LoadedMod;
import mindustry.type.ErrorContent;
import org.tomlj.Toml;

import java.util.Locale;

public final class LoadMod {
	public static final String[] metaFiles = {"mod.json", "mod.hjson", "plugin.json", "plugin.hjson"};

	private static final ExtraContentParser parser = new ExtraContentParser();
	/**
	 * The following mods have stolen other mod textures or engaged in even worse behavior. If
	 * attempting to use reflection to remove elements from {@code blacklistedMods} to play HeavyIndustry
	 * Mod, this itself contradicts the author's advocacy of protecting individual labor achievements.
	 */
	private static final String[] blacklistedMods = {"\u949b\u94c5\u5de5\u4e1a", "mfxiao2"};

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

	public static Jval getMeta(LoadedMod mod) {
		if (mod == null) return Jval.NULL;
		return getMeta(mod.root);
	}

	public static Jval getMeta(String modName) {
		return getMeta(Vars.mods.getMod(modName));
	}

	public static Jval getMeta(Class<? extends Mod> type) {
		return getMeta(Vars.mods.getMod(type));
	}

	/**
	 * Expand the JSON/TOML parser.
	 * <p>The parsed content is stored in the {@code hjstoc} folder and does not conflict with {@code toml-parser}.
	 *
	 * @author Alon
	 * @since 1.0.6
	 */
	public static void loadContent() {
		Vars.content.setCurrentMod(null);

		Seq<LoadRun> runs = new Seq<>();

		for (LoadedMod mod : Vars.mods.orderedMods()) {
			if (mod.root.child("hjstoc").exists()) {
				Fi contentRoot = mod.root.child("hjstoc");
				for (ContentType type : ContentType.all) {
					String lower = type.name().toLowerCase(Locale.ROOT);

					Fi folder = contentRoot.child(lower + (lower.endsWith("s") ? "" : "s"));//units,items....

					if (folder.exists()) {
						for (Fi file : folder.findAll(f -> f.extension().equals("json") || f.extension().equals("hjson") || f.extension().equals("toml"))) {
							runs.add(new LoadRun(type, file, mod));
						}
					}
				}
			}
		}

		//ensure that mod content is in the appropriate order
		runs.sort();
		for (LoadRun l : runs) {
			Content current = Vars.content.getLastAdded();
			try {
				//this binds the content but does not load it entirely
				Content loaded = parser.parse(l.mod, l.file.nameWithoutExtension(),
						l.file.extension().equals("toml") ? Toml.parse(l.file.read()).toJson() :
								l.file.readString("UTF-8")
						, l.file, l.type);
				Log.debug("[@] Loaded '@'.", l.mod.meta.name, loaded instanceof UnlockableContent u ? u.localizedName : loaded.toString());
			} catch (Throwable e) {
				if (current != Vars.content.getLastAdded() && Vars.content.getLastAdded() != null) {
					parser.markError(Vars.content.getLastAdded(), l.mod, l.file, e);
				} else {
					ErrorContent error = new ErrorContent();
					parser.markError(error, l.mod, l.file, e);
				}
			}
		}

		//this finishes parsing content fields
		parser.finishParsing();
	}

	/** Adds a listener for parsed JSON objects. */
	public static void addParseListener(ExtraParseListener hook){
		parser.listeners.add(hook);
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

	/**
	 * Remove all mods from the blacklistedMods list.
	 *
	 * @deprecated Due to humanitarian reasons, this method is temporarily abandoned.
	 */
	@Deprecated
	public static void removeMods() {
		for (String name : blacklistedMods) {
			var mod = Vars.mods.getMod(name);
			if (mod != null) Vars.mods.removeMod(mod);
		}
	}
}

class LoadRun implements Comparable<LoadRun> {
	final ContentType type;
	final Fi file;
	final LoadedMod mod;

	LoadRun(ContentType content, Fi fi, LoadedMod loaded) {
		type = content;
		file = fi;
		mod = loaded;
	}

	@Override
	public int compareTo(LoadRun l) {
		int compare = mod.name.compareTo(l.mod.name);
		if (compare != 0) return compare;
		return file.name().compareTo(l.file.name());
	}
}