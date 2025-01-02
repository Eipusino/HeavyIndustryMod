package heavyindustry.content;

import arc.func.*;
import arc.struct.*;
import arc.util.*;
import heavyindustry.core.*;
import mindustry.content.*;
import mindustry.content.TechTree.*;
import mindustry.ctype.*;
import mindustry.game.Objectives.*;
import mindustry.type.*;

import java.lang.reflect.*;
import java.util.*;

import static heavyindustry.content.HIBlocks.*;
import static heavyindustry.content.HISectorPresets.*;
import static heavyindustry.content.HIUnitTypes.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.SectorPresets.*;
import static mindustry.content.UnitTypes.*;

/**
 * Sets up content {@link TechNode tech tree nodes}. Loaded after every other content is instantiated.
 *
 * @author Eipusino
 */
public final class HITechTree {
    public static TechNode context = null;

    /** HITechTree should not be instantiated. */
    private HITechTree() {}

    /**
     * Instantiates all contents. Called in the main thread in {@link HeavyIndustryMod#loadContent()}.
     * <p>Remember not to execute it a second time, I did not take any precautionary measures.
     */
    public static void load() {
        //items,liquids
        vanillaNode(Liquids.water, () -> nodeProduce(HILiquids.brine, () -> {}));
        vanillaNode(Liquids.oil, () -> nodeProduce(HILiquids.nitratedOil, () -> {}));
        vanillaNode(Liquids.ozone, () -> nodeProduce(HILiquids.methane, () -> {}));
        vanillaNode(Items.sand, () -> {
            nodeProduce(HIItems.stone, () -> {});
            nodeProduce(HIItems.rareEarth, () -> {});
            nodeProduce(HIItems.salt, () -> {});
        });
        vanillaNode(Items.silicon, () -> nodeProduce(HIItems.nanocore, () -> nodeProduce(HILiquids.nanofluid, () -> {})));
        vanillaNode(Items.thorium, () -> nodeProduce(HIItems.uranium, () -> nodeProduce(HIItems.chromium, () -> {})));
        vanillaNode(Items.surgeAlloy, () -> nodeProduce(HIItems.heavyAlloy, () -> {}));
        //items,liquids-erekir
        vanillaNode(Items.tungsten, () -> {
            nodeProduce(HIItems.uranium, () -> {});
            nodeProduce(HIItems.chromium, () -> {});
        });
        vanillaNode(Items.oxide, () -> nodeProduce(HIItems.nanocoreErekir, () -> {}));
        //wall
        vanillaNode(copperWall, () -> node(armoredWall, () -> node(armoredWallLarge, () -> node(armoredWallHuge, () -> node(armoredWallGigantic, () -> {})))));
        vanillaNode(copperWallLarge, () -> node(copperWallHuge, () -> node(copperWallGigantic, () -> {})));
        vanillaNode(titaniumWallLarge, () -> node(titaniumWallHuge, () -> node(titaniumWallGigantic, () -> {})));
        vanillaNode(thoriumWall, () -> node(uraniumWall, () -> {
            node(uraniumWallLarge, () -> {});
            node(chromiumWall, () -> {
                node(chromiumWallLarge, () -> {});
                node(chromiumDoor, () -> node(chromiumDoorLarge, () -> {}));
            });
        }));
        vanillaNode(surgeWall, () -> node(heavyAlloyWall, () -> {
            node(heavyAlloyWallLarge, () -> {});
            node(nanoCompositeWall, () -> node(nanoCompositeWallLarge, () -> {}));
        }));
        //wall-erekir
        vanillaNode(berylliumWallLarge, () -> node(berylliumWallHuge, () -> node(berylliumWallGigantic, () -> {})));
        vanillaNode(tungstenWallLarge, () -> {
            node(tungstenWallHuge, () -> node(tungstenWallGigantic, () -> {}));
            node(aparajito, () -> node(aparajitoLarge, () -> {}));
        });
        vanillaNode(blastDoor, () -> node(blastDoorLarge, () -> node(blastDoorHuge, () -> {})));
        vanillaNode(reinforcedSurgeWallLarge, () -> node(reinforcedSurgeWallHuge, () -> node(reinforcedSurgeWallGigantic, () -> {})));
        vanillaNode(carbideWallLarge, () -> node(carbideWallHuge, () -> node(carbideWallGigantic, () -> {})));
        vanillaNode(shieldedWall, () -> node(shieldedWallLarge, () -> node(shieldedWallHuge, () -> {})));
        //drill
        vanillaNode(pneumaticDrill, () -> {
            node(titaniumDrill, () -> {});
            node(sporeFarm, () -> {});
        });
        vanillaNode(waterExtractor, () -> {
            node(largeWaterExtractor, () -> {});
            node(slagExtractor, () -> {});
        });
        vanillaNode(blastDrill, () -> {
            node(groundPenetratingDetector, () -> node(groundPenetratingDrill, () -> {}));
            node(beamDrill, Seq.with(new SectorComplete(impact0078)), () -> {});
        });
        vanillaNode(oilExtractor, () -> node(oilRig, () -> {}));
        //drill-erekir
        vanillaNode(cliffCrusher, () -> node(largeCliffCrusher, ItemStack.with(Items.graphite, 1600, Items.silicon, 600, Items.beryllium, 1200, Items.tungsten, 500), Seq.with(new OnSector(lake)), () -> {}));
        vanillaNode(impactDrill, () -> node(minerPoint, Seq.with(new Research(electrolyzer)), () -> node(minerCenter, Seq.with(new Research(atmosphericConcentrator)), () -> {})));
        vanillaNode(largePlasmaBore, () -> node(heavyPlasmaBore, ItemStack.with(Items.silicon, 6000, Items.oxide, 3000, Items.beryllium, 7000, Items.tungsten, 5000, Items.carbide, 2000), () -> {}));
        //distribution
        vanillaNode(sorter, () -> node(multiSorter, () -> {}));
        vanillaNode(junction, () -> {
            node(invertedJunction, () -> {});
            node(itemLiquidJunction, () -> {});
        });
        vanillaNode(plastaniumConveyor, () -> {
            node(plastaniumRouter, () -> {});
            node(plastaniumBridge, () -> {});
            node(stackHelper, () -> {});
        });
        vanillaNode(phaseConveyor, () -> node(phaseItemNode, () -> {}));
        vanillaNode(titaniumConveyor, () -> node(chromiumEfficientConveyor, () -> {
            node(chromiumArmorConveyor, () -> node(chromiumStackConveyor, () -> {
                node(chromiumStackRouter, () -> {});
                node(chromiumStackBridge, () -> {});
            }));
            node(chromiumTubeConveyor, () -> node(chromiumTubeDistributor, () -> {}));
            node(chromiumItemBridge, () -> {});
            node(chromiumRouter, () -> {});
            node(chromiumJunction, () -> {});
        }));
        //distribution-erekir
        vanillaNode(duct, () -> {
            node(ductJunction, () -> {});
            node(ductMultiSorter, () -> {});
        });
        vanillaNode(armoredDuct, () -> node(armoredDuctBridge, () -> {}));
        vanillaNode(ductUnloader, () -> node(rapidDuctUnloader, () -> {}));
        //liquid
        vanillaNode(liquidRouter, () -> {
            node(liquidOverflowValve, () -> node(liquidUnderflowValve, () -> {}));
            node(liquidSorter, () -> {});
            node(liquidValve, () -> {});
        });
        vanillaNode(liquidContainer, () -> node(liquidUnloader, () -> {}));
        vanillaNode(impulsePump, () -> node(turboPump, () -> {}));
        vanillaNode(phaseConduit, () -> node(phaseLiquidNode, () -> {}));
        vanillaNode(platedConduit, () -> node(chromiumArmorConduit, () -> {
            node(chromiumLiquidBridge, () -> {});
            node(chromiumArmorLiquidContainer, () -> node(chromiumArmorLiquidTank, () -> {}));
        }));
        //liquid-erekir
        vanillaNode(reinforcedLiquidContainer, () -> node(reinforcedLiquidUnloader, () -> {}));
        vanillaNode(reinforcedLiquidRouter, () -> {
            node(reinforcedLiquidOverflowValve, () -> node(reinforcedLiquidUnderflowValve, () -> {}));
            node(reinforcedLiquidSorter, () -> {});
            node(reinforcedLiquidValve, () -> {});
        });
        removeNode(reinforcedPump);
        vanillaNode(reinforcedConduit, () -> node(smallReinforcedPump, Seq.with(new OnSector(basin)), () -> node(reinforcedPump, () -> node(largeReinforcedPump, () -> {}))));
        //power
        vanillaNode(powerNode, () -> node(smartPowerNode, () -> node(powerAnalyzer, () -> {})));
        vanillaNode(powerNodeLarge, () -> node(powerNodeHuge, () -> node(powerNodePhase, () -> {})));
        vanillaNode(thoriumReactor, () -> node(uraniumReactor, () -> {}));
        vanillaNode(impactReactor, () -> node(hypermagneticReactor, () -> {}));
        vanillaNode(batteryLarge, () -> {
            node(hugeBattery, () -> {});
            node(armoredCoatedBattery, () -> {});
        });
        //power-erekir
        vanillaNode(beamNode, () -> {
            node(smartBeamNode, () -> node(reinforcedPowerAnalyzer, () -> {}));
            node(beamDiode, () -> {});
            node(beamInsulator, () -> {});
        });
        vanillaNode(turbineCondenser, () -> node(liquidConsumeGenerator, ItemStack.with(Items.beryllium, 2200, Items.graphite, 2400, Items.silicon, 2300, Items.tungsten, 1600, Items.oxide, 60), () -> {}));
        //production
        vanillaNode(kiln, () -> node(largeKiln, () -> {}));
        vanillaNode(pulverizer, () -> node(largePulverizer, () -> {
            node(uraniumSynthesizer, Seq.with(new OnSector(desolateRift)), () -> {});
            node(chromiumSynthesizer, Seq.with(new OnSector(desolateRift)), () -> {});
        }));
        vanillaNode(melter, () -> {
            node(largeMelter, () -> {});
            node(clarifier, () -> {});
        });
        vanillaNode(surgeSmelter, () -> node(heavyAlloySmelter, () -> {}));
        vanillaNode(disassembler, () -> node(metalAnalyzer, Seq.with(new OnSector(desolateRift)), () -> {}));
        vanillaNode(cryofluidMixer, () -> {
            node(largeCryofluidMixer, Seq.with(new SectorComplete(impact0078)), () -> {});
            node(nanocoreActivator, () -> {});
        });
        vanillaNode(pyratiteMixer, () -> node(largePyratiteMixer, Seq.with(new SectorComplete(facility32m)), () -> {}));
        vanillaNode(blastMixer, () -> node(largeBlastMixer, () -> {}));
        vanillaNode(cultivator, () -> node(largeCultivator, Seq.with(new SectorComplete(taintedWoods)), () -> {}));
        vanillaNode(plastaniumCompressor, () -> node(largePlastaniumCompressor, Seq.with(new SectorComplete(facility32m)), () -> node(corkscrewCompressor, () -> {})));
        vanillaNode(surgeSmelter, () -> node(largeSurgeSmelter, () -> {}));
        vanillaNode(siliconCrucible, () -> node(blastSiliconSmelter, () -> {}));
        vanillaNode(siliconSmelter, () -> node(nanocoreConstructor, Seq.with(new SectorComplete(impact0078)), () -> node(nanocorePrinter, () -> {})));
        vanillaNode(sporePress, () -> node(nitrificationReactor, () -> node(nitratedOilSedimentationTank, () -> {})));
        vanillaNode(phaseWeaver, () -> node(largePhaseWeaver, () -> node(phaseFusionInstrument, () -> {})));
        //production-erekir
        vanillaNode(siliconArcFurnace, () -> {
            node(chemicalSiliconSmelter, ItemStack.with(Items.graphite, 2800, Items.silicon, 1000, Items.tungsten, 2400, Items.oxide, 50), () -> {});
            node(ventHeater, () -> {});
            node(nanocoreConstructorErekir, Seq.with(new OnSector(crossroads)), () -> node(nanocorePrinterErekir, Seq.with(new OnSector(origin)), () -> {}));
        });
        vanillaNode(heatRedirector, () -> node(heatDriver, () -> {}));
        vanillaNode(electricHeater, () -> {
            node(largeElectricHeater, ItemStack.with(Items.tungsten, 3000, Items.oxide, 2400, Items.carbide, 800), () -> {});
            node(liquidFuelHeater, () -> {});
            node(heatReactor, () -> {});
            node(uraniumFuser, Seq.with(new OnSector(origin)), () -> node(chromiumFuser, () -> {}));
        });
        vanillaNode(oxidationChamber, () -> node(largeOxidationChamber, ItemStack.with(Items.tungsten, 3600, Items.graphite, 4400, Items.silicon, 4400, Items.beryllium, 6400, Items.oxide, 600, Items.carbide, 1400), () -> {}));
        vanillaNode(surgeCrucible, () -> node(largeSurgeCrucible, ItemStack.with(Items.graphite, 4400, Items.silicon, 4000, Items.tungsten, 4800, Items.oxide, 960, Items.surgeAlloy, 1600), () -> {}));
        vanillaNode(carbideCrucible, () -> node(largeCarbideCrucible, ItemStack.with(Items.thorium, 6000, Items.tungsten, 8000, Items.oxide, 1000, Items.carbide, 1200), () -> {}));
        //defense
        vanillaNode(coreShard, () -> node(detonator, () -> node(bombLauncher, () -> {})));
        vanillaNode(illuminator, () -> node(lighthouse, () -> {}));
        vanillaNode(mendProjector, () -> node(mendDome, () -> {}));
        vanillaNode(overdriveDome, () -> node(assignOverdrive, () -> {}));
        vanillaNode(forceProjector, () -> node(largeShieldGenerator, () -> {}));
        //defense-erekir
        vanillaNode(radar, () -> node(largeRadar, ItemStack.with(Items.graphite, 3600, Items.silicon, 3200, Items.beryllium, 600, Items.tungsten, 200, Items.oxide, 10), () -> {}));
        //storage
        vanillaNode(router, () -> node(bin, ItemStack.with(Items.copper, 550, Items.lead, 350), () -> node(machineryUnloader, ItemStack.with(Items.copper, 300, Items.lead, 200), () -> {})));
        vanillaNode(vault, () -> {
            node(cargo, () -> {});
            node(coreStorage, () -> {});
        });
        vanillaNode(unloader, () -> node(rapidUnloader, () -> node(rapidDirectionalUnloader, () -> {})));
        //storage-erekir
        vanillaNode(reinforcedVault, () -> node(reinforcedCoreStorage, () -> {}));
        //payload
        vanillaNode(payloadConveyor, () -> {
            node(payloadJunction, () -> {});
            node(payloadRail, () -> {});
        });
        //payload-erekir
        vanillaNode(reinforcedPayloadConveyor, () -> {
            node(reinforcedPayloadJunction, () -> {});
            node(reinforcedPayloadRail, () -> {});
        });
        //unit
        vanillaNode(tetrativeReconstructor, () -> node(titanReconstructor, () -> node(experimentalUnitFactory, () -> {})));
        //unit-erekir
        vanillaNode(unitRepairTower, () -> node(largeUnitRepairTower, ItemStack.with(Items.graphite, 2400, Items.silicon, 3000, Items.tungsten, 2600, Items.oxide, 1200, Items.carbide, 600), Seq.with(new OnSector(siege)), () -> {}));
        vanillaNode(basicAssemblerModule, () -> node(seniorAssemblerModule, () -> {}));
        //logic
        vanillaNode(memoryCell, () -> node(buffrerdMemoryCell, () -> node(buffrerdMemoryBank, () -> {})));
        vanillaNode(hyperProcessor, () -> node(matrixProcessor, () -> {}));
        vanillaNode(largeLogicDisplay, () -> node(hugeLogicDisplay, () -> {}));
        vanillaNode(switchBlock, () -> node(heatSink, () -> {
            node(heatFan, () -> {});
            node(heatSinkLarge, () -> {});
        }));
        //turret
        vanillaNode(segment, () -> node(dissipation, () -> {}));
        vanillaNode(duo, () -> {
            node(rocketLauncher, Seq.with(new SectorComplete(ruinousShores)), () -> {
                node(largeRocketLauncher, Seq.with(new Research(swarmer), new SectorComplete(facility32m)), () -> {});
                node(rocketSilo, Seq.with(new SectorComplete(tarFields)), () -> {});
            });
            node(cloudbreaker, () -> {});
        });
        vanillaNode(scorch, () -> node(dragonBreath, () -> {}));
        vanillaNode(arc, () -> node(hurricane, () -> {}));
        vanillaNode(salvo, () -> {
            node(spike, () -> node(fissure, () -> {}));
            node(minigun, () -> {});
        });
        vanillaNode(meltdown, () -> node(judgement, () -> {}));
        //turret-erekir
        vanillaNode(breach, () -> node(rupture, () -> {}));
        //tier6
        vanillaNode(dagger, () -> node(vanguard, () -> node(striker, () -> node(counterattack, () -> node(crush, () -> node(destruction, () -> node(purgatory, () -> {})))))));
        vanillaNode(flare, () -> node(caelifera, () -> node(schistocerca, () -> node(anthophila, () -> node(vespula, () -> node(lepidoptera, () -> node(mantodea, () -> {})))))));
        vanillaNode(reign, () -> node(suzerain, () -> {}));
        vanillaNode(corvus, () -> node(supernova, () -> {}));
        vanillaNode(toxopid, () -> node(cancer, () -> {}));
        vanillaNode(eclipse, () -> node(sunlit, () -> {}));
        vanillaNode(oct, () -> node(windstorm, () -> {}));
        vanillaNode(omura, () -> node(mosasaur, () -> {}));
        vanillaNode(navanax, () -> node(killerWhale, () -> {}));
        //tier6-erekir
        vanillaNode(conquer, () -> node(dominate, () -> {}));
        vanillaNode(collaris, () -> node(oracle, () -> {}));
        vanillaNode(disrupt, () -> node(havoc, () -> {}));
        //sector presets
        vanillaNode(impact0078, () -> node(frozenPlateau, Seq.with(new SectorComplete(impact0078)), () -> {}));
        vanillaNode(coastline, () -> {
            node(volcanicArchipelago, Seq.with(new SectorComplete(coastline)), () -> {});
            node(ironBridgeCoast, Seq.with(new SectorComplete(coastline)), () -> {});
        });
        vanillaNode(desolateRift, () -> node(moltenRiftValley, Seq.with(new SectorComplete(desolateRift)), () -> {}));
    }

