package endfield.gen;

import arc.struct.Seq;
import arc.util.pooling.Pools;
import mindustry.entities.Sized;
import mindustry.gen.Bullet;

public class BlackHoleBullet extends Bullet {
	public Seq<Sized> sizeds = new Seq<>(Sized.class);

	public static BlackHoleBullet createBlackHole() {
		return Pools.obtain(BlackHoleBullet.class, BlackHoleBullet::new);
	}

	@Override
	public int classId() {
		return Entitys.getId(BlackHoleBullet.class);
	}
}
