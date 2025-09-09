package heavyindustry.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.util.BaseObjectIntMap;
import mindustry.Vars;
import mindustry.type.Item;
import mindustry.world.blocks.distribution.OverflowGate;

public class TubeGate extends OverflowGate {
	public TextureRegion rotorRegion;

	public TubeGate(String name) {
		super(name);
	}

	@Override
	public void load() {
		super.load();

		rotorRegion = Core.atlas.find(name + "-rotator");
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = TubeGateBuild::new;
	}

	public class TubeGateBuild extends OverflowGateBuild {
		public BaseObjectIntMap<Item> directionalItems = new BaseObjectIntMap<>(Item.class);

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			short size = read.s();
			for (int i = 0; i < size; i++) {
				int item = read.i();
				int build = read.i();
				directionalItems.put(Vars.content.item(item), build);
			}
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.s(directionalItems.size);
			directionalItems.keys().toSeq().each(item -> {
				write.i(item.id);
				write.i(directionalItems.get(item));
			});
		}
	}
}
