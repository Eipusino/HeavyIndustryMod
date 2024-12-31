package heavyindustry.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;

import java.util.*;

/** Draws text on screen without label or other stuff. */
public final class DrawText {
    public static Font defaultFont;
    private static Seq<Font> fontList;

    private DrawText() {}

    /** {@link DrawText#defaultFont} must be set. */
    public static void drawText(float x, float y, float textSize, Color color, String text) {
        drawText(defaultFont(), x, y, textSize, color, text);
    }

    private static Font defaultFont() {
        if (defaultFont == null && Core.assets != null && fontList == null) {
            fontList = new Seq<>();
            defaultFont = Core.assets.getAll(Font.class, fontList).firstOpt();
        }
        return Objects.requireNonNull(defaultFont, "heavyindustry.graphics.DrawText.defaultFont is null");
    }

    public static void drawText(Font font, float x, float y, float textSize, Color color, String text) {
        boolean ints = font.usesIntegerPositions();
        font.getData().setScale(textSize / Scl.scl(1.0f));
        font.setUseIntegerPositions(false);

        font.setColor(color);

        float z = Draw.z();
        Draw.z(z + 0.01f);
        FontCache cache = font.getCache();
        cache.clear();
        GlyphLayout layout = cache.addText(text, x, y);

        font.draw(text, x - layout.width / 2f, y + layout.height / 2f);
        Draw.z(z);

        font.setUseIntegerPositions(ints);
        font.getData().setScale(1);
    }

    /** {@link DrawText#defaultFont} must be set. */
    public static void drawText(Position pos, float textSize, String text) {
        drawText(defaultFont(), pos, textSize, text);
    }

    public static void drawText(Font font, Position pos, float textSize, String text) {
        drawText(font, pos.getX(), pos.getY(), textSize, Color.white, text);
    }

    /** {@link DrawText#defaultFont} must be set. */
    public static void drawText(Position pos, Color color, String text) {
        drawText(defaultFont(), pos, color, text);
    }

    public static void drawText(Font font, Position pos, Color color, String text) {
        drawText(font, pos.getX(), pos.getY(), 0.23f, color, text);
    }

    /** {@link DrawText#defaultFont} must be set. */
    public static void drawText(Position pos, String text) {
        drawText(defaultFont(), pos, text);
    }

    public static void drawText(Font font, Position pos, String text) {
        drawText(font, pos, Color.white, text);
    }

    /** {@link DrawText#defaultFont} must be set. */
    public static void drawText(float x, float y, float textSize, String text) {
        drawText(defaultFont(), x, y, textSize, text);
    }

    public static void drawText(Font font, float x, float y, float textSize, String text) {
        drawText(font, x, y, textSize, Color.white, text);
    }

    /** {@link DrawText#defaultFont} must be set. */
    public static void drawText(float x, float y, Color color, String text) {
        drawText(defaultFont(), x, y, color, text);
    }

    public static void drawText(Font font, float x, float y, Color color, String text) {
        drawText(font, x, y, 0.23f, color, text);
    }

    /** {@link DrawText#defaultFont} must be set. */
    public static void drawText(float x, float y, String text) {
        drawText(defaultFont(), x, y, text);
    }

    public static void drawText(Font font, float x, float y, String text) {
        drawText(font, x, y, Color.white, text);
    }
}