    public static void vanillaNode(UnlockableContent content, Runnable children) {
        context = TechTree.all.find(t -> t.content == content);
        children.run();
    }

    public static void removeNode(UnlockableContent content) {
        context = TechTree.all.find(t -> t.content == content);
        if (context != null) {
            context.remove();
        }
    }

    public static void node(UnlockableContent content) {
        node(content, content.researchRequirements(), () -> {});
    }

    public static void node(UnlockableContent content, Runnable children) {
        node(content, content.researchRequirements(), children);
    }

    public static void node(UnlockableContent content, ItemStack[] requirements, Runnable children) {
        node(content, requirements, null, children);
    }

    public static void node(UnlockableContent content, ItemStack[] requirements, Seq<Objective> objectives, Runnable children) {
        TechNode node = new TechNode(context, content, requirements);
        if (objectives != null) node.objectives.addAll(objectives);

        TechNode prev = context;
        context = node;
        children.run();
        context = prev;
    }

    public static void node(UnlockableContent content, Seq<Objective> objectives, Runnable children) {
        node(content, content.researchRequirements(), objectives, children);
    }

    public static void nodeProduce(UnlockableContent content, Seq<Objective> objectives, Runnable children) {
        node(content, content.researchRequirements(), objectives.add(new Produce(content)), children);
    }

