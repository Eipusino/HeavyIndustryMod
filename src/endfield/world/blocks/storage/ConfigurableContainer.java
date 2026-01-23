package endfield.world.blocks.storage;

import arc.Core;
import arc.scene.ui.CheckBox;
import arc.scene.ui.TextField.TextFieldFilter;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import endfield.util.Get;
import mindustry.core.UI;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.content;

public class ConfigurableContainer extends StorageBlock {
	protected static int storageCapacitySetting;

	public int initialStorageCapacity = 1000;

	public ConfigurableContainer(String name) {
		super(name);

		envEnabled = Env.any;

		configurable = saveConfig = true;
		separateItemCapacity = true;
		itemCapacity = 10000;
		group = BlockGroup.transportation;
		flags = EnumSet.of(BlockFlag.storage);

		config(Integer.class, (ConfigurableContainerBuild tile, Integer cap) -> {
			tile.storageCapacity = cap;
			tile.items.each((i, a) -> tile.items.set(i, Math.min(a, cap)));
		});
		config(Boolean.class, (ConfigurableContainerBuild tile, Boolean incin) -> tile.incinerate = incin);
		config(Object[].class, (ConfigurableContainerBuild tile, Object[] config) -> {
			int cap = (int) config[0];
			tile.storageCapacity = cap;
			tile.items.each((i, a) -> tile.items.set(i, Math.min(a, cap)));

			tile.incinerate = (boolean) config[1];
		});
	}

	@Override
	public void setBars() {
		super.setBars();

		removeBar("items");
		addBar("items", (ConfigurableContainerBuild tile) -> new Bar(
				() -> Core.bundle.format("bar.capacity", UI.formatAmount(tile.storageCapacity)),
				() -> Pal.items,
				() -> (float) tile.items.total() / tile.storageCapacity * content.items().count(UnlockableContent::unlockedNow)
		));
	}

	@Override
	public void init() {
		super.init();

		coreMerge = false; //No compatibility, CoreBlocks don't check for dynamic item capacity.
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ConfigurableContainerBuild::new;
	}

	public class ConfigurableContainerBuild extends StorageBuild {
		public int storageCapacity = initialStorageCapacity;
		public boolean incinerate = false;

		@Override
		public void handleItem(Building source, Item item) {
			if (incinerate && items.get(item) >= storageCapacity) {
				incinerateEffect(this, source);
				return;
			}
			super.handleItem(source, item);
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			return items.get(item) < getMaximumAccepted(item);
		}

		@Override
		public int getMaximumAccepted(Item item) {
			return incinerate ? storageCapacity * 20 : storageCapacity;
		}

		@Override
		public void buildConfiguration(Table table) {
			storageCapacitySetting = storageCapacity;
			table.table(Styles.black5, t -> {
				t.defaults().left();
				t.margin(6f);
				t.field(String.valueOf(storageCapacitySetting), text -> {
					storageCapacitySetting = Strings.parseInt(text);
				}).width(120).valid(Strings::canParsePositiveInt).get().setFilter(TextFieldFilter.digitsOnly);
				t.add(Get.statUnitName(StatUnit.items)).left();
				t.button(Icon.save, () -> configure(storageCapacitySetting)).padLeft(6);
				t.button(Icon.trash, () -> {
					int cap = storageCapacity;
					configure(0); //Delete contents
					configure(cap); //Restore original capacity
				}).tooltip("@storage.delete-contents");
				t.row();
				CheckBox box = new CheckBox("@storage.incinerate-overflow");
				box.changed(() -> configure(!incinerate));
				box.setChecked(incinerate);
				box.update(() -> box.setChecked(incinerate));
				t.add(box).colspan(4);
			});
		}

		@Override
		public void overwrote(Seq<Building> previous) {
			for (Building other : previous) {
				if (other.items != null && other.items != items) {
					items.add(other.items);
				}
			}

			items.each((i, a) -> items.set(i, Math.min(a, itemCapacity)));
		}

		@Override
		public Object config() {
			return new Object[]{storageCapacity, incinerate};
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.i(storageCapacity);
			write.bool(incinerate);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			storageCapacity = read.i();
			incinerate = read.bool();
		}
	}
}
