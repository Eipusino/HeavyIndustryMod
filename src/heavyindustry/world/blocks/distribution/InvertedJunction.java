package heavyindustry.world.blocks.distribution;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.BufferItem;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.blocks.distribution.Junction;

import static heavyindustry.HVars.MOD_NAME;
import static heavyindustry.util.Utils.split;
import static mindustry.Vars.content;
import static mindustry.Vars.tilesize;

/**
 * Inverted Junction
 */
public class InvertedJunction extends Junction {
	public final int size = 1;
	public String placeSprite;
	public Color[] colors = {new Color(0xbf92f9ff), new Color(0xc0ecffff), new Color(0x84f491ff), new Color(0xfffa763)};

	public TextureRegion place, arrow1, arrow2, flip;
	public TextureRegion[] locs;

	public InvertedJunction(String name) {
		super(name);

		sync = true;
		configurable = true;
		config(Integer.class, (InvertedJunctionBuild tile, Integer loc) -> tile.loc = loc);
	}

	@Override
	public void load() {
		super.load();
		place = Core.atlas.find(placeSprite);
		locs = split(MOD_NAME + "-junction", 32, 0);
		arrow1 = Core.atlas.find(MOD_NAME + "-arrow-1");
		arrow2 = Core.atlas.find(MOD_NAME + "-arrow-2");
		flip = Core.atlas.find(MOD_NAME + "-flip");
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = InvertedJunctionBuild::new;
	}

	public class InvertedJunctionBuild extends JunctionBuild {
		public int loc = 1;

		@Override
		public void configured(Unit player, Object value) {
			super.configured(player, value);
			if (value instanceof Number in) loc = in.intValue();
		}

		@Override
		public void updateTile() {
			for (int i = 0; i < 4; i++) {
				int p = (i + loc) % 4;
				if (buffer.indexes[i] > 0) {
					if (buffer.indexes[i] > capacity) buffer.indexes[i] = capacity;
					long l = buffer.buffers[i][0];
					float time = BufferItem.time(l);

					if (Time.time >= time + speed / timeScale || Time.time < time) {

						Item item = content.item(BufferItem.item(l));
						Building dest = nearby(p);

						if (item == null || dest == null || !dest.acceptItem(this, item) || dest.team != team) {
							continue;
						}

						dest.handleItem(this, item);
						System.arraycopy(buffer.buffers[i], 1, buffer.buffers[i], 0, buffer.indexes[i] - 1);
						buffer.indexes[i]--;
					}
				}
			}
		}

		@Override
		public void draw() {
			Draw.rect(region, x, y);
			Draw.rect(place, x, y);
			Draw.rect(locs[loc(loc)], x, y);
		}

		protected int loc(int loc) {
			return switch (loc) {
				case 1 -> 0;
				case 3 -> 1;
				default -> locs.length - 1;
			};
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			int relative = source.relativeTo(tile);

			if (relative == -1 || !buffer.accepts(relative)) return false;
			Building to = nearby((relative + loc) % 4);
			return to != null && to.team == team;
		}

		@Override
		public void drawSelect() {
			super.drawSelect();

			float sin = Mathf.sin(Time.time, 6, 0.6f);
			for (int i = 0; i < 4; i++) {
				Draw.color(colors[i]);
				int in = loc == 1 ? 3 : 1;
				int input = (i + in) % 4;
				Draw.rect(arrow1, x + Geometry.d4x(i) * (tilesize + sin), y + Geometry.d4y(i) * (tilesize + sin), 90 * i);
				Draw.rect(arrow2, x + Geometry.d4x(input) * (tilesize - sin), y + Geometry.d4y(input) * (tilesize - sin), 90 * input);
				Draw.color();
			}
		}

		@Override
		public void buildConfiguration(Table table) {
			table.button(new TextureRegionDrawable(flip), Styles.cleari, this::switchF).size(36f).tooltip("switch");
		}

		public void switchF() {
			loc = loc == 1 ? 3 : 1;
			deselect();
			configure(loc);

		}

		@Override
		public Object config() {
			return loc;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.i(loc);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			loc = read.i();
		}
	}
}
