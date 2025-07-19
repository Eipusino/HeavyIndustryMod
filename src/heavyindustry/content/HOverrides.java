package heavyindustry.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.Seq;
import heavyindustry.core.HeavyIndustryMod;
import heavyindustry.entities.bullet.CtrlMissileBulletType;
import heavyindustry.graphics.HPal;
import heavyindustry.world.meta.HAttribute;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.Planets;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.ContinuousFlameBulletType;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.entities.bullet.LiquidBulletType;
import mindustry.entities.bullet.RailBulletType;
import mindustry.entities.bullet.ShrapnelBulletType;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.effect.WrapEffect;
import mindustry.entities.part.FlarePart;
import mindustry.entities.part.ShapePart;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.campaign.LandingPad;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ContinuousLiquidTurret;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LaserTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.blocks.distribution.StackConveyor;
import mindustry.world.blocks.payloads.Constructor;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.NuclearReactor;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.BeamDrill;
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
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BuildVisibility;

import static mindustry.Vars.content;
import static mindustry.type.ItemStack.with;

/**
 * Covering the original content.
 *
 * @author Eipusino
 */
public final class HOverrides {
	/** Don't let anyone instantiate this class. */
	private HOverrides() {}

	/**
	 * Instantiates all contents. Called in the main thread in {@link HeavyIndustryMod#loadContent()}.
	 * <p>Remember not to execute it a second time, I did not take any precautionary measures.
	 */
	public static void load() {
		//blocks-environment
		Blocks.stone.itemDrop = Blocks.craters.itemDrop = Blocks.charr.itemDrop = HItems.stone;
		Blocks.stone.playerUnmineable = Blocks.craters.playerUnmineable = Blocks.charr.playerUnmineable = true;
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
		((Wall) Blocks.surgeWall).lightningChance = ((Wall) Blocks.surgeWallLarge).lightningChance = 0.1f;
		((Wall) Blocks.surgeWall).lightningDamage = ((Wall) Blocks.surgeWallLarge).lightningDamage = 25f;
		//blocks-wall-erekir
		((Wall) Blocks.reinforcedSurgeWall).lightningChance = 0.1f;
		((Wall) Blocks.reinforcedSurgeWallLarge).lightningChance = 0.1f;
		//Blocks-distribution
		((StackConveyor) Blocks.plastaniumConveyor).outputRouter = false;
		((MassDriver) Blocks.massDriver).reload = 150f;
		//blocks-liquid
		((Pump) Blocks.impulsePump).pumpAmount = 0.3f;
		Blocks.phaseConduit.liquidCapacity = 16f;
		//blocks-liquid-erekir
		Blocks.reinforcedLiquidRouter.liquidCapacity = 40f;
		//Blocks-drill-erekir
		((BeamDrill) Blocks.largePlasmaBore).drillMultipliers.put(Items.beryllium, 1.5f);
		((BeamDrill) Blocks.largePlasmaBore).drillMultipliers.put(Items.graphite, 1.5f);
		//blocks-power
		((PowerNode) Blocks.surgeTower).maxNodes = 3;
		((ConsumeGenerator) Blocks.differentialGenerator).powerProduction = 28f;
		((NuclearReactor) Blocks.thoriumReactor).powerProduction = 18f;
		Blocks.impactReactor.liquidCapacity = 80f;
		Blocks.neoplasiaReactor.canOverdrive = true;
		//blocks-production
		Blocks.phaseWeaver.itemCapacity = 30;
		Blocks.disassembler.removeConsumers(c -> c instanceof ConsumeItems);
		((Separator) Blocks.disassembler).results = ItemStack.with(Items.copper, 1, Items.lead, 1, Items.graphite, 1, Items.titanium, 1, Items.thorium, 1);
		//blocks-production-erekir
		Blocks.oxidationChamber.canOverdrive = true;
		Blocks.heatReactor.buildVisibility = BuildVisibility.shown;
		((AttributeCrafter) Blocks.ventCondenser).maxBoost = 3f;
		((GenericCrafter) Blocks.electrolyzer).outputLiquids = LiquidStack.with(Liquids.ozone, 4f / 60f, Liquids.hydrogen, 8f / 60f);
		Blocks.cyanogenSynthesizer.removeConsumers(c -> c instanceof ConsumeLiquidBase);
		Blocks.cyanogenSynthesizer.consumeLiquid(Liquids.arkycite, 15f / 60f);
		((HeatCrafter) Blocks.cyanogenSynthesizer).outputLiquid = new LiquidStack(Liquids.cyanogen, 4f / 60f);
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
		((LiquidTurret) Blocks.wave).ammoTypes.put(HLiquids.nitratedOil, new LiquidBulletType(HLiquids.nitratedOil) {{
			drag = 0.01f;
			layer = Layer.bullet - 2f;
		}});
		((LiquidTurret) Blocks.wave).ammoTypes.put(HLiquids.originiumFluid, new LiquidBulletType(HLiquids.originiumFluid) {{
			drag = 0.01f;
			healPercent = 5f;
			collidesTeam = true;
		}});
		((ItemTurret) Blocks.salvo).ammoTypes.put(HItems.uranium, new BasicBulletType(5f, 39, "bullet") {{
			width = 10f;
			height = 13f;
			pierceCap = 2;
			pierceArmor = true;
			shootEffect = Fx.shootBig;
			smokeEffect = Fx.shootBigSmoke;
			ammoMultiplier = 4f;
			lifetime = 50f;
		}});
		((ItemTurret) Blocks.fuse).ammoTypes.put(HItems.uranium, new ShrapnelBulletType() {{
			length = 100f;
			damage = 135f;
			ammoMultiplier = 6f;
			toColor = Color.valueOf("a5b2c2");
			shootEffect = smokeEffect = HFx.shoot(HPal.uraniumGrey);
		}});
		((LiquidTurret) Blocks.tsunami).ammoTypes.put(HLiquids.nitratedOil, new LiquidBulletType(HLiquids.nitratedOil) {{
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
		((LiquidTurret) Blocks.tsunami).ammoTypes.put(HLiquids.originiumFluid, new LiquidBulletType(HLiquids.originiumFluid) {{
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
		((ItemTurret) Blocks.foreshadow).ammo(Items.surgeAlloy, new RailBulletType() {{
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
		((ItemTurret) Blocks.spectre).range = 280f;
		((ItemTurret) Blocks.spectre).ammoTypes.put(HItems.uranium, new BasicBulletType(9f, 105f) {{
			hitSize = 5f;
			width = 16f;
			height = 23f;
			shootEffect = Fx.shootBig;
			pierceCap = 3;
			pierceArmor = pierceBuilding = true;
			knockback = 0.7f;
			status = StatusEffects.melting;
			statusDuration = 270f;
		}});
		((LaserTurret) Blocks.meltdown).range = 245;
		((LaserTurret) Blocks.meltdown).shootType = new ContinuousLaserBulletType(96) {{
			length = 250f;
			hitEffect = Fx.hitMeltdown;
			hitColor = Pal.meltdownHit;
			status = StatusEffects.melting;
			drawSize = 420f;
			incendChance = 0.4f;
			incendSpread = 5f;
			incendAmount = 1;
			ammoMultiplier = 1f;
		}};
		//blocks-turret-erekir
		Blocks.breach.armor = 2f;
		Blocks.diffuse.armor = 3f;
		Blocks.sublimate.armor = 4f;
		((ContinuousLiquidTurret) Blocks.sublimate).range = 120f;
		((ContinuousLiquidTurret) Blocks.sublimate).ammo(HLiquids.gas, new ContinuousFlameBulletType() {{
			damage = 40f;
			length = 120f;
			knockback = 1f;
			pierceCap = 2;
			buildingDamageMultiplier = 0.3f;
			colors = new Color[]{Color.valueOf("ffd37fa1"), Color.valueOf("ffd37fcc"), Color.valueOf("ffd37f"), Color.valueOf("ffe6b7"), Color.valueOf("d8e2ff")};
			lightColor = flareColor = Color.valueOf("fbd367");
			hitColor = Color.valueOf("ffd367");
		}}, Liquids.hydrogen, new ContinuousFlameBulletType() {{
			damage = 60f;
			rangeChange = 10f;
			length = 130f;
			knockback = 1f;
			pierceCap = 2;
			buildingDamageMultiplier = 0.3f;
			colors = new Color[]{Color.valueOf("92abff7f"), Color.valueOf("92abffa2"), Color.valueOf("92abffd3"), Color.valueOf("92abff"), Color.valueOf("d4e0ff")};
			lightColor = hitColor = flareColor = Color.valueOf("92abff");
		}}, Liquids.cyanogen, new ContinuousFlameBulletType() {{
			damage = 130f;
			rangeChange = 80f;
			length = 200f;
			knockback = 2f;
			pierceCap = 3;
			buildingDamageMultiplier = 0.3f;
			colors = new Color[]{Color.valueOf("465ab888"), Color.valueOf("66a6d2a0"), Color.valueOf("89e8b6b0"), Color.valueOf("cafcbe"), Color.white};
			lightColor = hitColor = flareColor = Color.valueOf("89e8b6");
		}});
		/*((PowerTurret) Blocks.afflict).shootType = new BasicBulletType(5f, 180f, "large-orb") {{
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
			buildingDamageMultiplier = 0.5f;
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
		}};*/
		Blocks.titan.armor = 13f;
		Blocks.titan.researchCost = with(Items.thorium, 4000, Items.silicon, 3000, Items.tungsten, 2500);
		Blocks.disperse.armor = 9f;
		Blocks.afflict.armor = 16f;
		Blocks.lustre.armor = 15f;
		Blocks.scathe.armor = 15f;
		Blocks.smite.armor = 21f;
		((ItemTurret) Blocks.smite).minWarmup = 0.98f;
		((ItemTurret) Blocks.smite).warmupMaintainTime = 45f;
		Blocks.malign.armor = 19f;
		((PowerTurret) Blocks.malign).minWarmup = 0.98f;
		((PowerTurret) Blocks.malign).warmupMaintainTime = 45f;
		//blocks-units
		((UnitFactory) Blocks.groundFactory).plans.add(new UnitPlan(HUnitTypes.vanguard, 1200f, with(Items.lead, 25, Items.titanium, 25, Items.silicon, 30)));
		((UnitFactory) Blocks.airFactory).plans.add(new UnitPlan(HUnitTypes.caelifera, 1200f, with(Items.lead, 35, Items.titanium, 15, Items.silicon, 30)));
		((Reconstructor) Blocks.additiveReconstructor).upgrades.add(new UnitType[]{HUnitTypes.vanguard, HUnitTypes.striker}, new UnitType[]{HUnitTypes.caelifera, HUnitTypes.schistocerca});
		((Reconstructor) Blocks.multiplicativeReconstructor).upgrades.add(new UnitType[]{HUnitTypes.striker, HUnitTypes.counterattack}, new UnitType[]{HUnitTypes.schistocerca, HUnitTypes.anthophila});
		((Reconstructor) Blocks.exponentialReconstructor).upgrades.add(new UnitType[]{HUnitTypes.counterattack, HUnitTypes.crush}, new UnitType[]{HUnitTypes.anthophila, HUnitTypes.vespula});
		((Reconstructor) Blocks.tetrativeReconstructor).upgrades.add(new UnitType[]{HUnitTypes.crush, HUnitTypes.destruction}, new UnitType[]{HUnitTypes.vespula, HUnitTypes.lepidoptera});
		//blocks-units-erekir
		((Constructor) Blocks.constructor).filter = Seq.with();
		((UnitAssembler) Blocks.tankAssembler).plans.add(new AssemblerUnitPlan(HUnitTypes.dominate, 60f * 60f * 4f, PayloadStack.list(UnitTypes.precept, 4, HBlocks.aparajitoLarge, 20)));
		((UnitAssembler) Blocks.shipAssembler).plans.add(new AssemblerUnitPlan(HUnitTypes.havoc, 60f * 60f * 4f, PayloadStack.list(UnitTypes.obviate, 4, HBlocks.aparajitoLarge, 20)));
		((UnitAssembler) Blocks.mechAssembler).plans.add(new AssemblerUnitPlan(HUnitTypes.oracle, 60f * 60f * 4f, PayloadStack.list(UnitTypes.anthicus, 4, HBlocks.aparajitoLarge, 20)));
		//blocks-campaign

		//I can't figure out how this thing consumes so much water...
		//Anuke's recent mental state has been very poor. I can't figure out how this kind of thing came up with.
		((LandingPad) Blocks.landingPad).consumeLiquidAmount /= 150f;
		//unit types
		UnitTypes.alpha.coreUnitDock = true;
		UnitTypes.beta.coreUnitDock = true;
		UnitTypes.gamma.coreUnitDock = true;
		//unit types-erekir
		UnitTypes.quell.targetAir = true;
		UnitTypes.quell.weapons.get(0).bullet = new CtrlMissileBulletType("quell-missile", -1, -1) {{
			shootEffect = Fx.shootBig;
			smokeEffect = Fx.shootBigSmoke2;
			speed = 4.3f;
			keepVelocity = false;
			maxRange = 6f;
			lifetime = 60f * 1.6f;
			damage = 100f;
			splashDamage = 100f;
			splashDamageRadius = 25f;
			buildingDamageMultiplier = 0.5f;
			hitEffect = despawnEffect = Fx.massiveExplosion;
			trailColor = Pal.sapBulletBack;
		}};
		UnitTypes.quell.weapons.get(0).shake = 1f;
		UnitTypes.disrupt.targetAir = true;
		UnitTypes.disrupt.weapons.get(0).bullet = new CtrlMissileBulletType("disrupt-missile", -1, -1) {{
			shootEffect = Fx.sparkShoot;
			smokeEffect = Fx.shootSmokeTitan;
			hitColor = Pal.suppress;
			maxRange = 5f;
			speed = 4.6f;
			keepVelocity = false;
			homingDelay = 10f;
			trailColor = Pal.sapBulletBack;
			trailLength = 8;
			hitEffect = despawnEffect = new ExplosionEffect() {{
				lifetime = 50f;
				waveStroke = 5f;
				waveLife = 8f;
				waveColor = Color.white;
				sparkColor = smokeColor = Pal.suppress;
				waveRad = 40f;
				smokeSize = 4f;
				smokes = 7;
				smokeSizeBase = 0f;
				sparks = 10;
				sparkRad = 40f;
				sparkLen = 6f;
				sparkStroke = 2f;
			}};
			damage = 135f;
			splashDamage = 135f;
			splashDamageRadius = 25f;
			buildingDamageMultiplier = 0.5f;
			parts.add(new ShapePart() {{
				layer = Layer.effect;
				circle = true;
				y = -3.5f;
				radius = 1.6f;
				color = Pal.suppress;
				colorTo = Color.white;
				progress = PartProgress.life.curve(Interp.pow5In);
			}});
		}};
		UnitTypes.disrupt.weapons.get(0).shake = 1f;
		UnitTypes.anthicus.weapons.get(0).bullet = new CtrlMissileBulletType("anthicus-missile", -1, -1) {{
			shootEffect = new MultiEffect(Fx.shootBigColor, new Effect(9, e -> {
				Draw.color(Color.white, e.color, e.fin());
				Lines.stroke(0.7f + e.fout());
				Lines.square(e.x, e.y, e.fin() * 5f, e.rotation + 45f);
				Drawf.light(e.x, e.y, 23f, e.color, e.fout() * 0.7f);
			}), new WaveEffect() {{
				colorFrom = colorTo = Pal.techBlue;
				sizeTo = 15f;
				lifetime = 12f;
				strokeFrom = 3f;
			}});
			smokeEffect = Fx.shootBigSmoke2;
			speed = 3.7f;
			keepVelocity = false;
			inaccuracy = 2f;
			maxRange = 6;
			trailWidth = 2;
			trailColor = Pal.techBlue;
			low = true;
			damage = 110f;
			splashDamage = 110f;
			splashDamageRadius = 25f;
			buildingDamageMultiplier = 0.8f;
			despawnEffect = hitEffect = new MultiEffect(Fx.massiveExplosion, new WrapEffect(Fx.dynamicSpikes, Pal.techBlue, 24f), new WaveEffect() {{
				colorFrom = colorTo = Pal.techBlue;
				sizeTo = 40f;
				lifetime = 12f;
				strokeFrom = 4f;
			}});
			parts.add(new FlarePart() {{
				progress = PartProgress.life.slope().curve(Interp.pow2In);
				radius = 0f;
				radiusTo = 35f;
				stroke = 3f;
				rotation = 45f;
				y = -5f;
				followRotation = true;
			}});
		}};
		UnitTypes.anthicus.weapons.get(0).shake = 2f;
		UnitTypes.anthicus.weapons.get(0).reload = 120f;
		UnitTypes.tecta.armor = 11f;
		UnitTypes.collaris.armor = 15f;
		//liquids
		Liquids.slag.temperature = 2f;
		Liquids.hydrogen.flammability = 1.5f;
		Liquids.hydrogen.explosiveness = 1.5f;
		Liquids.ozone.flammability = 0f;
		Liquids.ozone.explosiveness = 0f;
		Liquids.neoplasm.canStayOn.addAll(HLiquids.originiumFluid, HLiquids.nitratedOil);
		Liquids.neoplasm.capPuddles = true;
		//items
		Items.graphite.hardness = 2;
		Items.metaglass.hardness = 2;
		Items.silicon.hardness = 2;
		Items.plastanium.hardness = 3;
		Items.surgeAlloy.hardness = 6;
		Items.phaseFabric.hardness = 3;
		Items.carbide.hardness = 6;
		Items.serpuloItems.addAll(HItems.stone, HItems.salt, HItems.rareEarth, HItems.crystalCircuit, HItems.chromium, HItems.uranium, HItems.heavyAlloy, HItems.originium);
		Items.erekirItems.addAll(HItems.uranium, HItems.chromium, HItems.originium);
		//planet
		Planets.serpulo.allowSectorInvasion = false;
		//other
	}

	/** special changes on April Fool's Day. */
	public static void loadAprilFoolsDay() {
		Seq<Block> sc = content.blocks().copy();
		sc.removeAll(b -> b.localizedName == null || b.description == null);
		for (int i = 0; i < sc.size; i++) {
			Block b = sc.get(i);
			if (b != null) {
				String l = b.localizedName;
				String n = b.description;
				int d = Mathf.random(sc.size - 1);
				while (d == i) {
					d = Mathf.random(sc.size - 1);
				}
				Block b1 = sc.get(d);
				if (b1 != null) {
					b.localizedName = b1.localizedName;
					b.description = b1.description;
					b1.localizedName = l;
					b1.description = n;
				}
			}
		}

		Seq<Item> ic = content.items().copy();
		ic.removeAll(it -> it.localizedName == null || it.description == null);
		for (int i = 0; i < ic.size; i++) {
			Item b = ic.get(i);
			if (b != null) {
				String l = b.localizedName;
				String n = b.description;
				int d = Mathf.random(ic.size - 1);
				while (d == i) {
					d = Mathf.random(ic.size - 1);
				}
				Item b1 = ic.get(d);
				if (b1 != null) {
					b.localizedName = b1.localizedName;
					b.description = b1.description;
					b1.localizedName = l;
					b1.description = n;
				}
			}
		}

		Seq<Liquid> lc = content.liquids().copy();
		lc.removeAll(lt -> lt.localizedName == null || lt.description == null);
		for (int i = 0; i < lc.size; i++) {
			Liquid b = lc.get(i);
			if (b != null) {
				String l = b.localizedName;
				String n = b.description;
				int d = Mathf.random(ic.size - 1);
				while (d == i) {
					d = Mathf.random(ic.size - 1);
				}
				Liquid b1 = lc.get(d);
				if (b1 != null) {
					b.localizedName = b1.localizedName;
					b.description = b1.description;
					b1.localizedName = l;
					b1.description = n;
				}
			}
		}

		Seq<UnitType> uc = content.units().copy();
		uc.removeAll(u -> u.localizedName == null || u.description == null);
		for (int i = 0; i < uc.size; i++) {
			UnitType b = uc.get(i);
			if (b != null) {
				String l = b.localizedName;
				String n = b.description;
				int d = Mathf.random(uc.size - 1);
				while (d == i) {
					d = Mathf.random(uc.size - 1);
				}
				UnitType b1 = uc.get(d);
				if (b1 != null) {
					b.localizedName = b1.localizedName;
					b.description = b1.description;
					b1.localizedName = l;
					b1.description = n;
				}
			}
		}
	}
}
