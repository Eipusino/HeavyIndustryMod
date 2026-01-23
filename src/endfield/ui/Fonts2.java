package endfield.ui;

import arc.Core;
import arc.assets.AssetDescriptor;
import arc.assets.AssetManager;
import arc.files.Fi;
import arc.freetype.FreeTypeFontGenerator;
import arc.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import arc.freetype.FreeTypeFontGeneratorLoader;
import arc.freetype.FreetypeFontLoader;
import arc.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import arc.graphics.g2d.Font;
import arc.struct.Seq;
import mindustry.Vars;

import static endfield.Vars2.MOD_NAME;

public final class Fonts2 {
	public static Font consolas, inconsoiata, jetbrainsmonomedium;

	public static final String loaderSuffix = "." + MOD_NAME +".gen";

	/** Don't let anyone instantiate this class. */
	private Fonts2() {}

	public static void load() {
		Core.assets.setLoader(FreeTypeFontGenerator.class, loaderSuffix, new FreeTypeFontGeneratorLoader(Vars.tree) {
			@Override
			public FreeTypeFontGenerator load(AssetManager assetManager, String fileName, Fi file, FreeTypeFontGeneratorParameters parameter) {
				return new FreeTypeFontGenerator(resolve(fileName.substring(0, fileName.length() - loaderSuffix.length())));
			}
		});

		Core.assets.setLoader(Font.class, "-" + MOD_NAME, new FreetypeFontLoader(Vars.tree) {
			@Override
			public Font loadSync(AssetManager manager, String fileName, Fi file, FreeTypeFontLoaderParameter parameter) {
				if (parameter == null)
					throw new IllegalArgumentException("FreetypeFontParameter must be set in AssetManager#load to point at a TTF file!");
				return manager
						.get(parameter.fontFileName + loaderSuffix, FreeTypeFontGenerator.class)
						.generateFont(parameter.fontParameters);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public Seq<AssetDescriptor> getDependencies(String fileName, Fi file, FreeTypeFontLoaderParameter parameter) {
				return Seq.with(new AssetDescriptor<>(parameter.fontFileName + loaderSuffix, FreeTypeFontGenerator.class));
			}
		});

		Core.assets.load("consolas-" + MOD_NAME, Font.class, new FreeTypeFontLoaderParameter("fonts/consolas.ttf", new FreeTypeFontParameter() {{
			size = 20;
			incremental = true;
			renderCount = 1;
		}})).loaded = f -> {
			f.setFixedWidthGlyphs(FreeTypeFontGenerator.DEFAULT_CHARS);
			consolas = f;
		};

		Core.assets.load("inconsoiata-" + MOD_NAME, Font.class, new FreeTypeFontLoaderParameter("fonts/inconsoiata.ttf", new FreeTypeFontParameter() {{
			size = 20;
			incremental = true;
			renderCount = 1;
		}})).loaded = f -> {
			f.setFixedWidthGlyphs(FreeTypeFontGenerator.DEFAULT_CHARS);
			inconsoiata = f;
		};

		Core.assets.load("jetbrainsmonomedium-" + MOD_NAME, Font.class, new FreeTypeFontLoaderParameter("fonts/jetbrainsmonomedium.ttf", new FreeTypeFontParameter() {{
			size = 19;
			borderWidth = 0.3f;
			shadowOffsetY = 2;
			incremental = true;
			borderColor = color;
		}})).loaded = f -> {
			f.setFixedWidthGlyphs(FreeTypeFontGenerator.DEFAULT_CHARS);
			jetbrainsmonomedium = f;
		};
	}
}
