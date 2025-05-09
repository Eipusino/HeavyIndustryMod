package heavyindustry.core;

import heavyindustry.ai.HealingDefenderAI;
import heavyindustry.ai.MinerPointAI;
import heavyindustry.ai.NullAI;
import heavyindustry.ai.ReloadingAI;
import heavyindustry.ai.SentryAI;
import heavyindustry.ai.SniperAI;
import heavyindustry.ai.SurroundAI;
import heavyindustry.ai.TargetCargoAI;
import heavyindustry.entities.abilities.BatteryAbility;
import heavyindustry.entities.abilities.DeathAbility;
import heavyindustry.entities.abilities.HealAbility;
import heavyindustry.entities.abilities.JavelinAbility;
import heavyindustry.entities.abilities.LightLandingAbility;
import heavyindustry.entities.abilities.LightSpeedAbility;
import heavyindustry.entities.abilities.MendFieldAbility;
import heavyindustry.entities.abilities.MindControlFieldAbility;
import heavyindustry.entities.abilities.MinigunAbility;
import heavyindustry.entities.abilities.MirrorArmorAbility;
import heavyindustry.entities.abilities.MirrorFieldAbility;
import heavyindustry.entities.abilities.RegenerationAbility;
import heavyindustry.entities.abilities.ShockWaveAbility;
import heavyindustry.entities.abilities.SuicideExplosionAbility;
import heavyindustry.entities.abilities.SurroundRegenAbility;
import heavyindustry.entities.abilities.SuspiciousAbility;
import heavyindustry.entities.abilities.SwapHealthAbility;
import heavyindustry.entities.abilities.TerritoryFieldAbility;
import heavyindustry.entities.abilities.ToxicAbility;
import heavyindustry.entities.bullet.*;
import heavyindustry.entities.effect.WrapperEffect;
import heavyindustry.entities.part.AimPart;
import heavyindustry.entities.part.ArcCharge;
import heavyindustry.entities.part.BowHalo;
import heavyindustry.entities.part.ConstructPart;
import heavyindustry.entities.part.CustomPart;
import heavyindustry.entities.part.HPartProgress;
import heavyindustry.entities.part.PartBow;
import heavyindustry.entities.part.RangeCirclePart;
import heavyindustry.entities.pattern.ShootBursts;
import heavyindustry.entities.pattern.ShootHelixf;
import heavyindustry.type.AtmospherePlanet;
import heavyindustry.type.ExtraSectorPreset;
import heavyindustry.type.MultiCellLiquid;
import heavyindustry.type.NonThreateningSector;
import heavyindustry.type.ammo.VanityAmmoType;
import heavyindustry.type.unit.AncientUnitType;
import heavyindustry.type.unit.CopterUnitType;
import heavyindustry.type.unit.DoubleLegMechUnitType;
import heavyindustry.type.unit.EnergyUnitType;
import heavyindustry.type.unit.ExtraUnitType;
import heavyindustry.type.unit.NucleoidUnitType;
import heavyindustry.type.unit.OrnitopterUnitType;
import heavyindustry.type.unit.PesterUnitType;
import heavyindustry.type.unit.SentryUnitType;
import heavyindustry.type.unit.SwordUnitType;
import heavyindustry.type.unit.TractorBeamUnitType;
import heavyindustry.type.weapons.AcceleratingWeapon;
import heavyindustry.type.weapons.BoostWeapon;
import heavyindustry.type.weapons.EnergyChargeWeapon;
import heavyindustry.type.weapons.FilterWeapon;
import heavyindustry.type.weapons.LimitedAngleWeapon;
import heavyindustry.type.weapons.MortarWeapon;
import heavyindustry.type.weapons.MultiBarrelWeapon;
import heavyindustry.type.weapons.PointDefenceMultiBarrelWeapon;
import heavyindustry.type.weather.EffectWeather;
import heavyindustry.type.weather.HailStormWeather;
import heavyindustry.type.weather.SpawnerWeather;
import heavyindustry.world.blocks.CustomShapeBlock;
import heavyindustry.world.blocks.campaign.CaptureBlock;
import heavyindustry.world.blocks.defense.AdjustableOverdrive;
import heavyindustry.world.blocks.defense.AirRaider;
import heavyindustry.world.blocks.defense.AparajitoWall;
import heavyindustry.world.blocks.defense.AssignOverdrive;
import heavyindustry.world.blocks.defense.BatteryWall;
import heavyindustry.world.blocks.defense.BombLauncher;
import heavyindustry.world.blocks.defense.CliffExplosive;
import heavyindustry.world.blocks.defense.EffectZone;
import heavyindustry.world.blocks.defense.Explosive;
import heavyindustry.world.blocks.defense.IndestructibleWall;
import heavyindustry.world.blocks.defense.InsulationWall;
import heavyindustry.world.blocks.defense.LaserWall;
import heavyindustry.world.blocks.defense.RegenWall;
import heavyindustry.world.blocks.defense.ShapedWall;
import heavyindustry.world.blocks.defense.StaticNode;
import heavyindustry.world.blocks.defense.Thorns;
import heavyindustry.world.blocks.defense.turrets.HackTurret;
import heavyindustry.world.blocks.defense.turrets.MinigunTurret;
import heavyindustry.world.blocks.defense.turrets.MultiBulletTurret;
import heavyindustry.world.blocks.defense.turrets.MultiTractorBeamTurret;
import heavyindustry.world.blocks.defense.turrets.PlatformTurret;
import heavyindustry.world.blocks.defense.turrets.ShootMatchTurret;
import heavyindustry.world.blocks.defense.turrets.SpeedupTurret;
import heavyindustry.world.blocks.defense.turrets.SwingContinuousTurret;
import heavyindustry.world.blocks.distribution.BeltConveyor;
import heavyindustry.world.blocks.distribution.BeltStackConveyor;
import heavyindustry.world.blocks.distribution.CoveredConveyor;
import heavyindustry.world.blocks.distribution.CoveredRouter;
import heavyindustry.world.blocks.distribution.XLDirectionalUnloader;
import heavyindustry.world.blocks.distribution.DuctJunction;
import heavyindustry.world.blocks.distribution.DuctNode;
import heavyindustry.world.blocks.distribution.HeavyDuct;
import heavyindustry.world.blocks.distribution.InstantBridge;
import heavyindustry.world.blocks.distribution.InvertedJunction;
import heavyindustry.world.blocks.distribution.LaserMassDriver;
import heavyindustry.world.blocks.distribution.ModifiedDuctBridge;
import heavyindustry.world.blocks.distribution.MultiJunction;
import heavyindustry.world.blocks.distribution.MultiRouter;
import heavyindustry.world.blocks.distribution.MultiSorter;
import heavyindustry.world.blocks.distribution.NodeBridge;
import heavyindustry.world.blocks.distribution.OverchargeDuct;
import heavyindustry.world.blocks.distribution.RotatorRouter;
import heavyindustry.world.blocks.distribution.SorterRevamp;
import heavyindustry.world.blocks.distribution.StackBridge;
import heavyindustry.world.blocks.distribution.StackHelper;
import heavyindustry.world.blocks.distribution.TubeConveyor;
import heavyindustry.world.blocks.distribution.TubeDistributor;
import heavyindustry.world.blocks.distribution.TubeDuct;
import heavyindustry.world.blocks.distribution.TubeItemBridge;
import heavyindustry.world.blocks.distribution.XLDirectionalUnloader.XLDirectionalUnloaderBuild;
import heavyindustry.world.blocks.environment.ArmorFloor;
import heavyindustry.world.blocks.environment.ConnectedWall;
import heavyindustry.world.blocks.environment.DataFloor;
import heavyindustry.world.blocks.environment.GrooveFloor;
import heavyindustry.world.blocks.environment.SizedVent;
import heavyindustry.world.blocks.environment.TallTreeBlock;
import heavyindustry.world.blocks.environment.TiledFloor;
import heavyindustry.world.blocks.environment.UndergroundOreBlock;
import heavyindustry.world.blocks.heat.FuelHeater;
import heavyindustry.world.blocks.heat.HeatDriver;
import heavyindustry.world.blocks.heat.HeatGenerator;
import heavyindustry.world.blocks.heat.HeatMultiCrafter;
import heavyindustry.world.blocks.heat.ThermalHeater;
import heavyindustry.world.blocks.liquid.BeltConduit;
import heavyindustry.world.blocks.liquid.BurstPump;
import heavyindustry.world.blocks.liquid.LiquidDirectionalUnloader;
import heavyindustry.world.blocks.liquid.LiquidMassDriver;
import heavyindustry.world.blocks.liquid.LiquidOverflowValve;
import heavyindustry.world.blocks.liquid.LiquidUnloader;
import heavyindustry.world.blocks.liquid.MergingLiquidBlock;
import heavyindustry.world.blocks.liquid.Pipe;
import heavyindustry.world.blocks.liquid.PipeBridge;
import heavyindustry.world.blocks.liquid.SortLiquidRouter;
import heavyindustry.world.blocks.liquid.ThermalPump;
import heavyindustry.world.blocks.liquid.TubeConduit;
import heavyindustry.world.blocks.liquid.TubeLiquidBridge;
import heavyindustry.world.blocks.logic.CopyMemoryBlock;
import heavyindustry.world.blocks.logic.LabelMessageBlock;
import heavyindustry.world.blocks.logic.LaserRuler;
import heavyindustry.world.blocks.logic.ProcessorCooler;
import heavyindustry.world.blocks.logic.ProcessorFan;
import heavyindustry.world.blocks.payload.PayloadBuffer;
import heavyindustry.world.blocks.payload.PayloadCompCons;
import heavyindustry.world.blocks.payload.PayloadCrafter;
import heavyindustry.world.blocks.payload.PayloadCrane;
import heavyindustry.world.blocks.payload.PayloadDuct;
import heavyindustry.world.blocks.payload.PayloadDuctRouter;
import heavyindustry.world.blocks.payload.PayloadJunction;
import heavyindustry.world.blocks.payload.PayloadManuGrid;
import heavyindustry.world.blocks.payload.PayloadRail;
import heavyindustry.world.blocks.payload.SingleProducer;
import heavyindustry.world.blocks.power.BeamDiode;
import heavyindustry.world.blocks.power.ConsumeVariableReactor;
import heavyindustry.world.blocks.power.HyperGenerator;
import heavyindustry.world.blocks.power.ImpulseNode;
import heavyindustry.world.blocks.power.LunarGenerator;
import heavyindustry.world.blocks.power.PowerAnalyzer;
import heavyindustry.world.blocks.power.PowerTower;
import heavyindustry.world.blocks.power.SmartBeamNode;
import heavyindustry.world.blocks.power.SmartPowerNode;
import heavyindustry.world.blocks.power.SpaceGenerator;
import heavyindustry.world.blocks.power.ThermalConsumeGenerator;
import heavyindustry.world.blocks.power.WindGenerator;
import heavyindustry.world.blocks.production.AccelerationCrafter;
import heavyindustry.world.blocks.production.AttributeGenerator;
import heavyindustry.world.blocks.production.ConfigIncinerator;
import heavyindustry.world.blocks.production.DrawerBurstDrill;
import heavyindustry.world.blocks.production.DrawerDrill;
import heavyindustry.world.blocks.production.FilterCrafter;
import heavyindustry.world.blocks.production.FuelCrafter;
import heavyindustry.world.blocks.production.GeneratorCrafter;
import heavyindustry.world.blocks.production.GeneratorFracker;
import heavyindustry.world.blocks.production.HeatDrill;
import heavyindustry.world.blocks.production.HeatProducerDrill;
import heavyindustry.world.blocks.production.LaserBeamDrill;
import heavyindustry.world.blocks.production.MinerPoint;
import heavyindustry.world.blocks.production.MultiCrafter;
import heavyindustry.world.blocks.production.MultiDrill;
import heavyindustry.world.blocks.production.MultiRotatorDrill;
import heavyindustry.world.blocks.production.OreDetector;
import heavyindustry.world.blocks.production.SingleDrill;
import heavyindustry.world.blocks.production.SporeFarm;
import heavyindustry.world.blocks.production.UndergroundDrill;
import heavyindustry.world.blocks.sandbox.AdaptiveSource;
import heavyindustry.world.blocks.storage.CoreStorageBlock;
import heavyindustry.world.blocks.storage.CoreUnloader;
import heavyindustry.world.blocks.storage.DetectorCoreBlock;
import heavyindustry.world.blocks.storage.FrontlineCoreBlock;
import heavyindustry.world.blocks.storage.ResourceUnloader;
import heavyindustry.world.blocks.storage.ResourcesDispatchingCenter;
import heavyindustry.world.blocks.storage.SpaceUnloader;
import heavyindustry.world.blocks.storage.TurretCoreBlock;
import heavyindustry.world.blocks.storage.XLUnloader;
import heavyindustry.world.blocks.storage.XLUnloader.XLUnloaderBuild;
import heavyindustry.world.blocks.units.Collector;
import heavyindustry.world.blocks.units.DerivativeUnitFactory;
import heavyindustry.world.blocks.units.IndestructibleUnitFactory;
import heavyindustry.world.blocks.units.JumpGate;
import heavyindustry.world.blocks.units.MechPad;
import heavyindustry.world.blocks.units.PayloadSourcef;
import heavyindustry.world.blocks.units.SelectableReconstructor;
import heavyindustry.world.blocks.units.UnitBoost;
import heavyindustry.world.blocks.units.UnitIniter;
import heavyindustry.world.consumers.ConsumeLiquidDynamic;
import heavyindustry.world.draw.Draw3dSpin;
import heavyindustry.world.draw.DrawAnim;
import heavyindustry.world.draw.DrawAntiSpliceBlock;
import heavyindustry.world.draw.DrawDirSpliceBlock;
import heavyindustry.world.draw.DrawEdgeLinkBits;
import heavyindustry.world.draw.DrawExpandPlasma;
import heavyindustry.world.draw.DrawFactories;
import heavyindustry.world.draw.DrawFrame;
import heavyindustry.world.draw.DrawHalfSpinner;
import heavyindustry.world.draw.DrawPayloadFactory;
import heavyindustry.world.draw.DrawPowerLight;
import heavyindustry.world.draw.DrawPress;
import heavyindustry.world.draw.DrawPrinter;
import heavyindustry.world.draw.DrawRegionDynamic;
import heavyindustry.world.draw.DrawRotator;
import heavyindustry.world.draw.DrawScanLine;
import heavyindustry.world.draw.DrawSpecConstruct;
import heavyindustry.world.draw.DrawTeam;
import heavyindustry.world.draw.DrawWeaveColor;
import heavyindustry.world.draw.DrawZSet;
import heavyindustry.world.draw.MultiDrawBlock;
import heavyindustry.world.draw.MultiDrawFlame;
import heavyindustry.world.draw.RunningLight;

