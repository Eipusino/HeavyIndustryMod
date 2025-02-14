package heavyindustry.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import heavyindustry.core.*;
import heavyindustry.entities.bullet.*;
import heavyindustry.entities.effect.*;
import heavyindustry.gen.*;
import heavyindustry.graphics.*;
import heavyindustry.util.*;
import heavyindustry.world.blocks.defense.*;
import heavyindustry.world.blocks.defense.turrets.*;
import heavyindustry.world.blocks.distribution.*;
import heavyindustry.world.blocks.environment.*;
import heavyindustry.world.blocks.heat.*;
import heavyindustry.world.blocks.liquid.*;
import heavyindustry.world.blocks.logic.*;
import heavyindustry.world.blocks.payload.*;
import heavyindustry.world.blocks.power.*;
import heavyindustry.world.blocks.production.*;
import heavyindustry.world.blocks.sandbox.*;
import heavyindustry.world.blocks.storage.*;
import heavyindustry.world.blocks.units.*;
import heavyindustry.world.draw.*;
import heavyindustry.world.meta.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.net.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.logic.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.sandbox.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.units.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static heavyindustry.Varsf.*;
import static mindustry.Vars.*;
import static mindustry.type.ItemStack.*;

/**
 * Defines the {@linkplain Block blocks} this mod offers.
 * <p>It is now quite large in a scale, even approaching the size of vanilla's {@link Blocks}.
 * But I don't want to divide it into multiple classes.
 *
 * @author Eipusino
 */
public final class Blocksf {
	public static Block
			//environment
			cliff, cliffHelper,
			darkPanel7, darkPanel8, darkPanel9, darkPanel10, darkPanel11, darkPanelDamaged,
			stoneVent, basaltVent, shaleVent, basaltSpikes, basaltWall, basaltGraphiticWall, basaltPyratiticWall, snowySand, snowySandWall, arkyciteSand, arkyciteSandWall, arkyciteSandBoulder, darksandBoulder,
			concreteBlank, concreteFill, concreteNumber, concreteStripe, concrete, stoneFullTiles, stoneFull, stoneHalf, stoneTiles, concreteWall, pit, waterPit,
			brine, nanoFluid,
			oldTracks,
			metalFloorWater, metalFloorWater2, metalFloorWater3, metalFloorWater4, metalFloorWater5, metalFloorDamagedWater,
			stoneWater, shaleWater, basaltWater, mudWater,
			overgrownGrass, overgrownShrubs, overgrownPine,
			corruptedMoss, corruptedSporeMoss, corruptedSporeRocks, corruptedSporePine, corruptedSporeFern, corruptedSporePlant, corruptedSporeTree,
			mycelium, myceliumSpore, myceliumShrubs, myceliumPine,
			softRareEarth, patternRareEarth, softRareEarthWall,
			oreUranium, oreChromium,
			//wall
			copperWallHuge, copperWallGigantic, titaniumWallHuge, titaniumWallGigantic, armoredWall, armoredWallLarge, armoredWallHuge, armoredWallGigantic,
			uraniumWall, uraniumWallLarge, chromiumWall, chromiumWallLarge, chromiumDoor, chromiumDoorLarge, heavyAlloyWall, heavyAlloyWallLarge, nanoCompositeWall, nanoCompositeWallLarge, shapedWall,
			//wall-erekir
			berylliumWallHuge, berylliumWallGigantic, tungstenWallHuge, tungstenWallGigantic, blastDoorLarge, blastDoorHuge, reinforcedSurgeWallHuge, reinforcedSurgeWallGigantic, carbideWallHuge, carbideWallGigantic, shieldedWallLarge, shieldedWallHuge,
			aparajito, aparajitoLarge,
			//drill
			titaniumDrill, largeWaterExtractor, slagExtractor, oilRig, beamDrill,
			//drill-erekir
			heavyPlasmaBore, minerPoint, minerCenter,
			//distribution
			invertedJunction, itemLiquidJunction, multiSorter, plastaniumRouter, plastaniumBridge, stackHelper, chromiumEfficientConveyor, chromiumArmorConveyor, chromiumTubeConveyor, chromiumTubeDistributor, chromiumStackConveyor, chromiumStackRouter, chromiumStackBridge, chromiumJunction, chromiumRouter, chromiumItemBridge,
			phaseItemNode, rapidDirectionalUnloader,
			//distribution-erekir
			ductJunction, ductDistributor, ductMultiSorter, armoredDuctBridge, rapidDuctUnloader,
			//liquid
			liquidSorter, liquidValve, liquidOverflowValve, liquidUnderflowValve, liquidUnloader, liquidMassDriver, turboPump, phaseLiquidNode, chromiumArmorConduit, chromiumLiquidBridge, chromiumArmorLiquidContainer, chromiumArmorLiquidTank,
			//liquid-erekir
			reinforcedLiquidOverflowValve, reinforcedLiquidUnderflowValve, reinforcedLiquidUnloader, reinforcedLiquidSorter, reinforcedLiquidValve, smallReinforcedPump, largeReinforcedPump,
			//power
			smartPowerNode, powerNodePhase, powerNodeHuge, powerAnalyzer, lunarPanel, uraniumReactor, hyperMagneticReactor, hugeBattery, armoredCoatedBattery,
			//power-erekir
			smartBeamNode, beamDiode, beamInsulator, reinforcedPowerAnalyzer, liquidConsumeGenerator,
			//production
			largeKiln, largePulverizer, largeMelter, largeCryofluidMixer, largePyratiteMixer, largeBlastMixer, largeCultivator, sporeFarm, largePlastaniumCompressor, largeSurgeSmelter, blastSiliconSmelter,
			nanoCoreConstructor, nanoCorePrinter, nanoCoreActivator, largePhaseWeaver, phaseFusionInstrument, clarifier, ironcladCompressor,
			originiumHeater,
			uraniumSynthesizer, chromiumSynthesizer, heavyAlloySmelter, metalAnalyzer, nitrificationReactor, nitratedOilSedimentationTank,
			//production-erekir
			ventHeater, chemicalSiliconSmelter, largeElectricHeater, liquidFuelHeater, heatDriver, largeOxidationChamber, largeSurgeCrucible, largeCarbideCrucible,
			uraniumFuser, chromiumFuser,
			//defense
			lighthouse, mendDome, sectorStructureMender, assignOverdrive, largeShieldGenerator, paralysisMine, detonator, bombLauncher,
			//defense-erekir
			largeRadar,
			//storage
			cargo, bin, machineryUnloader, rapidUnloader, coreStorage,
			//storage-erekir
			reinforcedCoreStorage,
			//payload
			payloadJunction, payloadRail,
			//payload-erekir
			reinforcedPayloadJunction, reinforcedPayloadRail,
			//unit
			unitMaintenanceDepot, titanReconstructor, experimentalUnitFactory,
			//unit-erekir
			largeUnitRepairTower, seniorAssemblerModule,
			//logic
			matrixProcessor, hugeLogicDisplay, buffrerdMemoryCell, buffrerdMemoryBank, heatSink, heatFan, heatSinkLarge, laserRuler, labelMessage,
			//turret
			dissipation, rocketLauncher, largeRocketLauncher, rocketSilo,
			dragonBreath, breakthrough, cloudbreaker, minigun,
			spike, fissure,
			hurricane, judgement, evilSpirits,
			//turret-erekir
			rupture,
			//sandbox
			unitIniter,
			reinforcedItemSource, reinforcedLiquidSource, reinforcedPowerSource, reinforcedPayloadSource, adaptiveSource,
			omniNode, ultraAssignOverdrive,
			teamChanger, barrierProjector, nihility, invincibleWall, invincibleWallLarge, invincibleWallHuge, invincibleWallGigantic,
			mustDieTurret, oneShotTurret, pointTurret,
			nextWave;

	/** Don't let anyone instantiate this class. */
	private Blocksf() {}

