package heavyindustry.gen;

import arc.audio.Music;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;

import static mindustry.Vars.tree;

/**
 * Manages music, including vanilla and custom tracks.
 *
 * @since 1.0.2
 */
public final class HMusics {
	private static final ObjectMap<String, Music[]> musicSets = new ObjectMap<>();

	/** Don't let anyone instantiate this class. */
	private HMusics() {}

	public static void onClient() {}

	/**
	 * Loads a set of music tracks from a specified base path.
	 *
	 * @param basePath   Base path for the music files.
	 * @param tracks Array of track names to load.
	 */
	public static void loadMusicSet(Class<?> clazz, String basePath, String[] tracks) {
		for (String track : tracks) {
			Music music = tree.loadMusic(basePath + track);
			try {
				clazz.getField(track).set(null, music);
			} catch (Exception e) {
				Log.err("Failed to load music: " + track, e);
			}
		}
	}

	private static void loadMusicSet(String basePath, String[] tracks) {
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
