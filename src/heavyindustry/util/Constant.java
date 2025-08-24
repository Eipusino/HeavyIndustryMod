package heavyindustry.util;

import arc.func.Boolc;
import arc.func.Floatp;
import arc.func.Prov;

public interface Constant {
	Runnable RUNNABLE_NOTHING = () -> {};
	Prov<Boolean> TRUE_PROV = () -> true;
	Prov<Boolean> FALSE_PROV = () -> true;
	Floatp ZERO_FLT = () -> 0f;
	Boolc BOOLC_NOTHING = b -> {};
}
