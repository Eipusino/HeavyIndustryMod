package heavyindustry.ai;

import arc.math.geom.Vec2;
import heavyindustry.world.blocks.production.UnitMinerDepot;
import mindustry.entities.units.AIController;
import mindustry.gen.BuildingTetherc;
import mindustry.gen.Call;
import mindustry.world.Tile;

import static mindustry.Vars.controlPath;
import static mindustry.Vars.indexer;

public class DepotMinerAI extends AIController {
	protected static final boolean[] noFound = {false};

	protected final Vec2 targetPos = new Vec2(), vecOut = new Vec2(), vecMovePos = new Vec2();

	public boolean mining = true;
	public Tile targetTile;

	@Override
	public void updateMovement() {
		if (!unit.canMine()) return;

		if (unit.mineTile != null && !unit.mineTile.within(unit, unit.type.mineRange)) {
			unit.mineTile(null);
		}

		UnitMinerDepot.UnitMinerDepotBuild home = home();

		if (home == null) return; //To prevent bad situations from happening

		if (mining) {
			if (home.targetItem == null || //No targetted item
					home.oreTiles.getNull(home.targetItem) == null || //No ore target tile
					unit.stack.amount >= unit.type.itemCapacity ||
					(home.targetItem != null && !unit.acceptsItem(home.targetItem)) || //Inventory full
					home.acceptStack(home.targetItem, 1, unit) == 0 //Depot is full
			) {
				mining = false;
			} else {
				//(When target not manually set) Closer ore was passed on the way to the original target, save it as new closer target.
				if (!home.targetSet && timer.get(timerTarget, 40)) {
					Tile ore = indexer.findClosestOre(unit, home.targetItem);
					if (ore != targetTile && unit.within(ore.worldx(), ore.worldy(), unit.type.mineRange)) {
						home.oreTiles.put(home.targetItem, ore);
					}
				}

				if (targetTile != home.oreTiles.get(home.targetItem)) {
					targetTile = home.oreTiles.get(home.targetItem);
					targetPos.set(targetTile.worldx(), targetTile.worldy());
				}

				if (unit.within(targetTile, unit.type.mineRange)) {
					unit.mineTile = targetTile;
				}
			}
		} else {
			unit.mineTile = null;

			if (unit.stack.amount == 0 && home.targetItem != null) {
				mining = true;
				return;
			}

			if (targetTile != home.tile) {
				targetTile = home.tile;
				targetPos.set(home);
			}

			if (unit.within(home, unit.type.range)) {
				if (home.acceptStack(unit.stack.item, unit.stack.amount, unit) > 0) {
					Call.transferItemTo(unit, unit.stack.item, unit.stack.amount, unit.x, unit.y, home);
				}

				unit.clearItem();
				mining = true;
			}
		}
		move();
		faceMovement();
		if (!unit.moving() && unit.mineTile != null) unit.lookAt(unit.mineTile);
	}

	public void move() {
		if (unit.type.flying) { //Unit flies, no need for pathfinding
			moveTo(targetPos, mining ? unit.type.mineRange / 2f : 0, 20f);
		} else {
			vecOut.set(targetPos);

			boolean move = controlPath.getPathPosition(unit, vecMovePos, targetPos, vecOut, noFound);
			if (move) {
				moveTo(vecOut, mining && unit.within(targetPos, unit.type.mineRange / 2) ? unit.type.mineRange : 0.5f, 8f);
			}
		}
	}

	public UnitMinerDepot.UnitMinerDepotBuild home() {
		if (unit instanceof BuildingTetherc bt && bt.building() instanceof UnitMinerDepot.UnitMinerDepotBuild build) {
			return build;
		}
		return null;
	}
}
