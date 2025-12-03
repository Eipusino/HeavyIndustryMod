package heavyindustry.type.particles.models;

import heavyindustry.type.particles.ParticleEffect;
import heavyindustry.type.particles.ParticleModel;

public class DrawDefaultTrailParticle extends ParticleModel {
	@Override
	public void drawTrail(ParticleEffect particle) {
		float n = 0;
		for (ParticleEffect.Cloud c : particle) {
			c.draw(1 - n / particle.cloudCount(), 1 - (n + 1) / particle.cloudCount());
			n++;
		}
	}
}
