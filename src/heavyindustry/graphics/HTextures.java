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
		smooth = loadTexture("smooth-noise", t -> {
			t.setFilter(TextureFilter.linear);
			t.setWrap(TextureWrap.repeat);
		});
		particle = loadTexture("particle-noise", t -> {
			t.setFilter(TextureFilter.linear);
			t.setWrap(TextureWrap.repeat);
		});
		darker = loadTexture("darker-noise", t -> {
			t.setFilter(TextureFilter.linear);
			t.setWrap(TextureWrap.repeat);
		});
		gaussian = loadTexture("gaussian-noise", t -> {
			t.setFilter(TextureFilter.linear);
			t.setWrap(TextureWrap.repeat);
		});
		median = loadTexture("median-noise", t -> {
			t.setFilter(TextureFilter.linear);
			t.setWrap(TextureWrap.repeat);
		});
		armor = loadTexture("armor", t -> {
			t.setFilter(TextureFilter.nearest);
			t.setWrap(TextureWrap.repeat);
		});
	}

	public static Texture loadTexture(String name, Cons<Texture> modifier) {
		Texture tex = new Texture(internalTree.child("sprites").child(name + (name.endsWith(".png") ? "" : ".png")));
		modifier.get(tex);

		return tex;
	}
}
