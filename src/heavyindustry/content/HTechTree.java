package heavyindustry.content;

import arc.struct.Seq;
import heavyindustry.func.FuncInte;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.SectorPresets;
import mindustry.content.TechTree;
import mindustry.content.TechTree.TechNode;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives.Objective;
import mindustry.game.Objectives.OnSector;
import mindustry.game.Objectives.Produce;
import mindustry.game.Objectives.Research;
import mindustry.game.Objectives.SectorComplete;
import mindustry.type.ItemStack;

import static heavyindustry.content.HBlocks.*;
import static heavyindustry.content.HUnitTypes.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.UnitTypes.*;

/**
 * Sets up content {@link TechNode tech tree nodes}. Loaded after every other content is instantiated.
 *
 * @author Eipusino
 */
public final class HTechTree {
	public static TechNode context = null;

	/** Don't let anyone instantiate this class. */
	private HTechTree() {}

	/** Instantiates all contents. Called in the main thread in {@code HeavyIndustryMod.loadContent()}. */
	public static void load() {
		//items,liquids
		vanillaNode(Liquids.water, () -> {
			nodeProduce(HLiquids.brine);
			nodeProduce(Liquids.gallium);
			nodeProduce(Liquids.nitrogen);
		});
		vanillaNode(Liquids.oil, () -> {
			nodeProduce(HLiquids.lightOil);
			nodeProduce(HLiquids.nitratedOil, () -> nodeProduce(HLiquids.blastReagent));
			nodeProduce(HLiquids.gas);
		});
		vanillaNode(Items.sand, () -> {
			nodeProduce(HItems.stone, () -> nodeProduce(HItems.originium));
			nodeProduce(HItems.rareEarth);
			nodeProduce(HItems.agglomerateSalt);
		});
		vanillaNode(Items.copper, () -> nodeProduce(HItems.gold));
		vanillaNode(Items.silicon, () -> {
			nodeProduce(HItems.crystallineCircuit, () -> nodeProduce(HLiquids.originiumFluid));
			nodeProduce(HItems.galliumNitride);
		});
		vanillaNode(Items.thorium, () -> nodeProduce(HItems.uranium, () -> nodeProduce(HItems.chromium)));
		vanillaNode(Items.surgeAlloy, () -> nodeProduce(HItems.heavyAlloy));
		//items,liquids-erekir
		vanillaNode(Items.beryllium, () -> nodeProduce(HItems.originium));
		vanillaNode(Items.tungsten, () -> {
			nodeProduce(HItems.uranium);
			nodeProduce(HItems.chromium);
		});
		//wall
		vanillaNode(scrapWall, () -> node(oldTracks));
		vanillaNode(copperWall, () -> node(armoredWall, () -> node(armoredWallLarge, () -> node(armoredWallHuge, () -> node(armoredWallGigantic)))));
		vanillaNode(copperWallLarge, () -> node(copperWallHuge, () -> node(copperWallGigantic)));
		vanillaNode(titaniumWallLarge, () -> node(titaniumWallHuge, () -> node(titaniumWallGigantic)));
		vanillaNode(doorLarge, () -> node(doorHuge, () -> node(doorGigantic)));
		vanillaNode(plastaniumWallLarge, () -> node(plastaniumWallHuge, () -> node(plastaniumWallGigantic)));
		vanillaNode(thoriumWall, () -> node(uraniumWall, () -> {
			node(uraniumWallLarge);
			node(chromiumWall, () -> {
				node(chromiumWallLarge);
				node(chromiumDoor, () -> node(chromiumDoorLarge));
			});
		}));
		vanillaNode(thoriumWallLarge, () -> node(thoriumWallHuge, () -> node(thoriumWallGigantic)));
		vanillaNode(phaseWallLarge, () -> node(phaseWallHuge, () -> node(phaseWallGigantic)));
		vanillaNode(surgeWall, () -> node(heavyAlloyWall, () -> {
			node(heavyAlloyWallLarge);
			node(compositeWall, () -> node(compositeWallLarge));
		}));
		vanillaNode(surgeWallLarge, () -> node(surgeWallHuge, () -> node(surgeWallGigantic)));
		//wall-erekir
		vanillaNode(berylliumWallLarge, () -> node(berylliumWallHuge, () -> node(berylliumWallGigantic)));
		vanillaNode(tungstenWallLarge, () -> {
			node(tungstenWallHuge, () -> node(tungstenWallGigantic));
			node(aparajito, () -> node(aparajitoLarge));
		});
		vanillaNode(blastDoor, () -> node(blastDoorLarge, () -> node(blastDoorHuge)));
		vanillaNode(reinforcedSurgeWallLarge, () -> node(reinforcedSurgeWallHuge, () -> node(reinforcedSurgeWallGigantic)));
		vanillaNode(carbideWallLarge, () -> node(carbideWallHuge, () -> node(carbideWallGigantic)));
		vanillaNode(shieldedWall, () -> node(shieldedWallLarge, () -> node(shieldedWallHuge)));
		//drill
		vanillaNode(pneumaticDrill, () -> node(titaniumDrill));
		vanillaNode(waterExtractor, () -> {
			node(largeWaterExtractor);
			node(slagExtractor);
		});
		vanillaNode(blastDrill, () -> node(beamDrill, Seq.with(new SectorComplete(SectorPresets.impact0078))));
		vanillaNode(oilExtractor, () -> node(oilRig));
		//drill-erekir
		vanillaNode(largePlasmaBore, () -> node(heavyPlasmaBore, ItemStack.with(Items.silicon, 6000, Items.oxide, 3000, Items.beryllium, 7000, Items.tungsten, 5000, Items.carbide, 2000)));
		//distribution
		vanillaNode(sorter, () -> node(multiSorter));
		vanillaNode(junction, () -> {
			node(invertedJunction);
			node(itemLiquidJunction);
		});
		vanillaNode(plastaniumConveyor, () -> {
			node(plastaniumRouter);
			node(plastaniumBridge);
			node(stackHelper);
		});
		vanillaNode(phaseConveyor, () -> node(phaseItemNode));
		vanillaNode(titaniumConveyor, () -> node(chromiumEfficientConveyor, () -> {
			node(chromiumArmorConveyor, () -> node(chromiumStackConveyor, () -> {
				node(chromiumStackRouter);
				node(chromiumStackBridge);
			}));
			node(chromiumTubeConveyor, () -> node(chromiumTubeDistributor));
			node(chromiumItemBridge);
			node(chromiumRouter);
			node(chromiumJunction);
		}));
		//distribution-erekir
		vanillaNode(duct, () -> {
			node(ductJunction);
			node(ductMultiSorter);
		});
		vanillaNode(ductRouter, () -> node(ductDistributor));
		vanillaNode(armoredDuct, () -> node(armoredDuctBridge));
		vanillaNode(ductUnloader, () -> node(rapidDuctUnloader));
		//liquid
		vanillaNode(liquidRouter, () -> {
			node(liquidOverflowValve, () -> node(liquidUnderflowValve));
			node(liquidSorter);
			node(liquidValve);
		});
		vanillaNode(liquidContainer, () -> node(liquidUnloader));
		vanillaNode(impulsePump, () -> node(turboPump));
		vanillaNode(phaseConduit, () -> node(phaseLiquidNode));
		vanillaNode(platedConduit, () -> node(chromiumArmorConduit, () -> {
			node(chromiumLiquidBridge);
			node(chromiumArmorLiquidContainer, () -> node(chromiumArmorLiquidTank));
		}));
		//liquid-erekir
		vanillaNode(reinforcedLiquidContainer, () -> node(reinforcedLiquidUnloader));
		vanillaNode(reinforcedLiquidRouter, () -> {
			node(reinforcedLiquidOverflowValve, () -> node(reinforcedLiquidUnderflowValve));
			node(reinforcedLiquidSorter);
			node(reinforcedLiquidValve);
		});
		removeNode(reinforcedPump);
		vanillaNode(reinforcedConduit, () -> node(smallReinforcedPump, Seq.with(new OnSector(SectorPresets.basin)), () -> node(reinforcedPump, () -> node(largeReinforcedPump))));
		//power
		vanillaNode(powerNode, () -> node(smartPowerNode, () -> node(powerAnalyzer)));
		vanillaNode(powerNodeLarge, () -> node(heavyArmoredPowerNode, () -> node(microArmoredPowerNode)));
		vanillaNode(thermalGenerator, () -> node(largeThermalGenerator));
		vanillaNode(thoriumReactor, () -> node(uraniumReactor));
		vanillaNode(impactReactor, () -> node(hyperMagneticReactor));
		vanillaNode(batteryLarge, () -> {
			node(hugeBattery);
			node(armoredCoatedBattery);
		});
		//power-erekir
		vanillaNode(beamNode, () -> {
			node(smartBeamNode, () -> node(reinforcedPowerAnalyzer));
			node(beamDiode);
			node(beamInsulator);
		});
		//production
		vanillaNode(kiln, () -> node(largeKiln));
		vanillaNode(pulverizer, () -> {
			node(stoneCrusher);
			node(largePulverizer, () -> {
				node(uraniumSynthesizer, Seq.with(new OnSector(SectorPresets.desolateRift)));
				node(chromiumSynthesizer, Seq.with(new OnSector(SectorPresets.desolateRift)));
			});
		});
		vanillaNode(melter, () -> {
			node(largeMelter);
			node(clarifier, Seq.with(new Research(HLiquids.brine)));
		});
		vanillaNode(surgeSmelter, () -> node(heavyAlloySmelter));
		vanillaNode(disassembler, () -> node(metalAnalyzer, Seq.with(new OnSector(SectorPresets.desolateRift))));
		vanillaNode(cryofluidMixer, () -> {
			node(largeCryofluidMixer, Seq.with(new SectorComplete(SectorPresets.impact0078)));
			node(originiumActivator);
		});
		vanillaNode(pyratiteMixer, () -> node(largePyratiteMixer, Seq.with(new SectorComplete(SectorPresets.facility32m))));
		vanillaNode(blastMixer, () -> node(largeBlastMixer));
		vanillaNode(cultivator, () -> node(largeCultivator, Seq.with(new SectorComplete(SectorPresets.taintedWoods))));
		vanillaNode(plastaniumCompressor, () -> node(largePlastaniumCompressor, Seq.with(new SectorComplete(SectorPresets.facility32m)), () -> node(corkscrewCompressor)));
		vanillaNode(surgeSmelter, () -> node(largeSurgeSmelter));
		vanillaNode(siliconCrucible, () -> node(blastSiliconSmelter));
		vanillaNode(siliconSmelter, () -> node(crystallineCircuitConstructor, Seq.with(new SectorComplete(SectorPresets.impact0078)), () -> node(crystallineCircuitPrinter)));
		vanillaNode(sporePress, () -> node(nitrificationReactor, () -> {
			node(nitratedOilPrecipitator);
			node(blastReagentMixer);
		}));
		vanillaNode(phaseWeaver, () -> node(largePhaseWeaver, () -> node(phaseFusionInstrument)));
		//production-erekir
		vanillaNode(siliconArcFurnace, () -> {
			node(chemicalSiliconSmelter, ItemStack.with(Items.graphite, 2800, Items.silicon, 1000, Items.tungsten, 2400, Items.oxide, 50));
			node(ventHeater);
		});
		vanillaNode(electricHeater, () -> {
			node(largeElectricHeater, ItemStack.with(Items.tungsten, 3000, Items.oxide, 2400, Items.carbide, 800));
			node(heatReactor);
		});
		vanillaNode(oxidationChamber, () -> node(largeOxidationChamber, ItemStack.with(Items.tungsten, 3600, Items.graphite, 4400, Items.silicon, 4400, Items.beryllium, 6400, Items.oxide, 600, Items.carbide, 1400)));
		vanillaNode(surgeCrucible, () -> node(largeSurgeCrucible, ItemStack.with(Items.graphite, 4400, Items.silicon, 4000, Items.tungsten, 4800, Items.oxide, 960, Items.surgeAlloy, 1600), Seq.with(new OnSector(SectorPresets.karst))));
		vanillaNode(carbideCrucible, () -> node(largeCarbideCrucible, ItemStack.with(Items.thorium, 6000, Items.tungsten, 8000, Items.oxide, 1000, Items.carbide, 1200), Seq.with(new OnSector(SectorPresets.karst))));
		//defense
		vanillaNode(coreShard, () -> node(detonator, () -> node(bombLauncher)));
		vanillaNode(illuminator, () -> node(lighthouse));
		vanillaNode(mendProjector, () -> node(mendDome));
		vanillaNode(overdriveDome, () -> node(assignOverdrive));
		vanillaNode(forceProjector, () -> node(largeShieldGenerator));
		//defense-erekir
		vanillaNode(radar, () -> node(largeRadar, ItemStack.with(Items.graphite, 3600, Items.silicon, 3200, Items.beryllium, 600, Items.tungsten, 200, Items.oxide, 10), Seq.with(new OnSector(SectorPresets.stronghold))));
		//storage
		vanillaNode(router, () -> node(bin, ItemStack.with(Items.copper, 550, Items.lead, 350), () -> node(machineryUnloader, ItemStack.with(Items.copper, 300, Items.lead, 200))));
		vanillaNode(vault, () -> {
			node(cargo);
			node(coreStorage);
		});
		vanillaNode(unloader, () -> node(rapidUnloader, () -> node(rapidDirectionalUnloader)));
		//payload
		vanillaNode(payloadConveyor, () -> {
			node(payloadJunction);
			node(payloadRail);
		});
		//payload-erekir
		vanillaNode(reinforcedPayloadConveyor, () -> {
			node(reinforcedPayloadJunction);
			node(reinforcedPayloadRail);
		});
		//unit
		vanillaNode(tetrativeReconstructor, () -> node(titanReconstructor));
		//unit-erekir
		vanillaNode(unitRepairTower, () -> node(largeUnitRepairTower, ItemStack.with(Items.graphite, 2400, Items.silicon, 3000, Items.tungsten, 2600, Items.oxide, 1200, Items.carbide, 600), Seq.with(new OnSector(SectorPresets.siege))));
		vanillaNode(basicAssemblerModule, () -> node(seniorAssemblerModule));
		//logic
		vanillaNode(memoryCell, () -> node(buffrerdMemoryCell, () -> node(buffrerdMemoryBank)));
		vanillaNode(hyperProcessor, () -> node(matrixProcessor));
		vanillaNode(largeLogicDisplay, () -> node(hugeLogicDisplay));
		vanillaNode(switchBlock, () -> node(heatSink, () -> {
			node(heatFan);
			node(heatSinkLarge);
		}));
		vanillaNode(message, () -> node(characterDisplay, () -> {
			node(characterDisplayLarge);
			node(iconDisplay, () -> node(iconDisplayLarge));
		}));
		//logic-erekir
		vanillaNode(reinforcedMessage, () -> node(reinforcedCharacterDisplay, () -> {
			node(reinforcedCharacterDisplayLarge);
			node(reinforcedIconDisplay, () -> node(reinforcedIconDisplayLarge));
		}));
		//turret
		vanillaNode(segment, () -> node(dissipation));
		vanillaNode(duo, () -> {
			node(rocketLauncher, Seq.with(new SectorComplete(SectorPresets.ruinousShores)), () -> {
				node(largeRocketLauncher, Seq.with(new Research(swarmer), new SectorComplete(SectorPresets.facility32m)));
				node(rocketSilo, Seq.with(new SectorComplete(SectorPresets.tarFields)));
			});
			node(cloudbreaker);
		});
		vanillaNode(scorch, () -> node(dragonBreath));
		vanillaNode(arc, () -> node(hurricane));
		vanillaNode(lancer, () -> node(breakthrough));
		vanillaNode(salvo, () -> node(minigun));
		vanillaNode(tsunami, () -> node(ironStream));
		vanillaNode(spectre, () -> node(evilSpirits));
		vanillaNode(meltdown, () -> node(judgement));
		//turret-erekir
		vanillaNode(breach, () -> node(rupture, Seq.with(new OnSector(SectorPresets.stronghold)), () -> node(rift, Seq.with(new OnSector(SectorPresets.karst)))));
		//tier6
		vanillaNode(dagger, () -> node(vanguard, () -> node(striker, () -> node(counterattack, () -> node(crush, () -> node(destruction, () -> node(purgatory)))))));
		vanillaNode(flare, () -> node(caelifera, () -> node(schistocerca, () -> node(anthophila, () -> node(vespula, () -> node(lepidoptera, () -> node(mantodea)))))));
		vanillaNode(reign, () -> node(fearless));
		vanillaNode(corvus, () -> node(supernova));
		vanillaNode(toxopid, () -> node(cancer));
		vanillaNode(eclipse, () -> node(sunlit));
		vanillaNode(oct, () -> node(windstorm));
		vanillaNode(omura, () -> node(poseidon));
		vanillaNode(navanax, () -> node(leviathan));
		//tier6-erekir
		vanillaNode(conquer, () -> node(dominate));
		vanillaNode(collaris, () -> node(oracle));
		vanillaNode(disrupt, () -> node(havoc));
		//sector presets
		vanillaNode(SectorPresets.impact0078, () -> node(HSectorPresets.frozenPlateau, Seq.with(new SectorComplete(SectorPresets.impact0078))));
		vanillaNode(SectorPresets.coastline, () -> {
			node(HSectorPresets.volcanicArchipelago, Seq.with(new SectorComplete(SectorPresets.coastline)));
			node(HSectorPresets.ironBridgeCoast, Seq.with(new SectorComplete(SectorPresets.coastline)));
		});
		vanillaNode(SectorPresets.desolateRift, () -> node(HSectorPresets.moltenRiftValley, Seq.with(new SectorComplete(SectorPresets.desolateRift))));
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

	public static TechNode nodeRoot(String name, UnlockableContent content, Runnable children) {
		return nodeRoot(name, content, false, children);
	}

	public static TechNode nodeRoot(String name, UnlockableContent content, boolean requireUnlock, Runnable children) {
		TechNode root = node(content, content.researchRequirements(), children);
		root.name = name;
		root.requiresUnlock = requireUnlock;
		TechTree.roots.add(root);
		return root;
	}

	public static TechNode node(UnlockableContent content) {
		return node(content, content.researchRequirements(), FuncInte.RUNNABLE_NOTHING);
	}

	public static TechNode node(UnlockableContent content, Runnable children) {
		return node(content, content.researchRequirements(), children);
	}

	public static TechNode node(UnlockableContent content, ItemStack[] requirements) {
		return node(content, requirements, null, FuncInte.RUNNABLE_NOTHING);
	}

	public static TechNode node(UnlockableContent content, ItemStack[] requirements, Runnable children) {
		return node(content, requirements, null, children);
	}

	public static TechNode node(UnlockableContent content, ItemStack[] requirements, Seq<Objective> objectives) {
		return node(content, requirements, objectives, FuncInte.RUNNABLE_NOTHING);
	}

	public static TechNode node(UnlockableContent content, ItemStack[] requirements, Seq<Objective> objectives, Runnable children) {
		TechNode node = new TechNode(context, content, requirements);
		if (objectives != null) node.objectives.addAll(objectives);

		TechNode prev = context;
		context = node;
		children.run();
		context = prev;

		return node;
	}

	public static TechNode node(UnlockableContent content, Seq<Objective> objectives, Runnable children) {
		return node(content, content.researchRequirements(), objectives, children);
	}

	public static TechNode node(UnlockableContent content, Seq<Objective> objectives) {
		return node(content, content.researchRequirements(), objectives, FuncInte.RUNNABLE_NOTHING);
	}

	public static TechNode nodeProduce(UnlockableContent content, Seq<Objective> objectives, Runnable children) {
		return node(content, content.researchRequirements(), objectives.add(new Produce(content)), children);
	}

	public static TechNode nodeProduce(UnlockableContent content, Runnable children) {
		return nodeProduce(content, Seq.with(), children);
	}

	public static TechNode nodeProduce(UnlockableContent content) {
		return nodeProduce(content, Seq.with(), FuncInte.RUNNABLE_NOTHING);
	}

	// -----legacy-addToResearch-----

	public static void research(UnlockableContent content, UnlockableContent parentContent) {
		research(content, parentContent, ItemStack.empty, Seq.with());
	}

	public static void research(UnlockableContent content, UnlockableContent parentContent, Seq<Objective> objectives) {
		research(content, parentContent, ItemStack.empty, objectives);
	}

	public static void research(UnlockableContent content, UnlockableContent parentContent, ItemStack[] customRequirements) {
		research(content, parentContent, customRequirements, Seq.with());
	}

	public static void research(UnlockableContent content, UnlockableContent parentContent, ItemStack[] customRequirements, Seq<Objective> objectives) {
		if (content == null || parentContent == null) return;

		TechNode lastNode = TechTree.all.find(t -> t.content == content);
		if (lastNode != null) {
			lastNode.remove();
		}

		TechNode node = new TechNode(null, content, customRequirements != null ? customRequirements : content.researchRequirements());

		if (objectives != null) {
			node.objectives.addAll(objectives);
		}

		if (node.parent != null) {
			node.parent.children.remove(node);
		}

		// find parent node.
		TechNode parent = TechTree.all.find(t -> t.content == parentContent);

		if (parent == null) return;

		// add this node to the parent
		if (!parent.children.contains(node)) {
			parent.children.add(node);
		}
		// reparent the node
		node.parent = parent;
	}
}
