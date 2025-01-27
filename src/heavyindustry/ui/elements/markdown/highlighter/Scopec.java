package heavyindustry.ui.elements.markdown.highlighter;

import java.util.*;

public interface Scopec {
	void apply(Tokenf token, ScopeHandler handler);

	enum Default implements Scopec {
		//internal special
		NONE,
		SPACE,

		KEYWORD,
		OPERATOR,
		NUMBER,
		TYPE,
		TYPE_ARG,
		STRING,
		CONTROL,
		COMMENT,
		DOCS,
		DOC_MARK,
		SEPARATOR,
		VARIABLE,
		MEMBER_VAR,
		FUNCTION,
		CONSTRUCTOR,
		FUNCTION_INVOKE,
		ARGUMENT,
		CODE_BLOCK,
		ANNOTATION;

		@Override
		public void apply(Tokenf token, ScopeHandler handler) {
			handler.applyScope(token, this);
		}
	}

	enum LuaScope implements Scopec {
		LOCAL_VARS,
		TABLE_VARS,
		KEYWORD_BODY,
		KEYWORD_CONTROL,
		KEYWORD_VAR1,
		KEYWORD_VAR2,
		KEYWORD_SELF,
		KEYWORD_FUNCTION;

		@Override
		public void apply(Tokenf token, ScopeHandler handler) {
			handler.applyScope(token, this);
		}
	}

	class RainbowSeparator implements Scopec {
		public int depth;
		public Scopec type;

		public RainbowSeparator(int dep, Scopec typ) {
			depth = dep;
			type = typ;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			RainbowSeparator obj = ((RainbowSeparator) o);
			return obj.depth == depth && Objects.equals(obj.type, type);
		}

		@Override
		public int hashCode() {
			return Objects.hash(depth, type);
		}

		@Override
		public void apply(Tokenf token, ScopeHandler handler) {
			handler.applyScope(token, this);
		}

		@Override
		public String toString() {
			return "RainbowSeparator{" + "depth=" + depth + ", type=" + type.toString() + "}";
		}
	}
}
