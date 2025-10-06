package heavyindustry.world.blocks.production;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.util.SpriteUtils;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.TileBitmask;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.meta.Attribute;

// Do you feel familiar? Yes, it has been added back now.
public class SporeFarmBlock extends Block {
	protected static final int frames = 5;

	public TextureRegion[] sporeRegions, groundRegions;
	public TextureRegion[] fenceRegions;
	public TextureRegion cageFloor;

	public float[] speed = {0.02f, 0.1f, 0.45f};

	public Liquid liquid = Liquids.water;
	public Attribute attribute = Attribute.spores;

	protected int timerGrowth;

	public SporeFarmBlock(String name) {
		super(name);

		hasItems = true;
		update = true;
		timerGrowth = timers++;
	}

	@Override
	public void load() {
		super.load();

		sporeRegions = SpriteUtils.split(name + "-spore", 32, 5, 1);
		groundRegions = SpriteUtils.split(name + "-ground", 32, 5, 1);

		fenceRegions = SpriteUtils.split(name + "-fence", 32, 12, 4);
		cageFloor = Core.atlas.find(name + "-floor");
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = SporeFarmBuild::new;
	}

	public class SporeFarmBuild extends Building {
		protected float growth, delay = -1;
		protected int tileIndex = 0;

		protected boolean randomChk() {
			Tile t = Vars.world.tile(tileX() + Mathf.range(3), tileY() + Mathf.range(3));

			return t != null && t.floor().liquidDrop == liquid;
		}

		@Override
		public void updateTile() {
			if (timer(timerGrowth, (60f + delay) * 5f)) {
				if (delay == -1) {
					delay = (tileX() * 89f + tileY() * 13f) % 21f;
				} else {
					boolean chk = randomChk();

					if (growth == 0f && !chk) return;
					growth += (chk ? growth > frames - 2 ? speed[1] : speed[2] : speed[0]) * (1 + sumAttribute(attribute, tile.x, tile.y));

					if (growth >= frames) {
						growth = frames - 1f;
						if (items.total() < 1) offload(Items.sporePod);
					}
					if (growth < 0f) growth = 0f;
				}
			}
			if (timer(timerDump, 15f)) dump(Items.sporePod);
		}

		@Override
		public void draw() {
			float rrot = (tileX() * 89f + tileY() * 13f) % 4f;
			float rrot2 = (tileX() * 69f + tileY() * 42f) % 4f;

			if (growth < frames - 0.5f) {
				Tile tiled = Vars.world.tileWorld(x, y);

				if (tiled != null && tiled.floor() != Blocks.air) {
					Floor floor = tiled.floor();
					floor.drawBase(tile);
				}

				Draw.rect(cageFloor, x, y);
			}

			if (growth != 0f) {
				Draw.rect(groundRegions[Mathf.floor(growth) & groundRegions.length], x, y, rrot * 90f);
				Draw.rect(sporeRegions[Mathf.floor(growth) & sporeRegions.length], x, y, rrot2 * 90f);
			}

			tileIndex = 0;
			for (int i = 0; i < 8; i++) {
				Tile other = tile.nearby(Geometry.d8[i]);
				if (other != null && other.block() == block) {
					tileIndex |= (1 << i);
				}
			}

			Draw.rect(fenceRegions[TileBitmask.values[tileIndex] & fenceRegions.length], x, y, 8f, 8f);
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
