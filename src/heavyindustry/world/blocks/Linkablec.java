package heavyindustry.world.blocks;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.graphics.Drawn;
import mindustry.gen.Building;
import mindustry.gen.Buildingc;
import mindustry.graphics.Drawf;
import mindustry.logic.Ranged;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public interface Linkablec extends Buildingc, Ranged {
	@Deprecated
	Seq<Building> tmpSeq = new Seq<>(1);

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

	void drawLink(@Nullable Seq<Building> builds);/* {
		Draw.reset();
		if (builds == null) {
			if (linkValid(link())) {
				Draw.color(getLinkColor());
				Drawf.circles(getX(), getY(), block().size / 2f * tilesize + Mathf.absin(Time.time * Drawn.sinScl, 6f, 1f), getLinkColor());
				Drawn.link(this, link(), getLinkColor());
			}
		} else if (builds.any()) {
			Draw.color(getLinkColor());
			Drawf.circles(getX(), getY(), block().size / 2f * tilesize + Mathf.absin(Time.time * Drawn.sinScl, 6f, 1f), getLinkColor());

			for (Building b : builds) {
				if (!linkValid(b)) continue;
				Drawn.link(this, b, getLinkColor());
			}
		}

		Draw.reset();
	}*/

	default void drawLink() {
		drawLink(null);
	}

	default Building link() {
		return world.build(linkPos());
	}

	default boolean linkValid() {
		return linkValid(link());
	}

	default boolean linkValid(Building b) {
		return b != null;
	}

	default void linkPos(Point2 point2) {
		linkPos(point2.pack());
	}

	int linkPos();

	void linkPos(int value);

	Color getLinkColor();
}
