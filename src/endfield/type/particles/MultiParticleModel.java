package endfield.type.particles;

import arc.graphics.Color;
import arc.util.Tmp;

public class MultiParticleModel implements ParticleModel {
	public ParticleModel[] models;

	public MultiParticleModel(ParticleModel[] models) {
		this.models = models;
	}

	public static MultiParticleModel of(ParticleModel... models) {
		return new MultiParticleModel(models);
	}

	@Override
	public void draw(ParticleEffect p) {
		for (ParticleModel model : models) {
			model.draw(p);
		}
	}

	@Override
	public void drawTrail(ParticleEffect c) {
		for (ParticleModel model : models) {
			model.drawTrail(c);
		}
	}

	@Override
	public void updateTrail(ParticleEffect p, ParticleEffect.Cloud c) {
		for (ParticleModel model : models) {
			model.updateTrail(p, c);
		}
	}

	@Override
	public void update(ParticleEffect p) {
		for (ParticleModel model : models) {
			if (model == null) break;
			model.update(p);
		}
	}

	@Override
	public void init(ParticleEffect p) {
		for (ParticleModel model : models) {
			model.init(p);
		}
	}

	@Override
	public Color trailColor(ParticleEffect p) {
		Tmp.c1.set(p.color);
		for (ParticleModel model : models) {
			Color c = model.trailColor(p);
			if (c == null) continue;
			Tmp.c1.mul(c);
		}
		return Tmp.c1;
	}

	@Override
	public void deflect(ParticleEffect p) {
		for (ParticleModel model : models) {
			model.deflect(p);
		}
	}

	@Override
	public boolean isFinal(ParticleEffect p) {
		for (ParticleModel model : models) {
			if (model.isFinal(p)) return true;
		}
		return false;
	}

	@Override
	public boolean isFaded(ParticleEffect p, ParticleEffect.Cloud cloud) {
		for (ParticleModel model : models) {
			if (model.isFaded(p, cloud)) return true;
		}
		return false;
	}

	@Override
	public float currSize(ParticleEffect p) {
		float res = Float.MAX_VALUE;

		for (ParticleModel model : models) {
			res = Math.min(model.currSize(p), res);
		}

		return res;
	}
}
