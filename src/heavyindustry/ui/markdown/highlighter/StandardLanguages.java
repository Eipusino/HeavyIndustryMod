package heavyindustry.ui.markdown.highlighter;

import heavyindustry.ui.markdown.highlighter.defaults.JavaHighlight;
import heavyindustry.ui.markdown.highlighter.defaults.LuaHighlight;

public final class StandardLanguages {
	public static PatternsHighlight JAVA, LUA;

	static {
		JAVA = JavaHighlight.create();
		LUA = LuaHighlight.create();
	}

	private StandardLanguages() {}
}
