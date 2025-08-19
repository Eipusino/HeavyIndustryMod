package heavyindustry.world.blocks.heat;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.core.Renderer;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static heavyindustry.core.HeavyIndustryMod.MOD_NAME;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class HeatDriver extends Block {
	public int range = 240;
	public float visualMaxHeat = 15f;
	public DrawBlock drawer = new DrawDefault();
	public boolean splitHeat = false;

	public HeatDriver(String name) {
		super(name);

		sync = true;
		envEnabled |= Env.space;
		configurable = true;
		hasPower = true;
		update = solid = rotate = true;
		rotateDraw = false;
		size = 3;

		config(Point2.class, (HeatDriverBuild tile, Point2 point) -> tile.link = Point2.pack(point.x + tile.tileX(), point.y + tile.tileY()));
		config(Integer.class, (HeatDriverBuild tile, Integer point) -> tile.link = point);
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stat.shootRange, (float) range / tilesize, StatUnit.blocks);
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);
		Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.accent);
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("heat", (HeatDriverBuild tile) -> new Bar(() -> Core.bundle.format("bar.heatamount", (int) (tile.heat + 0.001f)), () -> Pal.lightOrange, () -> tile.heat / visualMaxHeat));
	}

	@Override
	public void load() {
		super.load();

		drawer.load(this);
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		drawer.drawPlan(this, plan, list);
	}

	@Override
	public TextureRegion[] icons() {
		return drawer.finalIcons(this);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = HeatDriverBuild::new;
	}

	public static class DrawHeatDriver extends DrawBlock {
		public TextureRegion turretPart, turretLine, rPart, rLine, effect, arrow, preview;

		public float arrowSpacing = 4f, arrowOffset = 2f, arrowPeriod = 0.4f, arrowTimeScl = 6.2f;

		public Color dc1 = Color.valueOf("ea8878");

		@Override
		public void draw(Building build) {
			if (!(build instanceof HeatDriverBuild bu && build.block instanceof HeatDriver bl)) return;

			float progress = bu.progress;
			float rotation = bu.rotation;
			float resProgress = bu.resProgress;

			Draw.z(Layer.turret);

			float move = 3f * progress;

			Drawf.shadow(turretPart, bu.x, bu.y, rotation - 90f);

			Drawf.shadow(rPart, bu.x + Angles.trnsx(rotation + 180f, 0f, move) - bl.size / 2f, bu.y + Angles.trnsy(rotation + 180f, 0f, move) - bl.size / 2f, rotation - 90f);

			Draw.rect(rLine, bu.x + Angles.trnsx(rotation + 180f, 0f, move), bu.y + Angles.trnsy(rotation + 180f, 0f, move), rotation - 90f);

			Draw.rect(turretLine, bu.x, bu.y, rotation - 90f);
			Draw.rect(turretPart, bu.x, bu.y, rotation - 90f);

			Draw.rect(rPart, bu.x + Angles.trnsx(rotation + 180f, 0f, move), bu.y + Angles.trnsy(rotation + 180f, 0f, move), rotation - 90f);

			float p = Math.min((bu.heat / bl.visualMaxHeat) * bu.power.status, 1);

			Draw.color(dc1);
			Draw.alpha(p);
			Draw.z(Layer.effect);

			if (progress > 0.01f) {
				Draw.rect(effect, bu.x + Angles.trnsx(rotation + 180f, -8f), bu.y + Angles.trnsy(rotation + 180f, -8f), 10f * progress, 10f * progress, rotation - 90f - Time.time * 2);
				Draw.rect(effect, bu.x + Angles.trnsx(rotation + 180f, -8f), bu.y + Angles.trnsy(rotation + 180f, -8f), 6f * progress, 6f * progress, rotation - 90f + Time.time * 2);

				for (int i = 0; i < 4; i++) {
					float angle = i * 360f / 4;
					Drawf.tri(bu.x + Angles.trnsx(rotation + 180f, -8f) + Angles.trnsx(angle + Time.time, 5f), bu.y + Angles.trnsy(rotation + 180f, -8f) + Angles.trnsy(angle + Time.time, 5f), 6f, 2f * progress, angle + Time.time);
				}
			}

			if (resProgress > 0.01f) {
				Draw.alpha(1);
				Lines.stroke(1 * resProgress);
				Lines.circle(bu.x + Angles.trnsx(rotation + 180f, 10f), bu.y + Angles.trnsy(rotation + 180f, 10f), 5);
				Lines.circle(bu.x + Angles.trnsx(rotation + 180f, 10f), bu.y + Angles.trnsy(rotation + 180f, 10f), 3);

				for (int i = 0; i < 3; i++) {
					float angle = i * 360f / 3;
					Drawf.tri(bu.x + Angles.trnsx(rotation + 180f, 10f) + Angles.trnsx(angle - Time.time, 5), bu.y + Angles.trnsy(rotation + 180f, 10f) + Angles.trnsy(angle - Time.time, 5f), 4f, -2f * resProgress, angle - Time.time);
					Drawf.tri(bu.x + Angles.trnsx(rotation + 180f, 10f) + Angles.trnsx(angle + Time.time, 3), bu.y + Angles.trnsy(rotation + 180f, 10f) + Angles.trnsy(angle + Time.time, 3f), 3f, 1 * resProgress, angle + Time.time);
				}
			}

			if (bu.linkValid()) {
				Building other = world.build(bu.link);
				if (!Angles.near(rotation, bu.angleTo(other), 2f)) return;
				Draw.color();
				float dist = bu.dst(other) / arrowSpacing - bl.size;
				int arrows = (int) (dist / arrowSpacing);

				for (int a = 0; a < arrows; a++) {
					Draw.alpha(Mathf.absin(a - Time.time / arrowTimeScl, arrowPeriod, 1f) * progress * Renderer.bridgeOpacity * p);
					Draw.rect(arrow, bu.x + Angles.trnsx(rotation + 180f, -arrowSpacing) * (tilesize / 2f + a * arrowSpacing + arrowOffset), bu.y + Angles.trnsy(rotation + 180f, -arrowSpacing) * (tilesize / 2f + a * arrowSpacing + arrowOffset), 25f, 25f, rotation);
				}
			}
		}

		@Override
		public void load(Block block) {
			turretPart = Core.atlas.find(block.name + "-turret");
			turretLine = Core.atlas.find(block.name + "-turret-outline");
			rPart = Core.atlas.find(block.name + "-reflect");
			rLine = Core.atlas.find(block.name + "-reflect-outline");
			effect = Core.atlas.find(block.name + "-effect", MOD_NAME + "-heat-driver-effect");
			arrow = Core.atlas.find(block.name + "-arrow", MOD_NAME + "-heat-driver-arrow");
			preview = Core.atlas.find(block.name + "-preview");
		}

		@Override
		public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
			Draw.rect(preview, plan.drawx(), plan.drawy());
		}

		@Override
		public TextureRegion[] icons(Block block) {
			return new TextureRegion[]{preview};
		}
	}

	public class HeatDriverBuild extends Building implements HeatBlock, HeatConsumer {
		public float rotation = 90f;
		public float progress = 0f;
		public float resProgress = 0f;
		public int link = -1;
		public Seq<Building> owners = new Seq<>(Building.class);

		public float heat = 0f;
		public float[] sideHeat = new float[4];
		public IntSet cameFrom = new IntSet();
		public long lastHeatUpdate = -1;

		@Override
		public void draw() {
			drawer.draw(this);
		}

		@Override
		public void drawLight() {
			super.drawLight();
			drawer.drawLight(this);
		}

		@Override
		public void updateTile() {
			if (owners.isEmpty() && link == -1) heat = 0;

			checkOwner();

			Building linked = world.build(link);
			boolean hasLink = linkValid();

			if (hasLink) {
				HeatDriverBuild other = (HeatDriverBuild) linked;
				if (other.checkOneOwner(this)) other.owners.add(this);
				float toRotation = angleTo(other);
				rotation = Mathf.slerpDelta(rotation, toRotation, 0.02f * power.status);
				if (Angles.near(rotation, toRotation, 2)) {
					updateTransfer();
					other.updateTransfer();
					progress = Mathf.slerpDelta(progress, 1, 0.02f * power.status);
				} else {
					progress = Mathf.slerpDelta(progress, 0, 0.04f);
				}
			} else {
				progress = Mathf.slerpDelta(progress, 0, 0.04f);
			}
			float p = Math.min((heat / visualMaxHeat), 1);
			if (owners.any() && p > 0) {
				resProgress = Mathf.slerpDelta(resProgress, 1, 0.02f * p);
			} else {
				resProgress = Mathf.slerpDelta(resProgress, 0, 0.05f);
			}
		}

		public void updateTransfer() {
			if (owners.any()) {
				float totalHeat = 0f;
				for (int i = 0; i < owners.size; i++) {
					HeatDriverBuild owner = (HeatDriverBuild) owners.get(i);
					if (Angles.near(owner.rotation, owner.angleTo(this), 2f)) {
						totalHeat += owner.heat;
						totalHeat *= owner.power.status;
					}
				}
				heat = totalHeat;
			} else {
				updateHeat();
			}
		}

		public void updateHeat() {
			if (lastHeatUpdate == state.updateId) return;

			lastHeatUpdate = state.updateId;
			heat = calculateHeat(sideHeat, cameFrom);
		}

		@Override
		public float heatRequirement() {
			return linkValid() ? visualMaxHeat : Float.MAX_VALUE;
		}

		@Override
		public float warmup() {
			return heat;
		}

		@Override
		public float heat() {
			return (owners.any() && link == -1) ? heat : 0;
		}

		@Override
		public float heatFrac() {
			return (heat / visualMaxHeat) / (splitHeat ? 3f : 1);
		}

		@Override
		public float[] sideHeat() {
			return sideHeat;
		}

		@Override
		public void drawConfigure() {
			float sin = Mathf.absin(Time.time, 6f, 1f);

			Draw.color(Pal.accent);
			Lines.stroke(1f);
			Drawf.circles(x, y, (tile.block().size / 2f + 1) * tilesize + sin - 2f, Pal.accent);

			owners.each(owner -> {
				Drawf.circles(owner.x, owner.y, (tile.block().size / 2f + 1) * tilesize + sin - 2f, Pal.place);
				Drawf.arrow(owner.x, owner.y, x, y, size * tilesize + sin, 4f + sin, Pal.place);
			});

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
			} else if (other.block == block && other.dst(tile) <= range && other.team == team && checkOneOwner(other)) {
				configure(other.pos());
				return false;
			}

			return true;
		}

		public void checkOwner() {
			for (int i = 0; i < owners.size; i++) {
				int pos = owners.get(i).pos();
				Building build = world.build(pos);
				if (build instanceof HeatDriverBuild owner) {
					if (owner.block != block || owner.link != this.pos()) owners.remove(i);
				} else {
					owners.remove(i);
				}
			}
		}

		protected boolean checkOneOwner(Building other) {
			if (owners.isEmpty()) return true;
			return !owners.contains(other);
		}

		@Override
		public Point2 config() {
			if (tile == null) return null;
			return Point2.unpack(link).sub(tile.x, tile.y);
		}

		public boolean linkValid() {
			if (link == -1) return false;
			Building other = world.build(link);
			if (other == null) {
				link = -1;
				return false;
			}
			return other.block == block && other.team == team && within(other, range);
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.i(link);
			write.f(rotation);
			write.f(progress);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			link = read.i();
			rotation = read.f();
			progress = read.f();
		}
	}
}
