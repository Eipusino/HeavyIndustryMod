package heavyindustry.ui.elements.markdown.extensions;

import heavyindustry.ui.elements.markdown.*;
import heavyindustry.ui.elements.markdown.elemdraw.*;
import org.commonmark.*;
import org.commonmark.ext.gfm.strikethrough.*;
import org.commonmark.ext.gfm.strikethrough.internal.*;
import org.commonmark.node.*;
import org.commonmark.parser.*;

import java.util.*;

public class StrikethroughExtension implements Parser.ParserExtension, MDLayoutRenderer.DrawRendererExtension {
    private StrikethroughExtension() {}

    public static Extension create() {
        return new StrikethroughExtension();
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.customDelimiterProcessor(new StrikethroughDelimiterProcessor());
    }

    @Override
    public void extend(MDLayoutRenderer.Builder rendererBuilder) {
        rendererBuilder.nodeRendererFactory(LayoutStrikethroughRenderer::new);
    }

    public static class LayoutStrikethroughRenderer extends LayoutNodeRenderer {
        private LayoutStrikethroughRenderer(DrawRendererContext context) {
            super(context);
        }

        public static Extension create() {
            return new StrikethroughExtension();
        }

        @Override
        public Set<Class<? extends Node>> getNodeTypes() {
            return Collections.singleton(Strikethrough.class);
        }

        @Override
        public void render(Node node) {
            context.lastText = null;
            visitChildren(node);
            TextMirror orig = context.lastText;
            while (orig != null) {
                context.draw(DrawLine.get(
                        context.element,
                        context.lastText.fontColor,
                        context.lastText.offx,
                        context.lastText.offy - context.lastText.height / 2,
                        context.lastText.width
                ));

                orig = orig.sub;
            }
        }
    }
}
