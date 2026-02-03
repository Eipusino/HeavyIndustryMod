package endfield.world.meta;

import arc.struct.Seq;
import endfield.util.handler.FieldHandler;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatUnit;

public final class StatUnits2 {
	public static final StatUnit upTo = new StatUnit("up-to");
	public static final StatUnit threshold = new StatUnit("threshold");

	private StatUnits2() {}

	public static StatCat insert(String name, int index) {
		Seq<StatCat> all = StatCat.all;
		StatCat res = new StatCat(name);

		all.remove(res);
		all.insert(index, res);

		for (int i = 0; i < all.size; i++) {
			FieldHandler.setIntDefault(all.get(i), "id", i);
		}

		return res;
	}
}
