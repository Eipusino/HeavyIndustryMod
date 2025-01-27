package heavyindustry.ui;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.modules.*;

public class ItemImageDynamic extends Stack {
	public ItemImageDynamic(TextureRegion region, Intp amountp, Prov<Color> colorProv) {
		add(new Table(o -> {
			o.left();
			o.add(new Image(region)).size(32f).scaling(Scaling.fit);
		}));

		add(new Table(t -> {
			t.left().bottom();
			t.label(() -> {
				int amount = amountp.get();
				return amount >= 1000 ? UI.formatAmount(amount) : amount + "";
			}).style(Styles.outlineLabel).color(colorProv.get());
			t.pack();
		}));
	}

	public ItemImageDynamic(Item item, Intp amountp) {
		this(item.uiIcon, amountp, () -> Color.lightGray);
	}

	public ItemImageDynamic(Item item, Intp amountp, ItemModule module) {
		this(item.uiIcon, amountp, () -> module.has(item, amountp.get()) ? Color.white : Pal.redderDust);
	}
}
