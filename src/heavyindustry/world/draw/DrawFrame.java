package heavyindustry.world.draw;

import arc.func.*;
import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.draw.*;

import static arc.Core.*;
import static heavyindustry.util.Utils.*;

@SuppressWarnings("unchecked")
public class DrawFrame<E extends Building> extends DrawBlock {
	public int frames = 3;

	public Intf<E> cursor = e -> (int) ((e.totalProgress() / 5) % frames);
	public Floatf<E> alpha = e -> 1;
	public Floatf<E> rotation = e -> 0;

	public TextureRegion[] regions;
	public TextureRegion icon;

	public int size = 1;
	public boolean split = false;

	@Override
	public void draw(Building build) {
		Draw.alpha(alpha.get((E) build));
		Draw.rect(regions[cursor.get((E) build)], build.x, build.y, rotation.get((E) build));
		Draw.color();
	}

	@Override
	public TextureRegion[] icons(Block block) {
		return new TextureRegion[]{icon};
	}

	@Override
	public void load(Block block) {
		if (split) {
			regions = split(block.name + "-frame", (size > 0 ? size : block.size) * 32, 0);
		} else {
			regions = new TextureRegion[frames];
			for (int i = 0; i < frames; i++) {
				regions[i] = atlas.find(block.name + "-frame-" + i);
			}
		}
		icon = atlas.find(block.name + "-frame-icon");
	}
}
