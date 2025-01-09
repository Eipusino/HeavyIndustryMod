package heavyindustry.graphics;

import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.graphics.*;

import static arc.Core.*;

public final class Outliner {
    /** Don't let anyone instantiate this class. */
    private Outliner() {}

    /** Outlines a given textureRegion. Run in createIcons. */
    public static void outlineRegion(MultiPacker packer, TextureRegion texture, Color outlineColor, String name, int outlineRadius) {
        if (texture == null) return;
        PixmapRegion region = atlas.getPixmap(texture);
        Pixmap out = Pixmaps.outline(region, outlineColor, outlineRadius);
        if (settings.getBool("linear", true)) {
            Pixmaps.bleed(out);
        }
        packer.add(MultiPacker.PageType.main, name, out);
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