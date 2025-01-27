package heavyindustry.world.draw;

import arc.graphics.g2d.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class MultiDrawBlock extends DrawBlock {
	protected Seq<DrawBlock> drawBlocks = new Seq<>();

	public MultiDrawBlock(DrawBlock iconProvider, DrawBlock... drawBlocks) {
		super();
		this.drawBlocks.add(iconProvider);
		this.drawBlocks.addAll(drawBlocks);
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
