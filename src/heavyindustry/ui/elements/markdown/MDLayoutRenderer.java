package heavyindustry.ui.elements.markdown;

import org.commonmark.*;
import org.commonmark.internal.renderer.*;
import org.commonmark.node.*;
import org.commonmark.renderer.*;

import java.util.*;

public class MDLayoutRenderer {
    private final List<DrawRendererFactory> nodeRendererFactories;
    private RendererContext context;

    private MDLayoutRenderer(Builder builder) {
        nodeRendererFactories = new ArrayList<>(builder.nodeRendererFactories.size() + 1);
        nodeRendererFactories.addAll(builder.nodeRendererFactories);

        nodeRendererFactories.add(BaseDrawRenderer::new);
    }

    public static Builder builder() {
        return new Builder();
    }

    public RendererContext createContext(Markdown element) {
        context = new RendererContext(element);
        return context;
    }

    public void renderLayout(Node node) {
        if (context == null) throw new IllegalStateException("context must be created first");
        context.init();
        context.render(node);
    }

    public interface DrawRendererExtension extends Extension {
        void extend(MDLayoutRenderer.Builder rendererBuilder);
    }

    public static class Builder {
        private final List<DrawRendererFactory> nodeRendererFactories = new ArrayList<>();

        public MDLayoutRenderer build() {
            return new MDLayoutRenderer(this);
        }

        public Builder nodeRendererFactory(DrawRendererFactory nodeRendererFactory) {
            Objects.requireNonNull(nodeRendererFactory, "nodeRendererFactory must not be null");

            nodeRendererFactories.add(nodeRendererFactory);
            return this;
        }

        public Builder extensions(Iterable<? extends Extension> extensions) {
            Objects.requireNonNull(extensions, "extensions must not be null");

            for (Extension extension : extensions) {
                if (extension instanceof DrawRendererExtension ext) {
                    ext.extend(this);
                }
            }
            return this;
        }
    }

    public class RendererContext extends DrawRendererContext {
        protected final NodeRendererMap nodeRendererMap = new NodeRendererMap();

        private RendererContext(Markdown element) {
            super(element);

            for (int i = nodeRendererFactories.size() - 1; i >= 0; i--) {
                DrawRendererFactory nodeRendererFactory = nodeRendererFactories.get(i);
                NodeRenderer nodeRenderer = nodeRendererFactory.create(this);
                nodeRendererMap.add(nodeRenderer);
            }
        }

        @Override
        public void render(Node node) {
            nodeRendererMap.render(node);
        }
    }
}