    public static void nodeProduce(UnlockableContent content, Runnable children) {
        nodeProduce(content, new Seq<>(), children);
    }

    /**
     * Needs for adding new content into existed TechTrees.
     * In Runnable you can use methods from {@link TechTree}
     */
    public static final class TechTreeContext {
        private static Cons<TechNode> contextSetter;

        private TechTreeContext() {}

        /** @param context parent node. */
        public static void contextNode(TechNode context, Runnable children) {
            Objects.requireNonNull(context, "techNode cannot be null");
            initContextField();

            TechNode prev = TechTree.context();
            contextSetter.get(context);
            children.run();
            contextSetter.get(prev);
        }

        /** @param contextContent parent content. */
        public static void contextNode(UnlockableContent contextContent, Runnable children) {
            Objects.requireNonNull(contextContent, "contextContent cannot be null");
            TechNode techNode = contextContent.techNode;
            Objects.requireNonNull(techNode, "contextContent.techNode cannot be null");
            contextNode(techNode, children);
        }

        private static void initContextField() {
            if (contextSetter != null) return;
            try {
                Field contextField = TechTree.class.getDeclaredField("context");
                contextField.setAccessible(true);
                contextSetter = context -> Reflect.set(null, contextField, context);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static final class AddToResearch {
        private AddToResearch() {}

        /**
         * Adding TechNode without throw exception.
         *
         * @param content mod content
         * @param researchName vanilla content name
         * @param customRequirements research resources
         * @param objectives research needs
         */
        public static void addToResearch(UnlockableContent content, String researchName, ItemStack[] customRequirements, Seq<Objective> objectives) {
            if (content == null || researchName == null) return;

            TechNode lastNode = TechTree.all.find(t -> t.content == content);
            if (lastNode != null) {
                lastNode.remove();
            }

            TechNode node = new TechNode(null, content, customRequirements != null ? customRequirements : content.researchRequirements());
            if (objectives != null && objectives.any()) {
                node.objectives.addAll(objectives);
            }

            if (node.parent != null) {
                node.parent.children.remove(node);
            }

            // find parent node.
            TechNode parent = TechTree.all.find(t -> t.content.name.contains(researchName));

            if (parent == null) return;

            // add this node to the parent
            if (!parent.children.contains(node)) {
                parent.children.add(node);
            }
            // reparent the node
            node.parent = parent;
        }

        public static void addToResearch(UnlockableContent content, String researchName, Seq<Objective> objectives) {
            addToResearch(content, researchName, ItemStack.empty, objectives);
        }

        public static void addToResearch(UnlockableContent content, String researchName, ItemStack[] customRequirements) {
            addToResearch(content, researchName, customRequirements, Seq.with());
        }

        public static void addToResearch(UnlockableContent content, String researchName) {
            addToResearch(content, researchName, ItemStack.empty, Seq.with());
        }
    }
}
