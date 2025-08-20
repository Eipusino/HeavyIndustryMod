package heavyindustry.world.blocks;

import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.gen.Buildingc;

public interface MultiBuild extends Buildingc {
	Building build();

	Seq<Building> linkEntities();

	Seq<Building[]> linkProximityMap();

	int dumpIndex();

	void dumpIndex(int value);

	default void incrementDumpIndex(int prox) {
		dumpIndex((dumpIndex() + 1) % prox);
	}

    default void updateLinkProximity() {
	    if (linkEntities() != null) {
		    linkProximityMap().clear();
		    //add link entity's proximity
		    for (Building link : linkEntities()) {
			    for (Building linkProx : link.proximity) {
				    if (linkProx != this && !linkEntities().contains(linkProx)) {
					    if (checkValidPair(linkProx, link)) {
						    linkProximityMap().add(new Building[]{linkProx, link});
					    }
				    }
			    }
		    }

		    //add self entity's proximity
		    for (Building prox : build().proximity) {
			    if (!linkEntities().contains(prox)) {
				    if (checkValidPair(prox, build())) {
					    linkProximityMap().add(new Building[]{prox, build()});
				    }
			    }
		    }
	    }
    }

	default boolean checkValidPair(Building target, Building source) {
		for (Building[] pair : linkProximityMap()) {
			Building pairTarget = pair[0];
			Building pairSource = pair[1];

			if (target == pairTarget) {
				if (target.relativeTo(pairSource) == target.relativeTo(source)) {
					return false;
				}
			}
		}
		return true;
	}
}
