package endfield.ui.markdown.highlighter;

import endfield.ui.markdown.highlighter.defaults.JavaHighlight;
import endfield.ui.markdown.highlighter.defaults.LuaHighlight;

public final class StandardLanguages {
	public static PatternsHighlight javaHighlight, luaHighlight;

	static {
		javaHighlight = JavaHighlight.create();
		luaHighlight = LuaHighlight.create();
	}

	private StandardLanguages() {}
}
