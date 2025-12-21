package heavyindustry.world.blocks.units;

import arc.graphics.g2d.Draw;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import heavyindustry.util.Get;
import mindustry.Vars;
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

public class PayloadSource2 extends PayloadSource {
	public PayloadSource2(String name) {
		super(name);
		noUpdateDisabled = false;
		unitCapModifier = 1;
		targetable = false;
		underBullets = true;
		destructible = false;

		config(Block.class, (PayloadSourceBuild2 tile, Block block) -> {
			if (canProduce(block) && tile.configBlock != block) {
				tile.configBlock = block;
				tile.unit = null;
				tile.payload = null;
				tile.scl = 0f;
			}
		});

		config(UnitType.class, (PayloadSourceBuild2 tile, UnitType unit) -> {
			if (canProduce(unit) && tile.unit != unit) {
				tile.unit = unit;
				tile.configBlock = null;
				tile.payload = null;
				tile.scl = 0f;
			}
		});

		config(Integer.class, (PayloadSourceBuild2 tile, Integer index) -> {
			tile.unit = null;
			tile.configBlock = null;
			tile.payload = null;
			tile.scl = 0;
		});

		configClear((PayloadSourceBuild2 tile) -> {
			tile.configBlock = null;
			tile.unit = null;
			tile.payload = null;
			tile.scl = 0f;
		});
	}

	@Override
	public boolean canProduce(Block b) {
		return b.isVisible() && !(b instanceof CoreBlock) && !Vars.state.rules.isBanned(b) && b.environmentBuildable();
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = PayloadSourceBuild2::new;
	}

	public class PayloadSourceBuild2 extends PayloadSourceBuild {
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
			for (Team bt : Get.baseTeams) {
				ImageButton button = cont.button(((TextureRegionDrawable) Tex.whiteui).tint(bt.color), Styles.clearTogglei, 35, () -> {
				}).group(g).get();
				button.changed(() -> {
					if (button.isChecked()) {
						if (Vars.player.team() == team) {
							configure(bt.id);
						} else deselect();
					}
				});
				button.update(() -> button.setChecked(team == bt));
			}
			table.add(cont).maxHeight(Scl.scl(55 * 2)).left();
			table.row();
			ItemSelection.buildTable(block, table,
					Vars.content.blocks().select(PayloadSource2.this::canProduce).<UnlockableContent>as()
							.add(Vars.content.units().select(PayloadSource2.this::canProduce).as()),
					this::getContent, this::configure, false, selectionRows, selectionColumns);
		}

		public UnlockableContent getContent() {
			return unit == null ? configBlock : unit;
		}

		@Override
		public Object config() {
			return getContent();
		}

		@Override
		public void configure(Object value) {
			if (Vars.player.team() == team) super.configure(value);
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
		public void damage(float damage) {}

		@Override
		public float handleDamage(float amount) {
			return 0f;
		}

		@Override
		public boolean canPickup() {
			return false;
		}
	}
}
