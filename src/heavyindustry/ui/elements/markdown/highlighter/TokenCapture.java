package heavyindustry.ui.elements.markdown.highlighter;

import heavyindustry.ui.elements.markdown.highlighter.TokenMatcher.*;

import java.util.*;

public class TokenCapture extends Capture {
	private final Set<String> tokens;
	private final Scopec scope;

	private final int minMatch;
	private final int maxMatch;

	public TokenCapture(String... tok) {
		this(1, null, tok);
	}

	public TokenCapture(Scopec sco, String... tok) {
		this(1, sco, tok);
	}

	public TokenCapture(int mat, String... tok) {
		this(mat, mat, null, tok);
	}

	public TokenCapture(int mat, Scopec sco, String... tok) {
		this(mat, mat, sco, tok);
	}

	public TokenCapture(int min, int max, Scopec sco, String... tok) {
		minMatch = min;
		maxMatch = max;
		scope = sco;
		tokens = new HashSet<>(Arrays.asList(tok));
	}

	@Override
	public int match(MatcherContext context, Tokenf token) throws MatchFailed {
		int off = 0;

		int max = Math.min(maxMatch, context.getTokensCountInContext());
		while (off < max) {
			Tokenf curr = context.getTokenInContext(token.getIndexInContext(context) + off);
			if (!tokens.contains(curr.text)) {
				if (off < minMatch) throw MatchFailed.INSTANCE;
				else break;
			}

			off++;
		}

		return off;
	}

	@Override
	public void applyScope(MatcherContext context, Tokenf token, int matchedLen) {
		if (scope == null) return;

		for (int i = 0; i < matchedLen; i++) {
			context.getTokenInContext(token.getIndexInContext(context) + i).scope = scope;
		}
	}

	@Override
	public TokenCapture create() {
		TokenCapture capture = new TokenCapture(minMatch, maxMatch, scope, tokens.toArray(new String[0]));
		capture.setMatchOnly(matchOnly).setOptional(optional);
		return capture;
	}
}
