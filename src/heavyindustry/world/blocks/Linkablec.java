package heavyindustry.world.blocks;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import heavyindustry.graphics.Drawn;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Buildingc;
import mindustry.graphics.Drawf;
import mindustry.logic.Ranged;
import mindustry.world.Block;

public interface Linkablec extends Ranged, BaseBuild {
	/*default boolean onConfigureBuildTapped(Building other) {
		if (this == other || linkPos() == other.pos()) {
			configure(Tmp.p1.set(-1, -1));
			return false;
		}
		if (other.within(this, range()) && other.team == team()) {
			configure(Point2.unpack(other.pos()));
			return false;
		}
		return true;
	}*/

	default void drawLink(@Nullable Seq<Building> builds) {
		Draw.reset();
		if (builds == null) {
			if (linkValid(link())) {
				Draw.color(linkColor());
				Drawf.circles(getX(), getY(), block().size / 2f * Vars.tilesize + Mathf.absin(Time.time * Drawn.sinScl, 6f, 1f), linkColor());
				Drawn.link(build(), link(), linkColor());
			}
		} else if (builds.any()) {
			Draw.color(linkColor());
			Drawf.circles(getX(), getY(), block().size / 2f * Vars.tilesize + Mathf.absin(Time.time * Drawn.sinScl, 6f, 1f), linkColor());

			for (Building build : builds) {
				if (!linkValid(build)) continue;
				Drawn.link(build(), build, linkColor());
			}
		}

		Draw.reset();
	}

	default void drawLink() {
		drawLink(null);
	}

	default Building link() {
		return Vars.world.build(linkPos());
	}

	default boolean linkValid() {
		return linkValid(link());
	}

	default boolean linkValid(@Nullable Building b) {
		return b != null;
	}

	default void linkPos(Point2 point2) {
		linkPos(point2.pack());
	}

	int linkPos();

	void linkPos(int value);

	Color linkColor();
}
