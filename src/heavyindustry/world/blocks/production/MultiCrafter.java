package heavyindustry.world.blocks.production;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.struct.EnumSet;
import arc.util.*;
import arc.util.io.*;
import heavyindustry.ui.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

/**
 * MultiCrafter, Not currently supporting heat.
 * <p>Although Kotlin is really fun, it is still comfortable to write in Java.
 *
 * @author Eipusino
 * @author guiY
 */
public class MultiCrafter extends Block {
    /** Recipe {@link Formula}. */
    public Seq<Formula> products = new Seq<>();
    /** If {@link MultiCrafter#useBlockDrawer} is false, use the drawer in the recipe for the block. */
    public DrawBlock drawer = new DrawDefault();
    /** Do you want to use the {@link MultiCrafter#drawer} inside the block itself. */
    public boolean useBlockDrawer = true;
    /** Whether multiple liquid outputs require different directions, please refer to {@link Formula#liquidOutputDirections} to determine the value of this parameter. */
    public boolean hasDoubleOutput = false;
    /** Automatically add bar to liquid. */
    public boolean autoAddBar = true;
    /** Is liquid suspension display used. */
    public boolean useLiquidTable = true;
    /** How many formulas can be displayed at most once. */
    public int maxList = 4;

    protected Formula defFor = new Formula();

    public MultiCrafter(String name) {
        super(name);

        update = true;
        solid = true;
        hasItems = true;
        ambientSound = Sounds.machine;
        sync = true;
        ambientSoundVolume = 0.03f;
        flags = EnumSet.of(BlockFlag.factory);
        drawArrow = false;

        configurable = true;
        saveConfig = true;

        config(int[].class, (MultiCrafterBuild tile, int[] in) -> {
            if (in.length != 2) return;

            tile.rotation = in[0];

            if (products.isEmpty() || in[1] == -1) tile.formula = null;
            tile.formula = products.get(in[1]);
        });
    }

    @Override
    public void init() {
        defFor.owner = this;
        defFor.init();

        for (Formula product : products) {
            product.owner = this;
            product.init();
            if (product.outputLiquids.length > 0) {
                hasLiquids = true;
                outputsLiquid = true;
            }
            if (product.outputItems.length > 0) {
                hasItems = true;
            }
            if (product.consPower != null) {
                hasPower = true;
                consume(new ConsumePowerDynamic(b -> b instanceof MultiCrafterBuild mb ? mb.formulaPower() : 0));
            }
        }

        super.init();

        hasConsumers = products.any();
    }

