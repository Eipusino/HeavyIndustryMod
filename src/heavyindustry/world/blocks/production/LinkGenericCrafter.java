package heavyindustry.world.blocks.production;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import heavyindustry.world.blocks.MultiBlock;
import heavyindustry.world.blocks.MultiBuild;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.Stat;

public class LinkGenericCrafter extends GenericCrafter implements MultiBlock {
	//link positions
	public int[] linkValues = {};
	public Seq<Point2> linkPos = new Seq<>(Point2.class);
	public IntSeq linkSize = new IntSeq();

	public boolean canMirror = false;
	public int[] rotations = {0, 1, 2, 3, 0, 1, 2, 3};

	public LinkGenericCrafter(String name) {
		super(name);

		hasItems = true;
		hasLiquids = true;

		rotate = true;
		rotateDraw = true;
		quickRotate = false;
		allowDiagonal = false;
	}

	@Override
	public boolean isMirror() {
		return name.endsWith("-mirror");
	}

	@Override
	public Block mirrorBlock() {
		return Vars.content.block(isMirror() ? name.replace("-mirror", "") : name + "-mirror");
	}

	@Override
	public void init() {
		super.init();
		addLink(linkValues);

		//always set those, these are not supposed to be changed
		rotateDraw = true;
		quickRotate = false;
		allowDiagonal = false;

		//always required due to Tile#getFlammability, in this case anuke sucks for this
		hasItems = true;
		hasLiquids = true;

		//current no support and i dont want things being disaster
		//outputLiquids = null;

		if (isMirror()) {
			alwaysUnlocked = true;
		}
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.remove(Stat.size);
		stats.add(Stat.size, "@x@", getMaxSize(size, 0).x, getMaxSize(size, 0).y);
	}

	@Override
	public boolean canPlaceOn(Tile tile, Team team, int rotation) {
		return super.canPlaceOn(tile, team, rotation) && checkLink(tile, team, size, rotation);
	}

	@Override
	public void placeBegan(Tile tile, Block previous) {
		createPlaceholder(tile, size);
	}

	@Override
	public void changePlacementPath(Seq<Point2> points, int rotation) {
		Placement.calculateNodes(points, this, rotation, (point, other) -> {
			if (rotation % 2 == 0) {
				return Math.abs(point.x - other.x) <= getMaxSize(size, rotation).x;
			} else {
				return Math.abs(point.y - other.y) <= getMaxSize(size, rotation).y;
			}
		});
	}

	@Override
	public void setBars() {
		super.setBars();
		if (outputLiquid == null && (outputLiquids == null || outputLiquids.length == 0)) {
			removeBar("liquid");
		}
	}

	@Override
	public Seq<Point2> linkBlockPos() {
		return linkPos;
	}

	@Override
	public IntSeq linkBlockSize() {
		return linkSize;
	}

	@Override
	public void flipRotation(BuildPlan req, boolean x) {
		if (canMirror) {
			if (mirrorBlock() != null) {
				if (x) {
					if (req.rotation == 1) req.rotation = 3;
					if (req.rotation == 3) req.rotation = 1;
				} else {
					if (req.rotation == 0) req.rotation = 2;
					if (req.rotation == 2) req.rotation = 0;
				}
				req.block = mirrorBlock();
			} else {
				req.rotation = rotations[req.rotation + (x ? 0 : 4)];
			}
		} else {
			super.flipRotation(req, x);
		}
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = LinkGenericCrafterBuild::new;
	}

	public class LinkGenericCrafterBuild extends GenericCrafterBuild implements MultiBuild {
		public boolean linkCreated = false, linkValid = true;
		public Seq<Building> linkEntities;
		//ordered seq, target-source pair
		public Seq<Building[]> linkProximityMap;
		public int dumpIndex = 0;
		public Tile teamPos, statusPos;

		@Override
		public void created() {
			super.created();
			linkProximityMap = new Seq<>(Building[].class);
		}

		@Override
		public void updateTile() {
			if (isPayload()) return;

			if (!linkCreated) {
				linkEntities = setLinkBuild(this, block, tile, team, size, rotation);
				linkCreated = true;
				updateLinkProximity();
			}

			if (!linkValid) {
				linkEntities.each(Building::kill);
				kill();
			}

			super.updateTile();
		}

