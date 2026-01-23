package endfield.graphics;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.Texture.TextureFilter;
import arc.graphics.Texture.TextureWrap;
import endfield.Vars2;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class Textures2 {
	public static Fi texturesDir = Vars2.internalTree.child("other").child("textures");

	/** Blank image placeholder, used in Kotlin to prevent {@code lateinit var}. */
	public static Texture white;

	public static Texture noise;
	public static Texture smooth, particle, darker, gaussian, median, armor;

	/** Don't let anyone instantiate this class. */
	private Textures2() {}

	@Internal
	public static void load() {
		noise = loadTexture(Core.files.internal("sprites/noise.png"), TextureFilter.linear, TextureWrap.repeat);

		white = new Texture(Pixmaps2.white);

		smooth = loadTexture(texturesDir.child("smooth-noise.png"), TextureFilter.linear, TextureWrap.repeat);
		particle = loadTexture(texturesDir.child("particle-noise.png"), TextureFilter.linear, TextureWrap.repeat);
		darker = loadTexture(texturesDir.child("darker-noise.png"), TextureFilter.linear, TextureWrap.repeat);
		gaussian = loadTexture(texturesDir.child("gaussian-noise.png"), TextureFilter.linear, TextureWrap.repeat);
		median = loadTexture(texturesDir.child("median-noise.png"), TextureFilter.linear, TextureWrap.repeat);
		armor = loadTexture(texturesDir.child("armor.png"), TextureFilter.nearest, TextureWrap.repeat);
	}

	public static Texture loadTexture(Fi file, TextureFilter filter, TextureWrap wrap) {
		Texture texture = new Texture(new Pixmap(file));
		texture.setFilter(filter);
		texture.setWrap(wrap);

		return texture;
	}
}
