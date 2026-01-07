package heavyindustry.graphics;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;
import arc.scene.style.Drawable;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import heavyindustry.HVars;
import heavyindustry.util.Reflects;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class HRegions {
	public static Fi spritesDir = HVars.internalTree.child("sprites");

	public static AtlasRegion white;

	// form arc.Core.atlas
	public static ObjectSet<Texture> textures;
	public static Seq<AtlasRegion> regions;
	public static ObjectMap<String, Drawable> drawables;
	public static ObjectMap<String, AtlasRegion> regionmap;
	public static ObjectMap<Texture, Pixmap> pixmaps;

	private HRegions() {}

	@Internal
	public static void load() {
		white = create(HTextures.white, "white");
	}

	@Internal
	public static void addAll() {
		textures = Core.atlas.getTextures();
		regions = Core.atlas.getRegions();
		regionmap = Core.atlas.getRegionMap();
		pixmaps = Core.atlas.getPixmaps();

		drawables = Reflects.get(TextureAtlas.class, "drawables", Core.atlas, ObjectMap::new);

		textures.add(HTextures.white);
		regions.add(white);
		regionmap.put(white.name, white);
	}

	@Contract(value = "_, _ -> new", pure = true)
	public static @NotNull AtlasRegion create(Texture texture, String name) {
		AtlasRegion region = new AtlasRegion();
		region.texture = texture;
		region.name = name;
		region.set(0, 0, texture.width, texture.height);
		return region;
	}
}
