package heavyindustry.ui.elements.markdown;

import org.commonmark.node.*;
import org.commonmark.renderer.*;

public abstract class LayoutNodeRenderer implements NodeRenderer {
    protected final DrawRendererContext context;

    public LayoutNodeRenderer(DrawRendererContext context) {
        this.context = context;
    }

    protected void visitChildren(Node node) {
        Node n = node.getFirstChild();
        while (n != null) {
            Node next = n.getNext();
            context.render(n);
            n = next;
        }
    }
}
