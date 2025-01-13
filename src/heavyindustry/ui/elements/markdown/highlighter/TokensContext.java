package heavyindustry.ui.elements.markdown.highlighter;

import java.util.*;

public class TokensContext {
    protected final List<Tokenf> tokens = new ArrayList<>();
    protected final List<Tokenf> rawTokens = new ArrayList<>();

    public boolean inRawContext;

    private int cursor;

    public Tokenf getTokenInContext(int index) {
        return inRawContext ? getTokenRaw(index) : getToken(index);
    }

    public int getTokensCountInContext() {
        return inRawContext ? getTokenCountRaw() : getTokenCount();
    }

    public List<Tokenf> getTokens() {
        return Collections.unmodifiableList(tokens);
    }

    public List<Tokenf> getTokensRaw() {
        return Collections.unmodifiableList(rawTokens);
    }

    public void putToken(Tokenf tok) {
        tok.index = tokens.size();
        tok.rawIndex = rawTokens.size();

        tokens.add(tok);
        rawTokens.add(tok);
    }

    public void putTokenRaw(Tokenf tokens) {
        tokens.rawIndex = rawTokens.size();
        rawTokens.add(tokens);
    }

    protected Tokenf getToken(int index) {
        return tokens.get(index);
    }

    protected int getTokenCount() {
        return tokens.size();
    }

    protected Tokenf getTokenRaw(int index) {
        return rawTokens.get(index);
    }

    protected int getTokenCountRaw() {
        return rawTokens.size();
    }

    public void applyScopes(ScopeHandler handler) {
        for (Tokenf token : getTokensRaw()) {
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
