package endfield.type.unit;

import arc.math.Mathf;
import mindustry.Vars;
import mindustry.ai.ControlPathfinder;
import mindustry.ai.Pathfinder;
import mindustry.ai.types.FlyingAI;
import mindustry.ai.types.GroundAI;
import mindustry.entities.units.AIController;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.world.Tile;
import mindustry.world.meta.Env;

public class AirSeaAmphibiousUnitType extends UnitType2 {
	public float airReloadMulti = 0.75f;
	public float airShootingSpeedMulti = 0.8f;

	public AirSeaAmphibiousUnitType(String name) {
		super(name);

		envEnabled |= Env.space;
		pathCost = ControlPathfinder.costHover;
		canBoost = true;

		aiController = () -> new GroundAI() {
			@Override
			public AIController fallback() {
				return new FlyingAI() {
					@Override
					public void updateMovement() {
						Building core = unit.closestEnemyCore();

						if (core != null && unit.within(core, unit.range() / 1.3f + core.block.size * Vars.tilesize / 2f)) {
							target = core;
							for (var mount : unit.mounts) {
								if (mount.weapon.controllable && mount.weapon.bullet.collidesGround) {
									mount.target = core;
								}
							}
						}

						boolean boosting = false;
						if ((core == null || !unit.within(core, unit.type.range * 0.5f))) {
							boolean move = true;

							if (core != null) {
								if (unit.type.canBoost && Mathf.len(core.tileX() - unit.tileX(), core.tileY() - unit.tileY()) > 50) {
									unit.elevation = Mathf.approachDelta(unit.elevation, 1, unit.type.riseSpeed);
									boosting = true;
								}
							}

							if (Vars.state.rules.waves && unit.team == Vars.state.rules.defaultTeam) {
								Tile spawner = getClosestSpawner();
								if (unit.type.canBoost && Mathf.len(spawner.x - unit.tileX(), spawner.y - unit.tileY()) > 50) {
									unit.elevation = Mathf.approachDelta(unit.elevation, 1, unit.type.riseSpeed);
									boosting = true;
								}
								if (spawner != null && unit.within(spawner, Vars.state.rules.dropZoneRadius + 120f))
									move = false;
								if (spawner == null && core == null) move = false;
							}

							//no reason to move if there's nothing there
							if (core == null && (!Vars.state.rules.waves || getClosestSpawner() == null)) {
								move = false;
							}

							if (move) {
								moveTo(core != null ? core : getClosestSpawner(), Vars.state.rules.dropZoneRadius + 130f);
							}
						}

						if (unit.type.canBoost) {
							unit.elevation = Mathf.approachDelta(unit.elevation, boosting || unit.onSolid() || (unit.isFlying() && !unit.canLand()) ? 1f : 0f, unit.type.riseSpeed);
						}

						faceTarget();
					}
				};
			}

			@Override
			public boolean useFallback() {
				return unit.isFlying();
			}

			@Override
			public void updateMovement() {
				Building core = unit.closestEnemyCore();

				if (core != null && unit.within(core, unit.range() / 1.3f + core.block.size * Vars.tilesize / 2f)) {
					target = core;
					for (var mount : unit.mounts) {
						if (mount.weapon.controllable && mount.weapon.bullet.collidesGround) {
							mount.target = core;
						}
					}
				}

				if ((core == null || !unit.within(core, unit.type.range * 0.5f))) {
					boolean move = true;

					if (core != null) {
						if (unit.type.canBoost && Mathf.len(core.tileX() - unit.tileX(), core.tileY() - unit.tileY()) > 50) {
							unit.elevation = Mathf.approachDelta(unit.elevation, 1, unit.type.riseSpeed);
						}
					}

					if (Vars.state.rules.waves && unit.team == Vars.state.rules.defaultTeam) {
						Tile spawner = getClosestSpawner();
						if (unit.type.canBoost && Mathf.len(spawner.x - unit.tileX(), spawner.y - unit.tileY()) > 50) {
							unit.elevation = Mathf.approachDelta(unit.elevation, 1, unit.type.riseSpeed);
						}
						if (spawner != null && unit.within(spawner, Vars.state.rules.dropZoneRadius + 120f))
							move = false;
						if (spawner == null && core == null) move = false;
					}

					//no reason to move if there's nothing there
					if (core == null && (!Vars.state.rules.waves || getClosestSpawner() == null)) {
						move = false;
					}

					if (move) {
						pathfind(Pathfinder.fieldCore);
					}
				}

				if (unit.type.canBoost) {
					unit.elevation = Mathf.approachDelta(unit.elevation, unit.onSolid() || (unit.isFlying() && !unit.canLand()) ? 1f : 0f, unit.type.riseSpeed);
				}

				faceTarget();
			}
		};
	}

	@Override
	public void update(Unit unit) {
		super.update(unit);

		if (unit.isFlying()) {
			unit.reloadMultiplier *= airReloadMulti;
			if (unit.isShooting) {
				unit.speedMultiplier *= airShootingSpeedMulti;
			}
		}
	}
}
