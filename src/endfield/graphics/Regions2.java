package endfield.graphics;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import endfield.Vars2;

public final class Regions2 {
	public static Fi spritesDir = Vars2.internalTree.child("sprites");

	public static AtlasRegion white;

	// form arc.Core.atlas
	public static ObjectSet<Texture> textures;
	public static Seq<AtlasRegion> regions;
	public static ObjectMap<String, AtlasRegion> regionmap;
	public static ObjectMap<Texture, Pixmap> pixmaps;

	private Regions2() {}

	public static void load() {
		white = create(Textures2.white, "white");
	}

	public static void addAll() {
		textures = Core.atlas.getTextures();
		regions = Core.atlas.getRegions();
		regionmap = Core.atlas.getRegionMap();
		pixmaps = Core.atlas.getPixmaps();

		textures.add(Textures2.white);
		regions.add(white);
		regionmap.put(white.name, white);
	}

	public static AtlasRegion create(Texture texture, String name) {
		AtlasRegion region = new AtlasRegion();
		region.texture = texture;
		region.name = name;
		region.set(0, 0, texture.width, texture.height);
		return region;
	}
}
