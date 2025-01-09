package heavyindustry.world.particle;

import arc.graphics.*;
import arc.util.*;

public class MultiParticleModel extends ParticleModel {
    public ParticleModel[] models;

    public MultiParticleModel(ParticleModel... models) {
        this.models = models;
    }

    @Override
    public void draw(Particlef p) {
        for (ParticleModel model : models) {
            model.draw(p);
        }
    }

    @Override
    public void drawTrail(Particlef c) {
        for (ParticleModel model : models) {
            model.drawTrail(c);
        }
    }

    @Override
    public void updateTrail(Particlef p, Particlef.Cloud c) {
        for (ParticleModel model : models) {
            model.updateTrail(p, c);
        }
    }

    @Override
    public void update(Particlef p) {
        for (ParticleModel model : models) {
            if (model == null) break;
            model.update(p);
        }
    }

    @Override
    public void init(Particlef p) {
        for (ParticleModel model : models) {
            model.init(p);
        }
    }

    @Override
    public Color trailColor(Particlef p) {
        Tmp.c1.set(p.color);
        for (ParticleModel model : models) {
            Color c = model.trailColor(p);
            if (c == null) continue;
            Tmp.c1.mul(c);
        }
        return Tmp.c1;
    }

    @Override
    public void deflect(Particlef p) {
        for (ParticleModel model : models) {
            model.deflect(p);
        }
    }

    @Override
    public boolean isFinal(Particlef p) {
        for (ParticleModel model : models) {
            if (model.isFinal(p)) return true;
        }
        return false;
    }

    @Override
    public boolean isFaded(Particlef p, Particlef.Cloud cloud) {
        for (ParticleModel model : models) {
            if (model.isFaded(p, cloud)) return true;
        }
        return false;
    }

    @Override
    public float currSize(Particlef p) {
        float res = Float.MAX_VALUE;

        for (ParticleModel model : models) {
            res = Math.min(model.currSize(p), res);
        }

        return res;
    }
}