import static mindustry.mod.ClassMap.classes;

/** Generated class. Maps simple class names to concrete classes. For use in JSON attached mods. */
final class HClassMap {
	/** Don't let anyone instantiate this class. */
	private HClassMap() {}

	static void load() {
		//ai
		classes.put("NullAI", NullAI.class);
		classes.put("HealingDefenderAI", HealingDefenderAI.class);
		classes.put("SentryAI", SentryAI.class);
		classes.put("SniperAI", SniperAI.class);
		classes.put("SurroundAI", SurroundAI.class);
		classes.put("ReloadingAI", ReloadingAI.class);
		classes.put("TargetCargoAI", TargetCargoAI.class);
		classes.put("MinerPointAI", MinerPointAI.class);
		//ability
		classes.put("BatteryAbility", BatteryAbility.class);
		classes.put("DeathAbility", DeathAbility.class);
		classes.put("HealAbility", HealAbility.class);
		classes.put("JavelinAbility", JavelinAbility.class);
		classes.put("LightLandingAbility", LightLandingAbility.class);
		classes.put("LightSpeedAbility", LightSpeedAbility.class);
		classes.put("MendFieldAbility", MendFieldAbility.class);
		classes.put("MindControlFieldAbility", MindControlFieldAbility.class);
		classes.put("MinigunAbility", MinigunAbility.class);
		classes.put("MirrorArmorAbility", MirrorArmorAbility.class);
		classes.put("MirrorFieldAbility", MirrorFieldAbility.class);
		classes.put("RegenerationAbility", RegenerationAbility.class);
		classes.put("ShockWaveAbility", ShockWaveAbility.class);
		classes.put("SuicideExplosionAbility", SuicideExplosionAbility.class);
		classes.put("SurroundRegenAbility", SurroundRegenAbility.class);
		classes.put("SuspiciousAbility", SuspiciousAbility.class);
		classes.put("SwapHealthAbility", SwapHealthAbility.class);
		classes.put("TerritoryFieldAbility", TerritoryFieldAbility.class);
		classes.put("ToxicAbility", ToxicAbility.class);
		//bullets
		classes.put("AccelBulletType", AccelBulletType.class);
		classes.put("AimToPosBulletType", AimToPosBulletType.class);
		classes.put("AntiBulletFlakBulletType", AntiBulletFlakBulletType.class);
		classes.put("ArrowBulletType", ArrowBulletType.class);
		classes.put("BoidBulletType", BoidBulletType.class);
		classes.put("ChainBulletType", ChainBulletType.class);
		classes.put("ChainLightningBulletType", ChainLightningBulletType.class);
		classes.put("CritBulletType", CritBulletType.class);
		classes.put("CtrlMissileBulletType", CtrlMissileBulletType.class);
		classes.put("DelayedPointBulletType", DelayedPointBulletType.class);
		classes.put("DistFieldBulletType", DistFieldBulletType.class);
		classes.put("EdgeFragBulletType", EdgeFragBulletType.class);
		classes.put("EffectBulletType", EffectBulletType.class);
		classes.put("ElectricStormBulletType", ElectricStormBulletType.class);
		classes.put("FallingBulletType", FallingBulletType.class);
		classes.put("FireWorkBulletType", FireWorkBulletType.class);
		classes.put("FlameBulletType", FlameBulletType.class);
		classes.put("FlameBulletTypef", FlameBulletTypef.class);
		classes.put("ColorFireBulletType", FireWorkBulletType.ColorFireBulletType.class);
		classes.put("SpriteBulletType", FireWorkBulletType.SpriteBulletType.class);
		classes.put("GrenadeBulletType", GrenadeBulletType.class);
		classes.put("GuidedMissileBulletType", GuidedMissileBulletType.class);
		classes.put("HailStoneBulletType", HailStoneBulletType.class);
		classes.put("LightningLinkerBulletType", LightningLinkerBulletType.class);
		classes.put("LiquidMassDriverBolt", LiquidMassDriverBolt.class);
		classes.put("MultiBulletType", MultiBulletType.class);
		classes.put("MultiTrailBulletType", MultiTrailBulletType.class);
		classes.put("PositionLightningBulletType", PositionLightningBulletType.class);
		classes.put("ShieldBreakerType", ShieldBreakerType.class);
		classes.put("Shooter", LaserWall.Shooter.class);
		classes.put("StrafeLaserBulletType", StrafeLaserBulletType.class);
		classes.put("ThermoBulletType", ThermoBulletType.class);
		classes.put("TrailedEnergyBulletType", TrailedEnergyBulletType.class);
		classes.put("TrailFadeBulletType", TrailFadeBulletType.class);
		classes.put("TreeLightningBulletType", TreeLightningBulletType.class);
		//effects
		classes.put("WrapperEffect", WrapperEffect.class);
		//parts
		classes.put("AimPart", AimPart.class);
		classes.put("BowHalo", BowHalo.class);
		classes.put("PartBow", PartBow.class);
		classes.put("RunningLight", RunningLight.class);
		classes.put("ArcCharge", ArcCharge.class);
		classes.put("HIDrawPart", HPartProgress.class);
		classes.put("ConstructPart", ConstructPart.class);
		classes.put("CustomPart", CustomPart.class);
		classes.put("RangeCirclePart", RangeCirclePart.class);
		//sector
		classes.put("NonThreateningSector", NonThreateningSector.class);
		//ammo
		classes.put("VanityAmmoType", VanityAmmoType.class);
		//patterns
		classes.put("ShootBursts", ShootBursts.class);
		classes.put("ShootHelixf", ShootHelixf.class);
		//types-unit
		classes.put("ExtraUnitType", ExtraUnitType.class);
		classes.put("AncientUnitType", AncientUnitType.class);
		classes.put("CopterUnitType", CopterUnitType.class);
		classes.put("DoubleLegMechUnitType", DoubleLegMechUnitType.class);
		classes.put("EnergyUnitType", EnergyUnitType.class);
		classes.put("NucleoidUnitType", NucleoidUnitType.class);
		classes.put("OrnitopterUnitType", OrnitopterUnitType.class);
		classes.put("PesterUnitType", PesterUnitType.class);
		classes.put("SentryUnitType", SentryUnitType.class);
		classes.put("SwordUnitType", SwordUnitType.class);
		classes.put("TractorBeamUnitType", TractorBeamUnitType.class);
		//types-weapon
		classes.put("AcceleratingWeapon", AcceleratingWeapon.class);
		classes.put("BoostWeapon", BoostWeapon.class);
		classes.put("EnergyChargeWeapon", EnergyChargeWeapon.class);
		classes.put("FilterWeapon", FilterWeapon.class);
		classes.put("LimitedAngleWeapon", LimitedAngleWeapon.class);
		classes.put("MortarWeapon", MortarWeapon.class);
		classes.put("MultiBarrelWeapon", MultiBarrelWeapon.class);
		classes.put("PointDefenceMultiBarrelWeapon", PointDefenceMultiBarrelWeapon.class);
		//types-liquid
		classes.put("MultiCellLiquid", MultiCellLiquid.class);
		//types-planet
		classes.put("AtmospherePlanet", AtmospherePlanet.class);
		//types-sector
		classes.put("ExtraSectorPreset", ExtraSectorPreset.class);
		//types-weather
		classes.put("EffectWeather", EffectWeather.class);
		classes.put("HailStormWeather", HailStormWeather.class);
		classes.put("SpawnerWeather", SpawnerWeather.class);
		//blocks
		classes.put("CustomShapeBlock", CustomShapeBlock.class);
		classes.put("CustomFormBuild", CustomShapeBlock.CustomFormBuild.class);
		classes.put("ConnectedWall", ConnectedWall.class);
		classes.put("GrooveFloor", GrooveFloor.class);
		classes.put("TiledFloor", TiledFloor.class);
		classes.put("SizedVent", SizedVent.class);
		classes.put("ArmorFloor", ArmorFloor.class);
		classes.put("DataFloor", DataFloor.class);
		classes.put("UndergroundOreBlock", UndergroundOreBlock.class);
		classes.put("TallTreeBlock", TallTreeBlock.class);
		classes.put("StaticNode", StaticNode.class);
		classes.put("StaticNodeBuild", StaticNode.StaticNodeBuild.class);
		classes.put("EffectZone", EffectZone.class);
		classes.put("EffectZoneBuild", EffectZone.EffectZoneBuild.class);
		classes.put("AssignOverdrive", AssignOverdrive.class);
		classes.put("AssignOverdriveBuild", AssignOverdrive.AssignOverdriveBuild.class);
		classes.put("AdjustableOverdrive", AdjustableOverdrive.class);
		classes.put("AdjustableOverdriveBuild", AdjustableOverdrive.AdjustableOverdriveBuild.class);
		classes.put("BatteryWall", BatteryWall.class);
		classes.put("BatteryWallBuild", BatteryWall.BatteryWallBuild.class);
		classes.put("InsulationWall", InsulationWall.class);
		classes.put("InsulationWallBuild", InsulationWall.InsulationWallBuild.class);
		classes.put("RegenWall", RegenWall.class);
		classes.put("RegenWallBuild", RegenWall.RegenWallBuild.class);
		classes.put("ShapedWall", ShapedWall.class);
		classes.put("ShapedWallBuild", ShapedWall.ShapedWallBuild.class);
		classes.put("LaserWall", LaserWall.class);
		classes.put("LaserWallBuild", LaserWall.LaserWallBuild.class);
		classes.put("AparajitoWall", AparajitoWall.class);
		classes.put("AparajitoWallBuild", AparajitoWall.AparajitoWallBuild.class);
		classes.put("IndestructibleWall", IndestructibleWall.class);
		classes.put("IndestructibleBuild", IndestructibleWall.IndestructibleWallBuild.class);
		classes.put("Thorns", Thorns.class);
		classes.put("ThornsBuild", Thorns.ThornsBuild.class);
		classes.put("Explosive", Explosive.class);
		classes.put("ExplosiveBuild", Explosive.ExplosiveBuild.class);
		classes.put("CliffExplosive", CliffExplosive.class);
		classes.put("CliffExplosiveBuild", CliffExplosive.CliffExplosiveBuild.class);
		classes.put("BombLauncher", BombLauncher.class);
		classes.put("BombLauncherBuild", BombLauncher.BombLauncherBuild.class);
		classes.put("AirRaider", AirRaider.class);
		classes.put("AirRaiderBuild", AirRaider.AirRaiderBuild.class);
		classes.put("SpeedupTurret", SpeedupTurret.class);
		classes.put("SpeedupTurretBuild", SpeedupTurret.SpeedupTurretBuild.class);
		classes.put("SwingContinuousTurret", SwingContinuousTurret.class);
		classes.put("SwingContinuousTurretBuild", SwingContinuousTurret.SwingContinuousTurretBuild.class);
		classes.put("MultiBulletTurret", MultiBulletTurret.class);
		classes.put("MultiBulletTurretBuild", MultiBulletTurret.MultiBulletTurretBuild.class);
		classes.put("ShootMatchTurret", ShootMatchTurret.class);
		classes.put("ShootMatchTurretBuild", ShootMatchTurret.ShootMatchTurretBuild.class);
		classes.put("MultiTractorBeamTurret", MultiTractorBeamTurret.class);
		classes.put("MultiTractorBeamBuild", MultiTractorBeamTurret.MultiTractorBeamBuild.class);
		classes.put("MinigunTurret", MinigunTurret.class);
		classes.put("MinigunTurretBuild", MinigunTurret.MinigunTurretBuild.class);
		classes.put("PlatformTurret", PlatformTurret.class);
		classes.put("PlatformTurretBuild", PlatformTurret.PlatformTurretBuild.class);
		classes.put("HackTurret", HackTurret.class);
		classes.put("HackTurretBuild", HackTurret.HackTurretBuild.class);
		classes.put("XLDirectionalUnloader", XLDirectionalUnloader.class);
		classes.put("XLDirectionalUnloaderBuild", XLDirectionalUnloaderBuild.class);
		classes.put("BeltConveyor", BeltConveyor.class);
		classes.put("BeltConveyorBuild", BeltConveyor.BeltConveyorBuild.class);
		classes.put("BeltStackConveyor", BeltStackConveyor.class);
		classes.put("BeltStackConveyorBuild", BeltStackConveyor.BeltStackConveyorBuild.class);
		classes.put("CoveredConveyor", CoveredConveyor.class);
		classes.put("CoveredConveyorBuild", CoveredConveyor.CoveredConveyorBuild.class);
		classes.put("TubeConveyor", TubeConveyor.class);
		classes.put("TubeConveyorBuild", TubeConveyor.TubeConveyorBuild.class);
		classes.put("TubeDistributor", TubeDistributor.class);
		classes.put("TubeDistributorBuild", TubeDistributor.TubeDistributorBuild.class);
		classes.put("CoveredRouter", CoveredRouter.class);
		classes.put("CoveredRouterBuild", CoveredRouter.CoveredRouterBuild.class);
		classes.put("RotatorRouter", RotatorRouter.class);
		classes.put("RotatorRouterBuild", RotatorRouter.RotatorRouterBuild.class);
		classes.put("DuctJunction", DuctJunction.class);
		classes.put("DuctJunctionBuild", DuctJunction.DuctJunctionBuild.class);
		classes.put("TubeDuct", TubeDuct.class);
		classes.put("TubeDuctBuild", TubeDuct.TubeDuctBuild.class);
		classes.put("OverchargeDuct", OverchargeDuct.class);
		classes.put("OverchargeDuctBuild", OverchargeDuct.OverchargeDuctBuild.class);
		classes.put("DuctNode", DuctNode.class);
		classes.put("DuctNodeBuild", DuctNode.DuctNodeBuild.class);
		classes.put("HeavyDuct", HeavyDuct.class);
		classes.put("HeavyDuctBuild", HeavyDuct.HeavyDuctBuild.class);
		classes.put("InvertedJunction", InvertedJunction.class);
		classes.put("InvertedJunctionBuild", InvertedJunction.InvertedJunctionBuild.class);
		classes.put("MultiJunction", MultiJunction.class);
		classes.put("MultiJunctionBuild", MultiJunction.MultiJunctionBuild.class);
		classes.put("MultiRouter", MultiRouter.class);
		classes.put("MultiRouterBuild", MultiRouter.MultiRouterBuild.class);
		classes.put("MultiSorter", MultiSorter.class);
		classes.put("MultiSorterBuild", MultiSorter.MultiSorterBuild.class);
		classes.put("TubeItemBridge", TubeItemBridge.class);
		classes.put("TubeItemBridgeBuild", TubeItemBridge.TubeItemBridgeBuild.class);
		classes.put("InstantBridge", InstantBridge.class);
		classes.put("InstantBridgeBuild", InstantBridge.InstantBridgeBuild.class);
		classes.put("NodeBridge", NodeBridge.class);
		classes.put("NodeBridgeBuild", NodeBridge.NodeBridgeBuild.class);
		classes.put("ModifiedDuctBridge", ModifiedDuctBridge.class);
		classes.put("ModifiedDuctBridgeBuild", ModifiedDuctBridge.ModifiedDuctBridgeBuild.class);
		classes.put("SorterRevamp", SorterRevamp.class);
		classes.put("SorterRevampBuild", SorterRevamp.SorterRevampBuild.class);
		classes.put("StackHelper", StackHelper.class);
		classes.put("StackHelperBuild", StackHelper.StackHelperBuild.class);
		classes.put("StackBridge", StackBridge.class);
		classes.put("StackBridgeBuild", StackBridge.StackBridgeBuild.class);
		classes.put("LaserMassDriver", LaserMassDriver.class);
		classes.put("LaserMassDriverBuilding", LaserMassDriver.LaserMassDriverBuilding.class);
		classes.put("TubeLiquidBridge", TubeLiquidBridge.class);
		classes.put("TubeLiquidBridgeBuild", TubeLiquidBridge.TubeLiquidBridgeBuild.class);
		classes.put("FuelHeater", FuelHeater.class);
		classes.put("FuelHeaterBuild", FuelHeater.FuelHeaterBuild.class);
		classes.put("ThermalHeater", ThermalHeater.class);
		classes.put("ThermalHeaterBuild", ThermalHeater.ThermalHeaterBuild.class);
		classes.put("HeatGenerator", HeatGenerator.class);
		classes.put("HeatGeneratorBuild", HeatGenerator.HeatGeneratorBuild.class);
		classes.put("HeatDriver", HeatDriver.class);
		classes.put("HeatDriverBuild", HeatDriver.HeatDriverBuild.class);
		classes.put("HeatMultiCrafter", HeatMultiCrafter.class);
		classes.put("HeatMultiCrafterBuild", HeatMultiCrafter.HeatMultiCrafterBuild.class);
		classes.put("SortLiquidRouter", SortLiquidRouter.class);
		classes.put("SortLiquidRouterBuild", SortLiquidRouter.SortLiquidRouterBuild.class);
		classes.put("BeltConduit", BeltConduit.class);
		classes.put("BeltConduitBuild", BeltConduit.BeltConduitBuild.class);
		classes.put("TubeConduit", TubeConduit.class);
		classes.put("TubeConduitBuild", TubeConduit.TubeConduitBuild.class);
		classes.put("LiquidOverflowValve", LiquidOverflowValve.class);
		classes.put("LiquidOverfloatValveBuild", LiquidOverflowValve.LiquidOverfloatValveBuild.class);
		classes.put("LiquidUnloader", LiquidUnloader.class);
		classes.put("LiquidUnloaderBuild", LiquidUnloader.LiquidUnloaderBuild.class);
		classes.put("LiquidDirectionalUnloader", LiquidDirectionalUnloader.class);
		classes.put("LiquidDirectionalUnloaderBuild", LiquidDirectionalUnloader.LiquidDirectionalUnloaderBuild.class);
		classes.put("ThermalPump", ThermalPump.class);
		classes.put("ThermalPumpBuild", ThermalPump.ThermalPumpBuild.class);
		classes.put("BurstPump", BurstPump.class);
		classes.put("BurstPumpBuild", BurstPump.BurstPumpBuild.class);
		classes.put("LiquidMassDriver", LiquidMassDriver.class);
		classes.put("LiquidMassDriverBuild", LiquidMassDriver.LiquidMassDriverBuild.class);
		classes.put("LiquidBulletData", LiquidMassDriver.LiquidBulletData.class);
		classes.put("MergingLiquidBlock", MergingLiquidBlock.class);
		classes.put("MergingLiquidBuild", MergingLiquidBlock.MergingLiquidBuild.class);
		classes.put("Pipe", Pipe.class);
		classes.put("PipeBuild", Pipe.PipeBuild.class);
		classes.put("PipeBridge", PipeBridge.class);
		classes.put("PipeBridgeBuild", PipeBridge.PipeBridgeBuild.class);
		classes.put("LabelMessageBlock", LabelMessageBlock.class);
		classes.put("LabelMessageBuild", LabelMessageBlock.LabelMessageBuild.class);
		classes.put("LaserRuler", LaserRuler.class);
		classes.put("LaserRulerBuild", LaserRuler.LaserRulerBuild.class);
		classes.put("CopyMemoryBlock", CopyMemoryBlock.class);
		classes.put("CopyMemoryBuild", CopyMemoryBlock.CopyMemoryBuild.class);
		classes.put("ProcessorCooler", ProcessorCooler.class);
		classes.put("ProcessorCoolerBuild", ProcessorCooler.ProcessorCoolerBuild.class);
		classes.put("ProcessorFan", ProcessorFan.class);
		classes.put("ProcessorFanBuild", ProcessorFan.ProcessorFanBuild.class);
		classes.put("SingleProducer", SingleProducer.class);
		classes.put("SingleProducerBuild", SingleProducer.SingleProducerBuild.class);
		classes.put("PayloadCrafter", PayloadCrafter.class);
		classes.put("PayloadCrafterBuild", PayloadCrafter.PayloadCrafterBuild.class);
		classes.put("PayloadRecipe", PayloadCrafter.PayloadRecipe.class);
		classes.put("PayloadDuct", PayloadDuct.class);
		classes.put("PayloadDuctBuild", PayloadDuct.PayloadDuctBuild.class);
		classes.put("PayloadDuctRouter", PayloadDuctRouter.class);
		classes.put("PayloadDuctRouterBuild", PayloadDuctRouter.PayloadDuctRouterBuild.class);
		classes.put("PayloadBuffer", PayloadBuffer.class);
		classes.put("PayloadBufferBuild", PayloadBuffer.PayloadBufferBuild.class);
		classes.put("PayloadJunction", PayloadJunction.class);
		classes.put("PayloadJunctionBuild", PayloadJunction.PayloadJunctionBuild.class);
		classes.put("PayloadRail", PayloadRail.class);
		classes.put("PayloadRailBuild", PayloadRail.PayloadRailBuild.class);
		classes.put("PayloadCrane", PayloadCrane.class);
		classes.put("PayloadCraneBuild", PayloadCrane.PayloadCraneBuild.class);
		classes.put("PayloadManuGrid", PayloadManuGrid.class);
		classes.put("PayloadManuGridBuild", PayloadManuGrid.PayloadManuGridBuild.class);
		classes.put("PayloadCompCons", PayloadCompCons.class);
		classes.put("PayloadCompConsBuild", PayloadCompCons.PayloadCompConsBuild.class);
		classes.put("Collector", Collector.class);
		classes.put("CollectorBuild", Collector.CollectorBuild.class);
		classes.put("DerivativeUnitFactory", DerivativeUnitFactory.class);
		classes.put("DerivativeUnitFactoryBuild", DerivativeUnitFactory.DerivativeUnitFactoryBuild.class);
		classes.put("SelectableReconstructor", SelectableReconstructor.class);
		classes.put("SelectableReconstructorBuild", SelectableReconstructor.SelectableReconstructorBuild.class);
		classes.put("DynamicUnitPlan", SelectableReconstructor.DynamicUnitPlan.class);
		classes.put("MechPad", MechPad.class);
		classes.put("MechPadBuild", MechPad.MechPadBuild.class);
		classes.put("JumpGate", JumpGate.class);
		classes.put("JumpGateBuild", JumpGate.JumpGateBuild.class);
		classes.put("UnitBoost", UnitBoost.class);
		classes.put("UnitBoostBuild", UnitBoost.UnitBoostBuild.class);
		classes.put("UnitIniter", UnitIniter.class);
		classes.put("UnitIniterBuild", UnitIniter.UnitIniterBuild.class);
		classes.put("IndestructibleUnitFactory", IndestructibleUnitFactory.class);
		classes.put("IndestructibleUnitFactoryBuild", IndestructibleUnitFactory.IndestructibleUnitFactoryBuild.class);
		classes.put("PayloadSourcef", PayloadSourcef.class);
		classes.put("PayloadSourceBuildf", PayloadSourcef.PayloadSourceBuildf.class);
		classes.put("WindGenerator", WindGenerator.class);
		classes.put("WindGeneratorBuild", WindGenerator.WindGeneratorBuild.class);
		classes.put("ImpulseNode", ImpulseNode.class);
		classes.put("ImpulseNodeBuild", ImpulseNode.ImpulseNodeBuild.class);
		classes.put("PowerTower", PowerTower.class);
		classes.put("PowerTowerBuild", PowerTower.PowerTowerBuild.class);
		classes.put("PowerAnalyzer", PowerAnalyzer.class);
		classes.put("PowerAnalyzerBuild", PowerAnalyzer.PowerAnalyzerBuild.class);
		classes.put("BeamDiode", BeamDiode.class);
		classes.put("BeamDiodeBuild", BeamDiode.BeamDiodeBuild.class);
		classes.put("SmartBeamNode", SmartBeamNode.class);
		classes.put("SmartBeamNodeBuild", SmartBeamNode.SmartBeamNodeBuild.class);
		classes.put("SmartPowerNode", SmartPowerNode.class);
		classes.put("SmartPowerNodeBuild", SmartPowerNode.SmartPowerNodeBuild.class);
		classes.put("LunarGenerator", LunarGenerator.class);
		classes.put("LunarGeneratorBuild", LunarGenerator.LunarGeneratorBuild.class);
		classes.put("ThermalConsumeGenerator", ThermalConsumeGenerator.class);
		classes.put("ThermalConsumeGeneratorBuild", ThermalConsumeGenerator.ThermalConsumeGeneratorBuild.class);
		classes.put("ConsumeVariableReactor", ConsumeVariableReactor.class);
		classes.put("ConsumeVariableReactorBuild", ConsumeVariableReactor.ConsumeVariableReactorBuild.class);
		classes.put("SpaceGenerator", SpaceGenerator.class);
		classes.put("SpaceGeneratorBuild", SpaceGenerator.SpaceGeneratorBuild.class);
		classes.put("HyperGenerator", HyperGenerator.class);
		classes.put("HyperGeneratorBuild", HyperGenerator.HyperGeneratorBuild.class);
		classes.put("ConfigIncinerator", ConfigIncinerator.class);
		classes.put("ConfigIncineratorBuild", ConfigIncinerator.ConfigIncineratorBuild.class);
		classes.put("GeneratorCrafter", GeneratorCrafter.class);
		classes.put("GeneratorCrafterBuild", GeneratorCrafter.GeneratorCrafterBuild.class);
		classes.put("AttributeGenerator", AttributeGenerator.class);
		classes.put("AttributeGeneratorBuild", AttributeGenerator.AttributeGeneratorBuild.class);
		classes.put("FilterCrafter", FilterCrafter.class);
		classes.put("FilterCrafterBuild", FilterCrafter.FilterCrafterBuild.class);
		classes.put("GeneratorFracker", GeneratorFracker.class);
		classes.put("GeneratorFrackerBuild", GeneratorFracker.GeneratorFrackerBuild.class);
		classes.put("AccelerationCrafter", AccelerationCrafter.class);
		classes.put("AcceleratingCrafterBuild", AccelerationCrafter.AcceleratingCrafterBuild.class);
		classes.put("FuelCrafter", FuelCrafter.class);
		classes.put("FuelCrafterBuild", FuelCrafter.FuelCrafterBuild.class);
		classes.put("MultiCrafter", MultiCrafter.class);
		classes.put("MultiCrafterBuild", MultiCrafter.MultiCrafterBuild.class);
		classes.put("CraftPlan", MultiCrafter.CraftPlan.class);
		classes.put("SporeFarm", SporeFarm.class);
		classes.put("SporeFarmBuild", SporeFarm.SporeFarmBuild.class);
		classes.put("SingleDrill", SingleDrill.class);
		classes.put("SingleDrillBuild", SingleDrill.SingleDrillBuild.class);
		classes.put("DrawerDrill", DrawerDrill.class);
		classes.put("DrawerDrillBuild", DrawerDrill.DrawerDrillBuild.class);
		classes.put("DrawerBurstDrill", DrawerBurstDrill.class);
		classes.put("DrawerBurstDrillBuild", DrawerBurstDrill.DrawerBurstDrillBuild.class);
		classes.put("HeatDrill", HeatDrill.class);
		classes.put("HeatDrillBuild", HeatDrill.HeatDrillBuild.class);
		classes.put("HeatProducerDrill", HeatProducerDrill.class);
		classes.put("HeatProducerDrillBuild", HeatProducerDrill.HeatProducerDrillBuild.class);
		classes.put("LaserBeamDrill", LaserBeamDrill.class);
		classes.put("LaserBeamDrillBuild", LaserBeamDrill.LaserBeamDrillBuild.class);
		classes.put("MultiRotatorDrill", MultiRotatorDrill.class);
		classes.put("MultiRotororDrillBuild", MultiRotatorDrill.MultiRotororDrillBuild.class);
		classes.put("MultiDrill", MultiDrill.class);
		classes.put("MultiDrillBuild", MultiDrill.MultiDrillBuild.class);
		classes.put("UndergroundDrill", UndergroundDrill.class);
		classes.put("UndergroundDrillBuild", UndergroundDrill.UndergroundDrillBuild.class);
		classes.put("MinerPoint", MinerPoint.class);
		classes.put("MinerPointBuild", MinerPoint.MinerPointBuild.class);
		classes.put("OreDetector", OreDetector.class);
		classes.put("OreDetectorBuild", OreDetector.OreDetectorBuild.class);
		classes.put("XLUnloader", XLUnloader.class);
		classes.put("XLUnloaderBuild", XLUnloaderBuild.class);
		classes.put("DetectorCoreBlock", DetectorCoreBlock.class);
		classes.put("DetectorCoreBuild", DetectorCoreBlock.DetectorCoreBuild.class);
		classes.put("FrontlineCoreBlock", FrontlineCoreBlock.class);
		classes.put("FrontlineCoreBuild", FrontlineCoreBlock.FrontlineCoreBuild.class);
		classes.put("TurretCoreBlock", TurretCoreBlock.class);
		classes.put("TurretCoreBuild", TurretCoreBlock.TurretCoreBuild.class);
		classes.put("CoreStorageBlock", CoreStorageBlock.class);
		classes.put("CoreStorageBuild", CoreStorageBlock.CoreStorageBuild.class);
		classes.put("CoreUnloader", CoreUnloader.class);
		classes.put("CoreUnloaderBuild", CoreUnloader.CoreUnloaderBuild.class);
		classes.put("ResourceUnloader", ResourceUnloader.class);
		classes.put("ResourceUnloaderBuild", ResourceUnloader.ResourceUnloaderBuild.class);
		classes.put("AdaptiveSource", AdaptiveSource.class);
		classes.put("AdaptiveSourceBuild", AdaptiveSource.AdaptiveSourceBuild.class);
		classes.put("SpaceUnloader", SpaceUnloader.class);
		classes.put("SpaceUnloaderBuild", SpaceUnloader.SpaceUnloaderBuild.class);
		classes.put("ResourcesDispatchingCenter", ResourcesDispatchingCenter.class);
		classes.put("ResourcesDispatchingCenterBuild", ResourcesDispatchingCenter.ResourcesDispatchingCenterBuild.class);
		classes.put("CaptureBlock", CaptureBlock.class);
		classes.put("CaptureBuild", CaptureBlock.CaptureBuild.class);
		//consume
		classes.put("ConsumeLiquidDynamic", ConsumeLiquidDynamic.class);
		//draw
		classes.put("DrawZSet", DrawZSet.class);
		classes.put("DrawTeam", DrawTeam.class);
		classes.put("DrawHalfSpinner", DrawHalfSpinner.class);
		classes.put("DrawWeaveColor", DrawWeaveColor.class);
		classes.put("MultiDrawBlock", MultiDrawBlock.class);
		classes.put("MultiDrawFlame", MultiDrawFlame.class);
		classes.put("Draw3dSpin", Draw3dSpin.class);
		classes.put("DrawFactories", DrawFactories.class);
		classes.put("DrawPowerLight", DrawPowerLight.class);
		classes.put("DrawPress", DrawPress.class);
		classes.put("DrawPrinter", DrawPrinter.class);
		classes.put("DrawRotator", DrawRotator.class);
		classes.put("DrawScanLine", DrawScanLine.class);
		classes.put("DrawFrame", DrawFrame.class);
		classes.put("DrawAnim", DrawAnim.class);
		classes.put("DrawExpandPlasma", DrawExpandPlasma.class);
		classes.put("DrawSpecConstruct", DrawSpecConstruct.class);
		classes.put("DrawAntiSpliceBlock", DrawAntiSpliceBlock.class);
		classes.put("DrawDirSpliceBlock", DrawDirSpliceBlock.class);
		classes.put("DrawPayloadFactory", DrawPayloadFactory.class);
		classes.put("DrawEdgeLinkBits", DrawEdgeLinkBits.class);
		classes.put("DrawRegionDynamic", DrawRegionDynamic.class);
		classes.put("DrawHeatDriver", HeatDriver.DrawHeatDriver.class);
		classes.put("DrawLiquidMassDriver", LiquidMassDriver.DrawLiquidMassDriver.class);
		classes.put("DrawMinigunTurret", MinigunTurret.DrawMinigunTurret.class);
		classes.put("DrawSwingTurret", SwingContinuousTurret.DrawSwingTurret.class);
	}
}
