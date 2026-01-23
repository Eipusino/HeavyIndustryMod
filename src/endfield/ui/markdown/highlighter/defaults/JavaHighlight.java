package endfield.ui.markdown.highlighter.defaults;

import endfield.ui.markdown.highlighter.Capture;
import endfield.ui.markdown.highlighter.PatternsHighlight;
import endfield.ui.markdown.highlighter.Scope.Default;
import endfield.ui.markdown.highlighter.Scope.JavaScope;
import endfield.ui.markdown.highlighter.SelectionCapture;

import java.util.regex.Pattern;

public final class JavaHighlight extends Highlight {
	private JavaHighlight() {}

	static Capture modifiersCapture() {
		return regex(0, Integer.MAX_VALUE, Default.KEYWORD, Pattern.compile("public|private|protected|static|final|abstract|synchronized|volatile|transient|native|strictfp|default|sealed|non-sealed"));
	}

	static Capture annotationCapture() {
		return compound(0, Integer.MAX_VALUE,
				regex(JavaScope.ANNOTATION, Pattern.compile("@\\w+")),
				compound(
						token("("),
						makeJoin(
								lazy(JavaHighlight::expressionCapture),
								token(Default.SEPARATOR, ",")
						),
						token(")")
				).setOptional(true)
		);
	}

	static Capture typeCapture() {
		return makeJoin(
				compound(
						annotationCapture().setOptional(true),
						regex(Default.TYPE, Pattern.compile("((?!class)\\w)+")),
						compound(
								token("<"),
								makeJoin(
										lazy(JavaHighlight::typeCapture).setOptional(true),
										token(Default.SEPARATOR, ",")
								),
								token(">")
						).setOptional(true),
						compound(0, Integer.MAX_VALUE,
								token("["),
								token("]")
						)
				),
				token(Default.TYPE, ".")
		);
	}

	static Capture typeCaptureNonArray() {
		return makeJoin(
				compound(
						regex(Default.TYPE, Pattern.compile("((?!class)\\w)+")),
						compound(
								token("<"),
								makeJoin(
										lazy(JavaHighlight::typeCapture).setOptional(true),
										token(Default.SEPARATOR, ",")
								),
								token(">")
						).setOptional(true)
				),
				token(Default.TYPE, ".")
		);
	}

	static Capture typeArgCapture() {
		return compound(
				token("<"),
				compound(0, Integer.MAX_VALUE,
						regex(JavaScope.TYPE_ARG, Pattern.compile("\\w+")),
						compound(
								token(Default.KEYWORD, "extends", "super"),
								typeCapture()
						).setOptional(true),
						token(Default.SEPARATOR, ",").setOptional(true)
				),
				token(">")
		);
	}

	static Capture statementBlockCapture() {
		return new SelectionCapture(
				compound(
						token("{"),
						lazy(JavaHighlight::statementsCapture),
						token("}")
				),
				lazy(JavaHighlight::statementCapture)
		).setOptional(true);
	}

	static Capture arrayLiteralCapture() {
		return compound(
				token("{"),
				makeJoin(
						lazy(JavaHighlight::expressionCapture),
						regex(Default.SEPARATOR, Pattern.compile(","))
				),
				token("}")
		);
	}

	static Capture constantLiteralCapture() {
		return new SelectionCapture(
				//STRING
				compound(
						token(Default.STRING, "\""),
						new SelectionCapture(0, Integer.MAX_VALUE,
								regex(
										Default.CONTROL,
										Pattern.compile("(\\\\[0-7]{3})|(\\\\u[0-9a-fA-F]{4})|(\\\\[0abtnvfre\\\\\"'])")
								),
								regex(Default.STRING, Pattern.compile("[^\"]+"))
						),
						token(Default.STRING, "\"")
				),
				//CHARACTER
				compound(
						token(Default.STRING, "'"),
						new SelectionCapture(
								regex(
										Default.CONTROL,
										Pattern.compile("(\\\\[0-7]{3})|(\\\\u[0-9a-fA-F]{4})|(\\\\[0abtnvfre\\\\\"'])")
								),
								regex(Default.STRING, Pattern.compile("[^']"))
						),
						token(Default.STRING, "'")
				),
				//NUMBER
				/*forks(
						compound(
								regex(NUMBER, Pattern.compile("[\\d_]+")),
								forks(
										compound(
												token(NUMBER, "."),
												regex(NUMBER, Pattern.compile("[\\d_]+([eE][+-]?[\\d_]+)?[fFdD]?"))
										),
										regex(NUMBER, Pattern.compile("[eE][+-]?[\\d_]+[fFdD]?"))
								)
						),
						regex(NUMBER, Pattern.compile("[\\d_]+[lL]?"))
				),*/
				regex(Default.NUMBER, Pattern.compile("[\\d_]+(\\.[\\d_]+)?([eE][\\d_]+)?[fFdDlL]?")),
				//BOOLEAN
				regex(Default.KEYWORD, Pattern.compile("true|false")),
				//NULL
				token(Default.KEYWORD, "null")
		);
	}

