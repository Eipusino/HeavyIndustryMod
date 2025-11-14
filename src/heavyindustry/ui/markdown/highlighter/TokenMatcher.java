package heavyindustry.ui.markdown.highlighter;

import heavyindustry.util.Failed;

import java.util.List;

public interface TokenMatcher extends Comparable<TokenMatcher> {
	int match(MatcherContext context, Token token) throws MatchFailed;

	void apply(MatcherContext context, Token token);

	TokenMatcher create();

	int getPriority();

	@Override
	default int compareTo(TokenMatcher o) {
		return Integer.compare(o.getPriority(), getPriority());
	}

	interface MatcherGroup {
		List<TokenMatcher> asMatchers();
	}

	class MatchFailed extends Failed {
		private static final long serialVersionUID = 3458499504960611300l;

		public static final MatchFailed INSTANCE = new MatchFailed();

		protected MatchFailed() {}
	}
}
