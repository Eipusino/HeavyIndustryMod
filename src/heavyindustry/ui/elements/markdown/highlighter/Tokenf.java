package heavyindustry.ui.elements.markdown.highlighter;

public class Tokenf {
    public final String text;

    public Scopec scope;
    public Object data;

    public int index;
    public int rawIndex;

    public Tokenf(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "{\"" + text + "\": " + scope + "}";
    }

    public int getIndexInContext(TokensContext context) {
        return context.inRawContext ? rawIndex : index;
    }
}
