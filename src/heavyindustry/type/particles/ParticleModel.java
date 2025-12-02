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
	 * @param size  Particle size
	 */
	public Particle create(float x, float y, Color color, float sx, float sy, float size) {
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
	 * @param size   Particle size
	 */
	public Particle create(Particle parent, float x, float y, Color color, float sx, float sy, float size) {
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
	 * @param size  Particle size
	 * @param layer The layer where the particles are located is only used in the drawing process
	 */
	public Particle create(float x, float y, Color color, float sx, float sy, float size, float layer) {
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
	 * @param size   Particle size
	 * @param layer  The layer where the particles are located is only used in the drawing process
	 */
	public Particle create(Particle parent, float x, float y, Color color, float sx, float sy, float size, float layer) {
		Particle ent = Pools.obtain(Particle.class, Particle::new);
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

	public void draw(Particle p) {}

	public void updateTrail(Particle p, Particle.Cloud c) {}

	public void update(Particle p) {}

	public void deflect(Particle p) {}

	public void drawTrail(Particle c) {}

	public void init(Particle particle) {}

	public boolean isFinal(Particle p) {
		return false;
	}

	public Color trailColor(Particle p) {
		return null;
	}

	public float currSize(Particle p) {
		return p.defSize;
	}

	public boolean isFaded(Particle p, Particle.Cloud cloud) {
		return false;
	}
}
