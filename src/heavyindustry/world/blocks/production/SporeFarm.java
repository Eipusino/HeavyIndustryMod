package heavyindustry.world.blocks.production;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;

import static arc.Core.*;
import static heavyindustry.util.Utils.*;
import static mindustry.Vars.*;

/**
 * e.
 *
 * @author Eipusino
 */
public class SporeFarm extends Block {
	protected static final int frames = 5;

	public TextureRegion[] sporeRegions, groundRegions, fenceRegions;
	public TextureRegion cageFloor;
	/** Regarding the growth rate. */
	public float speed1 = 0.05f, speed2 = 0.15f, speed3 = 0.45f;
	/** Production time after growth. */
	public float dumpTime = 5f;
	/** If true, nearby floors need to contain growthLiquid in order to grow. */
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
		cageFloor = atlas.find(name + "-floor");
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
			Tile cTile = world.tile(tileX() + Mathf.range(3), tileY() + Mathf.range(3));

			return cTile != null && cTile.floor().liquidDrop == growthLiquid;
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
