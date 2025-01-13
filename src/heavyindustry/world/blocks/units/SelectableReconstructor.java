package heavyindustry.world.blocks.units;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.units.UnitFactory.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class SelectableReconstructor extends Reconstructor {
    public Seq<DynamicUnitPlan> plans = new Seq<>();

    public SelectableReconstructor(String name) {
        super(name);
        configurable = true;
        sync = true;
        clearOnDoubleTap = true;

        //items seq is already shrunk, it's safe to access.
        consume(new ConsumeItemDynamic((SelectableReconstructorBuild tile) -> {
            if (tile.currentPlan == -1) return ItemStack.empty;
            return plans.get(tile.currentPlan).requirements;
        }));

        config(Integer.class, (SelectableReconstructorBuild tile, Integer index) -> {
            tile.currentPlan = index;
        });

        configClear((SelectableReconstructorBuild tile) -> {
            tile.currentPlan = -1;
        });
    }

    @Override
    public void setStats() {
        stats.timePeriod = constructTime;
        super.setStats();
        stats.add(Stat.productionTime, constructTime / 60f, StatUnit.seconds);
        stats.add(Stat.output, table -> {
            table.row();
            for (UnitType[] upgrade : upgrades) {
                if (upgrade[0].unlockedNow() && upgrade[1].unlockedNow()) {
                    table.table(Styles.grayPanel, t -> {
                        t.left();

                        t.image(upgrade[0].uiIcon).size(40f).pad(10f).left().scaling(Scaling.fit);
                        t.table(info -> {
                            info.add(upgrade[0].localizedName).left();
                            info.row();
                        }).pad(10f).left();
                    }).fill().padTop(5f).padBottom(5f);

                    table.table(Styles.grayPanel, t -> {
                        t.image(Icon.right).color(Pal.darkishGray).size(40f).pad(10f);
                    }).fill().padTop(5f).padBottom(5f);

                    table.table(Styles.grayPanel, t -> {
                        t.left();

                        t.image(upgrade[1].uiIcon).size(40f).pad(10f).right().scaling(Scaling.fit);
                        t.table(info -> {
                            info.add(upgrade[1].localizedName).right();
                            info.row();
                        }).pad(10f).right();
                    }).fill().padTop(5f).padBottom(5f);

                    table.row();
                }
            }
        });
    }

    public static class DynamicUnitPlan extends UnitPlan {
        public UnitType resultUnit;

        public DynamicUnitPlan(UnitType unit, UnitType result, float time, ItemStack[] requirements) {
            super(unit, time, requirements);
            resultUnit = result;
        }
    }

    public class SelectableReconstructorBuild extends ReconstructorBuild {
        public int currentPlan = -1;

        @Override
        public int getMaximumAccepted(Item item) {
            if (currentPlan == -1) return 0;
            for (ItemStack cur : plans.get(currentPlan).requirements) {
                if (cur.item == item) {
                    return Mathf.round(cur.amount * state.rules.unitCost(team));
                }
            }
            return 0;
        }

        @Override
        public Integer config() {
            return currentPlan;
        }

        @Override
        public boolean shouldShowConfigure(Player player) {
            return true;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return getMaximumAccepted(item) > items.get(item);
        }

        @Override
        public boolean shouldConsume() {
            if (payload == null) return false;
            return currentPlan != -1 && plans.get(currentPlan).unit == payload.unit.type;
        }

        @Override
        public void updateTile() {
            boolean valid = false;
            if (payload != null && currentPlan != -1) {
                var Plan = plans.get(currentPlan);
                if (Plan.unit != payload.unit.type) {
                    moveOutPayload();
                } else if (moveInPayload()) {
                    if (efficiency > 0f) {
                        valid = true;
                        progress += edelta() * state.rules.unitBuildSpeed(team);
                    }

                    if (progress >= Plan.time) {
                        payload.unit = Plan.resultUnit.create(payload.unit.team());
                        if (payload.unit.isCommandable()) {
                            if (commandPos != null) {
                                payload.unit.command().commandPosition(commandPos);
                            }

                            if (command != null) {
                                payload.unit.command().command(command);
                            }
                        }

                        progress %= 1f;
                        Effect.shake(2f, 3f, this);
                        Fx.producesmoke.at(this);
                        consume();
                        Events.fire(new UnitCreateEvent(payload.unit, this));
                    }
                }
            }

            speedScl = Mathf.lerpDelta(speedScl, Mathf.num(valid), 0.05f);
            time += edelta() * speedScl * state.rules.unitBuildSpeed(team);
        }

        @Override
        public void buildConfiguration(Table table) {
            if (canSetCommand()) {
                super.buildConfiguration(table);
                return;
            }
            Seq<UnitType> units = Seq.with(plans).map(u -> u.resultUnit).retainAll(u -> u.unlockedNow() && !u.isBanned());

            if (units.any()) {
                ItemSelection.buildTable(block, table, units, () -> currentPlan == -1 ? null : plans.get(currentPlan).resultUnit, unit -> configure(plans.indexOf(u -> u.resultUnit == unit)), selectionRows, selectionColumns);
            } else {
                table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
            }
        }

        @Override
        public boolean acceptPayload(Building sou, Payload pay) {
            if (currentPlan == -1) return false;
            if (payload == null && (enabled || sou == this) && relativeTo(sou) != rotation && pay instanceof UnitPayload unit) {
                UnitType upgrade = plans.get(currentPlan).unit;
                if (upgrade != null) {
                    if (!upgrade.unlockedNowHost() && !team.isAI()) {
                        unit.showOverlay(Icon.tree);
                    }

                    if (upgrade.isBanned()) {
                        unit.showOverlay(Icon.cancel);
                    }
                }

                return upgrade != null && (team.isAI() || upgrade.unlockedNowHost()) && !upgrade.isBanned();
            } else {
                return false;
            }
        }

        public float fraction() {
            if (currentPlan == -1) return 0;
            return progress / plans.get(currentPlan).time;
        }
    }
}
