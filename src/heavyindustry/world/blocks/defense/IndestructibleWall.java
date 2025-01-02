package heavyindustry.world.blocks.defense;

import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.defense.*;

public class IndestructibleWall extends Wall {
    public IndestructibleWall(String name) {
        super(name);

        instantDeconstruct = true;
        placeableLiquid = true;
        absorbLasers = true;
        chanceDeflect = 1f;
    }

    public class IndestructibleWallBuild extends WallBuild implements ControlBlock {
        public @Nullable BlockUnitc unit;

        @Override
        public void damage(float damage) {}

        @Override
        public float handleDamage(float amount) {
            return 0;
        }

        @Override
        public boolean collision(Bullet bullet) {
            super.collision(bullet);
            return true;
        }

        @Override
        public Unit unit() {
            if (unit == null) {
                unit = (BlockUnitc) UnitTypes.block.create(team);
                unit.tile(this);
            }
            return (Unit) unit;
        }

        @Override
        public boolean canControl() {
            return true;
        }
    }
}
