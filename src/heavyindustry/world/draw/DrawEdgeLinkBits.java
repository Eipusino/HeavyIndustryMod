package heavyindustry.world.draw;

import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import heavyindustry.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.draw.*;

import static arc.Core.*;
import static mindustry.Vars.*;

@SuppressWarnings("unchecked")
public class DrawEdgeLinkBits<T extends Building> extends DrawBlock {
	public static final byte[] EMP = new byte[]{0, 0, 0, 0};
	public Func<T, byte[]> compLinked = e -> EMP;

	public float layer = -1;

	public TextureRegion linker;
	public String suffix = "-linker";

	@Override
	public void load(Block block) {
		super.load(block);
		linker = atlas.find(block.name + suffix);
	}

	@Override
	public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {}

	@Override
	public void draw(Building build) {
		float z = Draw.z();
		if (layer > 0) Draw.z(layer);
		for (int dir = 0; dir < 4; dir++) {
			Point2[] arr = DirEdges.get(build.block.size, dir);
			byte[] linkBits = compLinked.get((T) build);
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
				Draw.rect(linker, (build.tileX() + arr[i].x + dx) * tilesize, (build.tileY() + arr[i].y + dy) * tilesize, 90 * dir);
			}
		}

		Draw.z(z);
	}
}
