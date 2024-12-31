package heavyindustry.world.particle.model;

import arc.graphics.*;
import arc.math.*;
import heavyindustry.world.particle.*;

public class TrailFadeParticle extends ParticleModel {
    public float trailFade = 0.075f;
    public Color fadeColor;
    public float colorLerpSpeed = 0.03f;
    public boolean linear = false;

    @Override
    public void updateTrail(Particle p, Particle.Cloud c) {
        c.size = linear ? Mathf.approachDelta(c.size, 0, trailFade) : Mathf.lerpDelta(c.size, 0, trailFade);
        if (fadeColor != null) c.color.lerp(fadeColor, colorLerpSpeed);
    }
}