	static Capture metaExpCapture() {
		return compound(
				new SelectionCapture(
						arrayLiteralCapture(),
						//OPERATE
						compound(
								token("("),
								lazy(JavaHighlight::expressionCapture),
								token(")")
						),
						//SWITCH EXPRESSION
						compound(
								token(Default.KEYWORD, "switch"),
								token("("),
								lazy(JavaHighlight::expressionCapture),
								token(")"),
								token("{"),
								compound(0, Integer.MAX_VALUE,
										token(Default.KEYWORD, "case"),
										makeJoin(
												new SelectionCapture(
														constantLiteralCapture(),
														regex(Default.VARIABLE, Pattern.compile("\\w+"))
												),
												token(Default.SEPARATOR, ",")
										),
										token("->"),
										lazy(JavaHighlight::statementBlockCapture),
										token(Default.SEPARATOR, ";").setOptional(true)
								),
								token("}")
						),
						//LAMBDA
						compound(
								new SelectionCapture(
										compound(
												token("("),
												makeJoin(
														regex(Default.ARGUMENT, Pattern.compile("\\w+")),
														token(Default.SEPARATOR, ",")
												).setOptional(true),
												token(")")
										),
										regex(Default.ARGUMENT, Pattern.compile("\\w+"))
								),
								token("->"),
								statementBlockCapture()
						),
						//INVOKE
						compound(
								new SelectionCapture(
										regex(Default.KEYWORD, Pattern.compile("this|super")),
										regex(Default.FUNCTION_INVOKE, Pattern.compile("\\w+"))
								),
								compound(
										token("("),
										makeJoin(
												lazy(JavaHighlight::expressionCapture).setOptional(true),
												token(Default.SEPARATOR, ",")
										),
										token(")")
								)
						),
						//NEW
						compound(
								token(Default.KEYWORD, "new"),
								typeCaptureNonArray(),
								token("("),
								makeJoin(
										lazy(JavaHighlight::expressionCapture).setOptional(true),
										token(Default.SEPARATOR, ",")
								),
								token(")"),
								compound(
										token("{"),
										lazy(JavaHighlight::statementsCapture),
										token("}")
								).setOptional(true)
						),
						//NEW ARRAY
						compound(
								token(Default.KEYWORD, "new"),
								typeCaptureNonArray(),
								compound(1, Integer.MAX_VALUE,
										token("["),
										lazy(JavaHighlight::expressionCapture),
										token("]")
								),
								compound(0, Integer.MAX_VALUE,
										token("["),
										token("]")
								)
						),
						//NEW ARRAY LITERAL
						compound(
								token(Default.KEYWORD, "new"),
								typeCaptureNonArray(),
								compound(1, Integer.MAX_VALUE,
										token("["),
										token("]")
								),
								arrayLiteralCapture()
						),
						//READ ARRAY
						compound(
								regex(Default.VARIABLE, Pattern.compile("\\w+")),
								compound(1, Integer.MAX_VALUE,
										token("["),
										lazy(JavaHighlight::expressionCapture),
										token("]")
								)
						),
						//FUNC_REF
						compound(
								typeCaptureNonArray(),
								token(2, ":"),
								new SelectionCapture(
										token(Default.KEYWORD, "new"),
										regex(Default.FUNCTION_INVOKE, Pattern.compile("\\w+"))
								)
						),
						//CLASS REF
						compound(
								typeCapture(),
								token("."),
								token(Default.KEYWORD, "class")
						),
						//SINGLE OPERATOR
						compound(
								regex(Default.OPERATOR, Pattern.compile("[!+\\-~]")),
								lazy(JavaHighlight::expressionCapture)
						),
						constantLiteralCapture(),
						//THIS_SUPER
						regex(Default.KEYWORD, Pattern.compile("this|super")),
						//REF
						regex(Default.VARIABLE, Pattern.compile("\\w+"))
				),
				//INSTANCEOF
				compound(
						token(Default.KEYWORD, "instanceof"),
						typeCapture(),
						regex(Default.VARIABLE, Pattern.compile("\\w+")).setOptional(true)
				).setOptional(true)
		);
	}

