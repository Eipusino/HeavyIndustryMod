package heavyindustry.ui.dialogs;

import arc.Core;
import arc.scene.ui.layout.Table;
import arc.struct.IntSeq;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Time;
import heavyindustry.ui.PowerInfoGroup;
import heavyindustry.ui.PowerInfoGroup.InfoToggled;
import heavyindustry.util.IntMapf;
import mindustry.gen.Building;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.blocks.power.PowerGraph;

import static heavyindustry.ui.Elements.formatAmount;

public class PowerGraphInfoDialog extends BaseDialog {
	protected final float updateInterval = 60; //Update every second

	protected final IntSet opened = new IntSet();
	protected final IntMapf<Seq<Building>> producers = new IntMapf<>(Seq.class);
	protected final IntMapf<Seq<Building>> consumers = new IntMapf<>(Seq.class);
	protected final IntMapf<Seq<Building>> batteries = new IntMapf<>(Seq.class);
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
		super("@hi-power-info-title");

		init();
	}

	protected void init() {
		cont.table(modes -> {
			modes.button("@hi-power-info-producer", Styles.togglet, () -> {
				currType = PowerInfoType.producer;
				refresh();
			}).growX().update(b -> {
				b.setText(selectionTitle(PowerInfoType.producer));
				b.setChecked(currType == PowerInfoType.producer);
			});
			modes.button("@hi-power-info-consumer", Styles.togglet, () -> {
				currType = PowerInfoType.consumer;
				refresh();
			}).growX().update(b -> {
				b.setText(selectionTitle(PowerInfoType.consumer));
				b.setChecked(currType == PowerInfoType.consumer);
			});
			modes.button("@hi-power-info-battery", Styles.togglet, () -> {
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
			case producer ->
					Core.bundle.get("hi-power-info-producer") + " - " + Core.bundle.format("hi-power-info-persec", "[#98ffa9]+" + formatAmount(graph.getLastScaledPowerIn() * 60));
			case consumer ->
					Core.bundle.get("hi-power-info-consumer") + " - " + Core.bundle.format("hi-power-info-persec", "[#e55454]-" + formatAmount(graph.getLastScaledPowerOut() * 60));
			case battery ->
					Core.bundle.get("hi-power-info-battery") + " - [#fbad67]" + formatAmount(graph.getLastPowerStored()) + "[gray]/[]" + formatAmount(graph.getLastCapacity());
		};
	}

	protected void refresh() {
		infoTable.clear();

		switch (currType) {
			case producer -> {
				IntSeq prodKeys = producers.keys().toSeq();
				prodKeys.sort();
				prodKeys.each(id -> {
					infoTable.add(new PowerInfoGroup(producers.get(id), PowerInfoType.producer, opened.contains(id), collToggled)).growX().top().padBottom(6f);
					infoTable.row();
				});
			}
			case consumer -> {
				IntSeq consKeys = consumers.keys().toSeq();
				consKeys.sort();
				consKeys.each(id -> {
					infoTable.add(new PowerInfoGroup(consumers.get(id), PowerInfoType.consumer, opened.contains(id), collToggled)).growX().top().padBottom(6f);
					infoTable.row();
				});
			}
			case battery -> {
				IntSeq battKeys = batteries.keys().toSeq();
				battKeys.sort();
				battKeys.each(id -> {
					infoTable.add(new PowerInfoGroup(batteries.get(id), PowerInfoType.battery, opened.contains(id), collToggled)).growX().top().padBottom(6f);
					infoTable.row();
				});
			}
		}
	}

	protected void updateListings() {
		if (graph == null) return;

		clearData();

		graph.producers.each(p -> producers.get(p.block.id, () -> new Seq<>(Building.class)).add(p));
		graph.consumers.each(p -> consumers.get(p.block.id, () -> new Seq<>(Building.class)).add(p));
		graph.batteries.each(p -> batteries.get(p.block.id, () -> new Seq<>(Building.class)).add(p));

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
