package heavyindustry.type.particles.models;

import heavyindustry.type.particles.Particle;
import heavyindustry.type.particles.ParticleModel;

public class DrawDefaultTrailParticle extends ParticleModel {
	@Override
	public void drawTrail(Particle particle) {
		float n = 0;
		for (Particle.Cloud c : particle) {
			c.draw(1 - n / particle.cloudCount(), 1 - (n + 1) / particle.cloudCount());
			n++;
		}
	}
}
