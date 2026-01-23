package endfield.type.particles.models;

import arc.graphics.Color;
import arc.math.Mathf;
import endfield.type.particles.ParticleEffect;
import endfield.type.particles.ParticleModel;

public class TrailFadeParticle extends ParticleModel {
	public float trailFade = 0.075f;
	public Color fadeColor;
	public float colorLerpSpeed = 0.03f;
	public boolean linear = false;

	@Override
	public void updateTrail(ParticleEffect p, ParticleEffect.Cloud c) {
		c.size = linear ? Mathf.approachDelta(c.size, 0, trailFade) : Mathf.lerpDelta(c.size, 0, trailFade);
		if (fadeColor != null) c.color.lerp(fadeColor, colorLerpSpeed);
	}
}
