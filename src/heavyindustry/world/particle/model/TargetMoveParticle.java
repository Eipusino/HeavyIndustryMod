package heavyindustry.world.particle.model;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import heavyindustry.world.particle.*;

public class TargetMoveParticle extends ParticleModel {
    public Floatf<Particlef> deflection = e -> 0.2f;
    public Func<Particlef, Vec2> dest;

    @Override
    public void deflect(Particlef e) {
        float from = e.speed.angle();
        Vec2 dest = this.dest.get(e);
        float to = Tmp.v1.set(dest.x, dest.y).sub(e.x, e.y).angle();
        float r = to - from;
        r = r > 180 ? r - 360 : r < -180 ? r + 360 : r;
        e.speed.rotate(r * deflection.get(e) * Time.delta);
    }

    @Override
    public boolean isFinal(Particlef e) {
        Vec2 dest = this.dest.get(e);
        return Mathf.len(e.x - dest.x, e.y - dest.y) <= 2f;
    }
}
