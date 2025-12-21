package heavyindustry.world.draw;

import arc.Core;
import arc.func.Func;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Point2;
import heavyindustry.util.DirEdges;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawEdgeLinkBits extends DrawBlock {
	public static final byte[] empty = new byte[]{0, 0, 0, 0};

	public Func<Building, byte[]> compLinked;

	public float layer = -1;

	public TextureRegion linker;
	public String suffix = "-linker";

	@SuppressWarnings("unchecked")
	public <T extends Building> DrawEdgeLinkBits(Func<T, byte[]> compLinks) {
		compLinked = (Func<Building, byte[]>) compLinks;
	}

	public DrawEdgeLinkBits() {
		this(e -> empty);
	}

	@Override
	public void load(Block block) {
		super.load(block);
		linker = Core.atlas.find(block.name + suffix);
	}

	@Override
	public void draw(Building build) {
		float z = Draw.z();
		if (layer > 0) Draw.z(layer);
		for (int dir = 0; dir < 4; dir++) {
			Point2[] arr = DirEdges.get(build.block.size, dir);
			byte[] linkBits = compLinked.get(build);
			for (int i = 0; i < arr.length; i++) {
				if ((linkBits[dir] & 1 << i) == 0) continue;
				float dx = 0, dy = 0;

				Draw.scl(1, dir == 1 || dir == 2 ? -1 : 1);
				switch (dir) {
					case 0 -> dx = -1;
					case 1 -> dy = -1;
					case 2 -> dx = 1;
					case 3 -> dy = 1;
				}
				Draw.rect(linker, (build.tileX() + arr[i].x + dx) * Vars.tilesize, (build.tileY() + arr[i].y + dy) * Vars.tilesize, 90 * dir);
			}
		}

		Draw.z(z);
	}
}
