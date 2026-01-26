package endfield.world.blocks.power;

import arc.util.Scaling;
import endfield.util.CollectionList;
import mindustry.game.Team;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

import java.util.List;

public class TileGenerator extends AdvancedConsumeGenerator {
	public List<Block> filter = new CollectionList<>(Block.class);

	public TileGenerator(String name) {
		super(name);
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stat.tiles, table -> {
			table.row();

			for (Block plan : filter) {
				table.table(Styles.grayPanel, t -> {
					t.image(plan.uiIcon).scaling(Scaling.fit).size(40).pad(10f).left().with(i -> StatValues.withTooltip(i, plan));
					t.table(info -> {
						info.defaults().left();
						info.add(plan.localizedName);
						info.row();
					}).left();
				});
			}
		});
	}

	@Override
	public boolean canPlaceOn(Tile tile, Team team, int rotation) {
		for (Block floor : filter) {
			if (tile.floor() == floor) {
				return true;
			}
		}
		return false;
	}
}
