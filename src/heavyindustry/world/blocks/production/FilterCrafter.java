package heavyindustry.world.blocks.production;

import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import heavyindustry.util.Utils;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.Stat;

public class FilterCrafter extends GenericCrafter {
	public Seq<Item> filterItemsBuilder = new Seq<>(Item.class);

	public FilterCrafter(String name) {
		super(name);

		configurable = true;
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.remove(Stat.productionTime);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = FilterCrafterBuild::new;
	}

	public class FilterCrafterBuild extends GenericCrafterBuild {
		public Item[] filterItems = {};

		public boolean[] shown = {};
		public int current = 0;

		@Override
		public void created() {
			super.created();

			filterItems = filterItemsBuilder.toArray(Item.class);
			shown = new boolean[filterItems.length];
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			return Utils.indexOf(filterItems, item) < getMaximumAccepted(item);
		}

		@Override
		public void buildConfiguration(Table table) {
			table.background(Tex.pane);
			table.pane(but -> {
				but.collapser(bs -> {
					Vars.content.items().each(item -> {
						bs.button(new TextureRegionDrawable(item.uiIcon), Styles.flati, 24f, () -> {
							for (int i = 0; i < filterItems.length; i++) {
								if (shown[i]) {
									filterItems[i] = item;
									shown[i] = false;
								}
							}
						}).size(30f);
					});
				}, () -> showns(shown));
			}).size(120f, 30f).row();
			table.pane(but2 -> {
				place(but2, 1);
				place(but2, 0);
				place(but2, 3);
				place(but2, 2);
			}).row();
			table.table(places -> {
				places.defaults().size(30f);
				places.image(Icon.up);
				places.image(Icon.right);
				places.image(Icon.down);
				places.image(Icon.left);
			});
		}

		protected boolean showns(boolean[] bs) {
			for (boolean b : bs) {
				if (b) return true;
			}
			return false;
		}

		protected void place(Table table, int index) {
			table.button(Icon.add, Styles.flati, 24f, () -> {
				shown[current] = false;
				shown[index] = true;
				current = index;
			}).update(m -> {
				m.getStyle().imageUp = filterItems[index].uiIcon == null ? Icon.add : new TextureRegionDrawable(filterItems[index].uiIcon);
			}).size(30f);
		}

		@Override
		public void updateTile() {
			for (int i = 0; i < filterItems.length; i++) {
				Building build = nearby(i);
				if (build != null && build.acceptItem(this, filterItems[i]) && items.has(filterItems[i])) {
					build.handleItem(this, filterItems[i]);
					items.remove(filterItems[i], 1);
				}
			}
		}
	}
}
