package heavyindustry.world.draw;

import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class MultiDrawBlock extends DrawBlock {
	protected Seq<DrawBlock> drawBlocks = new Seq<>(DrawBlock.class);

	public MultiDrawBlock() {}

	public MultiDrawBlock(DrawBlock icons, DrawBlock... draws) {
		drawBlocks.add(icons);
		drawBlocks.addAll(draws);
	}

	public MultiDrawBlock setIconProvider(DrawBlock other) {
		if (drawBlocks.contains(other)) {
			drawBlocks.remove(other);
		}
		drawBlocks.insert(0, other);
		return this;
	}

	@Override
	public void draw(Building build) {
		super.draw(build);
		drawBlocks.each(d -> d.draw(build));
	}

	@Override
	public void drawLight(Building build) {
		super.drawLight(build);
		drawBlocks.each(d -> d.drawLight(build));
	}

	@Override
	public void load(Block block) {
		super.load(block);
		drawBlocks.each(d -> d.load(block));
	}

	@Override
	public TextureRegion[] icons(Block block) {
		return drawBlocks.get(0).icons(block);
	}
}
