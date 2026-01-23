package endfield.entities.part;

import mindustry.entities.part.DrawPart.PartProgress;

public final class HPartProgress {
	public static final PartProgress one = (IPartProgress) p -> 1f;
	public static final PartProgress recoilWarmup = (IPartProgress) p -> Math.max(0, p.warmup - p.recoil);
	public static final PartProgress recoilWarmupSep = (IPartProgress) p -> p.warmup - p.recoil;

	/** Don't let anyone instantiate this class. */
	private HPartProgress() {}
}
