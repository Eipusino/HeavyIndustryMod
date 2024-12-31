package heavyindustry.graphics;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.Texture.*;
import heavyindustry.core.*;

public final class HITextures {
    public static Texture smooth, particle, darker, armor;

    /** HITextures should not be instantiated. */
    private HITextures() {}

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
        armor = loadTexture("armor", t -> {
            t.setFilter(TextureFilter.nearest);
            t.setWrap(TextureWrap.repeat);
        });
    }

    public static Texture loadTexture(String name, Cons<Texture> modifier) {
        Texture tex = new Texture(HeavyIndustryMod.internalTree.child("sprites").child(name + (name.endsWith(".png") ? "" : ".png")));
        modifier.get(tex);

        return tex;
    }
}
