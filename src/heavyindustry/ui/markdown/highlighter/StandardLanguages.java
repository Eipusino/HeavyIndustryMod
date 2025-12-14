package heavyindustry.ui.markdown.highlighter;

import heavyindustry.ui.markdown.highlighter.defaults.JavaHighlight;
import heavyindustry.ui.markdown.highlighter.defaults.LuaHighlight;

public final class StandardLanguages {
	public static PatternsHighlight javaHighlight, luaHighlight;

	static {
		javaHighlight = JavaHighlight.create();
		luaHighlight = LuaHighlight.create();
	}

	private StandardLanguages() {}
}
