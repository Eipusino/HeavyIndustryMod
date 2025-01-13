package heavyindustry.ui;

import arc.graphics.*;
import arc.scene.ui.TextField.*;
import mindustry.gen.*;

public class HIStyles {
    public static TextFieldStyle scriptArea;

    public static void load() {
        scriptArea = new TextFieldStyle() {{
            font = HIFonts.inconsoiata;
            fontColor = Color.white;
            selection = Tex.selection;
            cursor = Tex.cursor;
        }};
    }
}
