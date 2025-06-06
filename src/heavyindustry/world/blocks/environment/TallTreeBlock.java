package heavyindustry.world.blocks.environment;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.TreeBlock;

public class TallTreeBlock extends TreeBlock {
	public TallTreeBlock(String name) {
		super(name);
	}

	@Override
	public void drawBase(Tile tile) {

		float
				x = tile.worldx(), y = tile.worldy(),
				rot = Mathf.randomSeed(tile.pos(), 0, 4) * 90 + Mathf.sin(Time.time + x, 50f, 0.5f) + Mathf.sin(Time.time - y, 65f, 0.9f) + Mathf.sin(Time.time + y - x, 85f, 0.9f),
				w = region.width * region.scl(), h = region.height * region.scl(),
				scl = 30f, mag = 0.2f;

		TextureRegion shad = variants == 0 ? customShadowRegion : variantShadowRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantShadowRegions.length - 1))];

		if (shad.found()) {
			Draw.z(Layer.power - 1);
			Draw.rect(shad, tile.worldx() + shadowOffset, tile.worldy() + shadowOffset, rot);
		}

		TextureRegion reg = variants == 0 ? region : variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))];

		Draw.z(Layer.darkness - 1);
		Draw.rectv(reg, x, y, w, h, rot, vec -> vec.add(
				Mathf.sin(vec.y * 3 + Time.time, scl, mag) + Mathf.sin(vec.x * 3 - Time.time, 70, 0.8f),
				Mathf.cos(vec.x * 3 + Time.time + 8, scl + 6f, mag * 1.1f) + Mathf.sin(vec.y * 3 - Time.time, 50, 0.2f)
		));
	}
}
