package endfield.ui.markdown.highlighter;

import endfield.util.CollectionList;

import java.util.List;

public class Piece {
	protected final Scope scope;
	protected final TokenMatcher ownerMatcher;
	protected final List<TokenMatcher> matchers;

	public Piece(Scope scope, List<TokenMatcher> matchers) {
		this.scope = scope;
		this.ownerMatcher = null;
		this.matchers = matchers;
	}

	public Piece(Scope scope, TokenMatcher currMatcher, List<TokenMatcher> matchers) {
		this.scope = scope;
		this.ownerMatcher = currMatcher;
		this.matchers = matchers;
	}

	public Scope scope() {
		return scope;
	}

	public List<TokenMatcher> matchers() {
		List<TokenMatcher> list = new CollectionList<>(TokenMatcher.class);
		for (TokenMatcher matcher : matchers) {
			if (matcher instanceof TokenMatcher.MatcherGroup ref) {
				list.addAll(ref.asMatchers());
			} else {
				list.add(matcher);
			}
		}
		return list;
	}

	public TokenMatcher ownerMatcher() {
		return ownerMatcher;
	}
}
