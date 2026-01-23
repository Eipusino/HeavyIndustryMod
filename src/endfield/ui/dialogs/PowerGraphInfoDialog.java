package endfield.ui.dialogs;

import arc.Core;
import arc.func.Prov;
import arc.scene.ui.layout.Table;
import arc.struct.IntSeq;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Time;
import endfield.ui.PowerInfoGroup;
import endfield.ui.PowerInfoGroup.InfoToggled;
import endfield.util.IntMap2;
import mindustry.gen.Building;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.blocks.power.PowerGraph;

import static endfield.ui.Elements.formatAmount;

public class PowerGraphInfoDialog extends BaseDialog {
	protected final float updateInterval = 60; //Update every second

	protected static final Prov<Seq<Building>> prov = () -> new Seq<>(Building.class);

	protected final IntSet opened = new IntSet();
	protected final IntMap2<Seq<Building>> producers = new IntMap2<>(Seq.class);
	protected final IntMap2<Seq<Building>> consumers = new IntMap2<>(Seq.class);
	protected final IntMap2<Seq<Building>> batteries = new IntMap2<>(Seq.class);
	protected final InfoToggled collToggled = (int id, boolean open) -> {
		if (open) {
			opened.add(id);
		} else {
			opened.remove(id);
		}
	};
	protected PowerGraph graph;
	protected float updateTimer;

	protected Table infoTable;
	protected PowerInfoType currType = PowerInfoType.producer;

	public PowerGraphInfoDialog() {
		super("@text.power-info-title");

		init();
	}

	protected void init() {
		cont.table(modes -> {
			modes.button("@text.power-info-producer", Styles.togglet, () -> {
				currType = PowerInfoType.producer;
				refresh();
			}).growX().update(b -> {
				b.setText(selectionTitle(PowerInfoType.producer));
				b.setChecked(currType == PowerInfoType.producer);
			});
			modes.button("@text.power-info-consumer", Styles.togglet, () -> {
				currType = PowerInfoType.consumer;
				refresh();
			}).growX().update(b -> {
				b.setText(selectionTitle(PowerInfoType.consumer));
				b.setChecked(currType == PowerInfoType.consumer);
			});
			modes.button("@text.power-info-battery", Styles.togglet, () -> {
				currType = PowerInfoType.battery;
				refresh();
			}).growX().update(b -> {
				b.setText(selectionTitle(PowerInfoType.battery));
				b.setChecked(currType == PowerInfoType.battery);
			});
		}).growX().top();

		cont.row();

		cont.pane(p -> infoTable = p.table().grow().top().get()).growX().expandY().top();

		hidden(() -> {
			graph = null;
			opened.clear();
			clearData();
		});

		update(() -> {
			updateTimer += Time.delta;
			if (updateTimer >= updateInterval) {
				updateTimer %= updateInterval;
				updateListings();
			}
		});

		onResize(this::refresh);

		addCloseButton();
	}

	public void show(PowerGraph gra) {
		graph = gra;
		updateListings();
		show();
	}

	protected String selectionTitle(PowerInfoType type) {
		if (graph == null) return "";

		return switch (type) {
			case producer -> Core.bundle.get("text.power-info-producer") + " - " + Core.bundle.format("text.power-info-persec", "[#98ffa9]+" + formatAmount(graph.getLastScaledPowerIn() * 60));
			case consumer -> Core.bundle.get("text.power-info-consumer") + " - " + Core.bundle.format("text.power-info-persec", "[#e55454]-" + formatAmount(graph.getLastScaledPowerOut() * 60));
			case battery -> Core.bundle.get("text.power-info-battery") + " - [#fbad67]" + formatAmount(graph.getLastPowerStored()) + "[gray]/[]" + formatAmount(graph.getLastCapacity());
		};
	}

	protected void refresh() {
		infoTable.clear();

		switch (currType) {
			case producer -> {
				IntSeq prodKeys = producers.keys().toSeq();
				prodKeys.sort();
				for (int i = 0; i < prodKeys.size; i++) {
					int id = prodKeys.items[i];

					infoTable.add(new PowerInfoGroup(producers.get(id), PowerInfoType.producer, opened.contains(id), collToggled)).growX().top().padBottom(6f);
					infoTable.row();
				}
			}
			case consumer -> {
				IntSeq consKeys = consumers.keys().toSeq();
				consKeys.sort();
				for (int i = 0; i < consKeys.size; i++) {
					int id = consKeys.items[i];

					infoTable.add(new PowerInfoGroup(consumers.get(id), PowerInfoType.consumer, opened.contains(id), collToggled)).growX().top().padBottom(6f);
					infoTable.row();
				}
			}
			case battery -> {
				IntSeq battKeys = batteries.keys().toSeq();
				battKeys.sort();
				for (int i = 0; i < battKeys.size; i++) {
					int id = battKeys.items[i];

					infoTable.add(new PowerInfoGroup(batteries.get(id), PowerInfoType.battery, opened.contains(id), collToggled)).growX().top().padBottom(6f);
					infoTable.row();
				}
			}
		}
	}

	protected void updateListings() {
		if (graph == null) return;

		clearData();

		for (Building p : graph.producers) producers.get(p.block.id, prov).add(p);
		for (Building p : graph.consumers) consumers.get(p.block.id, prov).add(p);
		for (Building p : graph.batteries) batteries.get(p.block.id, prov).add(p);

		refresh();
	}

	protected void clearData() {
		producers.clear();
		consumers.clear();
		batteries.clear();
	}

	public enum PowerInfoType {
		producer,
		consumer,
		battery;

		public static final PowerInfoType[] all = values();
	}
}
