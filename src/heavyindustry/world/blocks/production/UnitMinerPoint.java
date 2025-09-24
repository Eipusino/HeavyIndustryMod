package heavyindustry.world.blocks.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.struct.EnumSet;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.ai.MinerPointAI;
import heavyindustry.content.HUnitTypes;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.BuildingTetherc;
import mindustry.gen.Groups;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.net.Packet;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.ui.Fonts;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

/**
 * In fact, it continues the characteristics of Anuke's planet, but there may still be some bugs.
 * @author guiY
 */
public class UnitMinerPoint extends Block {
	/** Special exemption item that this miner can't mine. */
	public @Nullable Item blockedItem;

	/** Special exemption items that this miner can't mine. */
	public Seq<Item> blockedItems = new Seq<>(Item.class);

	public int range = 12;
	public int tier = 2;
	public int dronesCreated = 3;
	public float droneConstructTime = 60f * 5f;
	public float polyStroke = 1.8f, polyRadius = 8f;
	public int polySides = 6;
	public float polyRotateSpeed = 1f;
	public Color polyColor = new Color(0x92dd7eff);
	public boolean alwaysCons = false;
	public boolean limitSize = true;

	public UnitType minerUnit = HUnitTypes.miner;

	public boolean canPickUp = false;

	public UnitMinerPoint(String name) {
		super(name);

		size = 3;
		solid = true;
		update = true;
		hasItems = true;
		hasPower = true;
		configurable = true;
		copyConfig = false;
		sync = true;
		buildCostMultiplier = 0;
		flags = EnumSet.of(BlockFlag.factory);

		config(Integer.class, (UnitMinerPointBuild tile, Integer sort) -> tile.sort = sort);
	}

	@Override
	public void init() {
		super.init();

		if (blockedItem != null) {
			blockedItems.add(blockedItem);
		}
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);
		if (Vars.world.tile(x, y) != null && !canPlaceOn(Vars.world.tile(x, y), Vars.player.team(), rotation)) {
			drawPlaceText(Core.bundle.get((Vars.player.team().core() != null && Vars.player.team().core().items.has(requirements, Vars.state.rules.buildCostMultiplier)) || Vars.state.rules.infiniteResources ? "bar.hi-close" : "bar.noresources"), x, y, valid);
		}
		x *= Vars.tilesize;
		y *= Vars.tilesize;

