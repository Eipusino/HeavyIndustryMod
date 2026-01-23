package endfield.ui;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import endfield.util.InLazy;

public final class DefaultBackground {
	static final InLazy<Drawable> white = new InLazy<>(() -> {
		Pixmap pixmap = new Pixmap(1, 1);
		pixmap.set(0, 0, Color.whiteRgba);

		return new TextureRegionDrawable(Core.atlas.white());
	});
	static final InLazy<Drawable> black6 = new InLazy<>(() -> new TextureRegionDrawable(Core.atlas.white()).tint(0, 0, 0, 0.6f));

	/** Don't let anyone instantiate this class. */
	private DefaultBackground() {}

	public static Drawable white() {
		return white.get();
	}

	public static Drawable black6() {
		return black6.get();
	}

	public static void white(Drawable value) {
		white.set(value);
	}

	public static void black6(Drawable value) {
		black6.set(value);
	}
}
