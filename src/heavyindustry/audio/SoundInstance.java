package heavyindustry.audio;

import arc.Core;
import arc.audio.Sound;

public class SoundInstance {
	public final Sound sound;

	protected int id = -1;

	public SoundInstance(Sound sound) {
		this.sound = sound;
	}

	public void play(float vol, float pitch, boolean loop) {
		if (id != -1) return;
		id = sound.play(vol, pitch, 0f, loop);
	}

	public void setVolume(float vol) {
		if (id == -1) return;
		Core.audio.setVolume(id, vol);
	}

	public void protect() {
		if (id == -1) return;

		Core.audio.protect(id, true);
	}

	public void stop() {
		if (id == -1) return;

		Core.audio.protect(id, false);
		Core.audio.stop(id);
		id = -1;
	}

	public int id() {
		return id;
	}
}
