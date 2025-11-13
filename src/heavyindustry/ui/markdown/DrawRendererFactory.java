package heavyindustry.ui.markdown;

import org.commonmark.renderer.NodeRenderer;

public interface DrawRendererFactory {
	NodeRenderer create(DrawRendererContext context);
}
