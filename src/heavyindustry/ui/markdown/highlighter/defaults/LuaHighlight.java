package heavyindustry.ui.markdown.highlighter.defaults;

import heavyindustry.ui.markdown.highlighter.Capture;
import heavyindustry.ui.markdown.highlighter.PatternsHighlight;
import heavyindustry.ui.markdown.highlighter.Scope.Default;
import heavyindustry.ui.markdown.highlighter.Scope.LuaScope;
import heavyindustry.ui.markdown.highlighter.SelectionCapture;

import java.util.regex.Pattern;

/**
 * LuaHighlight
 */
public final class LuaHighlight extends DSL {
	private LuaHighlight() {}

	static Capture modifiersCapture() {
		return regex(1, Integer.MAX_VALUE, LuaScope.KEYWORD_CONTROL, "local");
	}

	static Capture variableCapture(int depth) {
		return forks(
				compound(
						modifiersCapture(),
						makeJoin(
								compound(
										regex(LuaScope.LOCAL_VARS, "\\w+"),
										compound(
												token(Default.OPERATOR, "="),
												lazy(() -> expressionCapture(0))
										).setOptional(true)
								),
								token(Default.SEPARATOR, ",")
						)
				),
				compound(
						makeJoin(
								compound(
										//TODD depth
										regex(depth > 0 ? LuaScope.TABLE_VARS : LuaScope.LOCAL_VARS, "^(?!end$|in$|do$|function$|if$|then$|for$)\\w+"),
										token(Default.OPERATOR, "="),
										lazy(() -> expressionCapture(depth))
								),
								token(Default.SEPARATOR, ",")
						)
				)
		);
	}

	static Capture functionCapture() {
		return compound(
				token(LuaScope.KEYWORD_BODY, "function"),
				makeJoin(
						regex(Default.FUNCTION, "\\w+"),
						forks(
								token(Default.SEPARATOR, ":"),
								token(Default.SEPARATOR, ".")
						)
				),
				token(Default.SEPARATOR, "("),
				makeJoin(
						forks(
								regex(Default.ARGUMENT, "\\w+"),
								regex(Default.ARGUMENT, "\\.+")
						),
						token(Default.SEPARATOR, ",")
				).setOptional(true),
				token(Default.SEPARATOR, ")"),
				functionStatementBlockCapture()
		);
	}

	static Capture statementCapture(int depth) {
		return forks(
				//IF
				compound(
						token(LuaScope.KEYWORD_BODY, "if"),
						expressionCapture(0),
						token(LuaScope.KEYWORD_BODY, "then"),
						ifStatementBlockCapture()
				),
				//WHILE
				compound(
						token(LuaScope.KEYWORD_BODY, "while"),
						expressionCapture(0),
						token(LuaScope.KEYWORD_BODY, "do"),
						functionStatementBlockCapture()
				),
				//REPEAT_UNTIL
				compound(
						token(LuaScope.KEYWORD_BODY, "repeat"),
						lazy(() -> statementsCapture(0)),
						token(LuaScope.KEYWORD_BODY, "until"),
						expressionCapture(0)
				),
				//FOR
				compound(
						token(LuaScope.KEYWORD_BODY, "for"),
						expressionCapture(0),
						token(Default.SEPARATOR, ","),
						expressionCapture(0),
						compound(
								token(Default.SEPARATOR, ","),
								expressionCapture(0)
						).setOptional(true),
						token(LuaScope.KEYWORD_BODY, "do"),
						functionStatementBlockCapture()
				),
				//FOR IN
				compound(
						token(LuaScope.KEYWORD_BODY, "for"),
						makeJoin(
								regex(Default.ARGUMENT, "\\w+"),
								token(Default.SEPARATOR, ",")
						),
						token(LuaScope.KEYWORD_CONTROL, "in"),
						expressionCapture(0),
						token(LuaScope.KEYWORD_BODY, "do"),
						functionStatementBlockCapture()
				),

				//RETURN
				compound(
						token(LuaScope.KEYWORD_CONTROL, "return"),
						expressionCapture(0).setOptional(true)
				),
				functionCapture(),
				variableCapture(depth),
				//EXPRESSION
				expressionCapture(depth),
				//CODE_BLOCK
				compound(
						token(LuaScope.KEYWORD_BODY, "do"),
						lazy(() -> statementsCapture(0)),
						token(LuaScope.KEYWORD_BODY, "end")
				)
		);
	}