		@Override
		public boolean dump(Item todump) {
			if (!hasItems || items.total() == 0 || linkProximityMap.size == 0 || (todump != null && !items.has(todump)))
				return false;
			int dump = dumpIndex;
			for (int i = 0; i < linkProximityMap.size; i++) {
				int idx = (i + dump) % linkProximityMap.size;
				Building[] pair = linkProximityMap.get(idx);
				Building target = pair[0];
				Building source = pair[1];

				if (todump == null) {
					for (int ii = 0; ii < Vars.content.items().size; ii++) {
						if (!items.has(ii)) continue;
						Item item = Vars.content.items().get(ii);
						if (target.acceptItem(source, item) && canDump(target, item)) {
							target.handleItem(source, item);
							items.remove(item, 1);
							incrementDumpIndex(linkProximityMap.size);
							return true;
						}
					}
				} else {
					if (target.acceptItem(source, todump) && canDump(target, todump)) {
						target.handleItem(source, todump);
						items.remove(todump, 1);
						incrementDumpIndex(linkProximityMap.size);
						return true;
					}
				}
				incrementDumpIndex(linkProximityMap.size);
			}
			return false;
		}

		@Override
		public void dumpLiquid(Liquid liquid, float scaling, int outputDir) {
			int dump = cdump;
			if (liquids.get(liquid) <= 0.0001f) return;
			if (!Vars.net.client() && Vars.state.isCampaign() && team == Vars.state.rules.defaultTeam) liquid.unlock();
			for (int i = 0; i < linkProximityMap.size; i++) {
				incrementDumpIndex(linkProximityMap.size);
				int idx = (i + dump) % linkProximityMap.size;
				Building[] pair = linkProximityMap.get(idx);
				Building target = pair[0];
				//Building source = pair[1];
				if (outputDir != -1 && (outputDir + rotation) % 4 != relativeTo(target)) continue;
				target = target.getLiquidDestination(this, liquid);
				if (target != null && target.block.hasLiquids && canDumpLiquid(target, liquid) && target.liquids != null) {
					float ofract = target.liquids.get(liquid) / target.block.liquidCapacity;
					float fract = liquids.get(liquid) / liquidCapacity;
					if (ofract < fract)
						transferLiquid(target, (fract - ofract) * liquidCapacity / scaling, liquid);
				}
			}
		}

		@Override
		public void offload(Item item) {
			produced(item, 1);
			int dump = dumpIndex;
			for (int i = 0; i < linkProximityMap.size; i++) {
				incrementDumpIndex(linkProximityMap.size);
				int idx = (i + dump) % linkProximityMap.size;
				Building[] pair = linkProximityMap.get(idx);
				Building target = pair[0];
				Building source = pair[1];
				if (target.acceptItem(source, item) && canDump(target, item)) {
					target.handleItem(source, item);
					return;
				}
			}
			handleItem(this, item);
		}

		@Override
		public int dumpIndex() {
			return dumpIndex;
		}

		@Override
		public void dumpIndex(int value) {
			dumpIndex = value;
		}

		@Override
		public void incrementDumpIndex(int prox) {
			dumpIndex = ((dumpIndex + 1) % prox);
		}

		@Override
		public Building build() {
			return this;
		}

		@Override
		public Block block() {
			return block;
		}

		@Override
		public Seq<Building> linkEntities() {
			return linkEntities;
		}

		@Override
		public Seq<Building[]> linkProximityMap() {
			return linkProximityMap;
		}

		@Override
		public void onProximityUpdate() {
			super.onProximityUpdate();
			updateLinkProximity();
		}

		@Override
		public void onRemoved() {
			createPlaceholder(tile, size);
		}

		@Override
		public boolean canPickup() {
			return false;
		}

		@Override
		public void drawSelect() {
			super.drawSelect();
		}

		@Override
		public void drawTeam() {
			teamPos = Vars.world.tile(tileX() + teamOverlayPos(size, rotation).x, tileY() + teamOverlayPos(size, rotation).y);
			if (teamPos != null) {
				Draw.color(team.color);
				Draw.rect("block-border", teamPos.worldx(), teamPos.worldy());
				Draw.color();
			}
		}

		@Override
		public void drawStatus() {
			statusPos = Vars.world.tile(tileX() + statusOverlayPos(size, rotation).x, tileY() + statusOverlayPos(size, rotation).y);
			if (enableDrawStatus && consumers.length > 0) {
				float multiplier = size > 1 ? 1 : 0.64F;
				Draw.z(Layer.power + 1);
				Draw.color(Pal.gray);
				Fill.square(statusPos.worldx(), statusPos.worldy(), 2.5F * multiplier, 45);
				Draw.color(status().color);
				Fill.square(statusPos.worldx(), statusPos.worldy(), 1.5F * multiplier, 45);
				Draw.color();
			}
		}
	}
}
