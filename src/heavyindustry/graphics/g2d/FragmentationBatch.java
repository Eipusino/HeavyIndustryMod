package heavyindustry.graphics.g2d;

import arc.Core;
import arc.audio.Sound;
import arc.func.Cons;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.GL20;
import arc.graphics.Texture;
import arc.graphics.g2d.Batch;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.Shader;
import arc.math.Mathf;
import arc.mock.MockGL20;
import heavyindustry.entities.effect.Fragmentation;
import heavyindustry.entities.effect.Fragmentation.FragmentEntity;
import mindustry.entities.Effect;
import mindustry.graphics.Pal;

public class FragmentationBatch extends BaseBatch {
	protected static TextureRegion circle;
	protected static GL20 mock = new MockGL20();

	public float baseElevation;
	public Cons<FragmentEntity> fragFunc = e -> {}, onDeathFunc = null;
	public Cons<Fragmentation> fragDataFunc = null;
	public AltFragFunc altFunc = (x, y, tex) -> {};
	public Effect trailEffect, explosionEffect;
	public Color fragColor = Color.white, goreColor = Pal.darkerMetal;
	public boolean useAlt = true, genGore = false;
	public float resScale = 1f, islandScl = 1f;
	public Sound sound = null;
	//public Floatc2 altFunc = (x, y) -> {};

	public static TextureRegion updateCircle() {
		if (circle == null || circle.texture.isDisposed()) {
			circle = Core.atlas.find("circle");
		}
		return circle;
	}

	public void switchBatch(Runnable run) {
		Batch last = Core.batch;
		GL20 lgl = Core.gl;
		Core.batch = this;
		Core.gl = mock;
		Lines.useLegacyLine = true;

		run.run();

		Lines.useLegacyLine = false;
		Core.batch = last;
		Core.gl = lgl;
		onDeathFunc = null;
		fragDataFunc = null;
		useAlt = true;
		resScale = islandScl = 1f;
		genGore = false;
		goreColor = Pal.darkerMetal;
		sound = null;
	}

	@Override
	protected void setPackedMixColor(float packedColor) {}

	@Override
	protected void draw(Texture texture, float[] spriteVertices, int offset, int count) {}

	@Override
	protected void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float rotation) {
		if (color.a <= 0.9f || region == updateCircle() || blending != Blending.normal || region == Core.atlas.white() || !region.found())
			return;

		//int dim = Math.max(region.width, region.height);
		float dim = Math.max(width, height) / Draw.scl;

		//float midX = (width / 2f) * region.scl(), midY = (height / 2f) * region.scl();
		float midX = (width / 2f), midY = (height / 2f);

		//float worldOriginX = originX;
		//float worldOriginY = originY;

		//Fix alignment
		float cos = Mathf.cosDeg(rotation), sin = Mathf.sinDeg(rotation);
		float dx = midX - originX, dy = midY - originY;
		//float dx = originX - midX, dy = originY - midY;
		//float dx = -midX, dy = -midY;

		float bx = (cos * dx - sin * dy) + (x + originX), by = (sin * dx + cos * dy) + (y + originY);
		//float bx = (cos * dx - sin * dy) + (x + Math.abs(midX * cos - midY * sin)), by = (sin * dx + cos * dy) + (y + Math.abs(midX * sin + midY * cos));

		if (dim >= (4 * 32) || !useAlt) {
			Fragmentation frag = Fragmentation.generate(bx, by, rotation, width, height, z, baseElevation, resScale, islandScl, region, genGore ? fr -> {
				fragFunc.get(fr);
				fr.generateGore();
			} : fragFunc);
			frag.drawnColor.set(color);
			if (genGore) frag.goreColor.set(goreColor);
			if (trailEffect != null) frag.trailEffect = trailEffect;
			if (explosionEffect != null) frag.explosionEffect = explosionEffect;
			if (sound != null) frag.explosionSound = sound;
			frag.effectColor = fragColor;
			frag.onDeath = onDeathFunc;
			if (fragDataFunc != null) fragDataFunc.get(frag);
		} else {
			altFunc.frag(bx, by, region);
		}
	}

	@Override
	protected void flush() {}

	@Override
	protected void setShader(Shader shader, boolean apply) {}

	public interface AltFragFunc {
		void frag(float x, float y, TextureRegion region);
	}
}
