package heavyindustry.type.formloaders;

import arc.graphics.*;
import heavyindustry.struct.*;
import heavyindustry.struct.BitWordList.*;
import heavyindustry.type.CustomShape.*;
import heavyindustry.type.*;

public class PixmapShapeLoader extends CustomShapeLoader<Pixmap> {
    public final int voidColor, blockColor, anchorColor;

    public PixmapShapeLoader(int a, int b, int c) {
        voidColor = a;
        blockColor = b;
        anchorColor = c;
    }

    public PixmapShapeLoader(Color a, Color b, Color c) {
        this(a.rgba(), b.rgba(), c.rgba());
    }

    /**
     * @throws IllegalArgumentException is pixmap contains unknown colors
     */
    @Override
    public void load(Pixmap pixmap) {
        width = pixmap.width;
        height = pixmap.height;
        blocks = new BitWordList(width * height, WordLength.two);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int index = (width - 1 - x) + (y) * width;
                int c = pixmap.get(x, y);
                BlockType blockType;
                if (c == voidColor) {
                    blockType = BlockType.voidBlock;
                } else if (c == blockColor) {
                    blockType = BlockType.block;
                } else if (c == anchorColor) {
                    blockType = BlockType.anchorBlock;
                } else {
                    throw new IllegalArgumentException("Illegal color \"" + c + "\"");
                }
                blocks.set(index, (byte) blockType.ordinal());
            }
        }
    }
}
