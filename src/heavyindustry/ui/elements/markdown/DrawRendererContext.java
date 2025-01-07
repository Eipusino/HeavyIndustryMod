package heavyindustry.ui.elements.markdown;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.*;
import arc.scene.actions.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.struct.*;
import heavyindustry.ui.elements.markdown.Markdown.*;
import heavyindustry.ui.elements.markdown.elemdraw.*;
import mindustry.ui.*;
import org.commonmark.node.Image;
import org.commonmark.node.*;

public abstract class DrawRendererContext {
    public final Markdown element;

    private final Seq<DrawObj> drawObjs = new Seq<>();
    private final ObjectMap<Node, TextureRegion> imgCache = new ObjectMap<>();

    public boolean prefSizeCalculating;

    /** The panel stacking counter should be used to record the sequence when drawing objects with background panels, and its numbering should be correctly assigned. */
    public int boardLayers;
    /** List stacking counter, used to record the number of nesting times when drawing indented lists */
    public int listLayer;

    /** The left margin value of the current drawn object (measured in the width of a space character), usually used in nested blocks, is used to calculate the left offset of the rendered object when a line break occurs. */
    public float padding;
    /** The scaling tool currently used for drawing objects (if available) */
    public float currScl;

    /** The horizontal coordinate offset ruler of the current drawn object is usually calculated by the line break action */
    public float rendOff;
    /** The vertical coordinate offset ruler (from top to bottom) of the current drawn object is usually calculated by the line break action */
    public float lineOff;
    /** The total height of the current document is usually calculated by the line break action */
    public float totalHeight;

    /** Last drawn text information mapping */
    public TextMirror lastText;
    /** The font used for drawing the current text is usually used to define the font for nested block content */
    public Font currFont;
    /** The color used when drawing the current text is usually used to define the content color of nested blocks */
    public Color currFontColor;

    protected DrawRendererContext(Markdown element) {
        this.element = element;
    }

    /**
     * Render a Markdown syntax node
     *
     * @param node Node to be rendered
     */
    public abstract void render(Node node);

    /** Obtain iterable objects for rendering results */
    public Iterable<DrawObj> renderResult() {
        return drawObjs;
    }

    /** Add a drawing object */
    public void draw(DrawObj obj) {
        drawObjs.add(obj);
    }

    /** Retrieve image cache, if the image does not exist, call {@code o} to generate it */
    public TextureRegion imageCache(Image image, Prov<TextureRegion> o) {
        return imgCache.get(image, o);
    }

    /** 初始化渲染上下文 */
    public void init() {
        boardLayers = 0;
        listLayer = 0;
        padding = 0;
        rendOff = 0;
        lineOff = 0;
        currScl = 1;
        totalHeight = 0;
        lastText = null;
        currFont = element.getStyle().font;
        currFontColor = element.getStyle().textColor;
        drawObjs.clear();
    }

    /** Clear image cache */
    public void clearCache() {
        init();
        imgCache.clear();
    }

    /**
     * Update the height of the document display area (will not decrease)
     *
     * @param h Display the height of the position
     */
    public void updateHeight(float h) {
        totalHeight = Math.max(totalHeight, h);
    }

    /** @see DrawRendererContext#makeStr(String, Font, String, Drawable, Color) */
    public TextMirror makeStr(String str, Font font, Color color) {
        return makeStr(str, font, null, null, color);
    }

    /** @see DrawRendererContext#makeStr(String, Font, String, Drawable, Color) */
    public TextMirror makeStr(String str, Font font, Color color, String openUrl) {
        return makeStr(str, font, openUrl, null, color);
    }

    /** @see DrawRendererContext#makeStr(String, Font, String, Drawable, Color) */
    public TextMirror makeStr(String str, Font font, Drawable background, Color color) {
        return makeStr(str, font, null, background, color);
    }

