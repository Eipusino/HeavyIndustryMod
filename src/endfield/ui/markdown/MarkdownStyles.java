package endfield.ui.markdown;

import arc.Core;
import arc.freetype.FreeTypeFontGenerator;
import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.Lines;
import arc.scene.style.BaseDrawable;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Scl;
import arc.util.Tmp;
import endfield.ui.Fonts2;
import endfield.ui.markdown.highlighter.Highlighter;
import endfield.ui.markdown.highlighter.Scope.Default;
import endfield.ui.markdown.highlighter.Scope.JavaScope;
import endfield.ui.markdown.highlighter.Scope.LuaScope;
import endfield.ui.markdown.highlighter.StandardLanguages;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;

public class MarkdownStyles {
	/**
	 * Default Markdown document style record object, with equal width font from JetBrains IDEs' Mono
	 * font. Thank you very much.
	 */
	public static final Markdown.MarkdownStyle defaultMD;

	static {
		defaultMD = makeDefault(Fonts2.jetbrainsmonomedium);
	}

	/** @deprecated Please use {@link #makeDefault(Font)}. */
	@Deprecated
	public static Markdown.MarkdownStyle defaultMD(Font mono) {
		return makeDefault(mono);
	}

	public static Markdown.MarkdownStyle makeDefault(Font mono) {
		return new Markdown.MarkdownStyle() {{
			font = subFont = Fonts.def;
			codeFont = mono;
			FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Core.files.internal("fonts/font.woff"));
			strongFont = gen.generateFont(new FreeTypeFontGenerator.FreeTypeFontParameter() {{
				size = (int) Scl.scl(19);
				borderWidth = Scl.scl(0.3f);
				shadowOffsetY = 2;
				incremental = true;
				borderColor = color;
			}});
			emFont = Fonts.def;

			textColor = Color.white;
			emColor = Pal.accent;
			subTextColor = Color.lightGray;
			lineColor = Color.gray;
			linkColor = Pal.place;

			linesPadding = 5;
			tablePadHor = 14;
			tablePadVert = 10;
			paragraphPadding = 14;

			board = Tex.paneLeft;
			codeBack = ((TextureRegionDrawable) Tex.whiteui).tint(Tmp.c1.set(Pal.darkerGray).a(0.7f));
			codeBack.setLeftWidth(4);
			codeBack.setRightWidth(4);
			codeBlockBack = ((TextureRegionDrawable) Tex.whiteui).tint(Tmp.c1.set(Pal.darkerGray));
			codeBlockStyle = Styles.smallPane;

			tableBack1 = ((TextureRegionDrawable) Tex.whiteui).tint(Tmp.c1.set(Pal.darkerGray).a(0.7f));
			tableBack2 = ((TextureRegionDrawable) Tex.whiteui).tint(Tmp.c1.set(Color.gray).a(0.7f));

			curtain = ((TextureRegionDrawable) Tex.whiteui).tint(Pal.darkerGray);

			listMarks = new Drawable[]{
					new BaseDrawable() {
						@Override
						public void draw(float x, float y, float width, float height) {
							Fill.square(x + width / 2, y + height / 2, width * 0.25f, 45f);
						}
					},
					new BaseDrawable() {
						@Override
						public void draw(float x, float y, float width, float height) {
							Fill.circle(x + width / 2, y + height / 2, width * 0.3f);
						}
					},
					new BaseDrawable() {
						@Override
						public void draw(float x, float y, float width, float height) {
							Lines.stroke(1);
							Lines.circle(x + width / 2, y + height / 2, width * 0.3f);
						}
					}
			};

			highlighter = new Highlighter().addLanguage(StandardLanguages.javaHighlight).addLanguage(StandardLanguages.luaHighlight);
			codeColorProvider = new ColorProvider();
			codeColorProvider.defaultColor = Color.lightGray;
			codeColorProvider.createMap("java")
					.put(new Color(0xffffff00), Default.SPACE)
					.put(new Color(0x676d9dff), Default.COMMENT)
					.put(new Color(0x676d9dff), Default.KEYWORD, Default.CONTROL, Default.SEPARATOR)
					.put(new Color(0x6897bbff), Default.NUMBER)
					.put(new Color(0xbe7032ff), Default.TYPE)
					.put(new Color(0x507874ff), JavaScope.TYPE_ARG)
					.put(new Color(0x6a8759ff), Default.STRING)
					.put(new Color(0xbbb529ff), JavaScope.ANNOTATION)
					.put(new Color(0x629755ff), JavaScope.DOCS, JavaScope.DOC_MARK)
					.put(new Color(0xffc66dff), Default.FUNCTION)
					.put(new Color(0x3c7893ff), JavaScope.CONSTRUCTOR)
					.put(new Color(0xbfbfbfff), Default.NONE, Default.FUNCTION_INVOKE, Default.ARGUMENT, Default.VARIABLE, Default.OPERATOR);
			codeColorProvider.createMap("lua")
					.put(new Color(0xffffff00), Default.SPACE)
					.put(new Color(0x7f7f7fff), Default.COMMENT)
					.put(new Color(0xc09df6ff), Default.KEYWORD)
					.put(new Color(0xc09df6ff), LuaScope.KEYWORD_BODY)
					.put(new Color(0xdbaad9ff), LuaScope.KEYWORD_CONTROL)
					.put(new Color(0xff7382ff), LuaScope.KEYWORD_SELF)
					.put(new Color(0xf89876ff), LuaScope.KEYWORD_VAR1)
					.put(new Color(0x6fb8ffff), LuaScope.KEYWORD_VAR2)
					.put(new Color(0x5fd3c0ff), LuaScope.TABLE_VARS)
					.put(new Color(0x9cd7f1ff), Default.CONTROL, Default.SEPARATOR, Default.OPERATOR)
					.put(new Color(0xee9c7cff), Default.NUMBER)
					.put(new Color(0xe47c8fff), Default.SEPARATOR)
					.put(new Color(0xf4c87cff), Default.ARGUMENT)
					.put(new Color(0xc5e59aff), Default.STRING)
					.put(new Color(0x82a3f2ff), Default.FUNCTION)
					.put(new Color(0x6fb8ffff), Default.FUNCTION_INVOKE)
					.put(new Color(0x82a3f2ff), LuaScope.KEYWORD_FUNCTION)
					.put(new Color(0xccd4f2ff), Default.NONE, Default.VARIABLE, LuaScope.LOCAL_VARS)
					.putProv(scope -> scope instanceof Default.RainbowSeparator, (Default.RainbowSeparator scope) -> switch (scope.depth) {
						case 0 -> new Color(0xfe767cff);
						case 1 -> new Color(0x8cdef5ff);
						case 2 -> new Color(0xf5a4dfff);
						case 3 -> new Color(0xc6e88dff);
						case 4 -> new Color(0xfc976aff);
						case 5 -> new Color(0x85aaffff);
						case 6 -> new Color(0xffc37aff);
						default -> Color.clear;
					});
			//.put(new Color(0xfe767cff), new RainbowSeparator(0, Default.SEPARATOR))
			//.put(new Color(0x8cdef5ff), new RainbowSeparator(1, Default.SEPARATOR))
			//.put(new Color(0xf5a4dfff), new RainbowSeparator(2, Default.SEPARATOR))
			//.put(new Color(0xc6e88dff), new RainbowSeparator(3, Default.SEPARATOR))
			//.put(new Color(0xfc976aff), new RainbowSeparator(4, Default.SEPARATOR))
			//.put(new Color(0x85aaffff), new RainbowSeparator(5, Default.SEPARATOR))
			//.put(new Color(0xffc37aff), new RainbowSeparator(6, Default.SEPARATOR));
		}};
	}
}
