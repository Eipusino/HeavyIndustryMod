package heavyindustry.world.blocks.storage;

import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.scene.ui.layout.Table;
import arc.struct.IntIntMap;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Interval;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.core.UI;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.entities.EntityGroup;
import mindustry.game.EventType.BlockBuildEndEvent;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.blocks.storage.StorageBlock;

/**
 * Resource distribution center, directly delivering resources to target buildings.
 *
 * @author abomb4 2022-12-08 21:16:40
 */
public class ResourcesDispatchingCenter extends StorageBlock {
	public static final EntityGroup<Building> rdcGroup = new EntityGroup<>(Building.class, false, false);

	public Color color = Color.valueOf("fea947");
	public Effect outEffect = Fx.none;

	/** max loop per frame */
	public int maxLoop = 50;
	/** Distribute every few frames */
	public int frameDelay = 5;

	public int range = 80 * 8;
	public float warmupSpeed = 0.05f;

	public TextureRegion topRegion;
	public TextureRegion bottomRegion;
	public TextureRegion rotatorRegion;

	public Seq<ItemHave> tmpWhatHave = new Seq<>(true, 32, ItemHave.class);

	public ResourcesDispatchingCenter(String name) {
		super(name);
		canOverdrive = false;
		update = true;
		solid = true;
		hasItems = true;
		configurable = true;
		saveConfig = false;
		noUpdateDisabled = true;

		config(IntSeq.class, (ResourcesDispatchingCenterBuild tile, IntSeq sq) -> {
			IntSeq links = new IntSeq();
			int linkX = -999;
			for (int i = 0; i < sq.size; i++) {
				int num = sq.get(i);
				if (linkX == -999) {
					linkX = num;
				} else {
					int pos = Point2.pack(linkX + tile.tileX(), num + tile.tileY());
					links.add(pos);
					linkX = -999;
				}
			}
			tile.setLinks(links);
		});

		config(Integer.class, ResourcesDispatchingCenterBuild::setOneLink);

		configClear((ResourcesDispatchingCenterBuild tile) -> tile.setLinks(new IntSeq()));
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		Drawf.dashCircle(x * Vars.tilesize, y * Vars.tilesize, range, Pal.accent);
		super.drawPlace(x, y, rotation, valid);
	}

	@Override
	public void load() {
		super.load();
		topRegion = Core.atlas.find(name + "-top");
		bottomRegion = Core.atlas.find(name + "-bottom");
		rotatorRegion = Core.atlas.find(name + "-rotator");
	}

	@Override
	public void init() {
		if (outEffect == Fx.none)
			outEffect = new Effect(38f, e -> {
				Draw.color(color);
				Angles.randLenVectors(e.id, 1, 8f * e.fout(), 0f, 360f, (x, y) -> {
					float angle = Angles.angle(0, 0, x, y);
					float trnsx = Angles.trnsx(angle, 2f);
					float trnsy = Angles.trnsy(angle, 2f);
					float trnsx2 = Angles.trnsx(angle, 4f);
					float trnsy2 = Angles.trnsy(angle, 4f);
					Fill.circle(
							e.x + trnsx + x + trnsx2 * e.fin(),
							e.y + trnsy + y + trnsy2 * e.fin(),
							e.fslope() * 0.8f
					);
				});
			});

		super.init();

		Events.on(BlockBuildEndEvent.class, (event -> {
			if (!event.breaking) {
				rdcGroup.each(cen -> {
					if (cen instanceof ResourcesDispatchingCenterBuild rdc)
						rdc.tryResumeDeadLink(event.tile.pos());
				});
			}
		}));
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("capacity", (ResourcesDispatchingCenterBuild tile) -> new Bar(
				() -> Core.bundle.format("bar.capacity", UI.formatAmount(tile.block.itemCapacity)),
				() -> Pal.items,
				() -> ((float) tile.items.total()) / (tile.block.itemCapacity * Vars.content.items().count(UnlockableContent::unlockedNow))
		));
	}

	@Override
	public boolean outputsItems() {
		return false;
	}

	@Override
	public Object pointConfig(Object config, Cons<Point2> transformer) {
		if (config instanceof IntSeq seq) {
			IntSeq newSeq = new IntSeq(seq.size);
			boolean linkXed = false;
			int linkX = 0;
			for (int i = 0; i < seq.size; i++) {
				int num = seq.get(i);
				if (linkXed) {
					linkX = num;
				} else {
					// The source position is relative to right bottom, transform it.
					Point2 point = new Point2(linkX * 2 - 1, num * 2 - 1);
					transformer.get(point);
					newSeq.add((point.x + 1) / 2);
					newSeq.add((point.y + 1) / 2);
				}
				linkXed = !linkXed;
			}
			return newSeq;
		} else {
			return config;
		}
	}

