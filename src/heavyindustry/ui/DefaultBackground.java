package heavyindustry.ui;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import heavyindustry.util.Lazy;

public final class DefaultBackground {
	static final Lazy<Drawable> white = Lazy.of(() -> {
		Pixmap pixmap = new Pixmap(1, 1);
		pixmap.set(0, 0, Color.whiteRgba);

		return new TextureRegionDrawable(Core.atlas.white());
	});
	static final Lazy<Drawable> black6 = Lazy.of(() -> new TextureRegionDrawable(Core.atlas.white()).tint(0, 0, 0, 0.6f));

	/// Don't let anyone instantiate this class.
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
