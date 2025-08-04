package heavyindustry.world.blocks.storage;

import arc.Core;
import arc.Events;
import arc.func.Cons2;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.TextField;
import arc.scene.ui.TextField.TextFieldFilter;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.content.HFx;
import heavyindustry.gen.HSounds;
import heavyindustry.graphics.HShaders;
import heavyindustry.ui.Elements;
import heavyindustry.util.Utils;
import mindustry.core.UI;
import mindustry.game.EventType.Trigger;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Teamc;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.type.PayloadSeq;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadVoid;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.modules.LiquidModule;

import static mindustry.Vars.content;
import static mindustry.Vars.renderer;

public class FlowrateVoid extends PayloadVoid {
	protected static float addTimeSetting;
	private static ObjectSet<FlowrateVoidBuild> spaceDraws;
	public int extraAbsorbEffects = 6;
	public float extraAbsorbOffset = 6f;
	public float extraAbsorbEffectMinDelay = 15f, extraAbsorbEffectMaxDelay = 35f;
	public float absorbPitch = 1f, absorbVolume = 0.5f;
	public float sclMax = 20f;
	public Color effectColor = Color.valueOf("1d053a");
	protected TextureRegion baseRegion, spaceRegion;

	public FlowrateVoid(String name) {
		super(name);

		envDisabled = Env.any;

		acceptsItems = hasItems = hasLiquids = hasPower = true;
		outputsPower = outputsPayload = false;
		configurable = true;
		saveConfig = false;
		itemCapacity = 10000;
		liquidCapacity = 10000f;

		incinerateEffect = HFx.flowrateAbsorb;
		incinerateSound = HSounds.flowrateAbosrb;

		config(Float.class, (FlowrateVoidBuild tile, Float time) -> {
			tile.readingTimer += time * 60f;
			tile.maxTime = tile.readingTimer;
		});
		config(Boolean.class, (FlowrateVoidBuild tile, Boolean ignored) -> {
			tile.readingTimer = 0f;
			tile.totalTime = 0f;
			tile.items.clear();
			tile.liquids.clear();
			tile.totalPowerProduced = tile.totalPowerConsumed = 0f;
			tile.totalPayloads.clear();
			tile.payloadData.clear();
		});

		if (spaceDraws == null) {
			spaceDraws = new ObjectSet<>();

			Events.run(Trigger.drawOver, () -> {
				Draw.draw(Layer.block - 0.01f, () -> {
					for (FlowrateVoidBuild build : spaceDraws) {
						renderer.effectBuffer.begin(Color.clear);
						Draw.rect(((FlowrateVoid) (build.block)).spaceRegion, build.x, build.y);
						renderer.effectBuffer.end();
						renderer.effectBuffer.blit(HShaders.smallSpaceShader);
					}
				});
				spaceDraws.clear();
			});
		}
	}

	@Override
	public void load() {
		super.load();

		baseRegion = Core.atlas.find(name + "-base");
		spaceRegion = Core.atlas.find(name + "-space");
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{baseRegion, region, topRegion};
	}

	@Override
	public boolean outputsItems() {
		return false;
	}

	@Override
	public boolean isAccessible() { //Dont show inventory
		return false;
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.remove(Stat.itemCapacity);
		stats.remove(Stat.liquidCapacity);
	}

	@Override
	public void setBars() {
		super.setBars();

		removeBar("items");
		removeBar("liquid");

		addBar("time", (FlowrateVoidBuild entity) -> new Bar(
				() -> UI.formatTime(entity.readingTimer) + " | " + UI.formatTime(entity.totalTime),
				() -> Pal.bar,
				() -> entity.readingTimer / entity.maxTime
		));
	}

	public static class PayloadInputData {
		public int[] items = new int[content.items().size];
		public float[] liquids = new float[content.liquids().size];
		public float power;

		public void addI(Item item, int amount) {
			items[item.id] += amount;
		}

		public void addL(Liquid liquid, float amount) {
			liquids[liquid.id] += amount;
		}

		public void addP(float amount) {
			power += amount;
		}

