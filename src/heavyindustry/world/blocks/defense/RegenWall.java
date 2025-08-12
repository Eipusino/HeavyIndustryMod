package heavyindustry.world.blocks.defense;

import arc.Events;
import arc.math.Mathf;
import arc.struct.IntFloatMap;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Damage;
import mindustry.entities.TargetPriority;
import mindustry.game.EventType.BuildingBulletDestroyEvent;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import static mindustry.Vars.indexer;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

/**
 * A wall that can self restore life.
 *
 * @author Eipusino
 */
public class RegenWall extends Wall {
	protected static final IntSet taken = new IntSet();
	protected static final IntFloatMap mendMap = new IntFloatMap();
	protected static long lastUpdateFrame = -1l;

	/** per frame. */
	public float healPercent = 12f / 60f;
	/** Chance of wall to heal itself on collision. -1 to disable. */
	public float chanceHeal = -1f;
	/** How much wall heals at collision. Based on bullet damage. */
	public float regenPercent = 0.1f;

	public RegenWall(String name) {
		super(name);
		group = BlockGroup.walls;
		priority = TargetPriority.wall;
		buildCostMultiplier = 6f;
		crushDamageMultiplier = 5f;
		update = true;
		hasPower = false;
		hasItems = false;
		canOverdrive = false;
		envEnabled = Env.any;
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		drawPotentialLinks(x, y);
		drawOverlay(x * tilesize + offset, y * tilesize + offset, rotation);
	}

	@Override
	public boolean outputsItems() {
		return false;
	}

	public class RegenWallBuild extends WallBuild {
		public Seq<Building> targets = new Seq<>(Building.class);
		public int lastChange = -2;
		public float warmup, totalTime;
		public boolean anyTargets = false;
		public boolean didRegen = false;

		protected float healAmount, hit;
		protected boolean heals;

		public void updateTargets() {
			targets.clear();
			taken.clear();
			indexer.eachBlock(team, Tmp.r1.setCentered(x, y, tilesize), b -> true, targets::add);
		}

		@Override
		public void updateTile() {
			if (lastChange != world.tileChanges) {
				lastChange = world.tileChanges;
				updateTargets();
			}

			//TODO should warmup depend on didRegen?
			warmup = Mathf.approachDelta(warmup, didRegen ? 1f : 0f, 1f / 70f);
			totalTime += warmup * Time.delta;
			didRegen = false;
			anyTargets = false;

			//no healing when suppressed
			if (checkSuppression()) {
				return;
			}

			anyTargets = targets.contains(Building::damaged);

			if (efficiency > 0) {
				float healAmount = optionalEfficiency * healPercent;

				//use Math.max to prevent stacking
				for (Building build : targets) {
					if (!build.damaged() || build.isHealSuppressed()) continue;

					didRegen = true;

					int pos = build.pos();
					//TODO periodic effect
					float value = mendMap.get(pos);
					mendMap.put(pos, Math.min(Math.max(value, healAmount * edelta() * build.block.health / 100f), build.block.health - build.health));
				}
			}

			if (lastUpdateFrame != state.updateId) {
				lastUpdateFrame = state.updateId;

				for (var entry : mendMap.entries()) {
					Building build = world.build(entry.key);
					if (build != null) {
						build.heal(entry.value);
						build.recentlyHealed();
					}
				}
				mendMap.clear();
			}

			hit = Mathf.clamp(hit - Time.delta / 10f);

			if (damaged() && heals) {
				heals = false;
				heal(healAmount);
			}
		}

		@Override
		public boolean collision(Bullet bullet) {
			boolean wasDead = health <= 0;

			float damage = bullet.damage() * bullet.type().buildingDamageMultiplier;
			if (!bullet.type.pierceArmor) {
				damage = Damage.applyArmor(damage, block.armor);
			}

			damage(bullet.team, damage);
			Events.fire(bulletDamageEvent.set(this, bullet));

			if (health <= 0 && !wasDead) {
				Events.fire(new BuildingBulletDestroyEvent(this, bullet));
			}

			hit = 1f;

			if (Mathf.chance(chanceHeal)) {
				healAmount = bullet.damage * regenPercent;
				heals = true;
			}

			if (chanceDeflect > 0f && bullet.vel.len() > 0.1f && bullet.type.reflectable && Mathf.chance(chanceDeflect / bullet.damage)) {
				bullet.trns(-bullet.vel.x, -bullet.vel.y);

				if (Math.abs(x - bullet.x) > Math.abs(y - bullet.y)) {
					bullet.vel.x *= -1f;
				} else {
					bullet.vel.y *= -1f;
				}

				bullet.owner = this;
				bullet.team = team;
				bullet.time += 1f;
				return false;
			}

			return true;
		}

		@Override
		public void drawSelect() {
			block.drawOverlay(x, y, rotation);
		}

		@Override
		public float warmup() {
			return warmup;
		}

		@Override
		public float totalProgress() {
			return totalTime;
		}
	}
}
