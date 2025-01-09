package heavyindustry.ui.elements.markdown.highlighter;

import heavyindustry.ui.elements.markdown.highlighter.TokenMatcher.*;

import java.util.regex.*;

public class RegexCapture extends Capture {
    private final Pattern pattern;
    private final Scopec scope;

    private final int minMatch;
    private final int maxMatch;

    public RegexCapture(Pattern pattern) {
        this(1, null, pattern);
    }

    public RegexCapture(int matches, Pattern pattern) {
        this(matches, null, pattern);
    }

    public RegexCapture(Scopec scope, Pattern pattern) {
        this(1, scope, pattern);
    }

    public RegexCapture(int matches, Scopec scope, Pattern pattern) {
        this(matches, matches, scope, pattern);
    }

    public RegexCapture(int minMatch, int maxMatch, Scopec scope, Pattern pattern) {
        this.scope = scope;
        this.pattern = pattern;
        this.minMatch = minMatch;
        this.maxMatch = maxMatch;
    }

    @Override
    public int match(MatcherContext context, Tokenf token) throws MatchFailed {
        int off = 0;

        int max = Math.min(maxMatch, context.getTokensCountInContext());
        while (off < max) {
            Tokenf curr = context.getTokenInContext(token.getIndexInContext(context) + off);
            if (!pattern.matcher(curr.text).matches()) {
                if (off < minMatch) throw MatchFailed.INSTANCE;
                else break;
            }

            off++;
        }

        if (off < minMatch) throw MatchFailed.INSTANCE;

        return off;
    }

    @Override
    public void applyScope(MatcherContext context, Tokenf token, int matchedLen) {
        if (scope == null) return;

        for (int i = 0; i < matchedLen; i++) {
            context.getTokenInContext(token.getIndexInContext(context) + i).scope = scope;
        }
    }

    @Override
    public RegexCapture create() {
        RegexCapture capture = new RegexCapture(minMatch, maxMatch, scope, pattern);
        capture.setMatchOnly(matchOnly).setOptional(optional);
        return capture;
    }
}
