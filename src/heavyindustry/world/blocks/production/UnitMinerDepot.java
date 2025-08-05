package heavyindustry.world.blocks.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.content.HUnitTypes;
import heavyindustry.graphics.Drawe;
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

import static mindustry.Vars.content;
import static mindustry.Vars.indexer;
import static mindustry.Vars.net;
import static mindustry.Vars.player;
import static mindustry.Vars.world;

public class UnitMinerDepot extends Block {
	public UnitType minerUnit = HUnitTypes.legsMiner;
	public float buildTime = 60f * 8f;

	public float polyStroke = 1.8f, polyRadius = 6.75f, polyRotateSpeed = 1f;
	public float polyStrokeScl = 1.75f, polyStrokeSclSpeed = 0.03f, polyStrokeTime = 120f;
	public int polySides = 4;
	public Color polyColor = Pal.accent;

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
				() -> tile.team.data().countType(minerUnit) / Units.getCap(tile.team)
		));
	}

	@Override
	public boolean canPlaceOn(Tile tile, Team team, int rotation) {
		return super.canPlaceOn(tile, team, rotation) && Units.canCreate(team, minerUnit);
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);

		if (!Units.canCreate(player.team(), minerUnit)) {
			drawPlaceText(Core.bundle.get("bar.cargounitcap"), x, y, valid);
		}
	}

	public class UnitMinerDepotBuild extends Building implements UnitTetherBlock {
		//needs to be "unboxed" after reading, since units are read after buildings.
		public int readUnitId = -1;
		public float buildProgress, totalProgress;
		public float warmup, readyness, strokeScl;

		public @Nullable Unit unit;
		public @Nullable Vec2 commandPos;

		public @Nullable Item targetItem;

		public ObjectMap<Item, Tile> oreTiles = new ObjectMap<>();
		public boolean oresFound, targetSet;

		@Override
		public void updateTile() {
			//unit was lost/destroyed
			if (unit != null && (unit.dead || !unit.isAdded())) {
				unit = null;
			}

			if (readUnitId != -1) {
				unit = Groups.unit.getByID(readUnitId);
				if (unit != null || !net.client()) {
					readUnitId = -1;
				}
			}

			warmup = Mathf.approachDelta(warmup, efficiency, 1f / 60f);
			readyness = Mathf.approachDelta(readyness, unit != null ? 1f : 0f, 1f / 60f);
			float scl = 1 + (1 - Interp.pow3Out.apply((Time.time % polyStrokeTime) / polyStrokeTime)) * (polyStrokeScl - 1);
			strokeScl = Mathf.approachDelta(strokeScl, scl, polyStrokeSclSpeed);

			if (!targetSet && targetItem != null && commandPos != null) {
				Tile tiled = world.tileWorld(commandPos.x, commandPos.y);
				if (tiled != null && oreDrop(tiled) == targetItem) {
					oreTiles.put(targetItem, tiled);
					targetSet = true;
					if (unit != null) unit.mineTile = null;
				}
			}

			if (!oresFound) {
				oresFound = true;
				minerUnit.mineItems.each(item -> {
					//is there a better way?
					Tile floor = indexer.findClosestOre(x, y, item);
					Tile wall = indexer.findClosestWallOre(x, y, item);
					if (!oreTiles.containsKey(item)) {
						if (floor != null && wall != null) { //If there are both floor ore and wall ore, the block closest to it will be selected.
							oreTiles.put(item, floor.dst2(x, y) < wall.dst2(x, y) ? floor : wall);
						} else if (floor != null) {
							oreTiles.put(item, floor);
						} else if (wall != null) {
							oreTiles.put(item, wall);
						}
					}
				});
			}

			if (unit == null && Units.canCreate(team, minerUnit)) {
				buildProgress += edelta() / buildTime;
				totalProgress += edelta();

				if (buildProgress >= 1f) {
					if (!net.client()) {
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

		public Item oreDrop(Tile tiled) {
			if (tiled == null) return null;

			if (tiled.solid() && tiled.wallDrop() != null) return tiled.wallDrop();
			if (tiled.block() == Blocks.air && tiled.drop() != null) return tiled.drop();

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
			if (net.client()) {
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
			Seq<Item> targets = minerUnit.mineItems.select(item -> indexer.hasOre(item) || indexer.hasWallOre(item));
			ItemSelection.buildTable(block, table, targets, () -> targetItem, this::configure);
		}

		@Override
		public Item config() {
			return targetItem;
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.i(unit == null ? -1 : unit.id);
			TypeIO.writeItem(write, targetItem);
			TypeIO.writeVecNullable(write, commandPos);

			write.i(oreTiles.size);
			for (var entry : oreTiles.entries()) {
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
				Item item = content.item(read.s());
				int pos = read.i();
				Tile ore = pos != -1 ? world.tile(pos) : null;
				if (item != null && ore != null) {
					oreTiles.put(item, ore);
				}
			}
		}
	}
}
