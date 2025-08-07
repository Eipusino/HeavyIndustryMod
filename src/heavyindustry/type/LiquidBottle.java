package heavyindustry.type;

import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.util.Tmp;
import heavyindustry.HVars;
import mindustry.graphics.MultiPacker;
import mindustry.type.Item;
import mindustry.type.Liquid;

/** Must be created via {@link LiquidBottle#LiquidBottle(java.lang.String, mindustry.type.Liquid)} and after loading mods. **/
public class LiquidBottle extends Item {
	public static Pixmap top;
	public static Pixmap bottom;

	static {
		top = new Pixmap(HVars.internalTree.child("sprites/items/bottle.png"));
		bottom = new Pixmap(HVars.internalTree.child("sprites/items/bottle-liquid.png"));
	}

	public Liquid liquid;

	public LiquidBottle(String name, Liquid liq) {
		super(name, liq.color);
		liquid = liq;

		explosiveness = liq.explosiveness * 0.8f;
		flammability = liq.flammability * 0.7f;
		radioactivity = 0;

		alwaysUnlocked = liq.alwaysUnlocked;
		generateIcons = true;
	}

	@Override
	public void createIcons(MultiPacker packer) {
		super.createIcons(packer);

		Pixmap pixmap = new Pixmap(32, 32);

		bottom.each((x, y) -> {
			int c = bottom.get(x, y);
			Tmp.c4.set(Color.ri(c), Color.gi(c), Color.bi(c), Color.ai(c)).mul(liquid.color);
			bottom.set(x, y, Tmp.c4);
		});

		pixmap.draw(bottom);
		pixmap.draw(top, true);

		packer.add(MultiPacker.PageType.main, name, pixmap);
	}
}