	/**
	 * Determine whether the target is within the influence range of the
	 *
	 * @param the	build
	 * @param target build
	 * @return Is it within the scope of influence
	 */
	public boolean linkValidTarget(Building the, Building target) {
		return target != null && target.team == the.team && the.within(target, range);
	}

	/**
	 * Determine whether POS is within the influence range of the
	 *
	 * @param the build
	 * @param pos index
	 * @return Is it within the scope of influence
	 */
	public boolean linkValid(Building the, int pos) {
		if (pos == -1) {
			return false;
		}
		Building build = Vars.world.build(pos);
		return linkValidTarget(the, build);
	}

	public static class ItemHave {
		public Item item;
		public int count;

		public ItemHave(Item item, int count) {
			this.item = item;
			this.count = count;
		}
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ResourcesDispatchingCenterBuild::new;
	}

	public class ResourcesDispatchingCenterBuild extends StorageBuild {
		public Interval timer = new Interval(6);
		public IntSeq links = new IntSeq();
		public IntSeq deadLinks = new IntSeq(100);
		public float warmup = 0;
		public float rotateDeg = 0;
		public float rotateSpeed = 0;

		public boolean itemSent = false;

		public int loopIndex = 0;

		/**
		 * Batch settings
		 *
		 * @param linkSeq list
		 */
		public void setLinks(IntSeq linkSeq) {
			links = linkSeq;
			for (int i = links.size - 1; i >= 0; i--) {
				int link = links.get(i);
				Building linkTarget = Vars.world.build(link);
				if (!linkValidTarget(this, linkTarget)) {
					links.removeIndex(i);
				} else {
					links.set(i, linkTarget.pos());
				}
			}
		}

		/**
		 * Toggle
		 *
		 * @param pos pos
		 */
		public void setOneLink(int pos) {
			if (!links.removeValue(pos)) {
				links.add(pos);
			}
		}

		/**
		 * Move a certain location to 'headLink' and perform position correction when rebuilding the building at the specified location
		 *
		 * @param pos position
		 */
		public void deadLink(int pos) {
			if (Vars.net.client()) {
				return;
			}
			if (links.contains(pos)) {
				configure(pos);
			}
			deadLinks.add(pos);
			if (deadLinks.size >= 100) {
				// If there's too much, just clear some
				deadLinks.removeRange(0, 50);
			}
		}

		/**
		 * Attempt to restore
		 *
		 * @param pos Location code
		 */
		public void tryResumeDeadLink(int pos) {
			if (Vars.net.client()) {
				return;
			}
			if (!deadLinks.removeValue(pos)) {
				return;
			}
			Building linkTarget = Vars.world.build(pos);
			if (linkValid(this, pos)) {
				configure(linkTarget.pos());
			}
		}

		/**
		 * Delivering resources to the target
		 *
		 * @param target	target
		 * @param whatIHave Existing resources
		 * @return Is the sending successful
		 */
		public boolean sendItems(Building target, Seq<ItemHave> whatIHave) {
			boolean s = false;
			for (int i = whatIHave.size - 1; i >= 0; i--) {
				ItemHave have = whatIHave.get(i);
				Item item = have.item;
				int count = have.count;
				int accept = Math.min(count, target.acceptStack(item, Math.min(count, frameDelay), this));
				if (accept > 0) {
					s = true;
					target.handleStack(item, accept, this);
					items.remove(item, accept);
					have.count -= accept;
					if (have.count <= 0) {
						whatIHave.remove(i);
					}
				}
			}
			return s;
		}

