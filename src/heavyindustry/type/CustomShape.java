package heavyindustry.type;

import arc.func.Intc2;
import heavyindustry.struct.BitWordList;
import heavyindustry.struct.BitWordList.WordLength;

public class CustomShape {
	public final int width;
	public final int height;
	public final int nonNothingAmount;
	public final int otherBlocksAmount;

	BitWordList blocks;
	int centerX, centerY;

	public CustomShape(int wid, int hei, int[] blo) {
		this(wid, hei, mapArrayToList(wid, hei, blo));
	}

	public CustomShape(int wid, int hei, BitWordList blo) {
		blocks = blo;
		width = wid;
		height = hei;
		findCenter();
		int nonNotAmo = 0;
		int othBloAmo = 0;
		for (int i = 0; i < blo.initialWordsAmount; i++) {
			BlockType block = BlockType.all[blo.get(i)];
			if (block.isSimpleBlock()) {
				nonNotAmo++;
				othBloAmo++;
			}
			if (block.isCenterBlock()) {
				nonNotAmo++;
			}
		}
		nonNothingAmount = nonNotAmo;
		otherBlocksAmount = othBloAmo;
	}

	private static BitWordList mapArrayToList(int width, int height, int[] blocks) {
		BitWordList list = new BitWordList(width * height, WordLength.two);
		for (int i = 0; i < blocks.length && i < list.initialWordsAmount; i++) {
			list.set(i, (byte) blocks[i]);
		}
		return list;
	}

	public int anchorX() {
		return centerX;
	}

	public int anchorY() {
		return centerY;
	}

	private void findCenter() {
		int ordinal = BlockType.anchorBlock.ordinal();
		for (int i = 0; i < blocks.initialWordsAmount; i++) {
			if (blocks.get(i) == ordinal) {
				centerX = unpackX(i);
				centerY = unpackY(i);
				return;
			}
		}
		throw new RuntimeException("Cannot find center");
	}

	public int unpackX(int index) {
		return index % width;
	}

	public int unpackY(int index) {
		return index / width;
	}

	public BlockType get(int x, int y) {
		return BlockType.all[blocks.get(x + y * width)];
	}

	public int getId(int x, int y) {
		return blocks.get(x + y * width);
	}

	public void eachRelativeCenter(Intc2 consumer) {
		for (int i = 0; i < blocks.initialWordsAmount; i++) {
			consumer.get(unpackX(i) - centerX, unpackY(i) - centerY);
		}
	}

	public void eachRelativeCenter(boolean includeNothing, boolean includeOther, boolean includeCenter, Intc2 consumer) {
		for (int i = 0; i < blocks.initialWordsAmount; i++) {
			BlockType type = BlockType.all[blocks.get(i)];
			if (type.isVoid() && includeNothing || type.isSimpleBlock() && includeOther || type.isCenterBlock() && includeCenter) {
				consumer.get(unpackX(i) - centerX, unpackY(i) - centerY);
			}
		}
	}

	public BlockType getRelativeCenter(int x, int y) {
		return get(x + centerX, y + centerY);
	}

	public int getIdRelativeCenter(int x, int y) {
		return getId(x + centerX, y + centerY);
	}

	public enum BlockType {
		block(true, false),
		anchorBlock(true, true),
		voidBlock(false, false);

		public static final BlockType[] all = values();

		public final boolean solid;
		public final boolean center;

		BlockType(boolean sol, boolean cen) {
			solid = sol;
			center = cen;
		}

		public boolean isVoid() {
			return !solid;
		}

		public boolean isSimpleBlock() {
			return this == block;
		}

		public boolean isCenterBlock() {
			return this == anchorBlock;
		}

		public boolean isVoidBlock() {
			return this == voidBlock;
		}
	}
}
