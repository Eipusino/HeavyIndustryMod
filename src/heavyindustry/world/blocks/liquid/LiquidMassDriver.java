package heavyindustry.world.blocks.liquid;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.Eachable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arc.util.pooling.Pool.Poolable;
import arc.util.pooling.Pools;
import heavyindustry.entities.bullet.LiquidMassDriverBolt;
import heavyindustry.graphics.Outliner;
import heavyindustry.util.CollectionOrderedSet;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.MultiPacker;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.RotBlock;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.control;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class LiquidMassDriver extends Block {
	public TextureRegion prefRegion, baseRegion, bottomRegion, liquidRegion, topRegion;

	public float range;
	public float rotateSpeed = 5f;
	public float translation = 7f;
	public int minDistribute = 50;
	public float knockback = 4f;
	public float reload = 100f;

	public LiquidMassDriverBolt bullet = new LiquidMassDriverBolt();

	public float bulletSpeed = 5.5f;
	public float bulletLifetime = 200f;

	public Effect shootEffect = Fx.shootBig2;
	public Effect smokeEffect = Fx.shootBigSmoke2;
	public Effect receiveEffect = Fx.mineBig;
	public Sound shootSound = Sounds.shootBig;
	public float shake = 3f;

	public LiquidMassDriver(String name) {
		super(name);
		update = true;
		solid = true;
		configurable = true;
		hasLiquids = true;
		liquidCapacity = 200f;
		hasPower = true;
		sync = true;
		envEnabled |= Env.space;
		outputsLiquid = true;

		group = BlockGroup.liquids;

		//point2 is relative
		config(Point2.class, (LiquidMassDriverBuild tile, Point2 point) -> tile.link = Point2.pack(point.x + tile.tileX(), point.y + tile.tileY()));
		config(Integer.class, (LiquidMassDriverBuild tile, Integer point) -> tile.link = point);
	}

	@Override
	public void load() {
		super.load();

		baseRegion = Core.atlas.find(name + "-base");
		prefRegion = Core.atlas.find(name + "-pref");
		bottomRegion = Core.atlas.find(name + "-bottom");
		liquidRegion = Core.atlas.find(name + "-liquid");
		topRegion = Core.atlas.find(name + "-top");
	}

	@Override
	protected TextureRegion[] icons() {
		return new TextureRegion[]{baseRegion, bottomRegion, prefRegion, topRegion};
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		Draw.rect(baseRegion, plan.drawx(), plan.drawy());
		Draw.rect(bottomRegion, plan.drawx(), plan.drawy());
		Draw.rect(prefRegion, plan.drawx(), plan.drawy());
		Draw.rect(topRegion, plan.drawx(), plan.drawy());
	}

	@Override
	public void createIcons(MultiPacker packer) {
		Outliner.outlineRegion(packer, prefRegion, outlineColor, name + "-pref-outline", outlineRadius);

		super.createIcons(packer);
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.add(Stat.shootRange, range / tilesize, StatUnit.blocks);
		stats.add(Stat.reload, 60f / reload, StatUnit.perSecond);
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);

		Drawf.dashCircle(x * tilesize, y * tilesize, range, Pal.accent);

		//check if a mass driver is selected while placing this driver
		if (!control.input.config.isShown()) return;
		Building selected = control.input.config.getSelected();
		if (selected == null || selected.block != this || !selected.within(x * tilesize, y * tilesize, range)) return;

		//if so, draw a dotted line towards it while it is in range
		float sin = Mathf.absin(Time.time, 6f, 1f);
		Tmp.v1.set(x * tilesize + offset, y * tilesize + offset).sub(selected.x, selected.y).limit((size / 2f + 1) * tilesize + sin + 0.5f);
		float x2 = x * tilesize - Tmp.v1.x, y2 = y * tilesize - Tmp.v1.y,
				x1 = selected.x + Tmp.v1.x, y1 = selected.y + Tmp.v1.y;
		int segs = (int) (selected.dst(x * tilesize, y * tilesize) / tilesize);

		Lines.stroke(4f, Pal.gray);
		Lines.dashLine(x1, y1, x2, y2, segs);
		Lines.stroke(2f, Pal.placing);
		Lines.dashLine(x1, y1, x2, y2, segs);
		Draw.reset();
	}

	public enum DriverState {
		idle,
		accepting,
		shooting;

		public static final DriverState[] all = values();
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = LiquidMassDriverBuild::new;
	}

	public static class LiquidBulletData implements Poolable {
		public LiquidMassDriverBuild from, to;
		public Liquid liquid = Liquids.water;
		public float amount = 0;

		@Override
		public void reset() {
			from = null;
			to = null;
			liquid = Liquids.water;
			amount = 0;
		}
	}

	public class LiquidMassDriverBuild extends Building implements RotBlock {
		public int link = -1;
		public float rotation = 90;
		public float reloadCounter = 0f;
		public DriverState state = DriverState.idle;
		//TODO use queue? this array usually holds about 3 shooters max anyway
		public CollectionOrderedSet<Building> waitingShooters = new CollectionOrderedSet<>(Building.class);

		public Building currentShooter() {
			return waitingShooters.isEmpty() ? null : waitingShooters.first();
		}

		public float liquidTotal() {
			return liquids.get(liquids.current());
		}

		@Override
		public void updateTile() {
			Building lin = world.build(link);
			boolean hasLink = linkValid();

			if (hasLink) {
				link = lin.pos();
			}

			//reload regardless of state
			if (reloadCounter > 0f) {
				reloadCounter = Mathf.clamp(reloadCounter - edelta() / reload);
			}

			Building current = currentShooter();

			//cleanup waiting shooters that are not valid
			if (current != null && !shooterValid(current)) {
				waitingShooters.remove(current);
			}

			//switch states
			if (state == DriverState.idle) {
				//start accepting when idle and there's space
				if (!waitingShooters.isEmpty() && (liquidCapacity - liquidTotal() >= minDistribute)) {
					state = DriverState.accepting;
				} else if (hasLink) { //switch to shooting if there's a valid link.
					state = DriverState.shooting;
				}
			}

			//dump when idle or accepting
			if (state == DriverState.idle || state == DriverState.accepting) {
				dumpLiquid(liquids.current());
			}

			//skip when there's no power
			if (efficiency <= 0f) {
				return;
			}

			if (state == DriverState.accepting) {

				if (currentShooter() == null || (liquidCapacity - liquidTotal() < minDistribute)) {
					state = DriverState.idle;
					return;
				}

				//align to shooter rotation
				rotation = Angles.moveToward(rotation, angleTo(currentShooter()), rotateSpeed * efficiency);
			} else if (state == DriverState.shooting) {

				if (!hasLink || (!waitingShooters.isEmpty() && (liquidCapacity - liquidTotal() >= minDistribute))) {
					state = DriverState.idle;
					return;
				}

				float targetRotation = angleTo(lin);

				if (liquidTotal() >= minDistribute && lin.block.liquidCapacity - lin.liquids.get(lin.liquids.current()) >= minDistribute) {
					LiquidMassDriverBuild other = (LiquidMassDriverBuild) lin;
					other.waitingShooters.add(this);

					if (reloadCounter <= 0.0001f) {

						//align to target location
						rotation = Angles.moveToward(rotation, targetRotation, rotateSpeed * efficiency);

						//fire when it's the first in the queue and angles are ready.
						if (other.currentShooter() == this &&
								other.state == DriverState.accepting &&
								Angles.near(rotation, targetRotation, 2f) && Angles.near(other.rotation, targetRotation + 180f, 2f)) {
							//actually fire
							fire(other);
							float timeToArrive = Math.min(bulletLifetime, dst(other) / bulletSpeed);
							Time.run(timeToArrive, () -> {
								//remove waiting shooters, it's done firing
								other.waitingShooters.remove(this);
								other.state = DriverState.idle;
							});
							//driver is immediately idle
							state = DriverState.idle;
						}
					}
				}
			}
		}

		@Override
		public double sense(LAccess sensor) {
			if (sensor == LAccess.progress) return Mathf.clamp(1f - reloadCounter / reload);
			return super.sense(sensor);
		}

		@Override
		public void draw() {
			Draw.rect(baseRegion, x, y);
			Draw.z(Layer.turret);
			Drawf.shadow(prefRegion,
					x + Angles.trnsx(rotation + 180, reloadCounter * knockback) - (size / 2f),
					y + Angles.trnsy(rotation + 180, reloadCounter * knockback) - (size / 2f), rotation - 90);
			Draw.rect(bottomRegion,
					x + Angles.trnsx(rotation + 180, reloadCounter * knockback),
					y + Angles.trnsy(rotation + 180, reloadCounter * knockback), rotation - 90);
			Draw.rect(prefRegion,
					x + Angles.trnsx(rotation + 180, reloadCounter * knockback),
					y + Angles.trnsy(rotation + 180, reloadCounter * knockback), rotation - 90);
			Draw.color(liquids.current().color);
			Draw.alpha(Math.min(liquidTotal() / liquidCapacity, 1));
			Draw.rect(liquidRegion,
					x + Angles.trnsx(rotation + 180, reloadCounter * knockback),
					y + Angles.trnsy(rotation + 180, reloadCounter * knockback), rotation - 90);
			Draw.color();
			Draw.alpha(1);
			Draw.rect(topRegion,
					x + Angles.trnsx(rotation + 180, reloadCounter * knockback),
					y + Angles.trnsy(rotation + 180, reloadCounter * knockback), rotation - 90);
		}

		@Override
		public void drawConfigure() {
			float sin = Mathf.absin(Time.time, 6f, 1f);

			Draw.color(Pal.accent);
			Lines.stroke(1f);
			Drawf.circles(x, y, (tile.block().size / 2f + 1) * tilesize + sin - 2f, Pal.accent);

			for (Building shooter : waitingShooters) {
				Drawf.circles(shooter.x, shooter.y, (tile.block().size / 2f + 1) * tilesize + sin - 2f, Pal.place);
				Drawf.arrow(shooter.x, shooter.y, x, y, size * tilesize + sin, 4f + sin, Pal.place);
			}

			if (linkValid()) {
				Building target = world.build(link);
				Drawf.circles(target.x, target.y, (target.block.size / 2f + 1) * tilesize + sin - 2f, Pal.place);
				Drawf.arrow(x, y, target.x, target.y, size * tilesize + sin, 4f + sin);
			}

			Drawf.dashCircle(x, y, range, Pal.accent);
		}

		@Override
		public boolean onConfigureBuildTapped(Building other) {
			if (this == other) {
				if (link == -1) deselect();
				configure(-1);
				return false;
			}

			if (link == other.pos()) {
				configure(-1);
				return false;
			} else if (other.block == block && other.dst(tile) <= range && other.team == team) {
				configure(other.pos());
				return false;
			}

			return true;
		}

		@Override
		public boolean acceptLiquid(Building source, Liquid liquid) {
			return liquidTotal() < liquidCapacity && linkValid();
		}

		@Override
		public void dumpLiquid(Liquid liquid) {
			if (linkValid()) return;
			super.dumpLiquid(liquid);
		}

		protected void fire(LiquidMassDriverBuild target) {
			//reset reload, use power.
			reloadCounter = 1f;

			LiquidBulletData data = Pools.obtain(LiquidBulletData.class, LiquidBulletData::new);
			data.from = this;
			data.to = target;
			data.liquid = liquids.current();
			data.amount = liquidTotal();
			liquids.clear();

			float angle = tile.angleTo(target);

			bullet.create(this, team,
					x + Angles.trnsx(angle, translation), y + Angles.trnsy(angle, translation),
					angle, -1f, bulletSpeed, bulletLifetime, data);

			shootEffect.at(x + Angles.trnsx(angle, translation), y + Angles.trnsy(angle, translation), angle);
			smokeEffect.at(x + Angles.trnsx(angle, translation), y + Angles.trnsy(angle, translation), angle);

			Effect.shake(shake, shake, this);

			shootSound.at(tile, Mathf.random(0.9f, 1.1f));
		}

		public void handlePayload(Bullet bullet, LiquidBulletData data) {
			liquids.add(data.liquid, data.amount);

			Effect.shake(shake, shake, this);
			receiveEffect.at(bullet);

			reloadCounter = 1f;
			bullet.remove();
		}

		protected boolean shooterValid(Building other) {
			if (other instanceof LiquidMassDriverBuild entity && other.isValid() && other.efficiency > 0) {
				return entity.block == block && entity.link == pos() && within(other, range);
			}
			return false;
		}

		protected boolean linkValid() {
			if (link == -1) return false;
			Building linked = world.build(link);
			if (linked instanceof LiquidMassDriverBuild other) {
				return other.block == block && other.team == team && within(other, range);
			}
			return false;
		}

		@Override
		public Point2 config() {
			if (tile == null) return null;
			return Point2.unpack(link).sub(tile.x, tile.y);
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.i(link);
			write.f(rotation);
			write.b((byte) state.ordinal());
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			link = read.i();
			rotation = read.f();
			state = DriverState.all[read.b()];
		}

		@Override
		public float buildRotation() {
			return rotation;
		}
	}
}
