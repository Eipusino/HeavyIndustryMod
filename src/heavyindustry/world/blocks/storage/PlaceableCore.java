package heavyindustry.world.blocks.storage;

import arc.Events;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import mindustry.game.EventType.BlockBuildBeginEvent;
import mindustry.game.Team;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;

import static mindustry.Vars.content;

public class PlaceableCore extends CoreBlock {
	public PlaceableCore(String name) {
		super(name);

		envEnabled = Env.any;

		configurable = true;
		saveConfig = false;

		config(Boolean.class, (PlaceableCoreBuild tile, Boolean ignored) -> {
			tile.tile.setBlock(tile.toPlace, tile.team);
			Events.fire(new BlockBuildBeginEvent(tile.tile, tile.team, null, false));
			tile.toPlace.placeBegan(tile.tile, this, null);
		});
		config(Block.class, (PlaceableCoreBuild tile, Block block) -> tile.toPlace = block);
		configClear((PlaceableCoreBuild build) -> build.toPlace = null);
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.remove(Stat.unitType);
	}

	@Override
	public boolean canPlaceOn(Tile tile, Team team, int rotation) {
		return true;
	}

	@Override
	public boolean canBreak(Tile tile) {
		return true;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = PlaceableCoreBuild::new;
	}

	public class PlaceableCoreBuild extends CoreBuild {
		public Block toPlace;

		@Override
		public void draw() {
			super.draw();

			if (toPlace != null) {
				Draw.alpha(0.25f + Mathf.absin(50f / Mathf.PI2, 0.25f));
				Draw.rect(toPlace.fullIcon, x + toPlace.offset, y + toPlace.offset);
			}
		}

		@Override
		public void buildConfiguration(Table table) {
			table.table(t -> {
				ImageButton ib = t.button(Icon.add, Styles.flati, () -> configure(Boolean.TRUE)).fillX().height(32f).disabled(b -> toPlace == null).get();
				ib.getStyle().disabled = ib.getStyle().over;
				ib.add("@hi-placeable-core-place").padLeft(16f);
				t.row();
				ItemSelection.buildTable(block, t, content.blocks().select(b -> b instanceof CoreBlock && !(b instanceof PlaceableCore)), () -> toPlace, this::configure, false, selectionRows, selectionColumns);
			});
		}
	}
}
