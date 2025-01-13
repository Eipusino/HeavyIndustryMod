package heavyindustry.ui.elements.markdown.highlighter;

import java.util.regex.*;

public class LinesCapture extends Capture {
    private final Pattern lineSep;
    private final Scopec scope;

    public LinesCapture() {
        scope = null;
        lineSep = Pattern.compile("\\r\\n|\\r|\\n");
    }

    public LinesCapture(Scopec sco) {
        scope = sco;
        lineSep = Pattern.compile("\\r\\n|\\r|\\n");
    }

    public LinesCapture(Scopec sco, Pattern line) {
        lineSep = line;
        scope = sco;
    }

    @Override
    public int match(MatcherContext context, Tokenf token) throws TokenMatcher.MatchFailed {
        if (token.rawIndex >= context.getTokensRaw().size() - 1)
            return 1;

        if (!lineSep.matcher(context.getTokensRaw().get(token.rawIndex + (context.inRawContext ? 0 : 1)).text).find())
            throw TokenMatcher.MatchFailed.INSTANCE;

        return 1;
    }

    @Override
    public void applyScope(MatcherContext context, Tokenf token, int matchedLen) {
        if (scope != null) token.scope = scope;
    }

    @Override
    public LinesCapture create() {
        LinesCapture capture = new LinesCapture(scope);
        capture.setMatchOnly(matchOnly).setOptional(optional);
        return capture;
    }
}