	static Capture expressionCapture() {
		return makeJoin(
				compound(
						metaExpCapture(),
						regex(Default.OPERATOR, Pattern.compile("\\+\\+|--")).setOptional(true)
				),
				regex(Default.OPERATOR, Pattern.compile("(->|!=|==|<=|>=|&&|\\|\\||\\+=|-=|\\*=|/=|%=|&=|\\|=|\\^=|<<=|>>=|>>>=)|[=.+\\-*/%&|<>^]"))
		);
	}

	static Capture statementCapture() {
		return new SelectionCapture(
				//IF
				compound(
						token(Default.KEYWORD, "if"),
						token("("),
						expressionCapture(),
						token(")"),
						statementBlockCapture(),
						compound(
								token(Default.KEYWORD, "else"),
								statementBlockCapture()
						).setOptional(true)
				),
				//SWITCH
				compound(
						token(Default.KEYWORD, "switch"),
						token("("),
						expressionCapture(),
						token(")"),
						token("{"),
						compound(0, Integer.MAX_VALUE,
								token(Default.KEYWORD, "case"),
								makeJoin(
										new SelectionCapture(
												constantLiteralCapture(),
												regex(Default.VARIABLE, Pattern.compile("\\w+"))
										),
										token(Default.SEPARATOR, ",")
								),
								token("->"),
								statementBlockCapture(),
								token(Default.SEPARATOR, ";").setOptional(true)
						),
						token("}")
				),
				//WHILE
				compound(
						compound(
								regex(Pattern.compile("\\w+")),
								token(":")
						).setOptional(true),
						token(Default.KEYWORD, "while"),
						token("("),
						expressionCapture(),
						token(")"),
						statementBlockCapture()
				),
				//DO_WHILE
				compound(
						compound(
								regex(Pattern.compile("\\w+")),
								token(":")
						).setOptional(true),
						token(Default.KEYWORD, "do"),
						statementBlockCapture(),
						token(Default.KEYWORD, "while"),
						token("("),
						expressionCapture(),
						token(")")
				),
				//FOR
				compound(
						compound(
								regex(Pattern.compile("\\w+")),
								token(":")
						).setOptional(true),
						token(Default.KEYWORD, "for"),
						token("("),
						compound(
								typeCapture(),
								makeJoin(
										compound(
												regex(Default.VARIABLE, Pattern.compile("\\w+")),
												compound(
														token(Default.OPERATOR, "="),
														expressionCapture()
												).setOptional(true)
										),
										token(Default.SEPARATOR, ",")
								)
						).setOptional(true),
						token(Default.SEPARATOR, ";"),
						expressionCapture().setOptional(true),
						token(Default.SEPARATOR, ";"),
						makeJoin(
								expressionCapture(),
								token(Default.SEPARATOR, ",")
						),
						token(")"),
						statementBlockCapture()
				),
				//FOR_EACH
				compound(
						compound(
								regex(Pattern.compile("\\w+")),
								token(":")
						).setOptional(true),
						token(Default.KEYWORD, "for"),
						token("("),
						typeCapture(),
						regex(Default.VARIABLE, Pattern.compile("\\w+")),
						token(":"),
						expressionCapture(),
						token(")"),
						statementBlockCapture()
				),
				//TRY_CATCH
				compound(
						token(Default.KEYWORD, "try"),
						token("{"),
						lazy(JavaHighlight::statementsCapture),
						token("}"),
						token("("),
						makeJoin(
								typeCaptureNonArray(),
								token(Default.SEPARATOR, "|")
						),
						token(")"),
						token("{"),
						lazy(JavaHighlight::statementsCapture),
						token("}")
				),
				//LOCAL_VARIABLE
				compound(
						annotationCapture(),
						modifiersCapture(),
						typeCapture(),
						makeJoin(
								compound(
										regex(Default.VARIABLE, Pattern.compile("\\w+")),
										compound(
												token(Default.OPERATOR, "="),
												expressionCapture()
										).setOptional(true)
								),
								token(Default.SEPARATOR, ",")
						),
						token(Default.SEPARATOR, ";")
				),
				//BREAK-CONTINUE
				compound(
						token(Default.KEYWORD, "break", "continue"),
						regex(Pattern.compile("\\w+")).setOptional(true)
				),
				//RETURN-YIELD
				compound(
						token(Default.KEYWORD, "return", "yield"),
						expressionCapture().setOptional(true)
				),
				//THROW
				compound(
						token(Default.KEYWORD, "throw"),
						expressionCapture()
				),
				//EXPRESSION
				expressionCapture(),
				//CODE_BLOCK
				compound(
						compound(
								regex(Pattern.compile("\\w+")),
								token(":")
						).setOptional(true),
						token("{"),
						lazy(JavaHighlight::statementsCapture),
						token("}")
				)
		);
	}

