package heavyindustry.audio;

import arc.Events;
import arc.audio.Music;
import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Log;
import heavyindustry.HVars;
import heavyindustry.util.CollectionObjectMap;
import mindustry.Vars;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.gen.Musics;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Manages music, including vanilla and custom tracks.
 *
 * @since 1.0.2
 */
public final class HMusics {
	static final Map<String, Music[]> musicSets = new CollectionObjectMap<>(String.class, Music[].class);

	/** Don't let anyone instantiate this class. */
	private HMusics() {}

	public static void load() {
		try {
			// no
		} catch (Exception e) {
			Log.err(e);

			Events.on(ClientLoadEvent.class, event -> Vars.ui.showException(e));
		}
	}

	public static Music load(String name) throws Exception {
		Fi file = HVars.internalTree.resolve("musics/" + name);

		if (file.exists()) {
			return new Music(file);
		}

		Log.warn("The path @ does not exist!", file.name());

		return Musics.menu;
	}

	/**
	 * Loads a set of music tracks from a specified base path.
	 *
	 * @param basePath   Base path for the music files.
	 * @param tracks Array of track names to load.
	 */
	public static void loadMusicSet(Class<?> clazz, String basePath, String[] tracks) {
		for (String track : tracks) {
			Music music = Vars.tree.loadMusic(basePath + track);
			try {
				Field field = clazz.getField(track);

				if (field.getType() != Music.class || !Modifier.isStatic(field.getModifiers())) {
					Log.warn("Failed to load music: @, @", track, field);

					continue;
				}

				field.set(null, music);
			} catch (Exception e) {
				Log.err("Failed to load music: " + track, e);
			}
		}
	}

	static void loadMusicSet(String basePath, String[] tracks) {
		loadMusicSet(HMusics.class, basePath, tracks);
	}

	/**
	 * Mixes two music sets and assigns the result to a target set.
	 *
	 * @param target Target sequence to store the mixed music.
	 */
	public static void mixMusicSets(String vanillaSetName, String modSetName, Seq<Music> target) {
		Music[] vanillaSet = musicSets.get(vanillaSetName);
		Music[] modSet = musicSets.get(modSetName);
		if (vanillaSet != null && modSet != null) {
			target.clear();
			target.addAll(vanillaSet);
			target.addAll(modSet);
		}
	}

	/**
	 * Sets a music set to a target sequence.
	 *
	 * @param setName Name of the music set to use.
	 * @param target  Target sequence to update.
	 */
	public static void setMusicSet(String setName, Seq<Music> target) {
		Music[] set = musicSets.get(setName);
		if (set != null) {
			target.set(set);
		}
	}
}
