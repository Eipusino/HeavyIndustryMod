package heavyindustry.graphics;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Log;
import heavyindustry.HVars;
import heavyindustry.util.Objects2;

import java.lang.reflect.Field;

public final class HRegions {
	public static Fi spritesDir = HVars.internalTree.child("sprites");

	public static AtlasRegion white;

	public static ObjectSet<Texture> textures;
	public static Seq<AtlasRegion> regions;
	public static ObjectMap<String, AtlasRegion> regionmap;

	private HRegions() {}

	public static void load() {
		white = create(HTextures.white, "white");
	}

	@SuppressWarnings("unchecked")
	public static void addAll() {
		try {
			Field texturesField = TextureAtlas.class.getDeclaredField("textures");
			Field regionsField = TextureAtlas.class.getDeclaredField("regions");
			Field regionmapField = TextureAtlas.class.getDeclaredField("regionmap");

			texturesField.setAccessible(true);
			regionsField.setAccessible(true);
			regionmapField.setAccessible(true);

			Objects2.requireNonNull(Core.atlas, "Core.atlas has not been initialized.");

			textures = (ObjectSet<Texture>) texturesField.get(Core.atlas);
			regions = (Seq<AtlasRegion>) regionsField.get(Core.atlas);
			regionmap = (ObjectMap<String, AtlasRegion>) regionmapField.get(Core.atlas);

			textures.add(HTextures.white);
			regions.add(white);
			regionmap.put(white.name, white);
		} catch (Exception e) {
			Log.err(e);
		}
	}

	public static AtlasRegion create(Texture texture, String name) {
		AtlasRegion region = new AtlasRegion();
		region.texture = texture;
		region.name = name;
		region.set(0, 0, texture.width, texture.height);
		return region;
	}
}
