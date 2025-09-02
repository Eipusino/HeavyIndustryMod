package heavyindustry.ui;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.geom.Point2;
import arc.scene.Element;
import arc.struct.IntMap;
import mindustry.ctype.UnlockableContent;
import mindustry.graphics.Pal;

public class CraftGridImage extends Element {
	public IntMap<UnlockableContent> items = new IntMap<>();
	public float scl = 80f, imgScl = 60f;

	public float itemsWidth() {
		int max = 0;
		for (var entry : items.entries()) {
			Point2 pos = Point2.unpack(entry.key);
			max = Math.max(max, pos.x + 1);
		}
		return max * scl;
	}

	public float itemsHeight() {
		int max = 0;
		for (var entry : items.entries()) {
			Point2 pos = Point2.unpack(entry.key);
			max = Math.max(max, pos.y + 1);
		}
		return max * scl;
	}

	@Override
	public void draw() {
		int linesX = (int) (itemsWidth() / scl) + 1, linesY = (int) (itemsHeight() / scl) + 1;

		Draw.color(Pal.gray, parentAlpha);
		Lines.stroke(6f);
		for (int lx = 0; lx < linesX; lx++) {
			Lines.line(x + lx * scl, y, x + lx * scl, y + height, true);
		}
		for (int ly = 0; ly < linesY; ly++) {
			Lines.line(x, y + ly * scl, x + width, y + ly * scl, true);
		}

		Draw.color(Color.white, parentAlpha);
		for (var entry : items.entries()) {
			Point2 pos = Point2.unpack(entry.key);
			Draw.rect(
					entry.value.uiIcon,
					x + (pos.x * scl) + (scl / 2f),
					y + (pos.y * scl) + (scl / 2f),
					imgScl,
					imgScl
			);
		}
	}
}
