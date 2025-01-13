package heavyindustry.ui.elements.markdown.highlighter;

import java.util.*;

public class SelectionCapture extends Capture {
    protected final int minMatch;
    protected final int maxMatch;
    protected final List<Capture> captures;

    protected final List<Capture> hits = new ArrayList<>();
    protected final List<Integer> hitLens = new ArrayList<>();

    public SelectionCapture(Capture... captures) {
        this(1, captures);
    }

    public SelectionCapture(int matches, Capture... captures) {
        this(matches, matches, captures);
    }

    public SelectionCapture(int min, int max, Capture... cap) {
        minMatch = min;
        maxMatch = max;
        captures = Arrays.asList(cap);
    }

    @Override
    public int match(MatcherContext context, Tokenf token) throws TokenMatcher.MatchFailed {
        hitLens.clear();
        hits.clear();

        int off = 0;
        int max = context.getTokensCountInContext();
        for (int i = 0; i < maxMatch; i++) {
            if (token.getIndexInContext(context) + off >= max) {
                if (i < minMatch) throw TokenMatcher.MatchFailed.INSTANCE;
                else break;
            }

            Capture hit = null;
            for (Capture capture : captures) {
                Capture capt = capture.create();
                Tokenf curr = context.getTokenInContext(token.getIndexInContext(context) + off);

                try {
                    int len = capt.match(context, curr);

                    hits.add(capt);
                    hitLens.add(len);
                    off += len;
                    hit = capt;

                    break;
                } catch (TokenMatcher.MatchFailed ignored) {}
            }

            if (hit == null) {
                if (i < minMatch) throw TokenMatcher.MatchFailed.INSTANCE;
                else break;
            }
        }

        return off;
    }

    @Override
    public void applyScope(MatcherContext context, Tokenf token, int matchedLen) {
        int off = 0;
        new Object();
        for (int i = 0; i < hits.size(); i++) {
            Tokenf curr = context.getTokenInContext(token.getIndexInContext(context) + off);
            hits.get(i).applyScope(context, curr, hitLens.get(i));
            off += hitLens.get(i);
        }
    }

    @Override
    public SelectionCapture create() {
        Capture[] cap = new Capture[captures.size()];
        for (int i = 0; i < cap.length; i++) cap[i] = captures.get(i).create();
        SelectionCapture capture = new SelectionCapture(minMatch, maxMatch, cap);
        capture.setMatchOnly(matchOnly).setOptional(optional);
        return capture;
    }
}
