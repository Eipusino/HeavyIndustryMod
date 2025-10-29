package heavyindustry.content;

import arc.graphics.Color;
import heavyindustry.HVars;
import heavyindustry.graphics.HPal;
import heavyindustry.world.meta.HAttribute;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.Planets;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.ContinuousFlameBulletType;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.entities.bullet.LiquidBulletType;
import mindustry.entities.bullet.RailBulletType;
import mindustry.entities.bullet.ShrapnelBulletType;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;
import mindustry.type.UnitType;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ContinuousLiquidTurret;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LaserTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.distribution.Duct;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.blocks.distribution.StackConveyor;
import mindustry.world.blocks.payloads.Constructor;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.NuclearReactor;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.BeamDrill;
import mindustry.world.blocks.production.BurstDrill;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.HeatCrafter;
import mindustry.world.blocks.production.Pump;
import mindustry.world.blocks.production.Separator;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.blocks.units.UnitAssembler.AssemblerUnitPlan;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.blocks.units.UnitFactory.UnitPlan;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BuildVisibility;

/**
 * Covering the original content.
 * <p> An overwriter class designed to easily modify vanilla contents. This has to be used with <em>huge
 * responsibility</em>, we do not want to break other mods or even the vanilla Mindustry itself.
 *
 * @author Eipusino
 */
public final class HOverrides {
	/// Don't let anyone instantiate this class.
	private HOverrides() {}