	static Capture metaExpCapture(int depth) {
		return compound(
				forks(
						token(LuaScope.KEYWORD_SELF, "self"),
						compound(
								token(Default.SEPARATOR, "("),
								lazy(() -> expressionCapture(depth)),
								token(Default.SEPARATOR, ")")
						),
						compound(
								token(new Default.RainbowSeparator(depth % 7, Default.SEPARATOR), "{"),
								makeJoin(
										lazy(() -> statementsCapture(depth + 1)),
										token(Default.SEPARATOR, ",")
								),
								token(new Default.RainbowSeparator(depth % 7, Default.SEPARATOR), "}")
						),
						//LAMBDA
						compound(
								token(LuaScope.KEYWORD_BODY, "function"),
								token(Default.SEPARATOR, "("),
								makeJoin(
										forks(
												regex(Default.ARGUMENT, "\\w+"),
												regex(Default.ARGUMENT, "\\.+")
										),
										token(Default.SEPARATOR, ",")
								).setOptional(true),
								token(Default.SEPARATOR, ")"),
								functionStatementBlockCapture()
						),
						//token(KEYWORD,"end").setMatchOnly(true),
						constantLiteralCapture(),
						//INVOKE
						compound(
								forks(
										regex(LuaScope.KEYWORD_FUNCTION, "import"),
										regex(Default.FUNCTION_INVOKE, "require"),
										regex(Default.FUNCTION_INVOKE, "^(?!end$|do$|in$|function$|then$|if$|for$)\\w+")
								),
								forks(
										compound(
												token("("),
												makeJoin(
														lazy(() -> expressionCapture(0)).setOptional(true),
														token(Default.SEPARATOR, ",")
												),
												token(")")
										),
										makeJoin(
												lazy(() -> expressionCapture(0)),
												token(Default.SEPARATOR, ",")
										)
								)
						),
						//READ ARRAY
						compound(
								regex(Default.VARIABLE, "^(?!end$|in$|do$|function$|if$|then$|for$)\\w+"),
								compound(1, Integer.MAX_VALUE,
										token("["),
										lazy(() -> expressionCapture(0)),
										token("]")
								)
						),
						compound(
								regex(Default.OPERATOR, "[!+\\-~]"),
								lazy(() -> expressionCapture(0))
						),
						//REF
						regex(Default.VARIABLE, "^(?!end$|in$|do$|function$|if$|then$|for$)\\w+")
				)
		);
	}

	static Capture bracketStringCapture() {
		return compound(
				compound(
						token(Default.STRING, "["),
						token(0, Integer.MAX_VALUE, Default.STRING, "="),
						token(Default.STRING, "[")
				),
				new SelectionCapture(0, Integer.MAX_VALUE,
						lazy(LuaHighlight::bracketStringCapture),
						regex(Default.STRING, "[^]]+")
				),
				compound(
						token(Default.STRING, "]"),
						token(0, Integer.MAX_VALUE, Default.STRING, "="),
						token(Default.STRING, "]")
				)
		);
	}

	static Capture constantLiteralCapture() {
		return forks(
				//STRING
				compound(
						token(Default.STRING, "\""),
						new SelectionCapture(0, Integer.MAX_VALUE,
								regex(
										Default.CONTROL,
										"(\\\\[0-7]{3})|(\\\\u[0-9a-fA-F]{4})|(\\\\[0abtnvfre\\\\\"'])"
								),
								regex(Default.STRING, "[^\"]+")
						),
						token(Default.STRING, "\"")
				),
				//CHARACTER
				compound(
						token(Default.STRING, "'"),
						new SelectionCapture(
								regex(
										Default.CONTROL,
										"(\\\\[0-7]{3})|(\\\\u[0-9a-fA-F]{4})|(\\\\[0abtnvfre\\\\\"'])"
								),
								regex(Default.STRING, "[^']")
						),
						token(Default.STRING, "'")
				),
				//DOUBLE BRACKETS
				bracketStringCapture(),
				//NUMBER
				regex(Default.NUMBER, "[\\d_]*(\\.[\\d_]+)?[fFdDlL]?"),
				//TODD
				//BOOLEAN
				regex(LuaScope.KEYWORD_VAR1, "true|false"),
				//NULL
				token(LuaScope.KEYWORD_VAR2, "nil")
		);
	}

