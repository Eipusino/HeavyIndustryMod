package heavyindustry.ui.markdown.highlighter.defaults;

import arc.func.Prov;
import heavyindustry.ui.markdown.highlighter.Capture;
import heavyindustry.ui.markdown.highlighter.CompoundCapture;
import heavyindustry.ui.markdown.highlighter.LazyCapture;
import heavyindustry.ui.markdown.highlighter.LinesCapture;
import heavyindustry.ui.markdown.highlighter.MatcherReference;
import heavyindustry.ui.markdown.highlighter.NameIndexer;
import heavyindustry.ui.markdown.highlighter.PieceMatcher;
import heavyindustry.ui.markdown.highlighter.RegexCapture;
import heavyindustry.ui.markdown.highlighter.Scope;
import heavyindustry.ui.markdown.highlighter.SelectionCapture;
import heavyindustry.ui.markdown.highlighter.SerialMatcher;
import heavyindustry.ui.markdown.highlighter.TokenCapture;
import heavyindustry.ui.markdown.highlighter.TokenMatcher;
import org.intellij.lang.annotations.RegExp;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public abstract class Highlight {
	protected Highlight() {}

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

	protected static PieceMatcher block(List<Capture> begin, List<Capture> end) {
		return PieceMatcher.create(begin, end);
	}

	protected static PieceMatcher block(Scope scope, List<Capture> begin, List<Capture> end) {
		return PieceMatcher.create(scope, begin, end);
	}

	protected static PieceMatcher block(int priority, Scope scope, List<Capture> begin, List<Capture> end) {
		return PieceMatcher.create(priority, scope, begin, end);
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

	protected static LinesCapture line(Scope scope) {
		return new LinesCapture(scope);
	}

	protected static RegexCapture regex(@RegExp String regex) {
		return new RegexCapture(Pattern.compile(regex));
	}

	protected static RegexCapture regex(Pattern pattern) {
		return new RegexCapture(pattern);
	}

	protected static TokenCapture token(String... tokens) {
		return new TokenCapture(tokens);
	}

	protected static RegexCapture regex(Scope scope, @RegExp String regex) {
		return new RegexCapture(scope, Pattern.compile(regex));
	}

	protected static RegexCapture regex(Scope scope, Pattern pattern) {
		return new RegexCapture(scope, pattern);
	}

	protected static TokenCapture token(Scope scope, String... tokens) {
		return new TokenCapture(scope, tokens);
	}

	protected static RegexCapture regex(int maths, @RegExp String regex) {
		return new RegexCapture(maths, Pattern.compile(regex));
	}

	protected static RegexCapture regex(int maths, Pattern pattern) {
		return new RegexCapture(maths, pattern);
	}

	protected static TokenCapture token(int matches, String... tokens) {
		return new TokenCapture(matches, tokens);
	}

	protected static RegexCapture regex(int matches, Scope scope, @RegExp String regex) {
		return new RegexCapture(matches, scope, Pattern.compile(regex));
	}

	protected static RegexCapture regex(int matches, Scope scope, Pattern pattern) {
		return new RegexCapture(matches, scope, pattern);
	}

	protected static TokenCapture token(int matches, Scope scope, String... tokens) {
		return new TokenCapture(matches, scope, tokens);
	}

	protected static RegexCapture regex(int minMatches, int maxMatches, Scope scope, @RegExp String regex) {
		return new RegexCapture(minMatches, maxMatches, scope, Pattern.compile(regex));
	}

	protected static RegexCapture regex(int minMatches, int maxMatches, Scope scope, Pattern pattern) {
		return new RegexCapture(minMatches, maxMatches, scope, pattern);
	}

	protected static TokenCapture token(int minMatches, int maxMatches, Scope scope, String... tokens) {
		return new TokenCapture(minMatches, maxMatches, scope, tokens);
	}
}
