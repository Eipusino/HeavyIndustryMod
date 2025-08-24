package heavyindustry.ui;

import arc.func.Boolf;
import arc.func.Cons;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.Block;

public final class MultiItemSelection {
	private static TextField search;
	private static int rowCount;

	private MultiItemSelection() {}

	public static void buildTable(Table table, MultiItemData data) {
		buildTable(table, Vars.content.items(), data);
	}

	public static void buildTable(Table table, Seq<Item> items, MultiItemData data) {
		buildTable(table, items, data::isToggled, data::toggle);
	}

	public static <T extends UnlockableContent> void buildTable(Table table, Seq<T> items, Boolf<T> holder, Cons<T> toggle) {
		buildTable(null, table, items, holder, toggle, 5, 4);
	}

	public static <T extends UnlockableContent> void buildTable(@Nullable Block block, Table table, Seq<T> items, Boolf<T> holder, Cons<T> toggle, int rows, int columns) {
		Table cont = new Table().top();
		cont.defaults().size(40);

		if (search != null) search.clearText();

		Runnable rebuild = () -> {
			cont.clearChildren();

			var text = search != null ? search.getText() : "";
			int i = 0;
			rowCount = 0;

			Seq<T> list = items.select(u -> (text.isEmpty() || u.localizedName.toLowerCase().contains(text.toLowerCase())));
			for (T item : list) {
				if (!item.unlockedNow() || item.isHidden()) continue;

				ImageButton button = cont.button(Tex.whiteui, Styles.clearNoneTogglei, Mathf.clamp(item.selectionSize, 0f, 40f), () -> {
				}).tooltip(item.localizedName).get();
				button.changed(() -> toggle.get(item));
				button.getStyle().imageUp = new TextureRegionDrawable(item.uiIcon);
				button.update(() -> button.setChecked(holder.get(item)));

				if (i++ % columns == (columns - 1)) {
					cont.row();
					rowCount++;
				}
			}
		};

		rebuild.run();

		Table main = new Table().background(Styles.black6);
		if (rowCount > rows * 1.5f) {
			main.table(s -> {
				s.image(Icon.zoom).padLeft(4f);
				search = s.field(null, text -> rebuild.run()).padBottom(4).left().growX().get();
				search.setMessageText("@players.search");
			}).fillX().row();
		}

		ScrollPane pane = new ScrollPane(cont, Styles.smallPane);
		pane.setScrollingDisabled(true, false);

		if (block != null) {
			pane.setScrollYForce(block.selectScroll);
			pane.update(() -> block.selectScroll = pane.getScrollY());
		}

		pane.setOverscroll(false, false);
		main.add(pane).maxHeight(40 * rows);
		table.top().add(main);
	}
}
