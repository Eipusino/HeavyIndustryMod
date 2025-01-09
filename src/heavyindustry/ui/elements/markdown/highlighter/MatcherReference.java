package heavyindustry.ui.elements.markdown.highlighter;

import heavyindustry.ui.elements.markdown.highlighter.TokenMatcher.*;

import java.util.*;

public class MatcherReference implements TokenMatcher, MatcherGroup {
    private final int priority;
    private final NameIndexer<TokenMatcher> nameIndexer;
    private final String[] patternNames;

    public MatcherReference(NameIndexer<TokenMatcher> map, String... patternNames) {
        this.priority = 0;
        this.nameIndexer = map;
        this.patternNames = patternNames;
    }

    public MatcherReference(NameIndexer<TokenMatcher> map) {
        this.priority = 0;
        this.nameIndexer = map;
        this.patternNames = null;
    }

    public MatcherReference(int priority, NameIndexer<TokenMatcher> map, String... patternNames) {
        this.priority = priority;
        this.nameIndexer = map;
        this.patternNames = patternNames;
    }

    public MatcherReference(int priority, NameIndexer<TokenMatcher> map) {
        this.priority = priority;
        this.nameIndexer = map;
        this.patternNames = null;
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
