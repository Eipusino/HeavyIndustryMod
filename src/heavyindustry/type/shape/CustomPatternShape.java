package heavyindustry.type.shape;

import arc.Core;
import arc.func.Intc2;
import arc.graphics.g2d.PixmapRegion;
import arc.util.Log;
import heavyindustry.graphics.HPixmaps;
import heavyindustry.util.BitWordList;

// TODO: Consider supporting multiple shapes/variants within a single CustomPatternShape instance.
public class CustomPatternShape implements Shape {
	public final String maskName;
	private int width = 1;
	private int height = 1;
	private BitWordList blocks;
	private boolean built = false;

	public CustomPatternShape(String maskName) {
		this.maskName = maskName;
		blocks = new BitWordList(1, BitWordList.WordLength.two);
		blocks.set(0, (byte) 1);
	}

	@Override
	public void load() {
		if (built) {
			return;
		}

		PixmapRegion pixmap = Core.atlas.getPixmap(Core.atlas.find(maskName));

		if (pixmap == null) {
			Log.err("Pixmap for CustomPatternShape is null for mask: @", maskName);
			return;
		}

		width = pixmap.width;
		height = pixmap.height;
		blocks = new BitWordList(width * height, BitWordList.WordLength.two);

		HPixmaps.readTexturePixels(pixmap, (color, index) -> {
			// from CustomShapeProp
			// 2815 = blue, center
			// 255 = black, part of shape
			// other = transparent, not part of shape
			switch (color) {
				case 2815:
					blocks.set(index, (byte) 3);
					break;
				case 255:
					blocks.set(index, (byte) 2);
					break;
				default:
					blocks.set(index, (byte) 1);
					break;
			}
		});
		built = true;
	}

	@Override
	public int width() {
		return width;
	}

	@Override
	public int height() {
		return height;
	}

	@Override
	public boolean get(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			return false;
		}
		byte id = blocks.get(x + y * width);
		return id == 2 || id == 3;
	}

	@Override
	public void each(Intc2 consumer) {
		for (int i = 0; i < blocks.initialWordsAmount; i++) {
			byte id = blocks.get(i);
			if (id == 2 || id == 3) { // Part of shape or center
				consumer.get(i % width, i / width);
			}
		}
	}
}
