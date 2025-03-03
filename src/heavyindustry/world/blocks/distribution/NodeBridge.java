package heavyindustry.world.blocks.distribution;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

/**
 * The difference from CardanItemBridge is that its connection has also been changed to the laser of the power node.
 */
public class NodeBridge extends ItemBridge {
	public TextureRegion laser, laserEnd;

	public NodeBridge(String name) {
		super(name);
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		drawPotentialLinks(x, y);
		drawOverlay(x * tilesize + offset, y * tilesize + offset, rotation);
		Drawf.dashCircle(x * tilesize, y * tilesize, range * tilesize, Pal.accent);
	}

	/**
	 * Change its connection method to range connection.
	 */
	@Override
	public boolean linkValid(Tile tile, Tile other, boolean checkDouble) {
		if (other == null || tile == null || other == tile) return false;
		if (!tile.within(other, range * tilesize + 0.1f)) return false;
		return ((other.block() == tile.block() && tile.block() == this) || (!(tile.block() instanceof NodeBridge) && other.block() == this))
				&& (other.team() == tile.team() || tile.block() != this)
				&& (!checkDouble || ((NodeBridgeBuild) other.build).link != tile.pos());
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stat.range, range, StatUnit.blocks);
	}

	@Override
	public void load() {
		super.load();
		laser = Core.atlas.find(name + "-laser", "laser");
		laserEnd = Core.atlas.find(name + "-laser-end", "laser-end");
	}

	public class NodeBridgeBuild extends ItemBridgeBuild {
		@Override
		public void updateTile() {
			Building other = world.build(link);
			if (other != null && !linkValid(tile, other.tile)) {
				link = -1;
			}
			super.updateTile();
		}

		@Override
		public void updateTransport(Building other) {
			if (hasItems) super.updateTransport(other);
			if (hasLiquids) {
				if (warmup >= 0.25f) {
					moved |= moveLiquid(other, liquids.current()) > 0.05f;
				}
			}
		}

		@Override
		public void doDump() {
			if (hasItems) super.doDump();
			if (hasLiquids) dumpLiquid(liquids.current(), 1f);
		}

		@Override
		public void drawConfigure() {
			float sin = Mathf.absin(Time.time, 6, 1);

			Draw.color(Pal.accent);
			Lines.stroke(1);
			Drawf.circles(x, y, (block.size / 2f + 1) * tilesize + sin - 2, Pal.accent);
			Building other = world.build(link);
			if (other != null) {
				Drawf.circles(other.x, other.y, (block.size / 3f + 1) * tilesize + sin - 2, Pal.place);
				Drawf.arrow(x, y, other.x, other.y, block.size * tilesize + sin, 4 + sin, Pal.accent);
			}
			Drawf.dashCircle(x, y, range * tilesize, Pal.accent);
		}

		@Override
		public void draw() {
			Draw.rect(region, x, y);

			Draw.z(Layer.power);
			Building other = world.build(link);
			if (other == null) return;
			float op = Core.settings.getInt("bridgeopacity") / 100f;
			if (Mathf.zero(op)) return;

			Draw.color(Color.white);
			if (block.hasPower) Draw.alpha(Math.max(power.status, 0.25f) * op);
			else Draw.alpha(op);

			Draw.rect(laserEnd, x, y);
			Draw.rect(laserEnd, other.x, other.y);

			Lines.stroke(8);

			Tmp.v1.set(x, y).sub(other.x, other.y).setLength(tilesize / 2f).scl(-1);

			Lines.line(laser, x, y, other.x, other.y, false);
			Draw.reset();
		}

		/**
		 * Modify its items and output items to be omnidirectional.
		 */
		@Override
		protected boolean checkAccept(Building source, Tile other) {
			if (tile == null || linked(source)) return true;
			return linkValid(tile, other);
		}

		@Override
		protected boolean checkDump(Building to) {
			return true;
		}
	}
}
