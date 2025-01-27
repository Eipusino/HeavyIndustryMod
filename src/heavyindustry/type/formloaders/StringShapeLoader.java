package heavyindustry.type.formloaders;

import heavyindustry.struct.*;
import heavyindustry.struct.BitWordList.*;
import heavyindustry.type.CustomShape.*;
import heavyindustry.type.*;

public class StringShapeLoader extends CustomShapeLoader<String[]> {
	public final char voidChar, blockChar, anchorChar;

	public StringShapeLoader(char a, char b, char c) {
		voidChar = a;
		blockChar = b;
		anchorChar = c;
	}

	@Override
	public void load(String... lines) {
		blocks = new BitWordList(lines.length * lines[0].length(), WordLength.two);
		int i = 0;
		width = lines[0].length();
		height = lines.length;
		for (String line : lines) {
			for (char c : line.toCharArray()) {
				BlockType blockType;
				if (c == voidChar) {
					blockType = BlockType.voidBlock;
				} else if (c == blockChar) {
					blockType = BlockType.block;
				} else if (c == anchorChar) {
					blockType = BlockType.anchorBlock;
				} else {
					throw new IllegalArgumentException("Illegal character \"" + c + "\"");
				}
				blocks.set(i, (byte) blockType.ordinal());
				i++;
			}
		}
	}
}