	static Capture statementsCapture() {
		return compound(0, Integer.MAX_VALUE,
				statementCapture(),
				token(Default.SEPARATOR, ";").setOptional(true)
		);
	}

	public static PatternsHighlight create() {
		PatternsHighlight res = new PatternsHighlight("java");

		res.tokensSplit = Pattern.compile("\\s+");
		res.rawTokenMatcher = Pattern.compile("//.*|/\\*(\\s|.)*?\\*/");
		res.symbolMatcher = Pattern.compile(
				"(->|!=|==|<=|>=|&&|\\|\\||\\+\\+|--|\\+=|-=|\\*=|/=|%=|&=|\\|=|\\^=|<<=|>>=|>>>=)" +
				"|([\\d_]+(\\.[\\d_]+)?([eE][\\d_]+)?[fFdDlL]?)" +
				"|(\\\\[0-7]{3})" +
				"|(\\\\u[0-9a-fA-F]{4})" +
				"|(\\\\[0abtnvfre\\\\\"'])" +
				"|[\\\\.+\\-*/%&|!<>~^=,;:(){}\"'\\[\\]]"
		);

		res
				//RAW CONTEXT
				.addRawContextPattern("line_comment", block(Default.COMMENT,
						of(token(2, "/")),
						of(line(Default.COMMENT))
				))
				.addRawContextPattern("javadoc",
						block(JavaScope.DOCS,
								of(token("/"), token(2, "*")),
								of(token("*"), token(JavaScope.DOCS, "/"))
						).addChildPattern("mark", serial(regex(JavaScope.DOC_MARK, Pattern.compile("@\\w+"))))
				)
				.addRawContextPattern("block_comment", block(Default.COMMENT,
						of(token(Default.COMMENT, "/"), token(Default.COMMENT, "*")),
						of(token(Default.COMMENT, "*"), token(Default.COMMENT, "/"))
				))

				//TOKENS CONTEXT
				.addPattern("keywords", serial(-100, token(
						Default.KEYWORD,
						"public", "protected", "private",
						"static", "final", "synchronized", "volatile", "transient",
						"strictfp", "abstract", "native", "default",
						"class", "interface", "enum",
						"extends", "implements",
						"package", "import",
						"super", "this",
						"new", "return", "instanceof",
						"throw", "throws",
						"try", "catch", "finally",
						"do", "while", "for", "switch", "case", "break", "continue",
						"if", "else",
						"int", "long", "short", "byte", "char", "boolean", "float", "double", "void",
						"null", "true", "false"
				)))
				//STATEMENT
				.addPattern("statement", serial(-200, statementCapture()))

				//STRUCT
				.addPattern("package", serial(
						token(Default.KEYWORD, "package"),
						typeCaptureNonArray(),
						token(Default.SEPARATOR, ";")
				))
				.addPattern("import", serial(
						token(Default.KEYWORD, "import"),
						token(Default.KEYWORD, "static").setOptional(true),
						typeCaptureNonArray(),
						compound(
								token(Default.TYPE, "."),
								token("*")
						).setOptional(true),
						token(Default.SEPARATOR, ";")
				))
				.addPattern("type_decl",
						block(
								of(
										annotationCapture(),
										modifiersCapture(),
										token(Default.KEYWORD, "class", "interface", "enum"),
										compound(
												regex(Default.TYPE, Pattern.compile("\\w+")),
												typeArgCapture().setOptional(true)
										),
										compound(
												token(Default.KEYWORD, "extends"),
												typeCapture()
										).setOptional(true),
										compound(
												token(Default.KEYWORD, "implements"),
												makeJoin(typeCapture(), token(Default.SEPARATOR, ","))
										).setOptional(true),
										token("{")
								),
								of(token("}"))
						).addChildPattern("constructor", serial(
								annotationCapture(),
								modifiersCapture(),
								regex(JavaScope.CONSTRUCTOR, Pattern.compile("\\w+")),
								compound(
										token("("),
										makeJoin(
												compound(
														annotationCapture(),
														token(Default.KEYWORD, "final").setOptional(true),
														typeCapture(),
														regex(Default.ARGUMENT, Pattern.compile("\\w+"))
												),
												token(Default.SEPARATOR, ",")
										).setOptional(true),
										compound(
												token(Default.SEPARATOR, ",").setOptional(true),
												token(Default.KEYWORD, "final").setOptional(true),
												annotationCapture(),
												typeCapture(),
												token(3, Default.ARGUMENT, "."),
												regex(Default.ARGUMENT, Pattern.compile("\\w+"))
										).setOptional(true),
										token(")")
								),
								compound(
										token("{"),
										statementsCapture(),
										token("}")
								)
						)).addChildPattern("inner_class", reference(res))
				)
				.addPattern("annotation_arguments",
						block(JavaScope.ANNOTATION,
								of(regex(Pattern.compile("@\\w+")), token("(")),
								of(token(")"))
						).addChildPattern("arg", serial(
								regex(Default.ARGUMENT, Pattern.compile("\\w+")),
								token(Default.OPERATOR, "="),
								expressionCapture()
						)).addChildPattern("value", reference(res))
				)
				.addPattern("annotation", serial(
						regex(JavaScope.ANNOTATION, Pattern.compile("@\\w+"))
				))
				.addPattern("function", serial(
						annotationCapture(),
						modifiersCapture(),
						typeArgCapture().setOptional(true),
						typeCapture(),
						regex(Default.FUNCTION, Pattern.compile("\\w+")),
						compound(
								token("("),
								makeJoin(
										compound(
												annotationCapture(),
												token(Default.KEYWORD, "final").setOptional(true),
												typeCapture(),
												regex(Default.ARGUMENT, Pattern.compile("\\w+"))
										),
										token(Default.SEPARATOR, ",")
								).setOptional(true),
								compound(
										token(Default.SEPARATOR, ",").setOptional(true),
										token(Default.KEYWORD, "final").setOptional(true),
										annotationCapture(),
										typeCapture(),
										token(3, Default.ARGUMENT, "."),
										regex(Default.ARGUMENT, Pattern.compile("\\w+"))
								).setOptional(true),
								token(")")
						),
						compound(
								token(Default.KEYWORD, "throws"),
								makeJoin(
										typeCaptureNonArray(),
										token(Default.SEPARATOR, ",")
								)
						).setOptional(true),
						compound(
								token("{"),
								statementsCapture(),
								token("}")
						).setOptional(true)
				))
				.addPattern("variable", serial(
						annotationCapture(),
						modifiersCapture(),
						typeCapture(),
						makeJoin(
								compound(
										regex(JavaScope.FIELD, Pattern.compile("\\w+")),
										compound(
												token(Default.OPERATOR, "="),
												expressionCapture()
										).setOptional(true)
								),
								token(Default.SEPARATOR, ",")
						),
						token(Default.SEPARATOR, ";")
				));

		return res;
	}
}
