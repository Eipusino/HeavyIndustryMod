package heavyindustry.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Eachable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Conveyor;

import static heavyindustry.util.Utils.splitUnLayers;
import static mindustry.Vars.itemSize;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

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

		regions = splitUnLayers(Core.atlas.find(name + "-base"), 32);
		edgeRegions = splitUnLayers(Core.atlas.find(name + "-edge"), 32);
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
			return super.acceptItem(source, item) && (!noSideBlend || (source.block instanceof Conveyor || Edges.getFacingEdge(source.tile, tile).relativeTo(tile) == rotation));
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
