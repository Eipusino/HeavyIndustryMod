package heavyindustry.util;

import arc.Core;
import arc.files.Fi;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.Font.Glyph;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Vec2;
import arc.util.Log;
import arc.util.Scaling;
import arc.util.Strings;
import mindustry.ui.Fonts;
import org.jetbrains.annotations.Contract;

import java.io.Reader;
import java.util.Properties;

public final class IconLoader {
	private IconLoader() {}

	public static void loadIcons(Fi file) {
		if (!file.exists() || file.isDirectory()) {
			Log.warn("The path @ does not exist!", file.name());

			return;
		}

		Font[] availableFonts = new Font[]{Fonts.def, Fonts.outline};
		int fontSize = (int) (Fonts.def.getData().lineHeight / Fonts.def.getData().scaleY);

		Properties iconProperties = new Properties();
		try (Reader reader = file.reader(512)) {
			iconProperties.load(reader);
		} catch (Exception e) {
			Log.err(e);

			return;
		}

		for (var entry : iconProperties.entrySet()) {
			Object key = entry.getKey(), value = entry.getValue();
			if (key instanceof String codePointStr && value instanceof String getValue) {
				String[] valueParts = getValue.split("\\|");
				if (valueParts.length < 2) {
					continue;
				}

				int codePoint = Strings.parseInt(codePointStr, -1);

				if (codePoint == -1) continue;

				String textureName = valueParts[1];
				TextureRegion region = Core.atlas.find(textureName);

				Vec2 scaledSize = Scaling.fit.apply(region.width, region.height, fontSize, fontSize);
				Glyph glyph = constructGlyph(codePoint, region, scaledSize, fontSize);

				for (Font font : availableFonts) {
					font.getData().setGlyph(codePoint, glyph);
				}
			} else {
				Log.warn("Illegal property: " + key + "=" + value);
			}
		}
	}

	@Contract(value = "_, _, _, _ -> new", pure = true)
	static Glyph constructGlyph(int id, TextureRegion region, Vec2 size, int fontSize) {
		Glyph glyph = new Glyph();
		glyph.id = id;
		glyph.srcX = 0;
		glyph.srcY = 0;
		glyph.width = (int) size.x;
		glyph.height = (int) size.y;
		glyph.u = region.u;
		glyph.v = region.v2;
		glyph.u2 = region.u2;
		glyph.v2 = region.v;
		glyph.xoffset = 0;
		glyph.yoffset = -fontSize;
		glyph.xadvance = fontSize;
		glyph.fixedWidth = true;
		glyph.page = 0;
		return glyph;
	}
}

