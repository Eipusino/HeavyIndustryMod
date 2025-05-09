package heavyindustry.ui.elements;

import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import heavyindustry.ui.dialogs.PowerGraphInfoDialog.PowerInfoType;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.world.Block;
import mindustry.world.consumers.ConsumePower;

import static heavyindustry.ui.UIUtils.formatAmount;

public class PowerInfoGroup extends Table {
	public PowerInfoGroup(Seq<Building> buildings, PowerInfoType type, boolean isOpen, InfoToggled toggled) {
		init(buildings, type, isOpen, toggled);
	}

	protected void init(Seq<Building> buildings, PowerInfoType type, boolean isOpen, InfoToggled toggled) {
		top();

		Table collT = new Table();
		int col = 0;
		int maxCol = Vars.mobile ? 3 : 5;
		collT.image().growX().pad(5f).padLeft(10).padRight(10).height(3).color(Color.darkGray).colspan(maxCol);
		collT.row();
		for (int i = 0; i < buildings.size; i++) {
			Building b = buildings.get(i);
			collT.label(() -> "[lightgray](" + b.tileX() + ", " + b.tileY() + "): " + getData(b, type)).pad(6f).uniformX().left();

			col++;
			if (col == maxCol) {
				collT.row();
				col = 0;
			}
		}
		if (col > 0) {
			for (int i = col; i < maxCol; i++) {
				collT.add().uniformX();
			}
		}

		Collapser coll = new Collapser(collT, !isOpen);

		Block block = buildings.first().block;
		ImageButton b = button(Icon.downOpen, () -> {
			coll.toggle(false);
			toggled.get(block.id, !coll.isCollapsed());
		}).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).growX().top().get();
		b.clearChildren();
		b.table(t -> {
			t.add(b.getImage()).left();
			t.image(block.fullIcon).size(24f).padLeft(6f).left();
			t.add(block.localizedName).padLeft(6f).left();
		}).left();
		b.row();
		b.add(coll).growX().top();
	}

	protected String getData(Building building, PowerInfoType type) {
		return switch (type) {
			case producer -> {
				float produced = building.getPowerProduction() * building.timeScale(); //Assume people don't override delta()
				yield Core.bundle.format("hi-power-info-persec", "[#98ffa9]+" + formatAmount(produced * 60));
			}
			case consumer -> {
				ConsumePower consumePower = building.block.consPower;
				float consumed = consumePower.requestedPower(building) * building.timeScale();
				yield Core.bundle.format("hi-power-info-persec", "[#e55454]-" + formatAmount(consumed * 60));
			}
			case battery -> {
				ConsumePower consumePower = building.block.consPower;
				float stored = building.power.status * consumePower.capacity;
				yield "[#fbad67]" + formatAmount(stored) + "[gray]/[]" + formatAmount(consumePower.capacity);
			}
		};
	}

	public interface InfoToggled {
		void get(int id, boolean collapsed);
	}
}
