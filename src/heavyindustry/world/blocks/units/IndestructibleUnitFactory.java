package heavyindustry.world.blocks.units;

import arc.graphics.g2d.Draw;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.struct.IntSeq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.util.Utils;
import mindustry.content.Items;
import mindustry.ctype.ContentType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Tex;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.Tile;
import mindustry.world.blocks.units.UnitFactory;

import static mindustry.Vars.content;

public class IndestructibleUnitFactory extends UnitFactory {
	public ItemStack[] consItems = {};

	public IndestructibleUnitFactory(String name) {
		super(name);

		hasPower = false;
		targetable = false;

		config(Integer.class, (IndestructibleUnitFactoryBuild tile, Integer index) -> {
			tile.currentPlan = index < 0 || index >= plans.size ? -1 : index;
			tile.progress = 0f;
			tile.payload = null;
		});
		config(UnitType.class, (IndestructibleUnitFactoryBuild tile, UnitType index) -> {
			tile.currentPlan = plans.indexOf(p -> p.unit == index);
			tile.progress = 0f;
			tile.payload = null;
		});
		config(IntSeq.class, (IndestructibleUnitFactoryBuild tile, IntSeq index) -> {
			int get = index.get(0);
			tile.currentPlan = get < 0 || get >= plans.size ? -1 : get;
			tile.progress = 0f;
			tile.targetTeam = Team.get(index.get(1));
			tile.payload = null;
		});
	}

	@Override
	public void init() {
		plans = content.<UnitType>getBy(ContentType.unit)
				.map(unit -> new UnitPlan(unit, 1f, consItems))
				.retainAll(plan -> plan.unit.isHidden());
		super.init();
		itemCapacity = 1;
		capacities[Items.copper.id] = 1;
	}

	public class IndestructibleUnitFactoryBuild extends UnitFactoryBuild {
		public Team targetTeam = Team.sharded;

		@Override
		public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
			super.init(tile, team, shouldAdd, rotation);
			targetTeam = team;
			return this;
		}

		@Override
		public void buildConfiguration(Table table) {
			ButtonGroup<ImageButton> group = new ButtonGroup<>();
			Table cont = new Table();
			cont.defaults().size(40f);
			int i = 0;

			for (Team t : Utils.baseTeams) {
				ImageButton button = cont.button(Tex.whiteui, Styles.clearTogglei, 24f, () -> {
				}).group(group).get();
				button.changed(() -> targetTeam = button.isChecked() ? t : null);
				if (Tex.whiteui instanceof TextureRegionDrawable w) {
					button.getStyle().imageUp = w.tint(t.color.r, t.color.g, t.color.b, t.color.a);
				}
				button.update(() -> button.setChecked(targetTeam == t));

				if (i++ % 4 == 3) {
					cont.row();
				}
			}
			if (i % 4 != 0) {
				int remaining = 4 - (i % 4);
				for (int j = 0; j < remaining; j++) {
					cont.image(Styles.black6);
				}
			}
			ScrollPane pane = new ScrollPane(cont, Styles.smallPane);
			pane.setScrollingDisabled(true, false);
			pane.setOverscroll(false, false);
			table.add(pane).maxHeight(Scl.scl(40f * 2)).left();
			table.row();

			super.buildConfiguration(table);
		}

		@Override
		public void draw() {
			super.draw();

			Draw.color(targetTeam.color);
			Draw.rect(teamRegion, x, y);
		}

		@Override
		public void drawPayload() {
			super.drawPayload();

			if (payload != null) {
				payload.unit.team = targetTeam;
			}
		}

		@Override
		public Object config() {
			return IntSeq.with(currentPlan, targetTeam.id);
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.i(targetTeam.id);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			targetTeam = Team.get(read.i());
		}
	}
}
