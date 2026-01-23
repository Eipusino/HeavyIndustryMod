package endfield.type.shape;

import arc.Core;
import arc.func.Intc2;
import arc.graphics.g2d.PixmapRegion;
import arc.util.Log;
import endfield.graphics.Pixmaps2;
import endfield.util.BitWordList;

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
		blocks.set(0, 1);
	}

	@Override
	public void load() {
		if (built) {
			return;
		}

		PixmapRegion region = Core.atlas.getPixmap(Core.atlas.find(maskName));

		if (region == null) {
			Log.err("Pixmap for CustomPatternShape is null for mask: @", maskName);
			return;
		}

		width = region.width;
		height = region.height;
		blocks = new BitWordList(width * height, BitWordList.WordLength.two);

		Pixmaps2.readTexturePixels(region, (color, index) -> {
			// from CustomShapeProp
			// 2815 = blue, center
			// 255 = black, part of shape
			// other = transparent, not part of shape
			switch (color) {
				case 2815 -> blocks.set(index, 3);
				case 255 -> blocks.set(index, 2);
				default -> blocks.set(index, 1);
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
