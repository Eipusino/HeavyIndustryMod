package heavyindustry.ui;

import arc.graphics.*;
import arc.scene.ui.TextField.*;
import mindustry.gen.*;

public final class HIStyles {
    public static TextFieldStyle scriptArea;

    /** Don't let anyone instantiate this class. */
    private HIStyles() {}

    public static void load() {
        scriptArea = new TextFieldStyle() {{
            font = HIFonts.inconsoiata;
            fontColor = Color.white;
            selection = Tex.selection;
            cursor = Tex.cursor;
        }};
    }
}
