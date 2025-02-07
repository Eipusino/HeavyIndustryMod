package heavyindustry.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.Texture.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.noise.*;
import heavyindustry.content.*;
import mindustry.content.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class FlowMenuRenderer extends MenuRenderer {
	protected int viewWidth = !mobile ? 100 : 60;
	protected MenuSlide[] menus = {MenuSlides.stone, MenuSlides.grass};

	public float slideDuration = 60 * 40, transitionTime = 120, scrollSpeed = 1;

	protected float time;
	protected int width = !mobile ? 100 : 60, height = !mobile ? 50 : 40;
	protected int index = 0;

	public FlowMenuRenderer() {
		width += (int) Math.ceil((slideDuration * scrollSpeed) / 60) + 5;
		unityGenerate();
	}

	// causes method signature conflicts if i just named it "generate()"
	public void unityGenerate() {
		// shuffle the menus
		for (int i = menus.length - 1; i >= 0; i--) {
			int j = (int) Mathf.randomSeed(Time.nanos(), i);
			MenuSlide temp = menus[i];
			menus[i] = menus[j];
			menus[j] = temp;
		}

		Time.mark();
		for (MenuSlide menu : menus) {
			menu.generateWorld(width, height);
		}
		Log.info("Total " + Time.elapsed());
	}

	@Override
	public void render() {
		time += Time.delta;

		float in = Mathf.curve(time, 0, transitionTime / 2);
		float out = Mathf.curve(time, slideDuration - transitionTime / 2, slideDuration);
		float dark = in - out;

		menus[index].render(time, slideDuration, viewWidth, height);

		Draw.color(0, 0, 0, 1 - dark);
		Fill.crect(0, 0, Core.graphics.getWidth(), Core.graphics.getHeight());

		if (time > slideDuration) {
			if (index + 1 < menus.length) {
				index++;
			} else {
				index = 0;
			}
			time = 0;
		}
	}

	public static class MenuSlide implements Disposable {
		protected static final Rand rand = new Rand();

		static {
			rand.setSeed(Time.millis());
		}

		protected Mat mat = new Mat();
		protected Camera camera = new Camera();
		protected int cacheFloor, cacheWall;
		protected CacheBatch batch;
		protected FrameBuffer shadows;
		protected FrameBuffer dark;
		protected boolean doDark;
		protected int width, height;
		protected int seed;

		public MenuSlide(boolean dark) {
			doDark = dark;
		}

		protected static void setTile(int x, int y, Block wall, Block overlay, Block floor) {
			Tile tile;
			world.tiles.set(x, y, (tile = new CachedTile()));
			tile.x = (short) x;
			tile.y = (short) y;

			tile.setFloor((floor == null ? Blocks.stone : floor).asFloor());
			tile.setBlock(wall);
			tile.setOverlay(overlay);
		}

		protected int seed() {
			return rand.nextInt();
		}

		public void generateWorld(int wid, int hei) {
			width = wid;
			height = hei;
			seed = seed();

			Time.mark();
			world.beginMapLoad();

			// Effectively clear the entire world so generation doesnt overlap.
			world.tiles = new Tiles(wid, hei);

			shadows = new FrameBuffer(wid, hei);

			if (doDark) {
				dark = new FrameBuffer(wid, hei);
			}

			generate(world.tiles);

			world.endMapLoad();
			Log.info("Generated in " + Time.elapsed());
			cache();
		}

		protected void generate(Tiles tiles) {
			for (int x = 0; x < tiles.width; x++) {
				for (int y = 0; y < tiles.height; y++) {
					Block floor = Blocks.air;
					Block wall = Blocks.air;
					Block overlay = Blocks.air;

					setTile(x, y, wall, overlay, floor);
				}
			}
		}

		protected void cache() {
			// draw shadows
			Draw.proj().setOrtho(0, 0, shadows.getWidth(), shadows.getHeight());
			shadows.begin(Color.clear);
			Draw.color(Color.black);

			for (Tile tile : world.tiles) {
				if (tile.block() != Blocks.air) {
					Fill.rect(tile.x + 0.5f, tile.y + 0.5f, 1, 1);
				}
			}
			Draw.color();
			shadows.end();

			// draw darkness if enabled
			if (doDark) {
				Draw.proj().setOrtho(0, 0, dark.getWidth(), dark.getHeight());
				dark.begin(Color.clear);
				for (Tile tile : world.tiles) {
					float darkness = world.getDarkness(tile.x, tile.y);

					if (darkness > 0) {
						Draw.colorl(0.2f, Math.min((darkness + 0.5f) / 4f, 1f));
						Fill.rect(tile.x + 0.5f, tile.y + 0.5f, 1, 1);
					}
				}

				Draw.flush();
				Draw.color();
				dark.end();

				dark.getTexture().setFilter(TextureFilter.linear);
			}

			Batch prev = Core.batch;

			// draw floors and overlays
			Core.batch = batch = new CacheBatch(new SpriteCache(width * height * 6, false));
			batch.beginCache();

			for (Tile tile : world.tiles) {
				tile.floor().drawBase(tile);
				tile.overlay().drawBase(tile);
			}
			cacheFloor = batch.endCache();
			batch.beginCache();

			// draw walls
			for (Tile tile : world.tiles) {
				tile.block().drawBase(tile);
			}
			cacheWall = batch.endCache();

			Core.batch = prev;
		}

		public void render(float time, float duration, int viewWidth, int viewHeight) {
			float movement = ((width - viewWidth) * tilesize) / duration;
			float scaling = Math.max(Scl.scl(4f), Math.max(Core.graphics.getWidth() / ((viewWidth - 1f) * tilesize), Core.graphics.getHeight() / ((viewHeight - 1f) * tilesize)));
			camera.position.set(viewWidth * tilesize / 2f + movement * time, viewHeight * tilesize / 2f);
			camera.resize(Core.graphics.getWidth() / scaling,
					Core.graphics.getHeight() / scaling);

			mat.set(Draw.proj());
			Draw.flush();
			Draw.proj(camera);
			batch.setProjection(camera.mat);

			// render floors
			batch.beginDraw();
			batch.drawCache(cacheFloor);
			batch.endDraw();

			// render shadows
			Draw.color();
			Draw.rect(Draw.wrap(shadows.getTexture()),
					width * tilesize / 2f - 4f, height * tilesize / 2f - 4f,
					width * tilesize, -height * tilesize);
			Draw.flush();

			// render walls
			batch.beginDraw();
			batch.drawCache(cacheWall);
			batch.endDraw();

			// render darkness if enabled
			if (doDark) {
				Draw.color();
				Draw.rect(Draw.wrap(dark.getTexture()),
						width * tilesize / 2f - 4f, height * tilesize / 2f - 4f,
						width * tilesize, -height * tilesize);
				Draw.flush();
			}

			Draw.proj(mat);
			Draw.color(0f, 0f, 0f, 0.3f);
			Fill.crect(0, 0, Core.graphics.getWidth(), Core.graphics.getHeight());
			Draw.color();
		}

		@Override
		public void dispose() {
			batch.dispose();
			shadows.dispose();
		}
	}

	public static class MenuSlides {
		public static MenuSlide
		stone = new MenuSlide(false) {
			@Override
			protected void generate(Tiles tiles) {
				boolean tech = Mathf.chance(0.25);
				for (int x = 0; x < tiles.width; x++) {
					for (int y = 0; y < tiles.height; y++) {
						Block floor = Blocks.basalt;
						Block wall = Blocks.air;

						if (Simplex.noise2d(seed + 1, 3, 0.5, 1 / 20.0, x, y) > 0.4) {
							floor = Blocks.stone;
						}

						if (Simplex.noise2d(seed + 1, 3, 0.3, 1 / 20.0, x, y) > 0.5) {
							wall = Blocks.stoneWall;
						}

						if (tech) {
							int mx = x % 10, my = y % 10;
							int sclx = x / 10, scly = y / 10;
							if (Simplex.noise2d(seed + 2, 2, 1f / 10f, 1f, sclx, scly) > 0.6f && (mx == 0 || my == 0 || mx == 9 || my == 9)) {
								floor = Blocks.darkPanel3;
								if (Mathf.dst(mx, my, 5, 5) > 6f) {
									floor = Blocks.darkPanel4;
								}

								if (wall != Blocks.air && Mathf.chance(0.7)) {
									wall = Blocks.darkMetal;
								}
							}
						}

						setTile(x, y, wall, Blocks.air, floor);
					}
				}
			}
		},

		grass = new MenuSlide(false) {
			@Override
			protected void generate(Tiles tiles) {
				for (int x = 0; x < tiles.width; x++) {
					for (int y = 0; y < tiles.height; y++) {
						Block floor = Blocksf.concreteNumber;
						Block wall = Blocks.air;

						if (tiles.get(x, y) == null) {
							floor = Blocksf.concreteFill;
							if (Mathf.chance(0.1)) {
								floor = Blocksf.concreteFill;
							}
						}

						boolean c1, c2;
						c1 = x % 10 == 0;
						c2 = y % 10 == 0;
						if (c1 || c2) {
							floor = Blocksf.concreteStripe;

							if (c1 && c2) {
								setTile(x + 1, y + 1, Blocks.air, Blocks.air, Blocksf.concreteNumber);
								setTile(x + 2, y + 1, Blocks.air, Blocks.air, Blocksf.concreteNumber);
							}

							if (Simplex.noise2d(seed + 3, 2, 0.6, 1 / 22.0, x, y) > 0.5) {
								wall = Blocks.stoneWall;

								if (Simplex.noise2d(seed, 2, 0.6, 1 / 22.0, x, y) > 0.55) {
									wall = Blocks.shrubs;
								}
							}
						}

						if (Simplex.noise2d(seed, 2, 0.6, 1 / 22.0, x, y) > 0.5) {
							floor = Blocks.grass;

							if (Mathf.chance(0.09)) {
								wall = Blocks.pine;
							}
						}

						setTile(x, y, wall, Blocks.air, floor);
					}
				}
			}
		},

		warzone = new MenuSlide(true) {
			@Override
			protected void generate(Tiles tiles) {
				for (int x = 0; x < tiles.width; x++) {
					for (int y = 0; y < tiles.height; y++) {
						Block floor = Blocks.stone;
						Block wall = Blocks.air;

						setTile(x, y, wall, Blocks.air, floor);
					}
				}
			}
		};
	}
}
