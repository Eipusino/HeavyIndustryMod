package heavyindustry.world.blocks.defense;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.graphics.Drawn;
import heavyindustry.world.blocks.LinkGroupc;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.ui.Fonts;
import mindustry.world.blocks.defense.Wall;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class DPSWallDisplay extends Wall {
	public float range = 1200f;
	public float boardTimeTotal = 60 * 6;

	public int maxLink = 100;

	public Color linkColor = Color.white;

	public DPSWallDisplay(String name) {
		super(name);

		update = true;
		solid = true;
		configurable = true;

		config(Integer.class, (DPSWallDisplayBuild tile, Integer index) -> tile.linkPos(index));
		config(Point2.class, (DPSWallDisplayBuild tile, Point2 point) -> tile.linkPos(point));
		config(Point2[].class, (DPSWallDisplayBuild tile, Point2[] points) -> {
			for (Point2 p : points) {
				tile.linkPos(Point2.pack(p.x + tile.tileX(), p.y + tile.tileY()));
			}
		});
	}

	public class DPSWallDisplayBuild extends WallBuild implements LinkGroupc {
		public float totalDamage, hits, firstHitTime, lastHitTime, showBoardTime;

		public IntSeq links = new IntSeq(maxLink);

		@Override
		public void health(float health) {
			super.health(health);

			damage(health);
		}

		@Override
		public void damage(float amount) {
			totalDamage += amount;
			hits += 1;
			if (firstHitTime == 0) {
				firstHitTime = Time.time;
			}
			showBoardTime = boardTimeTotal;
			lastHitTime = Time.time;
		}

		@Override
		public void updateTile() {
			super.updateTile();

			showBoardTime = Math.max(showBoardTime - Time.delta, 0);
			if (showBoardTime == 0 && totalDamage > 0) {
				totalDamage = 0;
				hits = 0;
				firstHitTime = 0;
				lastHitTime = 0;
				showBoardTime = 0;
			}
		}

		@Override
		public void draw() {
			super.draw();

			if (showBoardTime > 0) {
				Font font = Fonts.def;
				Color color = Color.yellow.cpy();
				float fontSize = 12f / 60f;
				float gap = Vars.mobile ? fontSize / 0.04f : fontSize / 0.06f;
				float dx = x - 13;
				float dy = y + (Vars.mobile ? 29 : 17);

				float gameDuration = lastHitTime - firstHitTime;
				float realDuration = gameDuration / 60;
				float damage = totalDamage;
				float dps = damage / (realDuration == 0 ? 1 : realDuration);

				float z = Draw.z();
				Draw.z(Layer.weather + 1);
				color.a = Math.min(showBoardTime / boardTimeTotal * 3, 1);
				font.draw("Hits: " + hits, dx, (dy -= gap), color, fontSize, false, Align.left);
				font.draw("Damage: " + damage, dx, (dy -= gap), color, fontSize, false, Align.left);
				font.draw("DPS: " + dps, dx, dy - gap, color, fontSize, false, Align.left);
				Draw.z(z);
				Draw.reset();
			}
		}

		@Override
		public void remove() {
			if (added) {
				linkBuilds().each(b -> {
					if (b instanceof DPSWall.DPSWallBuild wall) {
						wall.linkRemove(this);
					}
				});
			}

			super.remove();
		}

		@Override
		public void drawConfigure() {
			float sin = Mathf.absin(Time.time, 6, 1);

			Draw.color(Pal.accent);
			Lines.stroke(1);
			Drawf.circles(x, y, (tile.block().size / 2f + 1f) * tilesize + sin - 2, Pal.accent);

			for (int i = 0; i < links.size; i++) {
				int pos = links.get(i);
				if (linkValid()) {
					Building linkTarget = world.build(pos);
					if (linkValid(linkTarget)) {
						Drawf.square(linkTarget.x, linkTarget.y, linkTarget.block.size * tilesize / 2f + 1f, Pal.place);
					}
				}
			}
			Drawf.dashCircle(x, y, range, Pal.accent);
		}

		@Override
		public boolean onConfigureBuildTapped(Building other) {
			if (this == other) {
				return false;
			}
			if (dst(other) <= range && other.team == team && other instanceof DPSWall.DPSWallBuild) {
				configure(other.pos());
				return false;
			}
			return true;
		}

		@Override
		public Point2[] config() {
			Point2[] out = new Point2[links.size];
			for (int i = 0; i < out.length; i++) {
				out[i] = Point2.unpack(links.get(i)).sub(tile.x, tile.y);
			}
			return out;
		}

		public boolean linkValid(int pos) {
			if (pos == -1) return false;
			Building linkTarget = world.build(pos);
			return linkTarget instanceof DPSWall.DPSWallBuild && within(linkTarget, range);
		}

		@Override
		public boolean linkValid() {
			for (Building b : linkBuilds()) {
				if (!linkValid(b)) {
					links.removeValue(b.pos());
					if (b instanceof DPSWall.DPSWallBuild wall) wall.linkRemove(this);
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean linkValid(@Nullable Building b) {
			return b instanceof DPSWall.DPSWallBuild && b.team == team;
		}

		@Override
		public IntSeq linkGroup() {
			return links;
		}

		@Override
		public void linkGroup(IntSeq seq) {
			links = seq;
		}

		@Override
		public void drawLink(Seq<Building> builds) {
			Draw.reset();
			if (builds == null) {
				if (linkValid(link())) {
					Draw.color(getLinkColor());
					Drawf.circles(getX(), getY(), size / 2f * tilesize + Mathf.absin(Time.time * Drawn.sinScl, 6f, 1f), getLinkColor());
					Drawn.link(this, link(), getLinkColor());
				}
			} else if (builds.any()) {
				Draw.color(getLinkColor());
				Drawf.circles(getX(), getY(), size / 2f * tilesize + Mathf.absin(Time.time * Drawn.sinScl, 6f, 1f), getLinkColor());

				for (Building b : builds) {
					if (!linkValid(b)) continue;
					Drawn.link(this, b, getLinkColor());
				}
			}

			Draw.reset();
		}

		@Override
		public Building link() {
			return world.build(links.first());
		}

		@Override
		public int linkPos() {
			return pos();
		}

		@Override
		public void linkPos(int value) {
			Building other = world.build(value);

			if (other instanceof DPSWall.DPSWallBuild wall && !links.removeValue(value) && links.size < maxLink - 1 && within(other, range())) {
				links.add(value);
				wall.linkAdd(this);
			}
		}

		@Override
		public Color getLinkColor() {
			return linkColor;
		}

		@Override
		public float range() {
			return range;
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			TypeIO.writeIntSeq(write, links);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			links = TypeIO.readIntSeq(read);
		}
	}
}
