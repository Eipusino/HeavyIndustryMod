package heavyindustry.files;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;
import arc.graphics.g2d.TextureRegion;
import arc.util.Structs;

import java.util.HashMap;

import static heavyindustry.HVars.internalTree;
import static heavyindustry.core.HeavyIndustryMod.MOD_NAME;

public final class FileTree {
	public static final String[] filter = {"heavyindustry", "META-INF", "classes.dex"};

	public static final HashMap<String, Fi>
			rootDirectory = new HashMap<>(),
			sprites = new HashMap<>(),
			musics = new HashMap<>(),
			sounds = new HashMap<>(),
			shaders = new HashMap<>();

	public static void init() {
		for (Fi file : internalTree.root.list()) {
			if (!Structs.contains(filter, file.name()) && file.isDirectory()) {
				rootDirectory.put(file.name(), file);
			}
		}

		Fi spriteFile = rootDirectory.get("sprites");
		if (spriteFile != null) {
			spriteFile.findAll(fi -> fi.extension().equals("png")).each(sprite -> {
				sprites.put(sprite.name(), sprite);
			});
		}

		Fi musicsFile = rootDirectory.get("musics");
		if (musicsFile != null) {
			musicsFile.findAll(fi -> fi.extension().equals("ogg") || fi.extension().equals("mp3")).each(music -> {
				musics.put(music.name(), music);
			});
		}

		Fi soundsFile = rootDirectory.get("sounds");
		if (soundsFile != null) {
			soundsFile.findAll(fi -> fi.extension().equals("ogg") || fi.extension().equals("mp3")).each(sound -> {
				sounds.put(sound.name(), sound);
			});
		}

		Fi shadersFile = rootDirectory.get("shaders");
		if (shadersFile != null) {
			shadersFile.findAll(fi -> fi.extension().equals("vert") || fi.extension().equals("frag")).each(shader -> {
				shaders.put(shader.name(), shader);
			});
		}
	}

	private FileTree() {}

	public static AtlasRegion getRegion(String name) {
		Fi file = sprites.get(name);

		if (file == null) return Core.atlas.white();

		TextureRegion texture = new TextureRegion(new Texture(file));
		AtlasRegion atlas = new AtlasRegion(texture);
		atlas.name = MOD_NAME + "-" + name;
		return atlas;
	}
}
