package heavyindustry.world.particle.model;

import heavyindustry.world.particle.*;

public class DrawDefaultTrailParticle extends ParticleModel {
    @Override
    public void drawTrail(Particlef particle) {
        float n = 0;
        for (Particlef.Cloud c : particle) {
            c.draw(1 - n / particle.cloudCount(), 1 - (n + 1) / particle.cloudCount());
            n++;
        }
    }
}