		public boolean hasItems() {
			for (int i : items) {
				if (i > 0) return true;
			}
			return false;
		}

		public boolean hasLiquids() {
			for (float l : liquids) {
				if (l > 0) return true;
			}
			return false;
		}

		public boolean hasPower() {
			return power > 0;
		}

		public void eachItem(Cons2<Item, Integer> cons) {
			for (int i = 0; i < items.length; i++) {
				if (items[i] == 0) continue;
				cons.get(content.item(i), items[i]);
			}
		}

		public void eachLiquid(Cons2<Liquid, Float> cons) {
			for (int i = 0; i < liquids.length; i++) {
				if (liquids[i] == 0) continue;
				cons.get(content.liquid(i), liquids[i]);
			}
		}
	}

	public static class EmptyLiquidModule extends LiquidModule {
		@Override
		public float get(Liquid liquid) {
			return 0f; //Return 0 so that liquid flow rate is always the same,
		}
	}

	public class FlowrateVoidBuild extends Building {
		public Seq<Payload> payloads = new Seq<>();
		public float maxTime = 1f;
		public float readingTimer = 0f;
		public float totalTime = 0f;
		public float totalPowerProduced, totalPowerConsumed, totalPowerTransported;
		public PayloadSeq totalPayloads = new PayloadSeq();
		public ObjectMap<Block, PayloadInputData> payloadData = new ObjectMap<>();

		@Override
		public Building create(Block block, Team team) {
			super.create(block, team);

			if (block.hasLiquids) liquids = new EmptyLiquidModule();
			return this;
		}

		@Override
		public void draw() {
			Draw.z(Layer.block - 0.02f);
			Draw.rect(baseRegion, x, y);

			Draw.z(Layer.block);
			Draw.rect(region, x, y);

			//draw input
			for (int i = 0; i < 4; i++) {
				if (blends(this, rotation)) {
					Draw.rect(inRegion, x, y, (i * 90) - 180);
				}
			}

			Draw.rect(topRegion, x, y);

			if (payloads.any()) {
				payloads.each(p -> {
					Draw.z(Layer.blockOver);
					p.draw();
				});
				Draw.reset();
			}

			spaceDraws.add(this);
		}

		@Override
		public void updateTile() {
			if (readingTimer > 0f) {
				readingTimer -= Time.delta;
				totalTime += Time.delta;
				totalPowerProduced += power.graph.getPowerProduced();
				totalPowerConsumed += power.graph.getPowerNeeded();
				if (Elements.flowrateVoidDialog.isShown()) Elements.flowrateVoidDialog.rebuild();
			}

			for (int i = 0; i < payloads.size; i++) {
				Payload p = payloads.get(i);
				Tmp.v2.set(p).approach(Tmp.v1.set(this), payloadSpeed);
				p.set(Tmp.v2.x, Tmp.v2.y, p.rotation());

				if (p.within(this, 0.01f)) {
					consumePayload(p);
					payloads.remove(i);
				}
			}
		}

		public void consumePayload(Payload payload) {
			if (readingTimer > 0f) {
				if (payload instanceof BuildPayload p) {
					PayloadInputData data = payloadData.get(p.block(), PayloadInputData::new);
					if (p.block().hasItems) {
						p.build.items.each((item, amount) -> {
							items.add(item, amount);
							data.addI(item, amount);
						});
					}
					if (p.block().hasLiquids) {
						p.build.liquids.each((liquid, amount) -> {
							liquids.add(liquid, amount);
							data.addL(liquid, amount);
						});
					}
					if (p.block().consPower != null && p.block().consPower.buffered) {
						float pow = p.build.power.status * p.block().consPower.capacity;
						totalPowerTransported += pow;
						data.addP(pow);
					}
				}
				totalPayloads.add(payload.content());
			}

			incinerateEffect.at(x, y, effectColor);
			incinerateSound.at(x, y, absorbPitch, absorbVolume);

			float scl = Mathf.clamp(payload.size() / sclMax);
			int count = (int) (extraAbsorbEffects * scl * scl);
			for (int i = 0; i < count; i++) {
				Time.run(Mathf.random(extraAbsorbEffectMinDelay, extraAbsorbEffectMaxDelay), () -> {
					Vec2 pos = Utils.randomPoint(extraAbsorbOffset * scl);
					incinerateEffect.at(x + pos.x, y + pos.y, effectColor);
				});
			}
		}

