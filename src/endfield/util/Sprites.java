package endfield.util;

import arc.Core;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.IntIntMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * The utility set for split sprite.
 * <p>This class may be considered for merging into {@link Get} in future versions.
 *
 * @since 1.0.4
 */
public final class Sprites {
	public static final Point2[] orthogonalPos = {
			new Point2(0, 1),
			new Point2(1, 0),
			new Point2(0, -1),
			new Point2(-1, 0)
	};

	public static final Point2[][] diagonalPos = {
			{new Point2(1, 0), new Point2(1, 1), new Point2(0, 1)},
			{new Point2(1, 0), new Point2(1, -1), new Point2(0, -1)},
			{new Point2(-1, 0), new Point2(-1, -1), new Point2(0, -1)},
			{new Point2(-1, 0), new Point2(-1, 1), new Point2(0, 1)}
	};

	public static final Point2[] proximityPos = {
			new Point2(0, 1),
			new Point2(1, 0),
			new Point2(0, -1),
			new Point2(-1, 0),

			new Point2(1, 1),
			new Point2(1, -1),
			new Point2(-1, -1),
			new Point2(-1, 1)
	};

	/*
		  1
		4   2
		  3
	*/
	public static final int[] index4x4 = {
			0, 2, 10, 8,
			4, 6, 14, 12,
			5, 7, 15, 13,
			1, 3, 11, 9
	};

	/*
		8 1 5
		4   2
		7 3 6
	*/
	public static final int[] index4x12raw = {
			0, 2, 10, 8, 143, 46, 78, 31, 38, 111, 110, 76,
			4, 6, 14, 12, 39, 127, 239, 77, 55, 95, 175, 207,
			5, 7, 15, 13, 23, 191, 223, 141, 63, 255, 240, 205,
			1, 3, 11, 9, 79, 27, 139, 47, 19, 155, 159, 137
	};

	public static final int[] index4x12 = new int[index4x12raw.length];
	public static final IntIntMap index4r12map = new IntIntMap();

	static {
		int length = index4x12raw.length;

		int[] indices = new int[length];
		for (int i = 0; i < length; i++) {
			indices[i] = i;
		}

		for (int i = 1; i < indices.length; i++) {
			int key = indices[i];
			int keyValue = index4x12raw[key];
			int j = i - 1;

			while (j >= 0 && index4x12raw[indices[j]] > keyValue) {
				indices[j + 1] = indices[j];
				j = j - 1;
			}
			indices[j + 1] = key;
		}

		for (int i = 0; i < indices.length; i++) {
			index4x12[indices[i]] = i;
		}

		for (int i = 0; i < length; i++) {
			index4r12map.put(index4x12raw[i], index4x12[i]);
		}
	}

	private Sprites() {}

	/**
	 * Gets multiple regions inside a {@link TextureRegion}.
	 *
	 * @param name       sprite name
	 * @param size       split size, pixels per grid
	 * @param layerCount Total number of segmentation layers
	 * @throws NullPointerException       If the {@code name} is {@code null}.
	 * @throws NegativeArraySizeException If {@code size} or {@code layerCount} is negative.
	 * @apiNote The element returned by this method cannot be used in situations where it will be
	 * forcibly cast to {@link AtlasRegion}.
	 */
	@Contract(value = "_, _, _ -> new", pure = true)
	public static TextureRegion[][] splitLayers(String name, int size, int layerCount) {
		TextureRegion[][] layers = new TextureRegion[layerCount][];

		for (int i = 0; i < layerCount; i++) {
			layers[i] = splitLayer(name, size, i);
		}
		return layers;
	}

	/**
	 * Gets multiple regions inside a {@link TextureRegion}.
	 *
	 * @param name  sprite name
	 * @param size  split size, pixels per grid
	 * @param layer Number of segmentation layers
	 * @return Split sprites by size and layer parameter ratio.
	 * @throws NullPointerException	   If the {@code name} is {@code null}.
	 * @throws IllegalArgumentException If {@code size} or {@code layer} is negative.
	 * @apiNote The element returned by this method cannot be used in situations where it will be
	 * forcibly cast to {@link AtlasRegion}.
	 */
	@Contract(value = "_, _, _ -> new", pure = true)
	public static TextureRegion[] splitLayer(String name, int size, int layer) {
		TextureRegion region = Core.atlas.find(name);
		int margin = 0;
		int countX = region.width / size;
		TextureRegion[] tiles = new TextureRegion[countX];

		for (int i = 0; i < countX; i++) {
			tiles[i] = new TextureRegion(region, i * (margin + size), layer * (margin + size), size, size);
		}
		return tiles;
	}

