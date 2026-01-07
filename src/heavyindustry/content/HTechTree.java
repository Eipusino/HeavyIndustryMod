package heavyindustry.content;

import arc.struct.Seq;
import heavyindustry.HVars;
import heavyindustry.util.Constant;
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
import org.jetbrains.annotations.ApiStatus.Internal;

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
	@Internal
	public static void load() {
		if (HVars.isPlugin) return;

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
			nodeProduce(HItems.stone, () -> nodeProduce(HItems.crystal));
			nodeProduce(HItems.rareEarth);
			nodeProduce(HItems.agglomerateSalt);
		});
		vanillaNode(Items.copper, () -> nodeProduce(HItems.gold));
		vanillaNode(Items.silicon, () -> {
			nodeProduce(HItems.crystallineCircuit, () -> nodeProduce(HLiquids.crystalFluid));
			nodeProduce(HItems.galliumNitride);
		});
		vanillaNode(Items.thorium, () -> nodeProduce(HItems.uranium, () -> nodeProduce(HItems.chromium)));
		vanillaNode(Items.surgeAlloy, () -> nodeProduce(HItems.heavyAlloy));
		//items,liquids-erekir
		vanillaNode(Items.beryllium, () -> nodeProduce(HItems.crystal));
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
			node(chargeWall, () -> node(chargeWallLarge));
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
		vanillaNode(laserDrill, () -> node(ionDrill, () -> node(beamDrill, Seq.with(new SectorComplete(SectorPresets.impact0078)))));
		vanillaNode(blastDrill, () -> {
			node(blastWell);
			node(cuttingDrill);
		});
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
			node(chromiumTubeConveyor);
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
		vanillaNode(impulsePump, () -> node(turboPumpSmall, () -> node(turboPump)));
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
		vanillaNode(steamGenerator, () -> node(coalPyrolyzer, () -> node(gasGenerator)));
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
			node(largePulverizer, () -> node(crystalSynthesizer, () -> {
				node(uraniumSynthesizer, Seq.with(new OnSector(SectorPresets.desolateRift)));
				node(chromiumSynthesizer, Seq.with(new OnSector(SectorPresets.desolateRift)));
			}));
		});
		vanillaNode(melter, () -> {
			node(largeMelter);
			node(clarifier, Seq.with(new Research(HLiquids.brine)));
		});
		vanillaNode(surgeSmelter, () -> node(heavyAlloySmelter));
		vanillaNode(disassembler, () -> node(metalAnalyzer, Seq.with(new OnSector(SectorPresets.desolateRift))));
		vanillaNode(cryofluidMixer, () -> {
			node(largeCryofluidMixer, Seq.with(new SectorComplete(SectorPresets.impact0078)));
			node(crystalActivator);
		});
		vanillaNode(pyratiteMixer, () -> node(largePyratiteMixer, Seq.with(new SectorComplete(SectorPresets.facility32m))));
		vanillaNode(blastMixer, () -> node(largeBlastMixer));
		vanillaNode(cultivator, () -> node(largeCultivator, Seq.with(new SectorComplete(SectorPresets.taintedWoods))));
		vanillaNode(plastaniumCompressor, () -> node(largePlastaniumCompressor, () -> node(corkscrewCompressor)));
		vanillaNode(surgeSmelter, () -> node(largeSurgeSmelter));
		vanillaNode(siliconCrucible, () -> node(blastSiliconSmelter));
		vanillaNode(siliconSmelter, () -> {
			node(largeSiliconSmelter);
			node(crystallineCircuitConstructor, Seq.with(new SectorComplete(SectorPresets.impact0078)), () -> node(crystallineCircuitPrinter));
		});
		vanillaNode(sporePress, () -> {
			node(atmosphericCollector, () -> node(atmosphericCooler));
			node(nitrificationReactor, () -> {
				node(nitratedOilPrecipitator);
				node(blastReagentMixer);
			});
		});
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
		vanillaNode(shockMine, () -> node(paralysisMine));
		vanillaNode(mendProjector, () -> node(mendDome, () -> node(sectorStructureMender)));
		vanillaNode(forceProjector, () -> node(largeShieldGenerator));
		//defense-erekir
		vanillaNode(radar, () -> node(largeRadar, ItemStack.with(Items.graphite, 3600, Items.silicon, 3200, Items.beryllium, 600, Items.tungsten, 200, Items.oxide, 10), Seq.with(new OnSector(SectorPresets.stronghold))));
		//storage
		vanillaNode(coreShard, () -> node(coreShatter));
		vanillaNode(router, () -> node(bin, ItemStack.with(Items.copper, 550, Items.lead, 350), () -> node(machineryUnloader, ItemStack.with(Items.copper, 300, Items.lead, 200))));
		vanillaNode(vault, () -> {
			node(cargo);
			node(coreStorage);
		});
		vanillaNode(unloader, () -> node(rapidUnloader, () -> node(rapidDirectionalUnloader)));
		//storage-erekir
		vanillaNode(reinforcedVault, () -> node(reinforcedCoreStorage));
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
		//campaign
		vanillaNode(launchPad, () -> node(largeLaunchPad));
		vanillaNode(landingPad, () -> node(largeLandingPad));
		//turret
		vanillaNode(duo, () -> {
			node(rocketLauncher, Seq.with(new SectorComplete(SectorPresets.ruinousShores)), () -> {
				node(caelum);
				node(largeRocketLauncher, Seq.with(new Research(swarmer), new SectorComplete(SectorPresets.facility32m)));
				node(rocketSilo, Seq.with(new SectorComplete(SectorPresets.tarFields)));
			});
			node(cloudBreaker);
		});
		vanillaNode(scatter, () -> node(stabber));
		vanillaNode(scorch, () -> node(dragonBreath));
		vanillaNode(arc, () -> node(coilBlaster, () -> {
			node(electromagneticStorm);
			node(hurricane);
		}));
		vanillaNode(hail, () -> node(mammoth));
		vanillaNode(lancer, () -> {
			node(electricArrow, () -> node(frost));
			node(breakthrough);
		});
		vanillaNode(salvo, () -> {
			node(autocannonB6);
			node(autocannonF2);
			node(shellshock, () -> node(minigun));
		});
		vanillaNode(parallax, () -> node(cobweb));
		vanillaNode(segment, () -> node(dissipation));
		vanillaNode(tsunami, () -> {
			node(turbulence);
			node(ironStream);
		});
		vanillaNode(spectre, () -> node(evilSpirits));
		vanillaNode(meltdown, () -> node(judgement));
		//turret-erekir
		vanillaNode(breach, () -> node(rupture, Seq.with(new OnSector(SectorPresets.stronghold)), () -> node(rift, Seq.with(new OnSector(SectorPresets.karst)))));
		//tier6
		vanillaNode(dagger, () -> node(vanguard, () -> node(striker, () -> node(counterattack, () -> node(crush, () -> node(destruction, () -> node(purgatory)))))));
		vanillaNode(flare, () -> node(caelifera, () -> node(schistocerca, () -> node(anthophila, () -> node(vespula, () -> node(lepidoptera, () -> node(mantodea)))))));
		vanillaNode(reign, () -> node(empire));
		vanillaNode(corvus, () -> node(supernova));
		vanillaNode(toxopid, () -> node(cancer));
		vanillaNode(eclipse, () -> node(aphelion));
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

	public static void node(UnlockableContent content) {
		node(content, content.researchRequirements(), Constant.RUNNABLE_NOTHING);
	}

	public static void node(UnlockableContent content, Runnable children) {
		node(content, content.researchRequirements(), children);
	}

	public static void node(UnlockableContent content, ItemStack[] requirements) {
		node(content, requirements, null, Constant.RUNNABLE_NOTHING);
	}

	public static void node(UnlockableContent content, ItemStack[] requirements, Runnable children) {
		node(content, requirements, null, children);
	}

	public static void node(UnlockableContent content, ItemStack[] requirements, Seq<Objective> objectives) {
		node(content, requirements, objectives, Constant.RUNNABLE_NOTHING);
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

	public static void node(UnlockableContent content, Seq<Objective> objectives) {
		node(content, content.researchRequirements(), objectives, Constant.RUNNABLE_NOTHING);
	}

	public static void nodeProduce(UnlockableContent content, Seq<Objective> objectives, Runnable children) {
		node(content, content.researchRequirements(), objectives.add(new Produce(content)), children);
	}

	public static void nodeProduce(UnlockableContent content, Runnable children) {
		nodeProduce(content, Seq.with(), children);
	}

	public static void nodeProduce(UnlockableContent content) {
		nodeProduce(content, Seq.with(), Constant.RUNNABLE_NOTHING);
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

		TechNode node = new TechNode(null, content, customRequirements == null ? content.researchRequirements() : customRequirements);

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
