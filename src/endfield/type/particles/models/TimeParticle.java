package endfield.type.particles.models;

import arc.math.Mathf;
import arc.util.Time;
import endfield.type.particles.ParticleEffect;
import endfield.type.particles.ParticleModel;

public class TimeParticle implements ParticleModel {
	public static final String BEGIN = "begin";
	public static final String LIFE_TIME = "lifeTime";
	public static final String PROGRESS = "progress";

	public float defLifeMin = 180, defLifeMax = 180;
	public boolean speedRelated;

	@Override
	public void init(ParticleEffect particle) {
		particle.setVar(BEGIN, Time.time);
	}

	@Override
	public void update(ParticleEffect p) {
		float lifeTime = p.getVar(LIFE_TIME, () -> Mathf.random(defLifeMin, defLifeMax));
		float time = Time.time - p.getVar(BEGIN, 0f);

		float prog = 1 - Mathf.clamp(time / lifeTime);
		p.setVar(PROGRESS, prog);

		if (speedRelated) {
			p.speed.setLength(p.defSpeed * prog);
		}
	}

	@Override
	public float currSize(ParticleEffect p) {
		return p.defSize * p.getVar(PROGRESS, 0f);
	}

	@Override
	public boolean isFinal(ParticleEffect p) {
		return p.getVar(PROGRESS, 0f) <= 0f;
	}
}
