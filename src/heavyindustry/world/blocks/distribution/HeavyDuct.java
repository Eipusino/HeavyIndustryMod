package heavyindustry.world.blocks.distribution;

import arc.func.Boolf;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Tmp;
import mindustry.content.Blocks;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.Junction;
import mindustry.world.blocks.sandbox.ItemSource;
import mindustry.world.blocks.sandbox.ItemVoid;

import static heavyindustry.util.Utils.split;
import static mindustry.Vars.itemSize;
import static mindustry.Vars.tilesize;

public class HeavyDuct extends TubeDuct {
	public TextureRegion[] sheetRegions;
	public Seq<Block> acceptFrom;
	public Block junctionReplacement;

	public HeavyDuct(String name) {
		super(name);
	}

	@Override
	public void init() {
		super.init();
		if (junctionReplacement == null) junctionReplacement = Blocks.junction;
	}

	@Override
	public void load() {
		super.load();
		sheetRegions = split(name + "-sheet", 32, 0);
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region};
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		Draw.rect(region, plan.drawx(), plan.drawy(), 8, plan.rotation == 1 || plan.rotation == 2 ? -8 : 8, plan.rotation * 90);
	}

	@Override
	public Block getReplacement(BuildPlan req, Seq<BuildPlan> plans) {
		if (junctionReplacement == null) return this;

		Boolf<Point2> cont = p -> plans.contains(o -> o.x == req.x + p.x && o.y == req.y + p.y && (req.block instanceof HeavyDuct || req.block instanceof Junction));
		return cont.get(Geometry.d4(req.rotation)) &&
				cont.get(Geometry.d4(req.rotation - 2)) &&
				req.tile() != null &&
				req.tile().block() instanceof HeavyDuct &&
				Mathf.mod(req.tile().build.rotation - req.rotation, 2) == 1 ? junctionReplacement : this;
	}

	@Override
	public void handlePlacementLine(Seq<BuildPlan> plans) {}

	public class HeavyDuctBuild extends TubeDuctBuild {
		public int state = 0;
		public Building last;
		public Point2 frontPos, backPos;
		public boolean frontUnder, backUnder;

		@Override
		public void draw() {
			Draw.z(Layer.blockUnder);
			Draw.rect(sheetRegions[0], x, y, 0f);
			if (frontUnder) draughted(sheetRegions[0], false);
			if (backUnder) draughted(sheetRegions[0], true);

			//draw item
			if (current != null) {
				Draw.z(Layer.blockUnder + 0.1f);
				Tmp.v1.set(Geometry.d4x(recDir) * tilesize / 2f, Geometry.d4y(recDir) * tilesize / 2f)
						.lerp(Geometry.d4x(rotation) * tilesize / 2f, Geometry.d4y(rotation) * tilesize / 2f,
								Mathf.clamp((progress + 1f) / 2f));

				Draw.rect(current.fullIcon, x + Tmp.v1.x, y + Tmp.v1.y, itemSize, itemSize);
			}

			Draw.z(Layer.blockUnder + 0.2f);
			Draw.rect(sheetRegions[state == 4 ? 2 : state + 1], x, y, state == 4 ? -8f : 8f, rotation == 1 || rotation == 2 ? -8f : 8f, rotdeg());
			Draw.rect(sheetRegions[4], x, y, rotdeg());
			if (frontUnder) {
				draughted(sheetRegions[3], false);
				draughted(sheetRegions[4], false);
			}
			if (backUnder) {
				draughted(sheetRegions[3], true);
				draughted(sheetRegions[4], true);
			}
		}

		@Override
		protected void drawAt(float x, float y, int bits, float rotation, SliceMode slice) {}

		public void draughted(TextureRegion region, boolean back) {
			Draw.rect(region, (back ? backPos.x : frontPos.x) * 8f + x, (back ? backPos.y : frontPos.y) * 8f + y, 8f, rotation == 2 ? -8f : 8f, rotdeg());
		}

		@Override
		public void onProximityUpdate() {
			noSleep();
			frontPos = Geometry.d4(rotation);
			backPos = Geometry.d4(rotation + 2);
			next = nearby(frontPos.x, frontPos.y);
			last = nearby(backPos.x, backPos.y);
			nextc = next instanceof DuctBuild d ? d : null;
			frontUnder = backUnder = false;

			state = 0;
			if (next != null && (next.block.hasItems || next instanceof ItemVoid.ItemVoidBuild)
					&& !(nextc != null && nextc.rotation != rotation)) {
				state = 1;
				frontUnder = !next.block.squareSprite;
				if (acceptFrom(last)) {
					state = 2;
					backUnder = !last.block.squareSprite;
				}
			} else if (acceptFrom(last)) {
				state = 4;
				backUnder = !last.block.squareSprite;
			}
		}

		public boolean acceptFrom(Building build) {
			return build != null && build == last &&
					(acceptFrom == null
							|| build instanceof ItemSource.ItemSourceBuild
							|| (build.block == block && build.rotation == rotation || acceptFrom.contains(build.block)));
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			return current == null && items.total() == 0 && acceptFrom(source);
		}
	}
}
