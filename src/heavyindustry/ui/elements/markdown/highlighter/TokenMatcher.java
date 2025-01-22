package heavyindustry.ui.elements.markdown.highlighter;

import heavyindustry.util.*;

import java.util.*;

public interface TokenMatcher extends Comparable<TokenMatcher> {
    int match(MatcherContext context, Tokenf token) throws MatchFailed;

    void apply(MatcherContext context, Tokenf token);

    TokenMatcher create();

    int getPriority();

    default int compareTo(TokenMatcher o) {
        return Integer.compare(o.getPriority(), getPriority());
    }

    interface MatcherGroup {
        List<TokenMatcher> asMatchers();
    }

    class MatchFailed extends Failed {
        @java.io.Serial
        private static final long serialVersionUID = -1567788981146808003l;

        public static final MatchFailed INSTANCE = new MatchFailed();

        private MatchFailed() {}
    }
}