	/** Instantiates all contents. Called in the main thread in {@link HeavyIndustryMod#loadContent()}. */
	public static void load() {
		//environment
		cliff = new Clifff("cliff");
		cliffHelper = new CliffHelper("cliff-helper");
		darkPanel7 = new Floor("dark-panel-7", 0);
		darkPanel8 = new Floor("dark-panel-8", 0);
		darkPanel9 = new Floor("dark-panel-9", 0);
		darkPanel10 = new Floor("dark-panel-10", 0);
		darkPanel11 = new Floor("dark-panel-11", 0);
		darkPanelDamaged = new Floor("dark-panel-damaged", 3);
		stoneVent = new SteamVent("stone-vent") {{
			variants = 2;
			parent = blendGroup = Blocks.stone;
			attributes.set(Attribute.steam, 1f);
		}};
		basaltVent = new SteamVent("basalt-vent") {{
			variants = 2;
			parent = blendGroup = Blocks.basalt;
			attributes.set(Attribute.steam, 1f);
		}};
		shaleVent = new SteamVent("shale-vent") {{
			variants = 3;
			parent = blendGroup = Blocks.shale;
			attributes.set(Attribute.steam, 1f);
		}};
		basaltSpikes = new Floor("basalt-spikes", 4) {{
			attributes.set(Attribute.water, -0.3f);
		}};
		basaltWall = new StaticWall("basalt-wall") {{
			variants = 3;
			attributes.set(Attribute.sand, 0.7f);
			basaltSpikes.asFloor().wall = Blocks.basalt.asFloor().wall = Blocks.hotrock.asFloor().wall = Blocks.magmarock.asFloor().wall = this;
		}};
		basaltGraphiticWall = new StaticWall("basalt-graphitic-wall") {{
			itemDrop = Items.graphite;
			variants = 3;
			attributes.set(Attribute.sand, 0.7f);
		}};
		basaltPyratiticWall = new StaticWall("basalt-pyratitic-wall") {{
			itemDrop = Items.pyratite;
			variants = 3;
			attributes.set(Attribute.sand, 0.7f);
		}};
		snowySand = new Floor("snowy-sand", 3) {{
			itemDrop = Items.sand;
			attributes.set(Attribute.water, 0.2f);
			attributes.set(Attribute.oil, 0.5f);
			playerUnmineable = true;
		}};
		snowySandWall = new StaticWall("snowy-sand-wall") {{
			variants = 2;
			attributes.set(Attribute.sand, 2f);
		}};
		arkyciteSand = new Floor("arkycite-sand", 3) {{
			itemDrop = Items.sand;
			attributes.set(HIAttribute.arkycite, 1);
			playerUnmineable = true;
		}};
		arkyciteSandWall = new StaticWall("arkycite-sand-wall") {{
			variants = 2;
			attributes.set(Attribute.sand, 2f);
		}};
		arkyciteSandBoulder = new Prop("arkycite-sand-boulder") {{
			variants = 2;
			arkyciteSand.asFloor().decoration = this;
		}};
		darksandBoulder = new Prop("darksand-boulder") {{
			variants = 2;
			Blocks.darksand.asFloor().decoration = this;
		}};
		concreteBlank = new Floor("concrete-blank", 3) {{
			attributes.set(Attribute.water, -0.85f);
		}};
		concreteFill = new Floor("concrete-fill", 0) {{
			attributes.set(Attribute.water, -0.85f);
		}};
		concreteNumber = new Floor("concrete-number", 10) {{
			attributes.set(Attribute.water, -0.85f);
		}};
		concreteStripe = new Floor("concrete-stripe", 3) {{
			attributes.set(Attribute.water, -0.85f);
		}};
		concrete = new Floor("concrete", 3) {{
			attributes.set(Attribute.water, -0.85f);
		}};
		stoneFullTiles = new Floor("stone-full-tiles", 3) {{
			attributes.set(Attribute.water, -0.75f);
		}};
		stoneFull = new Floor("stone-full", 3) {{
			attributes.set(Attribute.water, -0.75f);
		}};
		stoneHalf = new Floor("stone-half", 3) {{
			attributes.set(Attribute.water, -0.5f);
		}};
		stoneTiles = new Floor("stone-tiles", 3) {{
			attributes.set(Attribute.water, -0.5f);
		}};
		concreteWall = new ConnectedWall("concrete-wall") {{
			variants = 0;
		}};
		pit = new Floor("pit", 0) {{
			cacheLayer = CacheLayerf.pit;
			placeableOn = false;
			canShadow = false;
			solid = true;
		}
			@Override
			public TextureRegion[] icons() {
				return new TextureRegion[]{fullIcon};
			}
		};
		waterPit = new Floor("water-pit", 0) {{
			cacheLayer = CacheLayerf.waterPit;
			isLiquid = true;
			drownTime = 20f;
			speedMultiplier = 0.1f;
			liquidMultiplier = 2f;
			status = StatusEffects.wet;
			statusDuration = 120f;
			liquidDrop = Liquids.water;
		}
			@Override
			public TextureRegion[] icons() {
				return new TextureRegion[]{fullIcon};
			}
		};
		softRareEarth = new Floor("soft-rare-earth", 3) {{
			itemDrop = Itemsf.rareEarth;
			playerUnmineable = true;
		}};
		patternRareEarth = new Floor("pattern-rare-earth", 4) {{
			itemDrop = Itemsf.rareEarth;
			playerUnmineable = true;
		}};
		softRareEarthWall = new StaticWall("soft-rare-earth-wall") {{
			variants = 2;
			itemDrop = Itemsf.rareEarth;
			softRareEarth.asFloor().wall = patternRareEarth.asFloor().wall = this;
		}};
		brine = new Floor("pooled-brine", 0) {{
			drownTime = 200f;
			speedMultiplier = 0.1f;
			variants = 0;
			liquidDrop = Liquidsf.brine;
			liquidMultiplier = 1.1f;
			isLiquid = true;
			cacheLayer = CacheLayerf.brine;
			albedo = 1f;
		}};
		nanoFluid = new Floor("pooled-nano-fluid", 0) {{
			status = StatusEffectsf.regenerating;
			statusDuration = 60f;
			drownTime = 160f;
			speedMultiplier = 0.6f;
			liquidDrop = Liquidsf.nanoFluid;
			isLiquid = true;
			cacheLayer = CacheLayerf.nanoFluid;
			liquidMultiplier = 0.5f;
			emitLight = true;
			lightRadius = 30f;
			lightColor = Color.green.cpy().a(0.19f);
		}};
		oldTracks = new Wall("old-tracks") {{
			requirements(Category.defense, BuildVisibility.sandboxOnly, with(Items.scrap, 12));
			variants = 3;
			health = 450;
			rotate = true;
		}};
		metalFloorWater = new Floor("metal-floor-water", 0) {{
			speedMultiplier = 0.6f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
		}};
		metalFloorWater2 = new Floor("metal-floor-water-2", 0) {{
			speedMultiplier = 0.6f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
		}};
		metalFloorWater3 = new Floor("metal-floor-water-3", 0) {{
			speedMultiplier = 0.6f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
		}};
		metalFloorWater4 = new Floor("metal-floor-water-4", 0) {{
			speedMultiplier = 0.6f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
		}};
		metalFloorWater5 = new Floor("metal-floor-water-5", 0) {{
			speedMultiplier = 0.6f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
		}};
		metalFloorDamagedWater = new Floor("metal-floor-damaged-water", 3) {{
			speedMultiplier = 0.6f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
		}};
		stoneWater = new Floor("stone-water", 3) {{
			speedMultiplier = 0.6f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
		}};
		shaleWater = new Floor("shale-water", 3) {{
			speedMultiplier = 0.6f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
		}};
		basaltWater = new Floor("basalt-water", 3) {{
			speedMultiplier = 0.6f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
		}};
		mudWater = new Floor("mud-water", 0) {{
			speedMultiplier = 0.5f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
		}};
		overgrownGrass = new Floor("overgrown-grass", 3) {{
			speedMultiplier = 0.9f;
		}};
		overgrownShrubs = new StaticWall("overgrown-shrubs") {{
			variants = 2;
			overgrownGrass.asFloor().wall = this;
		}};
		overgrownPine = new StaticWall("overgrown-pine") {{
			variants = 2;
		}};
		corruptedMoss = new Floor("corrupted-moss", 3) {{
			speedMultiplier = 0.9f;
			attributes.set(Attribute.water, 0.1f);
		}};
		corruptedSporeMoss = new Floor("corrupted-spore-moss", 3) {{
			speedMultiplier = 0.85f;
			attributes.set(Attribute.water, 0.1f);
		}};
		corruptedSporeRocks = new StaticWall("corrupted-spore-rocks") {{
			variants = 2;
			corruptedSporeMoss.asFloor().wall = this;
		}};
		corruptedSporePine = new StaticWall("corrupted-spore-pine") {{
			variants = 2;
		}};
		corruptedSporeFern = new TreeBlock("corrupted-spore-fern");
		corruptedSporePlant = new TreeBlock("corrupted-spore-plant");
		corruptedSporeTree = new TreeBlock("corrupted-spore-tree");
		mycelium = new Floor("mycelium", 3) {{
			speedMultiplier = 0.9f;
			attributes.set(Attribute.water, 0.1f);
		}};
		myceliumSpore = new Floor("mycelium-spore", 3) {{
			speedMultiplier = 0.9f;
			attributes.set(Attribute.water, 0.1f);
		}};
		myceliumShrubs = new StaticWall("mycelium-shrubs") {{
			variants = 2;
			mycelium.asFloor().wall = myceliumSpore.asFloor().wall = this;
		}};
		myceliumPine = new StaticWall("mycelium-pine") {{
			variants = 2;
		}};
		oreUranium = new OreBlock("ore-uranium", Itemsf.uranium) {{
			variants = 3;
			oreDefault = true;
			oreThreshold = 0.89f;
			oreScale = 33;
		}};
		oreChromium = new OreBlock("ore-chromium", Itemsf.chromium) {{
			variants = 3;
			oreDefault = true;
			oreThreshold = 0.9f;
			oreScale = 32;
		}};
		//wall
		copperWallHuge = new Wall("copper-wall-huge") {{
			requirements(Category.defense, with(Items.copper, 54));
			size = 3;
			health = 2880;
			armor = 1f;
		}};
		copperWallGigantic = new Wall("copper-wall-gigantic") {{
			requirements(Category.defense, with(Items.copper, 96));
			size = 4;
			health = 5120;
			armor = 1f;
		}};
		titaniumWallHuge = new Wall("titanium-wall-huge") {{
			requirements(Category.defense, with(Items.titanium, 54));
			size = 3;
			health = 3960;
			armor = 2f;
		}};
		titaniumWallGigantic = new Wall("titanium-wall-gigantic") {{
			requirements(Category.defense, with(Items.titanium, 96));
			size = 4;
			health = 7040;
			armor = 2f;
		}};
		armoredWall = new Wall("armored-wall") {{
			requirements(Category.defense, with(Items.copper, 5, Items.lead, 3, Items.graphite, 2));
			size = 1;
			health = 360;
			armor = 5f;
		}};
		armoredWallLarge = new Wall("armored-wall-large") {{
			requirements(Category.defense, with(Items.copper, 20, Items.lead, 12, Items.graphite, 8));
			size = 2;
			health = 1440;
			armor = 5f;
		}};
		armoredWallHuge = new Wall("armored-wall-huge") {{
			requirements(Category.defense, with(Items.copper, 45, Items.lead, 27, Items.graphite, 18));
			size = 3;
			health = 3240;
			armor = 5f;
		}};
		armoredWallGigantic = new Wall("armored-wall-gigantic") {{
			requirements(Category.defense, with(Items.copper, 80, Items.lead, 48, Items.graphite, 32));
			size = 4;
			health = 5760;
			armor = 5f;
		}};
		uraniumWall = new Wall("uranium-wall") {{
			requirements(Category.defense, with(Itemsf.uranium, 6));
			size = 1;
			health = 1680;
			armor = 24f;
			absorbLasers = true;
			crushDamageMultiplier = 0.8f;
		}};
		uraniumWallLarge = new Wall("uranium-wall-large") {{
			requirements(Category.defense, with(Itemsf.uranium, 24));
			size = 2;
			health = 6720;
			armor = 24f;
			absorbLasers = true;
			crushDamageMultiplier = 0.8f;
		}};
		chromiumWall = new Wall("chromium-wall") {{
			requirements(Category.defense, with(Itemsf.chromium, 6));
			size = 1;
			health = 1770;
			armor = 36f;
			absorbLasers = true;
			crushDamageMultiplier = 0.7f;
		}};
		chromiumWallLarge = new Wall("chromium-wall-large") {{
			requirements(Category.defense, with(Itemsf.chromium, 24));
			size = 2;
			health = 7080;
			armor = 36f;
			absorbLasers = true;
			crushDamageMultiplier = 0.7f;
		}};
		chromiumDoor = new AutoDoor("chromium-door") {{
			requirements(Category.defense, with(Itemsf.chromium, 6, Items.silicon, 4));
			size = 1;
			health = 1770;
			armor = 36f;
			absorbLasers = true;
			crushDamageMultiplier = 0.7f;
		}};
		chromiumDoorLarge = new AutoDoor("chromium-door-large") {{
			requirements(Category.defense, with(Itemsf.chromium, 24, Items.silicon, 16));
			size = 2;
			health = 7080;
			armor = 36f;
			absorbLasers = true;
			crushDamageMultiplier = 0.7f;
		}};
		heavyAlloyWall = new Wall("heavy-alloy-wall") {{
			requirements(Category.defense, with(Itemsf.heavyAlloy, 6, Items.metaglass, 3, Items.plastanium, 4));
			size = 1;
			health = 3220;
			armor = 48f;
			absorbLasers = insulated = true;
			crushDamageMultiplier = 0.5f;
		}};
		heavyAlloyWallLarge = new Wall("heavy-alloy-wall-large") {{
			requirements(Category.defense, with(Itemsf.heavyAlloy, 24, Items.metaglass, 12, Items.plastanium, 16));
			size = 2;
			health = 12880;
			armor = 48f;
			absorbLasers = insulated = true;
			crushDamageMultiplier = 0.5f;
		}};
		nanoCompositeWall = new RegenWall("nano-composite-wall") {{
			requirements(Category.defense, with(Itemsf.nanoCore, 2, Itemsf.heavyAlloy, 6, Items.metaglass, 1, Items.plastanium, 4));
			size = 1;
			health = 2680;
			armor = 42f;
			absorbLasers = insulated = true;
			crushDamageMultiplier = 0.5f;
			healPercent = 3f / 60f;
			chanceHeal = 0.15f;
			regenPercent = 0.5f;
		}};
		nanoCompositeWallLarge = new RegenWall("nano-composite-wall-large") {{
			requirements(Category.defense, with(Itemsf.nanoCore, 8, Itemsf.heavyAlloy, 24, Items.metaglass, 4, Items.plastanium, 16));
			size = 2;
			health = 10720;
			armor = 42f;
			absorbLasers = insulated = true;
			crushDamageMultiplier = 0.5f;
			healPercent = 3f / 60f;
			chanceHeal = 0.15f;
			regenPercent = 0.5f;
		}};
		shapedWall = new ShapedWall("shaped-wall") {{
			requirements(Category.defense, BuildVisibility.sandboxOnly, with(Itemsf.heavyAlloy, 8, Items.phaseFabric, 6));
			health = 3080;
			armor = 16f;
			insulated = absorbLasers = true;
			crushDamageMultiplier = 0.5f;
			maxShareStep = 3;
		}};
		//wall-erekir
		berylliumWallHuge = new Wall("beryllium-wall-huge") {{
			requirements(Category.defense, with(Items.beryllium, 54));
			health = 4680;
			armor = 2f;
			buildCostMultiplier = 3f;
			size = 3;
			hideDetails = false;
		}};
		berylliumWallGigantic = new Wall("beryllium-wall-gigantic") {{
			requirements(Category.defense, with(Items.beryllium, 96));
			health = 8320;
			armor = 2f;
			buildCostMultiplier = 2f;
			size = 4;
			hideDetails = false;
		}};
		tungstenWallHuge = new Wall("tungsten-wall-huge") {{
			requirements(Category.defense, with(Items.tungsten, 54));
			health = 6480;
			armor = 14f;
			buildCostMultiplier = 3f;
			size = 3;
			hideDetails = false;
		}};
		tungstenWallGigantic = new Wall("tungsten-wall-gigantic") {{
			requirements(Category.defense, with(Items.tungsten, 96));
			health = 11520;
			armor = 14f;
			buildCostMultiplier = 2f;
			size = 4;
			hideDetails = false;
		}};
		blastDoorLarge = new AutoDoor("blast-door-large") {{
			requirements(Category.defense, with(Items.tungsten, 54, Items.silicon, 54));
			health = 6300;
			armor = 14f;
			size = 3;
			hideDetails = false;
		}};
		blastDoorHuge = new AutoDoor("blast-door-huge") {{
			requirements(Category.defense, with(Items.tungsten, 96, Items.silicon, 96));
			health = 11200;
			armor = 14f;
			size = 4;
			hideDetails = false;
		}};
		reinforcedSurgeWallHuge = new Wall("reinforced-surge-wall-huge") {{
			requirements(Category.defense, with(Items.surgeAlloy, 54, Items.tungsten, 18));
			health = 9000;
			lightningChance = 0.1f;
			lightningDamage = 30f;
			armor = 20f;
			size = 3;
			researchCost = with(Items.surgeAlloy, 120, Items.tungsten, 600);
			hideDetails = false;
		}};
		reinforcedSurgeWallGigantic = new Wall("reinforced-surge-wall-gigantic") {{
			requirements(Category.defense, with(Items.surgeAlloy, 96, Items.tungsten, 32));
			health = 16000;
			lightningChance = 0.1f;
			lightningDamage = 30f;
			armor = 20f;
			size = 4;
			researchCost = with(Items.surgeAlloy, 240, Items.tungsten, 1200);
			hideDetails = false;
		}};
		carbideWallHuge = new Wall("carbide-wall-huge") {{
			requirements(Category.defense, with(Items.thorium, 54, Items.carbide, 54));
			health = 9720;
			armor = 16f;
			size = 3;
			hideDetails = false;
		}};
		carbideWallGigantic = new Wall("carbide-wall-gigantic") {{
			requirements(Category.defense, with(Items.thorium, 96, Items.carbide, 96));
			health = 17280;
			armor = 16f;
			size = 4;
			hideDetails = false;
		}};
		shieldedWallLarge = new ShieldWall("shielded-wall-large") {{
			requirements(Category.defense, with(Items.phaseFabric, 45, Items.surgeAlloy, 27, Items.beryllium, 27));
			outputsPower = false;
			hasPower = true;
			consumesPower = true;
			conductivePower = true;
			chanceDeflect = 8f;
			health = 9360;
			armor = 15f;
			size = 3;
			shieldHealth = 2200f;
			breakCooldown = 60f * 15f;
			regenSpeed = 2f;
			consumePower(4f / 60f);
			hideDetails = false;
		}};
		shieldedWallHuge = new ShieldWall("shielded-wall-huge") {{
			requirements(Category.defense, with(Items.phaseFabric, 80, Items.surgeAlloy, 48, Items.beryllium, 48));
			outputsPower = false;
			hasPower = true;
			consumesPower = true;
			conductivePower = true;
			chanceDeflect = 8f;
			health = 16640;
			armor = 15f;
			size = 4;
			shieldHealth = 3600f;
			breakCooldown = 60f * 15f;
			regenSpeed = 2f;
			consumePower(6f / 60f);
			hideDetails = false;
		}};
		aparajito = new AparajitoWall("aparajito") {{
			requirements(Category.defense, with(Itemsf.uranium, 3, Itemsf.chromium, 4, Items.phaseFabric, 2));
			size = 1;
			health = 2055;
			armor = 44f;
			healColor = Palf.chromiumGrey;
			buildCostMultiplier = 4f;
		}};
		aparajitoLarge = new AparajitoWall("aparajito-large") {{
			requirements(Category.defense, with(Itemsf.uranium, 12, Itemsf.chromium, 16, Items.phaseFabric, 8));
			size = 2;
			health = 8220;
			armor = 44f;
			healColor = Palf.chromiumGrey;
			buildCostMultiplier = 4f;
		}};
		//drill
		titaniumDrill = new Drill("titanium-drill") {{
			requirements(Category.production, with(Items.copper, 20, Items.graphite, 15, Items.titanium, 10));
			size = 2;
			drillTime = 340f;
			tier = 4;
			consumeLiquid(Liquids.water, 0.06f).boost();
		}};
		largeWaterExtractor = new SolidPump("large-water-extractor") {{
			requirements(Category.production, with(Items.lead, 60, Items.titanium, 40, Items.thorium, 20, Items.graphite, 80, Items.metaglass, 50));
			result = Liquids.water;
			pumpAmount = 0.33f;
			size = 3;
			liquidCapacity = 80f;
			rotateSpeed = 1.6f;
			attribute = Attribute.water;
			envRequired |= Env.groundWater;
			consumePower(2.5f);
		}};
		slagExtractor = new SolidPump("slag-extractor") {{
			requirements(Category.production, with(Items.graphite, 60, Items.titanium, 35, Items.metaglass, 80, Items.silicon, 80, Items.thorium, 45));
			size = 3;
			liquidCapacity = 30;
			result = Liquids.slag;
			attribute = Attribute.heat;
			updateEffect = Fx.redgeneratespark;
			rotateSpeed = 8.6f;
			baseEfficiency = 0;
			pumpAmount = 0.9f;
			envRequired |= Env.none;
			consumePower(3.5f);
		}
			public TextureRegion rotatorRegion1;
		{
			buildType = () -> new SolidPumpBuild() {
				@Override
				public void draw() {
					Draw.rect(bottomRegion, x, y);
					Draw.z(Layer.blockCracks);
					super.drawCracks();
					Draw.z(Layer.blockAfterCracks);

					Drawf.liquid(liquidRegion, x, y, liquids.get(result) / liquidCapacity, result.color);
					Drawf.spinSprite(rotatorRegion, x, y, pumpTime * rotateSpeed);
					Drawf.spinSprite(rotatorRegion1, x, y, pumpTime * -rotateSpeed / 3);
					Draw.rect(topRegion, x, y);
				}
			};
		}
			@Override
			public void load() {
				super.load();
				rotatorRegion1 = Core.atlas.find(name + "-rotator1");
			}

			@Override
			public TextureRegion[] icons() {
				return new TextureRegion[]{region};
			}
		};
		oilRig = new Fracker("oil-rig") {{
			requirements(Category.production, with(Items.lead, 220, Items.graphite, 200, Items.silicon, 100, Items.thorium, 180, Items.plastanium, 120, Items.phaseFabric, 30));
			size = 4;
			itemCapacity = 20;
			liquidCapacity = 100f;
			result = Liquids.oil;
			attribute = Attribute.oil;
			updateEffect = Fx.pulverize;
			updateEffectChance = 0.05f;
			baseEfficiency = 0f;
			itemUseTime = 30f;
			pumpAmount = 1.5f;
			consumePower(8f);
			consumeItem(Items.sand);
			consumeLiquid(Liquids.water, 0.3f);
			buildCostMultiplier = 0.8f;
		}};
		beamDrill = new LaserBeamDrill("beam-drill") {{
			requirements(Category.production, with(Items.lead, 160, Items.silicon, 120, Itemsf.chromium, 60, Itemsf.nanoCore, 35, Items.phaseFabric, 25));
			size = 4;
			health = 960;
			tier = 11;
			itemCapacity = 50;
			hardnessDrillMultiplier = 20f;
			buildCostMultiplier = 0.8f;
			consumePower(6f);
			consumeLiquid(Liquids.water, 0.1f).optional(true, true);
		}
			@Override
			public TextureRegion[] icons() {
				return teamRegion.found() ? new TextureRegion[]{region, teamRegions[Team.sharded.id]} : new TextureRegion[]{region};
			}
		};
		//drill-erekir
		heavyPlasmaBore = new BeamDrill("heavy-plasma-bore") {{
			requirements(Category.production, with(Items.silicon, 300, Items.oxide, 150, Items.beryllium, 350, Items.tungsten, 250, Items.carbide, 100));
			itemCapacity = 30;
			liquidCapacity = 20f;
			health = 3220;
			size = 4;
			range = 7;
			tier = 6;
			fogRadius = 5;
			drillTime = 63f;
			drillMultipliers.put(Items.beryllium, 1.5f);
			drillMultipliers.put(Items.graphite, 1.5f);
			consumePower(390f / 60f);
			consumeLiquid(Liquids.hydrogen, 1.5f / 60f);
			consumeLiquid(Liquids.nitrogen, 7.5f / 60f).boost();
		}};
		minerPoint = new MinerPoint("miner-point") {{
			requirements(Category.production, with(Items.beryllium, 120, Items.graphite, 120, Items.silicon, 85, Items.tungsten, 50));
			blockedItem.add(Items.thorium);
			droneConstructTime = 60 * 10f;
			tier = 5;
			consumePower(2f);
			consumeLiquid(Liquids.hydrogen, 6 / 60f);
			squareSprite = false;
		}};
		minerCenter = new MinerPoint("miner-center") {{
			requirements(Category.production, with(Items.beryllium, 480, Items.tungsten, 360, Items.oxide, 125, Items.carbide, 120, Items.surgeAlloy, 130));
			range = 18;
			dronesCreated = 6;
			droneConstructTime = 60 * 7f;
			tier = 7;
			size = 4;
			itemCapacity = 300;
			MinerUnit = UnitTypesf.largeMiner;
			consumePower(3);
			consumeLiquid(Liquids.nitrogen, 9 / 60f);
			buildCostMultiplier = 0.8f;
			squareSprite = false;
		}};
		//distribution
		invertedJunction = new InvertedJunction("inverted-junction") {{
			requirements(Category.distribution, with(Items.copper, 2));
			placeSprite = "junction";
			sync = true;
			speed = 26f;
			capacity = 6;
			configurable = true;
			buildCostMultiplier = 6f;
		}};
		itemLiquidJunction = new MultiJunction("item-liquid-junction") {{
			requirements(Category.distribution, with(Items.copper, 4, Items.graphite, 6, Items.metaglass, 10));
		}};
		multiSorter = new MultiSorter("multi-sorter") {{
			requirements(Category.distribution, with(Items.lead, 5, Items.copper, 5, Items.silicon, 5));
		}};
		plastaniumRouter = new StackRouter("plastanium-router") {{
			requirements(Category.distribution, with(Items.plastanium, 5, Items.silicon, 5, Items.graphite, 5));
			health = 90;
			speed = 8f;
		}};
		plastaniumBridge = new StackBridge("plastanium-bridge") {{
			requirements(Category.distribution, with(Items.lead, 15, Items.silicon, 12, Items.titanium, 15, Items.plastanium, 10));
			health = 90;
			itemCapacity = 10;
			range = 6;
			bridgeWidth = 8f;
		}};
		stackHelper = new StackHelper("stack-helper") {{
			requirements(Category.distribution, with(Items.silicon, 20, Items.phaseFabric, 10, Items.plastanium, 20));
			health = 90;
		}};
		chromiumEfficientConveyor = new BeltConveyor("chromium-efficient-conveyor") {{
			requirements(Category.distribution, with(Items.lead, 1, Itemsf.chromium, 1));
			health = 240;
			armor = 3f;
			speed = 0.18f;
			displayedSpeed = 18;
		}};
		chromiumArmorConveyor = new BeltConveyor("chromium-armor-conveyor") {{
			requirements(Category.distribution, with(Items.metaglass, 1, Items.thorium, 1, Items.plastanium, 1, Itemsf.chromium, 1));
			health = 560;
			armor = 5f;
			speed = 0.18f;
			displayedSpeed = 18;
			noSideBlend = true;
		}};
		chromiumTubeConveyor = new TubeConveyor("chromium-tube-conveyor") {{
			requirements(Category.distribution, with(Items.metaglass, 2, Items.thorium, 1, Items.plastanium, 1, Itemsf.chromium, 2));
			health = 670;
			armor = 5f;
			speed = 0.18f;
			displayedSpeed = 18;
			noSideBlend = true;
			placeableLiquid = true;
			displayFlow = true;
			hideDetails = false;
		}};
		chromiumTubeDistributor = new TubeDistributor("chromium-tube-distributor") {{
			requirements(Category.distribution, with(Items.copper, 1, Items.metaglass, 1, Itemsf.chromium, 1));
			health = 450;
			armor = 4f;
			speed = 0.18f;
			placeableLiquid = true;
			displayFlow = true;
		}};
		chromiumStackConveyor = new BeltStackConveyor("chromium-stack-conveyor") {{
			requirements(Category.distribution, with(Items.graphite, 1, Items.silicon, 1, Items.plastanium, 1, Itemsf.chromium, 1));
			health = 380;
			armor = 4f;
			speed = 0.125f;
			itemCapacity = 20;
			outputRouter = false;
		}};
		chromiumStackRouter = new StackRouter("chromium-stack-router") {{
			requirements(Category.distribution, with(Items.graphite, 4, Items.silicon, 5, Items.plastanium, 3, Itemsf.chromium, 1));
			health = 380;
			armor = 4f;
			speed = 0.125f;
			itemCapacity = 20;
			buildCostMultiplier = 0.8f;
		}};
		chromiumStackBridge = new StackBridge("chromium-stack-bridge") {{
			requirements(Category.distribution, with(Items.lead, 15, Items.silicon, 12, Items.plastanium, 10, Itemsf.chromium, 10));
			health = 420;
			armor = 4f;
			itemCapacity = 20;
			range = 8;
			bridgeWidth = 8f;
			buildCostMultiplier = 0.6f;
		}};
		chromiumRouter = new MultiRouter("chromium-router") {{
			requirements(Category.distribution, with(Items.copper, 3, Itemsf.chromium, 2));
			health = 420;
			armor = 4f;
			speed = 2;
			itemCapacity = 20;
			liquidCapacity = 64f;
			underBullets = true;
			solid = false;
		}};
		chromiumJunction = new MultiJunction("chromium-junction") {{
			requirements(Category.distribution, with(Items.copper, 2, Itemsf.chromium, 2));
			health = 420;
			armor = 4f;
			speed = 12;
			capacity = itemCapacity = 12;
			((BeltConveyor) chromiumEfficientConveyor).junctionReplacement = this;
			((BeltConveyor) chromiumArmorConveyor).junctionReplacement = this;
			((TubeConveyor) chromiumTubeConveyor).junctionReplacement = this;
		}};
		chromiumItemBridge = new ItemBridge("chromium-item-bridge") {{
			requirements(Category.distribution, with(Items.graphite, 6, Items.silicon, 8, Items.plastanium, 4, Itemsf.chromium, 3));
			health = 420;
			armor = 4f;
			hasPower = false;
			transportTime = 3f;
			range = 8;
			arrowSpacing = 6;
			bridgeWidth = 8;
			buildCostMultiplier = 0.8f;
			((BeltConveyor) chromiumEfficientConveyor).bridgeReplacement = this;
			((BeltConveyor) chromiumArmorConveyor).bridgeReplacement = this;
			((TubeConveyor) chromiumTubeConveyor).bridgeReplacement = this;
		}};
		phaseItemNode = new NodeBridge("phase-item-node") {{
			requirements(Category.distribution, with(Items.lead, 30, Itemsf.chromium, 10, Items.silicon, 15, Items.phaseFabric, 10));
			size = 1;
			health = 320;
			armor = 3f;
			squareSprite = false;
			range = 25;
			envEnabled |= Env.space;
			transportTime = 1f;
			consumePower(0.5f);
		}};
		rapidDirectionalUnloader = new DirectionalUnloaderf("rapid-directional-unloader") {{
			requirements(Category.distribution, with(Items.silicon, 40, Items.plastanium, 25, Itemsf.chromium, 15, Items.phaseFabric, 5));
			speed = 1f;
			squareSprite = false;
			underBullets = true;
			allowCoreUnload = true;
		}};
		//distribution-erekir
		ductJunction = new DuctJunction("duct-junction") {{
			requirements(Category.distribution, with(Items.beryllium, 5));
			health = 75;
			speed = 4f;
		}};
		ductDistributor = new CoveredRouter("duct-distributor") {{
			requirements(Category.distribution, with(Items.beryllium, 20));
			size = 2;
			speed = 2f;
			solid = false;
			squareSprite = false;
		}};
		ductMultiSorter = new MultiSorter("duct-multi-sorter") {{
			requirements(Category.distribution, with(Items.beryllium, 5, Items.silicon, 5));
			health = 90;
		}};
		armoredDuctBridge = new DuctBridge("armored-duct-bridge") {{
			requirements(Category.distribution, with(Items.beryllium, 20, Items.tungsten, 10));
			health = 140;
			range = 6;
			speed = 4;
			buildCostMultiplier = 2;
			((Duct) Blocks.armoredDuct).bridgeReplacement = this;
		}};
		rapidDuctUnloader = new DirectionalUnloaderf("rapid-duct-unloader") {{
			requirements(Category.distribution, with(Items.graphite, 25, Items.silicon, 30, Items.tungsten, 20, Items.oxide, 15));
			health = 240;
			speed = 2f;
			solid = false;
			underBullets = true;
			squareSprite = false;
			regionRotated1 = 1;
		}};
		//liquid
		liquidSorter = new SortLiquidRouter("liquid-sorter") {{
			requirements(Category.liquid, with(Items.graphite, 4, Items.metaglass, 2));
			liquidCapacity = 120f;
			liquidPadding = 3f / 4f;
			underBullets = true;
			rotate = false;
			solid = false;
		}};
		liquidValve = new SortLiquidRouter("liquid-valve") {{
			requirements(Category.liquid, with(Items.graphite, 4, Items.metaglass, 2));
			liquidCapacity = 120f;
			liquidPadding = 3f / 4f;
			underBullets = true;
			configurable = false;
			solid = false;
		}};
		liquidOverflowValve = new LiquidOverflowValve("liquid-overflow-valve") {{
			requirements(Category.liquid, with(Items.graphite, 6, Items.metaglass, 10));
			solid = false;
			underBullets = true;
		}};
		liquidUnderflowValve = new LiquidOverflowValve("liquid-underflow-valve") {{
			requirements(Category.liquid, with(Items.graphite, 6, Items.metaglass, 10));
			solid = false;
			underBullets = true;
			invert = true;
		}};
		liquidUnloader = new LiquidUnloader("liquid-unloader") {{
			requirements(Category.liquid, with(Items.titanium, 15, Items.metaglass, 10, Items.silicon, 15));
			health = 70;
			hideDetails = false;
			liquidCapacity = 60f;
		}};
		chromiumArmorConduit = new BeltConduit("chromium-armor-conduit") {{
			requirements(Category.liquid, with(Items.metaglass, 2, Itemsf.chromium, 2));
			health = 420;
			armor = 4f;
			liquidCapacity = 80f;
			liquidPressure = 3.2f;
			noSideBlend = true;
			leaks = false;
			fireproof = true;
			buildType = () -> new BeltConduitBuild() {
				@Override
				public float moveLiquid(Building next, Liquid liquid) {
					if (next == null) return 0f;

					next = next.getLiquidDestination(this, liquid);

					if (next.team == team && next.block.hasLiquids && liquids.get(liquid) > 0f) {
						float ofract = next.liquids.get(liquid) / next.block.liquidCapacity;
						float fract = liquids.get(liquid) / block.liquidCapacity * block.liquidPressure;
						float flow = Math.min(Mathf.clamp(fract - ofract) * block.liquidCapacity, liquids.get(liquid));
						flow = Math.min(flow, next.block.liquidCapacity - next.liquids.get(liquid));

						if (flow > 0f && ofract <= fract && next.acceptLiquid(this, liquid)) {
							next.handleLiquid(this, liquid, flow);
							liquids.remove(liquid, flow);
							return flow;
						} else if (next.liquids.currentAmount() / next.block.liquidCapacity > 0.1f && fract > 0.1f) {
							float fx = (x + next.x) / 2f, fy = (y + next.y) / 2f;

							Liquid other = next.liquids.current();
							// There were flammability logics, removed
							if ((liquid.temperature > 0.7f && other.temperature < 0.55f) || (other.temperature > 0.7f && liquid.temperature < 0.55f)) {
								liquids.remove(liquid, Math.min(liquids.get(liquid), 0.7f * Time.delta));
								if (Mathf.chance(0.2f * Time.delta)) {
									Fx.steam.at(fx, fy);
								}
							}
						}
					}
					return 0f;
				}
			};
		}};
		chromiumLiquidBridge = new LiquidBridge("chromium-liquid-bridge") {{
			requirements(Category.liquid, with(Items.metaglass, 10, Itemsf.chromium, 6));
			health = 480;
			armor = 5f;
			hasPower = false;
			range = 8;
			liquidCapacity = 200f;
			arrowSpacing = 6;
			bridgeWidth = 8f;
			((BeltConduit) chromiumArmorConduit).bridgeReplacement = this;
		}};
		chromiumArmorLiquidContainer = new LiquidRouter("chromium-armor-liquid-container") {{
			requirements(Category.liquid, with(Items.metaglass, 15, Itemsf.chromium, 6));
			size = 2;
			health = 950;
			armor = 8f;
			liquidCapacity = 1200f;
			underBullets = true;
			buildCostMultiplier = 0.8f;
		}};
		chromiumArmorLiquidTank = new LiquidRouter("chromium-armor-liquid-tank") {{
			requirements(Category.liquid, with(Items.metaglass, 40, Itemsf.chromium, 16));
			size = 3;
			health = 2500;
			armor = 8f;
			liquidCapacity = 3200f;
			underBullets = true;
			buildCostMultiplier = 0.8f;
		}};
		liquidMassDriver = new LiquidMassDriver("liquid-mass-driver") {{
			requirements(Category.liquid, with(Items.metaglass, 85, Items.silicon, 80, Items.titanium, 80, Items.thorium, 55));
			size = 3;
			liquidCapacity = 700f;
			range = 440f;
			reload = 150f;
			knockback = 3;
			hasPower = true;
			consumePower(1.8f);
		}};
		turboPump = new Pump("turbo-pump") {{
			requirements(Category.liquid, with(Items.titanium, 40, Items.thorium, 50, Items.metaglass, 80, Items.silicon, 60, Itemsf.chromium, 30));
			size = 2;
			consumePower(1.75f);
			pumpAmount = 0.8f;
			liquidCapacity = 200f;
			squareSprite = false;
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawPumpLiquid(), new DrawDefault());
			buildCostMultiplier = 0.8f;
		}};
		phaseLiquidNode = new NodeBridge("phase-liquid-node") {{
			requirements(Category.liquid, with(Items.metaglass, 20, Itemsf.chromium, 10, Items.silicon, 15, Items.phaseFabric, 10));
			size = 1;
			health = 320;
			armor = 3f;
			squareSprite = false;
			hasItems = false;
			hasLiquids = true;
			liquidCapacity = 200f;
			canOverdrive = false;
			outputsLiquid = true;
			range = 25;
			consumePower(0.5f);
		}};
		//liquid-erekir
		reinforcedLiquidOverflowValve = new LiquidOverflowValve("reinforced-liquid-overflow-valve") {{
			requirements(Category.liquid, with(Items.graphite, 6, Items.beryllium, 10));
			buildCostMultiplier = 3f;
			health = 260;
			researchCostMultiplier = 1;
			solid = false;
			underBullets = true;
		}};
		reinforcedLiquidUnderflowValve = new LiquidOverflowValve("reinforced-liquid-underflow-valve") {{
			requirements(Category.liquid, with(Items.graphite, 6, Items.beryllium, 10));
			buildCostMultiplier = 3f;
			health = 260;
			researchCostMultiplier = 1;
			invert = true;
			solid = false;
			underBullets = true;
		}};
		reinforcedLiquidUnloader = new LiquidDirectionalUnloader("reinforced-liquid-unloader") {{
			requirements(Category.liquid, with(Items.tungsten, 10, Items.beryllium, 15));
			buildCostMultiplier = 3f;
			health = 570;
			researchCostMultiplier = 1;
			solid = false;
			underBullets = true;
			liquidCapacity = 150f;
		}};
		reinforcedLiquidSorter = new SortLiquidRouter("reinforced-liquid-sorter") {{
			requirements(Category.liquid, with(Items.silicon, 8, Items.beryllium, 4));
			liquidCapacity = 400f;
			liquidPadding = 3f / 4f;
			researchCostMultiplier = 3;
			underBullets = true;
			rotate = false;
			solid = false;
			squareSprite = false;
		}};
		reinforcedLiquidValve = new SortLiquidRouter("reinforced-liquid-valve") {{
			requirements(Category.liquid, with(Items.graphite, 6, Items.beryllium, 6));
			liquidCapacity = 150f;
			liquidPadding = 3f / 4f;
			researchCostMultiplier = 3;
			underBullets = true;
			configurable = false;
			solid = false;
			squareSprite = false;
		}};
		smallReinforcedPump = new Pump("small-reinforced-pump") {{
			requirements(Category.liquid, with(Items.graphite, 35, Items.beryllium, 40));
			pumpAmount = 15f / 60f;
			liquidCapacity = 150f;
			size = 1;
			squareSprite = false;
		}};
		largeReinforcedPump = new Pump("large-reinforced-pump") {{
			requirements(Category.liquid, with(Items.beryllium, 105, Items.thorium, 65, Items.silicon, 50, Items.tungsten, 75));
			consumeLiquid(Liquids.hydrogen, 3f / 60f);
			pumpAmount = 240f / 60f / 9f;
			liquidCapacity = 360f;
			size = 3;
			squareSprite = false;
		}};
		//power
		smartPowerNode = new SmartPowerNode("smart-power-node") {{ //Copy stats from normal power node
			requirements(Category.power, with(Items.copper, 2, Items.lead, 5, Items.silicon, 1));
			maxNodes = 10;
			laserRange = 6;
		}};
		powerNodeHuge = new PowerNode("power-node-huge") {{
			requirements(Category.power, with(Items.lead, 45, Items.titanium, 35, Items.thorium, 15, Items.silicon, 25));
			size = 3;
			health = 370;
			maxNodes = 25;
			laserRange = 22.5f;
		}};
		powerNodePhase = new PowerNode("power-node-phase") {{
			requirements(Category.power, ItemStack.with(Items.plastanium, 5, Itemsf.heavyAlloy, 15, Items.phaseFabric, 10));
			size = 1;
			health = 2200;
			armor = 30f;
			absorbLasers = true;
			maxNodes = 20;
			laserRange = 30f;
			timers++;
			update = true;
			buildType = () -> new PowerNodeBuild() {
				@Override
				public void updateTile() {
					if (damaged() && power.graph.getSatisfaction() > 0.5f) {
						if (timer.get(90f)) {
							Fx.healBlockFull.at(x, y, 0, Pal.powerLight, block);
							healFract(5 * power.graph.getSatisfaction());
						}
					}
				}
			};
		}};
		powerAnalyzer = new PowerAnalyzer("power-analyzer") {{
			requirements(Category.power, with(Items.lead, 60, Items.silicon, 20, Items.metaglass, 10));
			size = 2;
			displayThickness = 9f / 4f;
			displaySpacing = 18f / 4f;
			displayLength = 24f / 4f;
			hideDetails = false;
		}};
		lunarPanel = new LunarGenerator("lunar-panel") {{
			requirements(Category.power, with(Items.silicon, 15, Itemsf.originium, 10, Items.lead, 15));
			health = 45 * size * size;
			size = 2;
			powerProduction = 0.4f;
		}};
		uraniumReactor = new NuclearReactor("uranium-reactor") {{
			requirements(Category.power, with(Items.lead, 400, Items.metaglass, 120, Items.graphite, 350, Items.silicon, 300, Itemsf.uranium, 100));
			size = 3;
			health = 1450;
			armor = 8f;
			outputsPower = true;
			powerProduction = 58f;
			itemDuration = 360;
			itemCapacity = 30;
			liquidCapacity = 100;
			fuelItem = Itemsf.uranium;
			coolantPower = 0.45f;
			heating = 0.06f;
			lightColor = Color.white;
			explosionShake = 9;
			explosionShakeDuration = 120;
			explosionRadius = 35;
			explosionDamage = 7200;
			explodeSound = Soundsf.dbz1;
			explodeEffect = new Effect(30, 500f, b -> {
				float intensity = 8f;
				float baseLifetime = 25f + intensity * 15f;
				b.lifetime = 50f + intensity * 64f;

				Draw.color(Palf.uraniumGrey);
				Draw.alpha(0.8f);
				for (int i = 0; i < 5; i++) {
					Fx.rand.setSeed(b.id * 2l + i);
					float lenScl = Fx.rand.random(0.25f, 1f);
					int j = i;
					b.scaled(b.lifetime * lenScl, e -> Angles.randLenVectors(e.id + j - 1, e.fin(Interp.pow10Out), (int) (2.8f * intensity), 25f * intensity, (x, y, in, out) -> {
						float fout = e.fout(Interp.pow5Out) * Fx.rand.random(0.5f, 1f);
						float rad = fout * ((2f + intensity) * 2.35f);

						Fill.circle(e.x + x, e.y + y, rad);
						Drawf.light(e.x + x, e.y + y, rad * 2.6f, Palf.uraniumGrey, 0.7f);
					}));
				}

				b.scaled(baseLifetime, e -> {
					Draw.color();
					e.scaled(5 + intensity * 2f, i -> {
						Lines.stroke((3.1f + intensity / 5f) * i.fout());
						Lines.circle(e.x, e.y, (3f + i.fin() * 14f) * intensity);
						Drawf.light(e.x, e.y, i.fin() * 14f * 2f * intensity, Color.white, 0.9f * e.fout());
					});

					Draw.color(Color.white, Palf.uraniumGrey, e.fin());
					Lines.stroke((2f * e.fout()));

					Draw.z(Layer.effect + 0.001f);
					Angles.randLenVectors(e.id + 1, e.finpow() + 0.001f, (int) (8 * intensity), 30f * intensity, (x, y, in, out) -> {
						Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + out * 4 * (4f + intensity));
						Drawf.light(e.x + x, e.y + y, (out * 4 * (3f + intensity)) * 3.5f, Draw.getColor(), 0.8f);
					});
				});
			});
			ambientSound = Sounds.hum;
			ambientSoundVolume = 0.24f;
			consumeItem(Itemsf.uranium);
			consumeLiquid(Liquids.cryofluid, heating / coolantPower).update(false);
		}
			public final Blending blending = Blending.additive;
			public final float alpha = 0.9f, glowScale = 10f, glowIntensity = 0.5f, layer = Layer.blockAdditive;
			public final Color color = Color.red.cpy();
			public TextureRegion bottomRegion, glowRegion;
		{
			buildType = () -> new NuclearReactorBuild() {
				@Override
				public void draw() {
					Draw.rect(bottomRegion, x, y);

					Draw.color(coolColor, hotColor, heat);
					Fill.rect(x, y, size * tilesize, size * tilesize);

					Draw.color(liquids.current().color);
					Draw.alpha(liquids.currentAmount() / liquidCapacity);
					Draw.rect(topRegion, x, y);
					Draw.reset();

					Draw.rect(region, x, y);

					drawGlow();

					if (heat > flashThreshold) {
						flash += (1f + ((heat - flashThreshold) / (1f - flashThreshold)) * 5.4f) * Time.delta;
						Draw.color(Color.red, Color.yellow, Mathf.absin(flash, 9f, 1f));
						Draw.alpha(0.3f);
						Draw.rect(lightsRegion, x, y);
					}

					Draw.reset();
				}

				public void drawGlow() {
					if (warmup() <= 0.001f) return;

					float z = Draw.z();
					Draw.z(layer);
					Draw.blend(blending);
					Draw.color(color);
					Draw.alpha((Mathf.absin(totalProgress(), glowScale, alpha) * glowIntensity + 1f - glowIntensity) * warmup() * alpha);
					Draw.rect(glowRegion, x, y, 0f);
					Draw.reset();
					Draw.blend();
					Draw.z(z);
				}
			};
		}
			@Override
			public void load() {
				super.load();
				bottomRegion = Core.atlas.find(name + "-bottom");
				glowRegion = Core.atlas.find(name + "-glow");
			}

