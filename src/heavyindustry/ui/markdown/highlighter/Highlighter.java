package heavyindustry.ui.markdown.highlighter;

import heavyindustry.util.CollectionObjectMap;

import java.util.Map;

public class Highlighter {
	private final Map<String, LanguageHighlight<?>> languages = new CollectionObjectMap<>(String.class, LanguageHighlight.class);

	public Highlighter addLanguage(LanguageHighlight<?> highlight) {
		languages.put(highlight.language().toLowerCase(), highlight);

		return this;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public TokensContext analyze(String language, String text) {
		LanguageHighlight highlight = languages.get(language.toLowerCase());

		if (highlight == null) return null;

		TokensContext context = highlight.initContext();
		highlight.splitTokens(context, text);

		for (int inRaw = 0; inRaw < 2; inRaw++) {
			context.inRawContext = inRaw != 0;

			context.resetCursor();
			int max = context.getTokensCountInContext();
			while (context.currCursor() < max) {
				Token token = context.getTokenInContext(context.currCursor());

				int step = highlight.flowScope(context, token);

				if (step <= 0) context.forwardCursor(1);
				else context.forwardCursor(step);
			}
		}

		context.getTokensRaw().forEach(e -> {
			if (e.scope == null) {
				e.scope = Scope.Default.NONE;
			}
		});

		return context;
	}
}
