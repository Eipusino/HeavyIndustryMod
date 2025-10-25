package heavyindustry.world.blocks.defense;

import arc.Core;
import arc.Graphics.Cursor;
import arc.Graphics.Cursor.SystemCursor;
import arc.audio.Sound;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Sounds;
import mindustry.logic.LAccess;
import mindustry.world.blocks.defense.Wall;

/**
 * A wall with insulation properties that change with opening and closing.
 *
 * @author Eipusino
 */
public class InsulationWall extends Wall {
	public final int timerToggle = timers++;
	public Effect openFx = Fx.dooropen, closeFx = Fx.doorclose;
	public Sound doorSound = Sounds.door;
	public TextureRegion openRegion;

	public InsulationWall(String name) {
		super(name);
		consumesTap = true;

		config(Boolean.class, (InsulationWallBuild tile, Boolean open) -> {
			doorSound.at(tile);
			tile.effect();
			tile.open = open;
			Vars.world.tileChanges++;
		});
	}

	@Override
	public void load() {
		super.load();

		openRegion = Core.atlas.find(name + "-open");
	}

	@Override
	public TextureRegion getPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		return plan.config == Boolean.TRUE ? openRegion : region;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = InsulationWallBuild::new;
	}

	public class InsulationWallBuild extends WallBuild {
		public boolean open = false;

		@Override
		public double sense(LAccess sensor) {
			if (sensor == LAccess.enabled) return open ? 1 : 0;
			return super.sense(sensor);
		}

		@Override
		public void control(LAccess type, double p1, double p2, double p3, double p4) {
			if (type == LAccess.enabled) {
				boolean shouldOpen = !Mathf.zero(p1);

				if (Vars.net.client() || open == shouldOpen || timer(timerToggle, 60f)) {
					return;
				}

				configureAny(shouldOpen);
			}
		}

		public void effect() {
			(open ? closeFx : openFx).at(this, size);
		}

		@Override
		public void draw() {
			Draw.rect(open ? openRegion : region, x, y);
		}

		@Override
		public Cursor getCursor() {
			return interactable(Vars.player.team()) ? SystemCursor.hand : SystemCursor.arrow;
		}

		@Override
		public boolean isInsulated() {
			return !open;
		}

		@Override
		public void tapped() {
			if (!timer(timerToggle, 60f)) return;

			configure(!open);
		}

		@Override
		public Object config() {
			return open;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.bool(open);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			open = read.bool();
		}
	}
}
