package heavyindustry.world.draw;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import heavyindustry.graphics.Drawn;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

import static heavyindustry.util.Utils.split;

/**
 * Display multi-layer textures in sequence according to the progress of the building.
 *
 * @author Eipusino
 */
public class DrawSpecConstruct extends DrawBlock {
	/** Color of Item Surface Construction. */
	public Color constructColor1;
	/** The color of the constructed lines. */
	public Color constructColor2;

	/** texture size. {@code i * 32}. */
	public int size = 1;

	public TextureRegion[] constructRegions;

	public DrawSpecConstruct() {
		this(Pal.accent, Pal.accent);
	}

	public DrawSpecConstruct(Color color) {
		this(color, color);
	}

	public DrawSpecConstruct(Color color1, Color color2) {
		constructColor1 = color1;
		constructColor2 = color2;
	}

	@Override
	public void draw(Building build) {
		int stage = (int) (build.progress() * constructRegions.length);
		float stageProgress = (build.progress() * constructRegions.length) % 1f;

		for (int i = 0; i < stage; i++) {
			Draw.rect(constructRegions[i], build.x, build.y);
		}

		Draw.draw(Layer.blockOver, () -> Drawn.construct(build, constructRegions[stage], constructColor1, constructColor2, 0f, stageProgress, build.warmup() * build.efficiency, build.totalProgress() * 1.6f));
	}

	@Override
	public void load(Block block) {
		constructRegions = split(block.name + "-construct", (size > 0 ? size : block.size) * 32, 0);
	}
}
