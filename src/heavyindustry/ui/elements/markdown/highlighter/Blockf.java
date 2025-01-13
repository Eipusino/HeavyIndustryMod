package heavyindustry.ui.elements.markdown.highlighter;

import java.util.*;

public class Blockf {
    private final Scopec scope;
    private final TokenMatcher ownerMatcher;
    private final List<TokenMatcher> matchers;

    public Blockf(Scopec sco, List<TokenMatcher> mat) {
        scope = sco;
        ownerMatcher = null;
        matchers = mat;
    }

    public Blockf(Scopec sco, TokenMatcher cur, List<TokenMatcher> mat) {
        scope = sco;
        ownerMatcher = cur;
        matchers = mat;
    }

    public Scopec scope() {
        return scope;
    }

    public List<TokenMatcher> matchers() {
        List<TokenMatcher> list = new ArrayList<>();
        for (TokenMatcher matcher : matchers) {
            if (matcher instanceof TokenMatcher.MatcherGroup ref) {
                list.addAll(ref.asMatchers());
            } else list.add(matcher);
        }
        return list;
    }

    public TokenMatcher ownerMatcher() {
        return ownerMatcher;
    }
}
