package heavyindustry.ui.markdown.highlighter;

import java.util.List;

public interface NameIndexer<T> {
	List<TokenMatcher> indexes(String... names);

	List<TokenMatcher> allIndexed();
}
