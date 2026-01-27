package endfield.util;

import arc.func.Boolc;
import arc.func.Boolf;
import arc.func.Boolp;
import arc.func.Cons;
import arc.func.Floatc;
import arc.func.Floatc2;
import arc.func.Floatp;
import arc.func.Func;
import arc.func.Prov;
import endfield.func.Doublep;
import mindustry.gen.Building;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;

/** Provide some commonly used lambda functions with simple structures. */
public final class Constant {
	public static final Object[] EMPTY_OBJECT = {};
	public static final Class<?>[] EMPTY_CLASS = {};
	public static final String[] EMPTY_STRING = {};

	public static final Runnable RUNNABLE_NOTHING = () -> {};
	public static final Floatp FLOATP_ZERO_FLT = () -> 0f;
	public static final Floatp FLOATP_ONE_FLT = () -> 1f;
	public static final Doublep DOUBLEP_ZERO_FLT = () -> 0d;
	public static final Boolc BOOLC_NOTHING = b -> {};
	public static final Boolp BOOLP_TRUE = () -> true;
	public static final Boolp BOOLP_FALSE = () -> false;
	public static final Boolf<Building> BOOLF_BUILDING_TRUE = boolf(true);
	public static final Boolf<Unit> BOOLF_UNIT_TRUE = boolf(true);
	public static final Boolf<Healthc> BOOLF_HEALTHC_FALSE = boolf(false);
	public static final Floatc FLOATC_NOTHING = a -> {};
	public static final Floatc2 FLOATC2_NOTHING = (a, b) -> {};
	public static final Prov<Building> PROV_BUILDING = Building::create;

	// ----------- Collection FIELD -------------

	public static final int PRIME1 = 0xbe1f14b1;
	public static final int PRIME2 = 0xb4b82e39;
	public static final int PRIME3 = 0xced1c241;

	public static final int EMPTY = 0;

	public static final int INDEX_ILLEGAL = -2;
	public static final int INDEX_ZERO = -1;

	private Constant() {}

	public static <T> Prov<T> prov(T value) {
		return () -> value;
	}

	public static <T> Cons<T> cons() {
		return t -> {};
	}

	public static <T> Boolf<T> boolf(boolean value) {
		return t -> value;
	}

	public static <P, R> Func<P, R> func(R value) {
		return p -> value;
	}
}