	/**
	 * Instantiates all contents. Called in the main thread in {@code HeavyIndustryMod.loadContent()}.
	 * <p>Remember not to execute it a second time, I did not take any precautionary measures.
	 */
	public static void load() {
		if (HVars.isPlugin) return;

		//blocks-environment
		Blocks.stone.itemDrop = Blocks.craters.itemDrop = Blocks.charr.itemDrop = Blocks.basalt.itemDrop = Blocks.dacite.itemDrop = HItems.stone;
		Blocks.stone.playerUnmineable = Blocks.craters.playerUnmineable = Blocks.charr.playerUnmineable = Blocks.basalt.playerUnmineable = Blocks.dacite.playerUnmineable = true;
		Blocks.sandWater.itemDrop = Blocks.darksandWater.itemDrop = Blocks.darksandTaintedWater.itemDrop = Items.sand;
		Blocks.sandWater.playerUnmineable = Blocks.darksandWater.playerUnmineable = Blocks.darksandTaintedWater.playerUnmineable = true;
		Blocks.deepTaintedWater.asFloor().liquidMultiplier = 1.5f;
		Blocks.oxidationChamber.canOverdrive = Blocks.neoplasiaReactor.canOverdrive = true;
		Blocks.slag.attributes.set(Attribute.heat, 1f);
		//blocks-environment-erekir
		Blocks.yellowStonePlates.attributes.set(Attribute.water, -1f);
		Blocks.beryllicStone.attributes.set(HAttribute.arkycite, 0.7f);
		Blocks.arkyicStone.attributes.set(HAttribute.arkycite, 1f);
		//blocks-wall
		Blocks.copperWall.armor = Blocks.copperWallLarge.armor = 1f;
		Blocks.titaniumWall.armor = Blocks.titaniumWallLarge.armor = Blocks.plastaniumWall.armor = Blocks.plastaniumWallLarge.armor = 2f;
		Blocks.thoriumWall.armor = Blocks.thoriumWallLarge.armor = 8f;
		Blocks.phaseWall.armor = Blocks.phaseWallLarge.armor = 3f;
		Blocks.surgeWall.armor = Blocks.surgeWallLarge.armor = 12f;
		if (Blocks.surgeWall instanceof Wall small && Blocks.surgeWallLarge instanceof Wall large) {
			large.lightningChance = small.lightningChance = 0.1f;
			large.lightningDamage = small.lightningDamage = 25f;
		}
		//blocks-wall-erekir
		if (Blocks.reinforcedSurgeWall instanceof Wall small && Blocks.reinforcedSurgeWallLarge instanceof Wall large) small.lightningChance = large.lightningChance = 0.1f;
		//Blocks-distribution
		if (Blocks.plastaniumConveyor instanceof StackConveyor conveyor) conveyor.outputRouter = false;
		if (Blocks.massDriver instanceof MassDriver driver) driver.reload = 150f;
		//Blocks-distribution-erekir
		if (Blocks.armoredDuct instanceof Duct duct) duct.bridgeReplacement = HBlocks.armoredDuctBridge;
		//blocks-liquid
		if (Blocks.impulsePump instanceof Pump pump) pump.pumpAmount = 0.3f;
		Blocks.phaseConduit.liquidCapacity = 16f;
		//blocks-liquid-erekir
		Blocks.reinforcedLiquidRouter.liquidCapacity = 40f;
		//Blocks-drill
		if (Blocks.blastDrill instanceof Drill drill) drill.hardnessDrillMultiplier = 40f;
		//Blocks-drill-erekir
		Blocks.largeCliffCrusher.requirements = ItemStack.with(Items.graphite, 120, Items.silicon, 80, Items.oxide, 30, Items.beryllium, 100, Items.tungsten, 50);
		Blocks.largeCliffCrusher.removeConsumers(c -> c instanceof ConsumeLiquid);
		Blocks.largeCliffCrusher.consumeLiquid(Liquids.hydrogen, 0.5f / 60f);
		Blocks.impactDrill.liquidCapacity *= 2f;
		Blocks.eruptionDrill.liquidCapacity *= 2f;
		if (Blocks.impactDrill instanceof BurstDrill drill) {
			drill.drillMultipliers.put(Items.sand, 3.5f);
			drill.drillMultipliers.put(Items.scrap, 3.5f);
			drill.drillMultipliers.put(Items.copper, 3f);
			drill.drillMultipliers.put(Items.lead, 3f);
			drill.drillMultipliers.put(HItems.stone, 3f);
			drill.drillMultipliers.put(HItems.rareEarth, 3f);
			drill.drillMultipliers.put(Items.coal, 2.5f);
			drill.drillMultipliers.put(Items.titanium, 2f);
			drill.drillMultipliers.put(HItems.uranium, 0.5f);
			drill.drillMultipliers.put(HItems.chromium, 0.5f);
		}
		if (Blocks.eruptionDrill instanceof BurstDrill drill) {
			drill.drillMultipliers.put(Items.sand, 3.5f);
			drill.drillMultipliers.put(Items.scrap, 3.5f);
			drill.drillMultipliers.put(Items.copper, 3f);
			drill.drillMultipliers.put(Items.lead, 3f);
			drill.drillMultipliers.put(HItems.stone, 3f);
			drill.drillMultipliers.put(HItems.rareEarth, 3f);
			drill.drillMultipliers.put(Items.coal, 2.5f);
			drill.drillMultipliers.put(Items.titanium, 2f);
			drill.drillMultipliers.put(HItems.uranium, 0.5f);
			drill.drillMultipliers.put(HItems.chromium, 0.5f);
		}
		if (Blocks.largePlasmaBore instanceof BeamDrill drill) {
			drill.drillMultipliers.put(Items.pyratite, 1.5f);
			drill.drillMultipliers.put(Items.beryllium, 1.5f);
			drill.drillMultipliers.put(Items.graphite, 1.5f);
		}
		//blocks-power
		if (Blocks.surgeTower instanceof PowerNode node) node.maxNodes = 3;
		if (Blocks.differentialGenerator instanceof ConsumeGenerator generator) generator.powerProduction = 28f;
		if (Blocks.thoriumReactor instanceof NuclearReactor reactor) reactor.powerProduction = 18f;
		Blocks.impactReactor.liquidCapacity = 80f;
		Blocks.neoplasiaReactor.canOverdrive = true;
		//blocks-production
		Blocks.siliconSmelter.consumePower(0.25f);
		Blocks.siliconCrucible.itemCapacity = 40;
		Blocks.siliconCrucible.floating = true;
		if (Blocks.siliconCrucible instanceof AttributeCrafter crafter) {
			crafter.maxBoost = 3f;
			crafter.outputItem = new ItemStack(Items.silicon, 10);
		}
		Blocks.phaseWeaver.itemCapacity = 30;
		if (Blocks.disassembler instanceof Separator separator) separator.results = ItemStack.with(Items.copper, 1, Items.lead, 1, Items.graphite, 1, Items.titanium, 1, Items.thorium, 1);
		Blocks.disassembler.removeConsumers(c -> c instanceof ConsumeItems || c instanceof ConsumeLiquid);
		Blocks.disassembler.consumeLiquid(Liquids.slag, 0.18f);
		//blocks-production-erekir
		Blocks.oxidationChamber.canOverdrive = true;
		Blocks.heatReactor.buildVisibility = BuildVisibility.shown;
		if (Blocks.ventCondenser instanceof AttributeCrafter crafter) crafter.maxBoost = 3f;
		if (Blocks.electrolyzer instanceof GenericCrafter crafter) crafter.outputLiquids = LiquidStack.with(Liquids.ozone, 4f / 60f, Liquids.hydrogen, 8f / 60f);
		if (Blocks.cyanogenSynthesizer instanceof HeatCrafter crafter) crafter.outputLiquid = new LiquidStack(Liquids.cyanogen, 4f / 60f);
		Blocks.cyanogenSynthesizer.removeConsumers(c -> c instanceof ConsumeLiquid);
		Blocks.cyanogenSynthesizer.consumeLiquid(Liquids.arkycite, 20f / 60f);
		//blocks-defense
		Blocks.shockMine.underBullets = true;
		//blocks-storage
		Blocks.coreShard.buildVisibility = BuildVisibility.shown;
		Blocks.coreShard.health *= 2;
		Blocks.coreShard.armor = 3f;
		Blocks.coreFoundation.health *= 2;
		Blocks.coreFoundation.armor = 7f;
		Blocks.coreNucleus.health *= 2;
		Blocks.coreNucleus.armor = 11f;
		Blocks.reinforcedContainer.itemCapacity = 160;
		//blocks-turret
		if (Blocks.duo instanceof ItemTurret turret) {
			turret.ammoTypes.put(Items.thorium, new BasicBulletType(4f, 25f) {{
				width = 8f;
				height = 13f;
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				ammoMultiplier = 4;
				lifetime = 55f;
				hitEffect = despawnEffect = Fx.hitBulletColor;
				backColor = hitColor = trailColor = Pal.thoriumAmmoBack;
				frontColor = Pal.thoriumAmmoFront;
			}});
			turret.ammoTypes.put(HItems.uranium, new BasicBulletType(5f, 36f) {{
				rangeChange = 10f;
				width = 10f;
				height = 13f;
				lifetime = 50f;
				pierceCap = 2;
				pierceArmor = true;
				ammoMultiplier = 8f;
				hitEffect = despawnEffect = Fx.hitBulletColor;
				hitColor = backColor = trailColor = HPal.uraniumAmmoBack;
				frontColor = HPal.uraniumAmmoFront;
			}});
		}
		if (Blocks.wave instanceof LiquidTurret turret) {
			turret.ammoTypes.put(HLiquids.nitratedOil, new LiquidBulletType(HLiquids.nitratedOil) {{
				drag = 0.01f;
				layer = Layer.bullet - 2f;
			}});
			turret.ammoTypes.put(HLiquids.crystalFluid, new LiquidBulletType(HLiquids.crystalFluid) {{
				drag = 0.01f;
				healPercent = 5f;
				collidesTeam = true;
			}});
		}
		if (Blocks.salvo instanceof ItemTurret turret) turret.ammoTypes.put(HItems.uranium, new BasicBulletType(5f, 39f, "bullet") {{
			rangeChange = 10f;
			width = 10f;
			height = 13f;
			pierceCap = 2;
			pierceArmor = true;
			shootEffect = Fx.shootBig;
			smokeEffect = Fx.shootBigSmoke;
			ammoMultiplier = 4f;
			lifetime = 60f;
			hitEffect = despawnEffect = Fx.hitBulletColor;
			hitColor = backColor = trailColor = HPal.uraniumAmmoBack;
			frontColor = HPal.uraniumAmmoFront;
		}});
		if (Blocks.fuse instanceof ItemTurret turret) turret.ammoTypes.put(HItems.uranium, new ShrapnelBulletType() {{
			length = 100f;
			damage = 135f;
			ammoMultiplier = 6f;
			toColor = new Color(0xa5b2c2ff);
			shootEffect = smokeEffect = HFx.shoot(HPal.uraniumAmmoBack);
		}});
		if (Blocks.tsunami instanceof LiquidTurret turret) {
			turret.ammoTypes.put(HLiquids.nitratedOil, new LiquidBulletType(HLiquids.nitratedOil) {{
				lifetime = 49f;
				speed = 4f;
				knockback = 1.3f;
				puddleSize = 8f;
				orbSize = 4f;
				drag = 0.001f;
				ammoMultiplier = 0.4f;
				statusDuration = 60f * 4f;
				damage = 0.2f;
				layer = Layer.bullet - 2f;
			}});
			turret.ammoTypes.put(HLiquids.crystalFluid, new LiquidBulletType(HLiquids.crystalFluid) {{
				lifetime = 49f;
				speed = 4f;
				knockback = 1.3f;
				puddleSize = 8f;
				orbSize = 4f;
				drag = 0.001f;
				ammoMultiplier = 1.2f;
				statusDuration = 60f * 4f;
				healPercent = 5f;
				collidesTeam = true;
				damage = 0.2f;
			}});
		}
		if (Blocks.foreshadow instanceof ItemTurret turret) {
			turret.ammoTypes.clear();
			turret.ammoTypes.put(Items.surgeAlloy, new RailBulletType() {{
				shootEffect = Fx.railShoot;
				length = 600f;
				pointEffectSpace = 60f;
				pierceEffect = Fx.railHit;
				pointEffect = Fx.railTrail;
				hitEffect = Fx.massiveExplosion;
				ammoMultiplier = 1f;
				smokeEffect = Fx.smokeCloud;
				damage = 1450f;
				pierceDamageFactor = 0.5f;
				buildingDamageMultiplier = 0.3f;
			}});
		}
		if (Blocks.spectre instanceof ItemTurret turret) {
			turret.range = 280f;
			turret.ammoTypes.put(HItems.uranium, new BasicBulletType(9f, 105f) {{
				rangeChange = 5f;
				hitSize = 5f;
				width = 16f;
				height = 23f;
				shootEffect = Fx.shootBig;
				pierceCap = 3;
				pierceArmor = pierceBuilding = true;
				knockback = 0.7f;
				status = StatusEffects.melting;
				statusDuration = 270f;
				lifetime = 37.5f;
			}});
		}
		if (Blocks.meltdown instanceof LaserTurret turret) {
			turret.range = 235f;
			turret.shootType = new ContinuousLaserBulletType(96f) {{
				length = 240f;
				hitEffect = Fx.hitMeltdown;
				hitColor = Pal.meltdownHit;
				status = StatusEffects.melting;
				drawSize = 420f;
				timescaleDamage = true;
				incendChance = 0.4f;
				incendSpread = 5f;
				incendAmount = 1;
				ammoMultiplier = 1f;
			}};
		}
		//blocks-turret-erekir
		Blocks.breach.armor = 2f;
		Blocks.diffuse.armor = 3f;
		Blocks.sublimate.armor = 4f;
		if (Blocks.sublimate instanceof ContinuousLiquidTurret turret) {
			turret.range = 130f;
			turret.ammoTypes.clear();
			turret.ammoTypes.put(HLiquids.gas, new ContinuousFlameBulletType() {{
				damage = 90f;
				rangeChange = 30f;
				length = 160f;
				knockback = 1f;
				pierceCap = 2;
				buildingDamageMultiplier = 0.3f;
				colors = new Color[]{new Color(0xffd37fa1), new Color(0xffd37fcc), new Color(0xffd37fff), new Color(0xffe6b7ff), new Color(0xd8e2ffff)};
				lightColor = flareColor = new Color(0xfbd367ff);
				hitColor = new Color(0xffd367ff);
			}});
			turret.ammoTypes.put(Liquids.hydrogen, new ContinuousFlameBulletType() {{
				damage = 60f;
				length = 130f;
				knockback = 1f;
				pierceCap = 2;
				buildingDamageMultiplier = 0.3f;
				colors = new Color[]{new Color(0x92abff7f), new Color(0x92abffa2), new Color(0x92abffd3), new Color(0x92abffff), new Color(0xd4e0ffff)};
				lightColor = hitColor = flareColor = new Color(0x92abffff);
			}});
			turret.ammoTypes.put(Liquids.cyanogen, new ContinuousFlameBulletType() {{
				damage = 130f;
				rangeChange = 70f;
				length = 200f;
				knockback = 2f;
				pierceCap = 3;
				buildingDamageMultiplier = 0.3f;
				colors = new Color[]{new Color(0x465ab888), new Color(0x66a6d2a0), new Color(0x89e8b6b0), new Color(0xcafcbeff), Color.white};
				lightColor = hitColor = flareColor = new Color(0x89e8b6ff);
			}});
		}
		Blocks.titan.armor = 13f;
		Blocks.titan.researchCost = ItemStack.with(Items.thorium, 4000, Items.silicon, 3000, Items.tungsten, 2500);
		Blocks.disperse.armor = 9f;
		Blocks.afflict.armor = 16f;
		if (Blocks.afflict instanceof PowerTurret turret) turret.shootType = new BasicBulletType(5f, 180f, "large-orb") {{
			shootEffect = new MultiEffect(Fx.shootTitan, new WaveEffect() {{
				colorTo = Pal.surge;
				sizeTo = 26f;
				lifetime = 14f;
				strokeFrom = 4f;
			}});
			smokeEffect = Fx.shootSmokeTitan;
			hitColor = Pal.surge;
			trailEffect = Fx.missileTrail;
			trailInterval = 3f;
			trailParam = 4f;
			pierceCap = 2;
			fragOnHit = false;
			lifetime = 80f;
			width = height = 16f;
			backColor = Pal.surge;
			frontColor = Color.white;
			shrinkX = shrinkY = 0f;
			trailColor = Pal.surge;
			trailLength = 12;
			trailWidth = 2.2f;
			despawnEffect = hitEffect = new ExplosionEffect() {{
				waveColor = Pal.surge;
				smokeColor = Color.gray;
				sparkColor = Pal.sap;
				waveStroke = 4f;
				waveRad = 40f;
			}};
			despawnSound = Sounds.dullExplosion;
			fragBullet = intervalBullet = new BasicBulletType(3f, 35) {{
				width = 9f;
				hitSize = 5f;
				height = 15f;
				pierce = true;
				lifetime = 35f;
				pierceBuilding = true;
				hitColor = backColor = trailColor = Pal.surge;
				frontColor = Color.white;
				trailWidth = 2.1f;
				trailLength = 5;
				hitEffect = despawnEffect = new WaveEffect() {{
					colorFrom = colorTo = Pal.surge;
					sizeTo = 4f;
					strokeFrom = 4f;
					lifetime = 10f;
				}};
				buildingDamageMultiplier = 0.3f;
				homingPower = 0.2f;
			}};
			bulletInterval = 3f;
			intervalRandomSpread = 20f;
			intervalBullets = 2;
			intervalAngle = 180f;
			intervalSpread = 300f;
			fragBullets = 20;
			fragVelocityMin = 0.5f;
			fragVelocityMax = 1.5f;
			fragLifeMin = 0.5f;
		}};
		Blocks.lustre.armor = 15f;
		Blocks.scathe.armor = 15f;
		Blocks.smite.armor = 21f;
		if (Blocks.smite instanceof ItemTurret turret) {
			turret.minWarmup = 0.98f;
			turret.warmupMaintainTime = 45f;
		}
		Blocks.malign.armor = 19f;
		if (Blocks.malign instanceof PowerTurret turret) {
			turret.minWarmup = 0.98f;
			turret.warmupMaintainTime = 45f;
		}
		//blocks-units
		if (Blocks.groundFactory instanceof UnitFactory factory) factory.plans.add(new UnitPlan(HUnitTypes.vanguard, 1200f, ItemStack.with(Items.lead, 25, Items.titanium, 25, Items.silicon, 30)));
		if (Blocks.airFactory instanceof UnitFactory factory) factory.plans.add(new UnitPlan(HUnitTypes.caelifera, 1200f, ItemStack.with(Items.lead, 35, Items.titanium, 15, Items.silicon, 30)));
		if (Blocks.additiveReconstructor instanceof Reconstructor reconstructor) reconstructor.upgrades.add(new UnitType[]{HUnitTypes.vanguard, HUnitTypes.striker}, new UnitType[]{HUnitTypes.caelifera, HUnitTypes.schistocerca});
		if (Blocks.multiplicativeReconstructor instanceof Reconstructor reconstructor) reconstructor.upgrades.add(new UnitType[]{HUnitTypes.striker, HUnitTypes.counterattack}, new UnitType[]{HUnitTypes.schistocerca, HUnitTypes.anthophila});
		if (Blocks.exponentialReconstructor instanceof Reconstructor reconstructor) reconstructor.upgrades.add(new UnitType[]{HUnitTypes.counterattack, HUnitTypes.crush}, new UnitType[]{HUnitTypes.anthophila, HUnitTypes.vespula});
		if (Blocks.tetrativeReconstructor instanceof Reconstructor reconstructor) reconstructor.upgrades.add(new UnitType[]{HUnitTypes.crush, HUnitTypes.destruction}, new UnitType[]{HUnitTypes.vespula, HUnitTypes.lepidoptera});
		//blocks-units-erekir
		if (Blocks.constructor instanceof Constructor constructor) constructor.filter.clear();
		if (Blocks.tankAssembler instanceof UnitAssembler assembler) assembler.plans.add(new AssemblerUnitPlan(HUnitTypes.dominate, 60f * 60f * 4f, PayloadStack.list(UnitTypes.precept, 4, HBlocks.aparajitoLarge, 20)));
		if (Blocks.shipAssembler instanceof UnitAssembler assembler) assembler.plans.add(new AssemblerUnitPlan(HUnitTypes.havoc, 60f * 60f * 4f, PayloadStack.list(UnitTypes.obviate, 4, HBlocks.aparajitoLarge, 20)));
		if (Blocks.mechAssembler instanceof UnitAssembler assembler) assembler.plans.add(new AssemblerUnitPlan(HUnitTypes.oracle, 60f * 60f * 4f, PayloadStack.list(UnitTypes.anthicus, 4, HBlocks.aparajitoLarge, 20)));
		//units
		UnitTypes.alpha.coreUnitDock = true;
		UnitTypes.beta.coreUnitDock = true;
		UnitTypes.gamma.coreUnitDock = true;
		//units-erekir
		UnitTypes.tecta.armor = 11f;
		UnitTypes.collaris.armor = 15f;
		//liquids
		Liquids.slag.temperature = 2f;
		Liquids.hydrogen.flammability = 1.5f;
		Liquids.hydrogen.explosiveness = 1.5f;
		Liquids.ozone.flammability = 0f;
		Liquids.ozone.explosiveness = 0f;
		Liquids.neoplasm.canStayOn.addAll(HLiquids.crystalFluid, HLiquids.lightOil, HLiquids.nitratedOil, HLiquids.blastReagent);
		Liquids.neoplasm.capPuddles = true;
		Liquids.gallium.hidden = false;
		//items
		Items.graphite.hardness = 2;
		Items.metaglass.hardness = 2;
		Items.silicon.hardness = 2;
		Items.plastanium.hardness = 3;
		Items.surgeAlloy.hardness = 6;
		Items.phaseFabric.hardness = 3;
		Items.carbide.hardness = 6;
		Items.serpuloItems.addAll(HItems.stone, HItems.agglomerateSalt, HItems.rareEarth, HItems.galliumNitride, HItems.crystallineCircuit, HItems.gold, HItems.chromium, HItems.uranium, HItems.heavyAlloy, HItems.crystal);
		Items.erekirItems.addAll(HItems.stone, HItems.uranium, HItems.chromium, HItems.crystal);
		//planets
		Planets.serpulo.allowSectorInvasion = false;
	}
}
