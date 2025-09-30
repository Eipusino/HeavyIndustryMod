package heavyindustry.graphics.g2d;

import arc.Core;
import arc.audio.Sound;
import arc.func.Cons;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.GL20;
import arc.graphics.Texture;
import arc.graphics.g2d.Batch;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.Shader;
import arc.math.Mathf;
import arc.struct.Seq;
import heavyindustry.content.HFx;
import heavyindustry.entities.effect.Severation;
import mindustry.entities.Effect;

public class CutBatch extends Batch {
	protected final Color color = new Color(1, 1, 1, 1);

	public Effect explosionEffect;
	public Cons<Severation> cutHandler;
	public Sound sound;
	static Seq<Severation> returnEntities = new Seq<>(Severation.class);

	public Seq<Severation> switchBatch(Runnable run) {
		Batch last = Core.batch;
		GL20 lgl = Core.gl;
		Core.batch = this;
		Core.gl = FragmentationBatch.mock;
		Lines.useLegacyLine = true;
		returnEntities.clear();

		run.run();

		Lines.useLegacyLine = false;
		Core.batch = last;
		Core.gl = lgl;
		sound = null;

		return returnEntities;
	}

	@Override
	protected void setPackedMixColor(float packedColor) {}

	@Override
	protected void draw(Texture texture, float[] spriteVertices, int offset, int count) {}

	@Override
	protected void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float rotation) {
		float midX = (width / 2f);
		float midY = (height / 2f);

		float cos = Mathf.cosDeg(rotation);
		float sin = Mathf.sinDeg(rotation);
		float dx = midX - originX;
		float dy = midY - originY;

		float bx = (cos * dx - sin * dy) + (x + originX);
		float by = (sin * dx + cos * dy) + (y + originY);

		if (color.a <= 0.9f || region == FragmentationBatch.updateCircle() || blending != Blending.normal || region == Core.atlas.white() || !region.found()) {
			RejectedRegion r = new RejectedRegion();
			r.region = region;
			r.blend = blending;
			r.z = z;
			r.width = width;
			r.height = height;

			HFx.rejectedRegion.at(bx, by, rotation, color, r);
			return;
		}
		Severation c = Severation.generate(region, bx, by, width, height, rotation);
		c.color = colorPacked;
		c.z = z;
		if (sound != null) c.explosionSound = sound;
		if (explosionEffect != null) {
			c.explosionEffect = explosionEffect;
		}
		if (cutHandler != null) {
			cutHandler.get(c);
		}
		returnEntities.add(c);
	}

	@Override
	protected void flush() {}

	@Override
	protected void setShader(Shader shader, boolean apply) {}

	public static class RejectedRegion {
		public TextureRegion region;
		public Blending blend = Blending.normal;
		public float width, height, z;
	}
}
