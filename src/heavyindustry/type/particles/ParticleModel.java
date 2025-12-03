package heavyindustry.type.particles;

import arc.graphics.Color;
import arc.util.pooling.Pools;
import mindustry.graphics.Layer;

public class ParticleModel {
	/**
	 * Create an instance of a particle using this model.
	 *
	 * @param x     The x-coordinate at the time of particle creation
	 * @param y     The y-coordinate at the time of particle creation
	 * @param color Color of particles
	 * @param sx    The x-component of the initial velocity of particle motion
	 * @param sy    The y-component of the initial velocity of particle motion
	 * @param size  ParticleEffect size
	 */
	public ParticleEffect create(float x, float y, Color color, float sx, float sy, float size) {
		return create(x, y, color, sx, sy, size, Layer.effect);
	}

	/**
	 * Create an instance of a particle using this model.
	 *
	 * @param parent The parent particle to which the particle belongs
	 * @param x      The x-coordinate at the time of particle creation
	 * @param y      The y-coordinate at the time of particle creation
	 * @param color  Color of particles
	 * @param sx     The x-component of the initial velocity of particle motion
	 * @param sy     The y-component of the initial velocity of particle motion
	 * @param size   ParticleEffect size
	 */
	public ParticleEffect create(ParticleEffect parent, float x, float y, Color color, float sx, float sy, float size) {
		return create(parent, x, y, color, sx, sy, size, Layer.effect);
	}

	/**
	 * Create an instance of a particle using this model,
	 *
	 * @param x     The x-coordinate at the time of particle creation
	 * @param y     The y-coordinate at the time of particle creation
	 * @param color Color of particles
	 * @param sx    The x-component of the initial velocity of particle motion
	 * @param sy    The y-component of the initial velocity of particle motion
	 * @param size  ParticleEffect size
	 * @param layer The layer where the particles are located is only used in the drawing process
	 */
	public ParticleEffect create(float x, float y, Color color, float sx, float sy, float size, float layer) {
		return create(null, x, y, color, sx, sy, size, layer);
	}

	/**
	 * Create an instance of a particle using this model.
	 *
	 * @param parent The parent particle to which the particle belongs
	 * @param x      The x-coordinate at the time of particle creation
	 * @param y      The y-coordinate at the time of particle creation
	 * @param color  Color of particles
	 * @param sx     The x-component of the initial velocity of particle motion
	 * @param sy     The y-component of the initial velocity of particle motion
	 * @param size   ParticleEffect size
	 * @param layer  The layer where the particles are located is only used in the drawing process
	 */
	public ParticleEffect create(ParticleEffect parent, float x, float y, Color color, float sx, float sy, float size, float layer) {
		ParticleEffect ent = Pools.obtain(ParticleEffect.class, ParticleEffect::new);
		ent.parent = parent;
		ent.x = x;
		ent.y = y;
		ent.color.set(color);
		ent.layer = layer;
		ent.startPos.set(x, y);
		ent.speed.set(sx, sy);
		ent.defSpeed = ent.speed.len();
		ent.defSize = size;
		ent.size = currSize(ent);

		ent.model = this;
		ent.add();

		return ent;
	}

	public void draw(ParticleEffect p) {}

	public void updateTrail(ParticleEffect p, ParticleEffect.Cloud c) {}

	public void update(ParticleEffect p) {}

	public void deflect(ParticleEffect p) {}

	public void drawTrail(ParticleEffect c) {}

	public void init(ParticleEffect particle) {}

	public boolean isFinal(ParticleEffect p) {
		return false;
	}

	public Color trailColor(ParticleEffect p) {
		return null;
	}

	public float currSize(ParticleEffect p) {
		return p.defSize;
	}

	public boolean isFaded(ParticleEffect p, ParticleEffect.Cloud cloud) {
		return false;
	}
}
