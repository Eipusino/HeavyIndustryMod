package endfield.audio;

import arc.Events;
import arc.audio.Music;
import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Log;
import endfield.util.CollectionObjectMap;
import mindustry.Vars;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.gen.Musics;

import java.util.Map;

/**
 * Manages music, including vanilla and custom tracks.
 *
 * @since 1.0.2
 */
public final class Musics2 {
	static final Map<String, Music[]> musicSets = new CollectionObjectMap<>(String.class, Music[].class);

	public static Fi musicsDir;// = Vars2.internalTree.child("musics");

	/** Don't let anyone instantiate this class. */
	private Musics2() {}

	public static void load() {
		try {
			// no
		} catch (Exception e) {
			Log.err(e);

			Events.on(ClientLoadEvent.class, event -> Vars.ui.showException(e));
		}
	}

	public static Music load(String name) throws Exception {
		Fi file = musicsDir.child(name);

		if (file.exists()) return new Music(file);

		Log.warn("The path @ does not exist!", file.name());

		return Musics.menu;
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
