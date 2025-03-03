package heavyindustry.type.form;

import arc.graphics.Pixmap;
import heavyindustry.struct.BitWordList;
import heavyindustry.struct.BitWordList.WordLength;
import heavyindustry.type.CustomShape.BlockType;
import heavyindustry.type.CustomShapeLoader;

public class SpriteShapeLoader extends CustomShapeLoader<Pixmap> {
	public final int chunkSize;
	public final ChunkProcessor chunkProcessor;

	public SpriteShapeLoader(int size, ChunkProcessor processor) {
		chunkSize = size;
		chunkProcessor = processor;
	}

	@Override
	public void load(Pixmap type) {
		width = type.width / chunkSize;
		height = type.height / chunkSize;
		blocks = new BitWordList(width * height, WordLength.two);
		//type= type.flipX();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int index = i + (height - 1 - j) * width;
				blocks.set(index, (byte) chunkProcessor.process(type, i, j, chunkSize).ordinal());
			}
		}
	}

	public interface ChunkProcessor {
		BlockType process(Pixmap pix, int x, int y, int s);

		class PercentProcessor implements ChunkProcessor {
			/** [0-1] */
			public float percent;
			public int anchorChunkX;
			public int anchorChunkY;

			public PercentProcessor(float per, int x, int y) {
				percent = per;
				anchorChunkX = x;
				anchorChunkY = y;
			}

			@Override
			public BlockType process(Pixmap pix, int x, int y, int s) {
				if (x == anchorChunkX && y == anchorChunkY) return BlockType.anchorBlock;
				int total = s * s;
				int worldX = x * s;
				int worldY = y * s;
				float counter = 0;
				for (int dx = 0; dx < s; dx++) {
					for (int dy = 0; dy < s; dy++) {
						counter += pix.getA(worldX + dx, worldY + dy) / 255f;
					}
				}
				return counter / total > percent ? BlockType.block : BlockType.voidBlock;
			}
		}
	}
}
