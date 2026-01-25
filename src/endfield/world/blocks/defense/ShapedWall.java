package endfield.world.blocks.defense;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.Queue;
import arc.struct.Seq;
import endfield.content.Fx2;
import endfield.math.Mathm;
import endfield.util.Sprites;
import endfield.world.meta.Stats2;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Call;
import mindustry.graphics.Layer;
import mindustry.world.meta.StatUnit;

/**
 * Shaped Wall
 */
public class ShapedWall extends ConnectedWall {
	protected final Seq<Building> toDamage = new Seq<>(Building.class);
	protected final Queue<Building> queue = new Queue<>(16, Building.class);

	public float damageReduction = 0.1f;
	public float maxShareStep = 3;

	public ShapedWall(String name) {
		super(name);
		size = 1;
		teamPassable = true;
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stats2.damageReduction, damageReduction * 100, StatUnit.percent);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ShapedWallBuild::new;
	}

	public class ShapedWallBuild extends ConnectedWallBuild {
		public Seq<ShapedWallBuild> connectedWalls = new Seq<>(ShapedWallBuild.class);

		public void findLinkWalls() {
			toDamage.clear();
			queue.clear();

			queue.addLast(this);
			while (queue.size > 0) {
				Building wall = queue.removeFirst();
				toDamage.addUnique(wall);
				for (Building next : wall.proximity) {
					if (linkValid(next) && !toDamage.contains(next)) {
						toDamage.add(next);
						queue.addLast(next);
					}
				}
			}
		}

		public boolean linkValid(Building build) {
			return checkWall(build) && Mathf.dstm(tileX(), tileY(), build.tileX(), build.tileY()) <= maxShareStep;
		}

		public boolean checkWall(Building build) {
			return build != null && build.team == team && build.block == block;
		}

		@Override
		public void drawSelect() {
			super.drawSelect();

			findLinkWalls();
			for (Building wall : toDamage) {
				Draw.color(team.color);
				Draw.alpha(0.5f);
				Fill.square(wall.x, wall.y, 2);
			}
			Draw.reset();
		}

		public void updateProximityWall() {
			connectedWalls.clear();

			for (Point2 point : Sprites.proximityPos) {
				Building other = Vars.world.build(tile.x + point.x, tile.y + point.y);
				if (other == null || other.team != team) continue;
				if (checkWall(other)) {
					connectedWalls.add((ShapedWallBuild) other);
				}
			}
		}

		@Override
		public void drawTeam() {
			Draw.color(team.color);
			Draw.alpha(0.25f);
			Draw.z(Layer.blockUnder);
			Fill.square(x, y, 5f);
			Draw.color();
		}

		@Override
		public boolean checkSolid() {
			return false;
		}

		@Override
		public boolean collision(Bullet other) {
			if (other.type.absorbable) other.absorb();
			return super.collision(other);
		}

		@Override
		public float handleDamage(float amount) {
			findLinkWalls();
			float shareDamage = (amount / toDamage.size) * (1 - damageReduction);
			for (Building b : toDamage) {
				damageShared(b, shareDamage);
			}
			return shareDamage;
		}

		//todo healthChanged sometimes not trigger properly
		public void damageShared(Building building, float damage) {
			if (building.dead()) return;
			float dm = Vars.state.rules.blockHealth(team);
			if (Mathf.zero(dm)) {
				damage = building.health + 1;
			} else {
				damage /= dm;
			}
			if (!Vars.net.client()) {
				building.health -= damage;
			}
			if (damaged()) {
				healthChanged();
			}
			if (building.health <= 0) {
				Call.buildDestroyed(building);
			}
			Fx2.shareDamage.at(building.x, building.y, building.block.size * Vars.tilesize / 2f, team.color, Mathm.clamp(damage / (block.health * 0.1f)));
		}

		@Override
		public void updateProximity() {
			super.updateProximity();

			updateProximityWall();
			for (ShapedWallBuild other : connectedWalls) {
				other.updateProximityWall();
			}
		}

		@Override
		public void onRemoved() {
			for (ShapedWallBuild other : connectedWalls) {
				other.updateProximityWall();
			}

			super.onRemoved();
		}
	}
}
