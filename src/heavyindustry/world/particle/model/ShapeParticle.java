package heavyindustry.world.particle.model;

import arc.graphics.g2d.*;
import arc.util.*;
import heavyindustry.world.particle.*;
import mindustry.graphics.*;

public class ShapeParticle extends ParticleModel {
    public boolean circle = true;
    public int polySides = 4;
    public boolean outline;
    public float outlineStoke = 1.6f;
    public float rotSpeed;

    public float layer = Layer.effect;

    @Override
    public void draw(Particle p) {
        float l = Draw.z();
        Draw.z(layer);

        Draw.color(p.color);
        if (circle) {
            if (outline) {
                Lines.stroke(outlineStoke);
                Lines.circle(p.x, p.y, p.size);
            } else Fill.circle(p.x, p.y, p.size);
        } else {
            if (outline) {
                Lines.stroke(outlineStoke);
                Lines.poly(p.x, p.y, polySides, p.size, Time.time * rotSpeed);
            } else Lines.poly(p.x, p.y, polySides, p.size, Time.time * rotSpeed);
        }

        Draw.z(l);
    }
}
