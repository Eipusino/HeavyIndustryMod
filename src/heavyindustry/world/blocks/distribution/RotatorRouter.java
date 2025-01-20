package heavyindustry.world.blocks.distribution;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.distribution.*;

import static mindustry.Vars.*;

/** Routes through rotation like a payload conveyor. heehoo rainer world ref */
public class RotatorRouter extends Duct {
	public TextureRegion botRegion;

	public RotatorRouter(String name) {
		super(name);

		configurable = true;
		clearOnDoubleTap = true;
		config(Item.class, (RotatorRouterBuild tile, Item item) -> tile.sorted = item);
		configClear((RotatorRouterBuild tile) -> tile.sorted = null);
	}

	@Override
	public void load() {
		super.load();
		botRegion = Core.atlas.find(name + "-bottom");
	}

	public class RotatorRouterBuild extends DuctBuild {
		public @Nullable Item sorted;
		public int receiveDir;
		public boolean matches, blocked;

		public void pickNext() {
			if (current != null) {
				if (matches) {
					//when the item matches, always move forward.
					rotation = receiveDir;
					onProximityUpdate();
				} else {
					int rotations = 0;
					do {
						rotation = (rotation + 1) % 4;
						//if it doesn't match the sort item and this router is facing forward, skip this rotation
						if (!matches && sorted != null && rotation == receiveDir) {
							rotation++;
						}
						onProximityUpdate();
					} while ((blocked || next == null) && ++rotations < 4);
				}
			} else {
				onProximityUpdate();
			}
		}

		@Override
		public void handleItem(Building source, Item item) {
			super.handleItem(source, item);
			matches = sorted != null && current == sorted;
			pickNext();
		}

		@Override
		public void onProximityUpdate() {
			super.onProximityUpdate();
			if (current == null) return;
			blocked = next == null || !(next.acceptItem(this, current));
		}

		@Override
		public void draw() {
			Draw.rect(botRegion, x, y);

			//draw item
			if (current != null) {
				Tmp.v1.set(Geometry.d4x(recDir) * tilesize / 2f, Geometry.d4y(recDir) * tilesize / 2f)
						.lerp(Geometry.d4x(rotation) * tilesize / 2f, Geometry.d4y(rotation) * tilesize / 2f,
								Mathf.clamp((progress + 1f) / 2f));

				Draw.rect(current.fullIcon, x + Tmp.v1.x, y + Tmp.v1.y, itemSize, itemSize);
			}

			Draw.rect(region, x, y);
		}
	}
}
