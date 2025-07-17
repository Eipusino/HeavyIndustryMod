package heavyindustry.maps.planets;

import arc.graphics.Color;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import heavyindustry.content.HBlocks;
import heavyindustry.maps.ColorPass;
import heavyindustry.maps.HeightPass;
import mindustry.content.Blocks;
import mindustry.world.Block;

//this fucking sucks
public class GliesePlanetGenerator extends BasePlanetGenerator {
	public Seq<HeightPass> heights = new Seq<>();
	public Seq<ColorPass> colors = new Seq<>();
	public float baseHeight = 1;
	public Color baseColor = Color.white;

	public Block[][] arr = {
			{Blocks.water, HBlocks.stoneWater, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone, HBlocks.stoneFull, HBlocks.stoneFull, HBlocks.stoneFull, HBlocks.stoneFull, HBlocks.stoneHalf, HBlocks.stoneHalf, HBlocks.stoneHalf},
			{Blocks.water, HBlocks.stoneWater, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone, HBlocks.stoneFull, HBlocks.stoneFull, HBlocks.stoneFull, HBlocks.stoneFull, HBlocks.stoneHalf, HBlocks.stoneHalf, HBlocks.stoneFullTiles},
			{Blocks.water, HBlocks.stoneWater, Blocks.stone, Blocks.stone, HBlocks.stoneFull, HBlocks.stoneFull, HBlocks.stoneHalf, HBlocks.stoneHalf, HBlocks.stoneHalf, HBlocks.stoneFullTiles, HBlocks.stoneFullTiles, HBlocks.stoneFullTiles},
			{Blocks.deepwater, Blocks.water, HBlocks.stoneWater, Blocks.stone, Blocks.stone, Blocks.stone, HBlocks.stoneFull, HBlocks.stoneFull, HBlocks.stoneHalf, HBlocks.stoneHalf, HBlocks.stoneHalf, HBlocks.stoneFullTiles, HBlocks.stoneFullTiles},
			{Blocks.water, Blocks.darksand, Blocks.darksand, Blocks.darksand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.shale, Blocks.shale, Blocks.shale, Blocks.shale, Blocks.shale},
			{Blocks.water, Blocks.stone, Blocks.snow, Blocks.ice, Blocks.iceSnow, Blocks.snow, Blocks.snow, Blocks.snow, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice},
			{Blocks.water, Blocks.stone, Blocks.snow, Blocks.ice, Blocks.iceSnow, HBlocks.snowySand, Blocks.snow, Blocks.snow, Blocks.snow, Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice}
	};

	@Override
	public Block[][] arr() {
		return arr;
	}

	@Override
	public float rawHeight(Vec3 position) {
		float height = baseHeight;
		for (HeightPass h : heights) {
			height = h.height(position, height);
		}
		return height;
	}

	@Override
	public float getHeight(Vec3 position) {
		float height = rawHeight(position);
		return Math.max(height, water);
	}

	@Override
	public Color getColor(Vec3 position) {
		Color color = baseColor;
		for (ColorPass c : colors) {
			if (c.color(position, rawHeight(position)) != null) color = c.color(position, rawHeight(position));
		}
		return color;
	}
}
