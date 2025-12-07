package heavyindustry.graphics;

import arc.files.Fi;
import arc.func.Intc2;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.Texture;
import arc.graphics.TextureData;
import arc.graphics.g2d.PixmapRegion;
import arc.graphics.gl.FileTextureData;
import arc.graphics.gl.PixmapTextureData;
import arc.math.Mathf;
import arc.util.Log;

import java.lang.reflect.Field;

public final class HPixmaps {
	public static Pixmap white;
	public static Pixmap bottleTop, bottleBottom;

	static Field pixmapField;

	private HPixmaps() {}

	public static void load() {
		white = new Pixmap(HTextures.texturesDir.child("white.png"));

		Fi itemsDir = HRegions.spritesDir.child("items");

		bottleTop = new Pixmap(itemsDir.child("bottle.png"));
		bottleBottom = new Pixmap(itemsDir.child("bottle-liquid.png"));
	}

	/**
	 * Rotate one {@link Pixmap} by a multiple of 90 degrees. This method does not change the original pixmap
	 * and returns a copy.
	 *
	 * @param target The target pixmap to be rotated.
	 * @param rotate Rotation angle coefficient, the actual rotation angle is 90 * rotate.
	 * @return A rotated pixmap copy.
	 */
	public static Pixmap rotatePixmap90(Pixmap target, int rotate) {
		Pixmap res = new Pixmap(target.width, target.height);

		for (int i = 0; i < target.width; i++) {
			for (int j = 0; j < target.height; j++) {
				int c = target.get(i, j);
				switch (Mathf.mod(-rotate, 4)) {
					case 0 -> res.set(i, j, c);
					case 1 -> res.set(target.width - j - 1, i, c);
					case 2 -> res.set(target.width - i - 1, target.height - j - 1, c);
					case 3 -> res.set(j, target.height - i - 1, c);
				}
			}
		}

		return res;
	}

	public static Pixmap gaussianBlur(Pixmap in, int blurWidth, int blurHeight, float deviation) {
		float[] blur = new float[(2 * blurWidth + 1) * (2 * blurHeight + 1)];
		float s = 2f * deviation * deviation, nor = 0f;

		for (int tx = -blurWidth; tx <= blurWidth; tx++) {
			for (int ty = -blurHeight; ty <= blurHeight; ty++) {
				float r = Mathf.sqrt(tx * tx + ty * ty);
				float res = Mathf.pow(Mathf.E, -(r * r) / s) / (Mathf.pi * s);

				blur[tx + blurWidth + (ty + blurHeight) * (2 * blurWidth + 1)] = res;
				nor += res;
			}
		}

		for (int i = 0; i < blur.length; i++) blur[i] /= nor;

		Pixmap ref = new Pixmap(in.width + blurWidth * 2, in.height + blurHeight * 2);
		ref.draw(in, blurWidth, blurHeight);
		Pixmaps.bleed(ref, Integer.MAX_VALUE);

		Color sum = new Color(), col = new Color();

		Pixmap out = new Pixmap(ref.width, ref.height);
		ref.each((x, y) -> {
			sum.set(0f, 0f, 0f, 0f);
			for (int tx = -blurWidth; tx <= blurWidth; tx++) {
				for (int ty = -blurHeight; ty <= blurHeight; ty++) {
					float factor = blur[tx + blurWidth + (ty + blurHeight) * (2 * blurWidth + 1)];
					col.set(ref.get(x + tx, y + ty));

					sum.r += col.r * factor;
					sum.g += col.g * factor;
					sum.b += col.b * factor;
					sum.a += col.a * factor;
				}
			}

			out.setRaw(x, y, sum.rgba());
		});

		ref.dispose();
		return out;
	}

	public static void drawCenter(Pixmap dst, Pixmap src) {
		dst.draw(src, dst.width / 2 - src.width / 2, dst.height / 2 - src.height / 2, true);
	}

	public static Pixmap copyScaled(Pixmap src, int w, int h) {
		Pixmap out = new Pixmap(w, h);

		Color a = new Color(), b = new Color(), c = new Color(), d = new Color();
		out.each((x, y) -> {
			float fracX = x / (float) w * src.width, fracY = y / (float) h * src.height;
			int fx = (int) fracX, tx = Math.min(fx + 1, src.width - 1);
			int fy = (int) fracY, ty = Math.min(fy + 1, src.height - 1);
			fracX -= fx;
			fracY -= fy;

			a.set(src.getRaw(fx, fy)).lerp(b.set(src.getRaw(tx, fy)), fracX);
			c.set(src.getRaw(fx, ty)).lerp(d.set(src.getRaw(tx, ty)), fracX);
			out.setRaw(x, y, a.lerp(c, fracY).rgba());
		});

		return out;
	}

	/** reads every single pixel on a textureRegion from bottom left to top right. */
	public static void readTexturePixels(PixmapRegion region, Intc2 cons) {
		for (int j = 0; j < region.height; j++) {
			for (int i = 0; i < region.width; i++) {
				cons.get(region.get(i, j), i + region.width * (region.height - 1 - j));
			}
		}
	}

	public static PixmapRegion color(PixmapRegion region, ColorBool cond, Int2Color to) {
		region.pixmap.each((x, y) -> {
			if (x >= region.x && x < region.x + region.width && y >= region.y && y < region.y + region.height && cond.get(region.pixmap.get(x, y))) {
				region.pixmap.set(x, y, to.get(x, y));
			}
		});
		return region;
	}

	public static PixmapRegion color(PixmapRegion region, Color from, Color to) {
		return color(region, c -> c == from.rgba(), (x, y) -> to);
	}

	public static Pixmap pixmapOf(Texture texture) {
		if (texture == null) return null;

		TextureData data = texture.getTextureData();

		if (data instanceof PixmapTextureData) return data.consumePixmap();
		if (data instanceof FileTextureData) {
			try {
				if (pixmapField == null) {
					pixmapField = FileTextureData.class.getDeclaredField("pixmap");
					pixmapField.setAccessible(true);
				}

				return (Pixmap) pixmapField.get(data);
			} catch (Exception e) {
				Log.err(e);// This shouldn't have happened.
			}
		}

		return null;
	}

	public interface ColorBool {
		boolean get(int color);
	}

	public interface Int2Color {
		Color get(int x, int y);
	}
}
