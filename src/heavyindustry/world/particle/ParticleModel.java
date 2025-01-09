package heavyindustry.world.particle;

import arc.graphics.*;
import arc.util.pooling.*;
import mindustry.graphics.*;

public class ParticleModel {
    /**
     * Create an instance of a particle using this model.
     *
     * @param x     The x-coordinate at the time of particle creation
     * @param y     The y-coordinate at the time of particle creation
     * @param color Particlef Color
     * @param sx    The x-component of the initial velocity of particle motion
     * @param sy    The y-component of the initial velocity of particle motion
     * @param size  Particlef size
     */
    public Particlef create(float x, float y, Color color, float sx, float sy, float size) {
        return create(x, y, color, sx, sy, size, Layer.effect);
    }

    /**
     * Create an instance of a particle using this model.
     *
     * @param parent The parent particle to which the particle belongs
     * @param x      The x-coordinate at the time of particle creation
     * @param y      The y-coordinate at the time of particle creation
     * @param color  Particlef Color
     * @param sx     The x-component of the initial velocity of particle motion
     * @param sy     The y-component of the initial velocity of particle motion
     * @param size   Particlef size
     */
    public Particlef create(Particlef parent, float x, float y, Color color, float sx, float sy, float size) {
        return create(parent, x, y, color, sx, sy, size, Layer.effect);
    }

    /**
     * Create an instance of a particle using this model.
     *
     * @param x     The x-coordinate at the time of particle creation
     * @param y     The y-coordinate at the time of particle creation
     * @param color Particlef Color
     * @param sx    The x-component of the initial velocity of particle motion
     * @param sy    The y-component of the initial velocity of particle motion
     * @param size  Particlef size
     * @param layer The layer where the particles are located is only used in the drawing process
     */
    public Particlef create(float x, float y, Color color, float sx, float sy, float size, float layer) {
        return create(null, x, y, color, sx, sy, size, layer);
    }

    /**
     * Create an instance of a particle using this model.
     *
     * @param parent The parent particle to which the particle belongs
     * @param x      The x-coordinate at the time of particle creation
     * @param y      The y-coordinate at the time of particle creation
     * @param color  Particlef Color
     * @param sx     The x-component of the initial velocity of particle motion
     * @param sy     The y-component of the initial velocity of particle motion
     * @param size   Particlef size
     * @param layer  The layer where the particles are located is only used in the drawing process
     */
    public Particlef create(Particlef parent, float x, float y, Color color, float sx, float sy, float size, float layer) {
        Particlef ent = Pools.obtain(Particlef.class, Particlef::new);
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

    public void draw(Particlef p) {}

    public void updateTrail(Particlef p, Particlef.Cloud c) {}

    public void update(Particlef p) {}

    public void deflect(Particlef p) {}

    public void drawTrail(Particlef c) {}

    public void init(Particlef particle) {}

    public boolean isFinal(Particlef p) {
        return false;
    }

    public Color trailColor(Particlef p) {
        return null;
    }

    public float currSize(Particlef p) {
        return p.defSize;
    }

    public boolean isFaded(Particlef p, Particlef.Cloud cloud) {
        return false;
    }
}