		Drawf.dashSquare(Pal.accent, x, y, range * Vars.tilesize * 2);
	}

	public Rect getRect(Rect rect, float x, float y, float range) {
		rect.setCentered(x, y, range * 2 * Vars.tilesize);

		return rect;
	}

	@Override
	public boolean canPlaceOn(Tile tile, Team team, int rotation) {
		CoreBuild core = team.core();
		if (core == null || (!Vars.state.rules.infiniteResources && !core.items.has(requirements, Vars.state.rules.buildCostMultiplier)))
			return false;
		if (!limitSize) return true;
		Rect rect = getRect(Tmp.r1, tile.worldx() + offset, tile.worldy() + offset, range).grow(0.1f);
		return !Vars.indexer.getFlagged(team, BlockFlag.factory).contains(build -> {
			if (build instanceof UnitMinerPointBuild miner) {
				UnitMinerPoint block = (UnitMinerPoint) build.block;
				return getRect(Tmp.r2, miner.x, miner.y, block.range).overlaps(rect);
			}
			return false;
		});
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.add(Stat.drillTier, StatValues.blocks(block -> {
			if (block instanceof Floor floor) {
				return (floor.wallOre && floor.itemDrop != null && !blockedItems.contains(floor.itemDrop) && floor.itemDrop.hardness <= tier) ||
						(!floor.wallOre && floor.itemDrop != null && floor.itemDrop.hardness <= tier && !blockedItems.contains(floor.itemDrop) &&
								(Vars.indexer.isBlockPresent(floor) || Vars.state.isMenu()));
			} else if (block instanceof StaticWall wall) {
				return wall.itemDrop != null && !blockedItems.contains(wall.itemDrop) && wall.itemDrop.hardness <= tier;
			}
			return false;
		}));
		stats.add(Stat.range, range);
		stats.remove(Stat.buildTime);
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("units", (UnitMinerPointBuild tile) -> new Bar(
				() -> Core.bundle.format("bar.unitcap", Fonts.getUnicodeStr(minerUnit.name), tile.units.size, dronesCreated),
				() -> Pal.power,
				() -> (float) tile.units.size / dronesCreated));
	}

	@Override
	public boolean outputsItems() {
		return true;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = UnitMinerPointBuild::new;
	}

	public static class DroneSpawnedCallPacket extends Packet {
		public Tile tile;
		public int id;
		private byte[] data;

		public DroneSpawnedCallPacket() {
			data = NODATA;
		}

		@Override
		public void write(Writes write) {
			TypeIO.writeTile(write, tile);
			write.i(id);
		}

		@Override
		public void read(Reads read, int length) {
			data = read.b(length);
		}

		@Override
		public void handled() {
			BAIS.setBytes(data);
			tile = TypeIO.readTile(READ);
			id = READ.i();
		}

		@Override
		public void handleClient() {
			if (tile != null && tile.build instanceof UnitMinerPointBuild miner) {
				miner.spawned(id);
			}
		}
	}

	public class UnitMinerPointBuild extends Building {
		public @Nullable Tile sortTile = null;

		public int sort = -1;
		public int lastSort = -1;

		public Seq<Tile> tiles = new Seq<>(Tile.class);
		public Seq<Unit> units = new Seq<>(Unit.class);

		public float droneWarmup, powerWarmup;
		public float warmup, readyness;
		public float droneProgress, totalDroneProgress;

		public boolean placeInAir = false;

		protected IntSeq readUnits = new IntSeq();
		protected IntSeq whenSyncedUnits = new IntSeq();

		@Override
		public void updateTile() {
			if (sort != -1 && lastSort != sort) {
				lastSort = sort;
				sortTile = Vars.world.tile(sort);
			}
			if (sort == -1 && lastSort != sort) {
				lastSort = sort;
				sortTile = null;
			}
			if (sortTile != null && (!checkOre(sortTile) || !validOre(sortTile))) {
				sortTile = null;
				sort = -1;
			}

			dumpAccumulate();

			if (!readUnits.isEmpty()) {
				units.clear();
				readUnits.each(i -> {
					Unit unit = Groups.unit.getByID(i);
					if (unit != null) {
						units.add(unit);
					}
				});
				readUnits.clear();
			}

			//read newly synced drones on client end
			//same as UnitAssembler
			if (units.size < dronesCreated && whenSyncedUnits.size > 0) {
				whenSyncedUnits.each(id -> {
					Unit unit = Groups.unit.getByID(id);
					if (unit != null) {
						units.addUnique(unit);
					}
				});
			}

			units.removeAll(u -> !u.isAdded() || u.dead || !(u.controller() instanceof MinerPointAI));

			if (!allowUpdate()) {
				droneProgress = 0f;
				units.each(Unit::kill);
				units.clear();
			}

			float powerStatus = power == null ? 1f : power.status;
			powerWarmup = Mathf.lerpDelta(powerStatus, powerStatus > 0.0001f ? 1f : 0f, 0.1f);
			droneWarmup = Mathf.lerpDelta(droneWarmup, units.size < dronesCreated ? powerStatus : 0f, 0.1f);
			totalDroneProgress += droneWarmup * edelta();
			warmup = Mathf.approachDelta(warmup, efficiency, 1f / 60f);
			readyness = Mathf.approachDelta(readyness, units.size == dronesCreated ? 1f : 0f, 1f / 60f);

			if (units.size < dronesCreated && (droneProgress += edelta() * Vars.state.rules.unitBuildSpeed(team) * powerStatus / droneConstructTime) >= 1f) {
				if (!Vars.net.client()) {
					Unit unit = minerUnit.create(team);
					if (unit instanceof BuildingTetherc bt) {
						bt.building(this);
					}
					unit.set(x, y);
					unit.rotation = 90f;
					unit.add();
					units.add(unit);
					droneSpawned(tile, unit.id);
				}
			}

			if (units.size >= dronesCreated) {
				droneProgress = 0f;
			}
			for (int i = 0; i < units.size; i++) {
				Unit unit = units.get(i);

				if (unit.controller() instanceof MinerPointAI ai) {
					ai.ore = alwaysCons ? efficiency > 0.4 ? sortTile : null : sortTile;
				}
			}
		}

		protected void droneSpawned(Tile tile, int id) {
			if ((Vars.net.server() || !Vars.net.active()) && tile != null && tile.build instanceof UnitMinerPointBuild ti) ti.spawned(id);

			if (Vars.net.server()) {
				DroneSpawnedCallPacket packet = new DroneSpawnedCallPacket();
				packet.tile = tile;
				packet.id = id;
				Vars.net.send(packet, true);
			}
		}

		@Override
		public boolean canPickup() {
			return canPickUp;
		}

		@Override
		public void pickedUp() {
			if (canPickUp) {
				configure(-1);
				sortTile = null;
			}
		}

		@Override
		public void draw() {
			//same as UnitCargoLoader
			Draw.rect(block.region, x, y);
			if (units.size < dronesCreated) {
				Draw.draw(Layer.blockOver, () -> Drawf.construct(this, minerUnit.fullIcon, 0f, droneProgress, warmup, totalDroneProgress));
			} else {
				Draw.z(Layer.bullet - 0.01f);
				Draw.color(polyColor);
				Lines.stroke(polyStroke * readyness);
				Lines.poly(x, y, polySides, polyRadius, Time.time * polyRotateSpeed);
				Draw.reset();
				Draw.z(Layer.block);
			}
		}

		@Override
		public void drawConfigure() {
			super.drawConfigure();

			Drawf.dashSquare(Pal.accent, x, y, range * Vars.tilesize * 2);

			if (tiles.isEmpty() && !placeInAir) {
				findOre();
				if (tiles.isEmpty()) {
					placeInAir = true;
					return;
				}
			}

			if (tiles.any()) {
				float z = Draw.z();
				Draw.z(Layer.blockUnder - 2.5f);
				float sin = Mathf.absin(Time.time, 6, 0.8f);
				for (Tile t : tiles) {
					Item i = oreDrop(t);
					if (i == null || !validOre(t)) continue;
					Draw.color(Tmp.c1.set(i.color).a(sin));
					Fill.square(t.worldx(), t.worldy(), Vars.tilesize / 2f);
				}
				Draw.reset();
				Draw.z(z);
			}

			if (sortTile == null) return;
			Tmp.v1.set(sortTile.getX(), sortTile.getY()).sub(x, y).limit((size / 2f + 1) * Vars.tilesize + 0.5f);
			float xx = x + Tmp.v1.x, yy = y + Tmp.v1.y;
			int segs = (int) (dst(sortTile.getX(), sortTile.getY()) / Vars.tilesize);
			Lines.stroke(4f, Pal.gray);
			Lines.dashLine(xx, yy, sortTile.getX(), sortTile.getY(), segs);
			Lines.stroke(2f, Pal.accent);
			Lines.dashLine(xx, yy, sortTile.getX(), sortTile.getY(), segs);
			Drawf.square(sortTile.getX(), sortTile.getY(), 5, Pal.accent);
		}

		public void spawned(int id) {
			Fx.spawn.at(x, y);
			droneProgress = 0f;
			if (Vars.net.client()) {
				whenSyncedUnits.add(id);
			}
		}

		public boolean checkOre(Tile t) {
			return Mathf.equal(tile.x, t.x, range) && Mathf.equal(tile.y, t.y, range);
		}

		protected boolean validOre(Tile t) {
			return (t.solid() && t.wallDrop() != null && !blockedItems.contains(t.wallDrop()) && t.wallDrop().hardness <= tier) || (t.block() == Blocks.air && t.drop() != null && !blockedItems.contains(t.drop()) && t.drop().hardness <= tier);
		}

		protected Item oreDrop(Tile tile) {
			if (tile.solid() && tile.wallDrop() != null) return tile.wallDrop();
			if (tile.block() == Blocks.air && tile.drop() != null) return tile.drop();

			return null;
		}

		protected void findOre() {
			tiles.clear();

			int tx = tile.x, ty = tile.y;
			int tr = range;
			for (int x = -tr; x <= tr; x++) {
				for (int y = -tr; y <= tr; y++) {
					Tile other = Vars.world.tile(x + tx, y + ty);
					if (other != null && checkOre(other)) {
						tiles.add(other);
					}
				}
			}
		}

		@Override
		public int acceptStack(Item item, int amount, Teamc source) {
			return Math.min(itemCapacity - items.get(item), amount);
		}

		@Override
		public boolean onConfigureTapped(float x, float y) {
			Tile t = Vars.world.tileWorld(x, y);
			if (t != null && checkOre(t) && validOre(t)) {
				if (sort == t.pos()) {
					configure(-1);
				} else {
					configure(t.pos());
				}
				return true;
			}
			return false;
		}

		@Override
		public Object config() {
			return sort;
		}

		@Override
		public boolean shouldConsume() {
			return alwaysCons || units.size < dronesCreated;
		}

		@Override
		public float totalProgress() {
			return totalDroneProgress;
		}

		@Override
		public float progress() {
			return droneProgress;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(droneWarmup);
			write.f(droneProgress);
			write.b(units.size);
			for (Unit unit : units) {
				write.i(unit.id);
			}

			write.i(sortTile == null ? -1 : sortTile.pos());
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			droneWarmup = read.f();
			droneProgress = read.f();
			int count = read.b();
			readUnits.clear();
			for (int i = 0; i < count; i++) {
				readUnits.add(read.i());
			}
			whenSyncedUnits.clear();

			sort = read.i();
		}
	}
}
