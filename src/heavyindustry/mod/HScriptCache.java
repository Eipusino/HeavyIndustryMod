package heavyindustry.mod;

import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.TextureData;
import arc.graphics.gl.FileTextureData;
import arc.graphics.gl.PixmapTextureData;
import heavyindustry.util.ReflectUtils;

import java.lang.reflect.Field;

public final class HScriptCache {
	static final Field pixmapField;

	static {
		try {
			pixmapField = FileTextureData.class.getDeclaredField("pixmap");
			pixmapField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException();
		}
	}

	private HScriptCache() {}

	public static Pixmap pixmapOf(Texture texture) {
		TextureData data = texture.getTextureData();

		if (data instanceof PixmapTextureData ptd) return ptd.consumePixmap();
		if (data instanceof FileTextureData ftd) return ReflectUtils.getField(ftd, pixmapField);

		return null;
	}
}
