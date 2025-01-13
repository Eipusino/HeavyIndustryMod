package heavyindustry.ui.elements.markdown.highlighter.defaults;

import arc.func.*;
import heavyindustry.ui.elements.markdown.highlighter.*;
import org.intellij.lang.annotations.*;

import java.util.*;
import java.util.regex.Pattern;

public abstract class DefaultLangDSL {
    @SafeVarargs
    protected static <T> List<T> of(T... elements) {
        return Arrays.asList(elements);
    }

    protected static SerialMatcher serial(Capture... captures) {
        return SerialMatcher.create(captures);
    }

    protected static SerialMatcher serial(int priority, Capture... captures) {
        return SerialMatcher.create(priority, captures);
    }

    protected static BlockMatcher block(List<Capture> begin, List<Capture> end) {
        return BlockMatcher.create(begin, end);
    }

    protected static BlockMatcher block(Scopec scope, List<Capture> begin, List<Capture> end) {
        return BlockMatcher.create(scope, begin, end);
    }

    protected static BlockMatcher block(int priority, Scopec scope, List<Capture> begin, List<Capture> end) {
        return BlockMatcher.create(priority, scope, begin, end);
    }

    protected static MatcherReference reference(NameIndexer<TokenMatcher> map) {
        return new MatcherReference(map);
    }

    protected static MatcherReference reference(NameIndexer<TokenMatcher> map, String... patternNames) {
        return new MatcherReference(map, patternNames);
    }

    protected static MatcherReference reference(int priority, NameIndexer<TokenMatcher> map) {
        return new MatcherReference(priority, map);
    }

    protected static MatcherReference reference(int priority, NameIndexer<TokenMatcher> map, String... patternNames) {
        return new MatcherReference(priority, map, patternNames);
    }

    protected static Capture lazy(Prov<Capture> captureProv) {
        return new LazyCapture(captureProv);
    }

    protected static Capture lazy(Capture captureProv) {
        return new LazyCapture(captureProv::create);
    }

    protected static Capture makeJoin(Capture capture, Capture join) {
        return makeJoin(Integer.MAX_VALUE, capture, join);
    }

    protected static Capture makeJoin(int maxMatch, Capture capture, Capture join) {
        return makeJoin(0, maxMatch, capture, join);
    }

    protected static Capture makeJoin(int minMatch, int maxMatch, Capture capture, Capture join) {
        return compound(
                capture,
                compound(minMatch, maxMatch, join, capture.create())
        );
    }

    protected static CompoundCapture compound(Capture... captures) {
        return new CompoundCapture(captures);
    }

    protected static CompoundCapture compound(int matches, Capture... captures) {
        return new CompoundCapture(matches, captures);
    }

    protected static CompoundCapture compound(int minMatch, int maxMatch, Capture... captures) {
        return new CompoundCapture(minMatch, maxMatch, captures);
    }

    protected static SelectionCapture forks(Capture... captures) {
        return new SelectionCapture(captures);
    }

    protected static SelectionCapture forks(int matches, Capture... captures) {
        return new SelectionCapture(matches, captures);
    }

    protected static SelectionCapture forks(int minMatch, int maxMatch, Capture... captures) {
        return new SelectionCapture(minMatch, maxMatch, captures);
    }

    protected static LinesCapture line() {
        return new LinesCapture();
    }

    protected static LinesCapture line(Scopec scope) {
        return new LinesCapture(scope);
    }

    protected static RegexCapture regex(@RegExp String regex) {
        return new RegexCapture(Pattern.compile(regex));
    }

    protected static TokenCapture token(String... tokens) {
        return new TokenCapture(tokens);
    }

    protected static RegexCapture regex(Scopec scope, @RegExp String regex) {
        return new RegexCapture(scope, Pattern.compile(regex));
    }

    protected static TokenCapture token(Scopec scope, String... tokens) {
        return new TokenCapture(scope, tokens);
    }

    protected static RegexCapture regex(int maths, @RegExp String regex) {
        return new RegexCapture(maths, Pattern.compile(regex));
    }

    protected static TokenCapture token(int matches, String... tokens) {
        return new TokenCapture(matches, tokens);
    }

    protected static RegexCapture regex(int matches, Scopec scope, @RegExp String regex) {
        return new RegexCapture(matches, scope, Pattern.compile(regex));
    }

    protected static TokenCapture token(int matches, Scopec scope, String... tokens) {
        return new TokenCapture(matches, scope, tokens);
    }

    protected static RegexCapture regex(int minMatches, int maxMatches, Scopec scope, @RegExp String regex) {
        return new RegexCapture(minMatches, maxMatches, scope, Pattern.compile(regex));
    }

    protected static TokenCapture token(int minMatches, int maxMatches, Scopec scope, String... tokens) {
        return new TokenCapture(minMatches, maxMatches, scope, tokens);
    }
}
