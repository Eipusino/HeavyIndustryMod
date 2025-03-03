package heavyindustry.world.blocks.production;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.Tile;

import static heavyindustry.util.Utils.split;
import static mindustry.Vars.world;

/**
 * e.
 *
 * @author Eipusino
 */
public class SporeFarm extends Block {
	static final int frames = 5;
	static final byte[] tileMap = {
			39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
			38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
			39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
			38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
			3, 4, 3, 4, 15, 40, 15, 20, 3, 4, 3, 4, 15, 40, 15, 20,
			5, 28, 5, 28, 29, 10, 29, 23, 5, 28, 5, 28, 31, 11, 31, 32,
			3, 4, 3, 4, 15, 40, 15, 20, 3, 4, 3, 4, 15, 40, 15, 20,
			2, 30, 2, 30, 9, 47, 9, 22, 2, 30, 2, 30, 14, 44, 14, 6,
			39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
			38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
			39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
			38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
			3, 0, 3, 0, 15, 42, 15, 12, 3, 0, 3, 0, 15, 42, 15, 12,
			5, 8, 5, 8, 29, 35, 29, 33, 5, 8, 5, 8, 31, 34, 31, 7,
			3, 0, 3, 0, 15, 42, 15, 12, 3, 0, 3, 0, 15, 42, 15, 12,
			2, 1, 2, 1, 9, 45, 9, 19, 2, 1, 2, 1, 14, 18, 14, 13
	};

	public TextureRegion[] sporeRegions, groundRegions, fenceRegions;
	public TextureRegion cageFloor;
	/** Regarding the growth rate. */
	public float speed1 = 0.05f, speed2 = 0.15f, speed3 = 0.45f;
	/** Production time after growth. */
	public float dumpTime = 5f;
	/** If true, nearby floors need to contain growthLiquid to grow. */
	public boolean hasGrowthLiquid = true;
	/** The liquid required for growth. (Can it also be slag?) */
	public Liquid growthLiquid = Liquids.water;
	/** Output Item. (Can it also be phase-fabric?) */
	public Item dumpItem = Items.sporePod;

	protected int gTimer;

	public SporeFarm(String name) {
		super(name);

		update = true;
		gTimer = timers++;
	}

	@Override
	public void load() {
		super.load();
		sporeRegions = split(name + "-spore", 32, 0);
		groundRegions = split(name + "-ground", 32, 0);

		fenceRegions = split(name + "-fence", 32, 12, 4);
		cageFloor = Core.atlas.find(name + "-floor");
	}

	@Override
	protected TextureRegion[] icons() {
		return new TextureRegion[]{region};
	}

	public class SporeFarmBuild extends Building {
		public float growth, delay = -1f;
		public int tileIndex = -1;
		public boolean needsTileUpdate;

		public boolean randomChk() {
			Tile ct = world.tile(tileX() + Mathf.range(3), tileY() + Mathf.range(3));

			return ct != null && ct.floor().liquidDrop == growthLiquid;
		}

		public void updateTilings() {
			tileIndex = 0;

			for (int i = 0; i < 8; i++) {
				Tile other = tile.nearby(Geometry.d8(i));

				if (other == null || !(other.build instanceof SporeFarmBuild)) continue;
				tileIndex += 1 << i;
			}
		}

		public void updateNeighbours() {
			for (int i = 0; i < 8; i++) {
				Tile other = tile.nearby(Geometry.d8(i));

				if (other == null || !(other.build instanceof SporeFarmBuild b)) continue;
				b.needsTileUpdate = true;
			}
		}

		@Override
		public void onProximityRemoved() {
			super.onProximityRemoved();

			updateNeighbours();
		}

		@Override
		public void updateTile() {
			if (tileIndex == -1) {
				updateTilings();
				updateNeighbours();
			}
			if (needsTileUpdate) {
				updateTilings();
				needsTileUpdate = false;
			}
			if (timer(gTimer, (60f + delay) * 5f)) {
				if (delay == -1) {
					delay = (tileX() * 89f + tileY() * 13f) % 21f;
				} else {
					boolean chk = !hasGrowthLiquid || randomChk();

					growth += chk ? growth > frames - 2 ? speed2 : speed3 : speed1;

					if (growth >= frames) {
						growth = frames - 1f;
						if (items.total() < itemCapacity) offload(dumpItem);
					}
					if (growth < 0f) growth = 0f;
				}
			}
			if (timer(timerDump, dumpTime)) dump(dumpItem);
		}

		@Override
		public void draw() {
			float rrot = (tileX() * 89f + tileY() * 13f) % 4f;
			float rrot2 = (tileX() * 69f + tileY() * 42f) % 4f;

			if (growth < frames - 0.5f) {
				Draw.rect(cageFloor, x, y);
			}

			if (growth != 0f) {
				Draw.rect(groundRegions[Mathf.floor(growth)], x, y, rrot * 90f);
				Draw.rect(sporeRegions[Mathf.floor(growth)], x, y, rrot2 * 90f);
			}

			//Mainly to prevent terrible situations.
			Draw.rect(fenceRegions[tileMap[Math.max(tileIndex, 0)]], x, y, 8f, 8f);
			drawTeamTop();
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(growth);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			growth = read.f();
		}
	}
}
