package heavyindustry.world.blocks.production;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Structs;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

import java.util.Arrays;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class ArcCrafter extends AttributeCrafter {
	public ItemStack[] withoutBoost, withBoost; //automatically set
	public Color midColor = new Color();

	public @Nullable ItemStack boostItem = null;
	public float boostAmount = 1f;

	public float flashChance = 0.01f;
	public float sizeScl = 15 * 6f;
	public boolean drawLarge = false;
	public Color color1 = Pal.lancerLaser, color2 = Pal.sapBullet;
	public int craftParticles = 5;
	public Effect edgeEffect = new Effect(25f, e -> {
		Draw.color(e.color);
		Fill.square(e.x, e.y, e.fout() * 1.3f + 0.01f, 45f);
	});
	public TextureRegion topRegion, lightRegion, heatRegion, shadowRegion;

	public ArcCrafter(String name) {
		super(name);

		updateEffect = craftEffect = Fx.none;
		lightRadius = 30f;
		emitLight = true;
		maxBoost = 2f;
	}

	@Override
	public void init() {
		if (boostItem != null) {
			ConsumeItems items = findConsumer(c -> c instanceof ConsumeItems);

			ItemStack[] before = items == null ? ItemStack.empty : items.items;
			withoutBoost = Arrays.copyOf(before, before.length);
			withBoost = Arrays.copyOf(before, before.length + 1);
			withBoost[before.length] = boostItem;

			if (items != null) {
				removeConsumer(items);
			}

			consume(new ConsumeItemDynamic((ArcCrafterBuild e) -> e.useBooster() ? withBoost : withoutBoost));
		}

		super.init();

		clipSize = Math.max(clipSize, (lightRadius + 20f) * size * 2.2f);
	}

	@Override
	public void load() {
		super.load();

		topRegion = Core.atlas.find(name + "-top");
		lightRegion = Core.atlas.find(name + "-light");
		heatRegion = Core.atlas.find(name + "-heat");
		shadowRegion = Core.atlas.find("circle-shadow");
		midColor.set(color1).lerp(color2, 0.5f);
	}

	@Override
	public void setStats() {
		super.setStats();
		if (boostItem != null) {
			stats.add(Stat.input, StatValues.items(craftTime, withoutBoost));
			stats.add(Stat.affinities, StatValues.items(craftTime, boostItem));
		}
	}

	@Override
	public boolean canPlaceOn(Tile tile, Team team, int rotation) {
		float sum = tile.getLinkedTilesAs(this, tempTiles).sumf(t -> canPump(t) ? baseEfficiency + (attribute != null ? t.floor().attributes.get(attribute) : 0f) : 0f);
		return boostItem != null || sum > 0.00001f;
	}

	protected boolean canPump(Tile tile) {
		return tile != null && !tile.floor().isLiquid;
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region, topRegion};
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ArcCrafterBuild::new;
	}

	public class ArcCrafterBuild extends AttributeCrafterBuild {
		public boolean nextFlash;

		public float heat, warmup2;

		public @Nullable Floor tmpFloor = null;
		public float maxAttr = 0f;

		@Override
		public void updateTile() {
			super.updateTile();

			if (!nextFlash && heat < 0.001f && Mathf.chance(flashChance * edelta()) && canConsume() && efficiency > 0.0001f) {
				nextFlash = true;
				heat = 1f;
			} else if (nextFlash && heat < 0.001f) {
				nextFlash = false;
				heat = 1f;
			}
			heat = Mathf.approachDelta(heat, 0f, 0.05f);
			warmup2 = Mathf.approachDelta(warmup2, efficiency, 0.04f);
		}

		public void setFlameColor(Color tmp) {
			tmp.set(color1).lerp(color2, Mathf.absin(Time.time + Mathf.randomSeed(pos(), 0f, 9f * 6.29f), 9f, 1f));
		}

		@Override
		public void craft() {
			super.craft();
			int n = Mathf.random(0, 3) + craftParticles;
			for (int i = 0; i < n; i++) {
				Tmp.v1.trns(Mathf.random(360f), size * tilesize / 1.414f).clamp(-size * tilesize / 2f, -size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);
				Tmp.v2.set(Tmp.v1).scl((size * tilesize / 2f + 4f) / (size * tilesize / 2f));
				Tile t = world.tileWorld(Tmp.v2.x + x, Tmp.v2.y + y);
				if (t == null || !t.solid()) {
					edgeEffect.at(Tmp.v1.x + x, Tmp.v1.y + y, Mathf.chance(0.33f) ? color1 : (Mathf.chance(0.5f) ? color2 : midColor));
				}
			}
		}

		public boolean useBooster() {
			return attrsum <= 0.0001f;
		}

		@Override
		public float efficiencyScale() {
			return super.efficiencyScale() + (useBooster() && canConsume() ? boostAmount : 0f);
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			if (boostItem == null) return super.acceptItem(source, item);
			return items.get(item) < getMaximumAccepted(item) &&
					Structs.contains(useBooster() ? withBoost : withoutBoost, stack -> stack.item == item);
		}

		@Override
		public void draw() {
			Draw.rect(region, x, y);
			tmpFloor = null;
			maxAttr = 0f;
			tile.getLinkedTiles(t -> {
				if (t != null && t.floor() != Blocks.air && t.floor().attributes.get(attribute) > maxAttr) {
					maxAttr = t.floor().attributes.get(attribute);
					tmpFloor = t.floor();
				}
			});
			setFlameColor(Tmp.c4);
			if (tmpFloor != null || boostItem != null) {
				Draw.mixcol(Tmp.c4, Mathf.absin(11f, 0.6f * warmup2));
				Draw.colorl(0.9f);
				TextureRegion reg = tmpFloor != null ? tmpFloor.fullIcon : boostItem.item.fullIcon;
				if (drawLarge) {
					int n = tmpFloor == null ? Math.min(4, items.get(boostItem.item)) : 4;
					for (int i = 0; i < n; i++) {
						Tmp.v1.trns(45f + i * 90f, 4f * 1.414f).add(x, y);
						Draw.rect(reg, Tmp.v1.x, Tmp.v1.y);
					}
				} else {
					Draw.rect(reg, x, y);
				}
				Draw.color();
				Draw.mixcol();
			}
			Draw.rect(topRegion, x, y);
			if (heat >= 0.001f) {
				Draw.z(Layer.bullet - 0.01f);

				Draw.color(Color.white, Tmp.c4, Mathf.clamp(heat * 3f - 2f));
				Draw.alpha(Mathf.clamp(heat * 1.5f));
				Draw.rect(lightRegion, x, y);
			}
			Draw.z(Layer.blockOver);
			Draw.blend(Blending.additive);
			if (heat >= 0.001f) {
				Draw.alpha(Mathf.clamp(heat * 1.5f) * 0.2f);
				Draw.rect(heatRegion, x, y);
			}
			Draw.alpha(Mathf.absin(11f, 0.2f * warmup2));
			Draw.rect(shadowRegion, x, y, sizeScl * Draw.scl * Draw.xscl, sizeScl * Draw.scl * Draw.yscl);
			Draw.blend();
			Draw.color();
		}

		@Override
		public void drawLight() {
			setFlameColor(Tmp.c4);
			Drawf.light(x, y, (lightRadius * (1f + Mathf.clamp(heat) * 0.1f) + Mathf.absin(10f, 5f)) * warmup2 * block.size, Tmp.c4, 0.65f);
		}
	}
}
