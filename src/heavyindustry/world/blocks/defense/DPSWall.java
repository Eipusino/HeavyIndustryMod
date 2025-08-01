package heavyindustry.world.blocks.defense;

import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.world.blocks.defense.Wall;

import static mindustry.Vars.world;

public class DPSWall extends Wall {
	public DPSWall(String name) {
		super(name);

		update = true;
		solid = true;
	}

	public class DPSWallBuild extends WallBuild {
		public Seq<DPSWallDisplay.DPSWallDisplayBuild> displays = new Seq<>();

		public void linkAdd(DPSWallDisplay.DPSWallDisplayBuild display) {
			if (!displays.contains(display)) {
				displays.add(display);
			}
		}

		public void linkRemove(DPSWallDisplay.DPSWallDisplayBuild display) {
			int index = displays.indexOf(display);
			if (index >= 0) {
				displays.remove(index);
			}
		}

		@Override
		public void damage(float damage) {
			for (DPSWallDisplay.DPSWallDisplayBuild display : displays) {
				display.damage(damage);
			}
		}

		@Override
		public void remove() {
			if (added) {
				for (DPSWallDisplay.DPSWallDisplayBuild display : displays) {
					display.links.removeValue(pos());
				}
			}

			super.remove();
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.i(displays.size);
			for (DPSWallDisplay.DPSWallDisplayBuild display : displays) {
				write.i(display.pos());
			}
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			int length = read.i();
			for (int i = 0; i < length; i++) {
				int pos = read.i();
				Building linkTarget = world.build(pos);
				if (linkTarget instanceof DPSWallDisplay.DPSWallDisplayBuild display) {
					displays.add(display);
				}
			}
		}
	}
}
