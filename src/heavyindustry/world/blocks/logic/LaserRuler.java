package heavyindustry.world.blocks.logic;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.graphics.DrawText;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.entities.Effect;
import mindustry.game.EventType.TapEvent;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.logic.Ranged;
import mindustry.world.Block;
import mindustry.world.Tile;

import static mindustry.Vars.control;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class LaserRuler extends Block {
	protected static final Seq<Runnable> drawRunners = new Seq<>(Runnable.class);
	protected static Tile lastTaped;

	static {
		Events.on(TapEvent.class, event -> {
			lastTaped = event.tile;

			Building selectedTile = control.input.config.getSelected();
			if (selectedTile instanceof LaserRulerBuild build) {
				if (build.tile == lastTaped) {
					build.configure(null);
					build.deselect();
				} else {
					build.setTarget(lastTaped.pos());
				}
			}
		});
	}

	public Effect selectTile = Fx.none;

	boolean laserRuler = true;

	public LaserRuler(String name) {
		super(name);
		update = true;
		destructible = true;
		configurable = true;
		config(Integer.class, (LaserRulerBuild build, Integer in) -> build.target = in);
		config(Point2.class, (LaserRulerBuild tile, Point2 po) -> tile.target = Point2.pack(po.x + tile.tileX(), po.y + tile.tileY()));
		configClear((LaserRulerBuild build) -> build.target = -1);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = LaserRulerBuild::new;
	}

	public class LaserRulerBuild extends Building implements Ranged {
		public final Seq<Tile> xtiles = new Seq<>(Tile.class);
		public final Seq<Tile> ytiles = new Seq<>(Tile.class);
		public int target = -1;

		@Override
		public Object config() {
			return Point2.unpack(target).sub(tile.x, tile.y);
		}

		@Override
		public void update() {
			super.update();
			if (!validTarget(target)) return;
			rebuildTiles();
		}

		public void rebuildTiles() {
			Tile target = targetTile();
			xtiles.clear();
			World.raycast(tile.x, tile.y, target.x, tile.y, (x, y) -> {
				xtiles.add(world.tile(x, y));
				return false;
			});
			ytiles.clear();
			World.raycast(target.x, tile.y, target.x, target.y, (x, y) -> {
				ytiles.add(world.tile(x, y));
				return false;
			});
		}

		protected void drawSelectedTile(Tile tile, Color color) {
			Drawf.select(tile.worldx(), tile.worldy(), tilesize / 2f, color);
		}

		protected void drawLinePart(Tile cur, Tile next, Color color) {
			drawLinePart(cur, next, color, color);
		}

		protected void drawLinePart(Tile cur, Tile next, Color color, Color divColor) {
			float x1 = cur.worldx(), y1 = cur.worldy();
			float x2 = next.worldx(), y2 = next.worldy();
			float rotation = Angles.angle(x1, y1, x2, y2);
			Tmp.v1.trns(rotation + 90, 2.0f);

			Draw.color(Pal.gray);
			Lines.stroke(3);
			Lines.line(x1, y1, x2, y2, false);
			Lines.line(x1 + Tmp.v1.x, y1 + Tmp.v1.y, x1 - Tmp.v1.x, y1 - Tmp.v1.y);
			Lines.line(x2 + Tmp.v1.x, y2 + Tmp.v1.y, x2 - Tmp.v1.x, y2 - Tmp.v1.y);
			drawRunners.add(() -> {
				Draw.color(color);
				Lines.stroke(1f);
				Lines.line(x1, y1, x2, y2, false);
				Tmp.v1.trns(rotation + 90, 2.0f);
				Draw.color(divColor);
				Lines.line(x1 + Tmp.v1.x, y1 + Tmp.v1.y, x1 - Tmp.v1.x, y1 - Tmp.v1.y);
				Lines.line(x2 + Tmp.v1.x, y2 + Tmp.v1.y, x2 - Tmp.v1.x, y2 - Tmp.v1.y);
			});
		}

		@Override
		public void draw() {
			super.draw();

			if (!validTarget(target)) return;

			Tile target = targetTile();
			Color tenColor = Color.red.cpy().lerp(Color.white, 0.5f);
			Color color = new Color(0x877badff);//bf92f8 8a73c6 665c9f
			drawSelectedTile(target, color);
			drawRunners.clear();
			int counter = 0;
			for (int i = 0; i < xtiles.size - 1; i++) {
				Tile cur = xtiles.get(i), next = xtiles.get(i + 1);
				if (cur == null || next == null) continue;
				drawLinePart(cur, next, color, (counter++ + 10) % 10 == 0 ? tenColor : color);

			}
			for (int i = 0; i < ytiles.size - 1; i++) {
				Tile cur = ytiles.get(i), next = ytiles.get(i + 1);
				if (cur == null || next == null) continue;
				drawLinePart(cur, next, color, (counter++ + 10) % 10 == 0 ? tenColor : color);
			}
			drawRunners.each(Runnable::run);
			drawRunners.clear();
			Draw.draw(Layer.flyingUnit + 4, () -> {
				Tile targetTile = targetTile();
				Tmp.v1.trns(tile.angleTo(targetTile), size * tilesize);
				DrawText.drawText(x + Tmp.v1.x, y + Tmp.v1.y, Pal.heal, String.valueOf(dstTileToTarget()));
				drawTiles(xtiles);
				drawTiles(ytiles);
			});
		}

		protected void drawTiles(Seq<Tile> tiles) {
			if (tiles.size > 2) {
				Tile tile = tiles.getFrac(0.5f);
				if (tile != null) {
					DrawText.drawText(tile.worldx(), tile.worldy(), Pal.heal, String.valueOf(tiles.size - 2));
				}
			}
		}

		@Override
		public double sense(LAccess sensor) {
			Tile tile = targetTile();
			return switch (sensor) {
				case shootX -> tile == null ? -1 : tile.x;
				case shootY -> tile == null ? -1 : tile.y;
				case shooting -> tile == null ? 0 : 1;
				default -> super.sense(sensor);
			};
		}

		protected Tile targetTile() {
			return validTarget(target) ? world.tile(target) : null;
		}

		@Override
		public boolean onConfigureTapped(float x, float y) {
			return lastTaped == tile;
		}

		@Override
		public float range() {
			return dstToTarget();
		}

		protected int dstTileToTarget() {
			if (!validTarget(target)) return -1;
			return (int) (dstToTarget() / tilesize);
		}

		protected boolean validTarget(int pos) {
			if (pos == -1 || world.tile(pos) == null) return false;
			return world.tile(pos).x == tile.x || world.tile(pos).y == tile.y || laserRuler;
		}

		protected float dstToTarget() {
			if (!validTarget(target)) return -1;
			Tile t = world.tile(target);
			return Mathf.dst(t.worldx(), t.worldy(), tile.worldx(), tile.worldy()) - 8f;
		}

		public void setTarget(int tar) {
			if (validTarget(tar)) {
				target = tar;
				rebuildTiles();
				deselect();
			} else {
				Fx.unitCapKill.at(world.tile(tar));
			}
			selectTile.at(world.tile(tar));
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.i(target);
		}

		@Override
		public byte version() {
			return 1;
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			if (revision == 0) return;
			target = read.i();
		}
	}
}
