package endfield.math;

import arc.math.Interp;
import arc.math.Scaled;

public interface IScaled extends Scaled {
	@Override
	default float fout() {
		return 1f - fin();
	}

	@Override
	default float fout(Interp i) {
		return i.apply(fout());
	}

	@Override
	default float fout(float margin) {
		float f = fin();
		if (f >= 1f - margin) {
			return 1f - (f - (1f - margin)) / margin;
		} else {
			return 1f;
		}
	}

	@Override
	default float fin(Interp i) {
		return i.apply(fin());
	}

	@Override
	default float finpow() {
		return Interp.pow3Out.apply(fin());
	}

	@Override
	default float foutpow() {
		return 1 - Interp.pow3Out.apply(fin());
	}

	@Override
	default float finpowdown() {
		return Interp.pow3In.apply(fin());
	}

	@Override
	default float foutpowdown() {
		return 1 - Interp.pow3In.apply(fin());
	}

	@Override
	default float fslope() {
		return (0.5f - Math.abs(fin() - 0.5f)) * 2f;
	}
}
