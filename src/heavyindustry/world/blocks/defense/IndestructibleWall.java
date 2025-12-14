package heavyindustry.world.blocks.defense;

import mindustry.content.UnitTypes;
import mindustry.gen.BlockUnitc;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.world.blocks.ControlBlock;
import mindustry.world.blocks.defense.Wall;
import org.jetbrains.annotations.Nullable;

public class IndestructibleWall extends Wall {
	public IndestructibleWall(String name) {
		super(name);

		instantDeconstruct = true;
		placeableLiquid = true;
		absorbLasers = true;
		chanceDeflect = 1f;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = IndestructibleWallBuild::new;
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
