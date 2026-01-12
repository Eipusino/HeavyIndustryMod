package heavyindustry.ui.markdown.highlighter;

@FunctionalInterface
public interface ScopeHandler {
	void applyScope(Token token, Scope scope);
}
