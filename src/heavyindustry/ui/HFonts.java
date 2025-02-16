package heavyindustry.ui;

import arc.*;
import arc.assets.*;
import arc.files.*;
import arc.freetype.*;
import arc.freetype.FreeTypeFontGenerator.*;
import arc.freetype.FreetypeFontLoader.*;
import arc.graphics.g2d.*;
import arc.struct.*;

import static mindustry.Vars.*;

@SuppressWarnings("rawtypes")
public final class HFonts {
	public static Font consolas, inconsoiata, jetbrainsmono;

	public static final String loaderSuffix = ".heavyindustry.gen";

	/** Don't let anyone instantiate this class. */
	private HFonts() {}

	public static void load() {
		Core.assets.setLoader(FreeTypeFontGenerator.class, loaderSuffix, new FreeTypeFontGeneratorLoader(tree) {
			@Override
			public FreeTypeFontGenerator load(AssetManager assetManager, String fileName, Fi file, FreeTypeFontGeneratorParameters parameter) {
				return new FreeTypeFontGenerator(resolve(fileName.substring(0, fileName.length() - loaderSuffix.length())));
			}
		});

		Core.assets.setLoader(Font.class, "-heavyindustry", new FreetypeFontLoader(tree) {
			@Override
			public Font loadSync(AssetManager manager, String fileName, Fi file, FreeTypeFontLoaderParameter parameter) {
				if (parameter == null)
					throw new IllegalArgumentException("FreetypeFontParameter must be set in AssetManager#load to point at a TTF file!");
				return manager
						.get(parameter.fontFileName + loaderSuffix, FreeTypeFontGenerator.class)
						.generateFont(parameter.fontParameters);
			}

			@Override
			public Seq<AssetDescriptor> getDependencies(String fileName, Fi file, FreeTypeFontLoaderParameter parameter) {
				return Seq.with(new AssetDescriptor<>(parameter.fontFileName + loaderSuffix, FreeTypeFontGenerator.class));
			}
		});

		Core.assets.load("consolas-heavyindustry", Font.class, new FreeTypeFontLoaderParameter("fonts/consolas.ttf", new FreeTypeFontParameter() {{
			size = 20;
			incremental = true;
			renderCount = 1;
			characters = FreeTypeFontGenerator.DEFAULT_CHARS;
		}})).loaded = f -> {
			f.setFixedWidthGlyphs(FreeTypeFontGenerator.DEFAULT_CHARS);
			consolas = f;
		};

		Core.assets.load("inconsoiata-heavyindustry", Font.class, new FreeTypeFontLoaderParameter("fonts/inconsoiata.ttf", new FreeTypeFontParameter() {{
			size = 20;
			incremental = true;
			renderCount = 1;
			characters = FreeTypeFontGenerator.DEFAULT_CHARS;
		}})).loaded = f -> {
			f.setFixedWidthGlyphs(FreeTypeFontGenerator.DEFAULT_CHARS);
			inconsoiata = f;
		};

		Core.assets.load("jetbrainsmono-heavyindustry", Font.class, new FreeTypeFontLoaderParameter("fonts/jetbrainsmono.ttf", new FreeTypeFontParameter() {{
			size = 22;
			incremental = true;
			renderCount = 1;
			characters = FreeTypeFontGenerator.DEFAULT_CHARS;
		}})).loaded = f -> {
			f.setFixedWidthGlyphs(FreeTypeFontGenerator.DEFAULT_CHARS);
			jetbrainsmono = f;
		};
	}
}
