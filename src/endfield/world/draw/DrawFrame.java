package endfield.world.draw;

import arc.Core;
import arc.func.Floatf;
import arc.func.Intf;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import endfield.util.Sprites;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawFrame extends DrawBlock {
	public int frames = 3;

	public Intf<Building> cursor = e -> (int) ((e.totalProgress() / 5) % frames);
	public Floatf<Building> alpha = e -> 1;
	public Floatf<Building> rotation = e -> 0;

	public TextureRegion[] regions;
	public TextureRegion icon;

	public int size = 1;
	public boolean split = false;

	@SuppressWarnings("unchecked")
	public <T extends Building> DrawFrame(Intf<T> cursors, Floatf<T> alphas, Floatf<T> rotations) {
		cursor = (Intf<Building>) cursors;
		alpha = (Floatf<Building>) alphas;
		rotation = (Floatf<Building>) rotations;
	}

	public DrawFrame() {}

	@Override
	public void draw(Building build) {
		Draw.alpha(alpha.get(build));
		Draw.rect(regions[cursor.get(build)], build.x, build.y, rotation.get(build));
		Draw.color();
	}

	@Override
	public TextureRegion[] icons(Block block) {
		return new TextureRegion[]{icon};
	}

	@Override
	public void load(Block block) {
		if (split) {
			regions = Sprites.splitLayer(block.name + "-frame", (size > 0 ? size : block.size) * 32, 0);
		} else {
			regions = new TextureRegion[frames];
			for (int i = 0; i < frames; i++) {
				regions[i] = Core.atlas.find(block.name + "-frame-" + i);
			}
		}
		icon = Core.atlas.find(block.name + "-frame-icon");
	}
}
