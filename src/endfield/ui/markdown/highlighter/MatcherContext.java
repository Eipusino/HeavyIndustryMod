package endfield.ui.markdown.highlighter;

import java.util.Stack;

public class MatcherContext extends TokensContext {
	protected final Stack<Piece> pieceStack = new Stack<>();
	protected final Stack<Piece> rawContextPieceStack = new Stack<>();

	protected int currentIndex = 0;

	public void pushPiece(Piece piece) {
		(inRawContext ? rawContextPieceStack : pieceStack).push(piece);
	}

	public Piece peekPiece() {
		Stack<Piece> stack = inRawContext ? rawContextPieceStack : pieceStack;
		return stack.isEmpty() ? null : stack.peek();
	}

	public void popPiece() {
		(inRawContext ? rawContextPieceStack : pieceStack).pop();
	}

	public int blockDepth() {
		return (inRawContext ? rawContextPieceStack : pieceStack).size();
	}

	public MatcherContext subContext() {
		MatcherContext res = new MatcherContext();
		res.currentIndex = currentIndex;
		res.tokens.addAll(tokens);
		res.rawTokens.addAll(rawTokens);
		return res;
	}
}