		@Override
		public void updateTile() {
			super.updateTile();
			tmpWhatHave.clear();
			boolean valid = efficiency > 0.4F;
			if (timer.get(1, frameDelay)) {
				itemSent = false;
				if (valid) {
					for (Item item : Vars.content.items()) {
						int count = items.get(item);
						if (count > 0) {
							tmpWhatHave.add(new ItemHave(item, count));
						}
					}

					int max = links.size;
					for (int i = 0; i < Math.min(maxLoop, max); i++) {
						if (loopIndex < 0) {
							loopIndex = max - 1;
						}
						int index = loopIndex;
						loopIndex -= 1;
						int pos = links.get(index);
						if (pos == -1) {
							configure(pos);
							continue;
						}
						Building linkTarget = Vars.world.build(pos);
						if (!linkValidTarget(this, linkTarget)) {
							deadLink(pos);
							max -= 1;
							if (max <= 0) {
								break;
							}
							continue;
						}
						if (sendItems(linkTarget, tmpWhatHave)) {
							itemSent = true;
						}
					}
				}
			}

			if (valid) {
				warmup = Mathf.lerpDelta(warmup, links.isEmpty() ? 0 : 1, warmupSpeed);
				rotateSpeed = Mathf.lerpDelta(rotateSpeed, itemSent ? 1 : 0, warmupSpeed);
			} else {
				warmup = Mathf.lerpDelta(warmup, 0, warmupSpeed);
				rotateSpeed = Mathf.lerpDelta(rotateSpeed, 0, warmupSpeed);
			}

			if (warmup > 0) {
				rotateDeg += rotateSpeed;
			}

			if (enabled && rotateSpeed > 0.5 && Mathf.random(60) > 48) {
				Time.run(Mathf.random(10), () -> outEffect.at(x, y, 0));
			}
		}

		@Override
		public void drawConfigure() {
			int tilesize = Vars.tilesize;
			float sin = Mathf.absin(Time.time, 6, 1);

			Draw.color(Pal.accent);
			Lines.stroke(1);
			Drawf.circles(x, y, (tile.block().size / 2f + 1) * tilesize + sin - 2, Pal.accent);

			for (int i = 0; i < links.size; i++) {
				int pos = links.get(i);
				if (linkValid(this, pos)) {
					Building linkTarget = Vars.world.build(pos);
					Drawf.square(linkTarget.x, linkTarget.y, linkTarget.block.size * tilesize / 2f + 1, Pal.place);
				}
			}

			Drawf.dashCircle(x, y, range, Pal.accent);
		}

		@Override
		public void draw() {
			super.draw();
			Draw.alpha(warmup);
			Draw.rect(bottomRegion, x, y);
			Draw.color();

			Draw.alpha(warmup);
			Draw.rect(rotatorRegion, x, y, -rotateDeg);

			Draw.alpha(1);
			Draw.rect(topRegion, x, y);
		}

		@Override
		public void display(Table table) {
			super.display(table);
			if (items != null) {
				table.row();
				table.left();
				table.table((l -> {
					IntIntMap map = new IntIntMap();
					l.update((() -> {
						l.clearChildren();
						l.left();
						Seq<Item> seq = new Seq<>(Item.class);
						items.each((item, amount) -> {
							map.put(item.id, amount);
							seq.add(item);
						});
						for (var entry : map.entries()) {
							int id = entry.key;
							int amount = entry.value;
							Item item = Vars.content.item(id);
							l.image(item.uiIcon).padRight(3f);
							l.label((() -> "  " + Strings.fixed(seq.contains(item) ? amount : 0, 0)))
									.color(Color.lightGray);
							l.row();
						}
					}));
				})).left();
			}
		}

		@Override
		public boolean onConfigureBuildTapped(Building other) {
			if (this == other) {
				return false;
			}
			if (dst(other) <= range && other.team == team) {
				configure(other.pos());
				return false;
			}
			return true;
		}

		@Override
		public Object config() {
			IntSeq output = new IntSeq(links.size * 2);
			for (int i = 0; i < links.size; i++) {
				int pos = links.get(i);
				Point2 point2 = Point2.unpack(pos).sub(tile.x, tile.y);
				output.add(point2.x, point2.y);
			}
			return output;
		}

		@Override
		public int acceptStack(Item item, int amount, Teamc source) {
			return linkedCore == null
					? super.acceptStack(item, amount, source)
					: linkedCore.acceptStack(item, amount, source);
		}

		@Override
		public void add() {
			if (added) {
				return;
			}
			rdcGroup.add(this);
			super.add();
		}

		@Override
		public void remove() {
			if (!added) {
				return;
			}
			rdcGroup.remove(this);
			super.remove();
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			int s = links.size;
			write.s(s);
			for (int i = 0; i < s; i++) {
				write.i(links.get(i));
			}
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			links = new IntSeq();
			short linkSize = read.s();
			for (int i = 0; i < linkSize; i++) {
				int pos = read.i();
				links.add(pos);
			}
		}
	}
}
