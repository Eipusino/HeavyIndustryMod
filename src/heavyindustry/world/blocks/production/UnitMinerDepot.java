package heavyindustry.world.blocks.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.content.HUnitTypes;
import heavyindustry.graphics.Drawe;
import heavyindustry.util.CollectionObjectMap;
import heavyindustry.util.holder.ObjectHolder;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.BuildingTetherc;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.ui.Fonts;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.UnitTetherBlock;
import mindustry.world.meta.BlockFlag;

public class UnitMinerDepot extends Block {
	public UnitType minerUnit = HUnitTypes.legsMiner;
	public float buildTime = 60f * 8f;

	public float polyStroke = 1.8f, polyRadius = 6.75f, polyRotateSpeed = 1f;
	public float polyStrokeScl = 1.75f, polyStrokeSclSpeed = 0.03f, polyStrokeTime = 120f;
	public int polySides = 6;
	public Color polyColor = new Color(0x92dd7eff);

	public UnitMinerDepot(String name) {
		super(name);

		solid = true;
		update = true;
		hasItems = true;
		configurable = true;
		clearOnDoubleTap = true;
		commandable = true;
		itemCapacity = 200;
		ambientSound = Sounds.respawning;
		flags = EnumSet.of(BlockFlag.drill); // Technically

		config(Item.class, (UnitMinerDepotBuild tile, Item item) -> {
			tile.targetItem = item;
			tile.commandPos = null;
			tile.targetSet = false;
		});
		configClear((UnitMinerDepotBuild tile) -> {
			tile.targetItem = null;
			tile.commandPos = null;
			tile.targetSet = false;
		});
	}

	@Override
	public void setBars() {
		super.setBars();

		addBar("units", (UnitMinerDepotBuild tile) -> new Bar(
				() -> Core.bundle.format("bar.unitcap", Fonts.getUnicodeStr(minerUnit.name), tile.team.data().countType(minerUnit), Units.getStringCap(tile.team)),
				() -> Pal.power,
				() -> (float) tile.team.data().countType(minerUnit) / Units.getCap(tile.team)
		));
	}

	@Override
	public boolean canPlaceOn(Tile tile, Team team, int rotation) {
		return super.canPlaceOn(tile, team, rotation) && Units.canCreate(team, minerUnit);
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);

