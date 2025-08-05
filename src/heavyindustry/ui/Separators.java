package heavyindustry.ui;

import arc.graphics.Color;
import arc.scene.style.Drawable;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;

public final class Separators {
	public static Drawable separatorDrawable;

	/** Don't let anyone instantiate this class. */
	private Separators() {}

	public static Cell<Image> verticalSeparator(Table table, Color color) {
		return table.image(separatorDrawable(), color).growY().width(3f);
	}

	public static Cell<Image> horizontalSeparator(Table table, Color color) {
		return table.image(separatorDrawable(), color).growX().height(3f);
	}

	private static Drawable separatorDrawable() {
		return separatorDrawable == null ? DefaultBackground.white() : separatorDrawable;
	}
}
