package heavyindustry.world.particle.model;

import arc.math.*;
import heavyindustry.world.particle.*;

public class SizeVelRelatedParticle extends ParticleModel {
    public float finalThreshold = 0.25f;
    public float fadeThreshold = 0.03f;
    public Interp sizeInterp = Interp.linear;

    @Override
    public boolean isFinal(Particlef p) {
        return p.speed.len() <= finalThreshold;
    }

    @Override
    public float currSize(Particlef p) {
        return p.defSize * sizeInterp.apply(Mathf.clamp(p.speed.len() / p.defSpeed));
    }

    @Override
    public boolean isFaded(Particlef p, Particlef.Cloud cloud) {
        return cloud.size < fadeThreshold;
    }
}
