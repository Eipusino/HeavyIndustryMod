package heavyindustry.type.formloaders;

import arc.graphics.*;
import heavyindustry.struct.*;
import heavyindustry.struct.BitWordList.*;
import heavyindustry.type.CustomShape.*;
import heavyindustry.type.*;

public class SpriteShapeLoader extends CustomShapeLoader<Pixmap> {
	public final int chunkSize;
	public final ChunkProcessor chunkProcessor;

	public SpriteShapeLoader(int a, ChunkProcessor b) {
		chunkSize = a;
		chunkProcessor = b;
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
		BlockType process(Pixmap p, int x, int y, int s);

		class PercentProcessor implements ChunkProcessor {
			/** [0-1] */
			public float percent;
			public int anchorChunkX;
			public int anchorChunkY;

			public PercentProcessor(float a, int x, int y) {
				percent = a;
				anchorChunkX = x;
				anchorChunkY = y;
			}

			@Override
			public BlockType process(Pixmap p, int x, int y, int s) {
				if (x == anchorChunkX && y == anchorChunkY) return BlockType.anchorBlock;
				int total = s * s;
				int worldX = x * s;
				int worldY = y * s;
				float counter = 0;
				for (int dx = 0; dx < s; dx++) {
					for (int dy = 0; dy < s; dy++) {
						counter += p.getA(worldX + dx, worldY + dy) / 255f;
					}
				}
				return counter / total > percent ? BlockType.block : BlockType.voidBlock;
			}
		}
	}
}
