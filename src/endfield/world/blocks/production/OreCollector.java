package endfield.world.blocks.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Rect;
import arc.struct.EnumSet;
import arc.util.Eachable;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import endfield.graphics.Drawn;
import endfield.math.Mathm;
import endfield.util.CollectionList;
import endfield.util.CollectionObjectSet;
import endfield.util.ObjectFloatMap2;
import endfield.world.blocks.MultiBlock;
import endfield.world.consumers.ConsumePowerMultiplier;
import mindustry.content.Fx;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static mindustry.Vars.content;
import static mindustry.Vars.indexer;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

/**
 * @author LaoHuaJi
 * @author Eipusino
 */
public class OreCollector extends MultiBlock {
	public static List<Tile> tmpClusters = new CollectionList<>(Tile.class);
	public static ObjectFloatMap2<Item> returnCount = new ObjectFloatMap2<>(Item.class);

	public TextureRegion[] innerRegions, outerRegions;
	public TextureRegion baseRegion;

	public int tier = 5;
	public float mineTime = 120f;
	public float drillTime = 30f;
	public float warmupSpeed = 0.075f;
	public @Nullable Item blockedItem;
	public @Nullable Set<Item> blockedItems;
	public float optionalBoostIntensity = 2f;
	public float hardnessDrillMultiplier = 10f;

	public ObjectFloatMap2<Item> drillMultipliers = new ObjectFloatMap2<>(Item.class);

	//public float baseDrillCount = 1f;

	public int collectOffset = 5;
	public int collectSize = 7;

	public OreCollector(String name) {
		super(name);

		solid = true;
		update = true;
		rotate = true;
		hasItems = true;
		hasLiquids = true;

		clipSize = 288f;

		ambientSound = Sounds.loopDrill;
		ambientSoundVolume = 0.018f;

		group = BlockGroup.drills;
		flags = EnumSet.of(BlockFlag.drill);
	}

	@Override
	public boolean outputsItems() {
		return true;
	}

	@Override
	public boolean rotatedOutput(int x, int y) {
		return false;
	}

	@Override
	public void load() {
		super.load();
		innerRegions = new TextureRegion[4];
		outerRegions = new TextureRegion[4];

		baseRegion = Core.atlas.find(name + "-base");

		for (int i = 0; i < 4; i++) {
			innerRegions[i] = Core.atlas.find(name + "-inner-" + i);
			outerRegions[i] = Core.atlas.find(name + "-outer-" + i);
		}
	}

	@Override
	public void init() {
		for (Consume c : consumeBuilder) c.multiplier = b -> ((OreCollectorBuild) b).oreClusters.size() / (float) (collectSize * collectSize);

		super.init();

		if (blockedItems == null && blockedItem != null) {
			blockedItems = CollectionObjectSet.with(blockedItem);
		}
	}

	@Override
	public ConsumePower consumePower(float powerPerTick) {
		return consume(new ConsumePowerMultiplier(powerPerTick, 0f, false));
	}

	@Override
	public TextureRegion getPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		return baseRegion;
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.add(Stat.drillTier, table -> {
			table.row();
			table.table(c -> {
				int i = 0;
				for (Block block : content.blocks()) {
					if ((block instanceof Floor floor && floor.wallOre && block.itemDrop != null && block.itemDrop.hardness <= tier && (blockedItems == null || !blockedItems.contains(block.itemDrop))) ||
							(block instanceof StaticWall && block.itemDrop != null && block.itemDrop.hardness <= tier && (blockedItems == null || !blockedItems.contains(block.itemDrop)))) {
						c.table(Styles.grayPanel, b -> {
							b.image(block.uiIcon).size(40f).pad(10f).left().scaling(Scaling.fit);
							b.table(info -> {
								info.left();
								info.add(block.localizedName).left().row();
								info.add(block.itemDrop.emoji()).with(l -> StatValues.withTooltip(l, block.itemDrop)).left();
							}).grow();
							b.add(Strings.autoFixed(60f / getDrillTime(block.itemDrop) / 2f, 2) + StatUnit.perSecond.localized())
									.right().pad(10f).padRight(15f).color(Color.lightGray);
						}).growX().pad(5f);
						if (++i % 2 == 0) c.row();
					}
				}
			}).growX().colspan(table.getColumns());
		});

		stats.add(Stat.drillSpeed, 60f / drillTime / 2f, StatUnit.itemsSecond);