    @Override
    public void setBars() {
        super.setBars();
        removeBar("items");
        removeBar("liquid");
        removeBar("power");
        if (consPower != null) {
            addBar("power", (MultiCrafterBuild e) -> new Bar(
                    () -> Core.bundle.format("bar.hi-fall-mti-power", Strings.autoFixed(e.getInputPower(), 2)),
                    () -> Pal.powerBar,
                    () -> Mathf.zero(consPower.requestedPower(e)) && e.power.graph.getPowerProduced() + e.power.graph.getBatteryStored() > 0f ? 1f : e.power.status)
            );
        }
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.output, table -> {
            table.row();

            for (int i = 0; i < products.size; i++) {
                Formula p = products.get(i);
                int j = i + 1;
                table.table(Styles.grayPanel, info -> {
                    info.left().defaults().left();
                    info.add("[accent]formula[]" + j + ":").row();
                    Stats stat = new Stats();
                    stat.timePeriod = p.craftTime;
                    if (p.hasConsumers)
                        for (Consume c : p.consumers) {
                            c.display(stat);
                        }
                    if ((hasItems && itemCapacity > 0) || p.outputItems.length > 0) {
                        stat.add(Stat.productionTime, p.craftTime / 60f, StatUnit.seconds);
                    }

                    if (p.outputItems.length > 0) {
                        stat.add(Stat.output, StatValues.items(p.craftTime, p.outputItems));
                    }

                    if (p.outputLiquids.length > 0) {
                        stat.add(Stat.output, StatValues.liquids(1f, p.outputLiquids));
                    }
                    info.table(t -> UIUtils.statTurnTable(stat, t)).pad(8).left();
                }).growX().left().pad(10);
                table.row();
            }
        });
    }

    @Override
    public void load() {
        super.load();
        if (useBlockDrawer) {
            drawer.load(this);
        } else {
            for (Formula p : products) {
                p.drawer.load(this);
            }
        }
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        if (useBlockDrawer) {
            drawer.drawPlan(this, plan, list);
        } else {
            if (products.any()) {
                products.get(0).drawer.drawPlan(this, plan, list);
            } else {
                super.drawPlanRegion(plan, list);
            }
        }
    }

    @Override
    protected TextureRegion[] icons() {
        return useBlockDrawer ? drawer.icons(this) : products.any() ? products.get(0).drawer.icons(this) : super.icons();
    }

    public class MultiCrafterBuild extends Building {
        public Formula formula = products.any() ? products.get(0) : defFor;
        public float progress;
        public float totalProgress;
        public float warmup;

        public int[] configs = {0, 0};
        public int lastRotation = -1;

        public TextureRegionDrawable[] rotationIcon = {Icon.right, Icon.up, Icon.left, Icon.down};

        @Override
        public void draw() {
            if (formula == null || useBlockDrawer) {
                drawer.draw(this);
            } else {
                formula.drawer.draw(this);
            }
        }

        @Override
        public void drawStatus() {
            if (block.enableDrawStatus && formula != null && formula.hasConsumers) {
                float multiplier = block.size > 1 ? 1 : 0.64f;
                float brcX = x + (block.size * 8) / 2f - 8f * multiplier / 2f;
                float brcY = y - (block.size * 8) / 2f + 8f * multiplier / 2f;
                Draw.z(71f);
                Draw.color(Pal.gray);
                Fill.square(brcX, brcY, 2.5f * multiplier, 45);
                Draw.color(status().color);
                Fill.square(brcX, brcY, 1.5f * multiplier, 45);
                Draw.color();
            }
        }

        public float warmupTarget() {
            return 1f;
        }

        public float formulaPower() {
            if (formula == null) return 0;

            ConsumePower consumePower = formula.consPower;
            if (consumePower == null) return 0;

            return consumePower.usage;
        }

        public float getInputPower() {
            return formulaPower() * 60 * efficiency() * optionalEfficiency() * potentialEfficiency();
        }

        @Override
        public void updateTile() {
            if (lastRotation != rotation) {
                Fx.placeBlock.at(x, y, size);
                lastRotation = rotation;
            }

            if (formula == null) return;
            if (efficiency > 0) {
                progress += getProgressIncrease(formula.craftTime, formula);
                warmup = Mathf.approachDelta(warmup, warmupTarget(), formula.warmupSpeed);

                if (formula.outputLiquids.length > 0) {
                    float inc = getProgressIncrease(1f);
                    for (LiquidStack output : formula.outputLiquids) {
                        handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
                    }
                }

                if (wasVisible && Mathf.chanceDelta(formula.updateEffectChance)) {
                    formula.updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
                }
            } else {
                warmup = Mathf.approachDelta(warmup, 0f, formula.warmupSpeed);
            }

            totalProgress += warmup * Time.delta;

            if (progress >= 1f) {
                craft(formula);
            }

            dumpOutputs(formula);
        }

        @Override
        public float totalProgress() {
            return totalProgress;
        }

        @Override
        public float progress() {
            return progress;
        }

        public float getProgressIncrease(float baseTime, Formula formula) {
            if (formula.ignoreLiquidFullness) {
                return super.getProgressIncrease(baseTime);
            }

            float scaling = 1f, max = 1f;
            if (formula.outputLiquids.length > 0) {
                max = 0f;
                for (LiquidStack output : formula.outputLiquids) {
                    float value = (liquidCapacity - liquids.get(output.liquid)) / (output.amount * edelta());
                    scaling = Math.min(scaling, value);
                    max = Math.max(max, value);
                }
            }

            return super.getProgressIncrease(baseTime) * (formula.dumpExtraLiquid ? Math.min(max, 1f) : scaling);
        }

        public void craft(Formula formula) {
            consume();

            for (ItemStack output : formula.outputItems) {
                for (int i = 0; i < output.amount; i++) {
                    offload(output.item);
                }
            }

            if (wasVisible) {
                formula.craftEffect.at(x, y);
            }
            progress %= 1f;
        }

        public void dumpOutputs(Formula formula) {
            if (formula.outputItems.length > 0 && timer(timerDump, dumpTime / timeScale)) {
                for (ItemStack output : formula.outputItems) {
                    dump(output.item);
                }
            }

            if (formula.outputLiquids.length > 0) {
                for (int i = 0; i < formula.outputLiquids.length; i++) {
                    int dir = formula.liquidOutputDirections.length > i ? formula.liquidOutputDirections[i] : -1;

                    dumpLiquid(formula.outputLiquids[i].liquid, 2f, dir);
                }
            }
        }

        @Override
        public boolean shouldConsume() {
            if (formula == null) return false;
            for (ItemStack output : formula.outputItems) {
                if (items.get(output.item) + output.amount > itemCapacity) {
                    return false;
                }
            }
            if (formula.outputLiquids.length > 0 && !formula.ignoreLiquidFullness) {
                boolean allFull = true;
                for (LiquidStack output : formula.outputLiquids) {
                    if (liquids.get(output.liquid) >= liquidCapacity - 0.001f) {
                        if (!formula.dumpExtraLiquid) {
                            return false;
                        }
                    } else {
                        allFull = false;
                    }
                }
                if (allFull) {
                    return false;
                }
            }
            return enabled;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            if (formula == null) return false;
            return formula.getConsumeItem(item) && items.get(item) < itemCapacity;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if (formula == null) return false;
            return block.hasLiquids && formula.getConsumeLiquid(liquid);
        }

        @Override
        public void consume() {
            if (formula == null) return;
            Consume[] c = formula.consumers;

            for (Consume cons : c) {
                cons.trigger(this);
            }
        }

        @Override
        public void displayConsumption(Table table) {
            if (formula == null) return;
            table.left();
            Formula[] lastFormula = {formula};
            table.table(t -> {
                table.update(() -> {
                    if (lastFormula[0] != formula) {
                        rebuild(t);
                        lastFormula[0] = formula;
                    }
                });
                rebuild(t);
            });
        }

        protected void rebuild(Table table) {
            table.clear();
            Consume[] consumes = formula.consumers;
            for (Consume cons : consumes) {
                if (!cons.optional || !cons.booster) {
                    cons.build(this, table);
                }
            }
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            if (formula == null || !useLiquidTable) return;

            if (formula.outputLiquids.length > 0) {
                for (int i = 0; i < formula.outputLiquids.length; i++) {
                    int dir = formula.liquidOutputDirections.length > i ? formula.liquidOutputDirections[i] : -1;

                    if (dir != -1) {
                        Draw.rect(
                                formula.outputLiquids[i].liquid.fullIcon,
                                x + Geometry.d4x(dir + rotation) * (size * tilesize / 2f + 4),
                                y + Geometry.d4y(dir + rotation) * (size * tilesize / 2f + 4),
                                8f, 8f
                        );
                    }
                }
            }
        }

        @Override
        public void displayBars(Table table) {
            super.displayBars(table);
            if (formula == null) return;
            Formula[] lastFormula = {formula};
            table.update(() -> {
                if (lastFormula[0] != formula) {
                    rebuildBar(table);
                    lastFormula[0] = formula;
                }
            });
            rebuildBar(table);
        }

        protected void rebuildBar(Table table) {
            table.clear();

            for (Func<Building, Bar> bar : block.listBars()) {
                Bar result = bar.get(this);
                if (result != null) {
                    table.add(result).growX();
                    table.row();
                }
            }
            if (formula == null || formula.barMap.isEmpty()) return;
            for (Func<Building, Bar> bar : formula.listBars()) {
                Bar result = bar.get(this);
                if (result == null) continue;
                table.add(result).growX();
                table.row();
            }
        }

        @Override
        public boolean shouldAmbientSound() {
            return efficiency > 0;
        }

        @Override
        public void updateConsumption() {
            if (formula == null) return;
            if (formula.hasConsumers && !cheating()) {
                if (!enabled) {
                    potentialEfficiency = efficiency = optionalEfficiency = 0;
                } else {
                    boolean update = shouldConsume() && productionValid();
                    float minEfficiency = 1f;
                    efficiency = optionalEfficiency = 1f;
                    Consume[] consumes = formula.nonOptionalConsumers;
                    int length = consumes.length;

                    int i;
                    Consume cons;
                    for (i = 0; i < length; i++) {
                        cons = consumes[i];
                        minEfficiency = Math.min(minEfficiency, cons.efficiency(this));
                    }

                    consumes = formula.optionalConsumers;
                    length = consumes.length;

                    for (i = 0; i < length; i++) {
                        cons = consumes[i];
                        optionalEfficiency = Math.min(optionalEfficiency, cons.efficiency(this));
                    }

                    efficiency = minEfficiency;
                    optionalEfficiency = Math.min(optionalEfficiency, minEfficiency);
                    potentialEfficiency = efficiency;
                    if (!update) {
                        efficiency = optionalEfficiency = 0f;
                    }

                    updateEfficiencyMultiplier();
                    if (update && efficiency > 0f) {
                        consumes = formula.updateConsumers;
                        length = consumes.length;

                        for (i = 0; i < length; i++) {
                            cons = consumes[i];
                            cons.update(this);
                        }
                    }
                }
            } else {
                potentialEfficiency = enabled && productionValid() ? 1f : 0f;
                efficiency = optionalEfficiency = shouldConsume() ? potentialEfficiency : 0f;
                updateEfficiencyMultiplier();
            }
        }

        @Override
        public void buildConfiguration(Table table) {
            Table rtc = new Table();
            rtc.left().defaults().size(55);

            Table cont = new Table().top();
            cont.left().defaults().left().growX();

            Runnable rebuild = () -> {
                rtc.clearChildren();
                if (hasDoubleOutput) {
                    for (int i = 0; i < rotationIcon.length; i++) {
                        ImageButton button = new ImageButton();
                        int I = i;
                        button.table(img -> img.image(rotationIcon[I]).color(Color.white).size(40).pad(10f));
                        button.changed(() -> {
                            configs[0] = I;
                            configure(configs);
                        });
                        button.update(() -> button.setChecked(rotation == I));
                        button.setStyle(Styles.clearNoneTogglei);
                        rtc.add(button).tooltip(String.valueOf(i * 90));
                    }
                }

                cont.clearChildren();
                for (Formula f : products) {
                    ImageButton button = new ImageButton();
                    button.table(info -> {
                        info.left();
                        info.table(from -> {
                            Stats stat = new Stats();
                            stat.timePeriod = f.craftTime;
                            if (f.hasConsumers)
                                for (Consume c : f.consumers) {
                                    c.display(stat);
                                }
                            UIUtils.statToTable(stat, from);
                        }).left().pad(6);
                        info.row();
                        info.table(to -> {
                            if (f.outputItems.length > 0) {
                                StatValues.items(f.craftTime, f.outputItems).display(to);
                            }

                            if (f.outputLiquids.length > 0) {
                                StatValues.liquids(1f, f.outputLiquids).display(to);
                            }
                        }).left().pad(6);
                    }).grow().left().pad(5);
                    button.setStyle(Styles.clearNoneTogglei);
                    button.changed(() -> {
                        configs[1] = products.indexOf(f);
                        configure(configs);
                    });
                    button.update(() -> button.setChecked(formula == f));
                    cont.add(button);
                    cont.row();
                }
            };

            rebuild.run();

            Table main = new Table().background(Styles.black6);

            main.add(rtc).left().row();

            ScrollPane pane = new ScrollPane(cont, Styles.smallPane);
            pane.setScrollingDisabled(true, false);

            if (block != null) {
                pane.setScrollYForce(block.selectScroll);
                pane.update(() -> block.selectScroll = pane.getScrollY());
            }

            pane.setOverscroll(false, false);
            main.add(pane).maxHeight(100 * maxList);
            table.top().add(main);
        }

        @Override
        public double sense(LAccess sensor) {
            if (sensor == LAccess.progress) {
                return progress();
            } else {
                return super.sense(sensor);
            }
        }

        @Override
        public int[] config() {
            return configs;
        }

        @Override
        public void configure(Object value) {
            super.configure(value);
            deselect();
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(progress);
            write.f(warmup);
            write.i(lastRotation);
            write.i(formula == null || !products.contains(formula) ? -1 : products.indexOf(formula));
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            progress = read.f();
            warmup = read.f();
            lastRotation = read.i();
            int i = read.i();
            formula = i == -1 ? null : products.get(i);
            configs[0] = rotation;
            configs[1] = i;
        }
    }

    public static class Formula {
        public Consume[] consumers = {}, optionalConsumers = {}, nonOptionalConsumers = {}, updateConsumers = {};
        public ConsumePower consPower = null;

        public float craftTime = 60f;
        public boolean hasConsumers = false;

        public ItemStack[] outputItems = {};
        public LiquidStack[] outputLiquids = {};

        public int[] liquidOutputDirections = {-1};
        public boolean ignoreLiquidFullness = false;
        public boolean dumpExtraLiquid = true;

        public float warmupSpeed = 0.02f;

        public float updateEffectChance = 0.05f;

        //The block must be HeatBlock, otherwise the following variables are invalid.
        public float heatOutput = 0f;
        public float heatRequirement = 0f;
        public float warmupRate = 0.15f;

        public float maxHeatEfficiency = 1f;

        public Effect updateEffect = Fx.none;
        public Effect craftEffect = Fx.none;

        public DrawBlock drawer = new DrawDefault();

        public ObjectMap<Item, Boolean> itemFilter = new ObjectMap<>();
        public ObjectMap<Liquid, Boolean> liquidFilter = new ObjectMap<>();

        protected MultiCrafter owner = null;

        protected Seq<Consume> consumeBuilder = new Seq<>();
        protected OrderedMap<String, Func<Building, Bar>> barMap = new OrderedMap<>();

        public void init() {
            consumers = consumeBuilder.toArray(Consume.class);
            optionalConsumers = consumeBuilder.select(consume -> consume.optional && !consume.ignore()).toArray(Consume.class);
            nonOptionalConsumers = consumeBuilder.select(consume -> !consume.optional && !consume.ignore()).toArray(Consume.class);
            updateConsumers = consumeBuilder.select(consume -> consume.update && !consume.ignore()).toArray(Consume.class);
            hasConsumers = consumers.length > 0;

            if (owner.autoAddBar) {
                if (!liquidFilter.isEmpty()) {
                    for (Liquid liquid : liquidFilter.keys().toSeq()) {
                        addLiquidBar(liquid);
                    }
                }
                for (LiquidStack liquid : outputLiquids) {
                    addLiquidBar(liquid.liquid);
                }
            }
        }

        public void setApply(UnlockableContent content) {
            if (content instanceof Item item) {
                itemFilter.put(item, true);
            }
            if (content instanceof Liquid liquid) {
                liquidFilter.put(liquid, true);
            }
        }

        public Iterable<Func<Building, Bar>> listBars() {
            return barMap.values();
        }

        public void addBar(String name, Func<Building, Bar> sup) {
            barMap.put(name, sup);
        }

        public void addLiquidBar(Liquid liquid) {
            addBar("liquid-" + liquid.name, entity -> !liquid.unlockedNow() ? null : new Bar(
                    () -> liquid.localizedName,
                    liquid::barColor,
                    () -> entity.liquids.get(liquid) / owner.liquidCapacity
            ));
        }

        public MultiCrafter owner() {
            return owner;
        }

        @SuppressWarnings("unchecked")
        public <T extends Consume> T findConsumer(Boolf<Consume> filter) {
            return consumers.length == 0 ? (T) consumeBuilder.find(filter) : (T) Structs.find(consumers, filter);
        }

        public boolean hasConsumer(Consume cons) {
            return consumeBuilder.contains(cons);
        }

        public void removeConsumer(Consume cons) {
            if (consumers.length > 0) {
                return;
            }
            consumeBuilder.remove(cons);
        }

        public void removeConsumers(Boolf<Consume> b) {
            consumeBuilder.removeAll(b);
            //the power was removed, unassign it
            if (!consumeBuilder.contains(c -> c instanceof ConsumePower)) {
                consPower = null;
            }
        }

        public boolean getConsumeItem(Item item) {
            return itemFilter.containsKey(item) && itemFilter.get(item);
        }

        public boolean getConsumeLiquid(Liquid liquid) {
            return liquidFilter.containsKey(liquid) && liquidFilter.get(liquid);
        }

        public void consumeLiquid(Liquid liquid, float amount) {
            setApply(liquid);
            consume(new ConsumeLiquid(liquid, amount));
        }

        public void consumeLiquids(LiquidStack... stacks) {
            for (LiquidStack liquid : stacks) setApply(liquid.liquid);
            consume(new ConsumeLiquids(stacks));
        }

        public void consumePower(float powerPerTick) {
            consume(new ConsumePower(powerPerTick, 0.0f, false));
        }

        public void consumeItem(Item item) {
            setApply(item);
            consumeItem(item, 1);
        }

        public void consumeItem(Item item, int amount) {
            setApply(item);
            consume(new ConsumeItems(new ItemStack[]{new ItemStack(item, amount)}));
        }

        public void consumeItems(ItemStack... items) {
            for (ItemStack item : items) {
                setApply(item.item);
            }
            consume(new ConsumeItems(items));
        }

        public <T extends Consume> void consume(T consume) {
            if (consume instanceof ConsumePower) {
                consumeBuilder.removeAll(b -> b instanceof ConsumePower);
                consPower = (ConsumePower) consume;
            }
            consumeBuilder.add(consume);
        }
    }
}