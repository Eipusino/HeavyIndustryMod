package heavyindustry.ui.markdown.highlighter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PieceCapture extends Capture {
	private final Capture beginCapture;
	private final Capture endCapture;

	private final List<TokenMatcher> children = new ArrayList<>();
	private List<TokenMatcher> list;

	public PieceCapture(Capture beginCapture, Capture endCapture) {
		this.beginCapture = beginCapture;
		this.endCapture = endCapture;
	}

	public PieceCapture addChildPatterns(TokenMatcher... matchers) {
		children.addAll(Arrays.asList(matchers));
		return this;
	}

	@Override
	public int match(MatcherContext context, Token token) throws TokenMatcher.MatchFailed {
		if (list == null) {
			list = new ArrayList<>();
			for (TokenMatcher child : children) {
				list.add(child.create());
			}
		}

		int len = beginCapture.match(context, token);

		MatcherContext subContext = context.subContext();
		subContext.forwardCursor(len);
		subContext.pushPiece(new Piece(null, list));

		c:
		while (subContext.currCursor() < subContext.getTokensCountInContext()) {
			Token curr = subContext.getTokenInContext(subContext.currCursor());

			Piece piece = subContext.peekPiece();
			Scope scope = piece.scope();

			List<TokenMatcher> mats = piece.matchers();
			mats.sort(TokenMatcher::compareTo);

			for (TokenMatcher matcher : mats) {
				try {
					int n = matcher.match(subContext, curr);
					if (scope != null) curr.scope = scope;
					matcher.apply(subContext, curr);

					subContext.forwardCursor(n);
					len += n;

					continue c;
				} catch (TokenMatcher.MatchFailed ignored) {
				}
			}

			try {
				int endLen = endCapture.match(subContext, curr);
				subContext.popPiece();
				subContext.forwardCursor(endLen);
				len += endLen;

				break;
			} catch (TokenMatcher.MatchFailed ignored) {
			}
		}

		return len;
	}

	@Override
	public void applyScope(MatcherContext context, Token token, int matchedLen) {
	}

	@Override
	public Capture create() {
		return new PieceCapture(beginCapture, endCapture).addChildPatterns(children.toArray(new TokenMatcher[0]));
	}
}
