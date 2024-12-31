package heavyindustry.ai;

import mindustry.content.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.*;

public class MinerPointAI extends AIController {
    public boolean mining = true;
    public Tile ore;

    @Override
    public void updateMovement() {
        if (unit.stack.amount > 0 && checkDrop(ore)) {
            mining = false;
        }
        if (unit instanceof BuildingTetherc tether && tether.building() != null) {
            if (unit.mineTile != null && !unit.mineTile.within(unit, unit.type.mineRange)) {
                unit.mineTile(null);
            }

            Building build = tether.building();

            if (mining) {
                if (unit.stack.amount >= unit.type.itemCapacity) {
                    mining = false;
                } else {
                    if (ore != null) {
                        moveTo(ore, unit.type.mineRange / 2f, 20f);

                        if ((ore.block() == Blocks.air || ore.wallDrop() != null) && unit.within(ore, unit.type.mineRange)) {
                            unit.mineTile = ore;
                        }

                        if (!(ore.block() == Blocks.air || ore.wallDrop() != null)) {
                            mining = false;
                        }
                    }
                }
            } else {
                unit.mineTile = null;
                if (unit.stack.amount == 0) {
                    mining = true;
                    return;
                }
                if (unit.within(build, unit.type.range)) {
                    if (build.acceptStack(unit.item(), unit.stack.amount, unit) > 0) {
                        Call.transferItemTo(unit, unit.stack.item, unit.stack.amount, unit.x, unit.y, build);
                        unit.clearItem();
                        mining = true;
                    } else if (checkDrop(ore)) {
                        unit.clearItem();
                        mining = true;
                    }
                }

                circle(build, unit.type.range / 1.8f);
            }
        }
    }

    protected boolean checkDrop(Tile t) {
        if (t == null) return true;
        return t.solid() ? t.wallDrop() != unit.stack.item : t.drop() != unit.stack.item;
    }
}
