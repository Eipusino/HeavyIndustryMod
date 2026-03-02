package endfield.world.blocks.defense;

import mindustry.gen.Bullet;
import mindustry.world.blocks.defense.Wall;

public class IndestructibleWall extends Wall {
	public IndestructibleWall(String name) {
		super(name);

		instantDeconstruct = true;
		placeableLiquid = true;
		absorbLasers = true;
		chanceDeflect = 1f;
	}

	public class IndestructibleWallBuild extends WallBuild {
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
	}
}
