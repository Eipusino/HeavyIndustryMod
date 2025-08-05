package heavyindustry.world.blocks;

import arc.struct.IntSeq;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.gen.Building;

public interface LinkGroupc extends Linkablec {
	Seq<Building> builds();

	default Seq<Building> linkBuilds() {
		builds().clear();

		for (int pos : linkGroup().shrink()) {
			Building build = Vars.world.build(pos);
			if (linkValid(build)) {
				builds().add(build);
			} else {
				linkGroup().removeValue(pos);
			}
		}
		return builds();
	}

	IntSeq linkGroup();

	void linkGroup(IntSeq seq);

	@Override
	default void drawLink() {
		drawLink(linkBuilds());
	}

	@Override
	default boolean linkValid() {
		for (Building b : linkBuilds()) if (!linkValid(b)) return false;
		return true;
	}

	@Override
	default Building link() {
		return as();
	}
}
