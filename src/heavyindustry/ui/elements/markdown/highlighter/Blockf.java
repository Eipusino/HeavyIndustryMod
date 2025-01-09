package heavyindustry.ui.elements.markdown.highlighter;

import java.util.*;

public class Blockf {
    private final Scopec scope;
    private final TokenMatcher ownerMatcher;
    private final List<TokenMatcher> matchers;

    public Blockf(Scopec scope, List<TokenMatcher> matchers) {
        this.scope = scope;
        this.ownerMatcher = null;
        this.matchers = matchers;
    }

    public Blockf(Scopec scope, TokenMatcher currMatcher, List<TokenMatcher> matchers) {
        this.scope = scope;
        this.ownerMatcher = currMatcher;
        this.matchers = matchers;
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
