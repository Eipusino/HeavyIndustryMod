package heavyindustry.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import heavyindustry.core.*;
import heavyindustry.entities.bullet.*;
import heavyindustry.graphics.*;
import heavyindustry.world.meta.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.units.UnitAssembler.*;
import mindustry.world.blocks.units.UnitFactory.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.type.ItemStack.*;

/**
 * Covering the original content.
 *
 * @author Eipusino
 */
public final class HIOverride {
    /** HIOverride should not be instantiated. */
    private HIOverride() {}

    /**
     * Instantiates all contents. Called in the main thread in {@link HeavyIndustryMod#loadContent()}.
     * <p>Remember not to execute it a second time, I did not take any precautionary measures.
     */
    public static void load() {
        //Blocks-Environment
        Blocks.stone.itemDrop = Blocks.craters.itemDrop = Blocks.charr.itemDrop = HIItems.stone;
        Blocks.stone.playerUnmineable = Blocks.craters.playerUnmineable = Blocks.charr.playerUnmineable = true;
        Blocks.sandWater.itemDrop = Blocks.darksandWater.itemDrop = Blocks.darksandTaintedWater.itemDrop = Items.sand;
        Blocks.sandWater.playerUnmineable = Blocks.darksandWater.playerUnmineable = Blocks.darksandTaintedWater.playerUnmineable = true;
        ((Floor) Blocks.deepTaintedWater).liquidMultiplier = 1.5f;
        Blocks.oxidationChamber.canOverdrive = Blocks.neoplasiaReactor.canOverdrive = true;
        Blocks.slag.attributes.set(Attribute.heat, 1f);
        //Blocks-Environment-Erekir
        Blocks.yellowStonePlates.attributes.set(Attribute.water, -1f);
        Blocks.beryllicStone.attributes.set(HIAttribute.arkycite, 0.7f);
        Blocks.arkyicStone.attributes.set(HIAttribute.arkycite, 1f);
        //Blocks-Wall
        Blocks.copperWall.armor = Blocks.copperWallLarge.armor = 1f;
        Blocks.titaniumWall.armor = Blocks.titaniumWallLarge.armor = Blocks.plastaniumWall.armor = Blocks.plastaniumWallLarge.armor = 2f;
        Blocks.thoriumWall.armor = Blocks.thoriumWallLarge.armor = 8f;
        Blocks.phaseWall.armor = Blocks.phaseWallLarge.armor = 3f;
        Blocks.surgeWall.armor = Blocks.surgeWallLarge.armor = 12f;
        ((Wall) Blocks.surgeWall).lightningChance = ((Wall) Blocks.surgeWallLarge).lightningChance = 0.1f;
        ((Wall) Blocks.surgeWall).lightningDamage = ((Wall) Blocks.surgeWallLarge).lightningDamage = 25f;
        //Blocks-Wall-erekir
        ((Wall) Blocks.reinforcedSurgeWall).lightningChance = 0.1f;
        ((Wall) Blocks.reinforcedSurgeWallLarge).lightningChance = 0.1f;
        //Blocks-Distribution
        ((StackConveyor) Blocks.plastaniumConveyor).outputRouter = false;
        ((MassDriver) Blocks.massDriver).reload = 150f;
        //Blocks-Liquid
        ((Pump) Blocks.impulsePump).pumpAmount = 0.3f;
        Blocks.phaseConduit.liquidCapacity = 16f;
        //Blocks-Liquid-Erekir
        Blocks.reinforcedLiquidRouter.liquidCapacity = 40f;
        //Blocks-Drill-Erekir
        ObjectFloatMap<Item> bur = new ObjectFloatMap<>();
        for (Item item : content.items()) {
            bur.put(item, hard(item));
        }
        ((BeamDrill) Blocks.largePlasmaBore).drillMultipliers.put(Items.beryllium, 1.5f);
        ((BeamDrill) Blocks.largePlasmaBore).drillMultipliers.put(Items.graphite, 1.5f);
        //Blocks-Power
        ((SolarGenerator) Blocks.largeSolarPanel).powerProduction = 1.75f;
        ((PowerNode) Blocks.surgeTower).maxNodes = 3;
        ((ConsumeGenerator) Blocks.differentialGenerator).powerProduction = 28f;
        ((NuclearReactor) Blocks.thoriumReactor).powerProduction = 18f;
        Blocks.impactReactor.liquidCapacity = 80f;
        Blocks.neoplasiaReactor.canOverdrive = true;
        //Blocks-Production
        Blocks.phaseWeaver.itemCapacity = 30;
        Blocks.disassembler.removeConsumers(c -> c instanceof ConsumeItems);
        ((Separator) Blocks.disassembler).results = ItemStack.with(Items.copper, 1, Items.lead, 1, Items.graphite, 1, Items.titanium, 1, Items.thorium, 1);
        //Blocks-Production-Erekir
        Blocks.oxidationChamber.canOverdrive = true;
        Blocks.heatReactor.buildVisibility = BuildVisibility.shown;
        ((AttributeCrafter) Blocks.ventCondenser).maxBoost = 3f;
        ((GenericCrafter) Blocks.electrolyzer).outputLiquids = LiquidStack.with(Liquids.ozone, 4f / 60f, Liquids.hydrogen, 8f / 60f);
        Blocks.cyanogenSynthesizer.removeConsumers(c -> c instanceof ConsumeLiquidBase);
        Blocks.cyanogenSynthesizer.consumeLiquid(Liquids.arkycite, 15f / 60f);
        ((HeatCrafter) Blocks.cyanogenSynthesizer).outputLiquid = new LiquidStack(Liquids.cyanogen, 4f / 60f);
        //Blocks-Defense
        Blocks.shockMine.underBullets = true;
        //Blocks-Storage
        Blocks.coreShard.buildVisibility = BuildVisibility.shown;
        Blocks.coreShard.health *= 2;
        Blocks.coreShard.armor = 3f;
        Blocks.coreFoundation.health *= 2;
        Blocks.coreFoundation.armor = 7f;
        Blocks.coreNucleus.health *= 2;
        Blocks.coreNucleus.armor = 11f;
        Blocks.reinforcedContainer.itemCapacity = 160;
        //Blocks-Turret
        ((LiquidTurret) Blocks.wave).ammoTypes.put(HILiquids.nitratedOil, new LiquidBulletType(HILiquids.nitratedOil) {{
            drag = 0.01f;
            layer = Layer.bullet - 2f;
        }});
        ((LiquidTurret) Blocks.wave).ammoTypes.put(HILiquids.nanofluid, new LiquidBulletType(HILiquids.nanofluid) {{
            drag = 0.01f;
            healPercent = 5f;
            collidesTeam = true;
        }});
        ((ItemTurret) Blocks.salvo).ammoTypes.put(HIItems.uranium, new BasicBulletType(5f, 39, "bullet") {{
            width = 10f;
            height = 13f;
            pierceCap = 2;
            pierceArmor = true;
            shootEffect = Fx.shootBig;
            smokeEffect = Fx.shootBigSmoke;
            ammoMultiplier = 4f;
            lifetime = 50f;
        }});
        ((ItemTurret) Blocks.fuse).ammoTypes.put(HIItems.uranium, new ShrapnelBulletType() {{
            length = 100f;
            damage = 135f;
            ammoMultiplier = 6f;
            toColor = Color.valueOf("a5b2c2");
            shootEffect = smokeEffect = HIFx.shoot(HIPal.uraniumGrey);
        }});
        ((LiquidTurret) Blocks.tsunami).ammoTypes.put(HILiquids.nitratedOil, new LiquidBulletType(HILiquids.nitratedOil) {{
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
        ((LiquidTurret) Blocks.tsunami).ammoTypes.put(HILiquids.nanofluid, new LiquidBulletType(HILiquids.nanofluid) {{
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
        ((ItemTurret) Blocks.spectre).ammoTypes.put(HIItems.uranium, new BasicBulletType(9f, 105f) {{
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
        //Blocks-Turret-Erekir
        Blocks.breach.armor = 2f;
        Blocks.diffuse.armor = 3f;
        Blocks.sublimate.armor = 4f;
        ((ContinuousLiquidTurret) Blocks.sublimate).range = 120f;
        ((ContinuousLiquidTurret) Blocks.sublimate).ammo(HILiquids.methane, new ContinuousFlameBulletType() {{
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
        //Blocks-Units
        ((UnitFactory) Blocks.groundFactory).plans.add(new UnitPlan(HIUnitTypes.vanguard, 1200f, with(Items.lead, 25, Items.titanium, 25, Items.silicon, 30)));
        ((UnitFactory) Blocks.airFactory).plans.add(new UnitPlan(HIUnitTypes.caelifera, 1200f, with(Items.lead, 35, Items.titanium, 15, Items.silicon, 30)));
        ((Reconstructor) Blocks.additiveReconstructor).upgrades.add(new UnitType[]{HIUnitTypes.vanguard, HIUnitTypes.striker}, new UnitType[]{HIUnitTypes.caelifera, HIUnitTypes.schistocerca});
        ((Reconstructor) Blocks.multiplicativeReconstructor).upgrades.add(new UnitType[]{HIUnitTypes.striker, HIUnitTypes.counterattack}, new UnitType[]{HIUnitTypes.schistocerca, HIUnitTypes.anthophila});
        ((Reconstructor) Blocks.exponentialReconstructor).upgrades.add(new UnitType[]{HIUnitTypes.counterattack, HIUnitTypes.crush}, new UnitType[]{HIUnitTypes.anthophila, HIUnitTypes.vespula});
        ((Reconstructor) Blocks.tetrativeReconstructor).upgrades.add(new UnitType[]{HIUnitTypes.crush, HIUnitTypes.destruction}, new UnitType[]{HIUnitTypes.vespula, HIUnitTypes.lepidoptera});
        //Blocks-Units-Erekir
        ((Constructor) Blocks.constructor).filter = Seq.with();
        ((UnitAssembler) Blocks.tankAssembler).plans.add(new AssemblerUnitPlan(HIUnitTypes.dominate, 60f * 60f * 4f, PayloadStack.list(UnitTypes.precept, 4, HIBlocks.aparajitoLarge, 20)));
        ((UnitAssembler) Blocks.shipAssembler).plans.add(new AssemblerUnitPlan(HIUnitTypes.havoc, 60f * 60f * 4f, PayloadStack.list(UnitTypes.obviate, 4, HIBlocks.aparajitoLarge, 20)));
        ((UnitAssembler) Blocks.mechAssembler).plans.add(new AssemblerUnitPlan(HIUnitTypes.oracle, 60f * 60f * 4f, PayloadStack.list(UnitTypes.anthicus, 4, HIBlocks.aparajitoLarge, 20)));
        //UnitTypes
        UnitTypes.alpha.coreUnitDock = true;
        UnitTypes.beta.coreUnitDock = true;
        UnitTypes.gamma.coreUnitDock = true;
        //UnitTypes-Erekir
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
        //Liquids
        Liquids.slag.temperature = 2f;
        Liquids.hydrogen.flammability = 1.5f;
        Liquids.hydrogen.explosiveness = 1.5f;
        Liquids.ozone.flammability = 0f;
        Liquids.ozone.explosiveness = 0f;
        Liquids.neoplasm.canStayOn.addAll(HILiquids.nanofluid, HILiquids.nitratedOil);
        Liquids.neoplasm.capPuddles = true;
        //Items
        Items.graphite.hardness = 2;
        Items.metaglass.hardness = 2;
        Items.silicon.hardness = 2;
        Items.plastanium.hardness = 3;
        Items.surgeAlloy.hardness = 6;
        Items.phaseFabric.hardness = 3;
        Items.carbide.hardness = 6;
        Items.serpuloItems.addAll(HIItems.rareEarth, HIItems.nanocore, HIItems.chromium, HIItems.uranium, HIItems.heavyAlloy);
        Items.erekirItems.addAll(HIItems.nanocoreErekir, HIItems.uranium, HIItems.chromium);
        //planet
        Planets.serpulo.allowSectorInvasion = settings.getBool("hi-serpulo-sector-invasion");
        //other
        boolean replaceWater = settings.getBool("hi-replace-water-surface");
        for (Block b : content.blocks()) {
            if (b == null) continue;
            if (replaceWater && b.cacheLayer == CacheLayer.water) {
                b.cacheLayer = HICacheLayer.dalani;
                continue;
            }
            if (b instanceof BurstDrill bu) {
                bu.drillMultipliers.putAll(bur);
                if (bu == Blocks.impactDrill || bu == Blocks.eruptionDrill) {
                    bu.drillMultipliers.remove(Items.thorium, 1);
                }
            }
        }
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

    static float hard(Item item) {
        return switch (item.hardness) {
            case 0 -> 4f;
            case 1 -> 3.5f;
            case 2 -> 3f;
            case 3 -> 2.5f;
            case 4 -> 2f;
            case 5 -> 1.5f;
            case 6 -> 1f;
            default -> 0.5f;
        };
    }
}
