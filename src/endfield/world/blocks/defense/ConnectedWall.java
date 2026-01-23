package endfield.world.blocks.defense;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import endfield.util.Sprites;
import mindustry.world.Tile;
import mindustry.world.blocks.TileBitmask;
import mindustry.world.blocks.defense.Wall;

public class ConnectedWall extends Wall {
	public TextureRegion[] autotileRegions;

	public ConnectedWall(String name) {
		super(name);
	}

	@Override
	public void load() {
		variants = 0;

		super.load();

		autotileRegions = Sprites.split(name + "-autotile", 32, 12, 4);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ConnectedWallBuild::new;
	}

	public class ConnectedWallBuild extends WallBuild {
		public int drawIndex = 0;

		@Override
		public void draw() {
			drawIndex = 0;
			for (int i = 0; i < 8; i++) {
				Tile other = tile.nearby(Geometry.d8[i]);
				if (other != null && other.block() == block && other.build != null && other.build.team == team) {
					drawIndex |= (1 << i);
				}
			}
			Draw.rect(autotileRegions[TileBitmask.values[drawIndex]], x, y);
		}
	}
}
