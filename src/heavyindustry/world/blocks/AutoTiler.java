package heavyindustry.world.blocks;

import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Eachable;
import arc.util.Tmp;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.Autotiler;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public interface AutoTiler extends Autotiler {
	int[] blendResult = new int[5];
	BuildPlan[] directionals = new BuildPlan[4];

	@Override
	default TextureRegion sliced(TextureRegion input, SliceMode mode) {
		return mode == SliceMode.none ? input : mode == SliceMode.bottom ? botHalf(input) : topHalf(input);
	}

	@Override
	default TextureRegion topHalf(TextureRegion input) {
		TextureRegion region = Tmp.tr1;
		region.set(input);
		region.setWidth(region.width / 2);
		return region;
	}

	@Override
	default TextureRegion botHalf(TextureRegion input) {
		TextureRegion region = Tmp.tr1;
		region.set(input);
		int width = region.width;
		region.setWidth(width / 2);
		region.setX(region.getX() + width);
		return region;
	}

	@Override
	default int @Nullable [] getTiling(BuildPlan req, Eachable<BuildPlan> list) {
		if (req.tile() == null) return null;

		Arrays.fill(directionals, null);
		//TODO this is O(n^2), very slow, should use quadtree or intmap or something instead
		list.each(other -> {
			if (other.breaking || other == req) return;

			int i = 0;
			for (Point2 point : Geometry.d4) {
				int x = req.x + point.x, y = req.y + point.y;
				if (x >= other.x - (other.block.size - 1) / 2 && x <= other.x + (other.block.size / 2) && y >= other.y - (other.block.size - 1) / 2 && y <= other.y + (other.block.size / 2)) {
					directionals[i] = other;
				}
				i++;
			}
		});

		return buildBlending(req.tile(), req.rotation, directionals, req.worldContext);
	}

	@Override
	default int[] buildBlending(Tile tile, int rotation, BuildPlan[] directional, boolean world) {
		blendResult[0] = 0;
		blendResult[1] = blendResult[2] = 1;

		int num =
				(blends(tile, rotation, directional, 2, world) && blends(tile, rotation, directional, 1, world) && blends(tile, rotation, directional, 3, world)) ? 0 :
						(blends(tile, rotation, directional, 1, world) && blends(tile, rotation, directional, 3, world)) ? 1 :
								(blends(tile, rotation, directional, 1, world) && blends(tile, rotation, directional, 2, world)) ? 2 :
										(blends(tile, rotation, directional, 3, world) && blends(tile, rotation, directional, 2, world)) ? 3 :
												blends(tile, rotation, directional, 1, world) ? 4 :
														blends(tile, rotation, directional, 3, world) ? 5 :
																-1;
		transformCase(num, blendResult);

		// Calculate bitmask for direction.

		blendResult[3] = 0;

		for (int i = 0; i < 4; i++) {
			if (blends(tile, rotation, directional, i, world)) {
				blendResult[3] |= (1 << i);
			}
		}

		// Calculate direction for non-square sprites.

		blendResult[4] = 0;

		for (int i = 0; i < 4; i++) {
			int realDir = Mathf.mod(rotation - i, 4);
			if (blends(tile, rotation, directional, i, world) && (tile != null && tile.nearbyBuild(realDir) != null && !tile.nearbyBuild(realDir).block.squareSprite)) {
				blendResult[4] |= (1 << i);
			}
		}

		return blendResult;
	}

	@Override
	default void transformCase(int num, int[] bits) {
		switch (num) {
			case 0 -> bits[0] = 3;
			case 1 -> bits[0] = 4;
			case 2 -> bits[0] = 2;
			case 3 -> {
				bits[0] = 2;
				bits[2] = -1;
			}
			case 4 -> {
				bits[0] = 1;
				bits[2] = -1;
			}
			case 5 -> bits[0] = 1;
		}
	}

	@Override
	default boolean facing(int x, int y, int rotation, int x2, int y2) {
		return Point2.equals(x + Geometry.d4(rotation).x, y + Geometry.d4(rotation).y, x2, y2);
	}

	@Override
	default boolean blends(Tile tile, int rotation, BuildPlan @Nullable [] directional, int direction, boolean checkWorld) {
		int realDir = Mathf.mod(rotation - direction, 4);
		if (directional != null && directional[realDir] != null) {
			BuildPlan req = directional[realDir];
			if (blends(tile, rotation, req.x, req.y, req.rotation, req.block)) {
				return true;
			}
		}
		return checkWorld && blends(tile, rotation, direction);
	}

	// TODO docs -- use for direction?
	@Override
	default boolean blends(Tile tile, int rotation, int direction) {
		Building other = tile.nearbyBuild(Mathf.mod(rotation - direction, 4));
		return other != null && other.team == tile.team() && blends(tile, rotation, other.tileX(), other.tileY(), other.rotation, other.block);
	}

	@Override
	default boolean blendsArmored(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
		return Point2.equals(tile.x + Geometry.d4(rotation).x, tile.y + Geometry.d4(rotation).y, otherx, othery)
				|| ((!otherblock.rotatedOutput(otherx, othery, tile) && Edges.getFacingEdge(otherblock, otherx, othery, tile) != null &&
				Edges.getFacingEdge(otherblock, otherx, othery, tile).relativeTo(tile) == rotation) ||
				(otherblock.rotatedOutput(otherx, othery, tile) && Point2.equals(otherx + Geometry.d4(otherrot).x, othery + Geometry.d4(otherrot).y, tile.x, tile.y)));
	}

	@Override
	default boolean notLookingAt(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
		return !(otherblock.rotatedOutput(otherx, othery, tile) && Point2.equals(otherx + Geometry.d4(otherrot).x, othery + Geometry.d4(otherrot).y, tile.x, tile.y));
	}

	@Override
	default boolean lookingAtEither(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
		return
				//block is facing the other
				Point2.equals(tile.x + Geometry.d4(rotation).x, tile.y + Geometry.d4(rotation).y, otherx, othery) ||
						//does not output to rotated direction
						!otherblock.rotatedOutput(otherx, othery, tile) ||
						//other block is facing this one
						Point2.equals(otherx + Geometry.d4(otherrot).x, othery + Geometry.d4(otherrot).y, tile.x, tile.y);
	}

	@Override
	default boolean lookingAt(Tile tile, int rotation, int otherx, int othery, Block otherblock) {
		Tile facing = Edges.getFacingEdge(otherblock, otherx, othery, tile);
		return facing != null &&
				Point2.equals(tile.x + Geometry.d4(rotation).x, tile.y + Geometry.d4(rotation).y, facing.x, facing.y);
	}
}