	static Capture statementsCapture(int depth) {
		return compound(0, Integer.MAX_VALUE,
				statementCapture(depth)
		);
	}

	static Capture functionStatementBlockCapture() {
		return compound(
				lazy(() -> statementsCapture(0)),
				token(LuaScope.KEYWORD_BODY, "end")
		);
	}

	static Capture ifStatementBlockCapture() {
		return compound(
				lazy(() -> statementsCapture(0)),
				forks(
						token(LuaScope.KEYWORD_BODY, "end"),
						compound(
								token(LuaScope.KEYWORD_BODY, "else"),
								lazy(() -> statementsCapture(0)),
								token(LuaScope.KEYWORD_BODY, "end")
						)
				),
				compound(
						token(LuaScope.KEYWORD_BODY, "elseif"),
						lazy(LuaHighlight::ifStatementBlockCapture)
				)
		);
	}

	static Capture statementBlockCapture() {
		return forks(
				compound(
						token(LuaScope.KEYWORD_BODY, "do"),
						lazy(() -> statementsCapture(0)),
						token(LuaScope.KEYWORD_BODY, "end")
				),
				lazy(() -> statementsCapture(0))
		).setOptional(true);
	}

	static Capture expressionCapture(int depth) {
		return makeJoin(
				compound(
						metaExpCapture(depth),
						regex(Default.OPERATOR, "\\+\\+|--").setOptional(true)
				),
				regex(Default.OPERATOR, "(->|!=|==|<=|>=|&&|\\|\\||\\+=|-=|\\*=|/=|%=|&=|\\|=|\\^=|<<=|>>=|>>>=)|[=.+\\-*/%&|<>^]")
		);
	}

	public static PatternsHighlight create() {
		PatternsHighlight res = new PatternsHighlight("lua");

		res.tokensSplit = Pattern.compile("\\s+");
		res.rawTokenMatcher = Pattern.compile("(--\\[\\[(.|\\n)*?]]|--.*)");
		res.symbolMatcher = Pattern.compile(
				"(->|!=|==|<=|>=|&&|\\|\\||\\+\\+|--|\\+=|-=|\\*=|/=|%=|&=|\\|=|\\^=|<<=|>>=|>>>=)" +
						"|(\\\\[0-7]{3})" +
						"|(\\\\u[0-9a-fA-F]{4})" +
						"|(\\\\[0abtnvfre\\\\\"'])" +
						"|[\\\\.+\\-*/%&|!<>~^=,;:(){}\"'\\[\\]]"
		);

		res//RAW CONTEXT
				.addRawContextPattern("line_comment", block(Default.COMMENT,
						of(token(2, "-")),
						of(line(Default.COMMENT))
				))
				.addRawContextPattern("block_comment", block(Default.COMMENT,
						of(token(Default.COMMENT, "--"), token(Default.COMMENT, "[[")),
						of(token(Default.COMMENT, "]]"))
				))

				.addPattern("keywords", serial(-100, token(
						Default.KEYWORD,
						"local",
						"return", "require", "import",
						"do", "then", "while", "for", "in", "break", "end",
						"if", "else", "elseif", "goto", "until", "repeat",
						"and", "or", "not",
						"nil", "true", "false",
						"self", "function"
				)))
				.addPattern("statement", serial(-10, statementCapture(0)))
				/*.addPattern("function", serial(functionCapture()))
				.addPattern("variable", serial(variableCapture()))*/;
		return res;
	}

}
