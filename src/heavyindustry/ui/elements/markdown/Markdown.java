package heavyindustry.ui.elements.markdown;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.struct.*;
import arc.util.pooling.*;
import heavyindustry.ui.elements.markdown.extensions.*;
import heavyindustry.ui.elements.markdown.highlighter.*;
import org.commonmark.*;
import org.commonmark.node.*;
import org.commonmark.parser.*;

import java.util.*;

/** Markdown document rendering elements. */
public class Markdown extends Group {
    public static final String[] IMAGE_BASE_64_List = {
            "data:image/png;base64,",
            "data:image/jpg;base64,",
            "data:image/jpeg;base64,"
    };

    private static final List<Extension> defaultExtensions = new ArrayList<>(Arrays.asList(
            TablesExtension.create(),
            InsExtension.create(),
            StrikethroughExtension.create(),
            CurtainExtension.create()
    ));

    protected final List<Extension> extensions;
    protected final Parser parser;
    protected final MDLayoutRenderer renderer;
    protected final DrawRendererContext rendererContext;

    private final Seq<DrawObj> drawObjs = new Seq<>();

    float prefWidth, prefHeight;
    boolean prefInvalid = true;
    boolean contentWrap = true;
    float lastPrefHeight;

    private Node node;
    private MarkdownStyle style;

    @Deprecated
    public Markdown(String md, Font mono) {
        this(md, MarkdownStyles.defaultMD(mono));
    }

    public Markdown(String md, MarkdownStyle style) {
        this(Collections.emptyList(), md, style);
    }

    public Markdown(List<Extension> ext, String md, MarkdownStyle sty) {
        checkExtensions(ext);

        extensions = new ArrayList<>(ext);
        parser = Parser.builder().extensions(ext).extensions(defaultExtensions).build();
        renderer = MDLayoutRenderer.builder().extensions(ext).extensions(defaultExtensions).build();
        rendererContext = renderer.createContext(this);

        node = parser.parse(md);
        touchable = Touchable.childrenOnly;

        style = sty;
    }

    /**
     * internal usage
     *
     * @hidden
     */
    public Markdown(Markdown parent, Node nod) {
        extensions = new ArrayList<>(parent.extensions);
        parser = null;
        renderer = MDLayoutRenderer.builder().extensions(parent.extensions).extensions(defaultExtensions).build();
        rendererContext = renderer.createContext(this);

        node = nod;
        touchable = Touchable.childrenOnly;

        style = parent.getStyle();
    }

    public static void defaultExtensions(Extension... extensions) {
        checkExtensions(Arrays.asList(extensions));

        Markdown.defaultExtensions.addAll(Arrays.asList(extensions));
    }

    private static void checkExtensions(List<Extension> extensions) {
        for (Extension extension : extensions) {
            if (!(extension instanceof MDLayoutRenderer.DrawRendererExtension)
                    || !(extension instanceof Parser.ParserExtension))
                throw new IllegalArgumentException("extension must be a DrawRendererExtension and a ParserExtension");
        }
    }

    public void setDocument(String string) {
        node = parser.parse(string);
        invalidate();
    }

    public MarkdownStyle getStyle() {
        return style;
    }

    public void setStyle(MarkdownStyle sty) {
        style = sty;
        invalidate();
    }

    public void setContentWrap(boolean wrap) {
        contentWrap = wrap;
    }

    private void calculatePrefSize(boolean layoutStep) {
        rendererContext.prefSizeCalculating = !layoutStep;

        prefHeight = prefWidth = 0;

        for (DrawObj obj : drawObjs) {
            obj.free();
        }
        drawObjs.clear();

        renderer.renderLayout(node);

        prefWidth = Math.max(prefWidth, rendererContext.rendOff);
        prefHeight += rendererContext.totalHeight;

        prefInvalid = false;

        if (prefHeight != lastPrefHeight) {
            lastPrefHeight = prefHeight;
            invalidateHierarchy();
        }

        rendererContext.prefSizeCalculating = false;
    }

    @Override
    public void layout() {
        calculatePrefSize(true);

        drawObjs.addAll(rendererContext.renderResult());
        drawObjs.sort((a, b) -> a.priority() - b.priority());

        clearChildren();
        for (DrawObj obj : drawObjs) {
            if (obj instanceof ActivityDrawer activity) {
                Element element = activity.getElem();
                addChild(element);
                element.setBounds(
                        obj.offsetX,
                        height + obj.offsetY - activity.height(),
                        activity.width(),
                        activity.height()
                );
                element.validate();
            }
        }
    }

    @Override
    public float getPrefWidth() {
        if (prefInvalid) calculatePrefSize(false);
        return prefWidth;
    }

    @Override
    public float getPrefHeight() {
        if (prefInvalid) calculatePrefSize(false);
        return prefHeight;
    }

    @Override
    protected void drawChildren() {
        for (DrawObj obj : drawObjs) {
            if (obj instanceof ActivityDrawer act && cullingArea != null
                    && !cullingArea.overlaps(obj.offsetX, height + obj.offsetY, act.width(), act.height())) continue;

            Draw.reset();
            Draw.alpha(parentAlpha);
            obj.draw();
        }
        super.drawChildren();
    }

    public interface ActivityDrawer {
        Element getElem();

        float width();

        float height();
    }

    public static class MarkdownStyle {
        public Font font, codeFont, emFont, strongFont, subFont;
        public Color textColor, emColor, subTextColor, lineColor, linkColor;
        public float linesPadding, tablePadHor, tablePadVert, paragraphPadding;
        public Drawable board, codeBack, codeBlockBack, tableBack1, tableBack2, curtain;
        public ScrollPane.ScrollPaneStyle codeBlockStyle;
        public Drawable[] listMarks;
        public Highlighter highlighter;
        public ColorProvider codeColorProvider;
    }

    public abstract static class DrawObj implements Pool.Poolable {
        protected static final Color tmp1 = new Color(), tmp2 = new Color();

        protected Markdown parent;
        protected float offsetX, offsetY;

        protected abstract void draw();

        public int priority() {
            return 0;
        }

        @Override
        public void reset() {
            offsetX = offsetY = 0;
        }

        void free() {
            Pools.free(this);
        }
    }
}
