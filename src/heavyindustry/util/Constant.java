package heavyindustry.util;

import arc.func.Boolc;
import arc.func.Boolp;
import arc.func.Floatp;

public final class Constant {
	public static final Runnable RUNNABLE_NOTHING = () -> {};
	public static final Floatp ZERO_FLT = () -> 0f;
	public static final Boolc BOOLC_NOTHING = b -> {};
	public static final Boolp BOOLP_TRUE = () -> true;
	public static final Boolp BOOLP_FALSE = () -> false;

	private Constant() {}
}
