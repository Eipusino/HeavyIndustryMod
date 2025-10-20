package heavyindustry.graphics;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Texture;
import arc.graphics.Texture.TextureFilter;
import arc.graphics.Texture.TextureWrap;
import heavyindustry.HVars;

public final class HTextures {
	public static Texture noise;
	public static Texture smooth, particle, darker, gaussian, median, armor;

	/** Don't let anyone instantiate this class. */
	private HTextures() {}

	public static void load() {
		noise = new Texture(Core.files.internal("sprites/noise.png"));
		noise.setFilter(TextureFilter.linear);
		noise.setWrap(TextureWrap.repeat);

		smooth = loadTexture("smooth-noise", TextureFilter.linear, TextureWrap.repeat);
		particle = loadTexture("particle-noise", TextureFilter.linear, TextureWrap.repeat);
		darker = loadTexture("darker-noise", TextureFilter.linear, TextureWrap.repeat);
		gaussian = loadTexture("gaussian-noise", TextureFilter.linear, TextureWrap.repeat);
		median = loadTexture("median-noise", TextureFilter.linear, TextureWrap.repeat);
		armor = loadTexture("armor", TextureFilter.nearest, TextureWrap.repeat);
	}

	public static Texture loadTexture(String name, TextureFilter filter, TextureWrap wrap) {
		Texture texture = new Texture(HVars.internalTree.children("other", "textures", name + ".png"));
		texture.setFilter(filter);
		texture.setWrap(wrap);

		return texture;
	}

	public static Texture loadTexture(String name, Cons<Texture> modifier) {
		Texture texture = new Texture(HVars.internalTree.children("other", "textures", name + ".png"));
		modifier.get(texture);

		return texture;
	}
}
