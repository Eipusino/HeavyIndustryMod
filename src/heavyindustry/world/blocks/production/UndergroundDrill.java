package heavyindustry.world.blocks.production;

import arc.Core;
import arc.func.Boolf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.ObjectFloatMap;
import arc.util.Scaling;
import arc.util.Strings;
import heavyindustry.world.blocks.environment.UndergroundOreBlock;
import heavyindustry.world.blocks.storage.DetectorCoreBlock;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.logic.Ranged;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.production.Drill;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValue;

import static mindustry.Vars.content;
import static mindustry.Vars.iconSmall;
import static mindustry.Vars.indexer;
import static mindustry.Vars.player;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class UndergroundDrill extends Drill {
	public UndergroundDrill(String name) {
		super(name);
		schematicPriority = -5; // build last to ensure ore detectors are built beforehand
		drawMineItem = false;
	}

	protected static StatValue drillAble(float drillTime, float drillMultiplier, float size, ObjectFloatMap<Item> multipliers, Boolf<Block> filter) {
		return t -> {
			t.row();
			t.table(c -> {
				int i = 0;
				for (Block block : content.blocks()) {
					if (!(block instanceof UndergroundOreBlock uo) || !filter.get(block)) continue;

					c.table(Styles.grayPanel, b -> {
						b.image(uo.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
						b.table(info -> {
							info.left();
							info.add(uo.localizedName).left().row();
							//info.add(block.itemDrop.emoji()).left(); there's no emoji for modded items.
						}).grow();
						if (multipliers != null) {
							b.add(Strings.autoFixed(60f / (Math.max(drillTime + drillMultiplier * uo.drop.hardness, drillTime) / multipliers.get(uo.itemDrop, 1f)) * size, 2) + StatUnit.perSecond.localized())
									.right().pad(10f).padRight(15f).color(Color.lightGray);
						}
					}).growX().pad(5);
					if (++i % 2 == 0) c.row();
				}
			}).growX().colspan(t.getColumns());
		};
	}

	//placeable on drill bases or replaceable by other underground drills
	@Override
	public boolean canPlaceOn(Tile tile, Team team, int rotation) {
		if (isMultiblock()) {
			for (Tile other : tile.getLinkedTilesAs(this, tempTiles)) {
				Building build = other.build;
				if (build != null && build.block instanceof UndergroundDrill && build.team == team) return true;
			}
			return nearestDetector(team, tile.worldx(), tile.worldy()) != null;
		} else {
			Building build = tile.build;
			return (build != null && build.block instanceof UndergroundDrill && build.team == team) ||
					nearestDetector(team, tile.worldx(), tile.worldy()) != null;
		}
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		Tile tile = world.tile(x, y);
		var detector = nearestDetector(player.team(), x * 8, y * 8);
		if (tile == null) return;

		if (detector == null) {
			drawPlaceText(Core.bundle.get("bar.detector-req"), x, y, valid);
			return;
		}

		countOre(tile);

		if (returnItem != null) {
			float width = drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", 60f / getDrillTime(returnItem) * returnCount, 2), x, y, valid);
			float dx = x * tilesize + offset - width / 2f - 4f, dy = y * tilesize + offset + size * tilesize / 2f + 5, s = iconSmall / 4f;
			Draw.mixcol(Color.darkGray, 1f);
			Draw.rect(returnItem.fullIcon, dx, dy - 1, s, s);
			Draw.reset();
			Draw.rect(returnItem.fullIcon, dx, dy, s, s);
		} else {
			Tile to = tile.getLinkedTilesAs(this, tempTiles).find(t -> getUnderDrop(t.overlay()) != null && (getUnderDrop(t.overlay()).hardness > tier || getUnderDrop(t.overlay()) == blockedItem));
			Item item = to == null ? null : to.drop();
			if (item != null) {
				drawPlaceText(Core.bundle.get("bar.drilltierreq"), x, y, valid);
			}
		}
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.remove(Stat.drillTier);
		stats.add(Stat.drillTier, drillAble(drillTime, hardnessDrillMultiplier, size * size, drillMultipliers, b -> b instanceof UndergroundOreBlock &&
				getUnderDrop(b) != null && getUnderDrop(b).hardness <= tier && getUnderDrop(b) != blockedItem && (indexer.isBlockPresent(b) || state.isMenu())));
	}

	@Override
	public boolean canMine(Tile tile) {
		if (tile == null || tile.block().isStatic()) return false;
		Item drops = getUnderDrop(tile.overlay());
		return drops != null && drops.hardness <= tier && drops != blockedItem;
	}

	@Override
	protected void countOre(Tile tile) {
		returnItem = null;
		returnCount = 0;

		oreCount.clear();
		itemArray.clear();

		for (Tile other : tile.getLinkedTilesAs(this, tempTiles)) {
			if (canMine(other) && (other.overlay() instanceof UndergroundOreBlock u)) {
				oreCount.increment(getUnderDrop(u), 0, 1);
			}
		}

		for (Item item : oreCount.keys()) {
			itemArray.add(item);
		}

		itemArray.sort((item1, item2) -> {
			int type = Boolean.compare(!item1.lowPriority, !item2.lowPriority);
			if (type != 0) return type;
			int amounts = Integer.compare(oreCount.get(item1, 0), oreCount.get(item2, 0));
			if (amounts != 0) return amounts;
			return Integer.compare(item1.id, item2.id);
		});

		if (itemArray.isEmpty()) {
			return;
		}

		returnItem = itemArray.peek();
		returnCount = oreCount.get(itemArray.peek(), 0);
	}

	public Item getOutput(Tile tile) {
		countOre(tile);

		return returnItem;
	}

	protected Item getUnderDrop(Block b) {
		return b instanceof UndergroundOreBlock u ? u.drop : null;
	}

	protected Ranged nearestDetector(Team team, float wx, float wy) {
		return (Ranged) indexer.findTile(team, wx, wy, 999f, b -> (b.block instanceof OreDetector || b.block instanceof DetectorCoreBlock)
				&& Mathf.within(wx, wy, b.x, b.y, ((Ranged) b).range()));
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = UndergroundDrillBuild::new;
	}

	public class UndergroundDrillBuild extends DrillBuild {
		@Override
		public void onProximityUpdate() {
			noSleep();

			countOre(tile);
			dominantItem = getOutput(tile);
			dominantItems = returnCount;
		}

		@Override
		public float efficiencyScale() {
			Ranged other = nearestDetector(team, x, y);
			return other != null && other.range() >= Mathf.dst(x, y, other.x(), other.y()) ? 1f : 0f;
		}

		@Override
		public void drawSelect() {
			super.drawSelect();

			var d = nearestDetector(team, x, y);
			if (d != null) {
				Drawf.dashLine(team.color, x, y, d.x(), d.y());
			}
		}
	}
}
