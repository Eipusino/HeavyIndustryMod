package endfield.ui.markdown.highlighter;

import endfield.ui.markdown.highlighter.TokenMatcher.MatchFailed;
import endfield.util.CollectionObjectSet;
import endfield.util.Constant;

import java.util.Set;

public class TokenCapture extends Capture {
	protected final Set<String> tokens;
	protected final Scope scope;

	protected final int minMatch;
	protected final int maxMatch;

	public TokenCapture(String... tokens) {
		this(1, null, tokens);
	}

	public TokenCapture(Scope scope, String... tokens) {
		this(1, scope, tokens);
	}

	public TokenCapture(int matches, String... tokens) {
		this(matches, matches, null, tokens);
	}

	public TokenCapture(int matches, Scope scope, String... tokens) {
		this(matches, matches, scope, tokens);
	}

	public TokenCapture(int minMatch, int maxMatch, Scope scope, String... tokens) {
		this.minMatch = minMatch;
		this.maxMatch = maxMatch;
		this.scope = scope;
		this.tokens = CollectionObjectSet.with(tokens);
	}

	@Override
	public int match(MatcherContext context, Token token) throws MatchFailed {
		int off = 0;

		int max = Math.min(maxMatch, context.getTokensCountInContext());
		while (off < max) {
			Token curr = context.getTokenInContext(token.getIndexInContext(context) + off);
			if (!tokens.contains(curr.text)) {
				if (off < minMatch) throw MatchFailed.instance;
				else break;
			}

			off++;
		}

		return off;
	}

	@Override
	public void applyScope(MatcherContext context, Token token, int matchedLen) {
		if (scope == null) return;

		for (int i = 0; i < matchedLen; i++) {
			context.getTokenInContext(token.getIndexInContext(context) + i).scope = scope;
		}
	}

	@Override
	public TokenCapture create() {
		TokenCapture capture = new TokenCapture(minMatch, maxMatch, scope, tokens.toArray(Constant.EMPTY_STRING));
		capture.setMatchOnly(matchOnly).setOptional(optional);
		return capture;
	}
}
