package heavyindustry.world.particle.model;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import heavyindustry.world.particle.*;
import mindustry.entities.part.*;
import mindustry.graphics.*;

public class DrawPartsParticle extends ParticleModel {
    public float layer = Layer.effect;
    public Seq<DrawPart> parts = new Seq<>();

    DrawPart.PartParams params = new DrawPart.PartParams();

    public static DrawPartsParticle getSimpleCircle(float size, Color cc) {
        return new DrawPartsParticle() {{
            parts.add(new ShapePart() {{
                progress = PartProgress.warmup;
                this.color = cc;
                circle = true;
                radius = 0;
                radiusTo = size;
            }});
        }};
    }

    @Override
    public void draw(Particle p) {
        float z = Draw.z();
        Draw.z(layer);

        params.x = p.x;
        params.y = p.y;
        params.warmup = p.size / p.defSize;
        params.life = p.size / p.defSize;
        params.rotation = p.speed.angle();

        for (DrawPart part : parts) {
            part.draw(params);
        }

        Draw.z(z);
    }
}
