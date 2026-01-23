package endfield.world.blocks;

import arc.struct.Seq;
import mindustry.gen.Building;

/**
 * @author LaoHuaJi
 */
public interface MultiBuild {
	Seq<Building> linkEntities();

	Seq<Building[]> linkProximityMap();

	int dumpIndex();

	void dumpIndex(int value);

	default void incrementDumpIndex(int prox) {
		dumpIndex((dumpIndex() + 1) % prox);
	}

	void updateLinkProximity();

	boolean checkValidPair(Building target, Building source);
}