		if (optionalBoostIntensity != 1 && findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase consBase) {
			stats.remove(Stat.booster);
			stats.add(Stat.booster, StatValues.speedBoosters("{0}" + StatUnit.timesSpeed.localized(),
					consBase.amount, optionalBoostIntensity, false,
					l -> {
						ConsumeLiquid c = findConsumer(f -> f instanceof ConsumeLiquid);
						return c != null && (consumesLiquid(l) && (c.booster || c.liquid != l));
					}));
		}
	}

	@Override
	public void setBars() {
		super.setBars();

		addBar("drillspeed", (OreCollectorBuild tile) -> new Bar(
				() -> {
					float sum = 0f;

					getOreOutput(tmpClusters, tile.tileX(), tile.tileY(), tile.rotation);

					var iterator = returnCount.values();
					while (iterator.hasNext()) sum += iterator.next();

					return Core.bundle.format("bar.drillspeed", Strings.fixed(sum / (mineTime / 60f) * tile.efficiency() * tile.timeScale(), 2));
				},
				() -> Pal.ammo,
				() -> tile.warmup * tile.efficiency
		));
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);

		getOreOutput(tmpClusters, x, y, rotation);

		int i = 0;
		for (var entry : returnCount.entries()) {
			Tmp.v1.setZero().add(collectOffset, 0).rotate(rotation * 90f).add(0f, (collectSize - size) / 2f).add(x, y);
			if (rotation == 3) Tmp.v1.set(x, y);
			drawPlaceText("[white]" + entry.key.emoji() + "[] " + entry.key.localizedName + " " +
					Strings.autoFixed(entry.value / (mineTime / 60f), 2) + StatUnit.perSecond.localized(), (int) Tmp.v1.x, (int) (Tmp.v1.y) + i, valid);
			i++;
		}

		x *= tilesize;
		y *= tilesize;
		x += offset;
		y += offset;

		Rect rect = getRect(Tmp.r1, x, y, rotation);
		Color c = valid ? Pal.accent : Pal.remove;
		Drawf.dashRect(c, rect);
		Draw.color(Pal.accent);
		Draw.alpha(0.5f);

		for (Tile tile : tmpClusters) Fill.square(tile.worldx(), tile.worldy(), tilesize / 2f);

		Draw.reset();
	}

	public float getDrillTime(Item item) {
		return (drillTime + hardnessDrillMultiplier * item.hardness) / drillMultipliers.get(item, 1f);
	}

	@Override
	public boolean canPlaceOn(Tile tile, Team team, int rotation) {
		//overlapping construction areas not allowed; grow by a tiny amount so edges can't overlap either.
		Rect rect = getRect(Tmp.r1, tile.worldx() + offset, tile.worldy() + offset, rotation).grow(-0.1f);
		getOreClusters(tmpClusters, tile.x, tile.y, rotation);
		boolean hasOre = !tmpClusters.isEmpty();
		boolean overlap = indexer.getFlagged(team, BlockFlag.drill).contains(b -> checkOverlap(rect, Tmp.r2, b.block, b));
		boolean planOverlap = team.data().getBuildings(ConstructBlock.get(size)).contains(b -> checkOverlap(rect, Tmp.r2, ((ConstructBlock.ConstructBuild) b).current, b));
		return super.canPlaceOn(tile, team, rotation) && hasOre && !overlap && !planOverlap;
	}

	public boolean checkOverlap(Rect rect1, Rect rect2, Block block, Building build) {
		if (build == null) return false;
		if (!(block instanceof OreCollector coll)) return false;
		return coll.getRect(rect2, build.x, build.y, build.rotation).overlaps(rect1);
	}

	public Rect getRect(Rect rect, float x, float y, int rotation) {
		rect.setCentered(x, y, collectSize * tilesize);
		float len = tilesize * (collectSize + size) / 2f;

		rect.x += Geometry.d4x(rotation) * len;
		rect.y += Geometry.d4y(rotation) * len;

		return rect;
	}

	public void getOreClusters(List<Tile> out, int x, int y, int rotation) {
		out.clear();

		int cx = x + Geometry.d4x(rotation) * collectOffset;
		int cy = y + Geometry.d4y(rotation) * collectOffset;
		int offset = collectSize / 2;
		for (int tx = cx - offset; tx <= cx + offset; tx++) {
			for (int ty = cy - offset; ty <= cy + offset; ty++) {
				Tile tile = world.tile(tx, ty);
				if (tile != null && tile.wallDrop() != null) out.add(tile);
			}
		}
	}

	public void getOreOutput(List<Tile> out, int x, int y, int rotation) {
		getOreClusters(out, x, y, rotation);
		returnCount.clear();
		for (Tile tile : out) {
			Item drops = tile.wallDrop();

			if (drops != null && drops.hardness <= tier && (blockedItems == null || !blockedItems.contains(drops))) {
				returnCount.increment(drops, 0f, 60f / getDrillTime(drops));
			}
		}
	}

	public void drawBlockBase(float x, float y, int rotation) {
		Draw.rect(innerRegions[rotation], x, y);
		Tmp.v1.set(4f, 20f).rotate(rotation * 90f);
		Tmp.v2.set(4f, -20f).rotate(rotation * 90f);
		Draw.rect(outerRegions[(rotation + 3) % 4], x + Tmp.v1.x, y + Tmp.v1.y);
		Draw.rect(outerRegions[(rotation + 1) % 4], x + Tmp.v2.x, y + Tmp.v2.y);
	}

	public class OreCollectorBuild extends MultiBuild {
		public List<Tile> oreClusters = new CollectionList<>(Tile.class);
		public float progress;
		public float warmup;

		@Override
		public void created() {
			super.created();
			getOreClusters(oreClusters, tileX(), tileY(), rotation);
		}

		@Override
		public void draw() {
			drawBlockBase(x, y, rotation);
			if (warmup > 0.01f) drawScanner();
		}

		protected void drawScanner() {
			float len1 = collectSize * tilesize / 2f;
			float len2 = size * tilesize / 2f;
			float len3 = tilesize * collectSize;
			float len4 = tilesize;

			float outlineAlpha = warmup * Mathm.timeValue(0.65f, 0.8f, 3f);
			float innerAlpha = warmup * Mathm.timeValue(0.20f, 0.25f, 3f);

			float ang = Drawn.rotator_90(Drawn.cycle(Time.time / 4f, 0f, 45f), 0.15f);
			float scl = Mathm.timeValue(0.75f, 1.25f, 3f);

			Draw.z(Layer.blockOver);

			Draw.color(team.color);
			Tmp.c1.set(team.color).lerp(Color.white, 0.8f).a(innerAlpha);
			float shift1 = 2f;
			float shift2 = Mathm.timeValue(6f, 8f, 3f);

			Draw.alpha(innerAlpha);
			Rect rect = getRect(Tmp.r1, x, y, rotation);
			Fill.rect(rect);

			Tmp.v1.setZero().add(len2 - 1f, len1 - 1f).rotate(rotdeg()).add(x, y);
			Tmp.v2.setZero().add(len2 - 1f, -len1 + 1f).rotate(rotdeg()).add(x, y);
			Tmp.v3.setZero().add(len2 - len4, -len1 + len4).rotate(rotdeg()).add(x, y);
			Tmp.v4.setZero().add(len2 - len4, len1 - len4).rotate(rotdeg()).add(x, y);

			Lines.stroke(2f);

			Draw.alpha(outlineAlpha);
			Draw.z(Layer.effect);

			Fill.circle(Tmp.v1.x, Tmp.v1.y, 1.25f);
			Fill.circle(Tmp.v2.x, Tmp.v2.y, 1.25f);

			Fill.circle(Tmp.v3.x, Tmp.v3.y, 1.25f);
			Fill.circle(Tmp.v4.x, Tmp.v4.y, 1.25f);

			Tmp.v1.setZero().add(len2 - 1.5f, len1 - 1.5f).rotate(rotdeg()).add(x, y);
			Tmp.v2.setZero().add(len2 - 1.5f, -len1 + 1.5f).rotate(rotdeg()).add(x, y);

			Lines.line(Tmp.v2.x, Tmp.v2.y, Tmp.v3.x, Tmp.v3.y);
			Lines.line(Tmp.v3.x, Tmp.v3.y, Tmp.v4.x, Tmp.v4.y);
			Lines.line(Tmp.v1.x, Tmp.v1.y, Tmp.v4.x, Tmp.v4.y);

			Tmp.v1.setZero().add(len2, len1 - 2f).rotate(rotdeg()).add(x, y);
			Tmp.v2.setZero().add(len2, -len1 + 2f).rotate(rotdeg()).add(x, y);

			Lines.line(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y);

			Tmp.v1.setZero().add(len2, len1).rotate(rotdeg()).add(x, y);
			Tmp.v2.setZero().add(len2, -len1).rotate(rotdeg()).add(x, y);

			Draw.alpha(innerAlpha);
			Draw.z(Layer.blockOver);
			Fill.quad(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, Tmp.v3.x, Tmp.v3.y, Tmp.v4.x, Tmp.v4.y);

			Tmp.v1.setZero().add(len2, len1).rotate(rotdeg()).add(x, y);
			Tmp.v2.setZero().add(len2 + len3, len1).rotate(rotdeg()).add(x, y);
			Tmp.v3.setZero().add(len2 + len3 - shift1, len1 - shift1).rotate(rotdeg()).add(x, y);
			Tmp.v4.setZero().add(len2, len1 - shift1).rotate(rotdeg()).add(x, y);
			Tmp.v5.setZero().add(len2, len1 - shift2).rotate(rotdeg()).add(x, y);
			Tmp.v6.setZero().add(len2 + len3 - shift2, len1 - shift2).rotate(rotdeg()).add(x, y);

			Draw.alpha(outlineAlpha);
			Draw.z(Layer.effect);
			Fill.quad(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, Tmp.v3.x, Tmp.v3.y, Tmp.v4.x, Tmp.v4.y);
			Draw.alpha(innerAlpha);
			Draw.z(Layer.blockOver);
			Fill.quad(
					Tmp.v3.x, Tmp.v3.y, Tmp.c1.toFloatBits(), Tmp.v4.x, Tmp.v4.y, Tmp.c1.toFloatBits(),
					Tmp.v5.x, Tmp.v5.y, 0f, Tmp.v6.x, Tmp.v6.y, 0f
			);

			Tmp.v1.setZero().add(len2 + len3, -len1).rotate(rotdeg()).add(x, y);
			Tmp.v4.setZero().add(len2 + len3 - shift1, -len1 + shift1).rotate(rotdeg()).add(x, y);
			Tmp.v5.setZero().add(len2 + len3 - shift2, -len1 + shift2).rotate(rotdeg()).add(x, y);

			Draw.alpha(outlineAlpha);
			Draw.z(Layer.effect);
			Fill.quad(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, Tmp.v3.x, Tmp.v3.y, Tmp.v4.x, Tmp.v4.y);
			Draw.alpha(innerAlpha);
			Draw.z(Layer.blockOver);
			Fill.quad(
					Tmp.v3.x, Tmp.v3.y, Tmp.c1.toFloatBits(), Tmp.v4.x, Tmp.v4.y, Tmp.c1.toFloatBits(),
					Tmp.v5.x, Tmp.v5.y, 0f, Tmp.v6.x, Tmp.v6.y, 0f
			);

			Tmp.v2.setZero().add(len2, -len1).rotate(rotdeg()).add(x, y);
			Tmp.v3.setZero().add(len2, -len1 + shift1).rotate(rotdeg()).add(x, y);
			Tmp.v6.setZero().add(len2, -len1 + shift2).rotate(rotdeg()).add(x, y);

			Draw.alpha(outlineAlpha);
			Draw.z(Layer.effect);
			Fill.quad(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, Tmp.v3.x, Tmp.v3.y, Tmp.v4.x, Tmp.v4.y);
			Draw.alpha(innerAlpha);
			Draw.z(Layer.blockOver);
			Fill.quad(
					Tmp.v3.x, Tmp.v3.y, Tmp.c1.toFloatBits(), Tmp.v4.x, Tmp.v4.y, Tmp.c1.toFloatBits(),
					Tmp.v5.x, Tmp.v5.y, 0f, Tmp.v6.x, Tmp.v6.y, 0f
			);

			Draw.alpha(warmup);
			Lines.stroke(0.5f);

			for (Tile tile : oreClusters) {
				Item drops = tile.wallDrop();

				if (drops != null) {
					Draw.color(drops.color);
					Draw.z(Layer.buildBeam);
					Fill.rect(tile.worldx(), tile.worldy(), tilesize * scl, tilesize * scl, ang);
					Draw.z(Layer.effect);

					for (int i = 0; i < 4; i++) {
						Tmp.v1.set(2f * scl, 6f * scl).rotate(ang + i * 90f).add(tile.worldx(), tile.worldy());
						Tmp.v2.set(6f * scl, 6f * scl).rotate(ang + i * 90f).add(tile.worldx(), tile.worldy());
						Tmp.v3.set(6f * scl, 2f * scl).rotate(ang + i * 90f).add(tile.worldx(), tile.worldy());

						Lines.beginLine();
						Lines.linePoint(Tmp.v1.x, Tmp.v1.y);
						Lines.linePoint(Tmp.v2.x, Tmp.v2.y);
						Lines.linePoint(Tmp.v3.x, Tmp.v3.y);
						Lines.endLine();

						Tmp.v1.set(2f * scl, 4f * scl).rotate(ang + i * 90f).add(tile.worldx(), tile.worldy());
						Tmp.v2.set(4f * scl, 4f * scl).rotate(ang + i * 90f).add(tile.worldx(), tile.worldy());
						Tmp.v3.set(4f * scl, 2f * scl).rotate(ang + i * 90f).add(tile.worldx(), tile.worldy());

						Lines.beginLine();
						Lines.linePoint(Tmp.v1.x, Tmp.v1.y);
						Lines.linePoint(Tmp.v2.x, Tmp.v2.y);
						Lines.linePoint(Tmp.v3.x, Tmp.v3.y);
						Lines.endLine();
					}
				}
			}

			Draw.z(Layer.effect);
			Draw.color(Tmp.c1.set(team.color).lerp(Color.white, 0.5f));

			for (int tx = 0; tx < 8; tx++) {
				for (int ty = 0; ty < 8; ty++) {
					float rx = tx * (60f / 8f) + x + collectOffset * tilesize * Geometry.d4x(rotation) - 26.5f;
					float ry = ty * (60f / 8f) + y + collectOffset * tilesize * Geometry.d4y(rotation) - 26.5f;
					float a = warmup * Mathm.timeValue(0.20f, 0.65f, 1f, Mathf.randomSeed(Point2.pack((int) rx, (int) ry), 0f, 360f));
					Draw.alpha(a);
					if (tx < 7) Lines.lineAngle(rx, ry, 0f, 3f);
					if (ty < 7) Lines.lineAngle(rx, ry, 90f, 3f);
					if (tx > 0) Lines.lineAngle(rx, ry, 180f, 3f);
					if (ty > 0) Lines.lineAngle(rx, ry, 270f, 3f);
				}
			}
			Draw.reset();
		}

		@Override
		public boolean shouldConsume() {
			return items.total() < itemCapacity && enabled && !oreClusters.isEmpty();
		}

		@Override
		public boolean shouldAmbientSound() {
			return efficiency > 0.01f && items.total() < itemCapacity;
		}

		@Override
		public float ambientVolume() {
			return efficiency * (size * size) / 4f;
		}

		@Override
		public void updateTile() {
			super.updateTile();

			if (timer(timerDump, dumpTime / timeScale)) dump();

			if (items.total() < itemCapacity && efficiency > 0f) {
				float multiplier = Mathf.lerp(1f, optionalBoostIntensity, optionalEfficiency);
				warmup = Mathf.approachDelta(warmup, 1f, warmupSpeed);
				progress += edelta() * warmup * multiplier * efficiency;
			} else {
				warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
				return;
			}

			if (progress >= mineTime) {
				getOreOutput(oreClusters, tileX(), tileY(), rotation);
				for (Tile tile : oreClusters) {
					Item drops = tile.wallDrop();

					if (drops != null) {
						Fx.mineHuge.at(tile.worldx(), tile.worldy(), drops.color);
						Fx.itemTransfer.at(tile.worldx(), tile.worldy(), 0f, drops.color, this);
					}
				}

				for (var entry : returnCount.entries()) addItem(entry.key, entry.value);

				progress %= mineTime;
			}
		}

		public void addItem(Item item, float value) {
			float chance = value % 1f;
			//items.add(item, Math.min(getMaximumAccepted(item) - items.get(item), (int) value));
			//if (getMaximumAccepted(item) > items.get(item) && Mathf.chance(chance)) items.add(item, 1);
			int amount = Math.min(getMaximumAccepted(item) - items.get(item), (int) value);
			for (int i = 0; i < amount; i++) offload(item);
			if (getMaximumAccepted(item) > items.get(item) && Mathf.chance(chance)) offload(item);
		}

		@Override
		public float efficiency() {
			return warmup * efficiency * Mathf.lerp(1f, optionalBoostIntensity, optionalEfficiency);
		}

		@Override
		public void onProximityUpdate() {
			super.onProximityUpdate();

			if (oreClusters.isEmpty()) getOreClusters(oreClusters, tileX(), tileY(), rotation);
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.f(progress);
			write.f(warmup);

			write.i(oreClusters.size());
			for (Tile tile : oreClusters) {
				TypeIO.writeTile(write, tile);
			}
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			progress = read.f();
			warmup = read.f();

			int size = read.i();
			for (int i = 0; i < size; i++) {
				Tile tile = TypeIO.readTile(read);
				if (tile != null && tile.wallDrop() != null) oreClusters.add(tile);
			}
		}
	}
}
