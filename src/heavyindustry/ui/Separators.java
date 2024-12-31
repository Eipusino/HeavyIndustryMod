package heavyindustry.ui;

import arc.graphics.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import heavyindustry.ui.defaults.*;

public final class Separators {
    public static Drawable separatorDrawable;

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
