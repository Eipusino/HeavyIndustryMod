package heavyindustry.ui.markdown.highlighter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TokensContext {
	protected final List<Token> tokens = new ArrayList<>();
	protected final List<Token> rawTokens = new ArrayList<>();

	public boolean inRawContext;

	private int cursor;

	public Token getTokenInContext(int index) {
		return inRawContext ? getTokenRaw(index) : getToken(index);
	}

	public int getTokensCountInContext() {
		return inRawContext ? getTokenCountRaw() : getTokenCount();
	}

	public List<Token> getTokens() {
		return Collections.unmodifiableList(tokens);
	}

	public List<Token> getTokensRaw() {
		return Collections.unmodifiableList(rawTokens);
	}

	public void putToken(Token token) {
		token.index = tokens.size();
		token.rawIndex = rawTokens.size();

		tokens.add(token);
		rawTokens.add(token);
	}

	public void putTokenRaw(Token tokens) {
		tokens.rawIndex = rawTokens.size();
		rawTokens.add(tokens);
	}

	protected Token getToken(int index) {
		return tokens.get(index);
	}

	protected int getTokenCount() {
		return tokens.size();
	}

	protected Token getTokenRaw(int index) {
		return rawTokens.get(index);
	}

	protected int getTokenCountRaw() {
		return rawTokens.size();
	}

	public void applyScopes(ScopeHandler handler) {
		for (Token token : getTokensRaw()) {
			if (token.scope != null) token.scope.apply(token, handler);
		}
	}

	public int currCursor() {
		return cursor;
	}

	public void resetCursor() {
		cursor = 0;
	}

	public void forwardCursor(int step) {
		cursor += step;
	}
}
