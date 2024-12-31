package heavyindustry.ui.defaults;

import arc.*;
import arc.graphics.*;
import arc.scene.style.*;

public final class DefaultBackground {
    private static final Lazy<Drawable> white = new Lazy<>(() -> {
        Pixmap pixmap = new Pixmap(1, 1);
        pixmap.set(0, 0, Color.whiteRgba);
        return new TextureRegionDrawable(Core.atlas.white());
    });
    private static final Lazy<Drawable> black6 = new Lazy<>(() ->
            new TextureRegionDrawable(Core.atlas.white()).tint(0, 0, 0, 0.6f));

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
