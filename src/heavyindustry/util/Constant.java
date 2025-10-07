package heavyindustry.util;

import arc.func.Boolc;
import arc.func.Boolf;
import arc.func.Boolp;
import arc.func.Floatc;
import arc.func.Floatc2;
import arc.func.Floatp;
import heavyindustry.func.Doublep;
import mindustry.gen.Building;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;

/** Provide some commonly used lambda functions with simple structures. */
public final class Constant {
	public static final Runnable RUNNABLE_NOTHING = () -> {};
	public static final Floatp FLOATP_ZERO_FLT = () -> 0f;
	public static final Doublep DOUBLEP_ZERO_FLT = () -> 0d;
	public static final Boolc BOOLC_NOTHING = b -> {};
	public static final Boolp BOOLP_TRUE = () -> true;
	public static final Boolp BOOLP_FALSE = () -> false;
	public static final Boolf<Building> BOOLF_BUILDING_TRUE = b -> true;
	public static final Boolf<Unit> BOOLF_UNIT_TRUE = u -> true;
	public static final Boolf<Healthc> BOOLF_HEALTHC_FALSE = h -> false;
	public static final Floatc FLOATC_NOTHING = a -> {};
	public static final Floatc2 FLOATC2_NOTHING = (a, b) -> {};

	// ----------- Collection FIELD -------------

	static final int PRIME1 = 0xbe1f14b1;
	static final int PRIME2 = 0xb4b82e39;
	static final int PRIME3 = 0xced1c241;
	static final int EMPTY = 0;

	private Constant() {}
}
