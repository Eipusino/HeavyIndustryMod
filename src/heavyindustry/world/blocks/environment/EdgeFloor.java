package heavyindustry.world.blocks.environment;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.PixmapRegion;
import arc.graphics.g2d.TextureRegion;
import heavyindustry.graphics.g2d.FreeableAtlas;
import mindustry.content.Blocks;
import mindustry.graphics.MultiPacker;
import mindustry.graphics.MultiPacker.PageType;
import mindustry.world.Block;
import mindustry.world.blocks.environment.Floor;

import static mindustry.Vars.tilesize;

/**
 * A custom {@linkplain Floor floor} with per-variant edges.
 *
 * @since 1.0.7
 */
public class EdgeFloor extends Floor {
	public TextureRegion[][][] edges;
	/** If {@code true}, edges will use regions for the originating tile instead of the target tile. */
	public boolean absolute = false;

	public EdgeFloor(String name) {
		super(name);
	}

	@Override
	public void load() {
		super.load();

		int size = (int) (tilesize / Draw.scl);
		edges = new TextureRegion[variants][][];
		for (int i = 0; i < variants; i++) {
			TextureRegion edge = Core.atlas.find(name + "-edge" + (i + 1));
			if (!edge.found()) continue;

			edges[i] = edge.split(size, size);
		}
	}

	@Override
	public void createIcons(MultiPacker packer) {
		if (blendGroup != this) {
			super.createIcons(packer);
		} else {
			// Make the super implementation not generate edges.
			Block blend = blendGroup;
			blendGroup = Blocks.air;

			super.createIcons(packer);
			blendGroup = blend;

			// Instead, create individual edges for each variant.
			for (int i = 0; i < variantRegions.length; i++) {
				if (packer.has(PageType.environment, name + "-edge" + (i + 1))) continue;

				// These two are to be excluded from the final texture atlas.
				TextureRegion stencil = Core.atlas.find(name + "-edge-stencil" + (i + 1), "edge-stencil");
				TextureRegion template = Core.atlas.find(name + "-edge-template" + (i + 1), variantRegions[i]);

				PixmapRegion edge = Core.atlas.getPixmap(stencil);
				Pixmap result = new Pixmap(edge.width, edge.height);
				PixmapRegion image = Core.atlas.getPixmap(template);
				result.each((x, y) -> result.setRaw(x, y, Color.muli(edge.getRaw(x, y), image.getRaw(x % image.width, y % image.height))));

				packer.add(PageType.environment, name + "-edge" + (i + 1), result);
				result.dispose();

				if (Core.atlas instanceof FreeableAtlas free) {
					free.delete(stencil);
					if (template != variantRegions[i]) free.delete(template);
				}
			}
		}
	}

	@Override
	public TextureRegion[][] edges(int x, int y) {
		return blendGroup == this ? edges[variant(x, y)] : super.edges(x, y);
	}

	@Override
	protected TextureRegion edge(int x, int y, int rx, int ry) {
		return (absolute ? edges(x - rx, y - ry) : edges(x, y))[rx][2 - ry];
	}
}
