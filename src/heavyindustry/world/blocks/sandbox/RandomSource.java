package heavyindustry.world.blocks.sandbox;

import arc.math.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class RandomSource extends Block {
    public int itemsPerSecond = 60000;
    public float powerProduction = 1000000 / 60f;
    public float heat = 1000;

    public RandomSource(String name) {
        super(name);
        hasItems = true;
        update = true;
        solid = true;
        noUpdateDisabled = true;
        envEnabled = Env.any;
        alwaysReplace = true;
        hasPower = true;
        consumesPower = false;
        outputsPower = true;
    }

    @Override
    public void setBars() {
        super.setBars();
        removeBar("items");
    }

    @Override
    public boolean outputsItems() {
        return true;
    }

    public class RandomSourceBuild extends Building implements HeatBlock {
        public float counter;

        @Override
        public void updateTile() {
            int r = Mathf.random(content.items().size);
            Item outputItem = content.item(r);
            int l = Mathf.random(content.liquids().size);
            Liquid outLiquid = content.liquid(l);

            counter += edelta();
            float limit = 60f / itemsPerSecond;
            while (counter >= limit && outputItem != null) {
                items.set(outputItem, 1);
                dump(outputItem);
                items.set(outputItem, 0);
                counter -= limit;
            }

            for (int i = 0; i < proximity.size; i++) {
                Building bd = proximity.get(i);
                if (bd != null && bd.block != null) {
                    boolean none = true;
                    if (bd.block.consumers != null) {
                        for (Consume c : bd.block.consumers) {
                            if (c instanceof ConsumeLiquid cl) {
                                none = false;
                                if (bd.acceptLiquid(this, cl.liquid) && bd.liquids.get(cl.liquid) < bd.block.liquidCapacity * 2) {
                                    bd.handleLiquid(this, cl.liquid, cl.amount);
                                }
                            } else if (c instanceof ConsumeLiquids cls) {
                                none = false;
                                LiquidStack[] ls = cls.liquids;
                                for (LiquidStack lk : ls) {
                                    if (bd.acceptLiquid(this, lk.liquid) && bd.liquids.get(lk.liquid) < bd.block.liquidCapacity * 2) {
                                        bd.handleLiquid(this, lk.liquid, lk.amount);
                                    }
                                }
                            } else if (c instanceof ConsumeLiquidFilter clf) {
                                none = false;
                                for (Liquid lq : content.liquids()) {
                                    if (clf.filter.get(lq) && bd.acceptLiquid(this, lq) && bd.liquids.get(lq) < bd.block.liquidCapacity * 2) {
                                        bd.handleLiquid(this, lq, clf.amount);
                                    }
                                }
                            } else if (c instanceof ConsumeLiquidsDynamic cld) {
                                none = false;
                                LiquidStack[] ls = cld.liquids.get(bd);
                                for (LiquidStack lk : ls) {
                                    if (bd.acceptLiquid(this, lk.liquid) && bd.liquids.get(lk.liquid) < bd.block.liquidCapacity * 2) {
                                        bd.handleLiquid(this, lk.liquid, lk.amount);
                                    }
                                }
                            }
                        }
                    }
                    if (none && outLiquid != null) {
                        if (bd.acceptLiquid(this, outLiquid)) {
                            bd.handleLiquid(this, outLiquid, bd.block.liquidCapacity - bd.liquids.get(outLiquid));
                        }
                    }
                }
            }
        }

        @Override
        public float getPowerProduction() {
            return enabled ? powerProduction : 0f;
        }

        @Override
        public float heat() {
            return heat;
        }

        @Override
        public float heatFrac() {
            return 1;
        }
    }
}

