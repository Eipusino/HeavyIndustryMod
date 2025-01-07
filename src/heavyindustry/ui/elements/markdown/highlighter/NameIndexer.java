package heavyindustry.ui.elements.markdown.highlighter;

import java.util.*;

public interface NameIndexer<T> {
    List<TokenMatcher> indexes(String... names);

    List<TokenMatcher> allIndexed();
}
