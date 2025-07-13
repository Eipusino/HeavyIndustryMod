package heavyindustry.graphics;

import arc.func.Cons;
import arc.graphics.Texture;
import arc.graphics.Texture.TextureFilter;
import arc.graphics.Texture.TextureWrap;

import static heavyindustry.HVars.internalTree;

public final class HTextures {
	public static Texture smooth, particle, darker, gaussian, median, armor;

	/** Don't let anyone instantiate this class. */
	private HTextures() {}

	public static void init() {
		smooth = loadTexture("smooth-noise");
		particle = loadTexture("particle-noise");
		darker = loadTexture("darker-noise");
		gaussian = loadTexture("gaussian-noise");
		median = loadTexture("median-noise");
		armor = loadTexture("armor", t -> {
			t.setFilter(TextureFilter.nearest);
			t.setWrap(TextureWrap.repeat);
		});
	}

	public static Texture loadTexture(String name) {
		return loadTexture(name, TextureFilter.linear, TextureWrap.repeat);
	}

	public static Texture loadTexture(String name, TextureFilter filter, TextureWrap wrap) {
		Texture texture = new Texture(internalTree.child("sprites").child(name + (name.endsWith(".png") ? "" : ".png")));
		texture.setFilter(filter);
		texture.setWrap(wrap);

		return texture;
	}

	public static Texture loadTexture(String name, Cons<Texture> modifier) {
		Texture texture = new Texture(internalTree.child("sprites").child(name + (name.endsWith(".png") ? "" : ".png")));
		modifier.get(texture);

		return texture;
	}
}
