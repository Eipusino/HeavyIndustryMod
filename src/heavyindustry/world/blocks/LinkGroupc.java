package heavyindustry.world.blocks;

import arc.struct.IntSeq;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.gen.Building;

public interface LinkGroupc extends Linkablec {
	default Seq<Building> linkBuilds() {
		Seq<Building> bs = new Seq<>();
		for (int pos : linkGroup().shrink()) {
			Building b = Vars.world.build(pos);
			if (linkValid(b)) bs.add(b);
			else linkGroup().removeValue(pos);
		}
		return bs;
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
