package heavyindustry.type.particles.models;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.struct.Seq;
import heavyindustry.type.particles.Particle;
import heavyindustry.type.particles.ParticleModel;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.ShapePart;
import mindustry.graphics.Layer;

public class DrawPartsParticle extends ParticleModel {
	public float layer = Layer.effect;
	public Seq<DrawPart> parts = new Seq<>(DrawPartsParticle.class);

	DrawPart.PartParams params = new DrawPart.PartParams();

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
}
