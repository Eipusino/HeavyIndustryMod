package heavyindustry.ui.markdown.highlighter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BlockMatcher implements TokenMatcher, NameIndexer<TokenMatcher> {
	protected Scope scope;
	protected int priority = 0;
	protected List<Capture> beginCaptures;
	protected List<Capture> endCaptures;
	protected final Map<String, TokenMatcher> children = new LinkedHashMap<>();

	protected int[] beginLens;
	protected int[] endLens;

	protected BlockMatcher() {}

	public static BlockMatcher create(List<Capture> beginCaptures, List<Capture> endCaptures) {
		return create(null, beginCaptures, endCaptures);
	}

	public static BlockMatcher create(Scope scope, List<Capture> beginCaptures, List<Capture> endCaptures) {
		return create(0, scope, beginCaptures, endCaptures);
	}

	public static BlockMatcher create(int priority, List<Capture> beginCaptures, List<Capture> endCaptures) {
		return create(priority, null, beginCaptures, endCaptures);
	}

	public static BlockMatcher create(int priority, Scope scope, List<Capture> beginCaptures, List<Capture> endCaptures) {
		BlockMatcher res = new BlockMatcher();
		res.scope = scope;
		res.priority = priority;
		res.beginCaptures = beginCaptures;
		res.endCaptures = endCaptures;

		res.beginLens = new int[beginCaptures.size()];
		res.endLens = new int[endCaptures.size()];

		return res;
	}

	public BlockMatcher addChildPattern(String patternName, TokenMatcher matcher) {
		children.put(patternName, matcher.create());
		return this;
	}

	@Override
	public int match(MatcherContext context, Token token) throws MatchFailed {
		return SerialMatcher.matchCapture(beginCaptures, beginLens, context, token);
	}

	@Override
	public void apply(MatcherContext context, Token token) {
		SerialMatcher.applyCapture(scope, beginCaptures, beginLens, context, token);

		List<TokenMatcher> matchers = new ArrayList<>(children.values());
		matchers.add(new InnerBlockMatcher());
		context.pushBlock(new Block(scope, this, matchers));
	}

	@Override
	public TokenMatcher create() {
		BlockMatcher res = create(priority, scope, beginCaptures, endCaptures);
		for (Map.Entry<String, TokenMatcher> entry : children.entrySet()) {
			res.addChildPattern(entry.getKey(), entry.getValue());
		}
		return res;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	@SuppressWarnings("DuplicatedCode")
	@Override
	public List<TokenMatcher> indexes(String... names) {
		List<TokenMatcher> list = new ArrayList<>();
		Set<String> set = new HashSet<>(Arrays.asList(names));
		for (Map.Entry<String, TokenMatcher> entry : children.entrySet()) {
			if (set.contains(entry.getKey())) list.add(entry.getValue());
		}
		return list;
	}

	@Override
	public List<TokenMatcher> allIndexed() {
		return new ArrayList<>(children.values());
	}

	protected class InnerBlockMatcher implements TokenMatcher {
		@Override
		public int match(MatcherContext context, Token token) throws MatchFailed {
			return SerialMatcher.matchCapture(endCaptures, endLens, context, token);
		}

		@Override
		public void apply(MatcherContext context, Token token) {
			SerialMatcher.applyCapture(scope, endCaptures, endLens, context, token);
			context.popBlock();
		}

		@Override
		public TokenMatcher create() {
			return new InnerBlockMatcher();
		}

		@Override
		public int getPriority() {
			return priority;
		}
	}
}
