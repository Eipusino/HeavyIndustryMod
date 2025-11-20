package heavyindustry.graphics;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.g2d.PixmapRegion;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;
import arc.graphics.g2d.TextureRegion;
import mindustry.graphics.Drawf;
import mindustry.graphics.MultiPacker;
import mindustry.graphics.MultiPacker.PageType;

public final class Outliner {
	/** Don't let anyone instantiate this class. */
	private Outliner() {}

	/** Outlines a given textureRegion. Run in createIcons. */
	public static void outlineRegion(MultiPacker packer, TextureRegion region, Color outlineColor, String name, int outlineRadius) {
		if (!(region instanceof AtlasRegion atlas) || !region.found()) return;

		PixmapRegion base = Core.atlas.getPixmap(atlas);
		Pixmap out = Pixmaps.outline(base, outlineColor, outlineRadius);

		Drawf.checkBleed(out);

		packer.add(PageType.main, name, out);
		out.dispose();
	}

	public static void outlineRegion(MultiPacker packer, TextureRegion tex, Color outlineColor, String name) {
		outlineRegion(packer, tex, outlineColor, name, 4);
	}

	/** Outlines a list of regions. Run in createIcons. */
	public static void outlineRegions(MultiPacker packer, TextureRegion[] textures, Color outlineColor, String name, int radius) {
		for (int i = 0; i < textures.length; i++) {
			outlineRegion(packer, textures[i], outlineColor, name + "-" + i, radius);
		}
	}

	public static void outlineRegions(MultiPacker packer, TextureRegion[] textures, Color outlineColor, String name) {
		outlineRegions(packer, textures, outlineColor, name, 4);
	}
}