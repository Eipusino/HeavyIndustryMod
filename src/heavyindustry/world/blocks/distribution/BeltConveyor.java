package heavyindustry.world.blocks.distribution;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;

import static mindustry.Vars.*;

/**
 * Are you still troubled by the 20 sprites of traditional conveyor belts?
 * <p>This Type only sprites 3 textures to handle!
 *
 * @author Eipusino
 * @since 1.0.4
 */
public class BeltConveyor extends Conveyor {
	public TextureRegion[][] edgeRegions;

	public BeltConveyor(String name) {
		super(name);
	}

	@Override
	public void load() {
		region = Core.atlas.find(name);

		customShadowRegion = Core.atlas.find(name + "-shadow");

		//load specific team regions
		teamRegion = Core.atlas.find(name + "-team");

		teamRegions = new TextureRegion[Team.all.length];
		for (Team team : Team.all) {
			teamRegions[team.id] = teamRegion.found() && team.hasPalette ? Core.atlas.find(name + "-team-" + team.name, teamRegion) : teamRegion;
		}

		regions = Core.atlas.find(name + "-base").split(32, 32);
		edgeRegions = Core.atlas.find(name + "-edge").split(32, 32);
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		int[] bits = getTiling(plan, list);

		if (bits == null) return;

		TextureRegion conveyor = regions[0][bits[0]], edge = edgeRegions[0][bits[0]];
		for (TextureRegion i : new TextureRegion[]{conveyor, edge}) {
			Draw.rect(i, plan.drawx(), plan.drawy(), i.width * bits[1] * i.scl(), i.height * bits[2] * i.scl(), plan.rotation * 90);
		}
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region};
	}

	@Override
	public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
		return noSideBlend ? (otherblock.outputsItems() && blendsArmored(tile, rotation, otherx, othery, otherrot, otherblock)) || (lookingAt(tile, rotation, otherx, othery, otherblock) && otherblock.hasItems) : super.blends(tile, rotation, otherx, othery, otherrot, otherblock);
	}

	@Override
	public boolean blendsArmored(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
		return noSideBlend ? Point2.equals(tile.x + Geometry.d4(rotation).x, tile.y + Geometry.d4(rotation).y, otherx, othery) || ((!otherblock.rotatedOutput(otherx, othery) && Edges.getFacingEdge(otherblock, otherx, othery, tile) != null && Edges.getFacingEdge(otherblock, otherx, othery, tile).relativeTo(tile) == rotation) || (otherblock instanceof Conveyor && otherblock.rotatedOutput(otherx, othery) && Point2.equals(otherx + Geometry.d4(otherrot).x, othery + Geometry.d4(otherrot).y, tile.x, tile.y))) : super.blendsArmored(tile, rotation, otherx, othery, otherrot, otherblock);
	}

	public class BeltConveyorBuild extends ConveyorBuild {
		@Override
		public boolean acceptItem(Building source, Item item) {
			return noSideBlend ? super.acceptItem(source, item) && (source.block instanceof Conveyor || Edges.getFacingEdge(source.tile(), tile).relativeTo(tile) == rotation) : super.acceptItem(source, item);
		}

		@Override
		public void draw() {
			int frame = enabled && clogHeat <= 0.5f ? (int) (((Time.time * speed * 8f * timeScale * efficiency)) % 4) : 0;

			//draw extra conveyors facing this one for non-square tiling purposes
			Draw.z(Layer.blockUnder);
			for (int i = 0; i < 4; i++) {
				if ((blending & (1 << i)) != 0) {
					int dir = rotation - i;
					float rot = i == 0 ? rotation * 90 : (dir) * 90;

					Draw.rect(sliced(regions[frame][0], i != 0 ? SliceMode.bottom : SliceMode.top), x + Geometry.d4x(dir) * tilesize * 0.75f, y + Geometry.d4y(dir) * tilesize * 0.75f, rot);
					Draw.rect(sliced(edgeRegions[(tile.x + tile.y) % 2][0], i != 0 ? SliceMode.bottom : SliceMode.top), x + Geometry.d4x(dir) * tilesize * 0.75f, y + Geometry.d4y(dir) * tilesize * 0.75f, rot);
				}
			}

			Draw.z(Layer.block - 0.2f);

			Draw.rect(regions[frame][blendbits], x, y, tilesize * blendsclx, tilesize * blendscly, rotation * 90);
			Draw.rect(edgeRegions[(tile.x + tile.y) % 2][blendbits], x, y, tilesize * blendsclx, tilesize * blendscly, rotation * 90);

			Draw.z(Layer.block - 0.1f);
			float layer = Layer.block - 0.1f, width = world.unitWidth(), height = world.unitHeight(), scaling = 0.01f;

			for (int i = 0; i < len; i++) {
				Item item = ids[i];
				Tmp.v1.trns(rotation * 90, tilesize, 0);
				Tmp.v2.trns(rotation * 90, -tilesize / 2f, xs[i] * tilesize / 2f);

				float ix = (x + Tmp.v1.x * ys[i] + Tmp.v2.x), iy = (y + Tmp.v1.y * ys[i] + Tmp.v2.y);

				//keep draw position deterministic.
				Draw.z(layer + (ix / width + iy / height) * scaling);
				Draw.rect(item.fullIcon, ix, iy, itemSize, itemSize);
			}
		}
	}
}
