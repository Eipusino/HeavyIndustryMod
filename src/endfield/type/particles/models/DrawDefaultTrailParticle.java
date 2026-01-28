package endfield.type.particles.models;

import endfield.type.particles.ParticleEffect;
import endfield.type.particles.ParticleModel;

public class DrawDefaultTrailParticle implements ParticleModel {
	@Override
	public void drawTrail(ParticleEffect particle) {
		float n = 0;
		for (ParticleEffect.Cloud c : particle) {
			c.draw(1 - n / particle.cloudCount(), 1 - (n + 1) / particle.cloudCount());
			n++;
		}
	}
}
