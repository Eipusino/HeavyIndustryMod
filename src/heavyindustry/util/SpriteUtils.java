package heavyindustry.util;

import arc.*;
import arc.graphics.g2d.*;
import arc.struct.*;

import java.util.*;

public final class SpriteUtils {
	/*
		  1
		4   2
		  3
	*/
	public static final int[] index44 = {
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
	public static final int[] index412raw = {
			0, 2, 10, 8, /**/143, 46, 78, 31, /**/38, 111, 110, 76,
			4, 6, 14, 12, /**/39, 127, 239, 77, /**/55, 95, 175, 207,
			5, 7, 15, 13, /**/23, 191, 223, 141, /**/63, 255, 240, 205,
			1, 3, 11, 9, /**/79, 27, 139, 47, /**/19, 155, 159, 137
	};

	public static final int[] index412 = new int[index412raw.length];
	public static final IntIntMap index412map = new IntIntMap();

	static {
		Integer[] indices = new Integer[index412raw.length];
		for (int i = 0; i < index412raw.length; i++) {
			indices[i] = i;
		}

        Arrays.sort(indices, UtilsKt.comparing(a -> index412raw[a]));

		for (int i = 0; i < indices.length; i++) {
			index412[indices[i]] = i;
		}

		for (int i = 0; i < index412raw.length; i++) {
			index412map.put(index412raw[i], index412[i]);
		}
	}

    private SpriteUtils() {}

	public static TextureRegion[] splitIndex(String name, int tileWidth, int tileHeight) {
		return splitIndex(Core.atlas.find(name), tileWidth, tileHeight);
	}

	public static TextureRegion[] splitIndex(String name, int tileWidth, int tileHeight, int pad) {
		return splitIndex(Core.atlas.find(name), tileWidth, tileHeight, pad);
	}

	public static TextureRegion[] splitIndex(String name, int tileWidth, int tileHeight, int pad, int[] indexMap) {
		return splitIndex(Core.atlas.find(name), tileWidth, tileHeight, pad, indexMap);
	}

	public static TextureRegion[] splitIndex(TextureRegion region, int tileWidth, int tileHeight) {
		return splitIndex(region, tileWidth, tileHeight, 0);
	}

	public static TextureRegion[] splitIndex(TextureRegion region, int tileWidth, int tileHeight, int pad) {
		return splitIndex(region, tileWidth, tileHeight, pad, null);
	}

	public static TextureRegion[] splitIndex(TextureRegion region, int tileWidth, int tileHeight, int pad, int[] indexMap) {
		int x = region.getX();
		int y = region.getY();
		int width = region.width;
		int height = region.height;

		int pWidth = tileWidth + pad * 2;
		int pHeight = tileHeight + pad * 2;

		int sw = width / pWidth;
		int sh = height / pHeight;

		int startX = x;
		TextureRegion[] tiles = new TextureRegion[sw * sh];
		for (int cy = 0; cy < sh; cy++, y += pHeight) {
			x = startX;
			for (int cx = 0; cx < sw; cx++, x += pWidth) {
				int index = cx + cy * sw;
				if (indexMap != null) {
					tiles[indexMap[index]] = new TextureRegion(region.texture, x + pad, y + pad, tileWidth, tileHeight);
				} else {
					tiles[index] = new TextureRegion(region.texture, x + pad, y + pad, tileWidth, tileHeight);
				}
			}
		}

		return tiles;
	}
}
