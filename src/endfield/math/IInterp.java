package endfield.math;

import arc.math.Interp;

@FunctionalInterface
public interface IInterp extends Interp {
	@Override
	default float apply(float start, float end, float a) {
		return start + (end - start) * apply(a);
	}
}
