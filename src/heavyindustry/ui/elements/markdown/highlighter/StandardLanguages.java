package heavyindustry.ui.elements.markdown.highlighter;

import heavyindustry.ui.elements.markdown.highlighter.defaults.*;

public class StandardLanguages {
	public static PatternsHighlight JAVA, LUA;

	static {
		JAVA = JavaHighlight.create();
		LUA = LuaHighlight.create();
	}
}