		@Override
		public void buildConfiguration(Table table) {
			addTimeSetting = 0f;
			table.table(Styles.black6, t -> {
				t.defaults().left();
				t.margin(6f);
				t.add("@hi-flowrate-reader.add-time");
				TextField f = t.field(String.valueOf(addTimeSetting), text -> {
					addTimeSetting = Strings.parseFloat(text);
				}).width(120).valid(text -> Strings.canParseFloat(text) && Strings.parseFloat(text) > 0).padLeft(6f).get();
				f.setFilter(TextFieldFilter.floatsOnly);
				t.add(StatUnit.seconds.localized()).padLeft(6f);
				t.button(Icon.add, () -> {
					configure(addTimeSetting);
					addTimeSetting = 0f;
					f.setText(String.valueOf(addTimeSetting));
				}).padLeft(6f);
				t.button(Icon.zoom, () -> Elements.flowrateVoidDialog.show(this));
				t.button(Icon.refresh, () -> configure(false)).tooltip("@hi-flowrate-reader.reset");
			});
		}

		public float getTotalLiquids() {
			float[] total = {0f};
			liquids.each((liquid, amount) -> total[0] += amount);
			return total[0];
		}

		@Override
		public void display(Table table) {
			super.display(table);

			table.row();
			table.table(t -> {
				t.left();
				t.image(new TextureRegionDrawable(Icon.distribution)).size(32).scaling(Scaling.fit);
				t.label(() -> labelText(items.total())).wrap().width(230f).padLeft(2).color(Color.lightGray).row();

				t.image(new TextureRegionDrawable(Icon.liquid)).size(32).scaling(Scaling.fit);
				t.label(() -> labelText(getTotalLiquids())).wrap().width(230f).padLeft(2).color(Color.lightGray).row();

				t.image(new TextureRegionDrawable(Icon.units)).size(32).scaling(Scaling.fit);
				t.label(() -> labelText(totalPayloads.total())).wrap().width(230f).padLeft(2).color(Color.lightGray).row();

				t.image(new TextureRegionDrawable(Icon.power)).size(32).scaling(Scaling.fit).color(Pal.accent);
				t.label(() -> labelText(totalPowerProduced)).wrap().width(230f).padLeft(2).color(Color.lightGray).row();

				t.image(new TextureRegionDrawable(Icon.power)).size(32).scaling(Scaling.fit).color(Pal.remove);
				t.label(() -> labelText(totalPowerConsumed)).wrap().width(230f).padLeft(2).color(Color.lightGray);
			}).left();
		}

		public String labelText(float value) {
			if (totalTime <= 0) return Core.bundle.get("hi-flowrate-reader.no-time");
			return Elements.round(value / totalTime * 60f) + StatUnit.perSecond.localized();
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			return enabled;
		}

		@Override
		public int acceptStack(Item item, int amount, Teamc source) {
			return amount;
		}

		@Override
		public void handleItem(Building source, Item item) {
			if (readingTimer <= 0) return;
			super.handleItem(source, item);
		}

		@Override
		public void handleStack(Item item, int amount, Teamc source) {
			if (readingTimer <= 0) return;
			super.handleStack(item, amount, source);
		}

		@Override
		public boolean acceptLiquid(Building source, Liquid liquid) {
			return enabled;
		}

		@Override
		public void handleLiquid(Building source, Liquid liquid, float amount) {
			if (readingTimer <= 0) return;
			super.handleLiquid(source, liquid, amount);
		}

		@Override
		public boolean acceptPayload(Building source, Payload payload) {
			return true;
		}

		@Override
		public void handlePayload(Building source, Payload payload) {
			payloads.add(payload);
		}
	}
}