	@Contract(value = "_, _ -> new", pure = true)
	public static TextureRegion[][] split(String name, int size) {
		return split(Core.atlas.find(name), size);
	}

	@Contract(value = "_, _ -> new", pure = true)
	public static TextureRegion[][] split(TextureRegion region, int size) {
		int x = region.getX();
		int y = region.getY();
		int width = region.width;
		int height = region.height;

		int sw = width / size;
		int sh = height / size;

		int startX = x;
		TextureRegion[][] tiles = new TextureRegion[sw][sh];
		for (int cy = 0; cy < sh; cy++, y += size) {
			x = startX;
			for (int cx = 0; cx < sw; cx++, x += size) {
				tiles[cx][cy] = new TextureRegion(region.texture, x, y, size, size);
			}
		}

		return tiles;
	}

	public static TextureRegion[] split(String name, int size, int width, int height) {
		return split(Core.atlas.find(name), size, width, height);
	}

	/**
	 * Gets multiple regions inside a {@link TextureRegion}.
	 *
	 * @param region    region to be split
	 * @param size   split size, pixels per grid
	 * @param width  The amount of regions horizontally.
	 * @param height The amount of regions vertically.
	 */
	@Contract(value = "_, _, _, _ -> new", pure = true)
	public static TextureRegion[] split(TextureRegion region, int size, int width, int height) {
		int textureSize = width * height;
		TextureRegion[] regions = new TextureRegion[textureSize];

		float tileWidth = (region.u2 - region.u) / width;
		float tileHeight = (region.v2 - region.v) / height;

		for (int i = 0; i < textureSize; i++) {
			float tileX = ((float) (i % width)) / width;
			float tileY = ((float) (i / width)) / height;
			TextureRegion out = new TextureRegion(region);

			//start coordinate
			out.u = Mathf.map(tileX, 0f, 1f, out.u, out.u2) + tileWidth * 0.02f;
			out.v = Mathf.map(tileY, 0f, 1f, out.v, out.v2) + tileHeight * 0.02f;
			//end coordinate
			out.u2 = out.u + tileWidth * 0.96f;
			out.v2 = out.v + tileHeight * 0.96f;

			out.width = out.height = size;

			regions[i] = out;
		}
		return regions;
	}

	@Contract(value = "_, _, _ -> new", pure = true)
	public static TextureRegion[][] splitTiles(TextureRegion region, int size, int pad) {
		int x = region.getX();
		int y = region.getY();
		int width = region.width;
		int height = region.height;

		int pWidth = size + pad * 2;
		int pHeight = size + pad * 2;

		int sw = width / pWidth;
		int sh = height / pHeight;

		int startX = x;

		TextureRegion[][] tiles = new TextureRegion[sw][sh];
		for (int cy = 0; cy < sh; cy++, y += pHeight) {
			x = startX;
			for (int cx = 0; cx < sw; cx++, x += pWidth) {
				tiles[cx][cy] = new TextureRegion(region.texture, x + pad, y + pad, size, size);
			}
		}

		return tiles;
	}

	public static TextureRegion[] splitArray(TextureRegion region, int size) {
		return splitArray(region, size, 0);
	}

	public static TextureRegion[] splitArray(TextureRegion region, int size, int pad) {
		return splitArray(region, size, pad, null);
	}

	@Contract(value = "_, _, _, _ -> new", pure = true)
	public static TextureRegion [] splitArray(TextureRegion region, int size, int pad, int @Nullable [] indexMap) {
		int x = region.getX();
		int y = region.getY();
		int width = region.width;
		int height = region.height;

		int pWidth = size + pad * 2;
		int pHeight = size + pad * 2;

		int sw = width / pWidth;
		int sh = height / pHeight;

		int startX = x;
		TextureRegion[] tiles = new TextureRegion[sw * sh];
		for (int cy = 0; cy < sh; cy++, y += pHeight) {
			x = startX;
			for (int cx = 0; cx < sw; cx++, x += pWidth) {
				int index = cx + cy * sw;
				if (indexMap != null) {
					tiles[indexMap[index]] = new TextureRegion(region.texture, x + pad, y + pad, size, size);
				} else {
					tiles[index] = new TextureRegion(region.texture, x + pad, y + pad, size, size);
				}
			}
		}

		return tiles;
	}
}
