package heavyindustry.ui;

import arc.graphics.Color;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import arc.util.Strings;
import mindustry.type.Liquid;
import mindustry.ui.Styles;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.iconMed;

/** Similar to {@code LiquidDisplay}, however does not append with the liquid's name. */
public class NamelessLiquidDisplay extends Table {
	public final Liquid liquid;
	public final float amount;
	public final boolean perSecond;

	public NamelessLiquidDisplay(Liquid liquid, float amount, boolean perSecond) {
		this.liquid = liquid;
		this.amount = amount;
		this.perSecond = perSecond;

		add(new Stack() {{
			add(new Image(liquid.uiIcon).setScaling(Scaling.fit));

			if (amount != 0) {
				Table t = new Table().left().bottom();
				t.add(Strings.autoFixed(amount, 2)).style(Styles.outlineLabel);
				add(t);
			}
		}}).size(iconMed).padRight(3 + (amount != 0 && Strings.autoFixed(amount, 2).length() > 2 ? 8 : 0));

		if (perSecond) {
			add(StatUnit.perSecond.localized()).padLeft(2).padRight(5).color(Color.lightGray).style(Styles.outlineLabel);
		}
	}
}
