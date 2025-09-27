package heavyindustry.mod;

import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.gl.FileTextureData;
import arc.graphics.gl.PixmapTextureData;
import heavyindustry.HVars;
import heavyindustry.util.Reflects;
import heavyindustry.util.Unsafer;

import java.lang.reflect.Field;

public final class HScriptCache {
	static final Field pixmapField;

	static {
		try {
			pixmapField = FileTextureData.class.getDeclaredField("pixmap");
		} catch (NoSuchFieldException e) {
			throw new RuntimeException();
		}
	}

	private HScriptCache() {}

	public static Pixmap pixmapOf(Texture texture) {
		if (texture.getTextureData() instanceof PixmapTextureData ptd) {
			return ptd.consumePixmap();
		}
		if (texture.getTextureData() instanceof FileTextureData ftd) {
			return Reflects.getField(ftd, pixmapField);
		}
		return null;
	}
}
