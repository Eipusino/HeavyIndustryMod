package heavyindustry.ui.elements.markdown;

import arc.graphics.*;
import arc.graphics.g2d.*;

public class TextMirror {
    public final String text;
    public final Font font;
    public final Color fontColor;
    public final float offx, offy;
    public final float width, height;

    public TextMirror sub;

    TextMirror(String text, Font font, Color fontColor, float offx, float offy, float width, float height) {
        this.text = text;
        this.font = font;
        this.fontColor = fontColor;
        this.offx = offx;
        this.offy = offy;
        this.width = width;
        this.height = height;
    }
}
