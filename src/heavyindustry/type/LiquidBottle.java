package heavyindustry.type;

import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.util.Tmp;
import heavyindustry.graphics.HPixmaps;
import mindustry.graphics.MultiPacker;
import mindustry.graphics.MultiPacker.PageType;
import mindustry.type.Item;
import mindustry.type.Liquid;

/** Must be created via {@link LiquidBottle#LiquidBottle(String, Liquid)} and after loading mods. **/
public class LiquidBottle extends Item {
	public Liquid liquid;

	public LiquidBottle(String name, Liquid liquid1) {
		super(name, liquid1.color);

		liquid = liquid1;

		explosiveness = liquid.explosiveness * 0.8f;
		flammability = liquid.flammability * 0.7f;
		radioactivity = 0;

		alwaysUnlocked = liquid.alwaysUnlocked;
		generateIcons = true;
	}

	@Override
	public void createIcons(MultiPacker packer) {
		super.createIcons(packer);

		Pixmap top = HPixmaps.bottleTop, bottom = HPixmaps.bottleBottom;

		Pixmap pixmap = new Pixmap(32, 32);

		bottom.each((x, y) -> {
			int c = bottom.get(x, y);
			Tmp.c4.set(Color.ri(c), Color.gi(c), Color.bi(c), Color.ai(c)).mul(liquid.color);
			bottom.set(x, y, Tmp.c4);
		});

		pixmap.draw(bottom);
		pixmap.draw(top, true);

		packer.add(PageType.main, name, pixmap);

		pixmap.dispose();
	}
}
