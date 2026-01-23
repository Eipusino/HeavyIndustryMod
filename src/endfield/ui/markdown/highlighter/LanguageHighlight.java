package endfield.ui.markdown.highlighter;

public interface LanguageHighlight<T extends TokensContext> {
	String language();

	T initContext();

	void splitTokens(TokensContext context, String text);

	int flowScope(T context, Token token);
}
