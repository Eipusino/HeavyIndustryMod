package heavyindustry.world.blocks.units;

import arc.graphics.g2d.Draw;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import heavyindustry.util.Utils;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Team;
import mindustry.gen.Tex;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.payloads.PayloadSource;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.content;
import static mindustry.Vars.player;
import static mindustry.Vars.state;

public class PayloadSourcef extends PayloadSource {
	public static final Team[] teams = new Team[]{Team.derelict, Team.sharded, Team.crux, Team.green, Team.malis, Team.blue};

	public PayloadSourcef(String name) {
		super(name);
		noUpdateDisabled = false;
		unitCapModifier = 1;
		targetable = false;
		underBullets = true;
		destructible = false;

		config(Block.class, (PayloadSourceBuildf tile, Block block) -> {
			if (canProduce(block) && tile.configBlock != block) {
				tile.configBlock = block;
				tile.unit = null;
				tile.payload = null;
				tile.scl = 0f;
			}
		});

		config(UnitType.class, (PayloadSourceBuildf tile, UnitType unit) -> {
			if (canProduce(unit) && tile.unit != unit) {
				tile.unit = unit;
				tile.configBlock = null;
				tile.payload = null;
				tile.scl = 0f;
			}
		});

		config(Integer.class, (PayloadSourceBuildf tile, Integer index) -> {
			tile.unit = null;
			tile.configBlock = null;
			tile.payload = null;
			tile.scl = 0;
		});

		configClear((PayloadSourceBuildf tile) -> {
			tile.configBlock = null;
			tile.unit = null;
			tile.payload = null;
			tile.scl = 0f;
		});
	}

	@Override
	public boolean canProduce(Block b) {
		return b.isVisible() && !(b instanceof CoreBlock) && !state.rules.isBanned(b) && b.environmentBuildable();
	}

	public class PayloadSourceBuildf extends PayloadSourceBuild {
		@Override
		public void updateTile() {
			enabled = true;
			super.updateTile();
		}

		@Override
		public void buildConfiguration(Table table) {
			ButtonGroup<ImageButton> g = new ButtonGroup<>();
			Table cont = new Table();
			cont.defaults().size(55);
			for (Team bt : Utils.baseTeams) {
				ImageButton button = cont.button(((TextureRegionDrawable) Tex.whiteui).tint(bt.color), Styles.clearTogglei, 35, () -> {
				}).group(g).get();
				button.changed(() -> {
					if (button.isChecked()) {
						if (player.team() == team) {
							configure(bt.id);
						} else deselect();
					}
				});
				button.update(() -> button.setChecked(team == bt));
			}
			table.add(cont).maxHeight(Scl.scl(55 * 2)).left();
			table.row();
			ItemSelection.buildTable(block, table,
					content.blocks().select(PayloadSourcef.this::canProduce).<UnlockableContent>as()
							.add(content.units().select(PayloadSourcef.this::canProduce).as()),
					this::config, this::configure, false, selectionRows, selectionColumns);
		}

		@Override
		public UnlockableContent config() {
			return unit == null ? configBlock : unit;
		}

		@Override
		public void configure(Object value) {
			if (player.team() == team) super.configure(value);
			else deselect();
		}

		@Override
		public void configured(Unit builder, Object value) {
			super.configured(builder, value);
			if (value instanceof Number v && builder != null && builder.isPlayer()) {
				Team team = Team.get(v.intValue());
				builder.team = team;
				builder.getPlayer().team(team);

				onRemoved();
				changeTeam(team);
				onProximityUpdate();
			}
		}

		@Override
		public void draw() {
			Draw.rect(region, x, y);
			Draw.rect(outRegion, x, y, rotdeg());
			Draw.color(team.color);
			Draw.rect(teamRegion, x, y);
			Draw.color();
			Draw.scl(scl);
			drawPayload();
			Draw.reset();
		}

		@Override
		public void damage(float damage) {
		}

		@Override
		public boolean canPickup() {
			return false;
		}
	}
}
