package endfield.type.particles.models;

import arc.math.Interp;
import arc.math.Mathf;
import endfield.type.particles.ParticleEffect;
import endfield.type.particles.ParticleModel;

public class SizeVelRelatedParticle extends ParticleModel {
	public float finalThreshold = 0.25f;
	public float fadeThreshold = 0.03f;
	public Interp sizeInterp = Interp.linear;

	@Override
	public boolean isFinal(ParticleEffect p) {
		return p.speed.len() <= finalThreshold;
	}

	@Override
	public float currSize(ParticleEffect p) {
		return p.defSize * sizeInterp.apply(Mathf.clamp(p.speed.len() / p.defSpeed));
	}

	@Override
	public boolean isFaded(ParticleEffect p, ParticleEffect.Cloud cloud) {
		return cloud.size < fadeThreshold;
	}
}
