package heavyindustry.ui.elements.markdown.highlighter;

import java.util.*;

public class MatcherContext extends TokensContext {
    private final Stack<Blockf> blockStack = new Stack<>();
    private final Stack<Blockf> rawContextBlockStack = new Stack<>();

    private int currentIndex = 0;

    public void pushBlock(Blockf block) {
        (inRawContext ? rawContextBlockStack : blockStack).push(block);
    }

    public Blockf peekBlock() {
        Stack<Blockf> stack = inRawContext ? rawContextBlockStack : blockStack;
        return stack.isEmpty() ? null : stack.peek();
    }

    public void popBlock() {
        (inRawContext ? rawContextBlockStack : blockStack).pop();
    }

    public int blockDepth() {
        return (inRawContext ? rawContextBlockStack : blockStack).size();
    }

    public MatcherContext subContext() {
        MatcherContext res = new MatcherContext();
        res.currentIndex = currentIndex;
        res.tokens.addAll(tokens);
        res.rawTokens.addAll(rawTokens);
        return res;
    }
}
