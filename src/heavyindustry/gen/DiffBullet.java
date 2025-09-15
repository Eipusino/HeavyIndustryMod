package heavyindustry.gen;

import arc.struct.Seq;
import arc.util.pooling.Pools;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;

public class DiffBullet extends Bullet {
	public Seq<Healthc> healthcs = new Seq<>(Healthc.class);

	public static DiffBullet createDiff() {
		return Pools.obtain(DiffBullet.class, DiffBullet::new);
	}

	@Override
	public int classId() {
		return Entitys.getId(DiffBullet.class);
	}
}
