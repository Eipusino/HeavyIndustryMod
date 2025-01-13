package heavyindustry.ui.elements.markdown.highlighter;

import heavyindustry.ui.elements.markdown.highlighter.TokenMatcher.*;

import java.util.*;

public class MatcherReference implements TokenMatcher, MatcherGroup {
    private final int priority;
    private final NameIndexer<TokenMatcher> nameIndexer;
    private final String[] patternNames;

    public MatcherReference(NameIndexer<TokenMatcher> map, String... names) {
        priority = 0;
        nameIndexer = map;
        patternNames = names;
    }

    public MatcherReference(NameIndexer<TokenMatcher> map) {
        priority = 0;
        nameIndexer = map;
        patternNames = null;
    }

    public MatcherReference(int pri, NameIndexer<TokenMatcher> map, String... names) {
        priority = pri;
        nameIndexer = map;
        patternNames = names;
    }

    public MatcherReference(int pri, NameIndexer<TokenMatcher> map) {
        priority = pri;
        nameIndexer = map;
        patternNames = null;
    }

    @Override
    public List<TokenMatcher> asMatchers() {
        return patternNames == null ? nameIndexer.allIndexed() : nameIndexer.indexes(patternNames);
    }

    @Override
    public int match(MatcherContext context, Tokenf token) throws MatchFailed {
        throw MatchFailed.INSTANCE;
    }

    @Override
    public void apply(MatcherContext context, Tokenf token) {
    }

    @Override
    public TokenMatcher create() {
        return new MatcherReference(priority, nameIndexer, patternNames);
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
