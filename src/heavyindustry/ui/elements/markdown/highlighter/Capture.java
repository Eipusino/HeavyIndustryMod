package heavyindustry.ui.elements.markdown.highlighter;

public abstract class Capture {
    public boolean matchOnly = false;
    public boolean optional = false;

    public Capture setMatchOnly(boolean mat) {
        matchOnly = mat;
        return this;
    }

    public Capture setOptional(boolean opt) {
        optional = opt;
        return this;
    }

    public abstract int match(MatcherContext context, Tokenf token) throws TokenMatcher.MatchFailed;

    public abstract void applyScope(MatcherContext context, Tokenf token, int matchedLen);

    public abstract Capture create();
}