			@Override
			public TextureRegion[] icons() {
				return new TextureRegion[]{bottomRegion, region};
			}
		};
		hyperMagneticReactor = new HyperGenerator("hyper-magnetic-reactor") {{
			requirements(Category.power, with(Items.titanium, 1200, Items.metaglass, 1300, Items.plastanium, 800, Items.silicon, 1600, Items.phaseFabric, 1200, Itemsf.chromium, 2500, Itemsf.heavyAlloy, 2200));
			size = 6;
			health = 16500;
			armor = 22f;
			powerProduction = 2200f;
			updateLightning = updateLightningRand = 3;
			itemCapacity = 30;
			itemDuration = 180f;
			liquidCapacity = 360f;
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawPlasma() {{
				plasma1 = plasma2 = Pal.techBlue;
			}}, new DrawDefault());
			ambientSound = Sounds.pulse;
			ambientSoundVolume = 0.1f;
			consumePower(65f);
			consumeItem(Items.phaseFabric, 1);
			consumeLiquid(Liquidsf.nanoFluid, 0.25f);
			buildCostMultiplier = 0.6f;
		}};
		hugeBattery = new Battery("huge-battery") {{
			requirements(Category.power, with(Itemsf.purifiedOriginium, 110, Items.graphite, 125, Items.phaseFabric, 80, Items.thorium, 110, Items.plastanium, 100));
			size = 5;
			health = 1600;
			consumePowerBuffered(750000f);
		}};
		armoredCoatedBattery = new Battery("armored-coated-battery") {{
			requirements(Category.power, with(Itemsf.purifiedOriginium, 70, Items.silicon, 180, Items.plastanium, 120, Itemsf.chromium, 100, Items.phaseFabric, 50));
			size = 4;
			health = 8400;
			armor = 28f;
			drawer = new DrawMulti(new DrawDefault(), new DrawPower() {{
				emptyLightColor = Color.valueOf("18473f");
				fullLightColor = Color.valueOf("ffd197");
			}}, new DrawRegion("-top"));
			consumePowerBuffered(425000f);
		}};
		//power-erekir
		smartBeamNode = new SmartBeamNode("smart-beam-node") {{ //Copy stats from normal beam node
			requirements(Category.power, with(Items.beryllium, 10, Items.silicon, 2));
			consumesPower = outputsPower = true;
			health = 90;
			range = 10;
			fogRadius = 1;
			consumePowerBuffered(1000f);
		}};
		beamDiode = new BeamDiode("beam-diode") {{
			requirements(Category.power, with(Items.beryllium, 10, Items.silicon, 10, Items.oxide, 5));
			health = 90;
			range = 10;
			fogRadius = 1;
		}};
		beamInsulator = new InsulationWall("beam-insulator") {{
			requirements(Category.power, with(Items.silicon, 10, Items.oxide, 5));
			health = 90;
		}};
		reinforcedPowerAnalyzer = new PowerAnalyzer("reinforced-power-analyzer") {{
			requirements(Category.power, with(Items.beryllium, 25, Items.silicon, 15));
			size = 2;
			displayThickness = 9f / 4f;
			displaySpacing = 18f / 4f;
			displayLength = 24f / 4f;
			horizontal = true;
			squareSprite = false;
		}};
		liquidConsumeGenerator = new ConsumeGenerator("liquid-generator") {{
			requirements(Category.power, with(Items.beryllium, 180, Items.graphite, 120, Items.silicon, 115, Items.tungsten, 80, Items.oxide, 120));
			size = 3;
			hasLiquids = true;
			powerProduction = 660f / 60f;
			drawer = new DrawMulti(new DrawDefault(), new DrawWarmupRegion() {{
				sinMag = 0;
				sinScl = 1;
			}}, new DrawLiquidRegion());
			generateEffect = new RadialEffect(new Effect(160f, e -> {
				Draw.color(Color.valueOf("6e685a"));
				Draw.alpha(0.6f);
				Rand rand = Fx.rand;
				Vec2 v = Fx.v;
				rand.setSeed(e.id);
				for (int i = 0; i < 3; i++) {
					float len = rand.random(6f), rot = rand.range(40f) + e.rotation;
					e.scaled(e.lifetime * rand.random(0.3f, 1f), b -> {
						v.trns(rot, len * b.finpow());
						Fill.circle(e.x + v.x, e.y + v.y, 2f * b.fslope() + 0.2f);
					});
				}
			}), 4, 90, 8f);
			effectChance = 0.2f;
			consume(new ConsumeLiquidFlammable(0.4f, 0.2f));
			squareSprite = false;
		}};
		//production
		largeKiln = new GenericCrafter("large-kiln") {{
			requirements(Category.crafting, with(Items.graphite, 60, Items.silicon, 35, Items.titanium, 50, Items.thorium, 40));
			craftEffect = Fx.smeltsmoke;
			outputItem = new ItemStack(Items.metaglass, 4);
			craftTime = 40f;
			size = 3;
			hasPower = hasItems = true;
			drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("ffc099")));
			ambientSound = Sounds.smelter;
			ambientSoundVolume = 0.14f;
			consumeItems(with(Items.lead, 3, Items.sand, 3));
			consumePower(1.8f);
		}};
		largePulverizer = new GenericCrafter("large-pulverizer") {{
			requirements(Category.crafting, with(Items.copper, 25, Items.lead, 25, Items.graphite, 15, Items.titanium, 10));
			size = 2;
			health = 160;
			itemCapacity = 20;
			craftTime = 35f;
			updateEffect = Fx.pulverizeSmall;
			craftEffect = Fxf.hugeSmokeGray;
			outputItem = new ItemStack(Items.sand, 3);
			updateEffect = new Effect(80f, e -> {
				Fx.rand.setSeed(e.id);
				Draw.color(Color.lightGray, Color.gray, e.fin());
				Angles.randLenVectors(e.id, 4, 2f + 12f * e.fin(Interp.pow3Out), (x, y) ->
						Fill.circle(e.x + x, e.y + y, e.fout() * Fx.rand.random(1, 2.5f))
				);
			}).layer(Layer.blockOver + 1);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawRegion("-rotate", 3f, true), /*new DrawFrames(), new DrawArcSmelt(), */new DrawDefault());
			ambientSound = Sounds.grinding;
			ambientSoundVolume = 0.12f;
			consumePower(1.5f);
			consumeItem(Items.scrap, 2);
			hideDetails = false;
		}};
		largeMelter = new GenericCrafter("large-melter") {{
			requirements(Category.crafting, with(Items.lead, 60, Items.graphite, 45, Items.silicon, 30, Items.titanium, 20));
			size = 2;
			hasLiquids = true;
			itemCapacity = 20;
			liquidCapacity = 30;
			craftTime = 12;
			outputLiquid = new LiquidStack(Liquids.slag, 36f / 60f);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.slag), new DrawDefault());
			consumePower(1.5f);
			consumeItem(Items.scrap, 2);
			hideDetails = false;
		}};
		largeCryofluidMixer = new GenericCrafter("large-cryofluid-mixer") {{
			requirements(Category.crafting, with(Items.lead, 120, Items.silicon, 60, Items.titanium, 150, Items.thorium, 110));
			outputLiquid = new LiquidStack(Liquids.cryofluid, 36f / 60f);
			size = 3;
			hasLiquids = true;
			rotate = false;
			solid = true;
			outputsLiquid = true;
			envEnabled = Env.any;
			itemCapacity = 20;
			liquidCapacity = 60f;
			craftTime = 50;
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.water), new DrawLiquidTile(Liquids.cryofluid), new DrawDefault());
			lightLiquid = Liquids.cryofluid;
			consumePower(2f);
			consumeItem(Items.titanium);
			consumeLiquid(Liquids.water, 36f / 60f);
			hideDetails = false;
		}};
		largePyratiteMixer = new GenericCrafter("large-pyratite-mixer") {{
			requirements(Category.crafting, with(Items.copper, 100, Items.lead, 50, Items.titanium, 25, Items.silicon, 20));
			outputItem = new ItemStack(Items.pyratite, 3);
			envEnabled |= Env.space;
			size = 3;
			itemCapacity = 20;
			consumePower(0.5f);
			consumeItems(with(Items.coal, 3, Items.lead, 4, Items.sand, 5));
			hideDetails = false;
		}};
		largeBlastMixer = new GenericCrafter("large-blast-mixer") {{
			requirements(Category.crafting, with(Items.lead, 60, Items.titanium, 40, Items.silicon, 20));
			outputItem = new ItemStack(Items.blastCompound, 3);
			size = 3;
			itemCapacity = 20;
			envEnabled |= Env.space;
			consumeItems(with(Items.pyratite, 3, Items.sporePod, 3));
			consumePower(1f);
			hideDetails = false;
		}};
		largeCultivator = new AttributeCrafter("large-cultivator") {{
			requirements(Category.production, with(Items.lead, 40, Items.titanium, 30, Items.plastanium, 20, Items.silicon, 30, Items.metaglass, 60));
			outputItem = new ItemStack(Items.sporePod, 1);
			craftTime = 20;
			health = 360;
			size = 3;
			hasLiquids = true;
			hasPower = true;
			hasItems = true;
			itemCapacity = 20;
			liquidCapacity = 60f;
			craftEffect = Fx.none;
			envRequired |= Env.spores;
			attribute = Attribute.spores;
			legacyReadWarmup = true;
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.water), new DrawCultivator(), new DrawDefault());
			maxBoost = 3f;
			consumePower(3f);
			consumeLiquid(Liquids.water, 36f / 60f);
		}};
		sporeFarm = new SporeFarm("spore-farm") {{
			requirements(Category.production, with(Items.copper, 5, Items.lead, 5));
			health = 50;
			rebuildable = false;
			hasItems = true;
			itemCapacity = 3;
			envRequired |= Env.spores;
			breakSound = Sounds.splash;
			buildCostMultiplier = 0.1f;
		}};
		largePlastaniumCompressor = new GenericCrafter("large-plastanium-compressor") {{
			requirements(Category.crafting, with(Items.silicon, 150, Items.lead, 220, Items.graphite, 120, Items.titanium, 150, Items.thorium, 100));
			hasLiquids = true;
			itemCapacity = 20;
			liquidCapacity = 80f;
			craftTime = 60f;
			outputItem = new ItemStack(Items.plastanium, 3);
			size = 3;
			health = 640;
			craftEffect = Fx.formsmoke;
			updateEffect = Fx.plasticburn;
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.oil), new DrawPistons() {{
				sinMag = 1;
			}}, new DrawDefault(), new DrawFade());
			consumeLiquid(Liquids.oil, 0.5f);
			consumeItem(Items.titanium, 5);
			consumePower(7f);
		}};
		largeSurgeSmelter = new GenericCrafter("large-surge-smelter") {{
			requirements(Category.crafting, with(Items.lead, 180, Items.silicon, 100, Items.metaglass, 60, Items.thorium, 150, Items.surgeAlloy, 30));
			size = 4;
			itemCapacity = 30;
			craftTime = 90f;
			craftEffect = Fx.smeltsmoke;
			outputItem = new ItemStack(Items.surgeAlloy, 3);
			drawer = new DrawMulti(new DrawDefault(), new DrawPowerLight(Color.valueOf("f3e979")), new DrawFlame(Color.valueOf("ffef99")));
			consumePower(6);
			consumeItems(with(Items.copper, 5, Items.lead, 6, Items.titanium, 5, Items.silicon, 4));
		}};
		blastSiliconSmelter = new GenericCrafter("blast-silicon-smelter") {{
			requirements(Category.crafting, with(Items.graphite, 90, Items.thorium, 70, Items.silicon, 80, Items.plastanium, 50, Items.surgeAlloy, 30));
			health = 660;
			size = 4;
			itemCapacity = 50;
			craftTime = 35f;
			outputItem = new ItemStack(Items.silicon, 10);
			craftEffect = new RadialEffect(Fx.surgeCruciSmoke, 9, 45f, 6f);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawDefault(), new DrawGlowRegion() {{
				alpha = 0.9f;
				glowScale = 3.14f;
				color = Color.valueOf("ff0000");
			}}, new DrawGlowRegion("-glow1") {{
				alpha = 0.9f;
				glowScale = 3.14f;
				color = Color.valueOf("eb564b");
			}});
			ambientSound = Sounds.smelter;
			ambientSoundVolume = 0.21f;
			consumeItems(with(Items.coal, 5, Items.sand, 8, Items.blastCompound, 1));
			consumePower(4f);
		}};
		nanoCoreConstructor = new GenericCrafter("nano-core-constructor") {{
			requirements(Category.crafting, with(Items.copper, 120, Items.titanium, 45, Items.silicon, 35, Itemsf.purifiedOriginium, 20));
			size = 2;
			itemCapacity = 15;
			craftTime = 100f;
			outputItem = new ItemStack(Itemsf.nanoCore, 1);
			craftEffect = Fx.none;
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawSpecConstruct() {{
				constructColor1 = constructColor2 = Palf.nanoCoreRed;
			}}, new DrawDefault());
			consumePower(2.5f);
			consumeItems(with(Items.titanium, 2, Items.silicon, 3, Itemsf.purifiedOriginium, 1));
		}};
		nanoCorePrinter = new GenericCrafter("nano-core-printer") {{
			requirements(Category.crafting, with(Items.titanium, 600, Items.silicon, 400, Items.plastanium, 350, Items.surgeAlloy, 250, Itemsf.chromium, 200, Itemsf.nanoCore, 150, Itemsf.purifiedOriginium, 150));
			size = 4;
			health = 1500;
			squareSprite = false;
			hasLiquids = true;
			itemCapacity = 40;
			liquidCapacity = 60f;
			craftTime = 150f;
			outputItem = new ItemStack(Itemsf.nanoCore, 12);
			craftEffect = Fx.none;
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.cryofluid), new DrawRegion("-mid"), new DrawSpecConstruct() {{
				constructColor1 = constructColor2 = Palf.nanoCoreRed;
			}}, new DrawDefault());
			consumePower(25f);
			consumeLiquid(Liquids.cryofluid, 6f / 60f);
			consumeItems(with(Items.titanium, 6, Items.silicon, 9, Itemsf.purifiedOriginium, 3));
			hideDetails = false;
		}};
		nanoCoreActivator = new GenericCrafter("nano-core-activator") {{
			requirements(Category.crafting, with(Items.titanium, 90, Items.silicon, 80, Itemsf.nanoCore, 30, Items.plastanium, 60));
			size = 2;
			health = 360;
			hasLiquids = outputsLiquid = true;
			rotate = false;
			solid = true;
			itemCapacity = 15;
			liquidCapacity = 24f;
			craftTime = 100f;
			craftEffect = Fx.smeltsmoke;
			outputLiquid = new LiquidStack(Liquidsf.nanoFluid, 18f / 60f);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquidsf.nanoFluid), new DrawDefault(), new DrawRegion("-top"), new DrawFrames() {{
				frames = 5;
				sine = true;
			}});
			lightLiquid = Liquidsf.nanoFluid;
			consumePower(3f);
			consumeItem(Itemsf.nanoCore, 1);
		}};
		largePhaseWeaver = new GenericCrafter("large-phase-weaver") {{
			requirements(Category.crafting, with(Items.lead, 160, Items.thorium, 100, Items.silicon, 80, Items.plastanium, 50, Items.phaseFabric, 15));
			size = 3;
			itemCapacity = 40;
			craftTime = 60f;
			outputItem = new ItemStack(Items.phaseFabric, 3);
			updateEffect = Fxf.squareRand(Pal.accent, 5f, 13f);
			craftEffect = new Effect(25f, e -> {
				Draw.color(Pal.accent);
				Angles.randLenVectors(e.id, 4, 24 * e.fout() * e.fout(), (x, y) -> {
					Lines.stroke(e.fout() * 1.7f);
					Lines.square(e.x + x, e.y + y, 2f + e.fout() * 6f);
				});
			});
			drawer = new DrawPrinter(outputItem.item) {{
				printColor = lightColor = Pal.accent;
				moveLength = 4.2f;
				time = 25f;
			}};
			clipSize = size * tilesize * 2f;
			consumePower(7f);
			consumeItems(with(Items.sand, 15, Items.thorium, 5));
		}};
		phaseFusionInstrument = new GenericCrafter("phase-fusion-instrument") {{
			requirements(Category.crafting, with(Items.silicon, 100, Items.plastanium, 80, Items.phaseFabric, 60, Itemsf.chromium, 80, Itemsf.nanoCore, 40));
			size = 3;
			itemCapacity = 30;
			lightRadius /= 2f;
			craftTime = 80;
			craftEffect = Fxf.crossBlast(Pal.accent, 45f, 45f);
			craftEffect.lifetime *= 1.5f;
			updateEffect = Fxf.squareRand(Pal.accent, 5f, 15f);
			outputItem = new ItemStack(Items.phaseFabric, 5);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawRegion("-bottom-2"), new DrawCrucibleFlame() {{
				flameColor = Pal.accent;
				midColor = Color.valueOf("2e2f34");
				circleStroke = 1.05f;
				circleSpace = 2.65f;
			}
				@Override
				public void draw(Building build) {
					if (build.warmup() > 0f && flameColor.a > 0.001f) {
						Lines.stroke(circleStroke * build.warmup());
						float si = Mathf.absin(flameRadiusScl, flameRadiusMag);
						float a = alpha * build.warmup();
						Draw.blend(Blending.additive);
						Draw.color(flameColor, a);
						float base = (Time.time / particleLife);
						rand.setSeed(build.id);
						for (int i = 0; i < particles; i++) {
							float fin = (rand.random(1f) + base) % 1f, fout = 1f - fin;
							float angle = rand.random(360f) + (Time.time / rotateScl) % 360f;
							float len = particleRad * particleInterp.apply(fout);
							Draw.alpha(a * (1f - Mathf.curve(fin, 1f - fadeMargin)));
							Fill.square(build.x + Angles.trnsx(angle, len), build.y + Angles.trnsy(angle, len), particleSize * fin * build.warmup(), 45);
						}
						Draw.blend();
						Draw.color(midColor, build.warmup());
						Lines.square(build.x, build.y, (flameRad + circleSpace + si) * build.warmup(), 45);
						Draw.reset();
					}
				}
			}, new DrawDefault(), new DrawGlowRegion() {{
				color = Pal.accent;
				layer = -1;
				glowIntensity = 1.1f;
				alpha = 1.1f;
			}}, new DrawRotator(1f, "-top") {
				@Override
				public void draw(Building build) {
					Drawf.spinSprite(rotator, build.x + x, build.y + y, Drawn.rotator_90(Drawn.cycle(build.totalProgress() * rotateSpeed, 0, craftTime), 0.15f));
				}
			});
			consumePower(9f);
			consumeItems(with(Itemsf.uranium, 3, Itemsf.rareEarth, 8));
		}};
		clarifier = new GenericCrafter("clarifier") {{
			requirements(Category.crafting, with(Items.copper, 60, Items.graphite, 35, Items.metaglass, 30));
			size = 3;
			hasLiquids = true;
			liquidCapacity = 90;
			outputLiquid = new LiquidStack(Liquids.water, 0.35f);
			outputItem = new ItemStack(Itemsf.salt, 1);
			Color color1 = Color.valueOf("85966a"), color2 = Color.valueOf("f1ffdc"), color3 = Color.valueOf("728259");
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquidsf.brine, 2), new DrawCultivator() {{
				timeScl = 120;
				bottomColor = color1;
				plantColorLight = color2;
				plantColor = color3;
				radius = 2.5f;
				bubbles = 32;
				spread = 8;
			}}, new DrawCells() {{
				range = 9;
				particles = 160;
				lifetime = 60f * 5f;
				particleColorFrom = color2;
				particleColorTo = color3;
				color = color1;
			}}, new DrawDefault()/*, new DrawGlowRegion("-glow") {{
				alpha = 0.7f;
				glowScale = 6;
			}}*/);
			consumeLiquid(Liquidsf.brine, 0.4f);
			consumePower(1.5f);
			squareSprite = false;
		}};
		ironcladCompressor = new MultiCrafter("ironclad-compressor") {{
			requirements(Category.crafting, with(Items.titanium, 250, Items.graphite, 210, Items.plastanium, 90, Items.silicon, 80));
			health = 1800;
			size = 3;
			itemCapacity = 60;
			liquidCapacity = 360f;
			hasItems = true;
			hasLiquids = true;
			outputsLiquid = true;
			useBlockDrawer = false;
			craftPlans.add(new CraftPlan() {{
				outputItems = ItemStack.with(Items.graphite, 5);
				craftTime = 15f;
				consumeItem(Items.coal, 8);
				consumeLiquid(Liquids.water, 0.8f);
				consumePower(3f);
				drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawDefault(), new DrawRegion("-top-graphite"));
			}}, new CraftPlan() {{
				outputLiquids = LiquidStack.with(Liquids.oil, 0.8f);
				craftTime = 30f;
				consumeItem(Items.sporePod, 4);
				consumePower(2.8f);
				drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawDefault(), new DrawCircles() {{
					color = Color.valueOf("1d201f").a(0.24f);
					strokeMax = 2.5f;
					radius = 30f * 0.25f;
					amount = 4;
				}}, new DrawLiquidTile() {{
					drawLiquid = Liquids.oil;
					padding = 34 * 0.25f;
				}}, new DrawRegion("-top-spore"));
			}}, new CraftPlan() {{
				outputItems = ItemStack.with(Items.plastanium, 1);
				craftTime = 15f;
				consumeItem(Items.titanium, 2);
				consumeLiquid(Liquids.oil, 1f);
				consumePower(7f);
				drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawDefault(), new DrawRegion("-top-plast"), new DrawFade() {{
					suffix = "-top-plast-top";
				}});
			}});
		}};
		originiumHeater = new HeatProducer("originium-heater") {{
			requirements(Category.crafting, with(Items.graphite, 50, Items.thorium, 30, Itemsf.originium, 15));
			size = 2;
			health = 220;
			drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput());
			rotateDraw = false;
			regionRotated1 = 1;
			ambientSound = Sounds.hum;
			consumePower(120f / 60f);
			heatOutput = 6f;
		}};
		uraniumSynthesizer = new GenericCrafter("uranium-synthesizer") {{
			requirements(Category.crafting, with(Items.graphite, 50, Items.silicon, 40, Items.plastanium, 30, Items.phaseFabric, 15, Itemsf.purifiedOriginium, 10));
			size = 2;
			health = 350;
			craftTime = 60;
			outputItem = new ItemStack(Itemsf.uranium, 1);
			craftEffect = Fx.smeltsmoke;
			drawer = new DrawMulti(new DrawDefault(), new DrawGlowRegion() {{
				alpha = 1f;
				color = Palf.uraniumGrey.cpy().lerp(Color.white, 0.1f);
			}});
			consumePower(5f);
			consumeItems(with(Items.graphite, 1, Items.thorium, 1));
		}};
		chromiumSynthesizer = new GenericCrafter("chromium-synthesizer") {{
			requirements(Category.crafting, with(Items.metaglass, 30, Items.silicon, 40, Items.plastanium, 50, Items.phaseFabric, 25, Itemsf.purifiedOriginium, 15));
			size = 3;
			health = 650;
			hasLiquids = true;
			itemCapacity = 20;
			liquidCapacity = 30f;
			craftTime = 240f;
			outputItem = new ItemStack(Itemsf.chromium, 5);
			craftEffect = Fx.smeltsmoke;
			drawer = new DrawMulti(new DrawDefault(), new DrawLiquidRegion(Liquids.slag), new DrawGlowRegion() {{
				glowScale = 8;
				alpha = 0.8f;
			}}, new DrawFlame() {{
				flameRadius = 0;
				flameRadiusIn = 0;
				flameRadiusMag = 0;
				flameRadiusInMag = 0;
			}});
			consumePower(7.5f);
			consumeLiquid(Liquids.slag, 12f / 60f);
			consumeItem(Items.titanium, 8);
		}};
		heavyAlloySmelter = new GenericCrafter("heavy-alloy-smelter") {{
			requirements(Category.crafting, with(Items.lead, 150, Items.silicon, 80, Itemsf.nanoCore, 12, Items.thorium, 120, Itemsf.chromium, 30, Items.phaseFabric, 20));
			size = 3;
			health = 850;
			craftTime = 80f;
			outputItem = new ItemStack(Itemsf.heavyAlloy, 1);
			craftEffect = Fx.smeltsmoke;
			drawer = new DrawMulti(new DrawDefault(), new DrawFlame());
			consumePower(11f);
			consumeItems(with(Itemsf.uranium, 1, Itemsf.chromium, 1));
		}};
		metalAnalyzer = new Separator("metal-analyzer") {{
			requirements(Category.crafting, with(Items.titanium, 120, Items.silicon, 180, Items.plastanium, 80, Itemsf.nanoCore, 30));
			size = 3;
			itemCapacity = 20;
			liquidCapacity = 30f;
			craftTime = 20f;
			results = with(Items.titanium, 2, Items.thorium, 2, Itemsf.uranium, 1, Itemsf.chromium, 1);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawPistons() {{
				sides = 4;
				sinMag = 3.9f;
				lenOffset = -1.785f;
				angleOffset = 45f;
			}}, new DrawRegion("-shade"), new DrawDefault(), new DrawLiquidTile(Liquids.water, 39f / 4f), new DrawRegion("-top"));
			consumePower(3.5f);
			consumeItem(Itemsf.rareEarth, 1);
			consumeLiquid(Liquids.water, 6f / 60f);
		}};
		nitrificationReactor = new GenericCrafter("nitrification-reactor") {{
			requirements(Category.crafting, with(Items.metaglass, 80, Items.silicon, 20, Items.plastanium, 30, Itemsf.chromium, 40));
			size = 2;
			health = 380;
			armor = 5f;
			hasLiquids = true;
			itemCapacity = 10;
			liquidCapacity = 24;
			craftTime = 60;
			outputLiquid = new LiquidStack(Liquidsf.nitratedOil, 12f / 60f);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.oil), new DrawLiquidTile(Liquidsf.nitratedOil), new DrawDefault());
			consumePower(5f);
			consumeLiquid(Liquids.oil, 12f / 60f);
			consumeItem(Items.sporePod, 1);
		}};
		nitratedOilSedimentationTank = new Separator("nitrated-oil-sedimentation-tank") {{
			requirements(Category.crafting, with(Items.copper, 160, Items.graphite, 120, Items.plastanium, 40, Itemsf.chromium, 60));
			size = 3;
			health = 680;
			armor = 8f;
			itemCapacity = 15;
			liquidCapacity = 54;
			results = with(Items.pyratite, 1, Items.blastCompound, 4);
			craftTime = 12f;
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquidsf.nitratedOil), new DrawDefault());
			ambientSound = Soundsf.largeBeam;
			ambientSoundVolume = 0.24f;
			consumePower(4f);
			consumeLiquid(Liquidsf.nitratedOil, 36f / 60f);
		}};
		//production-erekir
		ventHeater = new ThermalHeater("vent-heater") {{
			requirements(Category.crafting, with(Items.beryllium, 60, Items.graphite, 70, Items.tungsten, 80, Items.oxide, 50));
			size = 3;
			hasPower = false;
			hasLiquids = true;
			attribute = Attribute.steam;
			group = BlockGroup.liquids;
			displayEfficiencyScale = 1f / 9;
			minEfficiency = 9 - 0.0001f;
			displayEfficiency = false;
			generateEffect = Fx.turbinegenerate;
			effectChance = 0.04f;
			ambientSound = Sounds.hum;
			ambientSoundVolume = 0.06f;
			drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput());
			squareSprite = false;
			heatOutput = 15f / 9;
			outputLiquid = new LiquidStack(Liquids.water, 5f / 60f / 9);
			buildCostMultiplier = 0.8f;
		}
			@Override
			public void setStats() {
				super.setStats();
				stats.remove(Stat.basePowerGeneration);
			}
		};
		chemicalSiliconSmelter = new GenericCrafter("chemical-silicon-smelter") {{
			requirements(Category.crafting, with(Items.graphite, 140, Items.silicon, 50, Items.tungsten, 120, Items.oxide, 100));
			size = 4;
			hasLiquids = true;
			itemCapacity = 30;
			liquidCapacity = 20f;
			craftTime = 50;
			outputItem = new ItemStack(Items.silicon, 8);
			consumeItems(with(Items.sand, 8));
			consumeLiquids(LiquidStack.with(Liquids.hydrogen, 3f / 60f));
			consumePower(3f);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidRegion(Liquids.hydrogen), new DrawCrucibleFlame() {{
				midColor = Color.valueOf("97a5f7");
				flameColor = Color.valueOf("d1e4ff");
				flameRad = 4.45f;
				circleSpace = 3f;
				flameRadiusScl = 16f;
				flameRadiusMag = 3f;
				circleStroke = 0.6f;
				particles = 33;
				particleLife = 107f;
				particleRad = 16f;
				particleSize = 2.68f;
				rotateScl = 1.7f;
			}}, new DrawDefault());
			squareSprite = false;
		}};
		liquidFuelHeater = new FuelHeater("liquid-fuel-heater") {{
			requirements(Category.crafting, with(Items.graphite, 160, Items.beryllium, 120, Items.tungsten, 80, Items.oxide, 30));
			size = 3;
			heatOutput = 10;
			liquidCapacity = 60;
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(), new DrawDefault(), new DrawHeatOutput());
			consume(new ConsumeLiquidFlammable(7.5f / 60f));
		}};
		heatDriver = new HeatDriver("heat-driver") {{
			requirements(Category.crafting, with(Items.graphite, 110, Items.silicon, 120, Items.tungsten, 150, Items.oxide, 130, Items.carbide, 60));
			size = 3;
			drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput(), new DrawHeatInput("-heat"), new DrawHeatDriver());
			range = 360;
			regionRotated1 = 1;
			consumePower(4f);
		}};
		largeElectricHeater = new HeatProducer("large-electric-heater") {{
			requirements(Category.crafting, with(Items.tungsten, 150, Items.oxide, 120, Items.carbide, 50));
			size = 5;
			health = 4160;
			armor = 13f;
			drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput());
			squareSprite = false;
			rotateDraw = false;
			heatOutput = 25f;
			regionRotated1 = 1;
			ambientSound = Sounds.hum;
			consumePower(550f / 60f);
		}};
		largeOxidationChamber = new HeatProducer("large-oxidation-chamber") {{
			requirements(Category.crafting, with(Items.tungsten, 180, Items.graphite, 220, Items.silicon, 220, Items.beryllium, 320, Items.oxide, 120, Items.carbide, 70));
			size = 5;
			health = 2650;
			armor = 3f;
			squareSprite = false;
			hasItems = hasLiquids = hasPower = true;
			outputItem = new ItemStack(Items.oxide, 5);
			researchCostMultiplier = 1.1f;
			consumeLiquid(Liquids.ozone, 8f / 60f);
			consumeItem(Items.beryllium, 5);
			consumePower(1.5f);
			rotateDraw = false;
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidRegion(Liquids.ozone), new DrawDefault(), new DrawHeatOutput());
			ambientSound = Sounds.extractLoop;
			ambientSoundVolume = 0.3f;
			regionRotated1 = 2;
			craftTime = 60f * 2f;
			itemCapacity = 30;
			liquidCapacity = 50f;
			heatOutput = 35f;
			canOverdrive = true;
		}};
		largeSurgeCrucible = new HeatCrafter("large-surge-crucible") {{
			requirements(Category.crafting, with(Items.graphite, 220, Items.silicon, 200, Items.tungsten, 240, Items.oxide, 120, Items.surgeAlloy, 80));
			size = 4;
			health = 1650;
			armor = 6f;
			hasLiquids = true;
			itemCapacity = 30;
			liquidCapacity = 600;
			heatRequirement = 15;
			craftTime = 180;
			outputItem = new ItemStack(Items.surgeAlloy, 3);
			ambientSound = Sounds.smelter;
			ambientSoundVolume = 1.8f;
			craftEffect = new RadialEffect(Fx.surgeCruciSmoke, 4, 90, 5);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawCircles() {{
				color = Color.valueOf("ffc073");
				strokeMax = 2.5f;
				radius = 10;
				amount = 3;
			}}, new DrawLiquidRegion(Liquids.slag), new DrawDefault(), new DrawHeatInput(), new DrawHeatRegion() {{
				color = Color.valueOf("ff6060");
			}}, new DrawHeatRegion("-vents"));
			consumeItem(Items.silicon, 8);
			consumeLiquid(Liquids.slag, 80f / 60f);
			consumePower(1f);
		}};
		largeCarbideCrucible = new HeatCrafter("large-carbide-crucible") {{
			requirements(Category.crafting, with(Items.thorium, 300, Items.tungsten, 400, Items.oxide, 100, Items.carbide, 60));
			size = 5;
			health = 2950;
			armor = 10f;
			itemCapacity = 30;
			heatRequirement = 20;
			craftTime = 135;
			outputItem = new ItemStack(Items.carbide, 3);
			ambientSound = Sounds.smelter;
			ambientSoundVolume = 1.8f;
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawCrucibleFlame(), new DrawDefault(), new DrawHeatInput());
			consumeItems(with(Items.graphite, 8, Items.tungsten, 5));
			consumePower(1f);
		}};
		uraniumFuser = new HeatCrafter("uranium-fuser") {{
			requirements(Category.crafting, with(Items.silicon, 120, Items.graphite, 60, Items.tungsten, 100, Items.oxide, 40, Items.surgeAlloy, 60, Itemsf.purifiedOriginium, 10));
			size = 3;
			itemCapacity = 20;
			heatRequirement = 10f;
			craftTime = 120f;
			liquidCapacity = 300f;
			hasLiquids = true;
			outputItem = new ItemStack(Itemsf.uranium, 1);
			craftEffect = new Effect(50f, e -> {
				Draw.color(Liquids.slag.color);
				Draw.alpha(e.fslope() * 0.8f);
				Mathf.rand.setSeed(e.id + 2);
				Angles.randLenVectors(e.id, 5, 3f, 9f * e.fin(), (x, y) -> Fill.circle(e.x + x, e.y + y, Mathf.rand.random(0.65f, 1.5f)));
			}).layer(Layer.bullet - 1f);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.slag, 3f), new DrawDefault(), new DrawHeatInput(), new DrawGlowRegion("-glow") {{
				color = Color.valueOf("70170b");
			}}, new DrawBlurSpin("-rotator", 12f));
			consumeItem(Items.thorium, 1);
			consumeLiquid(Liquids.slag, 30f / 60f);
			consumePower(2f);
			squareSprite = false;
		}};
		chromiumFuser = new GenericCrafter("chromium-fuser") {{
				requirements(Category.crafting, with(Items.silicon, 150, Items.graphite, 120, Items.tungsten, 200, Items.oxide, 180, Items.phaseFabric, 100, Itemsf.purifiedOriginium, 15));
				size = 3;
				itemCapacity = 20;
				craftTime = 35f;
				liquidCapacity = 300f;
				hasLiquids = true;
				craftEffect = Fx.none;
				outputItem = new ItemStack(Itemsf.chromium, 1);
				drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.hydrogen, 3f), new DrawDefault(), new DrawHeatInput());
				consumeItem(Items.tungsten, 1);
				consumeLiquid(Liquids.hydrogen, 12f / 60f);
				consumePower(12f);
				squareSprite = false;
		}
			public final float sizeScl = 15 * 6f;
			public final Color color1 = Pal.lancerLaser, color2 = Pal.sapBullet;
			public TextureRegion lightRegion, heatRegion, shadowRegion;
		{
			buildType = () -> new GenericCrafterBuild() {
				public boolean nextFlash;
				public float heatf, warmupf;

				@Override
				public void updateTile() {
					super.updateTile();
					if (!nextFlash && heatf < 0.001f && Mathf.chance(0.01f * edelta()) && canConsume() && efficiency() > 0.0001f) {
						nextFlash = true;
						heatf = 1f;
					} else if (nextFlash && heatf < 0.001f) {
						nextFlash = false;
						heatf = 1f;
					}
					heatf = Mathf.approachDelta(heatf, 0f, 0.05f);
					warmupf = Mathf.approachDelta(warmupf, efficiency(), 0.04f);
				}

				@Override
				public void draw() {
					super.draw();
					setFlameColor(Tmp.c4);
					if (heatf >= 0.001f) {
						Draw.z(Layer.bullet - 0.01f);
						Draw.color(Color.white, Tmp.c4, Mathf.clamp(heatf * 3f - 2f));
						Draw.alpha(Mathf.clamp(heatf * 1.5f));
						Draw.rect(lightRegion, x, y);
					}
					Draw.z(Layer.blockOver);
					Draw.blend(Blending.additive);
					if (heatf >= 0.001f) {
						Draw.alpha(Mathf.clamp(heatf * 1.5f) * 0.2f);
						Draw.rect(heatRegion, x, y);
					}
					Draw.alpha(Mathf.absin(11f, 0.2f * warmupf));
					Draw.rect(shadowRegion, x, y, sizeScl * Draw.scl * Draw.xscl, sizeScl * Draw.scl * Draw.yscl);
					Draw.blend();
					Draw.color();
				}

				@Override
				public void drawLight() {
					super.drawLight();
					setFlameColor(Tmp.c4);
					Drawf.light(x, y, (lightRadius * (1f + Mathf.clamp(heatf) * 0.1f) + Mathf.absin(10f, 5f)) * warmupf * block.size, Tmp.c4, 0.65f);
				}

				public void setFlameColor(Color tmp) {
					tmp.set(color1).lerp(color2, Mathf.absin(Time.time + Mathf.randomSeed(pos(), 0f, 9f * 6.29f), 9f, 1f));
				}
			};
		}
			@Override
			public void load() {
				super.load();
				lightRegion = Core.atlas.find(name + "-light");
				heatRegion = Core.atlas.find(name + "-light-heat");
				shadowRegion = Core.atlas.find("circle-shadow");
			}
		};
		//defense
		lighthouse = new LightBlock("lighthouse") {{
			requirements(Category.effect, BuildVisibility.lightingOnly, with(Items.graphite, 20, Items.silicon, 10, Items.lead, 30, Items.titanium, 15));
			size = 2;
			brightness = 1f;
			radius = 220f;
			consumePower(0.15f);
			buildCostMultiplier = 0.8f;
		}};
		mendDome = new MendProjector("mend-dome") {{
			requirements(Category.effect, with(Items.lead, 200, Items.titanium, 130, Items.silicon, 120, Items.plastanium, 60, Items.surgeAlloy, 40));
			size = 3;
			reload = 200f;
			range = 150f;
			healPercent = 15f;
			phaseBoost = 25f;
			phaseRangeBoost = 75;
			scaledHealth = 240;
			consumePower(2.5f);
			consumeItem(Items.phaseFabric, 1).boost();
		}};
		sectorStructureMender = new RegenProjector("sector-structure-mender") {{
			requirements(Category.effect, with(Items.lead, 600, Items.titanium, 400, Items.silicon, 350, Items.plastanium, 250, Items.surgeAlloy, 220, Itemsf.nanoCore, 150));
			size = 4;
			health = 1800;
			range = 1500;
			healPercent = 0.025f;
			canOverdrive = false;
			optionalUseTime = 3600;
			optionalMultiplier = 6;
			effectChance = 0.5f;
			effect = WrapperEffect.wrap(Fxf.polyParticle, Palf.nanoCoreRed);
			drawer = new DrawMulti(new DrawDefault(), new DrawPulseShape() {{
				layer = 110;
				stroke = 3f;
				timeScl = 120f;
				color = Palf.regenerating;
			}}, new DrawShape() {{
				layer = 110;
				radius = 5f;
				useWarmupRadius = true;
				timeScl = 1.22f;
				color = Palf.regenerating;
			}});
			consumePower(20f);
			consumeItem(Itemsf.nanoCore, 10).boost();
		}};
		assignOverdrive = new AssignOverdrive("assign-overdrive") {{
			requirements(Category.effect, with(Items.silicon, 150, Items.thorium, 120, Items.plastanium, 100, Items.surgeAlloy, 60, Itemsf.chromium, 80));
			size = 3;
			range = 240f;
			phaseRangeBoost = 0f;
			speedBoost = 2.75f;
			speedBoostPhase = 1.25f;
			useTime = 400f;
			maxLink = 9;
			hasBoost = true;
			strokeOffset = -0.05f;
			strokeClamp = 0.06f;
			consumePower(14f);
			consumeItem(Items.phaseFabric).boost();
			squareSprite = false;
		}};
		largeShieldGenerator = new ForceProjector("large-shield-generator") {{
			requirements(Category.effect, with(Items.silicon, 120, Items.lead, 250, Items.graphite, 180, Items.plastanium, 150, Items.phaseFabric, 40, Itemsf.chromium, 60));
			size = 4;
			radius = 220f;
			shieldHealth = 20000f;
			cooldownNormal = 12f;
			cooldownLiquid = 6f;
			cooldownBrokenBase = 9f;
			itemConsumer = consumeItem(Items.phaseFabric).boost();
			phaseUseTime = 180f;
			phaseRadiusBoost = 100f;
			phaseShieldBoost = 15000f;
			consumePower(15f);
		}
			@Override
			protected TextureRegion[] icons() {
				return teamRegion.found() ? new TextureRegion[]{region, teamRegions[Team.sharded.id]} : new TextureRegion[]{region};
			}
		};
		paralysisMine = new ShockMine("paralysis-mine") {{
			requirements(Category.effect, with(Items.titanium, 15, Items.silicon, 10, Items.surgeAlloy, 5));
			size = 1;
			hasShadow = false;
			underBullets = true;
			health = 120;
			damage = 36f;
			shots = 8;
			length = 12;
			tendrils = 6;
			lightningColor = Pal.surge;
		}};
		detonator = new Explosive("detonator") {{
			requirements(Category.effect, HIBuildVisibility.campaignOrSandboxOnly, with(Items.lead, 30, Items.graphite, 20, Items.thorium, 10, Items.blastCompound, 10));
			health = 160;
			size = 3;
			squareSprite = false;
		}};
		bombLauncher = new BombLauncher("bomb-launcher") {{
			requirements(Category.effect, with(Items.plastanium, 200, Items.graphite, 250, Items.silicon, 350, Items.thorium, 400, Items.surgeAlloy, 75));
			health = 1200;
			size = 3;
			itemCapacity = 30;
			storage = 1;
			bullet = new EffectBulletType(15f, 100f, 1800f) {{
				trailChance = 0.25f;
				trailEffect = Fxf.trailToGray;
				trailParam = 1.5f;
				smokeEffect = Fxf.hugeSmoke;
				shootEffect = Fxf.boolSelector;
				hittable = false;
				scaledSplashDamage = true;
				collidesTiles = collidesGround = collides = true;
				lightningDamage = 100f;
				lightColor = lightningColor = trailColor = hitColor = Pal.command;
				lightning = 3;
				lightningLength = 8;
				lightningLengthRand = 16;
				splashDamageRadius = 120f;
				hitShake = despawnShake = 20f;
				hitSound = despawnSound = Sounds.explosionbig;
				hitEffect = despawnEffect = new MultiEffect(Fxf.crossBlast(hitColor, splashDamageRadius * 1.25f), Fxf.blast(hitColor, splashDamageRadius * 1.5f));
			}};
			reloadTime = 300f;
			consumePowerCond(6f, BombLauncherBuild::isCharging);
			consumeItem(Items.blastCompound, 10);
		}};
		//defense-erekir
		largeRadar = new Radar("large-radar") {{
			requirements(Category.effect, BuildVisibility.fogOnly, with(Items.graphite, 180, Items.silicon, 160, Items.beryllium, 30, Items.tungsten, 10, Items.oxide, 20));
			health = 180;
			size = 3;
			outlineColor = Color.valueOf("4a4b53");
			fogRadius = 86;
			consumePower(1.2f);
		}};
		//storage
		bin = new StorageBlock("bin") {{
			requirements(Category.effect, with(Items.copper, 55, Items.lead, 35));
			size = 1;
			itemCapacity = 60;
			scaledHealth = 55;
		}
			@Override
			protected TextureRegion[] icons() {
				return teamRegion.found() ? new TextureRegion[]{region, teamRegions[Team.sharded.id]} : new TextureRegion[]{region};
			}
		};
		cargo = new StorageBlock("cargo") {{
			requirements(Category.effect, with(Items.titanium, 350, Items.thorium, 250, Items.plastanium, 125));
			size = 4;
			itemCapacity = 3000;
			scaledHealth = 55;
		}
			@Override
			protected TextureRegion[] icons() {
				return teamRegion.found() ? new TextureRegion[]{region, teamRegions[Team.sharded.id]} : new TextureRegion[]{region};
			}
		};
		machineryUnloader = new Unloader("machinery-unloader") {{
			requirements(Category.effect, with(Items.copper, 15, Items.lead, 10));
			health = 40;
			speed = 60f / 4.2f;
			group = BlockGroup.transportation;
		}};
		rapidUnloader = new Unloaderf("rapid-unloader") {{
			requirements(Category.effect, with(Items.silicon, 35, Items.plastanium, 15, Itemsf.nanoCore, 10, Itemsf.chromium, 15));
			speed = 1f;
			group = BlockGroup.transportation;
		}};
		coreStorage = new CoreStorageBlock("core-storage") {{
			requirements(Category.effect, with(Items.lead, 600, Items.titanium, 400, Items.silicon, 300, Items.thorium, 150, Items.plastanium, 120));
			size = 3;
		}
			@Override
			protected TextureRegion[] icons() {
				return teamRegion.found() ? new TextureRegion[]{region, teamRegions[Team.sharded.id]} : new TextureRegion[]{region};
			}
		};
		//storage-erekir
		reinforcedCoreStorage = new CoreStorageBlock("reinforced-core-storage") {{
			requirements(Category.effect, BuildVisibility.sandboxOnly, with(Items.beryllium, 300, Items.tungsten, 200, Items.thorium, 120, Items.silicon, 220, Items.oxide, 80));
			size = 3;
			squareSprite = false;
		}
			@Override
			protected TextureRegion[] icons() {
				return teamRegion.found() ? new TextureRegion[]{region, teamRegions[Team.sharded.id]} : new TextureRegion[]{region};
			}
		};
		//payload
		payloadJunction = new PayloadJunction("payload-junction") {{
			requirements(Category.units, with(Items.graphite, 15, Items.copper, 20));
		}};
		payloadRail = new PayloadRail("payload-rail") {{
			requirements(Category.units, with(Items.graphite, 45, Items.titanium, 35, Items.silicon, 20));
		}};
		//payload-erekir
		reinforcedPayloadJunction = new PayloadJunction("reinforced-payload-junction") {{
			requirements(Category.units, with(Items.tungsten, 15, Items.beryllium, 10));
			moveTime = 35f;
			health = 800;
			researchCostMultiplier = 4f;
			underBullets = true;
		}};
		reinforcedPayloadRail = new PayloadRail("reinforced-payload-rail") {{
			requirements(Category.units, with(Items.tungsten, 55, Items.silicon, 25, Items.oxide, 10));
		}};
		//unit
		unitMaintenanceDepot = new RepairTower("unit-maintenance-depot") {{
			requirements(Category.units, with(Items.thorium, 360, Items.plastanium, 220, Items.surgeAlloy, 160, Items.phaseFabric, 100, Itemsf.nanoCore, 120));
			size = 4;
			health = 1200;
			liquidCapacity = 120f;
			range = 224f;
			healAmount = 12;
			circleSpeed = 75f;
			circleStroke = 8f;
			squareRad = 6f;
			squareSpinScl = 1.2f;
			glowMag = 0.4f;
			glowScl = 12f;
			consumePower(18f);
			consumeLiquid(Liquidsf.nanoFluid, 0.6f);
		}};
		titanReconstructor = new Reconstructor("titan-reconstructor") {{
			requirements(Category.units, with(Items.lead, 4000, Items.silicon, 3000, Items.plastanium, 1500, Items.surgeAlloy, 1200, Items.phaseFabric, 300, Itemsf.uranium, 600, Itemsf.chromium, 800));
			size = 11;
			liquidCapacity = 360f;
			scaledHealth = 100f;
			constructTime = 60f * 60f * 5f;
			upgrades.addAll(
					new UnitType[]{UnitTypes.eclipse, UnitTypesf.sunlit},
					new UnitType[]{UnitTypes.toxopid, UnitTypesf.cancer},
					new UnitType[]{UnitTypes.reign, UnitTypesf.suzerain},
					new UnitType[]{UnitTypes.oct, UnitTypesf.windstorm},
					new UnitType[]{UnitTypes.corvus, UnitTypesf.supernova},
					new UnitType[]{UnitTypes.omura, UnitTypesf.mosasaur},
					new UnitType[]{UnitTypes.navanax, UnitTypesf.killerWhale},
					new UnitType[]{UnitTypesf.destruction, UnitTypesf.purgatory},
					new UnitType[]{UnitTypesf.lepidoptera, UnitTypesf.mantodea}
			);
			consumePower(35f);
			consumeLiquid(Liquids.cryofluid, 4f);
			consumeItems(ItemStack.with(Items.silicon, 1500, Itemsf.nanoCore, 300, Itemsf.uranium, 400, Itemsf.chromium, 500));
		}};
		experimentalUnitFactory = new DerivativeUnitFactory("experimental-unit-factory") {{
			requirements(Category.units, with(Items.silicon, 2500, Items.plastanium, 1000, Items.surgeAlloy, 1200, Itemsf.nanoCore, 800, Items.phaseFabric, 1500, Itemsf.heavyAlloy, 1100));
			size = 5;
			liquidCapacity = 60f;
			floating = true;
			config(Integer.class, (DerivativeUnitFactoryBuild tile, Integer index) -> {
				tile.currentPlan = index < 0 || index >= plans.size ? -1 : index;
				tile.progress = 0f;
				tile.payload = null;
			});
			config(UnitType.class, (DerivativeUnitFactoryBuild tile, UnitType unit) -> {
				tile.currentPlan = plans.indexOf(p -> p.unit == unit);
				tile.progress = 0f;
				tile.payload = null;
			});
			consumePower(40f);
			consumeLiquid(Liquidsf.nanoFluid, 0.3f);
		}
			@Override
			public void init() {
				for (int i = 0; i < content.units().size; i++) {
					UnitType u = content.unit(i);
					if (u != null && u.getFirstRequirements() != null) {
						ItemStack[] is = u.getFirstRequirements();
						ItemStack[] os = new ItemStack[is.length];
						for (int a = 0; a < is.length; a++) {
							os[a] = new ItemStack(is[a].item, is[a].amount >= 40 ? (int) (is[a].amount * (1.0)) : is[a].amount);
						}
						float time = 0;
						if (u.getFirstRequirements().length > 0) {
							for (ItemStack itemStack : os) {
								time += itemStack.amount * itemStack.item.cost;
							}
						}
						plans.add(new UnitPlan(u, time * 2, is));
					}
				}
				super.init();
			}
		};
		//unit-erekir
		largeUnitRepairTower = new RepairTower("large-unit-repair-tower") {{
			requirements(Category.units, with(Items.graphite, 120, Items.silicon, 150, Items.tungsten, 180, Items.oxide, 60, Items.carbide, 30));
			size = 4;
			health = 650;
			squareSprite = false;
			liquidCapacity = 30;
			range = 220;
			healAmount = 10;
			consumePower(3);
			consumeLiquid(Liquids.ozone, 8f / 60f);
		}};
		seniorAssemblerModule = new UnitAssemblerModule("senior-assembler-module") {{
			requirements(Category.units, with(Items.thorium, 800, Items.phaseFabric, 600, Items.surgeAlloy, 400, Items.oxide, 300, Items.carbide, 400));
			size = 5;
			tier = 2;
			regionSuffix = "-dark";
			researchCostMultiplier = 0.75f;
			consumePower(5.5f);
		}};
		//logic
		matrixProcessor = new LogicBlock("matrix-processor") {{
			requirements(Category.logic, with(Items.lead, 500, Items.silicon, 350, Items.surgeAlloy, 125, Itemsf.nanoCore, 50, Itemsf.chromium, 75));
			consumeLiquid(Liquids.cryofluid, 0.12f);
			hasLiquids = true;
			instructionsPerTick = 100;
			range = 8 * 62;
			size = 4;
			squareSprite = false;
		}};
		hugeLogicDisplay = new LogicDisplay("huge-logic-display") {{
			requirements(Category.logic, with(Items.lead, 300, Items.silicon, 250, Items.metaglass, 200, Items.phaseFabric, 150));
			displaySize = 272;
			size = 9;
		}};
		buffrerdMemoryCell = new CopyMemoryBlock("buffrerd-memory-cell") {{
			requirements(Category.logic, with(Items.titanium, 40, Items.graphite, 40, Items.silicon, 40));
			size = 1;
			memoryCapacity = 64;
		}};
		buffrerdMemoryBank = new CopyMemoryBlock("buffrerd-memory-bank") {{
			requirements(Category.logic, with(Items.titanium, 40, Items.graphite, 90, Items.silicon, 90, Items.phaseFabric, 30));
			size = 2;
			memoryCapacity = 512;
		}};
		heatSink = new ProcessorCooler("heat-sink") {{
			requirements(Category.logic, with(Items.titanium, 70, Items.silicon, 25, Items.plastanium, 65));
			size = 2;
		}};
		heatFan = new ProcessorFan("cooler-fan") {{
			requirements(Category.logic, with(Items.titanium, 90, Items.silicon, 50, Items.plastanium, 50, Items.phaseFabric, 25));
			size = 3;
			boost = 3;
			maxProcessors = 5;
			consumePower(4f);
		}};
		heatSinkLarge = new ProcessorCooler("water-block") {{
			requirements(Category.logic, with(Items.titanium, 110, Items.silicon, 50, Items.metaglass, 40, Items.plastanium, 30, Items.surgeAlloy, 15));
			size = 3;
			boost = 2;
			maxProcessors = 6;
			liquidCapacity = 60;
			acceptCoolant = true;
		}};
		laserRuler = new LaserRuler("laser-ruler") {{
			requirements(Category.logic, with(Items.lead, 15, Items.silicon, 25, Items.metaglass, 5));
			size = 1;
		}};
		labelMessage = new LabelMessageBlock("label-message");
		//turret
		dissipation = new PointDefenseTurret("dissipation") {{
			requirements(Category.turret, with(Items.silicon, 220, Itemsf.chromium, 80, Items.phaseFabric, 40, Items.plastanium, 60));
			size = 3;
			hasPower = true;
			scaledHealth = 250;
			range = 240f;
			shootLength = 5f;
			bulletDamage = 110f;
			retargetTime = 6f;
			reload = 6f;
			envEnabled |= Env.space;
			consumePower(12f);
		}};
		rocketLauncher = new ItemTurret("rocket-launcher") {{
			requirements(Category.turret, with(Items.copper, 60, Items.lead, 40, Items.graphite, 30));
			ammo(Items.graphite, new MissileBulletType(3.6f, 30f) {{
				splashDamage = 15f;
				splashDamageRadius = 18f;
				drag = -0.028f;
				backColor = trailColor = Palf.brightSteelBlue;
				frontColor = Palf.lightGrey;
				lifetime = 36;
				homingPower = 0.045f;
				homingRange = 40f;
				width = 4f;
				height = 16f;
				hitEffect = Fx.flakExplosion;
				ammoMultiplier = 2;
			}}, Items.pyratite, new MissileBulletType(3.6f, 18f) {{
				splashDamage = 36f;
				splashDamageRadius = 22f;
				drag = -0.028f;
				makeFire = true;
				backColor = trailColor = Color.valueOf("ffb90f");
				frontColor = Palf.lightGrey;
				lifetime = 36;
				homingPower = 0.03f;
				homingRange = 40f;
				width = 4f;
				height = 16f;
				hitEffect = Fx.flakExplosion;
				ammoMultiplier = 3;
			}}, Items.blastCompound, new MissileBulletType(3.6f, 16f) {{
				splashDamage = 47f;
				splashDamageRadius = 32f;
				drag = -0.026f;
				backColor = trailColor = Palf.orangeBack;
				frontColor = Palf.lightGrey;
				lifetime = 38;
				homingPower = 0.03f;
				homingRange = 40f;
				width = 4f;
				height = 16f;
				hitEffect = Fx.flakExplosionBig;
				ammoMultiplier = 3;
			}});
			size = 1;
			health = 300;
			range = 210;
			reload = 100f;
			maxAmmo = 20;
			shoot = new ShootAlternate() {{
				shots = 2;
				shotDelay = 10;
				barrels = 2;
			}};
			rotateSpeed = 8f;
			inaccuracy = 0f;
			shootSound = Sounds.missile;
		}};
		largeRocketLauncher = new ItemTurret("large-rocket-launcher") {{
			requirements(Category.turret, with(Items.graphite, 360, Items.titanium, 220, Items.thorium, 100, Items.silicon, 110, Items.plastanium, 70));
			ammo(Items.pyratite, new MissileBulletType(10f, 44f, name("rocket")) {{
				shrinkY = 0;
				inaccuracy = 4;
				trailChance = 0.8f;
				homingRange = 80;
				splashDamage = 68f;
				splashDamageRadius = 36f;
				lifetime = 38f;
				hitShake = 2;
				backColor = trailColor = Color.valueOf("ffb90f");
				frontColor = Palf.lightGrey;
				hitColor = Palf.lightYellow;
				status = StatusEffects.burning;
				statusDuration = 600;
				width = 16;
				height = 40;
				ammoMultiplier = 3;
				shootEffect = new MultiEffect(Fx.shootBig2, Fx.shootPyraFlame, Fx.shootPyraFlame);
				despawnEffect = Fx.flakExplosion;
				hitEffect = new MultiEffect(Fxf.explodeImpWaveSmall, Fxf.impactWave);
			}}, Items.blastCompound, new MissileBulletType(10f, 46f, name("missile")) {{
				recoil = 1;
				shrinkY = 0;
				inaccuracy = 4;
				trailChance = 0.8f;
				homingRange = 80;
				splashDamage = 132f;
				splashDamageRadius = 76f;
				lifetime = 38f;
				hitShake = 2;
				hitColor = backColor = trailColor = Palf.orangeBack;
				frontColor = Palf.lightGrey;
				status = StatusEffects.burning;
				statusDuration = 600;
				width = 14;
				height = 50;
				hitSound = Sounds.explosion;
				ammoMultiplier = 3;
				shootEffect = new MultiEffect(Fx.shootBig2, Fx.shootPyraFlame, Fx.shootPyraFlame);
				despawnEffect = Fx.flakExplosion;
				hitEffect = new MultiEffect(Fxf.explodeImpWave, Fxf.impactWave);
			}});
			size = 3;
			health = 350;
			range = 400;
			reload = 35f;
			shake = 2f;
			rotateSpeed = 5f;
			inaccuracy = 0f;
			shootY = 8;
			shootWarmupSpeed = 0.04f;
			warmupMaintainTime = 45;
			minWarmup = 0.8f;
			shootSound = Sounds.missileSmall;
			drawer = new DrawTurret() {{
				parts.add(new RegionPart("-side") {{
					mirror = true;
					moveX = 1.5f;
					moveY = 0f;
					moveRot = 0f;
				}});
			}};
			smokeEffect = Fxf.shootSmokeMissileSmall;
			shoot = new ShootAlternate() {{
				barrels = 2;
				spread = 11f;
				shots = 3;
				shotDelay = 8f;
			}};
			consumeAmmoOnce = false;
			maxAmmo = 24;
			ammoPerShot = 3;
		}};
		rocketSilo = new ItemTurret("rocket-silo") {{
			requirements(Category.turret, with(Items.lead, 300, Items.graphite, 150, Items.titanium, 120, Items.silicon, 120, Items.plastanium, 50));
			ammo(Items.graphite, new MissileBulletType(8f, 22f, name("missile")) {{
				buildingDamageMultiplier = 0.3f;
				splashDamage = 15f;
				splashDamageRadius = 18f;
				knockback = 0.7f;
				lifetime = 135f;
				homingDelay = 10f;
				homingRange = 800f;
				homingPower = 0.15f;
				backColor = Palf.brightSteelBlue;
				frontColor = Palf.lightGrey;
				trailLength = 15;
				trailWidth = 1.5f;
				trailColor = Color.white.cpy().a(0.5f);
				trailEffect = Fx.none;
				width = 10f;
				height = 40f;
				hitShake = 1f;
				ammoMultiplier = 2f;
				smokeEffect = Fx.shootSmallFlame;
				hitEffect = Fx.flakExplosion;
			}}, Items.pyratite, new MissileBulletType(7f, 14f, name("missile")) {{
				buildingDamageMultiplier = 0.3f;
				splashDamage = 39f;
				splashDamageRadius = 32f;
				status = StatusEffects.burning;
				statusDuration = 600;
				makeFire = true;
				lifetime = 145f;
				homingPower = 0.15f;
				homingDelay = 10f;
				homingRange = 800f;
				backColor = Color.valueOf("ffb90f");
				frontColor = Palf.lightGrey;
				trailLength = 15;
				trailWidth = 1.5f;
				trailColor = Color.white.cpy().a(0.5f);
				trailEffect = Fx.none;
				width = 10f;
				height = 40f;
				hitShake = 1f;
				ammoMultiplier = 2f;
				smokeEffect = Fx.shootSmallFlame;
				hitEffect = Fx.flakExplosionBig;
			}}, Items.blastCompound, new MissileBulletType(7f, 17f, name("missile")) {{
				buildingDamageMultiplier = 0.3f;
				splashDamage = 55f;
				splashDamageRadius = 45f;
				knockback = 3;
				status = StatusEffects.blasted;
				lifetime = 145f;
				homingPower = 0.15f;
				homingDelay = 10f;
				homingRange = 800f;
				backColor = Color.valueOf("ff7055");
				frontColor = Palf.lightGrey;
				trailLength = 15;
				trailWidth = 1.5f;
				trailColor = Color.white.cpy().a(0.5f);
				trailEffect = Fx.none;
				width = 10f;
				height = 40f;
				hitShake = 1f;
				ammoMultiplier = 2f;
				smokeEffect = Fx.shootSmallFlame;
				hitEffect = Fx.flakExplosionBig;
			}}, Items.surgeAlloy, new MissileBulletType(9f, 47f, name("missile")) {{
				buildingDamageMultiplier = 0.3f;
				splashDamage = 75f;
				splashDamageRadius = 45f;
				lightningDamage = 17;
				lightning = 3;
				lightningLength = 8;
				knockback = 3;
				status = StatusEffects.blasted;
				lifetime = 125f;
				homingPower = 0.15f;
				homingDelay = 10f;
				homingRange = 800f;
				backColor = Color.valueOf("f2e770");
				frontColor = Palf.lightGrey;
				trailLength = 16;
				trailWidth = 2.5f;
				trailColor = Color.white.cpy().a(0.5f);
				trailEffect = Fx.none;
				width = 13f;
				height = 48f;
				hitShake = 1f;
				ammoMultiplier = 3f;
				smokeEffect = Fx.shootSmallFlame;
				hitEffect = Fx.flakExplosionBig;
			}});
			size = 3;
			health = 1850;
			range = 760;
			reload = 150f;
			shake = 1f;
			targetAir = true;
			targetGround = true;
			consumeAmmoOnce = false;
			customShadow = false;
			maxAmmo = 32;
			rotateSpeed = 0f;
			inaccuracy = 5f;
			recoil = 0f;
			shootY = 0f;
			shootCone = 360;
			cooldownTime = 110f;
			minWarmup = 0.8f;
			shootWarmupSpeed = 0.055f;
			warmupMaintainTime = 120f;
			solid = false;
			underBullets = true;
			elevation = 0f;
			unitSort = UnitSorts.weakest;
			drawer = new DrawTurret() {{
				parts.addAll(new RegionPart("-cover-top") {{
					progress = PartProgress.warmup;
					moveY = -6f;
				}}, new RegionPart("-cover-left") {{
					progress = PartProgress.warmup;
					moveX = 6f;
				}}, new RegionPart("-cover-bottom") {{
					progress = PartProgress.warmup;
					moveY = 6f;
				}}, new RegionPart("-cover-right") {{
					progress = PartProgress.warmup;
					moveX = -6f;
				}});
			}};
			shoot = new ShootBarrel() {{
				shots = 4;
				shotDelay = 5;
				barrels = new float[]{
						0f, 6f, 0f,
						-6f, 0f, 90f,
						0f, -6f, 180f,
						6f, 0f, -90f
				};
			}};
			ammoUseEffect = Fxf.casing(60f);
			canOverdrive = false;
			shootSound = Soundsf.dd1;
			consumePowerCond(6f, TurretBuild::isActive);
		}};
		dragonBreath = new ItemTurret("dragon-breath") {{
			requirements(Category.turret, with(Items.graphite, 40, Items.silicon, 25, Items.titanium, 60, Items.plastanium, 30));
			ammo(Items.coal, new FlameBulletType(Pal.lightFlame, Pal.darkFlame, Color.gray, range + 8, 14, 60, 22), Items.pyratite, new FlameBulletType(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, range + 8, 20, 72, 22) {{
				damage = 98;
				statusDuration = 60 * 6;
				ammoMultiplier = 4;
			}}, Items.blastCompound, new FlameBulletType(Items.blastCompound.color.cpy().mul(Pal.lightFlame), Items.blastCompound.color.cpy(), Pal.lightishGray, range + 8, 22, 66, 30) {{
				damage = 90;
				status = StatusEffectsf.flamePoint;
				statusDuration = 8 * 60f;
				reloadMultiplier = 0.5f;
				ammoMultiplier = 4;
			}
				final float slpRange = 52f;
				final Effect easyExp = new Effect(20, e -> {
					Fx.rand.setSeed(e.id);
					float baseRd = e.rotation;
					float randRd = baseRd / 6f;
					float pin = (1 - e.foutpow());
					Lines.stroke(2 * e.foutpow(), e.color);
					Lines.circle(e.x, e.y, baseRd * pin);
					for (int i = 0; i < 12; i++) {
						float a = Fx.rand.random(360);
						float lx = Utils.dx(e.x, baseRd * pin, a);
						float ly = Utils.dy(e.y, baseRd * pin, a);
						Drawf.tri(lx, ly, baseRd / 6f * e.foutpow(), (baseRd / 2f + Fx.rand.random(-randRd, randRd)) * e.foutpow(), a + 180);
					}
				});

				@Override
				public void hit(Bullet b) {
					if (absorbable && b.absorbed) return;
					Units.nearbyEnemies(b.team, b.x, b.y, flameLength, unit -> {
						if (Angles.within(b.rotation(), b.angleTo(unit), flameCone) && unit.checkTarget(collidesAir, collidesGround) && unit.hittable()) {
							Fx.hitFlameSmall.at(unit);
							unit.damage(damage);
							if (unit.hasEffect(status)) {
								Damage.damage(b.team, unit.x, unit.y, slpRange, damage / 3f, false, true);
								Damage.status(b.team, unit.x, unit.y, slpRange, status, statusDuration, false, true);
								easyExp.at(unit.x, unit.y, slpRange, Items.blastCompound.color);
								unit.unapply(status);
							} else {
								unit.apply(status, statusDuration);
							}
						}
					});
					indexer.allBuildings(b.x, b.y, flameLength, other -> {
						if (other.team != b.team && Angles.within(b.rotation(), b.angleTo(other), flameCone)) {
							Fx.hitFlameSmall.at(other);
							other.damage(damage * buildingDamageMultiplier);
						}
					});
				}
			});
			size = 2;
			health = 1160;
			recoil = 0f;
			reload = 8f;
			range = 88f;
			shootCone = 50f;
			targetAir = false;
			ammoUseEffect = Fx.none;
			shootSound = Sounds.flame;
			coolantMultiplier = 1.5f;
			coolant = consumeCoolant(0.1f);
		}};
		breakthrough = new PowerTurret("breakthrough") {{
			requirements(Category.turret, with(Items.silicon, 130, Items.thorium, 150));
			size = 4;
			health = 2800;
			range = 500f;
			coolantMultiplier = 1.5f;
			targetAir = true;
			reload = 500f;
			shoot.firstShotDelay = 100f;
			recoil = 5f;
			shake = 4f;
			shootEffect = new Effect(40f, e -> {
				Draw.color(e.color);

				Lines.stroke(e.fout() * 2.5f);
				Lines.circle(e.x, e.y, e.finpow() * 100f);

				Lines.stroke(e.fout() * 5f);
				Lines.circle(e.x, e.y, e.fin() * 100f);

				Draw.color(e.color, Color.white, e.fout());

				Angles.randLenVectors(e.id, 20, 80f * e.finpow(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 5f));

				for (int i = 0; i < 4; i++) {
					Drawf.tri(e.x, e.y, 9f * e.fout(), 170f, e.rotation + Mathf.randomSeed(e.id, 360f) + 90f * i + e.finpow() * (0.5f - Mathf.randomSeed(e.id)) * 150f);
				}
			});
			smokeEffect = Fx.none;
			heatColor = Pal.lancerLaser;
			shootSound = Sounds.laserblast;
			chargeSound = Sounds.lasercharge;
			shootType = new LaserBulletType(1200f) {{
				ammoMultiplier = 1;
				drawSize = length * 2f;
				hitEffect = Fx.hitLiquid;
				shootEffect = Fx.hitLiquid;
				despawnEffect = Fx.none;
				keepVelocity = false;
				collides = false;
				pierce = true;
				hittable = false;
				absorbable = false;
				length = 500f;
				largeHit = true;
				width = 80f;
				lifetime = 65f;
				sideLength = 0f;
				sideWidth = 0f;
				chargeEffect = new Effect(100f, 100f, e -> {
					Draw.color(Pal.lancerLaser);
					Lines.stroke(e.fin() * 3f);

					Lines.circle(e.x, e.y, 4f + e.fout() * 120f);
					Fill.circle(e.x, e.y, e.fin() * 23.5f);

					Angles.randLenVectors(e.id, 20, 50f * e.fout(), (x, y) ->
							Fill.circle(e.x + x, e.y + y, e.fin() * 6f)
					);

					Draw.color();
					Fill.circle(e.x, e.y, e.fin() * 13);
				});
			}};
			rotateSpeed /= 1.66f;
			consumePower(7.2f);
			coolant = consumeCoolant(0.6f);
			coolantMultiplier /= 1.22f;
			hideDetails = false;
		}};
		cloudbreaker = new ItemTurret("cloudbreaker") {{
			requirements(Category.turret, with(Items.graphite, 230, Items.titanium, 220, Items.thorium, 150));
			ammo(Items.titanium, new CritBulletType(14f, 220f) {{
				lifetime = 25f;
				knockback = 5f;
				width = 7f;
				height = 12f;
				pierce = pierceArmor = true;
				pierceCap = 3;
				critChance = 0.08f;
				critMultiplier = 2.5f;
			}}, Items.thorium, new CritBulletType(17.5f, 280f) {{
				lifetime = 25f;
				rangeChange = 70f;
				knockback = 5f;
				width = 8f;
				height = 14f;
				pierce = pierceArmor = true;
				pierceCap = 5;
				critChance = 0.05f;
				critMultiplier = 4.5f;
			}}, Itemsf.uranium, new CritBulletType(20f, 360f) {{
				lifetime = 25f;
				rangeChange = 120f;
				knockback = 4f;
				width = 9f;
				height = 16f;
				pierce = pierceArmor = true;
				pierceCap = 8;
				critChance = 0.05f;
				critMultiplier = 3f;
				despawnHitEffects = false;
				setDefaults = false;
				fragOnHit = false;
				fragBullets = 5;
				fragVelocityMin = 0.8f;
				fragVelocityMax = 1.2f;
				fragRandomSpread = 30f;
				fragBullet = new CritBulletType(12f, 80f) {{
					lifetime = 5f;
					knockback = 3f;
					width = 6f;
					height = 14f;
					pierceCap = 3;
					critMultiplier = 3f;
					critEffect = Fxf.miniCrit;
				}};
			}
				@Override
				public void removed(Bullet b) {
					super.removed(b);
					if (b.fdata != 1f) createFrags(b, b.x, b.y);
				}
			});
			size = 3;
			range = 330f;
			hideDetails = false;
			scaledHealth = 120;
			reload = 80f;
			rotateSpeed = 2.5f;
			recoil = 5f;
			cooldownTime = 300f;
			shootSound = Sounds.artillery;
			coolant = consumeCoolant(0.2f);
		}};
		minigun = new MinigunTurret("minigun") {{
			requirements(Category.turret, with(Items.copper, 350, Items.graphite, 300, Items.titanium, 150, Items.plastanium, 175, Items.surgeAlloy, 120));
			ammo(Items.copper, new BasicBulletType(11f, 19f) {{
				width = 5f;
				height = 7f;
				lifetime = 25f;
				ammoMultiplier = 2;
			}}, Items.graphite, new BasicBulletType(13f, 37f) {{
				width = 5.5f;
				height = 9f;
				reloadMultiplier = 0.6f;
				ammoMultiplier = 4;
				lifetime = 23f;
			}}, Items.pyratite, new BasicBulletType(13f, 24f) {{
				width = 5f;
				height = 8f;
				frontColor = Pal.lightishOrange;
				backColor = Pal.lightOrange;
				status = StatusEffects.burning;
				hitEffect = new MultiEffect(Fx.hitBulletSmall, Fx.fireHit);
				homingPower = 0.07f;
				reloadMultiplier = 1.3f;
				ammoMultiplier = 5;
				lifetime = 23f;
				makeFire = true;
			}}, Items.silicon, new BasicBulletType(12f, 21f) {{
				width = 5f;
				height = 8f;
				homingPower = 0.07f;
				reloadMultiplier = 1.3f;
				ammoMultiplier = 5;
				lifetime = 24f;
			}}, Items.thorium, new BasicBulletType(15f, 47f) {{
				width = 6f;
				height = 11f;
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				ammoMultiplier = 4f;
				lifetime = 20f;
			}}, Itemsf.uranium, new BasicBulletType(17f, 65f) {{
				width = 7f;
				height = 13f;
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				status = StatusEffects.melting;
				ammoMultiplier = 6f;
				lifetime = 18f;
			}});
			size = 4;
			range = 280f;
			maxSpeed = 27f;
			scaledHealth = 150;
			shootCone = 35f;
			shootSound = Sounds.shootBig;
			targetAir = targetGround = true;
			recoil = 3f;
			recoilTime = 90f;
			cooldownTime = 10f;
			inaccuracy = 2f;
			shootEffect = smokeEffect = Fx.none;
			heatColor = Pal.turretHeat;
			barX = 4f;
			barY = -10f;
			barStroke = 1f;
			barLength = 9f;
			shoot = new ShootBarrel() {{
				shots = 2;
				barrels = new float[]{
						-4f, 0f, 0f,
						4f, 0f, 0f
				};
			}};
			ammoUseEffect = Fxf.casing(32f);
		}};
		spike = new ItemTurret("spike") {{
			requirements(Category.turret, with(Items.copper, 30, Items.lead, 60, Items.graphite, 40, Items.titanium, 50));
			health = 960;
			range = 200f;
			smokeEffect = Fx.shootBigSmoke;
			coolant = consumeCoolant(0.1f);
			shoot = new ShootSpread() {{
				shots = 12;
				shotDelay = 2f;
				spread = 0.55f;
			}};
			reload = 90f;
			recoil = 3f;
			shootCone = 30f;
			inaccuracy = 4f;
			size = 2;
			shootSound = Sounds.shootSnap;
			shake = 1f;
			ammo(Items.titanium, new BasicBulletType(5f, 24f) {{
				width = 8f;
				height = 25f;
				hitColor = backColor = lightColor = trailColor = Items.titanium.color.cpy().lerp(Color.white, 0.1f);
				frontColor = backColor.cpy().lerp(Color.white, 0.35f);
				hitEffect = Fxf.crossBlast(hitColor, height + width);
				shootEffect = despawnEffect = Fxf.square(hitColor, 20f, 3, 12f, 2f);
				ammoMultiplier = 8;
			}}, Items.plastanium, new BasicBulletType(5f, 26f) {{
				width = 8f;
				height = 25f;
				fragBullets = 4;
				fragBullet = new BasicBulletType(2f, 26f) {{
					width = 3f;
					lifetime = 10f;
					height = 12f;
					ammoMultiplier = 12;
					hitColor = backColor = lightColor = trailColor = Items.plastanium.color.cpy().lerp(Color.white, 0.1f);
					frontColor = backColor.cpy().lerp(Color.white, 0.35f);
					hitEffect = Fxf.lightningHitSmall(backColor);
					shootEffect = despawnEffect = Fxf.square45_4_45;
				}};
				fragAngle = 130f;
				fragVelocityMax = 1.1f;
				fragVelocityMin = 0.5f;
				fragLifeMax = 1.25f;
				fragLifeMin = 0.25f;
				ammoMultiplier = 12;
				hitColor = backColor = lightColor = trailColor = Items.plastanium.color.cpy().lerp(Color.white, 0.1f);
				frontColor = backColor.cpy().lerp(Color.white, 0.35f);
				hitEffect = Fxf.crossBlast(hitColor, height + width);
				shootEffect = despawnEffect = Fxf.square(hitColor, 20f, 3, 20f, 2f);
			}}, Items.thorium, new BasicBulletType(5f, 38f) {{
				width = 8f;
				height = 25f;
				status = StatusEffects.shocked;
				statusDuration = 15f;
				ammoMultiplier = 12;
				lightningColor = hitColor = backColor = lightColor = trailColor = Items.thorium.color.cpy().lerp(Color.white, 0.1f);
				frontColor = backColor.cpy().lerp(Color.white, 0.35f);
				hitEffect = Fxf.crossBlast(hitColor, height + width);
				shootEffect = despawnEffect = Fxf.square(hitColor, 20f, 3, 20f, 2f);
			}}, Items.pyratite, new BasicBulletType(5f, 18f) {{
				width = 8f;
				height = 25f;
				incendAmount = 4;
				incendChance = 0.25f;
				incendSpread = 12f;
				status = StatusEffects.burning;
				statusDuration = 15f;
				ammoMultiplier = 12f;
				hitColor = backColor = lightColor = trailColor = Items.pyratite.color.cpy().lerp(Color.white, 0.1f);
				frontColor = backColor.cpy().lerp(Color.white, 0.35f);
				hitEffect = Fxf.crossBlast(hitColor, height + width);
				despawnEffect = Fx.blastExplosion;
				shootEffect = Fxf.square(hitColor, 20f, 3, 20f, 2f);
			}}, Items.blastCompound, new BasicBulletType(5f, 22f) {{
				width = 8f;
				height = 25f;
				status = StatusEffects.blasted;
				statusDuration = 15f;
				splashDamageRadius = 12f;
				splashDamage = 36f;
				ammoMultiplier = 8;
				hitColor = backColor = lightColor = trailColor = Items.blastCompound.color.cpy().lerp(Color.white, 0.1f);
				frontColor = backColor.cpy().lerp(Color.white, 0.35f);
				hitEffect = Fxf.crossBlast(hitColor, height + width);
				despawnEffect = Fx.blastExplosion;
				shootEffect = Fxf.square(hitColor, 20f, 3, 20f, 2f);
			}});
			limitRange();
			maxAmmo = 120;
			ammoPerShot = 12;
		}};
		fissure = new ItemTurret("fissure") {{
			requirements(Category.turret, with(Items.titanium, 110, Items.thorium, 90, Items.graphite, 150, Items.silicon, 120, Items.plastanium, 80));
			ammo(Items.titanium, new BasicBulletType(8f, 45f) {{
				lifetime = 48f;
				width = 8f;
				height = 42f;
				shrinkX = 0;
				trailWidth = 1.7f;
				trailLength = 9;
				trailColor = backColor = hitColor = lightColor = lightningColor = Items.titanium.color;
				frontColor = backColor.cpy().lerp(Color.white, 0.35f);
				shootEffect = Fxf.square(backColor, 45f, 5, 38, 4);
				smokeEffect = Fx.shootBigSmoke;
				despawnEffect = Fxf.square(backColor, 85f, 5, 52, 5);
				hitEffect = Fxf.hitSparkLarge;
				ammoMultiplier = 4;
			}}, Items.thorium, new BasicBulletType(8f, 65f) {{
				lifetime = 48f;
				width = 8f;
				height = 42f;
				shrinkX = 0;
				trailWidth = 1.7f;
				trailLength = 9;
				trailColor = backColor = hitColor = lightColor = lightningColor = Items.thorium.color;
				frontColor = backColor.cpy().lerp(Color.white, 0.35f);
				shootEffect = Fxf.square(backColor, 45f, 5, 38, 4);
				smokeEffect = Fx.shootBigSmoke;
				despawnEffect = Fxf.square(backColor, 85f, 5, 52, 5);
				hitEffect = Fxf.hitSparkLarge;
				ammoMultiplier = 4;
			}}, Items.plastanium, new BasicBulletType(8f, 60f) {{
				lifetime = 48f;
				width = 8f;
				height = 42f;
				shrinkX = 0;
				splashDamage = 25f;
				splashDamageRadius = 20f;
				trailWidth = 1.7f;
				trailLength = 9;
				trailColor = backColor = hitColor = lightColor = lightningColor = Pal.plastanium;
				frontColor = backColor.cpy().lerp(Color.white, 0.35f);
				shootEffect = Fxf.square(backColor, 45f, 5, 38, 4);
				smokeEffect = Fx.shootBigSmoke;
				despawnEffect = Fxf.square(backColor, 85f, 5, 52, 5);
				hitEffect = Fxf.hitSparkLarge;
				ammoMultiplier = 4;
			}}, Items.pyratite, new BasicBulletType(8f, 40f) {{
				lifetime = 48f;
				width = 8f;
				height = 42f;
				shrinkX = 0;
				splashDamage = 35f;
				splashDamageRadius = 25f;
				trailWidth = 1.7f;
				trailLength = 9;
				status = StatusEffects.burning;
				trailColor = backColor = hitColor = lightColor = lightningColor = Items.pyratite.color;
				frontColor = backColor.cpy().lerp(Color.white, 0.35f);
				shootEffect = Fxf.square(backColor, 45f, 5, 38, 4);
				smokeEffect = Fx.shootBigSmoke;
				despawnEffect = Fxf.square(backColor, 85f, 5, 52, 5);
				hitEffect = Fxf.hitSparkLarge;
				ammoMultiplier = 4;
			}}, Items.blastCompound, new BasicBulletType(8f, 40f) {{
				lifetime = 48f;
				width = 8f;
				height = 42f;
				shrinkX = 0;
				splashDamage = 55f;
				splashDamageRadius = 35f;
				trailWidth = 1.7f;
				trailLength = 9;
				status = StatusEffects.blasted;
				trailColor = backColor = hitColor = lightColor = lightningColor = Items.blastCompound.color;
				frontColor = backColor.cpy().lerp(Color.white, 0.35f);
				shootEffect = Fxf.square(backColor, 45f, 5, 38, 4);
				smokeEffect = Fx.shootBigSmoke;
				despawnEffect = Fxf.square(backColor, 85f, 5, 52, 5);
				hitEffect = Fxf.hitSparkLarge;
				ammoMultiplier = 4;
			}}, Itemsf.chromium, new BasicBulletType(8f, 125f) {{
				lifetime = 48f;
				width = 8f;
				height = 42f;
				shrinkX = 0;
				pierce = true;
				pierceCap = 3;
				pierceArmor = true;
				trailWidth = 1.7f;
				trailLength = 9;
				status = StatusEffectsf.breached;
				trailColor = backColor = hitColor = lightColor = lightningColor = Palf.chromiumGrey;
				frontColor = backColor.cpy().lerp(Color.white, 0.35f);
				shootEffect = Fxf.square(backColor, 45f, 5, 38, 4);
				smokeEffect = Fx.shootBigSmoke;
				despawnEffect = Fxf.square(backColor, 85f, 5, 52, 5);
				hitEffect = Fxf.hitSparkLarge;
				ammoMultiplier = 4;
			}}, Items.phaseFabric, new BasicBulletType(8f, 65f) {{
				lifetime = 48f;
				width = 8f;
				height = 42f;
				shrinkX = 0;
				trailWidth = 1.7f;
				trailLength = 9;
				trailColor = backColor = hitColor = lightColor = lightningColor = Pal.accent;
				frontColor = Color.white;
				shootEffect = Fxf.square(backColor, 45f, 5, 38, 4);
				smokeEffect = Fx.shootBigSmoke;
				splashDamage = damage;
				splashDamageRadius = 32f;
				despawnEffect = hitEffect = new MultiEffect(Fxf.circleOut(backColor, splashDamageRadius * 1.25f), Fxf.hitSparkLarge);
				ammoMultiplier = 6;
				reloadMultiplier = 0.9f;
				status = StatusEffects.melting;
				statusDuration = 120f;
			}});
			size = 3;
			health = 1420;
			reload = 12f;
			inaccuracy = 0.75f;
			recoil = 0.5f;
			coolant = consumeCoolant(0.2f);
			drawer = new DrawTurret() {{
				parts.add(new RegionPart("-shooter") {{
					under = true;
					outline = true;
					moveY = -3f;
					progress = PartProgress.recoil;
				}});
			}};
			coolantMultiplier = 2f;
			velocityRnd = 0.075f;
			unitSort = UnitSorts.weakest;
			range = 360f;
			shootSound = Soundsf.fissure;
			shoot = new ShootMulti(new ShootPattern(), new ShootBarrel() {{
				barrels = new float[]{-6.5f, 3f, 0f};
			}}, new ShootBarrel() {{
				barrels = new float[]{6.5f, 3f, 0f};
			}});
		}};
		hurricane = new PowerTurret("hurricane") {{
			requirements(Category.turret, with(Items.lead, 80, Items.graphite, 100, Items.silicon, 250, Items.plastanium, 120, Items.phaseFabric, 150));
			size = 3;
			health = 960;
			range = 300f;
			reload = 12f;
			shoot = new ShootAlternate() {{
				spread = 7f;
			}};
			shootCone = 24f;
			shootSound = Sounds.spark;
			shootType = new PositionLightningBulletType(50f) {{
				lightningColor = hitColor = Pal.techBlue;
				maxRange = rangeOverride = 250f;
				hitEffect = Fxf.hitSpark;
				smokeEffect = Fx.shootBigSmoke2;
			}};
			warmupMaintainTime = 120f;
			rotateSpeed = 3f;
			coolant = new ConsumeCoolant(0.15f);
			consumePowerCond(35f, TurretBuild::isActive);
			canOverdrive = false;
		}};
		judgement = new ContinuousTurret("judgement") {{
			requirements(Category.turret, with(Items.silicon, 1200, Items.metaglass, 400, Items.plastanium, 800, Items.surgeAlloy, 650, Itemsf.syntheticJade, 350, Itemsf.heavyAlloy, 600));
			shootType = new PointLaserBulletType() {{
				damage = 100f;
				hitEffect = Fxf.hitSpark;
				beamEffect = Fx.none;
				beamEffectInterval = 0;
				buildingDamageMultiplier = 0.75f;
				damageInterval = 1;
				color = hitColor = Pal.techBlue;
				sprite = "laser-white";
				status = StatusEffects.slow;
				statusDuration = 60;
				oscScl /= 1.77f;
				oscMag /= 1.33f;
				hitShake = 2;
				range = 75 * 8;
				trailLength = 8;
				buildCostMultiplier = 0.8f;
			}
				public final Color tmpColor = new Color();
				public final Color from = color, to = Pal.techBlue;
				public final float chargeReload = 65f;
				public final float lerpReload = 10f;

				public boolean charged(Bullet b) {
					return b.fdata > chargeReload;
				}

				public Color getColor(Bullet b) {
					return tmpColor.set(from).lerp(to, warmup(b));
				}

				public float warmup(Bullet b) {
					return Mathf.curve(b.fdata, chargeReload - lerpReload / 2f, chargeReload + lerpReload / 2f);
				}

				@Override
				public void update(Bullet b) {
					super.update(b);

					float maxDamageMultiplier = 1.5f;
					b.damage = damage * (1 + warmup(b) * maxDamageMultiplier);

					boolean cool = false;

					if (b.data == null) cool = true;
					else if (b.data instanceof Healthc h && (!h.isValid() || !h.within(b.aimX, b.aimY, ((Sized) h).hitSize() + 4))) {
						b.data = null;
						cool = true;
					}

					if (cool) b.fdata = Mathf.approachDelta(b.fdata, 0, 1);
					else b.fdata = Math.min(b.fdata, chargeReload + lerpReload / 2f + 1f);

					if (charged(b)) {
						if (!headless && b.timer(3, 3)) {
							PositionLightning.createEffect(b, Tmp.v1.set(b.aimX, b.aimY), getColor(b), 1, 2);
							if (Mathf.chance(0.25)) Fxf.hitSparkLarge.at(b.x, b.y, tmpColor);
						}

						if (b.timer(4, 2.5f)) {
							Lightning.create(b, getColor(b), b.damage() / 2f, b.aimX, b.aimY, b.rotation() + Mathf.range(34f), Mathf.random(5, 12));
						}
					}
				}

				@Override
				public void draw(Bullet b) {
					float darkenPartWarmup = warmup(b);
					float stroke = b.fslope() * (1f - oscMag + Mathf.absin(Time.time, oscScl, oscMag)) * (darkenPartWarmup + 1) * 5;

					if (trailLength > 0 && b.trail != null) {
						float z = Draw.z();
						Draw.z(z - 0.0001f);
						b.trail.draw(getColor(b), stroke);
						Draw.z(z);
					}

					Draw.color(getColor(b));
					Drawn.basicLaser(b.x, b.y, b.aimX, b.aimY, stroke);
					Draw.color(Color.white);
					Drawn.basicLaser(b.x, b.y, b.aimX, b.aimY, stroke * 0.64f * (2 + darkenPartWarmup) / 3f);

					Drawf.light(b.aimX, b.aimY, b.x, b.y, stroke, tmpColor, 0.76f);
					Drawf.light(b.x, b.y, stroke * 4, tmpColor, 0.76f);
					Drawf.light(b.aimX, b.aimY, stroke * 3, tmpColor, 0.76f);

					Draw.color(tmpColor);
					if (charged(b)) {
						float qW = Mathf.curve(warmup(b), 0.5f, 0.7f);

						for (int s : Mathf.signs) {
							Drawf.tri(b.x, b.y, 6, 21 * qW, 90 * s + Time.time * 1.8f);
						}

						for (int s : Mathf.signs) {
							Drawf.tri(b.x, b.y, 7.2f, 25 * qW, 90 * s + Time.time * -1.1f);
						}
					}

					int particles = 44;
					float particleLife = 74f;
					float particleLen = 7.5f;
					Rand rand = new Rand(b.id);

					float base = (Time.time / particleLife);
					for (int i = 0; i < particles; i++) {
						float fin = (rand.random(1f) + base) % 1f, fout = 1f - fin, fslope = Fxf.fslope(fin);
						float len = rand.random(particleLen * 0.7f, particleLen * 1.3f) * Mathf.curve(fin, 0.2f, 0.9f) * (darkenPartWarmup / 2.5f + 1);
						float centerDeg = rand.random(Mathf.pi);

						Tmp.v1.trns(b.rotation(), Interp.pow3In.apply(fin) * rand.random(44, 77) - rand.range(11) - 8, (((rand.random(22, 35) * (fout + 1) / 2 + 2) / (3 * fin / 7 + 1.3f) - 1) + rand.range(4)) * Mathf.cos(centerDeg));
						float angle = Mathf.slerp(Tmp.v1.angle() - 180, b.rotation(), Interp.pow2Out.apply(fin));
						Tmp.v1.scl(darkenPartWarmup / 3.7f + 1);
						Tmp.v1.add(b);

						Draw.color(Tmp.c2.set(tmpColor), Color.white, fin * 0.7f);
						Lines.stroke(Mathf.curve(fslope, 0, 0.42f) * 1.4f * b.fslope() * Mathf.curve(fin, 0, 0.6f));
						Lines.lineAngleCenter(Tmp.v1.x, Tmp.v1.y, angle, len);
					}

					if (darkenPartWarmup > 0.005f) {
						tmpColor.lerp(Color.white, 0.86f);
						Draw.color(tmpColor);
						Drawn.basicLaser(b.x, b.y, b.aimX, b.aimY, stroke * 0.55f * darkenPartWarmup);
						Draw.z(Layerf.effectBottom);
						Drawn.basicLaser(b.x, b.y, b.aimX, b.aimY, stroke * 0.6f * darkenPartWarmup);
						Draw.z(Layer.bullet);
					}

					Draw.reset();
				}

				@Override
				public void hit(Bullet b, float x, float y) {
					if (Mathf.chance(0.4)) hitEffect.at(x, y, b.rotation(), getColor(b));
					hitSound.at(x, y, hitSoundPitch, hitSoundVolume);

					Effect.shake(hitShake, hitShake, b);

					if (fragOnHit) {
						createFrags(b, x, y);
					}
				}

				@Override
				public void hitEntity(Bullet b, Hitboxc entity, float health) {
					if (entity instanceof Healthc h) {
						if (charged(b)) {
							h.damagePierce(b.damage);
						} else {
							h.damage(b.damage);
						}
					}

					if (charged(b) && entity instanceof Unit unit) {
						unit.apply(status, statusDuration);
					}

					if (entity == b.data) b.fdata += Time.delta;
					else b.fdata = 0;
					b.data = entity;
				}

				@Override
				public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
					super.hitTile(b, build, x, y, initialHealth, direct);
					if (build == b.data) b.fdata += Time.delta;
					else b.fdata = 0;
					b.data = build;
				}
			};
			drawer = new DrawTurret() {{
				parts.addAll(new RegionPart("-charger") {{
					mirror = true;
					under = true;
					moveRot = 10;
					moveX = 4.677f;
					moveY = 6.8f;
				}}, new RegionPart("-side") {{
					mirror = true;
					under = true;
					moveRot = 10;
					moveX = 2.75f;
					moveY = 2;
				}}, new RegionPart("-barrel") {{
					moveY = -7.5f;
					progress = progress.curve(Interp.pow2Out);
				}});
			}};
			shootSound = Sounds.none;
			loopSoundVolume = 1f;
			loopSound = Sounds.laserbeam;
			shootWarmupSpeed = 0.08f;
			shootCone = 360f;
			aimChangeSpeed = 1.75f;
			rotateSpeed = 1.45f;
			canOverdrive = false;
			shootY = 16f;
			minWarmup = 0.8f;
			warmupMaintainTime = 45;
			shootWarmupSpeed /= 2;
			outlineColor = Pal.darkOutline;
			size = 5;
			range = 420f;
			scaledHealth = 300;
			armor = 10f;
			unitSort = UnitSorts.strongest;
			consumePower(26f);
			consumeLiquid(Liquidsf.nanoFluid, 12f / 60f);
		}};
		evilSpirits = new ItemTurret("evil-spirits") {{
			requirements(Category.turret, with(Items.copper, 400, Items.graphite, 400, Items.plastanium, 200, Itemsf.uranium, 220, Items.surgeAlloy, 280));
			ammo(Items.graphite, new BasicBulletType(7.5f, 82f) {{
				hitSize = 5f;
				width = 16.8f;
				height = 23.52f;
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				ammoMultiplier = 4;
				reloadMultiplier = 1.7f;
				knockback = 0.3f;
			}}, Items.pyratite, new BasicBulletType(8.25f, 94f) {{
				rangeChange = 5f;
				hitSize = 5f;
				width = 16.8f;
				height = 23.52f;
				frontColor = Pal.lightishOrange;
				backColor = Pal.lightOrange;
				status = StatusEffects.burning;
				hitEffect = new MultiEffect(Fx.hitBulletSmall, Fx.fireHit);
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				splashDamageRadius = 30f;
				splashDamage = 34f;
				pierce = true;
				pierceCap = 2;
				pierceBuilding = true;
				ammoMultiplier = 3;
				reloadMultiplier = 1.7f;
				knockback = 0.5f;
			}}, Items.blastCompound, new BasicBulletType(8.25f, 54f) {{
				rangeChange = 5f;
				hitSize = 5f;
				width = 16.8f;
				height = 23.52f;
				frontColor = Pal.lightPyraFlame;
				backColor = Pal.lightFlame;
				status = StatusEffects.blasted;
				hitEffect = Fx.flakExplosionBig;
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				splashDamageRadius = 47f;
				splashDamage = 116f;
				pierce = true;
				pierceCap = 2;
				pierceBuilding = true;
				ammoMultiplier = 3;
				reloadMultiplier = 1.7f;
				knockback = 0.5f;
			}}, Items.thorium, new BasicBulletType(8.75f, 123f) {{
				rangeChange = 15f;
				pierce = true;
				pierceCap = 2;
				pierceBuilding = true;
				hitSize = 5.2f;
				width = 16.8f;
				height = 23.52f;
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				reloadMultiplier = 1.7f;
				knockback = 0.4f;
			}}, Itemsf.uranium, new BasicBulletType(9.75f, 146f) {{
				rangeChange = 25f;
				pierce = true;
				pierceCap = 3;
				pierceBuilding = true;
				pierceArmor = true;
				status = StatusEffects.melting;
				hitSize = 5.4f;
				width = 16.8f;
				height = 23.52f;
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				reloadMultiplier = 1.7f;
				knockback = 0.4f;
			}});
			size = 5;
			health = 5500;
			armor = 12f;
			range = 320f;
			reload = 12f;
			coolantMultiplier = 0.5f;
			inaccuracy = 3f;
			shoot = new ShootAlternate(12f) {{
				shots = 2;
				shotDelay = 5f;
			}};
			shootSound = Sounds.shootBig;
			shootCone = 24f;
			recoil = 3f;
			rotateSpeed = 4.5f;
			coolant = consumeCoolant(1.5f);
			liquidCapacity = 120f;
			hideDetails = false;
		}};
		//turrets-erekir
		rupture = new ItemTurret("rupture") {{
			requirements(Category.turret, with(Items.graphite, 350, Items.silicon, 250, Items.beryllium, 400, Items.tungsten, 200, Items.oxide, 50));
			ammo(Items.beryllium, new BasicBulletType(12f, 72f) {{
				buildingDamageMultiplier = 0.33f;
				ammoMultiplier = 3f;
				knockback = 1.1f;
				lifetime = 56f / 3;
				width = 13f;
				height = 22f;
				pierce = pierceBuilding = true;
				pierceCap = 3;
				hitColor = backColor = trailColor = Pal.berylShot;
				frontColor = Color.white;
				trailLength = 11;
				trailWidth = 2.2f;
				hitEffect = despawnEffect = Fx.hitBulletColor;
				smokeEffect = Fx.shootBigSmoke;
				shootEffect = new MultiEffect(Fx.shootSmallColor, Fx.colorSpark);
			}}, Items.tungsten, new BasicBulletType(13f, 96.8f) {{
				buildingDamageMultiplier = 0.33f;
				ammoMultiplier = 4f;
				knockback = 1.5f;
				lifetime = 21.3f;
				rangeChange = 22f;
				width = 13f;
				height = 23f;
				pierce = pierceBuilding = true;
				pierceCap = 4;
				hitColor = backColor = trailColor = Pal.tungstenShot;
				frontColor = Color.white;
				trailLength = 11;
				trailWidth = 2.3f;
				hitEffect = despawnEffect = Fx.hitBulletColor;
				smokeEffect = Fx.shootBigSmoke;
				shootEffect = new MultiEffect(Fx.shootSmallColor, Fx.colorSpark);
			}}, Items.carbide, new BasicBulletType(16f, 337.9f) {{
				buildingDamageMultiplier = 0.33f;
				ammoMultiplier = 5f;
				reloadMultiplier = 0.2f;
				knockback = 1.5f;
				lifetime = 20.97f;
				rangeChange = 46f;
				width = 14f;
				height = 22.5f;
				pierce = pierceBuilding = true;
				pierceCap = 5;
				hitColor = backColor = trailColor = Palf.carbideShot;
				frontColor = Color.white;
				trailLength = 11;
				trailWidth = 2.4f;
				trailEffect = Fx.disperseTrail;
				trailInterval = 2f;
				hitEffect = despawnEffect = Fx.hitBulletColor;
				smokeEffect = Fx.shootBigSmoke;
				shootEffect = new MultiEffect(Fx.shootSmallColor, Fx.colorSpark);
				trailRotation = true;
				fragBullets = 3;
				fragRandomSpread = 0f;
				fragSpread = 25f;
				fragVelocityMin = 1f;
				fragBullet = new BasicBulletType(8.7f, 225.6f) {{
					width = 10f;
					height = 15f;
					lifetime = 8.55f;
					pierce = pierceBuilding = true;
					pierceCap = 2;
					hitColor = backColor = trailColor = Palf.carbideShot;
					frontColor = Color.white;
					trailLength = 11;
					trailWidth = 1.9f;
					hitEffect = despawnEffect = Fx.hitBulletColor;
					smokeEffect = Fx.shootBigSmoke;
					buildingDamageMultiplier = 0.22f;
				}};
			}});
			health = 2330;
			size = 4;
			armor = 8f;
			reload = 44f;
			range = 223.6f;
			coolantMultiplier = 3f;
			minWarmup = 0.6f;
			warmupMaintainTime = 45f;
			shootWarmupSpeed = 0.08f;
			outlineColor = Pal.darkOutline;
			drawer = new DrawTurret("reinforced-");
			shootSound = Sounds.shootAlt;
			consumeAmmoOnce = false;
			shoot = new ShootBarrel() {{
				shots = 6;
				shotDelay = 13;
				barrels = new float[]{
						0f, 12f, 0f,
						6.75f, 0f, 0f,
						-6.75f, 0f, 0f
				};
			}};
			shootY = 0f;
			ammoUseEffect = Fx.none;
			inaccuracy = rotateSpeed = shake = 1f;
			maxAmmo = 60;
			ammoPerShot = 2;
			recoilTime = 22f;
			recoil = 3f;
			coolant = consume(new ConsumeLiquid(Liquids.water, 0.5f));
			buildCostMultiplier = 0.8f;
			squareSprite = false;
		}};
		//sandbox
		unitIniter = new UnitIniter("unit-initer");
		reinforcedItemSource = new ItemSource("reinforced-item-source") {{
			requirements(Category.distribution, BuildVisibility.sandboxOnly, with());
			health = 1000;
			buildType = () -> new ItemSourceBuild() {
				@Override
				public boolean dump(Item item) {
					int count = 36;
					items.set(item, count);
					while (count > 0 && super.dump(item)) {
						count--;
					}
					return super.dump(item);
				}
			};
		}};
		reinforcedLiquidSource = new LiquidSource("reinforced-liquid-source") {{
			requirements(Category.liquid, BuildVisibility.sandboxOnly, with());
			health = 1000;
		}};
		reinforcedPowerSource = new PowerSource("reinforced-power-source") {{
			requirements(Category.power, BuildVisibility.sandboxOnly, with());
			health = 1000;
			powerProduction = 10000000f / 60f;
		}};
		reinforcedPayloadSource = new PayloadSourcef("reinforced-payload-source") {{
			requirements(Category.units, BuildVisibility.sandboxOnly, with());
			size = 5;
			health = 1000;
			placeableLiquid = true;
			floating = true;
		}};
		adaptiveSource = new AdaptiveSource("adaptive-source") {{
			requirements(Category.distribution, BuildVisibility.sandboxOnly, with());
			health = 1000;
			liquidCapacity = 100f;
			laserRange = 200f;
			maxNodes = 500;
			itemsPerSecond = 2000;
			autolink = drawRange = false;
		}};
		omniNode = new NodeBridge("omni-node") {{
			requirements(Category.distribution, BuildVisibility.sandboxOnly, with());
			health = 1000;
			range = 35;
			hasPower = false;
			hasLiquids = true;
			hasItems = true;
			liquidCapacity = 100f;
			itemCapacity = 20;
			outputsLiquid = true;
			transportTime = 1f;
			squareSprite = false;
		}};
		ultraAssignOverdrive = new AssignOverdrive("ultra-assign-overdrive") {{
			requirements(Category.effect, BuildVisibility.sandboxOnly, with());
			size = 2;
			health = 1000;
			hasItems = false;
			hasPower = false;
			range = 600f;
			phaseRangeBoost = 0f;
			speedBoost = 35f;
			speedBoostPhase = 0f;
			maxLink = 100;
			hasBoost = false;
			strokeOffset = -0.05f;
			strokeClamp = 0.06f;
		}};
		teamChanger = new CoreBlock("team-changer") {{
			requirements(Category.effect, BuildVisibility.sandboxOnly, with());
			size = 3;
			health = 1000;
			itemCapacity = 10000;
			unitCapModifier = 1;
			configurable = true;
			buildType = () -> new CoreBuild() {
				@Override
				public void damage(float damage) {}

				@Override
				public float handleDamage(float amount) {
					return 0f;
				}

				@Override
				public void buildConfiguration(Table table) {
					var g = new ButtonGroup<>();
					Table cont = new Table();
					cont.defaults().size(55);
					for (Team bt : Utils.baseTeams) {
						ImageButton button = cont.button(((TextureRegionDrawable) Tex.whiteui).tint(bt.color), Styles.clearTogglei, 35, () -> {
						}).group(g).get();
						button.changed(() -> {
							if (button.isChecked()) {
								if (player.team() == team) {
									configure(bt.id);
								} else deselect();
							}
						});
						button.update(() -> button.setChecked(team == bt));
					}
					var pane = new ScrollPane(cont, Styles.smallPane);
					pane.setScrollingDisabled(true, false);
					pane.setOverscroll(false, false);
					table.add(pane).maxHeight(Scl.scl(40 * 2)).left();
					table.row();
				}

				@Override
				public void configured(Unit builder, Object value) {
					if (builder != null && builder.isPlayer() && value instanceof Number number) {
						Team t = Team.get(number.intValue());
						builder.team = t;
						builder.getPlayer().team(t);

						onRemoved();
						team = t;
						onProximityUpdate();
					}
				}
			};
			unitType = UnitTypes.evoke;
		}
			@Override
			public boolean canBreak(Tile tile) {
				return state.teams.cores(tile.team()).size > 1;
			}

			@Override
			public boolean canReplace(Block other) {
				return other.alwaysReplace;
			}

			@Override
			public boolean canPlaceOn(Tile tile, Team team, int rotation) {
				return true;
			}

			@Override
			public void placeBegan(Tile tile, Block previous) {}

			@Override
			public void beforePlaceBegan(Tile tile, Block previous) {}

			@Override
			public void drawPlace(int x, int y, int rotation, boolean valid) {}

			@Override
			protected TextureRegion[] icons() {
				return teamRegion.found() ? new TextureRegion[]{region, teamRegions[Team.sharded.id]} : new TextureRegion[]{region};
			}
		};
		barrierProjector = new BaseShield("barrier-projector") {{
			requirements(Category.effect, BuildVisibility.sandboxOnly, with());
			health = 1000;
			size = 2;
			hasPower = false;
			radius = 300f;
			buildType = () -> new BaseShieldBuild() {
				@Override
				public void damage(float damage) {}

				@Override
				public float handleDamage(float amount) {
					return 0f;
				}
			};
		}};
		nihility = new ForceProjector("nihility") {{
			requirements(Category.effect, BuildVisibility.sandboxOnly, with());
			health = 1000;
			size = 2;
			hasPower = false;
			radius = 220f;
			buildType = () -> new ForceBuild() {
				@Override
				public void updateTile() {
					boolean phaseValid = itemConsumer != null && itemConsumer.efficiency(this) > 0f;

					phaseHeat = Mathf.lerpDelta(phaseHeat, Mathf.num(phaseValid), 0.1f);

					radscl = Mathf.lerpDelta(radscl, broken ? 0f : warmup, 0.05f);

					if (phaseValid && !broken && timer(timerUse, phaseUseTime) && efficiency > 0f) {
						consume();
					}

					if (Mathf.chanceDelta(buildup / shieldHealth * 0.1f)) {
						Fx.reactorsmoke.at(x + Mathf.range(tilesize / 2), y + Mathf.range(tilesize / 2));
					}

					warmup = Mathf.lerpDelta(warmup, efficiency, 0.1f);

					if (buildup > 0f) {
						float scale = !broken ? cooldownNormal : cooldownBrokenBase;

						if (coolantConsumer != null) {
							if (coolantConsumer.efficiency(this) > 0f) {
								coolantConsumer.update(this);
								scale *= (cooldownLiquid * (1f + (liquids.current().heatCapacity - 0.4f) * 0.9f));
							}
						}

						buildup -= delta() * scale;
					}

					if (broken && buildup <= 0f) {
						broken = false;
					}

					if (buildup >= shieldHealth + phaseShieldBoost && !broken) {
						broken = true;
						buildup = shieldHealth;
						Fx.shieldBreak.at(x, y, realRadius(), team.color);
					}

					if (hit > 0f) {
						hit -= 1f / 5f * Time.delta;
					}

					float realRadius = realRadius();

					if (realRadius > 0 && !broken) {
						Groups.unit.intersect(x - realRadius, y - realRadius, realRadius * 2f, realRadius * 2f, trait -> {
							if (trait.team != team
									&& Intersector.isInsideHexagon(x, y, realRadius() * 2f, trait.x, trait.y)) {
								trait.remove();
							}
						});
					}
				}

				@Override
				public void damage(float damage) {}

				@Override
				public float handleDamage(float amount) {
					return 0f;
				}
			};
			itemConsumer = consumeItem(Items.phaseFabric).boost();
		}};
		invincibleWall = new IndestructibleWall("invincible-wall") {{
			requirements(Category.defense, BuildVisibility.sandboxOnly, with());
			health = 1000;
			size = 1;
			absorbLasers = insulated = true;
			unlocked = false;
		}};
		invincibleWallLarge = new IndestructibleWall("invincible-wall-large") {{
			requirements(Category.defense, BuildVisibility.sandboxOnly, with());
			health = 4000;
			size = 2;
			absorbLasers = insulated = true;
			unlocked = false;
		}};
		invincibleWallHuge = new IndestructibleWall("invincible-wall-huge") {{
			requirements(Category.defense, BuildVisibility.sandboxOnly, with());
			health = 9000;
			size = 3;
			absorbLasers = insulated = true;
			unlocked = false;
		}};
		invincibleWallGigantic = new IndestructibleWall("invincible-wall-gigantic") {{
			requirements(Category.defense, BuildVisibility.sandboxOnly, with());
			health = 16000;
			size = 4;
			absorbLasers = insulated = true;
			unlocked = false;
		}};
		mustDieTurret = new PlatformTurret("must-die-turret") {{
			requirements(Category.turret, BuildVisibility.sandboxOnly, with());
			health = 1000;
			range = 500f;
			inaccuracy = 25f;
			rotateSpeed = 20f;
			targetInterval = 0f;
			shootCone = 80f;
			shootSound = Sounds.shootBig;
			shootType = new BasicBulletType(6f, 114514f) {{
				pierce = true;
				pierceCap = 6;
				pierceBuilding = false;
				hitSize = 8;
				healPercent = 1000;
				homingPower = 0.3f;
				homingRange = 240;
				splashDamage = damage;
				splashDamageRadius = 30;
				shootEffect = Fx.none;
				hitEffect = new Effect(8f, e -> {
					Draw.color(Color.black, Color.purple, e.fin());
					Lines.stroke(0.5f + e.fout());
					Lines.circle(e.x, e.y, e.fin() * 30f);
				});
				despawnEffect = new Effect(8f, e -> {
					Draw.color(Color.black, Color.purple, e.fin());
					Lines.stroke(0.5f + e.fout());
					Lines.circle(e.x, e.y, e.fin() * 5f);
				});
				fragBullet = new BasicBulletType(3.5f, 114514f) {{
					pierce = true;
					pierceCap = 6;
					pierceBuilding = false;
					healPercent = 500f;
					homingPower = 0.3f;
					homingRange = 50f;
					splashDamage = 3f;
					splashDamageRadius = 10f;
					hittable = false;
					hitEffect = new Effect(8f, e -> {
						Draw.color(Color.black, Color.purple, e.fin());
						Lines.stroke(0.5f + e.fout());
						Lines.circle(e.x, e.y, e.fin() * 10);
					});
					despawnEffect = new Effect(8f, e -> {
						Draw.color(Color.black, Color.purple, e.fin());
						Lines.stroke(0.5f + e.fout());
						Lines.circle(e.x, e.y, e.fin() * 5);
					});
					lifetime = 35f;
					shootEffect = Fx.none;
				}
					@Override
					public void hitEntity(Bullet b, Hitboxc entity, float health) {
						super.hitEntity(b, entity, health);
						if (entity instanceof Healthc h && !h.dead()) {
							Call.unitDestroy(h.id());
						}
					}

					@Override
					public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
						super.hitTile(b, build, x, y, initialHealth, direct);
						if (build != null && build.team != b.team) {
							build.killed();
						}
					}

					@Override
					public void draw(Bullet b) {
						Draw.color(Color.purple);
						Drawf.tri(b.x, b.y, 4, 8, b.rotation());
						Drawf.tri(b.x, b.y, 4, 12, b.rotation() - 180);
						Draw.reset();
					}

					@Override
					public void update(Bullet b) {
						if (homingPower > 0.0001f && b.time > 25f) {
							Teamc target = Units.closestTarget(b.team, b.x, b.y, homingRange, e -> (e.isGrounded() && collidesGround) || (e.isFlying() && collidesAir), t -> collidesGround);

							if (target != null) {
								b.vel.setAngle(Mathf.slerpDelta(b.rotation(), b.angleTo(target), homingPower));
							}
						}
					}
				};
				fragBullets = 6;
				lifetime = 110;
			}
				@Override
				public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
					super.hitTile(b, build, x, y, initialHealth, direct);
					if (build != null && build.team != b.team) {
						build.killed();
					}
				}

				@Override
				public void draw(Bullet b) {
					Draw.color(Color.purple);
					Drawf.tri(b.x, b.y, 8, 16, b.rotation());
					Drawf.tri(b.x, b.y, 8, 30 * Math.min(1, b.time / speed * 0.8f + 0.2f), b.rotation() - 180);
					Draw.reset();
				}

				@Override
				public void update(Bullet b) {
					if (homingPower > 0.0001f && b.time > 25f) {
						Teamc target = Units.closestTarget(b.team, b.x, b.y, homingRange, e -> (e.isGrounded() && collidesGround) || (e.isFlying() && collidesAir), t -> collidesGround);

						if (target != null) {
							b.vel.setAngle(Mathf.slerpDelta(b.rotation(), b.angleTo(target), homingPower));
						}
					}

					if (b.timer.get(1, 1)) {
						Draw.color(Color.black, Color.purple, Math.max(0, b.fout() * 2 - 1));
						Drawf.tri(b.x, b.y, 8 * b.fout(), 16, b.rotation());
						Drawf.tri(b.x, b.y, 8 * b.fout(), 30 * Math.min(1, b.time / 8 * 0.8f + 0.2f), b.rotation() - 180);
					}
				}
			};
			buildType = () -> new PlatformTurretBuild() {
				@Override
				public void damage(float damage) {}

				@Override
				public float handleDamage(float amount) {
					return 0;
				}
			};
		}};
		oneShotTurret = new PlatformTurret("one-shot-turret") {{
			requirements(Category.turret, BuildVisibility.sandboxOnly, with());
			size = 2;
			health = 1000;
			range = 600f;
			reload = 30f;
			inaccuracy = 0f;
			rotateSpeed = 22f;
			targetInterval = 0f;
			shootCone = 2f;
			shootSound = Sounds.shootBig;
			shootType = new BasicBulletType(24f, 1145141f) {{
				splashDamage = damage;
				hitSize = 6f;
				width = 9f;
				height = 45f;
				lifetime = 26f;
				hittable = false;
				inaccuracy = 0f;
				despawnEffect = Fx.hitBulletSmall;
				keepVelocity = false;
			}
				@Override
				public void hitEntity(Bullet b, Hitboxc entity, float health) {
					if (entity instanceof Healthc h && !h.dead()) {
						Call.unitDestroy(h.id());
					}
				}

				@Override
				public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
					super.hitTile(b, build, x, y, initialHealth, direct);
					if (build != null && build.team != b.team) {
						build.killed();
					}
				}
			};
			coolant = consumeCoolant(0.6f);
			coolantMultiplier = 10f;
			buildType = () -> new PlatformTurretBuild() {
				@Override
				public void damage(float damage) {}

				@Override
				public float handleDamage(float amount) {
					return 0f;
				}
			};
		}};
		pointTurret = new PlatformTurret("point-turret") {{
			requirements(Category.turret, BuildVisibility.sandboxOnly, with());
			size = 2;
			health = 1000;
			range = 600f;
			reload = 30f;
			inaccuracy = 0f;
			rotateSpeed = 22f;
			targetInterval = 0f;
			shootCone = 2f;
			shootSound = Sounds.shootBig;
			shootType = new PointBulletType() {{
				damage = 114514f;
				speed = 0.0001f;
				hittable = false;
				inaccuracy = 0f;
				keepVelocity = false;
				trailSpacing = 20f;
				hitShake = 0.3f;
				despawnEffect = hitEffect = new Effect(8f, e -> {
					Draw.color(Pal.spore);
					Lines.stroke(e.fout() * 1.5f);

					Angles.randLenVectors(e.id, 8, e.finpow() * 22f, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 4f + 1f));
				});
				trailEffect = new Effect(12f, e -> {
					float fx = Angles.trnsx(e.rotation, 24f), fy = Angles.trnsy(e.rotation, 24f);
					Lines.stroke(3f * e.fout(), Pal.spore);
					Lines.line(e.x, e.y, e.x + fx, e.y + fy);

					Drawf.light(e.x, e.y, 60f * e.fout(), Pal.spore, 0.5f);
				});
			}
				@Override
				public void hitEntity(Bullet b, Hitboxc entity, float health) {
					super.hitEntity(b, entity, health);
					if (entity instanceof Healthc h && !h.dead()) {
						Call.unitDestroy(h.id());
					}
				}

				@Override
				public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
					super.hitTile(b, build, x, y, initialHealth, direct);
					if (build != null && build.team != b.team) {
						build.killed();
					}
				}
			};
			shootEffect = new Effect(21f, e -> {
				Draw.color(Pal.spore);
				for (int i : Mathf.signs) {
					Drawf.tri(e.x, e.y, 4 * e.fout(), 29, e.rotation + 90 * i);
				}
			});
			coolant = consumeCoolant(0.6f);
			coolantMultiplier = 10f;
			buildType = () -> new PlatformTurretBuild() {
				@Override
				public void damage(float damage) {}

				@Override
				public float handleDamage(float amount) {
					return 0f;
				}

				@Override
				protected void shoot(BulletType type) {
					if (isControlled() || logicShooting) {
						super.shoot(type);
					} else if (target != null && type instanceof PointBulletType p) {
						if (target instanceof Buildingc b) {
							b.killed();
						} else if (target instanceof Unitc u) {
							Call.unitDestroy(u.id());
						}
						totalShots += 1;
						float bulletX = x + Angles.trnsx(rotation - 90, shootX, shootY), bulletY = y + Angles.trnsy(rotation - 90, shootX, shootY);
						shootSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax));

						ammoUseEffect.at(x - Angles.trnsx(rotation, ammoEjectBack), y - Angles.trnsy(rotation, ammoEjectBack), rotation * Mathf.sign(0));

						float angle = Mathf.angle(target.getX() - bulletX, target.getY() - bulletY);
						Geometry.iterateLine(0f, bulletX, bulletY, target.getX(), target.getY(), p.trailSpacing, (x, y) -> p.trailEffect.at(x, y, angle));

						if (shootEffect != null) {
							shootEffect.at(bulletX, bulletY, angle, Pal.spore);
						}
						if (shake > 0f) {
							Effect.shake(shake, shake, this);
						}
						useAmmo();
						curRecoil = 1f;
						heat = 1f;
					} else {
						super.shoot(type);
					}
				}
			};
		}};
		nextWave = new Block("next-wave") {{
			requirements(Category.effect, BuildVisibility.sandboxOnly, with());
			size = 2;
			health = 1000;
			update = true;
			solid = false;
			targetable = false;
			hasItems = false;
			configurable = true;
			buildType = () -> new Building() {
				@Override
				public void buildConfiguration(Table table) {
					table.button(Icon.upOpen, Styles.cleari, () -> configure(0)).size(50f).tooltip(Core.bundle.get("hi-next-wave-1"));
					table.button(Icon.warningSmall, Styles.cleari, () -> configure(1)).size(50f).tooltip(Core.bundle.get("hi-next-wave-10"));
				}

				@Override
				public void configured(Unit builder, Object value) {
					if (value instanceof Number index) switch (index.intValue()) {
						case 0 -> {
							if (net.client()) Call.adminRequest(player, Packets.AdminAction.wave, null);
							else state.wavetime = 0;
						}
						case 1 -> {
							for (int i = 10; i > 0; i--) {
								if (net.client()) Call.adminRequest(player, Packets.AdminAction.wave, null);
								else logic.runWave();
							}
						}
					}
				}
			};
		}};
	}
}
