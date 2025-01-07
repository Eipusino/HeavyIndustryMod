package heavyindustry.ui.elements.markdown;

import org.commonmark.renderer.*;

public interface DrawRendererFactory {
    NodeRenderer create(DrawRendererContext context);
}