		if (!Units.canCreate(Vars.player.team(), minerUnit)) {
			drawPlaceText(Core.bundle.get("bar.cargounitcap"), x, y, valid);
		}
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = UnitMinerDepotBuild::new;
	}

	public class UnitMinerDepotBuild extends Building implements UnitTetherBlock {
		//needs to be "unboxed" after reading, since units are read after buildings.
		public int readUnitId = -1;
		public float buildProgress, totalProgress;
		public float warmup, readyness, strokeScl;

		public @Nullable Unit unit;
		public @Nullable Vec2 commandPos;

		public @Nullable Item targetItem;

		public CollectionObjectMap<Item, Tile> oreTiles = new CollectionObjectMap<>(Item.class, Tile.class);
		public boolean oresFound, targetSet;

		@Override
		public void updateTile() {
			//unit was lost/destroyed
			if (unit != null && (unit.dead || !unit.isAdded())) {
				unit = null;
			}

			if (readUnitId != -1) {
				unit = Groups.unit.getByID(readUnitId);
				if (unit != null || !Vars.net.client()) {
					readUnitId = -1;
				}
			}

			warmup = Mathf.approachDelta(warmup, efficiency, 1f / 60f);
			readyness = Mathf.approachDelta(readyness, unit == null ? 0f : 1f, 1f / 60f);
			float scl = 1 + (1 - Interp.pow3Out.apply((Time.time % polyStrokeTime) / polyStrokeTime)) * (polyStrokeScl - 1);
			strokeScl = Mathf.approachDelta(strokeScl, scl, polyStrokeSclSpeed);

			if (!targetSet && targetItem != null && commandPos != null) {
				Tile t = Vars.world.tileWorld(commandPos.x, commandPos.y);
				if (t != null && oreDrop(t) == targetItem) {
					oreTiles.put(targetItem, t);
					targetSet = true;
					if (unit != null) unit.mineTile = null;
				}
			}

			if (!oresFound) {
				oresFound = true;
				minerUnit.mineItems.each(item -> {
					//is there a better way?
					Tile ore = oreTile(x, y, item);
					if (!oreTiles.containsKey(item) && ore != null) {
						oreTiles.put(item, ore);
					}
				});
			}

			if (unit == null && Units.canCreate(team, minerUnit)) {
				buildProgress += edelta() / buildTime;
				totalProgress += edelta();

				if (buildProgress >= 1f) {
					if (!Vars.net.client()) {
						unit = minerUnit.create(team);
						if (unit instanceof BuildingTetherc bt) {
							bt.building(this);
						}
						unit.set(x, y);
						unit.rotation = 90f;
						unit.add();
						Call.unitTetherBlockSpawned(tile, unit.id);
					}
				}
			}

			dump();
		}

		public Tile oreTile(Position pos, Item item) {
			return oreTile(pos.getX(), pos.getY(), item);
		}

		public Tile oreTile(float xp, float yp, Item item) {
			Tile floor = Vars.indexer.findClosestOre(xp, yp, item);
			Tile wall = Vars.indexer.findClosestWallOre(xp, yp, item);

			if (floor != null && wall != null) {
				return floor.dst2(xp, yp) < wall.dst2(xp, yp) ? floor : wall;
			} else if (floor != null) {
				return floor;
			} else {
				return wall;
			}
		}

		public Item oreDrop(Tile t) {
			if (t == null) return null;

			if (t.solid() && t.wallDrop() != null) return t.wallDrop();
			if (t.block() == Blocks.air && t.drop() != null) return t.drop();

			return null;
		}

		@Override
		public Vec2 getCommandPosition() {
			return commandPos;
		}

		@Override
		public void onCommand(Vec2 target) {
			commandPos = target;
			targetSet = false;
		}

		@Override
		public void spawned(int id) {
			Fx.spawn.at(x, y);
			buildProgress = 0f;
			if (Vars.net.client()) {
				readUnitId = id;
			}
		}

		@Override
		public boolean shouldConsume() {
			return unit == null;
		}

		@Override
		public void draw() {
			Draw.rect(block.region, x, y);
			if (unit == null) {
				Draw.draw(Layer.blockOver, () -> {
					Drawf.construct(this, minerUnit.fullIcon, 0f, buildProgress, warmup, totalProgress);
				});
			} else {
				Draw.z(Layer.bullet - 0.01f);
				Draw.color(polyColor);
				Lines.stroke(polyStroke * readyness * strokeScl);
				Lines.poly(x, y, polySides, polyRadius, Time.time * polyRotateSpeed);
				Draw.reset();
				Draw.z(Layer.block);
			}
		}

		@Override
		public void drawSelect() {
			super.drawSelect();

			if (targetItem != null) {
				Tile ore = oreTiles.get(targetItem);
				Drawe.targetLine(x, y, ore.worldx(), ore.worldy(), hitSize() / 1.4f + 1f, 8f / 2f, targetItem.color);
			}

			Draw.reset();
		}

		@Override
		public float totalProgress() {
			return totalProgress;
		}

		@Override
		public float progress() {
			return buildProgress;
		}

		@Override
		public int acceptStack(Item item, int amount, Teamc source) {
			return Math.min(itemCapacity - items.total(), amount);
		}

		@Override
		public void buildConfiguration(Table table) {
			Seq<Item> targets = minerUnit.mineItems.select(item -> Vars.indexer.hasOre(item) || Vars.indexer.hasWallOre(item));
			ItemSelection.buildTable(block, table, targets, () -> targetItem, this::configure);
		}

		@Override
		public Object config() {
			return targetItem;
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.i(unit == null ? -1 : unit.id);
			TypeIO.writeItem(write, targetItem);
			TypeIO.writeVecNullable(write, commandPos);

			write.i(oreTiles.size);
			for (ObjectHolder<Item, Tile> entry : oreTiles.iterator()) {
				write.s(entry.key.id);
				write.i(entry.value == null ? -1 : entry.value.pos());
			}
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			readUnitId = read.i();
			targetItem = TypeIO.readItem(read);
			commandPos = TypeIO.readVecNullable(read);

			int size = read.i();
			for (int i = 0; i < size; i++) {
				Item item = Vars.content.item(read.s());
				int pos = read.i();
				Tile ore = pos != -1 ? Vars.world.tile(pos) : null;
				if (item != null && ore != null) {
					oreTiles.put(item, ore);
				}
			}
		}
	}
}
