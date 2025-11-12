package heavyindustry.world.blocks.distribution;

import arc.Graphics;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Eachable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.util.SpriteUtils;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Conveyor;

import static mindustry.Vars.itemSize;
import static mindustry.Vars.player;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class RailConveyor extends Conveyor2 {
	public TextureRegion[] edgeRegions, armorRegions, arrowRegions, pulseRegions;
	public float framePeriod = 8f;

	public RailConveyor(String name) {
		super(name);

		canOverdrive = false;
		placeableLiquid = true;
		drawTeamOverlay = false;

		config(Boolean.class, (RailConveyorBuild build, Boolean armored) -> build.armored = armored);
	}

	@Override
	public void load() {
		super.load();

		edgeRegions = SpriteUtils.split(name + "-edge", 32, 5, 1);
		armorRegions = SpriteUtils.split(name + "-edge-armored", 32, 5, 1);
		arrowRegions = SpriteUtils.split(name + "-arrow", 32, 16, 2);
		pulseRegions = SpriteUtils.split(name + "-pulse", 32, 5, 3);
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		int[] bits = getTiling(plan, list);

		if (bits == null) return;
		Draw.rect(region, plan.drawx(), plan.drawy(), plan.rotation * 90);
	}

	@Override
	public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
		boolean armored = (tile.build instanceof RailConveyorBuild conveyor && conveyor.armored);
		if (!armored)
			return (otherblock.outputsItems() || (lookingAt(tile, rotation, otherx, othery, otherblock) && otherblock.hasItems))
					&& lookingAtEither(tile, rotation, otherx, othery, otherrot, otherblock);
		else
			return (otherblock.outputsItems() && blendsArmored(tile, rotation, otherx, othery, otherrot, otherblock)) ||
					(lookingAt(tile, rotation, otherx, othery, otherblock) && otherblock.hasItems);
	}

	@Override
	public boolean blendsArmored(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
		return Point2.equals(tile.x + Geometry.d4(rotation).x, tile.y + Geometry.d4(rotation).y, otherx, othery)
				|| ((!otherblock.rotatedOutput(otherx, othery) && Edges.getFacingEdge(otherblock, otherx, othery, tile) != null &&
				Edges.getFacingEdge(otherblock, otherx, othery, tile).relativeTo(tile) == rotation) ||
				(otherblock instanceof Conveyor && otherblock.rotatedOutput(otherx, othery) && Point2.equals(otherx + Geometry.d4(otherrot).x, othery + Geometry.d4(otherrot).y, tile.x, tile.y)));
	}

	public boolean blends(Building self, Building other) {
		if (other == null) return false;
		return blends(self.tile, self.rotation, other.tileX(), other.tileY(), other.rotation, other.block);
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);
	}

	public int conveyorFrame() {
		return (int) ((((Time.time) % framePeriod) / framePeriod) * 16);
	}

	public int pulseFrame() {
		int value = (int) ((Time.time / 4) % 4);
		return switch (value) {
			case 1 -> 1;
			case 2 -> 2;
			case 3 -> 3;
			default -> 0;
		};
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = RailConveyorBuild::new;
	}

	public class RailConveyorBuild extends ConveyorBuild2 {
		public int drawIndex = 0;
		public boolean armored = false;

		@Override
		public void tapped() {
			super.tapped();
			Fx.placeBlock.at(this, size);
			Sounds.click.at(this);
			configure(!armored);

			onProximityUpdate();
		}

		@Override
		public Graphics.Cursor getCursor() {
			return interactable(player.team()) ? Graphics.Cursor.SystemCursor.hand : Graphics.Cursor.SystemCursor.arrow;
		}

		@Override
		public Object config() {
			return armored;
		}

		@Override
		public void onProximityUpdate() {
			super.onProximityUpdate();
			drawIndex = 0;
			if (check(tile.x, tile.y + 1)) drawIndex += 1;
			if (check(tile.x + 1, tile.y)) drawIndex += 2;
			if (check(tile.x, tile.y - 1)) drawIndex += 4;
			if (check(tile.x - 1, tile.y)) drawIndex += 8;
		}

		public boolean check(int x, int y) {
			Building other = Vars.world.build(x, y);
			return blends(this, other);
		}

		@Override
		public void draw() {
			Draw.blend(Blending.additive);
			Draw.color(team.color, Pal.gray, 0.35f);
			Draw.z(Layer.block - 0.25f);
			Draw.rect(pulseRegions[blendbits + pulseFrame() * 5], x, y, tilesize * blendsclx, tilesize * blendscly, rotation * 90);
			Draw.blend();

			Draw.color(team.color, Color.white, 0.65f);
			Draw.z(Layer.block - 0.2f);
			Draw.rect(arrowRegions[conveyorFrame()], x, y, tilesize * blendsclx, tilesize * blendscly, rotation * 90);

			boolean backDraw = true;
			if (blends(this, right())) {
				Draw.rect(arrowRegions[conveyorFrame() + 16], x, y, rotdeg() + 90);
				backDraw = false;
			}
			if (blends(this, back())) {
				Draw.rect(arrowRegions[conveyorFrame() + 16], x, y, rotdeg());
				backDraw = false;
			}
			if (blends(this, left())) {
				Draw.rect(arrowRegions[conveyorFrame() + 16], x, y, rotdeg() - 90);
				backDraw = false;
			}
			if (backDraw) {
				Draw.rect(arrowRegions[conveyorFrame() + 16], x, y, rotdeg());
			}

			Draw.z(Layer.block - 0.15f);
			Draw.color(team.color, Color.white, 0.3f);
			if (!armored) {
				Draw.rect(edgeRegions[blendbits], x, y, tilesize * blendsclx, tilesize * blendscly, rotation * 90);
			} else {
				Draw.rect(armorRegions[blendbits], x, y, tilesize * blendsclx, tilesize * blendscly, rotation * 90);
			}
			Draw.color();

			Draw.z(Layer.block - 0.1f);
			float layer = Layer.block - 0.1f, wwidth = world.unitWidth(), wheight = world.unitHeight(), scaling = 0.01f;

			for (int i = 0; i < len; i++) {
				Item item = ids[i];
				Tmp.v1.trns(rotation * 90, tilesize, 0);
				Tmp.v2.trns(rotation * 90, -tilesize / 2f, xs[i] * tilesize / 2f);

				float
						ix = (x + Tmp.v1.x * ys[i] + Tmp.v2.x),
						iy = (y + Tmp.v1.y * ys[i] + Tmp.v2.y);

				//keep draw position deterministic.
				Draw.z(layer + (ix / wwidth + iy / wheight) * scaling);
				Draw.rect(item.fullIcon, ix, iy, itemSize, itemSize);
			}
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			return super.acceptItem(source, item) && (!armored || (source.block instanceof Conveyor || Edges.getFacingEdge(source.tile, tile).relativeTo(tile) == rotation));
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.bool(armored);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			armored = read.bool();
		}
	}
}
