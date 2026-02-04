package endfield.ui;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;

public final class DefaultBackground {
	static Drawable white;
	static Drawable black6;

	/** Don't let anyone instantiate this class. */
	private DefaultBackground() {}

	public static Drawable white() {
		if (white == null) {
			Pixmap pixmap = new Pixmap(1, 1);
			pixmap.set(0, 0, Color.whiteRgba);

			white = new TextureRegionDrawable(Core.atlas.white());
		}
		return white;
	}

	public static Drawable black6() {
		if (black6 == null) {
			black6 = new TextureRegionDrawable(Core.atlas.white()).tint(0, 0, 0, 0.6f);
		}
		return black6;
	}

	public static void white(Drawable value) {
		white = value;
	}

	public static void black6(Drawable value) {
		black6 = value;
	}
}
