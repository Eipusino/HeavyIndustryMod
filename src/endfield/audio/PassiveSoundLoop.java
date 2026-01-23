package endfield.audio;

import arc.Core;
import arc.audio.Sound;
import arc.util.Time;
import endfield.math.Mathm;

public class PassiveSoundLoop {
	public static final float fadeSpeed = 0.075f;

	public final Sound sound;

	protected int id = -1;
	protected float volume, baseVolume;
	protected boolean played;
	protected float x, y;
	protected float pan;
	protected boolean constant = false, doppler = false;

	public PassiveSoundLoop(Sound sound) {
		this.sound = sound;
	}

	public PassiveSoundLoop(Sound sound, boolean constant, boolean doppler) {
		this(sound);
		this.constant = constant;
		this.doppler = doppler;
	}

	public void update() {
		if (!played && id > 0) {
			volume = Mathm.clamp(volume - fadeSpeed * Time.delta);

			if (volume <= 0.001f) {
				Core.audio.stop(id);
				id = -1;
				return;
			}

			float v = constant ? 1f : sound.calcVolume(x, y);

			Core.audio.set(id, pan, v * volume * baseVolume);
		}
		played = false;
	}

	public float calcFalloff(float x, float y) {
		return sound.calcFalloff(x, y);
	}

	public float calcPan(float x, float y) {
		return sound.calcPan(x, y);
	}

	public void play(float x, float y, float vol, boolean play) {
		play(x, y, vol, sound.calcPan(x, y), play);
	}

	public void play(float x, float y, float vol, float pan, boolean play) {
		if (id < 0) {
			if (play) {
				float v = constant ? 1f : sound.calcVolume(x, y);

				baseVolume = vol;
				id = sound.loop(v * volume * vol, 1f, sound.calcPan(x, y));
				this.x = x;
				this.y = y;
				this.pan = pan;
				played = true;
			}
		} else {
			float v = constant ? 1f : sound.calcVolume(x, y);
			float lx = this.x, ly = this.y;

			baseVolume = vol;
			this.x = x;
			this.y = y;
			this.pan = pan;
			played = play;

			Core.audio.set(id, pan, v * volume * vol);

			if (doppler) {
				float dst1 = Core.camera.position.dst(lx, ly);
				float dst2 = Core.camera.position.dst(x, y);

				float d = ((dst2 - dst1) / 3f) / Time.delta;
				float w = 20f;
				float delta = Mathm.clamp(w / (w + Math.max(d, -(w - 1f))), 0.5f, 3f);

				Core.audio.setPitch(id, delta);
			}
		}
		if (play) volume = Mathm.clamp(volume + fadeSpeed * Time.delta);
	}

	public void stop() {
		if (id > 0) {
			Core.audio.stop(id);
			id = -1;
		}
	}

	public int id() {
		return id;
	}

	public float x() {
		return x;
	}

	public float y() {
		return y;
	}

	@Override
	public String toString() {
		return sound.toString();
	}
}