    /**
     * Add a text display and return its {@linkplain TextMirror boundary information mapping object}
     *
     * @param str        Display text content
     * @param font       Text font
     * @param openUrl    Text hyperlink, null does not define hyperlink
     * @param background Text background, if null, transparent
     * @param color      text color
     */
    public TextMirror makeStr(String str, Font font, String openUrl, Drawable background, Color color) {
        float maxWidth = prefSizeCalculating || !element.contentWrap || element.getWidth() <= font.getSpaceXadvance() * 3 ? Float.MAX_VALUE : element.getWidth() - rendOff;
        float tmp = 0;
        int index = 0;

        float width = 0;
        updateHeight(font.getLineHeight() * currScl);
        for (int c : str.chars().toArray()) {
            Font.Glyph glyph = font.getData().getGlyph((char) c);
            if (glyph == null) {
                index++;
                continue;
            }

            tmp += glyph.xadvance * currScl * font.getScaleX();
            if (tmp > maxWidth) {
                break;
            }
            width = tmp;
            index++;
        }

        TextMirror res = lastText;
        if (width > 0) {
            rendOff += background != null ? background.getLeftWidth() : 0;
            if (res == null)
                res = new TextMirror(str.substring(0, index), font, color, rendOff, -lineOff, width, totalHeight);
            else res.sub = new TextMirror(str.substring(0, index), font, color, rendOff, -lineOff, width, totalHeight);

            draw(openUrl != null ?
                    DrawClickable.get(element, str.substring(0, index), font,
                            () -> Core.app.openURI(openUrl), new Tooltip(t -> t.table(element.getStyle().board).get().add(openUrl)),
                            color, rendOff, -lineOff, currScl
                    ) :
                    DrawStr.get(element, str.substring(0, index), font, color, rendOff, -lineOff, currScl, background));
            rendOff += width + (background != null ? background.getRightWidth() : 0) + font.getSpaceXadvance() * font.getScaleX();
        } else if (res == null) res = new TextMirror("", font, color, rendOff, -lineOff, 0, totalHeight);
        else res.sub = new TextMirror("", font, color, rendOff, -lineOff, 0, totalHeight);

        lastText = res;

        if (index < str.length()) {
            row();
            res.sub = makeStr(str.substring(index), font, openUrl, background, color);
        }

        return res;
    }

    /**
     * Add a code block display area
     *
     * @param lang Language tags used in the code
     * @param code Code Content
     */
    public void makeCodeBox(String lang, String code) {
        MarkdownStyle style = element.getStyle();

        padding += 4;
        lineOff += style.linesPadding;
        row();
        float begin = lineOff;
        lineOff += style.linesPadding * 2;
        DrawCode pane = DrawCode.get(element, lang, code, style.codeFont, rendOff, -lineOff, style.codeBlockStyle);
        updateHeight(pane.height() + style.linesPadding);
        draw(pane);
        padding -= 4;
        row();
        lineOff += style.linesPadding * 2;
        draw(DrawBoard.get(element, style.codeBlockBack, boardLayers, lineOff - begin, rendOff, -begin));

        DrawClickable c = DrawClickable.get(element, Core.bundle.get("editor.copy"), Fonts.outline,
                () -> Core.app.setClipboardText(code),
                null, style.subTextColor, pane.width() - 64, -begin - style.linesPadding * 2 - 8, 1
        );
        draw(c);
        Element e = c.getElem();
        e.color.a = 0.4f;
        e.hovered(() -> e.actions(Actions.alpha(1, 0.5f)));
        e.exited(() -> e.actions(Actions.alpha(0.4f, 0.5f)));
    }

    /** Perform line break, which will update the height of the document area and reset the line offset ruler */
    public void row() {
        MarkdownStyle style = element.getStyle();
        element.prefWidth = Math.max(element.prefWidth, rendOff);

        rendOff = padding * style.font.getSpaceXadvance();
        lineOff += totalHeight + style.linesPadding * currScl;

        element.prefHeight = lineOff;
        totalHeight = 0;
    }
}
