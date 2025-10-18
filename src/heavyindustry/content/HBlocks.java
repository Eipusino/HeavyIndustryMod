package heavyindustry.content;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.math.geom.Intersector;
import arc.math.geom.Vec2;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Button;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.HVars;
import heavyindustry.audio.HSounds;
import heavyindustry.entities.HUnitSorts;
import heavyindustry.entities.bullet.ConeFlameBulletType;
import heavyindustry.entities.bullet.CritBulletType;
import heavyindustry.entities.bullet.CtrlMissileBulletType;
import heavyindustry.entities.bullet.EffectBulletType;
import heavyindustry.entities.bullet.EndNukeBulletType;
import heavyindustry.entities.bullet.PositionLightningBulletType;
import heavyindustry.entities.effect.WrapperEffect;
import heavyindustry.entities.part.ArcCharge;
import heavyindustry.entities.part.DrawArrowSequence;
import heavyindustry.entities.part.FlipRegionPart;
import heavyindustry.graphics.Drawn;
import heavyindustry.graphics.HCacheLayer;
import heavyindustry.graphics.HLayer;
import heavyindustry.graphics.HPal;
import heavyindustry.graphics.PositionLightning;
import heavyindustry.type.Recipe;
import heavyindustry.util.Constant;
import heavyindustry.util.Utils;
import heavyindustry.world.blocks.LinkBlock;
import heavyindustry.world.blocks.PlaceholderBlock;
import heavyindustry.world.blocks.defense.AparajitoWall;
import heavyindustry.world.blocks.defense.BombLauncher;
import heavyindustry.world.blocks.defense.ChargeWall;
import heavyindustry.world.blocks.defense.ConfigurableMendProjector;
import heavyindustry.world.blocks.defense.ConfigurableOverdriveProjector;
import heavyindustry.world.blocks.defense.DPSWall;
import heavyindustry.world.blocks.defense.Explosive;
import heavyindustry.world.blocks.defense.IndestructibleWall;
import heavyindustry.world.blocks.defense.InsulationWall;
import heavyindustry.world.blocks.defense.RectOverdriveProjector;
import heavyindustry.world.blocks.defense.ShapedWall;
import heavyindustry.world.blocks.defense.turrets.Cobweb;
import heavyindustry.world.blocks.defense.turrets.MinigunTurret;
import heavyindustry.world.blocks.defense.turrets.PlatformTurret;
import heavyindustry.world.blocks.defense.turrets.SpeedupTurret;
import heavyindustry.world.blocks.defense.turrets.TeslaTurret;
import heavyindustry.world.blocks.distribution.Conveyor2;
import heavyindustry.world.blocks.distribution.CoveredRouter;
import heavyindustry.world.blocks.distribution.DirectionalUnloader2;
import heavyindustry.world.blocks.distribution.InvertedJunction;
import heavyindustry.world.blocks.distribution.MultiJunction;
import heavyindustry.world.blocks.distribution.MultiRouter;
import heavyindustry.world.blocks.distribution.MultiSorter;
import heavyindustry.world.blocks.distribution.NodeBridge;
import heavyindustry.world.blocks.distribution.RailItemBridge;
import heavyindustry.world.blocks.distribution.RailStackBridge;
import heavyindustry.world.blocks.distribution.StackBridge;
import heavyindustry.world.blocks.distribution.StackHelper;
import heavyindustry.world.blocks.distribution.TubeConveyor;
import heavyindustry.world.blocks.distribution.TubeSorter;
import heavyindustry.world.blocks.environment.ConnectedFloor;
import heavyindustry.world.blocks.environment.ConnectedStaticWall;
import heavyindustry.world.blocks.environment.DepthCliff;
import heavyindustry.world.blocks.environment.DepthCliffHelper;
import heavyindustry.world.blocks.heat.ThermalHeater;
import heavyindustry.world.blocks.liquid.ConnectedPump;
import heavyindustry.world.blocks.liquid.LiquidDirectionalUnloader;
import heavyindustry.world.blocks.liquid.LiquidExtractor;
import heavyindustry.world.blocks.liquid.LiquidMassDriver;
import heavyindustry.world.blocks.liquid.LiquidOverflowValve;
import heavyindustry.world.blocks.liquid.LiquidUnloader;
import heavyindustry.world.blocks.liquid.RailLiquidBridge;
import heavyindustry.world.blocks.liquid.SortLiquidRouter;
import heavyindustry.world.blocks.logic.CharacterDisplay;
import heavyindustry.world.blocks.logic.CopyMemoryBlock;
import heavyindustry.world.blocks.logic.IconDisplay;
import heavyindustry.world.blocks.logic.LabelMessageBlock;
import heavyindustry.world.blocks.logic.LaserRuler;
import heavyindustry.world.blocks.logic.ProcessorCooler;
import heavyindustry.world.blocks.logic.ProcessorFan;
import heavyindustry.world.blocks.payload.PayloadJunction;
import heavyindustry.world.blocks.payload.PayloadRail;
import heavyindustry.world.blocks.power.ArmoredPowerNode;
import heavyindustry.world.blocks.power.BeamDiode;
import heavyindustry.world.blocks.power.GlowNuclearReactor;
import heavyindustry.world.blocks.power.HyperGenerator;
import heavyindustry.world.blocks.power.PowerAnalyzer;
import heavyindustry.world.blocks.power.SmartBeamNode;
import heavyindustry.world.blocks.power.SmartPowerNode;
import heavyindustry.world.blocks.production.AdaptiveCrafter;
import heavyindustry.world.blocks.production.Centrifuge;
import heavyindustry.world.blocks.production.LaserBeamDrill;
import heavyindustry.world.blocks.production.SporeFarmBlock;
import heavyindustry.world.blocks.production.UnitMinerDepot;
import heavyindustry.world.blocks.production.UnitMinerPoint;
import heavyindustry.world.blocks.sandbox.AdaptiveSource;
import heavyindustry.world.blocks.sandbox.NextWave;
import heavyindustry.world.blocks.sandbox.RandomSource;
import heavyindustry.world.blocks.storage.CoreStorageBlock;
import heavyindustry.world.blocks.storage.CrashCore;
import heavyindustry.world.blocks.storage.Unloader2;
import heavyindustry.world.blocks.units.PayloadSource2;
import heavyindustry.world.blocks.units.UnitIniter;
import heavyindustry.world.draw.DrawAnim;
import heavyindustry.world.draw.DrawHeat;
import heavyindustry.world.draw.DrawLiquidsOutputs;
import heavyindustry.world.draw.DrawPowerLight;
import heavyindustry.world.draw.DrawPrinter;
import heavyindustry.world.draw.DrawRotator;
import heavyindustry.world.draw.DrawScanLine;
import heavyindustry.world.meta.HAttribute;
import heavyindustry.world.meta.HBuildVisibility;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.Sized;
import mindustry.entities.UnitSorts;
import mindustry.entities.Units;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.FlakBulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.entities.bullet.LiquidBulletType;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.entities.bullet.PointBulletType;
import mindustry.entities.bullet.PointLaserBulletType;
import mindustry.entities.bullet.RailBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.RadialEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.effect.WrapEffect;
import mindustry.entities.part.HaloPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.part.ShapePart;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.pattern.ShootSpread;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Buildingc;
import mindustry.gen.Bullet;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Tex;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.campaign.LandingPad;
import mindustry.world.blocks.campaign.LaunchPad;
import mindustry.world.blocks.defense.AutoDoor;
import mindustry.world.blocks.defense.BaseShield;
import mindustry.world.blocks.defense.Door;
import mindustry.world.blocks.defense.ForceProjector;
import mindustry.world.blocks.defense.MendProjector;
import mindustry.world.blocks.defense.Radar;
import mindustry.world.blocks.defense.RegenProjector;
import mindustry.world.blocks.defense.ShieldWall;
import mindustry.world.blocks.defense.ShockMine;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ContinuousTurret;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.PointDefenseTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.distribution.DuctBridge;
import mindustry.world.blocks.distribution.DuctJunction;
import mindustry.world.blocks.distribution.StackConveyor;
import mindustry.world.blocks.distribution.StackRouter;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.environment.Prop;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.blocks.environment.SteamVent;
import mindustry.world.blocks.environment.TallBlock;
import mindustry.world.blocks.environment.TreeBlock;
import mindustry.world.blocks.heat.HeatProducer;
import mindustry.world.blocks.liquid.ArmoredConduit;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.logic.LogicBlock;
import mindustry.world.blocks.logic.LogicDisplay;
import mindustry.world.blocks.power.Battery;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.LightBlock;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.blocks.power.ThermalGenerator;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.BeamDrill;
import mindustry.world.blocks.production.BurstDrill;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.Fracker;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.HeatCrafter;
import mindustry.world.blocks.production.Pump;
import mindustry.world.blocks.production.Separator;
import mindustry.world.blocks.production.SolidPump;
import mindustry.world.blocks.sandbox.ItemSource;
import mindustry.world.blocks.sandbox.LiquidSource;
import mindustry.world.blocks.sandbox.PowerSource;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.RepairTower;
import mindustry.world.blocks.units.UnitAssemblerModule;
import mindustry.world.consumers.ConsumeCoolant;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumeLiquidFlammable;
import mindustry.world.draw.DrawCells;
import mindustry.world.draw.DrawCircles;
import mindustry.world.draw.DrawCrucibleFlame;
import mindustry.world.draw.DrawCultivator;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawFade;
import mindustry.world.draw.DrawFlame;
import mindustry.world.draw.DrawGlowRegion;
import mindustry.world.draw.DrawHeatInput;
import mindustry.world.draw.DrawHeatOutput;
import mindustry.world.draw.DrawHeatRegion;
import mindustry.world.draw.DrawLiquidRegion;
import mindustry.world.draw.DrawLiquidTile;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawParticles;
import mindustry.world.draw.DrawPistons;
import mindustry.world.draw.DrawPlasma;
import mindustry.world.draw.DrawPower;
import mindustry.world.draw.DrawPulseShape;
import mindustry.world.draw.DrawRegion;
import mindustry.world.draw.DrawShape;
import mindustry.world.draw.DrawTurret;
import mindustry.world.draw.DrawWarmupRegion;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;

import static heavyindustry.HVars.MOD_NAME;

/**
 * Defines the {@linkplain Block blocks} this mod offers.
 * <p>It is now quite large in a scale, even approaching the size of vanilla's {@link Blocks}.
 * But I don't want to divide it into multiple classes.
 *
 * @author Eipusino
 */
public final class HBlocks {
	//environment
	public static DepthCliff cliff;
	public static DepthCliffHelper cliffHelper;
	public static Floor coreZoneCenter, coreZoneDot, darkPanel7, darkPanel8, darkPanel9, darkPanel10, darkPanel11, darkPanelDamaged;
	public static ConnectedFloor metalTiles15, metalTiles16, metalTiles17, metalTiles18;
	public static Floor asphalt;
	public static ConnectedFloor asphaltTiles;
	public static SteamVent shaleVent;
	public static Floor basaltSpikes, basaltPlates;
	public static TallBlock basaltRock;
	public static StaticWall basaltWall, basaltGraphiticWall, basaltPyratiticWall;
	public static Floor snowySand;
	public static StaticWall snowySandWall;
	public static Floor arkyciteSand;
	public static StaticWall arkyciteSandWall;
	public static Prop arkyciteSandBoulder;
	public static Prop darksandBoulder;
	public static Floor concreteBlank, concreteFill, concreteNumber, concreteStripe, concrete;
	public static Floor stoneFullTiles, stoneFull, stoneHalf, stoneTiles;
	public static ConnectedStaticWall concreteWall;
	public static Floor pit, waterPit;
	public static Floor gravel;
	public static Floor deepSlate, deepSlateBrick;
	public static StaticWall deepSlateWall, deepSlateBrickWall;
	public static Floor brine;
	public static Floor crystalFluid, deepCrystalFluid;
	public static Floor metalFloorWater, metalFloorWater2, metalFloorWater3;
	public static Floor metalFloorWater4, metalFloorWater5, metalFloorDamagedWater;
	public static Floor stoneWater;
	public static Floor shaleWater;
	public static Floor basaltWater;
	public static Floor mudWater;
	public static Floor corruptedMoss, corruptedSporeMoss;
	public static StaticWall corruptedSporeRocks, corruptedSporePine;
	public static TreeBlock corruptedSporeFern, corruptedSporePlant, corruptedSporeTree;
	public static Floor mycelium, myceliumSpore;
	public static StaticWall myceliumShrubs, myceliumPine;
	public static Floor softRareEarth, patternRareEarth;
	public static StaticWall softRareEarthWall;
	public static TallBlock crystals, crystalsBoulder;
	public static OreBlock oreSilicon, oreCrystal, oreUranium, oreChromium;
	//wall
	public static Wall copperWallHuge, copperWallGigantic;
	public static Wall armoredWall, armoredWallLarge, armoredWallHuge, armoredWallGigantic;
	public static Wall titaniumWallHuge, titaniumWallGigantic;
	public static Door doorHuge, doorGigantic;
	public static Wall plastaniumWallHuge, plastaniumWallGigantic;
	public static Wall thoriumWallHuge, thoriumWallGigantic;
	public static Wall phaseWallHuge, phaseWallGigantic;
	public static Wall surgeWallHuge, surgeWallGigantic;
	public static Wall uraniumWall, uraniumWallLarge;
	public static Wall chromiumWall, chromiumWallLarge;
	public static AutoDoor chromiumDoor, chromiumDoorLarge;
	public static Wall heavyAlloyWall, heavyAlloyWallLarge;
	public static ChargeWall chargeWall, chargeWallLarge;
	public static ShapedWall shapedWall;
	public static Wall oldTracks;
	//wall-erekir
	public static Wall berylliumWallHuge, berylliumWallGigantic;
	public static Wall tungstenWallHuge, tungstenWallGigantic;
	public static AutoDoor blastDoorLarge, blastDoorHuge;
	public static Wall reinforcedSurgeWallHuge, reinforcedSurgeWallGigantic;
	public static Wall carbideWallHuge, carbideWallGigantic;
	public static Wall shieldedWallLarge, shieldedWallHuge;
	public static AparajitoWall aparajito, aparajitoLarge;
	//drill
	public static Drill titaniumDrill;
	public static SolidPump largeWaterExtractor;
	public static LiquidExtractor slagExtractor;
	public static Fracker oilRig;
	public static BurstDrill blastWell;
	public static Drill ionDrill, cuttingDrill;
	public static LaserBeamDrill beamDrill;
	public static SporeFarmBlock sporeFarm;
	//drill-erekir
	public static BeamDrill heavyPlasmaBore;
	public static UnitMinerPoint unitMinerPoint, unitMinerCenter;
	public static UnitMinerDepot unitMinerDepot;
	//distribution
	public static InvertedJunction invertedJunction;
	public static MultiJunction itemLiquidJunction;
	public static MultiSorter multiSorter;
	public static StackRouter plastaniumRouter;
	public static StackBridge plastaniumBridge;
	public static StackHelper stackHelper;
	public static Conveyor2 chromiumEfficientConveyor, chromiumArmorConveyor;
	public static TubeConveyor chromiumTubeConveyor;
	public static TubeSorter chromiumTubeSorter;
	public static StackConveyor chromiumStackConveyor;
	public static StackRouter chromiumStackRouter;
	public static RailStackBridge chromiumStackBridge;
	public static MultiJunction chromiumJunction;
	public static MultiRouter chromiumRouter;
	public static RailItemBridge chromiumItemBridge;
	public static NodeBridge phaseItemNode;
	public static DirectionalUnloader2 rapidDirectionalUnloader;
	//distribution-erekir
	public static DuctJunction ductJunction;
	public static CoveredRouter ductDistributor;
	public static MultiSorter ductMultiSorter;
	public static DuctBridge armoredDuctBridge;
	public static DirectionalUnloader2 rapidDuctUnloader;
	//liquid
	public static SortLiquidRouter liquidSorter, liquidValve;
	public static LiquidOverflowValve liquidOverflowValve, liquidUnderflowValve;
	public static LiquidUnloader liquidUnloader;
	public static LiquidMassDriver liquidMassDriver;
	public static ConnectedPump turboPumpSmall, turboPump;
	public static NodeBridge phaseLiquidNode;
	public static ArmoredConduit chromiumArmorConduit;
	public static RailLiquidBridge chromiumLiquidBridge;
	public static LiquidRouter chromiumArmorLiquidContainer, chromiumArmorLiquidTank;
	//liquid-erekir
	public static LiquidOverflowValve reinforcedLiquidOverflowValve, reinforcedLiquidUnderflowValve;
	public static LiquidDirectionalUnloader reinforcedLiquidUnloader;
	public static SortLiquidRouter reinforcedLiquidSorter, reinforcedLiquidValve;
	public static Pump smallReinforcedPump, largeReinforcedPump;
	//power
	public static PowerNode networkPowerNode;
	public static SmartPowerNode smartPowerNode;
	public static ArmoredPowerNode microArmoredPowerNode, heavyArmoredPowerNode;
	public static PowerAnalyzer powerAnalyzer;
	public static ConsumeGenerator gasGenerator;
	public static ConsumeGenerator coalPyrolyzer;
	public static ThermalGenerator largeThermalGenerator;
	public static ConsumeGenerator liquidConsumeGenerator;
	public static GlowNuclearReactor uraniumReactor;
	public static HyperGenerator hyperMagneticReactor;
	public static Battery hugeBattery, armoredCoatedBattery;
	//power-erekir
	public static SmartBeamNode smartBeamNode;
	public static BeamDiode beamDiode;
	public static InsulationWall beamInsulator;
	public static PowerAnalyzer reinforcedPowerAnalyzer;
	//production
	public static GenericCrafter largeSiliconSmelter;
	public static GenericCrafter largeKiln;
	public static GenericCrafter largePulverizer;
	public static GenericCrafter largeMelter;
	public static GenericCrafter largeCryofluidMixer;
	public static GenericCrafter largePyratiteMixer;
	public static GenericCrafter largeBlastMixer;
	public static AttributeCrafter largeCultivator;
	public static GenericCrafter stoneCrusher;
	public static GenericCrafter fractionator;
	public static GenericCrafter largePlastaniumCompressor;
	public static GenericCrafter largeSurgeSmelter;
	public static GenericCrafter blastSiliconSmelter;
	public static GenericCrafter crystallineCircuitConstructor;
	public static AdaptiveCrafter crystallineCircuitPrinter;
	public static GenericCrafter crystalActivator;
	public static GenericCrafter largePhaseWeaver;
	public static GenericCrafter phaseFusionInstrument;
	public static GenericCrafter clarifier;
	public static AdaptiveCrafter corkscrewCompressor;
	public static AttributeCrafter atmosphericCollector;
	public static GenericCrafter atmosphericCooler;
	public static GenericCrafter crystalSynthesizer;
	public static GenericCrafter uraniumSynthesizer, chromiumSynthesizer;
	public static GenericCrafter heavyAlloySmelter;
	public static Separator metalAnalyzer;
	public static GenericCrafter nitrificationReactor;
	public static Separator nitratedOilPrecipitator;
	public static GenericCrafter blastReagentMixer;
	public static Centrifuge centrifuge;
	public static GenericCrafter galliumNitrideSmelter;
	//production-erekir
	public static ThermalHeater ventHeater;
	public static GenericCrafter chemicalSiliconSmelter;
	public static HeatProducer largeElectricHeater;
	public static HeatProducer largeOxidationChamber;
	public static HeatCrafter largeSurgeCrucible;
	public static HeatCrafter largeCarbideCrucible;
	//defense
	public static LightBlock lighthouse;
	public static MendProjector mendDome;
	public static RegenProjector sectorStructureMender;
	public static ForceProjector largeShieldGenerator;
	public static ShockMine paralysisMine;
	public static Explosive detonator;
	public static BombLauncher bombLauncher;
	//defense-erekir
	public static Radar largeRadar;
	public static RectOverdriveProjector reinforcedOverdriveProjector;
	//storage
	public static StorageBlock cargo;
	public static StorageBlock bin;
	public static Unloader machineryUnloader;
	public static Unloader2 rapidUnloader;
	public static CoreStorageBlock coreStorage;
	public static CrashCore coreShatter;
	//storage-erekir
	public static CoreStorageBlock reinforcedCoreStorage;
	//payload
	public static PayloadJunction payloadJunction;
	public static PayloadRail payloadRail;
	//payload-erekir
	public static PayloadJunction reinforcedPayloadJunction;
	public static PayloadRail reinforcedPayloadRail;
	//unit
	public static RepairTower unitMaintenanceDepot;
	public static Reconstructor titanReconstructor;
	//unit-erekir
	public static RepairTower largeUnitRepairTower;
	public static UnitAssemblerModule seniorAssemblerModule;
	//logic
	public static LogicBlock matrixProcessor;
	public static LogicDisplay hugeLogicDisplay;
	public static CopyMemoryBlock buffrerdMemoryCell, buffrerdMemoryBank;
	public static ProcessorCooler heatSink;
	public static ProcessorFan heatFan;
	public static ProcessorCooler heatSinkLarge;
	public static LaserRuler laserRuler;
	public static LabelMessageBlock labelMessage;
	public static IconDisplay iconDisplay, iconDisplayLarge;
	public static CharacterDisplay characterDisplay, characterDisplayLarge;
	//logic-erekir
	public static IconDisplay reinforcedIconDisplay, reinforcedIconDisplayLarge;
	public static CharacterDisplay reinforcedCharacterDisplay, reinforcedCharacterDisplayLarge;
	//turret
	public static PointDefenseTurret dissipation;
	public static Cobweb cobweb;
	public static TeslaTurret coilBlaster, electromagneticStorm;
	public static PowerTurret frost, electricArrow;
	public static ItemTurret stabber;
	public static ItemTurret autocannonB6, autocannonF2;
	public static ItemTurret shellshock;
	public static ItemTurret rocketLauncher, largeRocketLauncher;
	public static ItemTurret rocketSilo;
	public static ItemTurret caelum;
	public static ItemTurret mammoth;
	public static ItemTurret dragonBreath;
	public static PowerTurret breakthrough;
	public static ItemTurret cloudBreaker;
	public static LiquidTurret turbulence;
	public static LiquidTurret ironStream;
	public static MinigunTurret minigun;
	public static SpeedupTurret hurricane;
	public static ContinuousTurret judgement;
	public static ItemTurret evilSpirits;
	public static ItemTurret nukeLauncherPlatform;
	public static PowerTurret solstice;
	public static ItemTurret starfall;
	public static PowerTurret annihilate;
	public static PowerTurret executor;
	public static PowerTurret heatDeath;
	//turret-erekir
	public static ItemTurret rupture;
	public static ItemTurret rift;
	//campaign
	public static LaunchPad largeLaunchPad;
	public static LandingPad largeLandingPad;
	//sandbox
	public static UnitIniter unitIniter;
	public static ItemSource reinforcedItemSource;
	public static LiquidSource reinforcedLiquidSource;
	public static PowerSource reinforcedPowerSource;
	public static PayloadSource2 reinforcedPayloadSource;
	public static AdaptiveSource adaptiveSource;
	public static RandomSource randomSource;
	public static Drill staticDrill;
	public static NodeBridge omniNode;
	public static ConfigurableMendProjector configurableMendProjector;
	public static ConfigurableOverdriveProjector configurableOverdriveProjector;
	public static CoreBlock teamChanger;
	public static BaseShield barrierProjector;
	public static ForceProjector entityRemove;
	public static IndestructibleWall invincibleWall, invincibleWallLarge, invincibleWallHuge, invincibleWallGigantic;
	public static DPSWall dpsWall, dpsWallLarge, dpsWallHuge, dpsWallGigantic;
	public static PlatformTurret mustDieTurret, oneShotTurret, pointTurret;
	public static NextWave nextWave;

	//intrernal
	public static final int maxsize = 4;

	public static LinkBlock[] linkBlock, linkBlockLiquid;
	public static PlaceholderBlock[] placeholderBlock;

	private static boolean loadedInternal;

	/** Don't let anyone instantiate this class. */
	private HBlocks() {}

	public static void loadInternal() {
		if (loadedInternal) return;

		loadedInternal = true;

		linkBlock = new LinkBlock[maxsize];
		linkBlockLiquid = new LinkBlock[maxsize];
		placeholderBlock = new PlaceholderBlock[maxsize];

		for (int i = 0; i < maxsize; i++) {
			int j = i + 1;
			linkBlock[i] = new LinkBlock("link-block-" + j) {{
				size = j;
			}};
			linkBlockLiquid[i] = new LinkBlock("link-block-liquid-" + j) {{
				size = j;
				outputsLiquid = true;
			}};
			placeholderBlock[i] = new PlaceholderBlock("placeholder-block-" + j) {{
				size = j;
			}};
		}
	}

	/** Instantiates all contents. Called in the main thread in {@code HeavyIndustryMod.loadContent()}. */
	public static void load() {
		if (HVars.isPlugin) return;//Do not load content in plugin mode.

		//environment
		cliff = new DepthCliff("cliff") {{
			buildType = Constant.PROV_BUILDING;
		}};
		cliffHelper = new DepthCliffHelper("cliff-helper") {{
			buildType = Constant.PROV_BUILDING;
		}};
		coreZoneCenter = new Floor("core-zone-center", 0) {{
			allowCorePlacement = true;
			buildType = Constant.PROV_BUILDING;
		}};
		coreZoneDot = new Floor("core-zone-dot", 0) {{
			allowCorePlacement = true;
			buildType = Constant.PROV_BUILDING;
		}};
		darkPanel7 = new Floor("dark-panel-7", 0) {{
			buildType = Constant.PROV_BUILDING;
		}};
		darkPanel8 = new Floor("dark-panel-8", 0) {{
			buildType = Constant.PROV_BUILDING;
		}};
		darkPanel9 = new Floor("dark-panel-9", 0) {{
			buildType = Constant.PROV_BUILDING;
		}};
		darkPanel10 = new Floor("dark-panel-10", 0) {{
			buildType = Constant.PROV_BUILDING;
		}};
		darkPanel11 = new Floor("dark-panel-11", 0) {{
			buildType = Constant.PROV_BUILDING;
		}};
		darkPanelDamaged = new Floor("dark-panel-damaged", 3) {{
			buildType = Constant.PROV_BUILDING;
		}};
		metalTiles15 = new ConnectedFloor("metal-tiles-15") {{
			autotile = true;
			drawEdgeOut = false;
			drawEdgeIn = false;
			buildType = Constant.PROV_BUILDING;
		}};
		metalTiles16 = new ConnectedFloor("metal-tiles-16") {{
			autotile = true;
			drawEdgeOut = false;
			drawEdgeIn = false;
			buildType = Constant.PROV_BUILDING;
		}};
		metalTiles17 = new ConnectedFloor("metal-tiles-17") {{
			autotile = true;
			drawEdgeOut = false;
			drawEdgeIn = false;
			buildType = Constant.PROV_BUILDING;
		}};
		metalTiles18 = new ConnectedFloor("metal-tiles-18") {{
			autotile = true;
			drawEdgeOut = false;
			drawEdgeIn = false;
			buildType = Constant.PROV_BUILDING;
		}};
		asphalt = new Floor("asphalt", 0) {{
			drawEdgeOut = false;
			drawEdgeIn = false;
			buildType = Constant.PROV_BUILDING;
		}};
		asphaltTiles = new ConnectedFloor("asphalt-tiles") {{
			autotile = true;
			drawEdgeOut = false;
			drawEdgeIn = false;
			blendGroup = asphalt;
			buildType = Constant.PROV_BUILDING;
		}};
		shaleVent = new SteamVent("shale-vent") {{
			variants = 3;
			parent = blendGroup = Blocks.shale;
			attributes.set(Attribute.steam, 1f);
			buildType = Constant.PROV_BUILDING;
		}};
		basaltSpikes = new Floor("basalt-spikes", 4) {{
			itemDrop = HItems.stone;
			playerUnmineable = true;
			attributes.set(Attribute.water, -0.3f);
			buildType = Constant.PROV_BUILDING;
		}};
		basaltPlates = new Floor("basalt-plates") {{
			tilingVariants = 2;
			tilingSize = 4;
			itemDrop = HItems.stone;
			playerUnmineable = true;
			attributes.set(Attribute.water, -0.3f);
			buildType = Constant.PROV_BUILDING;
		}};
		basaltRock = new TallBlock("basalt-rock") {{
			clipSize = 120f;
			variants = 2;
			attributes.set(Attribute.sand, 0.7f);
			buildType = Constant.PROV_BUILDING;
		}};
		basaltWall = new StaticWall("basalt-wall") {{
			variants = 3;
			attributes.set(Attribute.sand, 0.7f);
			basaltSpikes.wall = Blocks.basalt.asFloor().wall = Blocks.hotrock.asFloor().wall = Blocks.magmarock.asFloor().wall = this;
			buildType = Constant.PROV_BUILDING;
		}};
		basaltGraphiticWall = new StaticWall("basalt-graphitic-wall") {{
			itemDrop = Items.graphite;
			variants = 3;
			attributes.set(Attribute.sand, 0.7f);
			buildType = Constant.PROV_BUILDING;
		}};
		basaltPyratiticWall = new StaticWall("basalt-pyratitic-wall") {{
			itemDrop = Items.pyratite;
			variants = 3;
			attributes.set(Attribute.sand, 0.7f);
			buildType = Constant.PROV_BUILDING;
		}};
		snowySand = new Floor("snowy-sand", 3) {{
			itemDrop = Items.sand;
			attributes.set(Attribute.water, 0.2f);
			attributes.set(Attribute.oil, 0.5f);
			buildType = Constant.PROV_BUILDING;
			playerUnmineable = true;
		}};
		snowySandWall = new StaticWall("snowy-sand-wall") {{
			variants = 2;
			attributes.set(Attribute.sand, 2f);
			buildType = Constant.PROV_BUILDING;
		}};
		arkyciteSand = new Floor("arkycite-sand", 3) {{
			itemDrop = Items.sand;
			attributes.set(HAttribute.arkycite, 1);
			buildType = Constant.PROV_BUILDING;
			playerUnmineable = true;
		}};
		arkyciteSandWall = new StaticWall("arkycite-sand-wall") {{
			variants = 2;
			attributes.set(Attribute.sand, 2f);
			buildType = Constant.PROV_BUILDING;
		}};
		arkyciteSandBoulder = new Prop("arkycite-sand-boulder") {{
			variants = 2;
			arkyciteSand.decoration = this;
			buildType = Constant.PROV_BUILDING;
		}};
		darksandBoulder = new Prop("darksand-boulder") {{
			variants = 2;
			Blocks.darksand.asFloor().decoration = this;
			buildType = Constant.PROV_BUILDING;
		}};
		concreteBlank = new Floor("concrete-blank", 3) {{
			buildType = Constant.PROV_BUILDING;
		}};
		concreteFill = new Floor("concrete-fill", 0) {{
			buildType = Constant.PROV_BUILDING;
		}};
		concreteNumber = new Floor("concrete-number", 10) {{
			buildType = Constant.PROV_BUILDING;
		}};
		concreteStripe = new Floor("concrete-stripe", 3) {{
			buildType = Constant.PROV_BUILDING;
		}};
		concrete = new Floor("concrete", 3) {{
			buildType = Constant.PROV_BUILDING;
		}};
		stoneFullTiles = new Floor("stone-full-tiles", 3) {{
			itemDrop = HItems.stone;
			playerUnmineable = true;
			buildType = Constant.PROV_BUILDING;
			wall = Blocks.stoneWall;
		}};
		stoneFull = new Floor("stone-full", 3) {{
			itemDrop = HItems.stone;
			playerUnmineable = true;
			buildType = Constant.PROV_BUILDING;
			wall = Blocks.stoneWall;
		}};
		stoneHalf = new Floor("stone-half", 3) {{
			itemDrop = HItems.stone;
			playerUnmineable = true;
			buildType = Constant.PROV_BUILDING;
			wall = Blocks.stoneWall;
		}};
		stoneTiles = new Floor("stone-tiles", 3) {{
			itemDrop = HItems.stone;
			playerUnmineable = true;
			buildType = Constant.PROV_BUILDING;
			wall = Blocks.stoneWall;
		}};
		concreteWall = new ConnectedStaticWall("concrete-wall") {{
			autotile = true;
			variants = 0;
			buildType = Constant.PROV_BUILDING;
			concreteBlank.wall = concreteFill.wall = concreteNumber.wall = concreteStripe.wall = concrete.wall = this;
		}};
		pit = new Floor("pit", 0) {{
			cacheLayer = HCacheLayer.pit;
			placeableOn = false;
			canShadow = false;
			solid = true;
			buildType = Constant.PROV_BUILDING;
		}
			@Override
			public TextureRegion[] icons() {
				return new TextureRegion[]{fullIcon};
			}
		};
		waterPit = new Floor("water-pit", 0) {{
			cacheLayer = HCacheLayer.waterPit;
			isLiquid = true;
			drownTime = 20f;
			speedMultiplier = 0.1f;
			liquidMultiplier = 2f;
			status = StatusEffects.wet;
			statusDuration = 120f;
			liquidDrop = Liquids.water;
			buildType = Constant.PROV_BUILDING;
		}
			@Override
			public TextureRegion[] icons() {
				return new TextureRegion[]{fullIcon};
			}
		};
		gravel = new Floor("gravel", 5) {{
			itemDrop = HItems.stone;
			playerUnmineable = true;
			buildType = Constant.PROV_BUILDING;
			wall = Blocks.stoneWall;
		}};
		deepSlate = new Floor("deep-slate", 8) {{
			itemDrop = HItems.stone;
			playerUnmineable = true;
			buildType = Constant.PROV_BUILDING;
		}};
		deepSlateBrick = new Floor("deep-slate-brick", 8) {{
			itemDrop = HItems.stone;
			playerUnmineable = true;
			buildType = Constant.PROV_BUILDING;
		}};
		deepSlateWall = new StaticWall("deep-slate-wall") {{
			variants = 4;
			attributes.set(Attribute.sand, 0.7f);
			buildType = Constant.PROV_BUILDING;
			deepSlate.wall = this;
		}};
		deepSlateBrickWall = new StaticWall("deep-slate-brick-wall") {{
			variants = 5;
			attributes.set(Attribute.sand, 0.7f);
			buildType = Constant.PROV_BUILDING;
			deepSlateBrick.wall = this;
		}};
		softRareEarth = new Floor("soft-rare-earth", 3) {{
			itemDrop = HItems.rareEarth;
			buildType = Constant.PROV_BUILDING;
			playerUnmineable = true;
		}};
		patternRareEarth = new Floor("pattern-rare-earth", 4) {{
			itemDrop = HItems.rareEarth;
			buildType = Constant.PROV_BUILDING;
			playerUnmineable = true;
		}};
		softRareEarthWall = new StaticWall("soft-rare-earth-wall") {{
			variants = 2;
			itemDrop = HItems.rareEarth;
			softRareEarth.wall = patternRareEarth.wall = this;
			buildType = Constant.PROV_BUILDING;
		}};
		brine = new Floor("pooled-brine", 0) {{
			drownTime = 200f;
			speedMultiplier = 0.1f;
			variants = 0;
			liquidDrop = HLiquids.brine;
			liquidMultiplier = 1.1f;
			isLiquid = true;
			cacheLayer = HCacheLayer.brine;
			albedo = 1f;
			buildType = Constant.PROV_BUILDING;
		}};
		crystalFluid = new Floor("pooled-crystal-fluid", 0) {{
			status = HStatusEffects.regenerating;
			statusDuration = 60f;
			drownTime = 160f;
			speedMultiplier = 0.8f;
			liquidDrop = HLiquids.crystalFluid;
			isLiquid = true;
			cacheLayer = HCacheLayer.crystalFluid;
			liquidMultiplier = 0.5f;
			emitLight = true;
			lightRadius = 20f;
			lightColor = Color.green.cpy().a(0.19f);
			buildType = Constant.PROV_BUILDING;
		}};
		deepCrystalFluid = new Floor("pooled-deep-crystal-fluid", 0) {{
			status = HStatusEffects.regenerating;
			statusDuration = 180f;
			drownTime = 120f;
			speedMultiplier = 0.6f;
			liquidDrop = HLiquids.crystalFluid;
			isLiquid = true;
			cacheLayer = HCacheLayer.deepCrystalFluid;
			emitLight = true;
			lightRadius = 30f;
			lightColor = Color.green.cpy().a(0.19f);
			buildType = Constant.PROV_BUILDING;
		}};
		metalFloorWater = new Floor("metal-floor-water", 0) {{
			speedMultiplier = 0.6f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
			buildType = Constant.PROV_BUILDING;
		}};
		metalFloorWater2 = new Floor("metal-floor-water-2", 0) {{
			speedMultiplier = 0.6f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
			buildType = Constant.PROV_BUILDING;
		}};
		metalFloorWater3 = new Floor("metal-floor-water-3", 0) {{
			speedMultiplier = 0.6f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
			buildType = Constant.PROV_BUILDING;
		}};
		metalFloorWater4 = new Floor("metal-floor-water-4", 0) {{
			speedMultiplier = 0.6f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
			buildType = Constant.PROV_BUILDING;
		}};
		metalFloorWater5 = new Floor("metal-floor-water-5", 0) {{
			speedMultiplier = 0.6f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
			buildType = Constant.PROV_BUILDING;
		}};
		metalFloorDamagedWater = new Floor("metal-floor-damaged-water", 3) {{
			speedMultiplier = 0.6f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
			buildType = Constant.PROV_BUILDING;
		}};
		stoneWater = new Floor("stone-water", 3) {{
			speedMultiplier = 0.6f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
			buildType = Constant.PROV_BUILDING;
		}};
		shaleWater = new Floor("shale-water", 3) {{
			speedMultiplier = 0.6f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
			buildType = Constant.PROV_BUILDING;
		}};
		basaltWater = new Floor("basalt-water", 3) {{
			speedMultiplier = 0.6f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
			buildType = Constant.PROV_BUILDING;
		}};
		mudWater = new Floor("mud-water", 0) {{
			speedMultiplier = 0.5f;
			liquidDrop = Liquids.water;
			isLiquid = true;
			cacheLayer = CacheLayer.water;
			albedo = 0.9f;
			supportsOverlay = true;
			buildType = Constant.PROV_BUILDING;
		}};
		corruptedMoss = new Floor("corrupted-moss", 3) {{
			speedMultiplier = 0.9f;
			attributes.set(Attribute.water, 0.1f);
			buildType = Constant.PROV_BUILDING;
		}};
		corruptedSporeMoss = new Floor("corrupted-spore-moss", 3) {{
			speedMultiplier = 0.85f;
			attributes.set(Attribute.water, 0.1f);
			buildType = Constant.PROV_BUILDING;
		}};
		corruptedSporeRocks = new StaticWall("corrupted-spore-rocks") {{
			variants = 2;
			corruptedSporeMoss.asFloor().wall = this;
			buildType = Constant.PROV_BUILDING;
		}};
		corruptedSporePine = new StaticWall("corrupted-spore-pine") {{
			variants = 2;
			buildType = Constant.PROV_BUILDING;
		}};
		corruptedSporeFern = new TreeBlock("corrupted-spore-fern") {{
			buildType = Constant.PROV_BUILDING;
		}};
		corruptedSporePlant = new TreeBlock("corrupted-spore-plant") {{
			buildType = Constant.PROV_BUILDING;
		}};
		corruptedSporeTree = new TreeBlock("corrupted-spore-tree") {{
			buildType = Constant.PROV_BUILDING;
		}};
		mycelium = new Floor("mycelium", 3) {{
			speedMultiplier = 0.9f;
			attributes.set(Attribute.water, 0.1f);
			buildType = Constant.PROV_BUILDING;
		}};
		myceliumSpore = new Floor("mycelium-spore", 3) {{
			speedMultiplier = 0.9f;
			attributes.set(Attribute.water, 0.1f);
			buildType = Constant.PROV_BUILDING;
		}};
		myceliumShrubs = new StaticWall("mycelium-shrubs") {{
			variants = 2;
			mycelium.wall = myceliumSpore.wall = this;
			buildType = Constant.PROV_BUILDING;
		}};
		myceliumPine = new StaticWall("mycelium-pine") {{
			variants = 2;
			buildType = Constant.PROV_BUILDING;
		}};
		crystals = new TallBlock("crystals") {{
			variants = 3;
			clipSize = 128f;
			itemDrop = HItems.crystal;
			buildType = Constant.PROV_BUILDING;
		}};
		crystalsBoulder = new TallBlock("crystals-boulder") {{
			variants = 3;
			clipSize = 96f;
			itemDrop = HItems.crystal;
			buildType = Constant.PROV_BUILDING;
		}};
		oreSilicon = new OreBlock("ore-silicon", Items.silicon) {{
			variants = 3;
			buildType = Constant.PROV_BUILDING;
		}};
		oreCrystal = new OreBlock("ore-crystal", HItems.crystal) {{
			variants = 3;
			buildType = Constant.PROV_BUILDING;
		}};
		oreUranium = new OreBlock("ore-uranium", HItems.uranium) {{
			variants = 3;
			oreDefault = true;
			oreThreshold = 0.89f;
			oreScale = 33;
			buildType = Constant.PROV_BUILDING;
		}};
		oreChromium = new OreBlock("ore-chromium", HItems.chromium) {{
			variants = 3;
			oreDefault = true;
			oreThreshold = 0.9f;
			oreScale = 32;
			buildType = Constant.PROV_BUILDING;
		}};
		//wall
		copperWallHuge = new Wall("copper-wall-huge") {{
			requirements(Category.defense, ItemStack.mult(Blocks.copperWall.requirements, 9));
			size = 3;
			health = 2880;
			armor = 1f;
			buildType = WallBuild::new;
		}};
		copperWallGigantic = new Wall("copper-wall-gigantic") {{
			requirements(Category.defense, ItemStack.mult(Blocks.copperWall.requirements, 16));
			size = 4;
			health = 5120;
			armor = 1f;
			buildType = WallBuild::new;
		}};
		armoredWall = new Wall("armored-wall") {{
			requirements(Category.defense, ItemStack.with(Items.copper, 5, Items.lead, 3, Items.graphite, 2));
			size = 1;
			health = 360;
			armor = 5f;
			buildType = WallBuild::new;
		}};
		armoredWallLarge = new Wall("armored-wall-large") {{
			requirements(Category.defense, ItemStack.mult(armoredWall.requirements, 4));
			size = 2;
			health = 1440;
			armor = 5f;
			buildType = WallBuild::new;
		}};
		armoredWallHuge = new Wall("armored-wall-huge") {{
			requirements(Category.defense, ItemStack.mult(armoredWall.requirements, 9));
			size = 3;
			health = 3240;
			armor = 5f;
			buildType = WallBuild::new;
		}};
		armoredWallGigantic = new Wall("armored-wall-gigantic") {{
			requirements(Category.defense, ItemStack.mult(armoredWall.requirements, 16));
			size = 4;
			health = 5760;
			armor = 5f;
			buildType = WallBuild::new;
		}};
		titaniumWallHuge = new Wall("titanium-wall-huge") {{
			requirements(Category.defense, ItemStack.mult(Blocks.titaniumWall.requirements, 9));
			size = 3;
			health = 3960;
			armor = 2f;
			buildType = WallBuild::new;
		}};
		titaniumWallGigantic = new Wall("titanium-wall-gigantic") {{
			requirements(Category.defense, ItemStack.mult(Blocks.titaniumWall.requirements, 16));
			size = 4;
			health = 7040;
			armor = 2f;
			buildType = WallBuild::new;
		}};
		doorHuge = new Door("door-huge") {{
			requirements(Category.defense, ItemStack.mult(Blocks.door.requirements, 9));
			size = 3;
			health = 3600;
			armor = 2f;
			openfx = Fx.dooropen;
			closefx = Fx.doorclose;
			buildType = DoorBuild::new;
		}};
		doorGigantic = new Door("door-gigantic") {{
			requirements(Category.defense, ItemStack.mult(Blocks.door.requirements, 16));
			size = 4;
			health = 6400;
			armor = 2f;
			openfx = Fx.dooropen;
			closefx = Fx.doorclose;
			buildType = DoorBuild::new;
		}};
		plastaniumWallHuge = new Wall("plastanium-wall-huge") {{
			requirements(Category.defense, ItemStack.mult(Blocks.plastaniumWall.requirements, 9));
			size = 3;
			health = 4500;
			armor = 2f;
			insulated = true;
			absorbLasers = true;
			schematicPriority = 10;
			buildType = WallBuild::new;
		}};
		plastaniumWallGigantic = new Wall("plastanium-wall-gigantic") {{
			requirements(Category.defense, ItemStack.mult(Blocks.plastaniumWall.requirements, 16));
			size = 4;
			health = 8000;
			armor = 2f;
			insulated = true;
			absorbLasers = true;
			schematicPriority = 10;
			buildType = WallBuild::new;
		}};
		thoriumWallHuge = new Wall("thorium-wall-huge") {{
			requirements(Category.defense, ItemStack.mult(Blocks.thoriumWall.requirements, 9));
			size = 3;
			health = 7200;
			armor = 8f;
			buildType = WallBuild::new;
		}};
		thoriumWallGigantic = new Wall("thorium-wall-gigantic") {{
			requirements(Category.defense, ItemStack.mult(Blocks.thoriumWall.requirements, 16));
			size = 4;
			health = 12800;
			armor = 8f;
			buildType = WallBuild::new;
		}};
		phaseWallHuge = new Wall("phase-wall-huge") {{
			requirements(Category.defense, ItemStack.mult(Blocks.phaseWall.requirements, 9));
			size = 3;
			health = 5400;
			armor = 3f;
			chanceDeflect = 10f;
			flashHit = true;
			buildType = WallBuild::new;
		}};
		phaseWallGigantic = new Wall("phase-wall-gigantic") {{
			requirements(Category.defense, ItemStack.mult(Blocks.phaseWall.requirements, 16));
			size = 4;
			health = 9600;
			armor = 3f;
			chanceDeflect = 10f;
			flashHit = true;
			buildType = WallBuild::new;
		}};
		surgeWallHuge = new Wall("surge-wall-huge") {{
			requirements(Category.defense, ItemStack.mult(Blocks.surgeWall.requirements, 9));
			size = 3;
			health = 8280;
			armor = 12f;
			lightningChance = 0.1f;
			lightningDamage = 25f;
			buildType = WallBuild::new;
		}};
		surgeWallGigantic = new Wall("surge-wall-gigantic") {{
			requirements(Category.defense, ItemStack.mult(Blocks.surgeWall.requirements, 16));
			size = 4;
			health = 14720;
			armor = 12f;
			lightningChance = 0.1f;
			lightningDamage = 25f;
			buildType = WallBuild::new;
		}};
		uraniumWall = new Wall("uranium-wall") {{
			requirements(Category.defense, ItemStack.with(HItems.uranium, 6));
			size = 1;
			health = 1680;
			armor = 26f;
			absorbLasers = true;
			crushDamageMultiplier = 0.7f;
			buildType = WallBuild::new;
		}};
		uraniumWallLarge = new Wall("uranium-wall-large") {{
			requirements(Category.defense, ItemStack.mult(uraniumWall.requirements, 4));
			size = 2;
			health = 6720;
			armor = 26f;
			absorbLasers = true;
			crushDamageMultiplier = 0.7f;
			buildType = WallBuild::new;
		}};
		chromiumWall = new Wall("chromium-wall") {{
			requirements(Category.defense, ItemStack.with(HItems.chromium, 6));
			size = 1;
			health = 1920;
			armor = 40f;
			absorbLasers = true;
			crushDamageMultiplier = 0.5f;
			buildType = WallBuild::new;
		}};
		chromiumWallLarge = new Wall("chromium-wall-large") {{
			requirements(Category.defense, ItemStack.mult(chromiumWall.requirements, 4));
			size = 2;
			health = 7680;
			armor = 40f;
			absorbLasers = true;
			crushDamageMultiplier = 0.5f;
			buildType = WallBuild::new;
		}};
		chromiumDoor = new AutoDoor("chromium-door") {{
			requirements(Category.defense, ItemStack.with(HItems.chromium, 6, Items.silicon, 4));
			size = 1;
			health = 1920;
			armor = 40f;
			absorbLasers = true;
			crushDamageMultiplier = 0.5f;
			buildType = AutoDoorBuild::new;
		}};
		chromiumDoorLarge = new AutoDoor("chromium-door-large") {{
			requirements(Category.defense, ItemStack.mult(chromiumDoor.requirements, 4));
			size = 2;
			health = 7680;
			armor = 40f;
			absorbLasers = true;
			crushDamageMultiplier = 0.5f;
			buildType = AutoDoorBuild::new;
		}};
		heavyAlloyWall = new Wall("heavy-alloy-wall") {{
			requirements(Category.defense, ItemStack.with(HItems.heavyAlloy, 6, Items.metaglass, 3, Items.plastanium, 4));
			size = 1;
			health = 3220;
			armor = 72f;
			absorbLasers = insulated = true;
			crushDamageMultiplier = 0.25f;
			buildType = WallBuild::new;
			hideDetails = false;
		}};
		heavyAlloyWallLarge = new Wall("heavy-alloy-wall-large") {{
			requirements(Category.defense, ItemStack.mult(heavyAlloyWall.requirements, 4));
			size = 2;
			health = 12880;
			armor = 72f;
			absorbLasers = insulated = true;
			crushDamageMultiplier = 0.25f;
			buildType = WallBuild::new;
			hideDetails = false;
		}};
		chargeWall = new ChargeWall("charge-wall") {{
			requirements(Category.defense, ItemStack.with(HItems.crystallineElectronicUnit, 2, HItems.heavyAlloy, 4, Items.metaglass, 1, Items.plastanium, 4));
			size = 1;
			health = 2960;
			armor = 48f;
			absorbLasers = insulated = true;
			crushDamageMultiplier = 0.25f;
			hideDetails = false;
		}};
		chargeWallLarge = new ChargeWall("charge-wall-large") {{
			requirements(Category.defense, ItemStack.mult(chargeWall.requirements, 4));
			size = 2;
			health = 11840;
			armor = 48f;
			absorbLasers = insulated = true;
			crushDamageMultiplier = 0.25f;
			hideDetails = false;
		}};
		shapedWall = new ShapedWall("shaped-wall") {{
			requirements(Category.defense, ItemStack.with(HItems.heavyAlloy, 8, Items.phaseFabric, 6));
			health = 3080;
			armor = 16f;
			insulated = absorbLasers = true;
			crushDamageMultiplier = 0.5f;
			maxShareStep = 3;
			hideDetails = false;
		}};
		oldTracks = new Wall("old-tracks") {{
			requirements(Category.defense, ItemStack.with(Items.scrap, 12));
			variants = 3;
			health = 450;
			rotate = true;
			buildType = WallBuild::new;
		}};
		//wall-erekir
		berylliumWallHuge = new Wall("beryllium-wall-huge") {{
			requirements(Category.defense, ItemStack.mult(Blocks.berylliumWall.requirements, 9));
			health = 4680;
			armor = 2f;
			buildCostMultiplier = 3f;
			size = 3;
			buildType = WallBuild::new;
			hideDetails = false;
		}};
		berylliumWallGigantic = new Wall("beryllium-wall-gigantic") {{
			requirements(Category.defense, ItemStack.mult(Blocks.berylliumWall.requirements, 16));
			health = 8320;
			armor = 2f;
			buildCostMultiplier = 2f;
			size = 4;
			buildType = WallBuild::new;
			hideDetails = false;
		}};
		tungstenWallHuge = new Wall("tungsten-wall-huge") {{
			requirements(Category.defense, ItemStack.mult(Blocks.tungstenWall.requirements, 9));
			health = 6480;
			armor = 14f;
			buildCostMultiplier = 3f;
			size = 3;
			buildType = WallBuild::new;
			hideDetails = false;
		}};
		tungstenWallGigantic = new Wall("tungsten-wall-gigantic") {{
			requirements(Category.defense, ItemStack.mult(Blocks.tungstenWall.requirements, 16));
			health = 11520;
			armor = 14f;
			buildCostMultiplier = 2f;
			size = 4;
			buildType = WallBuild::new;
			hideDetails = false;
		}};
		blastDoorLarge = new AutoDoor("blast-door-large") {{
			requirements(Category.defense, ItemStack.with(Items.tungsten, 54, Items.silicon, 54));
			health = 6300;
			armor = 14f;
			size = 3;
			buildType = WallBuild::new;
			hideDetails = false;
		}};
		blastDoorHuge = new AutoDoor("blast-door-huge") {{
			requirements(Category.defense, ItemStack.with(Items.tungsten, 96, Items.silicon, 96));
			health = 11200;
			armor = 14f;
			size = 4;
			buildType = WallBuild::new;
			hideDetails = false;
		}};
		reinforcedSurgeWallHuge = new Wall("reinforced-surge-wall-huge") {{
			requirements(Category.defense, ItemStack.mult(Blocks.reinforcedSurgeWall.requirements, 9));
			health = 9000;
			lightningChance = 0.1f;
			lightningDamage = 30f;
			armor = 20f;
			size = 3;
			researchCost = ItemStack.with(Items.surgeAlloy, 120, Items.tungsten, 600);
			buildType = WallBuild::new;
			hideDetails = false;
		}};
		reinforcedSurgeWallGigantic = new Wall("reinforced-surge-wall-gigantic") {{
			requirements(Category.defense, ItemStack.mult(Blocks.reinforcedSurgeWall.requirements, 16));
			health = 16000;
			lightningChance = 0.1f;
			lightningDamage = 30f;
			armor = 20f;
			size = 4;
			researchCost = ItemStack.with(Items.surgeAlloy, 240, Items.tungsten, 1200);
			buildType = WallBuild::new;
			hideDetails = false;
		}};
		carbideWallHuge = new Wall("carbide-wall-huge") {{
			requirements(Category.defense, ItemStack.mult(Blocks.carbideWall.requirements, 9));
			health = 9720;
			armor = 16f;
			size = 3;
			buildType = WallBuild::new;
			hideDetails = false;
		}};
		carbideWallGigantic = new Wall("carbide-wall-gigantic") {{
			requirements(Category.defense, ItemStack.mult(Blocks.carbideWall.requirements, 16));
			health = 17280;
			armor = 16f;
			size = 4;
			buildType = WallBuild::new;
			hideDetails = false;
		}};
		shieldedWallLarge = new ShieldWall("shielded-wall-large") {{
			requirements(Category.defense, ItemStack.with(Items.phaseFabric, 45, Items.surgeAlloy, 27, Items.beryllium, 27));
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
			buildType = ShieldWallBuild::new;
			hideDetails = false;
		}};
		shieldedWallHuge = new ShieldWall("shielded-wall-huge") {{
			requirements(Category.defense, ItemStack.with(Items.phaseFabric, 80, Items.surgeAlloy, 48, Items.beryllium, 48));
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
			buildType = ShieldWallBuild::new;
			hideDetails = false;
		}};
		aparajito = new AparajitoWall("aparajito") {{
			requirements(Category.defense, ItemStack.with(HItems.uranium, 3, HItems.chromium, 4, Items.phaseFabric, 2));
			size = 1;
			health = 2055;
			armor = 44f;
			healColor = HPal.chromiumAmmoBack;
			buildCostMultiplier = 4f;
		}};
		aparajitoLarge = new AparajitoWall("aparajito-large") {{
			requirements(Category.defense, ItemStack.mult(aparajito.requirements, 4));
			size = 2;
			health = 8220;
			armor = 44f;
			healColor = HPal.chromiumAmmoBack;
			buildCostMultiplier = 4f;
		}};
		//drill
		titaniumDrill = new Drill("titanium-drill") {{
			requirements(Category.production, ItemStack.with(Items.copper, 20, Items.graphite, 15, Items.titanium, 10));
			size = 2;
			drillTime = 340f;
			tier = 4;
			consumeLiquid(Liquids.water, 0.06f).optional(true, true);
			buildType = DrillBuild::new;
		}};
		largeWaterExtractor = new SolidPump("large-water-extractor") {{
			requirements(Category.production, ItemStack.with(Items.lead, 60, Items.titanium, 80, Items.thorium, 110, Items.graphite, 80, Items.metaglass, 80));
			result = Liquids.water;
			pumpAmount = 0.66f;
			size = 3;
			liquidCapacity = 120f;
			warmupSpeed = 0.008f;
			rotateSpeed = 1.6f;
			attribute = Attribute.water;
			envRequired |= Env.groundWater;
			consumePower(5.5f);
			buildType = SolidPumpBuild::new;
		}};
		slagExtractor = new LiquidExtractor("slag-extractor") {{
			requirements(Category.production, ItemStack.with(Items.graphite, 60, Items.titanium, 35, Items.metaglass, 80, Items.silicon, 80, Items.thorium, 45));
			size = 3;
			liquidCapacity = 120f;
			result = Liquids.slag;
			attribute = Attribute.heat;
			updateEffect = Fx.redgeneratespark;
			rotateSpeed = 8.6f;
			baseEfficiency = 0;
			pumpAmount = 0.9f;
			envRequired |= Env.none;
			consumePower(3.5f);
		}};
		oilRig = new Fracker("oil-rig") {{
			requirements(Category.production, ItemStack.with(Items.lead, 220, Items.graphite, 200, Items.silicon, 100, Items.thorium, 180, Items.plastanium, 120, Items.surgeAlloy, 60));
			size = 4;
			itemCapacity = 20;
			liquidCapacity *= 40f;
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
			buildType = FrackerBuild::new;
			buildCostMultiplier = 0.8f;
		}};
		blastWell = new BurstDrill("blast-ore-well") {{
			requirements(Category.production, ItemStack.with(Items.lead, 80, Items.graphite, 180, Items.thorium, 110, Items.plastanium, 80, Items.surgeAlloy, 60));
			size = 5;
			itemCapacity = 50;
			liquidCapacity = 30;
			drillTime = 100;
			tier = 10;
			arrows = 12;
			arrowSpacing = 0.5f;
			arrowOffset = 0f;
			arrowColor = new Color(0xfec59e80);
			drillMultipliers.put(Items.sand, 2f);
			drillMultipliers.put(Items.scrap, 2f);
			drillMultipliers.put(HItems.stone, 2f);
			drillMultipliers.put(HItems.rareEarth, 2f);
			drillMultipliers.put(HItems.uranium, 0.5f);
			drillMultipliers.put(HItems.chromium, 0.5f);
			blockedItem = HItems.uranium;
			liquidBoostIntensity = 1;
			drillEffect = new MultiEffect(new WrapEffect() {{
				effect = Fx.dynamicExplosion;
				color = new Color(0xfec59ef1);
				rotation = 1.5f;
			}}, Fx.mineImpactWave.wrap(Pal.blastAmmoBack, 45f));
			consumeLiquid(HLiquids.blastReagent, 0.1f);
			buildType = BurstDrillBuild::new;
			hideDetails = false;
		}};
		ionDrill = new Drill("ion-drill") {{
			requirements(Category.production, ItemStack.with(Items.graphite, 80, Items.silicon, 60, Items.thorium, 50, Items.plastanium, 35, Items.surgeAlloy, 25, Items.phaseFabric, 15));
			size = 3;
			health = 640;
			armor = 3f;
			tier = 8;
			updateEffect = Fx.mineBig;
			updateEffectChance = 0.03f;
			drawRim = true;
			drillTime = 140f;
			drillEffect = Fx.mineBig;
			rotateSpeed = 2f;
			warmupSpeed = 0.06f;
			itemCapacity = 15;
			liquidCapacity = 20f;
			liquidBoostIntensity = 1.6f;
			hardnessDrillMultiplier = 40f;
			consumePower(3f);
			consumeLiquid(Liquids.water, 0.1f).optional(true, true);
			buildType = DrillBuild::new;
			hideDetails = false;
		}};
		cuttingDrill = new Drill("cutting-drill") {{
			requirements(Category.production, ItemStack.with(Items.copper, 320, Items.silicon, 180, Items.thorium, 50, Items.plastanium, 70, Items.surgeAlloy, 80, HItems.uranium, 30));
			size = 4;
			health = 1070;
			armor = 6f;
			tier = 10;
			updateEffectChance = 0.03f;
			drillTime = 360f;
			drillEffect = Fx.mineHuge;
			updateEffect = new ParticleEffect() {{
				particles = 3;
				interp = Interp.fastSlow;
				sizeFrom = 1;
				sizeTo = 9;
				length = 60;
				lifetime = 300;
				colorFrom = HPal.discDark;
				colorTo = HPal.discDark.cpy().a(0f);
				cone = 20;
			}};
			rotateSpeed = 1.5f;
			warmupSpeed = 0.002f;
			itemCapacity = 35;
			liquidCapacity = 30f;
			liquidBoostIntensity = 1.871f;
			hardnessDrillMultiplier = 5f;
			consumePower(11f);
			consumeLiquid(HLiquids.lightOil, 0.15f).optional(true, true);
			buildType = DrillBuild::new;
			hideDetails = false;
		}};
		beamDrill = new LaserBeamDrill("beam-drill") {{
			requirements(Category.production, ItemStack.with(Items.lead, 160, Items.silicon, 120, Items.plastanium, 80, HItems.heavyAlloy, 60, HItems.crystallineCircuit, 35, Items.phaseFabric, 25));
			size = 4;
			health = 1660;
			armor = 12f;
			tier = 14;
			drillTime = 80f;
			liquidBoostIntensity = 1.65f;
			itemCapacity = 50;
			liquidCapacity = 30f;
			hardnessDrillMultiplier = 15f;
			buildCostMultiplier = 0.8f;
			consumePower(6f);
			consumeLiquid(Liquids.water, 0.1f).optional(true, true);
			hideDetails = false;
		}};
		sporeFarm = new SporeFarmBlock("spore-farm") {{
			requirements(Category.production, ItemStack.with(Items.copper, 5, Items.lead, 5));
			health = 50;
			buildCostMultiplier = 0.01f;
			breakSound = Sounds.splash;
			hideDetails = false;
		}};
		//drill-erekir
		heavyPlasmaBore = new BeamDrill("heavy-plasma-bore") {{
			requirements(Category.production, ItemStack.with(Items.silicon, 300, Items.oxide, 150, Items.beryllium, 350, Items.tungsten, 250, Items.carbide, 100));
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
			drillMultipliers.put(Items.pyratite, 1.5f);
			consumePower(390f / 60f);
			consumeLiquid(Liquids.hydrogen, 1.5f / 60f);
			consumeLiquid(Liquids.nitrogen, 7.5f / 60f).optional(true, true);
			buildType = BeamDrillBuild::new;
		}};
		unitMinerPoint = new UnitMinerPoint("unit-miner-point") {{
			requirements(Category.production, BuildVisibility.sandboxOnly, ItemStack.with(Items.beryllium, 220, Items.graphite, 220, Items.silicon, 200, Items.tungsten, 180));
			blockedItems.add(Items.thorium);
			droneConstructTime = 60 * 10f;
			tier = 5;
			itemCapacity = 200;
			consumePower(2f);
			consumeLiquid(Liquids.hydrogen, 6 / 60f);
			squareSprite = false;
		}};
		unitMinerCenter = new UnitMinerPoint("unit-miner-center") {{
			requirements(Category.production, BuildVisibility.sandboxOnly, ItemStack.with(Items.beryllium, 600, Items.tungsten, 500, Items.oxide, 150, Items.carbide, 120, Items.surgeAlloy, 150));
			range = 18;
			dronesCreated = 6;
			droneConstructTime = 60 * 7f;
			tier = 7;
			size = 4;
			itemCapacity = 300;
			minerUnit = HUnitTypes.largeMiner;
			consumePower(3);
			consumeLiquid(Liquids.cyanogen, 9 / 60f);
			buildCostMultiplier = 0.8f;
			squareSprite = false;
		}};
		unitMinerDepot = new UnitMinerDepot("unit-miner-depot") {{
			requirements(Category.production, BuildVisibility.sandboxOnly, ItemStack.with(Items.beryllium, 120, Items.graphite, 120, Items.silicon, 100, Items.tungsten, 80, Items.oxide, 20));
			size = 3;
			buildTime = 60f * 8f;
			consumePower(8f / 60f);
			consumeLiquid(Liquids.nitrogen, 8 / 60f);
			itemCapacity = 100;
			squareSprite = false;
		}};
		//distribution
		invertedJunction = new InvertedJunction("inverted-junction") {{
			requirements(Category.distribution, ItemStack.with(Items.copper, 2));
			placeSprite = "junction";
			sync = true;
			speed = 26f;
			capacity = 6;
			configurable = true;
			buildCostMultiplier = 6f;
		}};
		itemLiquidJunction = new MultiJunction("item-liquid-junction") {{
			requirements(Category.distribution, ItemStack.with(Items.copper, 4, Items.graphite, 6, Items.metaglass, 10));
		}};
		multiSorter = new MultiSorter("multi-sorter") {{
			requirements(Category.distribution, ItemStack.with(Items.lead, 5, Items.copper, 5, Items.silicon, 5));
		}};
		plastaniumRouter = new StackRouter("plastanium-router") {{
			requirements(Category.distribution, ItemStack.with(Items.plastanium, 5, Items.silicon, 5, Items.graphite, 5));
			health = 90;
			speed = 8f;
			buildType = StackRouterBuild::new;
		}};
		plastaniumBridge = new StackBridge("plastanium-bridge") {{
			requirements(Category.distribution, ItemStack.with(Items.lead, 15, Items.silicon, 12, Items.titanium, 15, Items.plastanium, 10));
			health = 90;
			hasPower = false;
			itemCapacity = 10;
			range = 6;
			bridgeWidth = 8f;
			transportTime = 10f;
		}};
		stackHelper = new StackHelper("stack-helper") {{
			requirements(Category.distribution, ItemStack.with(Items.silicon, 20, Items.phaseFabric, 10, Items.plastanium, 20));
			health = 90;
		}};
		chromiumEfficientConveyor = new Conveyor2("chromium-efficient-conveyor") {{
			requirements(Category.distribution, ItemStack.with(Items.lead, 1, HItems.chromium, 1));
			health = 240;
			armor = 3f;
			speed = 0.18f;
			displayedSpeed = 18;
		}};
		chromiumArmorConveyor = new Conveyor2("chromium-armor-conveyor") {{
			requirements(Category.distribution, ItemStack.with(Items.metaglass, 1, Items.thorium, 1, Items.plastanium, 1, HItems.chromium, 1));
			health = 560;
			armor = 5f;
			speed = 0.18f;
			displayedSpeed = 18;
			noSideBlend = true;
		}};
		chromiumTubeConveyor = new TubeConveyor("chromium-tube-conveyor") {{
			requirements(Category.distribution, ItemStack.with(Items.metaglass, 2, Items.thorium, 1, Items.plastanium, 1, HItems.chromium, 2));
			health = 670;
			armor = 5f;
			speed = 0.18f;
			displayedSpeed = 18;
			noSideBlend = true;
			placeableLiquid = true;
			displayFlow = true;
			hideDetails = false;
		}};
		chromiumTubeSorter = new TubeSorter("chromium-tube-sorter") {{
			requirements(Category.distribution, ItemStack.with(Items.copper, 1, Items.metaglass, 1, HItems.chromium, 1));
			health = 450;
			armor = 4f;
		}};
		chromiumStackConveyor = new StackConveyor("chromium-stack-conveyor") {{
			requirements(Category.distribution, ItemStack.with(Items.graphite, 1, Items.silicon, 1, Items.plastanium, 1, HItems.chromium, 1));
			health = 380;
			armor = 4f;
			speed = 0.125f;
			itemCapacity = 20;
			outputRouter = false;
			buildType = StackConveyorBuild::new;
		}};
		chromiumStackRouter = new StackRouter("chromium-stack-router") {{
			requirements(Category.distribution, ItemStack.with(Items.graphite, 4, Items.silicon, 5, Items.plastanium, 3, HItems.chromium, 1));
			health = 380;
			armor = 4f;
			speed = 0.125f;
			itemCapacity = 20;
			buildCostMultiplier = 0.8f;
			buildType = StackRouterBuild::new;
		}};
		chromiumStackBridge = new RailStackBridge("chromium-stack-bridge") {{
			requirements(Category.distribution, ItemStack.with(Items.lead, 15, Items.silicon, 12, Items.plastanium, 10, HItems.chromium, 10));
			health = 420;
			armor = 4f;
			hasPower = false;
			itemCapacity = 20;
			range = 8;
			bridgeWidth = 8f;
			buildCostMultiplier = 0.6f;
			transportTime = 6f;
		}};
		chromiumRouter = new MultiRouter("chromium-router") {{
			requirements(Category.distribution, ItemStack.with(Items.copper, 3, HItems.chromium, 2));
			health = 420;
			armor = 4f;
			speed = 2;
			itemCapacity = 20;
			liquidCapacity = 200f;
			underBullets = true;
			solid = false;
		}};
		chromiumJunction = new MultiJunction("chromium-junction") {{
			requirements(Category.distribution, ItemStack.with(Items.copper, 2, HItems.chromium, 2));
			health = 420;
			armor = 4f;
			speed = 12;
			capacity = itemCapacity = 12;
			chromiumEfficientConveyor.junctionReplacement =  chromiumArmorConveyor.junctionReplacement =  chromiumTubeConveyor.junctionReplacement = this;
		}};
		chromiumItemBridge = new RailItemBridge("chromium-item-bridge") {{
			requirements(Category.distribution, ItemStack.with(Items.graphite, 6, Items.silicon, 8, Items.plastanium, 4, HItems.chromium, 3));
			health = 420;
			armor = 4f;
			hasPower = false;
			transportTime = 3f;
			range = 8;
			arrowSpacing = 6;
			bridgeWidth = 8;
			buildCostMultiplier = 0.8f;
			chromiumEfficientConveyor.bridgeReplacement =  chromiumArmorConveyor.bridgeReplacement =  chromiumTubeConveyor.bridgeReplacement = this;
		}};
		phaseItemNode = new NodeBridge("phase-item-node") {{
			requirements(Category.distribution, ItemStack.with(Items.lead, 30, HItems.chromium, 10, Items.silicon, 15, Items.phaseFabric, 10));
			size = 1;
			health = 320;
			armor = 3f;
			squareSprite = false;
			range = 25;
			envEnabled |= Env.space;
			transportTime = 1f;
			consumePower(0.5f);
		}};
		rapidDirectionalUnloader = new DirectionalUnloader2("rapid-directional-unloader") {{
			requirements(Category.distribution, ItemStack.with(Items.silicon, 40, Items.plastanium, 25, HItems.chromium, 15, Items.phaseFabric, 5));
			speed = 1f;
			squareSprite = false;
			underBullets = true;
			allowCoreUnload = true;
		}};
		//distribution-erekir
		ductJunction = new DuctJunction("duct-junction") {{
			requirements(Category.distribution, ItemStack.with(Items.beryllium, 5));
			health = 90;
			speed = 4f;
			buildType = DuctJunctionBuild::new;
		}};
		ductDistributor = new CoveredRouter("duct-distributor") {{
			requirements(Category.distribution, ItemStack.with(Items.beryllium, 20));
			health = 270;
			size = 2;
			speed = 2f;
			solid = false;
			squareSprite = false;
		}};
		ductMultiSorter = new MultiSorter("duct-multi-sorter") {{
			requirements(Category.distribution, ItemStack.with(Items.beryllium, 5, Items.silicon, 5));
			health = 90;
		}};
		armoredDuctBridge = new DuctBridge("armored-duct-bridge") {{
			requirements(Category.distribution, ItemStack.with(Items.beryllium, 20, Items.tungsten, 10));
			health = 140;
			range = 6;
			speed = 4;
			buildCostMultiplier = 2;
			buildType = DuctBridgeBuild::new;
		}};
		rapidDuctUnloader = new DirectionalUnloader2("rapid-duct-unloader") {{
			requirements(Category.distribution, ItemStack.with(Items.graphite, 25, Items.silicon, 30, Items.tungsten, 20, Items.oxide, 15));
			health = 240;
			speed = 2f;
			solid = false;
			underBullets = true;
			squareSprite = false;
			regionRotated1 = 1;
		}};
		//liquid
		liquidSorter = new SortLiquidRouter("liquid-sorter") {{
			requirements(Category.liquid, ItemStack.with(Items.graphite, 4, Items.metaglass, 2));
			liquidCapacity = 120f;
			liquidPadding = 3f / 4f;
			underBullets = true;
			rotate = false;
			solid = false;
		}};
		liquidValve = new SortLiquidRouter("liquid-valve") {{
			requirements(Category.liquid, ItemStack.with(Items.graphite, 4, Items.metaglass, 2));
			liquidCapacity = 120f;
			liquidPadding = 3f / 4f;
			underBullets = true;
			configurable = false;
			solid = false;
		}};
		liquidOverflowValve = new LiquidOverflowValve("liquid-overflow-valve") {{
			requirements(Category.liquid, ItemStack.with(Items.graphite, 6, Items.metaglass, 10));
			solid = false;
			underBullets = true;
		}};
		liquidUnderflowValve = new LiquidOverflowValve("liquid-underflow-valve") {{
			requirements(Category.liquid, ItemStack.with(Items.graphite, 6, Items.metaglass, 10));
			solid = false;
			underBullets = true;
			invert = true;
		}};
		liquidUnloader = new LiquidUnloader("liquid-unloader") {{
			requirements(Category.liquid, ItemStack.with(Items.titanium, 15, Items.metaglass, 10, Items.silicon, 15));
			health = 70;
			hideDetails = false;
			liquidCapacity = 220f;
		}};
		chromiumArmorConduit = new ArmoredConduit("chromium-armor-conduit") {{
			requirements(Category.liquid, ItemStack.with(Items.metaglass, 2, HItems.chromium, 2));
			health = 420;
			armor = 4f;
			liquidCapacity = 80f;
			liquidPressure = 3.2f;
			noSideBlend = true;
			leaks = false;
			buildType = () -> new ArmoredConduitBuild() {
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
		chromiumLiquidBridge = new RailLiquidBridge("chromium-liquid-bridge") {{
			requirements(Category.liquid, ItemStack.with(Items.metaglass, 10, HItems.chromium, 6));
			health = 480;
			armor = 5f;
			hasPower = false;
			range = 8;
			liquidCapacity = 200f;
			arrowSpacing = 6;
			bridgeWidth = 8f;
			chromiumArmorConduit.bridgeReplacement = this;
		}};
		chromiumArmorLiquidContainer = new LiquidRouter("chromium-armor-liquid-container") {{
			requirements(Category.liquid, ItemStack.with(Items.metaglass, 15, HItems.chromium, 6));
			size = 2;
			health = 950;
			armor = 8f;
			liquidCapacity = 1200f;
			underBullets = true;
			buildType = LiquidRouterBuild::new;
			buildCostMultiplier = 0.8f;
		}};
		chromiumArmorLiquidTank = new LiquidRouter("chromium-armor-liquid-tank") {{
			requirements(Category.liquid, ItemStack.with(Items.metaglass, 40, HItems.chromium, 16));
			size = 3;
			health = 2500;
			armor = 8f;
			liquidCapacity = 3200f;
			underBullets = true;
			buildType = LiquidRouterBuild::new;
			buildCostMultiplier = 0.8f;
		}};
		liquidMassDriver = new LiquidMassDriver("liquid-mass-driver") {{
			requirements(Category.liquid, ItemStack.with(Items.metaglass, 85, Items.silicon, 80, Items.titanium, 80, Items.thorium, 55));
			size = 3;
			liquidCapacity = 700f;
			range = 440f;
			reload = 150f;
			knockback = 3;
			hasPower = true;
			consumePower(1.8f);
		}};
		turboPumpSmall = new ConnectedPump("turbo-pump-small") {{
			requirements(Category.liquid, ItemStack.with(Items.titanium, 12, Items.thorium, 10, Items.metaglass, 20, Items.silicon, 15, HItems.chromium, 10));
			hasPower = true;
			conductivePower = true;
			pumpAmount = 0.8f;
			liquidCapacity = 120f;
			buildCostMultiplier = 0.8f;
			consumePower(0.4f);
		}};
		turboPump = new ConnectedPump("turbo-pump") {{
			requirements(Category.liquid, ItemStack.mult(turboPumpSmall.requirements, 4f));
			size = 2;
			hasPower = true;
			conductivePower = true;
			pumpAmount = 0.8f;
			liquidCapacity = 480f;
			buildCostMultiplier = 0.8f;
			consumePower(1.6f);
		}};
		phaseLiquidNode = new NodeBridge("phase-liquid-node") {{
			requirements(Category.liquid, ItemStack.with(Items.metaglass, 20, HItems.chromium, 10, Items.silicon, 15, Items.phaseFabric, 10));
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
			requirements(Category.liquid, ItemStack.with(Items.graphite, 6, Items.beryllium, 10));
			buildCostMultiplier = 3f;
			health = 250;
			researchCostMultiplier = 1;
			solid = false;
			underBullets = true;
		}};
		reinforcedLiquidUnderflowValve = new LiquidOverflowValve("reinforced-liquid-underflow-valve") {{
			requirements(Category.liquid, ItemStack.with(Items.graphite, 6, Items.beryllium, 10));
			buildCostMultiplier = 3f;
			health = 250;
			researchCostMultiplier = 1;
			invert = true;
			solid = false;
			underBullets = true;
		}};
		reinforcedLiquidUnloader = new LiquidDirectionalUnloader("reinforced-liquid-unloader") {{
			requirements(Category.liquid, ItemStack.with(Items.tungsten, 10, Items.beryllium, 15));
			buildCostMultiplier = 3f;
			health = 550;
			researchCostMultiplier = 1;
			solid = false;
			underBullets = true;
			liquidCapacity = 360f;
		}};
		reinforcedLiquidSorter = new SortLiquidRouter("reinforced-liquid-sorter") {{
			requirements(Category.liquid, ItemStack.with(Items.silicon, 8, Items.beryllium, 4));
			health = 250;
			liquidCapacity = 400f;
			liquidPadding = 3f / 4f;
			researchCostMultiplier = 3;
			underBullets = true;
			rotate = false;
			solid = false;
			squareSprite = false;
		}};
		reinforcedLiquidValve = new SortLiquidRouter("reinforced-liquid-valve") {{
			requirements(Category.liquid, ItemStack.with(Items.graphite, 6, Items.beryllium, 6));
			health = 250;
			liquidCapacity = 150f;
			liquidPadding = 3f / 4f;
			researchCostMultiplier = 3;
			underBullets = true;
			configurable = false;
			solid = false;
			squareSprite = false;
		}};
		smallReinforcedPump = new Pump("small-reinforced-pump") {{
			requirements(Category.liquid, ItemStack.with(Items.graphite, 35, Items.beryllium, 40));
			pumpAmount = 15f / 60f;
			liquidCapacity = 150f;
			size = 1;
			buildType = PumpBuild::new;
			squareSprite = false;
		}};
		largeReinforcedPump = new Pump("large-reinforced-pump") {{
			requirements(Category.liquid, ItemStack.with(Items.beryllium, 105, Items.thorium, 65, Items.silicon, 50, Items.tungsten, 75));
			consumeLiquid(Liquids.hydrogen, 3f / 60f);
			pumpAmount = 240f / 60f / 9f;
			liquidCapacity = 360f;
			size = 3;
			buildType = PumpBuild::new;
			squareSprite = false;
		}};
		//power
		networkPowerNode = new PowerNode("network-power-node") {{
			requirements(Category.power, ItemStack.with(Items.titanium, 15, Items.silicon, 15, Items.surgeAlloy, 10));
			size = 3;
			maxNodes = 25;
			laserRange = 28f;
			buildType = PowerNodeBuild::new;
		}};
		smartPowerNode = new SmartPowerNode("smart-power-node") {{ //Copy stats from normal power node
			requirements(Category.power, ItemStack.with(Items.copper, 2, Items.lead, 5, Items.silicon, 1));
			maxNodes = 10;
			laserRange = 6;
		}};
		heavyArmoredPowerNode = new ArmoredPowerNode("heavy-armored-power-node") {{
			requirements(Category.power, ItemStack.with(Items.plastanium, 30, Items.phaseFabric, 15, HItems.galliumNitride, 10, HItems.heavyAlloy, 25));
			size = 3;
			health = 3350;
			armor = 35f;
			absorbLasers = true;
			maxNodes = 28;
			laserRange = 36f;
			update = true;
			hideDetails = false;
		}};
		microArmoredPowerNode = new ArmoredPowerNode("micro-armored-power-node") {{
			requirements(Category.power, ItemStack.with(Items.plastanium, 5, Items.phaseFabric, 10, HItems.galliumNitride, 5, HItems.heavyAlloy, 10));
			size = 1;
			health = 1150;
			armor = 30f;
			absorbLasers = true;
			maxNodes = 12;
			laserRange = 32f;
			update = true;
			hideDetails = false;
		}};
		powerAnalyzer = new PowerAnalyzer("power-analyzer") {{
			requirements(Category.power, ItemStack.with(Items.lead, 60, Items.silicon, 20, Items.metaglass, 10));
			size = 2;
			displayThickness = 9f / 4f;
			displaySpacing = 18f / 4f;
			displayLength = 24f / 4f;
			hideDetails = false;
		}};
		gasGenerator = new ConsumeGenerator("gas-generator") {{
			size = 1;
			requirements(Category.power, ItemStack.with(Items.metaglass, 30, Items.titanium, 40));
			hasLiquids = true;
			powerProduction = 3.5f;
			consumeLiquid(HLiquids.gas, 0.1f);
			generateEffect = Fx.steam;
			effectChance = 0.01f;
			drawer = new DrawMulti(new DrawDefault(), new DrawWarmupRegion());
			ambientSound = Sounds.steam;
			ambientSoundVolume = 0.02f;
		}};
		coalPyrolyzer = new ConsumeGenerator("coal-pyrolyzer") {{
			size = 3;
			health = 460;
			requirements(Category.power, ItemStack.with(Items.silicon, 70, Items.metaglass, 100, Items.titanium, 80, Items.thorium, 90));
			hasItems = hasLiquids = true;
			liquidCapacity = 60f;
			itemCapacity = 15;
			itemDuration = 60f;
			powerProduction = 17.5f;
			consumeLiquid(Liquids.water, 0.3f);
			consumeItem(Items.coal, 2);
			outputLiquid = new LiquidStack(HLiquids.gas, 0.6f);
			generateEffect = new MultiEffect(Fx.steam, Fx.generatespark);
			effectChance = 0.02f;
			drawer = new DrawMulti(new DrawDefault(), new DrawRegion("-rot1") {{
				rotation = 60f;
				rotateSpeed = 9f;
			}}, new DrawRegion("-rot2") {{
				rotation = 45f;
				rotateSpeed = -1.25f;
				spinSprite = true;
			}}, new DrawRegion("-top"), new DrawLiquidRegion(Liquids.water), new DrawLiquidRegion(HLiquids.gas));
			ambientSound = Sounds.steam;
			ambientSoundVolume = 0.04f;
		}};
		largeThermalGenerator = new ThermalGenerator("large-thermal-generator") {{
			requirements(Category.power, ItemStack.with(Items.graphite, 200, Items.metaglass, 150, Items.silicon, 130, Items.plastanium, 60, Items.surgeAlloy, 20));
			size = 3;
			health = 660;
			buildCostMultiplier = 0.9f;
			attribute = Attribute.heat;
			powerProduction = 2.7f;
			generateEffect = Fx.redgeneratespark;
			effectChance = 0.011f;
			drawer = new DrawMulti(new DrawDefault(), new DrawFade() {{
				scale = 10;
			}});
			floating = true;
			ambientSound = Sounds.hum;
			ambientSoundVolume = 0.06f;
		}};
		liquidConsumeGenerator = new ConsumeGenerator("liquid-generator") {{
			requirements(Category.power, ItemStack.with(Items.graphite, 120, Items.metaglass, 80, Items.silicon, 115));
			size = 3;
			hasLiquids = true;
			powerProduction = 660f / 60f;
			drawer = new DrawMulti(new DrawDefault(), new DrawWarmupRegion() {{
				sinMag = 0;
				sinScl = 1;
			}}, new DrawLiquidRegion());
			Color generateColor = new Color(0x6e685aff);
			generateEffect = new RadialEffect(new Effect(160f, e -> {
				Draw.color(generateColor);
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
			buildType = ConsumeGeneratorBuild::new;
			squareSprite = false;
		}};
		uraniumReactor = new GlowNuclearReactor("uranium-reactor") {{
			requirements(Category.power, ItemStack.with(Items.lead, 400, Items.metaglass, 120, Items.graphite, 350, Items.silicon, 300, HItems.uranium, 100));
			size = 3;
			health = 1450;
			armor = 8f;
			outputsPower = true;
			powerProduction = 58f;
			itemDuration = 360;
			itemCapacity = 30;
			liquidCapacity = 100;
			fuelItem = HItems.uranium;
			coolantPower = 0.45f;
			heating = 0.06f;
			lightColor = Color.white;
			explosionShake = 9;
			explosionShakeDuration = 120;
			explosionRadius = 35;
			explosionDamage = 7200;
			explodeSound = HSounds.dbz1;
			explodeEffect = new Effect(30, 500f, b -> {
				float intensity = 8f;
				float baseLifetime = 25f + intensity * 15f;
				b.lifetime = 50f + intensity * 64f;

				Draw.color(HPal.uraniumAmmoBack);
				Draw.alpha(0.8f);
				for (int i = 0; i < 5; i++) {
					Fx.rand.setSeed(b.id * 2 + i);
					float lenScl = Fx.rand.random(0.25f, 1f);
					int j = i;
					b.scaled(b.lifetime * lenScl, e -> Angles.randLenVectors(e.id + j - 1, e.fin(Interp.pow10Out), (int) (2.8f * intensity), 25f * intensity, (x, y, in, out) -> {
						float fout = e.fout(Interp.pow5Out) * Fx.rand.random(0.5f, 1f);
						float rad = fout * ((2f + intensity) * 2.35f);

						Fill.circle(e.x + x, e.y + y, rad);
						Drawf.light(e.x + x, e.y + y, rad * 2.6f, HPal.uraniumAmmoBack, 0.7f);
					}));
				}

				b.scaled(baseLifetime, e -> {
					Draw.color();
					e.scaled(5 + intensity * 2f, i -> {
						Lines.stroke((3.1f + intensity / 5f) * i.fout());
						Lines.circle(e.x, e.y, (3f + i.fin() * 14f) * intensity);
						Drawf.light(e.x, e.y, i.fin() * 14f * 2f * intensity, Color.white, 0.9f * e.fout());
					});

					Draw.color(Color.white, HPal.uraniumAmmoBack, e.fin());
					Lines.stroke(2f * e.fout());

					Draw.z(Layer.effect + 0.001f);
					Angles.randLenVectors(e.id + 1, e.finpow() + 0.001f, (int) (8 * intensity), 30f * intensity, (x, y, in, out) -> {
						Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + out * 4 * (4f + intensity));
						Drawf.light(e.x + x, e.y + y, (out * 4 * (3f + intensity)) * 3.5f, Draw.getColor(), 0.8f);
					});
				});
			});
			ambientSound = Sounds.hum;
			ambientSoundVolume = 0.24f;
			consumeItem(HItems.uranium);
			consumeLiquid(Liquids.cryofluid, heating / coolantPower).update(false);
		}};
		hyperMagneticReactor = new HyperGenerator("hyper-magnetic-reactor") {{
			requirements(Category.power, ItemStack.with(Items.titanium, 1200, Items.metaglass, 1300, Items.plastanium, 800, Items.silicon, 1600, Items.phaseFabric, 1200, HItems.chromium, 2500, HItems.heavyAlloy, 2200));
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
			consumeLiquid(HLiquids.crystalFluid, 0.25f);
			buildCostMultiplier = 0.6f;
		}};
		hugeBattery = new Battery("huge-battery") {{
			requirements(Category.power, ItemStack.with(HItems.crystal, 110, Items.graphite, 125, Items.phaseFabric, 80, Items.thorium, 110, Items.plastanium, 100));
			size = 5;
			health = 1600;
			consumePowerBuffered(750000f);
			buildType = BatteryBuild::new;
		}};
		armoredCoatedBattery = new Battery("armored-coated-battery") {{
			requirements(Category.power, ItemStack.with(HItems.crystal, 70, Items.silicon, 180, Items.plastanium, 120, HItems.chromium, 100, Items.phaseFabric, 50));
			size = 4;
			health = 8400;
			armor = 28f;
			drawer = new DrawMulti(new DrawDefault(), new DrawPower() {{
				emptyLightColor = new Color(0x18473fff);
				fullLightColor = new Color(0xffd197ff);
			}}, new DrawRegion("-top"));
			consumePowerBuffered(425000f);
			buildType = BatteryBuild::new;
		}};
		//power-erekir
		smartBeamNode = new SmartBeamNode("smart-beam-node") {{ //Copy stats from normal beam node
			requirements(Category.power, ItemStack.with(Items.beryllium, 10, Items.silicon, 2));
			consumesPower = outputsPower = true;
			health = 90;
			range = 10;
			fogRadius = 1;
			consumePowerBuffered(1000f);
		}};
		beamDiode = new BeamDiode("beam-diode") {{
			requirements(Category.power, ItemStack.with(Items.beryllium, 10, Items.silicon, 10, Items.oxide, 5));
			health = 90;
			range = 10;
			fogRadius = 1;
		}};
		beamInsulator = new InsulationWall("beam-insulator") {{
			requirements(Category.power, ItemStack.with(Items.silicon, 10, Items.oxide, 5));
			health = 90;
		}};
		reinforcedPowerAnalyzer = new PowerAnalyzer("reinforced-power-analyzer") {{
			requirements(Category.power, ItemStack.with(Items.beryllium, 25, Items.silicon, 15));
			size = 2;
			displayThickness = 9f / 4f;
			displaySpacing = 18f / 4f;
			displayLength = 24f / 4f;
			horizontal = true;
			squareSprite = false;
		}};
		//production
		largeSiliconSmelter = new GenericCrafter("large-silicon-smelter") {{
			requirements(Category.crafting, ItemStack.with(Items.lead, 50, Items.graphite, 50, Items.titanium, 50, Items.thorium, 25));
			health = 360;
			outputItem = new ItemStack(Items.silicon, 3);
			itemCapacity = 40;
			size = 3;
			hasPower = hasItems = true;
			craftTime = 40f;
			updateEffect = Fx.none;
			craftEffect = Fx.smeltsmoke;
			drawer = new DrawMulti(new DrawDefault(), new DrawFlame() {{
				flameRadius = 0f;
				flameRadiusIn = 0f;
				flameRadiusMag = 0f;
				flameRadiusInMag = 0f;
			}});
			consumePower(2.5f);
			consumeItems(ItemStack.with(Items.sand, 5, Items.coal, 2));
		}};
		largeKiln = new GenericCrafter("large-kiln") {{
			requirements(Category.crafting, ItemStack.with(Items.graphite, 60, Items.silicon, 35, Items.thorium, 40, Items.plastanium, 30));
			craftEffect = Fx.smeltsmoke;
			outputItem = new ItemStack(Items.metaglass, 4);
			craftTime = 40f;
			size = 3;
			hasPower = hasItems = true;
			drawer = new DrawMulti(new DrawDefault(), new DrawFlame(new Color(0xffc099ff)));
			ambientSound = Sounds.smelter;
			ambientSoundVolume = 0.14f;
			consumeItems(ItemStack.with(Items.lead, 3, Items.sand, 3));
			consumePower(1.8f);
			buildType = GenericCrafterBuild::new;
		}};
		largePulverizer = new GenericCrafter("large-pulverizer") {{
			requirements(Category.crafting, ItemStack.with(Items.copper, 25, Items.lead, 25, Items.graphite, 15, Items.thorium, 10));
			size = 2;
			health = 160;
			itemCapacity = 20;
			craftTime = 35f;
			//updateEffect = Fx.pulverizeSmall;
			craftEffect = HFx.hugeSmokeGray;
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
			buildType = GenericCrafterBuild::new;
			hideDetails = false;
		}};
		largeMelter = new GenericCrafter("large-melter") {{
			requirements(Category.crafting, ItemStack.with(Items.lead, 60, Items.graphite, 45, Items.silicon, 30, Items.thorium, 20));
			size = 2;
			hasLiquids = true;
			itemCapacity = 20;
			liquidCapacity = 30;
			craftTime = 12;
			outputLiquid = new LiquidStack(Liquids.slag, 36f / 60f);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.slag), new DrawDefault());
			consumePower(1.5f);
			consumeItem(Items.scrap, 2);
			buildType = GenericCrafterBuild::new;
			hideDetails = false;
		}};
		largeCryofluidMixer = new GenericCrafter("large-cryofluid-mixer") {{
			requirements(Category.crafting, ItemStack.with(Items.lead, 120, Items.silicon, 60, Items.titanium, 150, Items.thorium, 110, Items.plastanium, 20));
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
			buildType = GenericCrafterBuild::new;
			hideDetails = false;
		}};
		largePyratiteMixer = new GenericCrafter("large-pyratite-mixer") {{
			requirements(Category.crafting, ItemStack.with(Items.copper, 100, Items.lead, 50, Items.silicon, 20, Items.thorium, 15));
			outputItem = new ItemStack(Items.pyratite, 3);
			envEnabled |= Env.space;
			size = 3;
			itemCapacity = 20;
			consumePower(0.5f);
			consumeItems(ItemStack.with(Items.coal, 3, Items.lead, 4, Items.sand, 5));
			buildType = GenericCrafterBuild::new;
			hideDetails = false;
		}};
		largeBlastMixer = new GenericCrafter("large-blast-mixer") {{
			requirements(Category.crafting, ItemStack.with(Items.lead, 60, Items.titanium, 40, Items.silicon, 20, Items.thorium, 15));
			outputItem = new ItemStack(Items.blastCompound, 3);
			size = 3;
			itemCapacity = 20;
			envEnabled |= Env.space;
			consumeItems(ItemStack.with(Items.pyratite, 3, Items.sporePod, 3));
			consumePower(1f);
			buildType = GenericCrafterBuild::new;
			hideDetails = false;
		}};
		largeCultivator = new AttributeCrafter("large-cultivator") {{
			requirements(Category.production, ItemStack.with(Items.lead, 40, Items.titanium, 30, Items.plastanium, 20, Items.silicon, 30, Items.metaglass, 60));
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
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.water), new DrawCultivator(), new DrawDefault());
			maxBoost = 3f;
			consumePower(3f);
			consumeLiquid(Liquids.water, 36f / 60f);
			buildType = AttributeCrafterBuild::new;
		}};
		stoneCrusher = new GenericCrafter("stone-crusher") {{
			requirements(Category.crafting, ItemStack.with(Items.copper, 40, Items.graphite, 60, Items.silicon, 25));
			outputItem = new ItemStack(Items.sand, 3);
			craftTime = 60f;
			size = 2;
			hasPower = hasItems = true;
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawAnim(), new DrawDefault(), new DrawRegion("-top"));
			updateEffect = new Effect(20f, e -> {
				Draw.color(Pal.gray, Color.lightGray, e.fin());
				Angles.randLenVectors(e.id, 6, 3f + e.fin() * 6f, (x, y) -> Fill.square(e.x + x, e.y + y, e.fout() * 2f, 45f));
			});
			consumeItem(HItems.stone, 2);
			consumePower(1.2f);
			buildType = GenericCrafterBuild::new;
		}};
		fractionator = new GenericCrafter("fractionator") {{
			requirements(Category.crafting, ItemStack.with(Items.graphite, 250, Items.silicon, 200, Items.thorium, 200, Items.plastanium, 150, Items.surgeAlloy, 180));
			size = 5;
			health = 3200;
			armor = 4f;
			hasPower = true;
			hasItems = true;
			hasLiquids = true;
			rotate = true;
			rotateDraw = false;
			invertFlip = true;
			regionRotated1 = 3;
			liquidOutputDirections = new int[]{1, 3};
			craftTime = 60f;
			itemCapacity = 40;
			liquidCapacity = 300f;
			outputItem = new ItemStack(Items.coal, 3);
			outputLiquids = LiquidStack.with(HLiquids.lightOil, 2.2f, HLiquids.gas, 1.2f);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.oil), new DrawDefault(), new DrawLiquidsOutputs());
			consumeLiquid(Liquids.oil, 2.4f);
			consumePower(15f);
			buildType = GenericCrafterBuild::new;
			hideDetails = false;
		}};
		largePlastaniumCompressor = new GenericCrafter("large-plastanium-compressor") {{
			requirements(Category.crafting, ItemStack.with(Items.silicon, 180, Items.lead, 220, Items.graphite, 120, Items.titanium, 150, Items.thorium, 100));
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
			buildType = GenericCrafterBuild::new;
		}};
		largeSurgeSmelter = new GenericCrafter("large-surge-smelter") {{
			requirements(Category.crafting, ItemStack.with(Items.lead, 180, Items.silicon, 100, Items.metaglass, 60, Items.thorium, 150, Items.surgeAlloy, 30));
			size = 4;
			itemCapacity = 30;
			craftTime = 90f;
			craftEffect = Fx.smeltsmoke;
			outputItem = new ItemStack(Items.surgeAlloy, 3);
			drawer = new DrawMulti(new DrawDefault(), new DrawPowerLight(new Color(0xf3e979ff)), new DrawFlame(new Color(0xffef99ff)));
			consumePower(6);
			consumeItems(ItemStack.with(Items.copper, 5, Items.lead, 6, Items.titanium, 5, Items.silicon, 4));
			buildType = GenericCrafterBuild::new;
		}};
		blastSiliconSmelter = new GenericCrafter("blast-silicon-smelter") {{
			requirements(Category.crafting, ItemStack.with(Items.graphite, 90, Items.thorium, 70, Items.silicon, 80, Items.plastanium, 50, Items.surgeAlloy, 30));
			health = 660;
			size = 4;
			itemCapacity = 50;
			craftTime = 35f;
			outputItem = new ItemStack(Items.silicon, 10);
			craftEffect = new RadialEffect(Fx.surgeCruciSmoke, 9, 45f, 6f);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawDefault(), new DrawGlowRegion() {{
				alpha = 0.9f;
				glowScale = 3.14f;
				color = new Color(0xff0000ff);
			}}, new DrawGlowRegion("-glow1") {{
				alpha = 0.9f;
				glowScale = 3.14f;
				color = new Color(0xeb564bff);
			}});
			ambientSound = Sounds.smelter;
			ambientSoundVolume = 0.21f;
			consumeItems(ItemStack.with(Items.coal, 5, Items.sand, 8, Items.blastCompound, 1));
			consumePower(4f);
			buildType = GenericCrafterBuild::new;
			hideDetails = false;
		}};
		crystallineCircuitConstructor = new GenericCrafter("crystalline-circuit-constructor") {{
			requirements(Category.crafting, ItemStack.with(Items.copper, 120, Items.titanium, 45, Items.silicon, 35, HItems.crystal, 20));
			size = 2;
			itemCapacity = 15;
			craftTime = 100f;
			outputItem = new ItemStack(HItems.crystallineCircuit, 1);
			craftEffect = Fx.none;
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawDefault());
			consumePower(2.5f);
			consumeItems(ItemStack.with(Items.titanium, 2, Items.silicon, 3, HItems.crystal, 1));
			buildType = GenericCrafterBuild::new;
		}};
		crystallineCircuitPrinter = new AdaptiveCrafter("crystalline-circuit-printer") {{
			requirements(Category.crafting, ItemStack.with(Items.titanium, 600, Items.silicon, 400, Items.plastanium, 350, Items.surgeAlloy, 250, HItems.chromium, 200, HItems.crystallineCircuit, 150, HItems.crystal, 150));
			size = 4;
			health = 1500;
			armor = 3f;
			hasItems = true;
			hasLiquids = true;
			itemCapacity = 40;
			liquidCapacity = 60f;
			craftEffect = Fx.none;
			Color senior = Pal.heal.cpy().lerp(Color.green, 0.63f).lerp(Color.white, 0.2f);
			Color junior = Pal.heal;
			updateEffectChance = 0.07f;
			updateEffect = new Effect(30f, e -> {
				Rand rand = Utils.rand(e.id);
				Draw.color(senior, Color.white, e.fout() * 0.66f);
				Draw.alpha(0.55f * e.fout() + 0.5f);
				Angles.randLenVectors(e.id, 2, 4f + e.finpow() * 16f, (x, y) -> {
					Fill.square(e.x + x, e.y + y, e.fout() * rand.random(2, 4));
				});

				Draw.color(junior, Color.white, e.fout() * 0.66f);
				Draw.alpha(0.55f * e.fout() + 0.5f);
				Angles.randLenVectors(e.id + 12, 4, 4f + e.finpow() * 16f, (x, y) -> {
					Fill.square(e.x + x, e.y + y, e.fout() * rand.random(1, 3));
				});
			});
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawScanLine() {{
				colorFrom = junior;
				//lineLength = 73 / 4f;
				scanLength = 73 / 4f;
				scanScl = 15f;
				scanAngle = 90;
				lineStroke -= 0.15f;
				totalProgressMultiplier = 1.25f;
				phaseOffset = Mathf.random() * 5f;
			}}, new DrawScanLine() {{
				colorFrom = junior;
				//lineLength = 73 / 4f;
				scanLength = 73 / 4f;
				scanScl = 15f;
				scanAngle = 0;
				totalProgressMultiplier = 1.55f;
				phaseOffset = Mathf.random() * 5f;
			}}, new DrawScanLine() {{
				colorFrom = senior;
				//lineLength = 73 / 4f;
				scanLength = 73 / 4f;
				scanScl = 15f;
				scanAngle = 90;
				totalProgressMultiplier = 1.35f;
				phaseOffset = Mathf.random() * 5f;
			}}, new DrawScanLine() {{
				colorFrom = senior;
				//lineLength = 73 / 4f;
				scanLength = 73 / 4f;
				scanScl = 8f;
				scanAngle = 0;
				lineStroke -= 0.15f;
				totalProgressMultiplier = 1.65f;
				phaseOffset = Mathf.random() * 5f;
			}}, new DrawRegion("-mid"), new DrawLiquidTile(Liquids.cryofluid, 54 / 4f), new DrawDefault(), new DrawGlowRegion("-glow1") {{
				color = junior;
			}}, new DrawGlowRegion("-glow2") {{
				color = junior;
			}}, new DrawGlowRegion("-glow3") {{
				color = junior;
			}});
			recipes.add(new Recipe() {{
				inputItem = ItemStack.with(Items.titanium, 6, Items.silicon, 9, HItems.crystal, 3);
				inputLiquid = LiquidStack.with(Liquids.cryofluid, 6f / 60f);
				outputItem = ItemStack.with(HItems.crystallineCircuit, 18);
				craftTime = 150f;
			}}, new Recipe() {{
				inputItem = ItemStack.with(HItems.galliumNitride, 8, HItems.crystal, 4, HItems.chromium, 6);
				inputLiquid = LiquidStack.with(Liquids.cryofluid, 18f / 60f);
				outputItem = ItemStack.with(HItems.crystallineElectronicUnit, 9);
				craftTime = 150f;
			}});
			consumePower(25f);
			squareSprite = false;
			hideDetails = false;
		}};
		crystalActivator = new GenericCrafter("crystal-activator") {{
			requirements(Category.crafting, ItemStack.with(Items.titanium, 90, Items.silicon, 80, HItems.crystallineCircuit, 30, Items.plastanium, 60));
			size = 2;
			health = 360;
			hasPower = hasLiquids = outputsLiquid = true;
			rotate = false;
			solid = true;
			itemCapacity = 15;
			liquidCapacity = 24f;
			craftTime = 100f;
			craftEffect = HFx.square(HPal.crystalAmmoBright, 38, 3, 24, 3.2f);
			outputLiquid = new LiquidStack(HLiquids.crystalFluid, 18f / 60f);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.water), new DrawLiquidTile(HLiquids.crystalFluid), new DrawRotator(1.5f), new DrawDefault(), new DrawRegion("-top"));
			lightLiquid = HLiquids.crystalFluid;
			consumePower(3f);
			consumeItem(HItems.crystal, 1);
			consumeLiquid(Liquids.water, 18f / 60f);
			buildType = GenericCrafterBuild::new;
			hideDetails = false;
		}};
		largePhaseWeaver = new GenericCrafter("large-phase-weaver") {{
			requirements(Category.crafting, ItemStack.with(Items.lead, 160, Items.thorium, 100, Items.silicon, 80, Items.plastanium, 50, Items.phaseFabric, 15));
			size = 3;
			itemCapacity = 40;
			craftTime = 60f;
			outputItem = new ItemStack(Items.phaseFabric, 3);
			updateEffect = HFx.squareRand(Pal.accent, 5f, 13f);
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
			clipSize = size * Vars.tilesize * 2f;
			consumePower(7f);
			consumeItems(ItemStack.with(Items.sand, 15, Items.thorium, 5));
			buildType = GenericCrafterBuild::new;
		}};
		phaseFusionInstrument = new GenericCrafter("phase-fusion-instrument") {{
			requirements(Category.crafting, ItemStack.with(Items.silicon, 100, Items.plastanium, 80, Items.phaseFabric, 60, HItems.chromium, 80, HItems.crystallineCircuit, 40));
			size = 3;
			itemCapacity = 30;
			lightRadius /= 2f;
			craftTime = 80;
			craftEffect = HFx.crossBlast(Pal.accent, 45f, 45f);
			craftEffect.lifetime *= 1.5f;
			updateEffect = HFx.squareRand(Pal.accent, 5f, 15f);
			outputItem = new ItemStack(Items.phaseFabric, 5);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawRegion("-bottom-2"), new DrawCrucibleFlame() {{
				flameColor = Pal.accent;
				midColor = new Color(0x2e2f34ff);
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
			consumeItems(ItemStack.with(HItems.uranium, 3, HItems.rareEarth, 8));
			buildType = GenericCrafterBuild::new;
		}};
		clarifier = new GenericCrafter("clarifier") {{
			requirements(Category.crafting, ItemStack.with(Items.copper, 60, Items.graphite, 35, Items.metaglass, 30));
			size = 3;
			hasLiquids = true;
			liquidCapacity = 90;
			outputLiquid = new LiquidStack(Liquids.water, 0.35f);
			outputItem = new ItemStack(HItems.agglomerateSalt, 1);
			Color color1 = new Color(0x85966aff), color2 = new Color(0xf1ffdcff), color3 = new Color(0x728259ff);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(HLiquids.brine, 2), new DrawCultivator() {{
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
			consumeLiquid(HLiquids.brine, 0.4f);
			consumePower(1.5f);
			buildType = GenericCrafterBuild::new;
			squareSprite = false;
		}};
		corkscrewCompressor = new AdaptiveCrafter("corkscrew-compressor") {{
			requirements(Category.crafting, ItemStack.with(Items.titanium, 250, Items.graphite, 210, Items.plastanium, 90, Items.silicon, 80));
			health = 1800;
			size = 4;
			itemCapacity = 60;
			liquidCapacity = 360f;
			hasItems = true;
			hasLiquids = true;
			outputsLiquid = true;
			drawer = new DrawMulti(new DrawDefault(), new DrawRegion("-rotator") {{
				spinSprite = true;
				rotateSpeed = 1.125f;
			}});
			recipes.add(new Recipe() {{
				outputItem = ItemStack.with(Items.graphite, 5);
				inputItem = ItemStack.with(Items.coal, 8);
				inputLiquid = LiquidStack.with(Liquids.water, 0.8f);
				craftTime = 15f;
			}}, new Recipe() {{
				outputLiquid = LiquidStack.with(Liquids.oil, 0.8f);
				inputItem = ItemStack.with(Items.sporePod, 4);
				craftTime = 30f;
			}}, new Recipe() {{
				outputItem = ItemStack.with(Items.plastanium, 1);
				inputItem = ItemStack.with(Items.titanium, 2);
				inputLiquid = LiquidStack.with(Liquids.oil, 1f);
				craftTime = 15f;
			}});
			consumePower(5.5f);
		}};
		atmosphericCollector = new AttributeCrafter("atmospheric-collector") {{
			requirements(Category.crafting, ItemStack.with(Items.copper, 40, Items.metaglass, 20, Items.silicon, 40, Items.titanium, 30));
			size = 2;
			hasPower = hasItems = hasLiquids = true;
			liquidCapacity = 30;
			attribute = Attribute.spores;
			baseEfficiency = 1;
			maxBoost = 1;
			boostScale = 0.25f;
			minEfficiency = 0.5f;
			craftTime = 360f;
			outputItem = new ItemStack(Items.sporePod, 1);
			outputLiquid = new LiquidStack(Liquids.nitrogen, 0.05f);
			consumePower(1.75f);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.nitrogen, 2.1f), new DrawDefault(), new DrawParticles() {{
				particles = 8;
				particleSize = 1.5f;
				particleRad = 12;
				particleLife = 120;
				alpha = 0.4f;
				color = Pal.spore;
			}});
			buildType = AttributeCrafterBuild::new;
		}};
		atmosphericCooler = new GenericCrafter("atmospheric-cooler") {{
			requirements(Category.crafting, ItemStack.with(Items.lead, 120, Items.metaglass, 80, Items.plastanium, 40, Items.surgeAlloy, 30));
			size = 3;
			hasPower = hasLiquids = true;
			liquidCapacity = 120;
			craftTime = 60f;
			outputLiquid = new LiquidStack(Liquids.nitrogen, 0.25f);
			consumePower(1.5f);
			consumeLiquid(Liquids.cryofluid, 0.1f);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.cryofluid) {{
				alpha = 0.9f;
			}}, new DrawLiquidRegion(Liquids.nitrogen), new DrawRegion("-rotator") {{
				rotateSpeed = -1f;
			}}, new DrawDefault());
			buildType = GenericCrafterBuild::new;
		}};
		crystalSynthesizer = new GenericCrafter("crystal-synthesizer") {{
			requirements(Category.crafting, ItemStack.with(Items.graphite, 80, Items.silicon, 100, Items.titanium, 80, Items.thorium, 20));
			size = 3;
			health = 450;
			hasLiquids = true;
			itemCapacity += 15;
			liquidCapacity *= 6f;
			craftTime /= 3f;
			outputItem = new ItemStack(HItems.crystal, 1);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.nitrogen), new DrawDefault());
			consumePower(1.75f);
			consumeItems(ItemStack.with(HItems.stone, 1));
			consumeLiquid(Liquids.nitrogen, 0.15f);
			buildType = GenericCrafterBuild::new;
		}};
		uraniumSynthesizer = new GenericCrafter("uranium-synthesizer") {{
			requirements(Category.crafting, ItemStack.with(Items.graphite, 50, Items.silicon, 40, Items.plastanium, 30, Items.phaseFabric, 15, HItems.crystal, 10));
			size = 2;
			health = 350;
			craftTime = 60f;
			outputItem = new ItemStack(HItems.uranium, 1);
			craftEffect = Fx.smeltsmoke;
			drawer = new DrawMulti(new DrawDefault(), new DrawGlowRegion() {{
				alpha = 1f;
				color = HPal.uraniumAmmoBack.cpy().lerp(Color.white, 0.1f);
			}});
			consumePower(5f);
			consumeItems(ItemStack.with(Items.graphite, 1, Items.thorium, 1));
			buildType = GenericCrafterBuild::new;
		}};
		chromiumSynthesizer = new GenericCrafter("chromium-synthesizer") {{
			requirements(Category.crafting, ItemStack.with(Items.metaglass, 30, Items.silicon, 40, Items.plastanium, 50, Items.phaseFabric, 25, HItems.crystal, 15));
			size = 3;
			health = 650;
			hasLiquids = true;
			itemCapacity = 20;
			liquidCapacity = 30f;
			craftTime = 240f;
			outputItem = new ItemStack(HItems.chromium, 5);
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
			buildType = GenericCrafterBuild::new;
		}};
		heavyAlloySmelter = new GenericCrafter("heavy-alloy-smelter") {{
			requirements(Category.crafting, ItemStack.with(Items.lead, 150, Items.silicon, 80, HItems.crystallineCircuit, 12, Items.thorium, 120, HItems.chromium, 30, Items.phaseFabric, 20));
			size = 3;
			health = 850;
			craftTime = 80f;
			outputItem = new ItemStack(HItems.heavyAlloy, 1);
			craftEffect = Fx.smeltsmoke;
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawDefault(), new DrawFlame());
			consumePower(11f);
			consumeItems(ItemStack.with(HItems.uranium, 1, HItems.chromium, 1));
			buildType = GenericCrafterBuild::new;
			hideDetails = false;
		}};
		metalAnalyzer = new Separator("metal-analyzer") {{
			requirements(Category.crafting, ItemStack.with(Items.titanium, 120, Items.silicon, 180, Items.plastanium, 80, HItems.crystallineCircuit, 30));
			size = 3;
			itemCapacity = 20;
			liquidCapacity = 30f;
			craftTime = 20f;
			results = ItemStack.with(Items.titanium, 2, Items.thorium, 2, HItems.uranium, 1, HItems.chromium, 1);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawPistons() {{
				sides = 4;
				sinMag = 3.9f;
				lenOffset = -1.785f;
				angleOffset = 45f;
			}}, new DrawRegion("-shade"), new DrawDefault(), new DrawLiquidTile(Liquids.water, 39f / 4f), new DrawRegion("-top"));
			consumePower(3.5f);
			consumeItem(HItems.rareEarth, 1);
			consumeLiquid(Liquids.water, 6f / 60f);
			buildType = SeparatorBuild::new;
		}};
		nitrificationReactor = new GenericCrafter("nitrification-reactor") {{
			requirements(Category.crafting, ItemStack.with(Items.lead, 80, Items.metaglass, 50, Items.silicon, 20, Items.plastanium, 30, Items.thorium, 25));
			size = 2;
			health = 380;
			armor = 5f;
			hasLiquids = true;
			itemCapacity = 10;
			liquidCapacity = 24;
			craftTime = 60;
			outputLiquid = new LiquidStack(HLiquids.nitratedOil, 12f / 60f);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.oil), new DrawLiquidTile(HLiquids.nitratedOil), new DrawDefault());
			consumePower(5f);
			consumeLiquid(HLiquids.lightOil, 12f / 60f);
			consumeItem(Items.sporePod, 1);
			buildType = GenericCrafterBuild::new;
		}};
		nitratedOilPrecipitator = new Separator("nitrated-oil-precipitator") {{
			requirements(Category.crafting, ItemStack.with(Items.copper, 120, Items.graphite, 100, Items.plastanium, 40, Items.surgeAlloy, 60));
			size = 3;
			health = 680;
			armor = 8f;
			itemCapacity = 15;
			liquidCapacity = 54;
			results = ItemStack.with(Items.pyratite, 1, Items.blastCompound, 4);
			craftTime = 12f;
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(HLiquids.nitratedOil), new DrawRotator(), new DrawRegion("-middle"), new DrawDefault());
			ambientSound = HSounds.largeBeam;
			ambientSoundVolume = 0.24f;
			consumePower(4f);
			consumeLiquid(HLiquids.nitratedOil, 36f / 60f);
			buildType = SeparatorBuild::new;
		}};
		blastReagentMixer = new GenericCrafter("blast-reagent-mixer") {{
			size = 4;
			armor = 5;
			health = 660;
			requirements(Category.crafting, ItemStack.with(Items.lead, 100, Items.metaglass, 160, Items.titanium, 80, Items.thorium, 60, HItems.chromium, 30));
			hasPower = hasItems = hasLiquids = true;
			liquidCapacity = 180;
			craftTime = 60;
			outputLiquid = new LiquidStack(HLiquids.blastReagent, 1.25f * 2);
			consumePower(2.25f);
			consumeItem(HItems.rareEarth, 3);
			consumeLiquid(HLiquids.nitratedOil, 2f);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(HLiquids.nitratedOil), new DrawLiquidTile(HLiquids.blastReagent), new DrawRegion("-rotator") {{
				rotateSpeed = -0.8f;
				rotation = 45;
				spinSprite = true;
			}}, new DrawDefault());
			buildType = GenericCrafterBuild::new;
		}};
		centrifuge = new Centrifuge("slag-centrifuge") {{
			requirements(Category.crafting, ItemStack.with(Items.lead, 80, Items.silicon, 120, Items.metaglass, 80, Items.plastanium, 40, Items.surgeAlloy, 30));
			health = 600;
			size = 3;
			itemCapacity = 50;
			liquidCapacity = 240f;
			hasPower = consumesPower = hasLiquids = true;
			results = ItemStack.with(Items.copper, 1, Items.lead, 1, Items.graphite, 1, Items.titanium, 1, Items.thorium, 1);
			craftTime = 3.5f;
			outputLiquids = LiquidStack.with(Liquids.gallium, 0.12f);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidRegion(Liquids.gallium), new DrawDefault());
			consumeLiquid(Liquids.slag, 0.48f);
			consumePower(1.2f);
		}};
		galliumNitrideSmelter = new GenericCrafter("gallium-nitride-smelter") {{
			requirements(Category.crafting, ItemStack.with(Items.graphite, 100, Items.silicon, 80, Items.metaglass, 60, Items.plastanium, 30, Items.surgeAlloy, 20));
			health = 300;
			size = 2;
			hasPower = consumesPower = hasLiquids = true;
			itemCapacity = 20;
			liquidCapacity = 60f;
			craftTime = 40f;
			outputItem = new ItemStack(HItems.galliumNitride, 1);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.gallium), new DrawLiquidTile(Liquids.nitrogen), new DrawHeat(), new DrawDefault());
			consumeLiquids(LiquidStack.with(Liquids.nitrogen, 0.15f, Liquids.gallium, 0.05f));
			buildType = GenericCrafterBuild::new;
		}};
		//production-erekir
		ventHeater = new ThermalHeater("vent-heater") {{
			requirements(Category.crafting, ItemStack.with(Items.beryllium, 60, Items.graphite, 70, Items.tungsten, 80, Items.oxide, 50));
			size = 3;
			hasLiquids = true;
			hasPower = false;
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
			requirements(Category.crafting, ItemStack.with(Items.graphite, 140, Items.silicon, 50, Items.tungsten, 120, Items.oxide, 100));
			size = 4;
			hasLiquids = true;
			itemCapacity = 30;
			liquidCapacity = 20f;
			craftTime = 50;
			outputItem = new ItemStack(Items.silicon, 8);
			consumeItems(ItemStack.with(Items.sand, 8));
			consumeLiquids(LiquidStack.with(Liquids.hydrogen, 3f / 60f));
			consumePower(3f);
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidRegion(Liquids.hydrogen), new DrawCrucibleFlame() {{
				midColor = new Color(0x97a5f7ff);
				flameColor = new Color(0xd1e4ffff);
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
			buildType = GenericCrafterBuild::new;
			squareSprite = false;
		}};
		largeElectricHeater = new HeatProducer("large-electric-heater") {{
			requirements(Category.crafting, ItemStack.with(Items.tungsten, 150, Items.oxide, 120, Items.carbide, 50));
			size = 4;
			health = 3450;
			armor = 13f;
			drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput());
			squareSprite = false;
			rotateDraw = false;
			heatOutput = 25f;
			regionRotated1 = 1;
			ambientSound = Sounds.hum;
			consumePower(550f / 60f);
			buildType = HeatProducerBuild::new;
		}};
		largeOxidationChamber = new HeatProducer("large-oxidation-chamber") {{
			requirements(Category.crafting, ItemStack.with(Items.tungsten, 180, Items.graphite, 220, Items.silicon, 220, Items.beryllium, 320, Items.oxide, 120, Items.carbide, 70));
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
			buildType = HeatProducerBuild::new;
		}};
		largeSurgeCrucible = new HeatCrafter("large-surge-crucible") {{
			requirements(Category.crafting, ItemStack.with(Items.graphite, 220, Items.silicon, 200, Items.tungsten, 240, Items.oxide, 120, Items.surgeAlloy, 80));
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
				color = new Color(0xffc073ff);
				strokeMax = 2.5f;
				radius = 10;
				amount = 3;
			}}, new DrawLiquidRegion(Liquids.slag), new DrawDefault(), new DrawHeatInput(), new DrawHeatRegion() {{
				color = new Color(0xff6060ff);
			}}, new DrawHeatRegion("-vents"));
			consumeItem(Items.silicon, 8);
			consumeLiquid(Liquids.slag, 80f / 60f);
			consumePower(1f);
			buildType = HeatCrafterBuild::new;
		}};
		largeCarbideCrucible = new HeatCrafter("large-carbide-crucible") {{
			requirements(Category.crafting, ItemStack.with(Items.thorium, 300, Items.tungsten, 400, Items.oxide, 100, Items.carbide, 60));
			size = 5;
			health = 2950;
			armor = 10f;
			itemCapacity = 50;
			heatRequirement = 20;
			craftTime = 135;
			outputItem = new ItemStack(Items.carbide, 3);
			ambientSound = Sounds.smelter;
			ambientSoundVolume = 1.8f;
			drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawCrucibleFlame(), new DrawDefault(), new DrawHeatInput());
			consumeItems(ItemStack.with(Items.graphite, 8, Items.tungsten, 5));
			consumePower(1f);
			buildType = HeatCrafterBuild::new;
		}};
		//defense
		lighthouse = new LightBlock("lighthouse") {{
			requirements(Category.effect, BuildVisibility.lightingOnly, ItemStack.with(Items.graphite, 20, Items.silicon, 10, Items.lead, 30, Items.titanium, 15));
			size = 2;
			brightness = 1f;
			radius = 220f;
			consumePower(0.15f);
			buildType = LightBuild::new;
			buildCostMultiplier = 0.8f;
		}};
		mendDome = new MendProjector("mend-dome") {{
			requirements(Category.effect, ItemStack.with(Items.lead, 200, Items.titanium, 130, Items.silicon, 120, Items.plastanium, 60, Items.surgeAlloy, 40));
			size = 3;
			reload = 200f;
			range = 150f;
			healPercent = 15f;
			phaseBoost = 25f;
			phaseRangeBoost = 75;
			scaledHealth = 240;
			consumePower(2.5f);
			consumeItem(Items.phaseFabric, 1).optional(true, true);
			buildType = MendBuild::new;
		}};
		sectorStructureMender = new RegenProjector("sector-structure-mender") {{
			requirements(Category.effect, ItemStack.with(Items.lead, 600, Items.titanium, 400, Items.silicon, 350, Items.plastanium, 250, Items.surgeAlloy, 220, HItems.crystallineCircuit, 150));
			size = 4;
			health = 1800;
			range = 1500;
			healPercent = 0.025f;
			canOverdrive = false;
			optionalUseTime = 3600;
			optionalMultiplier = 6;
			effectChance = 0.5f;
			effect = WrapperEffect.wrap(HFx.polyParticle, HPal.crystalAmmoBack);
			drawer = new DrawMulti(new DrawDefault(), new DrawPulseShape() {{
				layer = 110;
				stroke = 3f;
				timeScl = 120f;
				color = HPal.regenerating;
			}}, new DrawShape() {{
				layer = 110;
				radius = 5f;
				useWarmupRadius = true;
				timeScl = 1.22f;
				color = HPal.regenerating;
			}});
			consumePower(20f);
			consumeItem(HItems.crystallineCircuit, 10).optional(true, true);
			buildType = RegenProjectorBuild::new;
		}};
		largeShieldGenerator = new ForceProjector("large-shield-generator") {{
			requirements(Category.effect, ItemStack.with(Items.silicon, 120, Items.lead, 250, Items.graphite, 180, Items.plastanium, 150, Items.phaseFabric, 40, HItems.chromium, 60));
			size = 4;
			radius = 220f;
			shieldHealth = 20000f;
			cooldownNormal = 12f;
			cooldownLiquid = 6f;
			cooldownBrokenBase = 9f;
			itemConsumer = consumeItem(Items.phaseFabric).optional(true, true);
			phaseUseTime = 180f;
			phaseRadiusBoost = 100f;
			phaseShieldBoost = 15000f;
			consumePower(15f);
			buildType = ForceBuild::new;
			hideDetails = false;
		}
			@Override
			protected TextureRegion[] icons() {
				return teamRegion.found() ? new TextureRegion[]{region, teamRegions[Team.sharded.id]} : new TextureRegion[]{region};
			}
		};
		paralysisMine = new ShockMine("paralysis-mine") {{
			requirements(Category.effect, ItemStack.with(Items.titanium, 15, Items.silicon, 10, Items.surgeAlloy, 5));
			size = 1;
			hasShadow = false;
			underBullets = true;
			health = 120;
			damage = 36f;
			shots = 8;
			length = 12;
			tendrils = 6;
			lightningColor = Pal.surge;
			buildType = ShockMineBuild::new;
		}};
		detonator = new Explosive("detonator") {{
			requirements(Category.effect, HBuildVisibility.singlePlayer, ItemStack.with(Items.lead, 30, Items.graphite, 20, Items.thorium, 10, Items.blastCompound, 10));
			health = 160;
			size = 3;
			squareSprite = false;
		}};
		bombLauncher = new BombLauncher("bomb-launcher") {{
			requirements(Category.effect, ItemStack.with(Items.plastanium, 200, Items.graphite, 250, Items.silicon, 350, Items.thorium, 400, Items.surgeAlloy, 75));
			health = 1200;
			size = 3;
			itemCapacity = 30;
			storage = 1;
			bullet = new EffectBulletType(15f, 100f, 1800f) {{
				trailChance = 0.25f;
				trailEffect = HFx.trailToGray;
				trailParam = 1.5f;
				smokeEffect = HFx.hugeSmoke;
				shootEffect = HFx.boolSelector;
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
				hitEffect = despawnEffect = new MultiEffect(HFx.crossBlast(hitColor, splashDamageRadius * 1.25f), HFx.blast(hitColor, splashDamageRadius * 1.5f));
			}};
			reloadTime = 300f;
			consumePowerCond(6f, BombLauncherBuild::isCharging);
			consumeItem(Items.blastCompound, 10);
		}};
		//defense-erekir
		largeRadar = new Radar("large-radar") {{
			requirements(Category.effect, BuildVisibility.fogOnly, ItemStack.with(Items.graphite, 180, Items.silicon, 160, Items.beryllium, 30, Items.tungsten, 10, Items.oxide, 20));
			health = 180;
			size = 3;
			outlineColor = new Color(0x4a4b53ff);
			fogRadius = 86;
			consumePower(1.2f);
			buildType = RadarBuild::new;
		}};
		reinforcedOverdriveProjector = new RectOverdriveProjector("reinforced-overdrive-projector") {{
			requirements(Category.effect, ItemStack.with(Items.silicon, 100, Items.oxide, 80, Items.phaseFabric, 20, Items.surgeAlloy, 60, Items.tungsten, 90, Items.carbide, 25));
			health = 1040;
			size = 3;
			range = 150f;
			reload = 90f;
			phaseRangeBoost = 0f;
			speedBoostPhase = 0.3f;
			speedBoost = 1.5f;
			hasBoost = true;
			consumePower(6f);
			consumeLiquid(Liquids.hydrogen, 0.05f);
			consumeItem(Items.phaseFabric, 1).optional(true, true);
			squareSprite = false;
		}};
		//storage
		bin = new StorageBlock("bin") {{
			requirements(Category.effect, ItemStack.with(Items.copper, 55, Items.lead, 35));
			size = 1;
			itemCapacity = 60;
			scaledHealth = 55;
			buildType = StorageBuild::new;
		}
			@Override
			protected TextureRegion[] icons() {
				return teamRegion.found() ? new TextureRegion[]{region, teamRegions[Team.sharded.id]} : new TextureRegion[]{region};
			}
		};
		cargo = new StorageBlock("cargo") {{
			requirements(Category.effect, ItemStack.with(Items.titanium, 350, Items.thorium, 250, Items.plastanium, 125));
			size = 4;
			itemCapacity = 3000;
			scaledHealth = 55;
			buildType = StorageBuild::new;
		}
			@Override
			protected TextureRegion[] icons() {
				return teamRegion.found() ? new TextureRegion[]{region, teamRegions[Team.sharded.id]} : new TextureRegion[]{region};
			}
		};
		machineryUnloader = new Unloader("machinery-unloader") {{
			requirements(Category.effect, ItemStack.with(Items.copper, 15, Items.lead, 10));
			health = 40;
			speed = 60f / 4.2f;
			group = BlockGroup.transportation;
			buildType = UnloaderBuild::new;
		}};
		rapidUnloader = new Unloader2("rapid-unloader") {{
			requirements(Category.effect, ItemStack.with(Items.silicon, 35, Items.plastanium, 15, HItems.crystallineCircuit, 10, HItems.chromium, 15));
			speed = 1f;
			group = BlockGroup.transportation;
		}};
		coreStorage = new CoreStorageBlock("core-storage") {{
			requirements(Category.effect, ItemStack.with(Items.lead, 600, Items.titanium, 400, Items.silicon, 800, Items.thorium, 400, Items.plastanium, 300));
			size = 3;
			range = 40;
			hideDetails = false;
		}};
		coreShatter = new CrashCore("core-cripple") {{
			requirements(Category.effect, BuildVisibility.shown, ItemStack.with(Items.copper, 400, Items.lead, 150));
			unitType = UnitTypes.alpha;
			health = 500;
			itemCapacity = 1500;
			size = 2;
			explosionSoundVolume = 4f;
			thrusterLength = 2f;
			unitCapModifier = 3;
			alwaysUnlocked = true;
		}};
		//storage-erekir
		reinforcedCoreStorage = new CoreStorageBlock("reinforced-core-storage") {{
			requirements(Category.effect, ItemStack.with(Items.beryllium, 600, Items.tungsten, 400, Items.thorium, 350, Items.silicon, 800, Items.oxide, 200, Items.carbide, 150));
			size = 3;
			range = 40;
			squareSprite = false;
		}};
		//payload
		payloadJunction = new PayloadJunction("payload-junction") {{
			requirements(Category.units, ItemStack.with(Items.graphite, 15, Items.copper, 20));
			health = 350;
		}};
		payloadRail = new PayloadRail("payload-rail") {{
			requirements(Category.units, ItemStack.with(Items.graphite, 45, Items.titanium, 35, Items.silicon, 20));
			health = 350;
		}};
		//payload-erekir
		reinforcedPayloadJunction = new PayloadJunction("reinforced-payload-junction") {{
			requirements(Category.units, ItemStack.with(Items.tungsten, 15, Items.beryllium, 10));
			moveTime = 35f;
			health = 800;
			researchCostMultiplier = 4f;
			underBullets = true;
		}};
		reinforcedPayloadRail = new PayloadRail("reinforced-payload-rail") {{
			requirements(Category.units, ItemStack.with(Items.tungsten, 55, Items.silicon, 25, Items.oxide, 10));
			health = 800;
		}};
		//unit
		unitMaintenanceDepot = new RepairTower("unit-maintenance-depot") {{
			requirements(Category.units, ItemStack.with(Items.thorium, 360, Items.plastanium, 220, Items.surgeAlloy, 160, Items.phaseFabric, 100, HItems.crystallineCircuit, 120));
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
			consumeLiquid(HLiquids.crystalFluid, 0.6f);
			buildType = RepairTowerBuild::new;
		}};
		titanReconstructor = new Reconstructor("titan-reconstructor") {{
			requirements(Category.units, ItemStack.with(Items.lead, 4000, Items.silicon, 3000, Items.plastanium, 1500, Items.surgeAlloy, 1200, Items.phaseFabric, 300, HItems.uranium, 600, HItems.chromium, 800));
			size = 11;
			liquidCapacity = 360f;
			scaledHealth = 100f;
			constructTime = 60f * 60f * 5f;
			upgrades.addAll(
					new UnitType[]{UnitTypes.eclipse, HUnitTypes.aphelion},
					new UnitType[]{UnitTypes.toxopid, HUnitTypes.cancer},
					new UnitType[]{UnitTypes.reign, HUnitTypes.empire},
					new UnitType[]{UnitTypes.oct, HUnitTypes.windstorm},
					new UnitType[]{UnitTypes.corvus, HUnitTypes.supernova},
					new UnitType[]{UnitTypes.omura, HUnitTypes.poseidon},
					new UnitType[]{UnitTypes.navanax, HUnitTypes.leviathan},
					new UnitType[]{HUnitTypes.destruction, HUnitTypes.purgatory},
					new UnitType[]{HUnitTypes.lepidoptera, HUnitTypes.mantodea}
			);
			consumePower(35f);
			consumeLiquid(Liquids.cryofluid, 4f);
			consumeItems(ItemStack.with(Items.silicon, 1500, HItems.crystallineCircuit, 300, HItems.uranium, 400, HItems.chromium, 500));
			buildType = ReconstructorBuild::new;
		}};
		//unit-erekir
		largeUnitRepairTower = new RepairTower("large-unit-repair-tower") {{
			requirements(Category.units, ItemStack.with(Items.graphite, 120, Items.silicon, 150, Items.tungsten, 180, Items.oxide, 60, Items.carbide, 30));
			size = 4;
			health = 650;
			squareSprite = false;
			liquidCapacity = 30;
			range = 220;
			healAmount = 10;
			consumePower(3);
			consumeLiquid(Liquids.ozone, 8f / 60f);
			buildType = RepairTowerBuild::new;
		}};
		seniorAssemblerModule = new UnitAssemblerModule("senior-assembler-module") {{
			requirements(Category.units, ItemStack.with(Items.thorium, 800, Items.phaseFabric, 600, Items.surgeAlloy, 400, Items.oxide, 300, Items.carbide, 400));
			size = 5;
			tier = 2;
			regionSuffix = "-dark";
			researchCostMultiplier = 0.75f;
			consumePower(5.5f);
			buildType = UnitAssemblerModuleBuild::new;
		}};
		//logic
		matrixProcessor = new LogicBlock("matrix-processor") {{
			requirements(Category.logic, ItemStack.with(Items.lead, 500, Items.silicon, 350, Items.surgeAlloy, 125, HItems.crystallineCircuit, 50, HItems.chromium, 75));
			consumeLiquid(Liquids.cryofluid, 0.12f);
			hasLiquids = true;
			instructionsPerTick = 100;
			range = 8 * 62;
			size = 4;
			squareSprite = false;
			buildType = LogicBuild::new;
		}};
		hugeLogicDisplay = new LogicDisplay("huge-logic-display") {{
			requirements(Category.logic, ItemStack.with(Items.lead, 300, Items.silicon, 250, Items.metaglass, 200, Items.phaseFabric, 150));
			displaySize = 272;
			size = 9;
		}};
		buffrerdMemoryCell = new CopyMemoryBlock("buffrerd-memory-cell") {{
			requirements(Category.logic, ItemStack.with(Items.titanium, 40, Items.graphite, 40, Items.silicon, 40));
			size = 1;
			memoryCapacity = 64;
		}};
		buffrerdMemoryBank = new CopyMemoryBlock("buffrerd-memory-bank") {{
			requirements(Category.logic, ItemStack.with(Items.titanium, 40, Items.graphite, 90, Items.silicon, 90, Items.phaseFabric, 30));
			size = 2;
			memoryCapacity = 512;
		}};
		heatSink = new ProcessorCooler("heat-sink") {{
			requirements(Category.logic, ItemStack.with(Items.titanium, 70, Items.silicon, 25, Items.plastanium, 65));
			size = 2;
		}};
		heatFan = new ProcessorFan("cooler-fan") {{
			requirements(Category.logic, ItemStack.with(Items.titanium, 90, Items.silicon, 50, Items.plastanium, 50, Items.phaseFabric, 25));
			size = 3;
			boost = 3;
			maxProcessors = 5;
			consumePower(4f);
		}};
		heatSinkLarge = new ProcessorCooler("water-block") {{
			requirements(Category.logic, ItemStack.with(Items.titanium, 110, Items.silicon, 50, Items.metaglass, 40, Items.plastanium, 30, Items.surgeAlloy, 15));
			size = 3;
			boost = 2;
			maxProcessors = 6;
			liquidCapacity = 60;
			acceptCoolant = true;
		}};
		laserRuler = new LaserRuler("laser-ruler") {{
			requirements(Category.logic, ItemStack.with(Items.lead, 15, Items.silicon, 25, Items.metaglass, 5));
			size = 1;
		}};
		iconDisplay = new IconDisplay("icon-display") {{
			requirements(Category.logic, ItemStack.with(Items.graphite, 3, Items.silicon, 4, Items.metaglass, 2));
		}};
		iconDisplayLarge = new IconDisplay("icon-display-large") {{
			requirements(Category.logic, ItemStack.mult(iconDisplay.requirements, 4));
			size = 2;
		}};
		characterDisplay = new CharacterDisplay("character-display") {{
			requirements(Category.logic, ItemStack.with(Items.graphite, 3, Items.silicon, 4, Items.metaglass, 2));
		}};
		characterDisplayLarge = new CharacterDisplay("character-display-large") {{
			requirements(Category.logic, ItemStack.mult(characterDisplay.requirements, 4));
			size = 2;
		}};
		labelMessage = new LabelMessageBlock("label-message");
		//logic-erekir
		reinforcedIconDisplay = new IconDisplay("reinforced-icon-display") {{
			requirements(Category.logic, ItemStack.with(Items.beryllium, 2, Items.graphite, 3, Items.silicon, 4));
		}};
		reinforcedIconDisplayLarge = new IconDisplay("reinforced-icon-display-large") {{
			requirements(Category.logic, ItemStack.mult(reinforcedIconDisplay.requirements, 4));
			size = 2;
		}};
		reinforcedCharacterDisplay = new CharacterDisplay("reinforced-character-display") {{
			requirements(Category.logic, ItemStack.with(Items.beryllium, 2, Items.graphite, 3, Items.silicon, 4));
		}};
		reinforcedCharacterDisplayLarge = new CharacterDisplay("reinforced-character-display-large") {{
			requirements(Category.logic, ItemStack.mult(reinforcedCharacterDisplay.requirements, 4));
			size = 2;
		}};
		//turret
		dissipation = new PointDefenseTurret("dissipation") {{
			requirements(Category.turret, ItemStack.with(Items.silicon, 220, HItems.chromium, 80, Items.phaseFabric, 40, Items.plastanium, 60));
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
			buildType = PointDefenseBuild::new;
		}};
		cobweb = new Cobweb("cobweb") {{
			requirements(Category.turret, ItemStack.with(Items.silicon, 200, Items.graphite, 200, Items.surgeAlloy, 50, Items.phaseFabric, 50));
			hasPower = true;
			size = 3;
			force = 30f;
			scaledForce = 12f;
			range = 320f;
			damage = 1.5f;
			scaledHealth = 200;
			rotateSpeed = 10;
			laserWidth = 1;
			shootLength = 10;
			coolant = consumeCoolant(0.3f);
			coolantMulti = 4;
			consumePower(6f);
		}};
		coilBlaster = new TeslaTurret("coil-blaster") {{
			requirements(Category.turret, ItemStack.with(Items.copper, 120, Items.lead, 80, Items.graphite, 30));
			rings.add(new TeslaRing(2f), new TeslaRing(6f));
			size = 2;
			scaledHealth = 200;
			reload = 20f;
			range = 130f;
			maxTargets = 5;
			damage = 23f;
			status = StatusEffects.shocked;
			consumePower(4.8f);
			coolant = consumeCoolant(0.2f);
		}};
		electromagneticStorm = new TeslaTurret("electromagnetic-storm") {{
			requirements(Category.turret, ItemStack.with(Items.copper, 120, Items.lead, 150, Items.graphite, 55, Items.titanium, 90, Items.thorium, 50, Items.surgeAlloy, 40));
			size = 3;
			float spinSpeed = 12f;
			//Center
			rings.add(new TeslaRing(1f), new TeslaRing(3.25f), new TeslaRing(6.5f));
			//Spinner 1
			rings.add(new TeslaRing(4.25f) {{ //TL
				hasSprite = true;
				drawUnder = true;
				xOffset = -8.625f;
				yOffset = 8.625f;
				rotationMul = spinSpeed;
			}}, new TeslaRing(4.25f) {{ //TR
				drawUnder = true;
				xOffset = yOffset = 8.625f;
				rotationMul = spinSpeed;
			}}, new TeslaRing(4.25f) {{ //BL
				drawUnder = true;
				xOffset = yOffset = -8.625f;
				rotationMul = spinSpeed;
			}}, new TeslaRing(4.25f) {{ //BR
				drawUnder = true;
				xOffset = 8.625f;
				yOffset = -8.625f;
				rotationMul = spinSpeed;
			}});
			//Spinner 2
			rings.add(new TeslaRing(1f) {{ //TL
				hasSprite = true;
				drawUnder = true;
				xOffset = -7.625f;
				yOffset = 7.625f;
				rotationMul = -spinSpeed;
			}}, new TeslaRing(1f) {{ //TR
				drawUnder = true;
				xOffset = yOffset = 7.625f;
				rotationMul = -spinSpeed;
			}}, new TeslaRing(1f) {{ //BL
				drawUnder = true;
				xOffset = yOffset = -7.625f;
				rotationMul = -spinSpeed;
			}}, new TeslaRing(1f) {{ //BR
				drawUnder = true;
				xOffset = 7.625f;
				yOffset = -7.625f;
				rotationMul = -spinSpeed;
			}});
			scaledHealth = 180;
			reload = 10f;
			range = 315f;
			maxTargets = 16;
			coolantMultiplier = 1f;
			hasSpinners = true;
			damage = 27f;
			status = StatusEffects.shocked;
			consumePower(8.9f);
			coolant = consumeCoolant(0.2f);
		}};
		frost = new PowerTurret("frost") {{
			requirements(Category.turret, ItemStack.with(Items.copper, 90, Items.lead, 100, Items.silicon, 90));
			targetAir = true;
			targetGround = true;
			health = 1200;
			size = 2;
			heatColor = Pal.turretHeat;
			shootSound = HSounds.jg1;
			shake = 0.2f;
			inaccuracy = 3f;
			recoil = 2f;
			shoot = new ShootAlternate() {{
				barrels = 2;
				spread = 4;
			}};
			recoilTime = 10;
			shootType = new BasicBulletType(20f, 12f) {{
				frontColor = backColor = trailColor = HPal.monolith;
				width = 4f;
				height = 8f;
				trailLength = 1;
				trailWidth = 1.3f;
				pierce = true;
				status = StatusEffects.freezing;
				statusDuration = 180f;
				lifetime = 10f;
				ammoMultiplier = 1f;
				hitEffect = new WaveEffect() {{
					lifetime = 8f;
					sizeFrom = 0f;
					sizeTo = 8f;
					strokeFrom = 1f;
					strokeTo = 0f;
					colorFrom = Color.white;
					colorTo = HPal.monolith;
				}};
				despawnEffect = new WaveEffect() {{
					lifetime = 8f;
					sizeFrom = 0f;
					sizeTo = 8f;
					strokeFrom = 1f;
					strokeTo = 0f;
					colorFrom = Color.white;
					colorTo = HPal.monolith;
				}};
			}};
			shootEffect = new WaveEffect() {{
				lifetime = 8f;
				sizeFrom = 0f;
				sizeTo = 8f;
				strokeFrom = 1f;
				strokeTo = 0f;
				colorFrom = Color.white;
				colorTo = HPal.monolith;
			}};
			consumePower(7f);
			coolant = consumeCoolant(0.2f);
			coolant.optional = true;
			reload = 5f;
			rotateSpeed = 13f;
			range = 200f;
			buildType = PowerTurretBuild::new;
		}};
		electricArrow = new PowerTurret("electric-arrow") {{
			requirements(Category.turret, ItemStack.with(Items.lead, 90, Items.titanium, 45, Items.silicon, 130));
			targetAir = true;
			targetGround = true;
			health = 1300;
			size = 2;
			shootSound = Sounds.laser;
			shake = 2f;
			inaccuracy = 0f;
			recoil = 2f;
			recoilTime = 10f;
			Color back = new Color(0xff8c00ff), front = new Color(0xffd700ff);
			shootType = new BasicBulletType(20f, 60f) {{
				splashDamageRadius = 45f;
				splashDamage = 50f;
				makeFire = true;
				trailLength = 4;
				trailWidth = 1.5f;
				trailColor = backColor = back;
				frontColor = front;
				width = 8f;
				height = 26f;
				status = StatusEffects.burning;
				statusDuration = 180f;
				lifetime = 10f;
				ammoMultiplier = 1f;
				hitEffect = new MultiEffect(new ParticleEffect() {{
					particles = 16;
					sizeFrom = 6f;
					sizeTo = 0f;
					length = 35f;
					baseLength = 10f;
					lifetime = 25f;
					colorFrom = colorTo = back.cpy().a(0.5f);
				}}, new WaveEffect() {{
					lifetime = 15f;
					sizeFrom = 1f;
					sizeTo = 30f;
					strokeFrom = 3f;
					strokeTo = 0f;
					colorFrom = back;
					colorTo = back.cpy().a(0f);
				}});
				despawnEffect = Fx.none;
				shootEffect = new WaveEffect() {{
					lifetime = 16f;
					sizeFrom = 0f;
					sizeTo = 32f;
					strokeFrom = 2f;
					strokeTo = 0f;
					colorFrom = Color.white;
					colorTo = front;
				}};
			}};
			consumePower(8f);
			coolant = consumeCoolant(0.2f);
			coolant.optional = true;
			reload = 35f;
			rotateSpeed = 6f;
			range = 200f;
			buildType = PowerTurretBuild::new;
		}};
		stabber = new ItemTurret("stabber") {{
			requirements(Category.turret, ItemStack.with(Items.copper, 85, Items.titanium, 60, Items.silicon, 70));
			health = 900;
			size = 2;
			shootSound = Sounds.shootBig;
			shake = 0.05f;
			inaccuracy = 2f;
			recoil = 2f;
			recoilTime = 10f;
			cooldownTime = 80f;
			shootEffect = Fx.shootBig;
			maxAmmo = 30;
			ammo(Items.graphite, new PointBulletType() {{
				trailEffect = Fx.none;
				hitEffect = Fx.none;
				despawnEffect = Fx.none;
				splashDamageRadius = 24f;
				splashDamage = 35f;
				knockback = 2.5f;
				speed = 20f;
				lifetime = 30f;
				ammoMultiplier = 1f;
				fragBullets = 4;
				fragBullet = new PointBulletType() {{
					trailEffect = Fx.none;
					hitEffect = Fx.flakExplosion;
					despawnEffect = Fx.flakExplosion;
					splashDamageRadius = 16f;
					splashDamage = 10f;
					status = StatusEffects.blasted;
					statusDuration = 30f;
					speed = 20f;
					lifetime = 1f;
				}};
				ammoMultiplier = 2f;
			}});
			coolant = consumeCoolant(0.2f);
			coolant.optional = true;
			reload = 20f;
			rotateSpeed = 7f;
			range = 200f;
			buildType = ItemTurretBuild::new;
		}};
		autocannonB6 = new ItemTurret("autocannon-b6") {{
			requirements(Category.turret, ItemStack.with(Items.copper, 130, Items.graphite, 60, Items.titanium, 70));
			health = 960;
			size = 2;
			reload = 20f;
			range = 200f;
			maxAmmo = 120;
			recoilTime = 10f;
			recoil = 0.5f;
			coolant = consumeCoolant(0.2f);
			coolant.optional = true;
			shoot.shots = 2;
			shoot.shotDelay = 6f;
			drawer = new DrawTurret() {{
				parts.add(new RegionPart("-barrel") {{
					mirror = false;
					progress = PartProgress.recoil;
					moveY = -2;
				}});
			}};
			shake = 1.1f;
			liquidCapacity = 30f;
			ammoPerShot = 6;
			ammoUseEffect = Fx.casing2;
			targetGround = targetAir = true;
			inaccuracy = 0.5f;
			rotateSpeed = 15;
			ammo(Items.copper, new BasicBulletType(6.5f, 11f) {{
				knockback = 0.5f;
				lifetime = 30.76f;
				width = 6f;
				height = 9f;
				ammoMultiplier = 2f;
				frontColor = Pal.copperAmmoFront;
				backColor =  Pal.copperAmmoBack;
			}}, Items.lead, new BasicBulletType(6.5f, 7f) {{
				fragBullets = 3;
				fragBullet = new BasicBulletType(0.8f, 9f);
				fragBullet.lifetime = 32f;
				lifetime = 30.76f;
				width = 6f;
				height = 9f;
				ammoMultiplier = 2f;
				frontColor = HPal.leadAmmoBack;
				backColor =  HPal.leadAmmoBack;
			}}, Items.titanium, new BasicBulletType(9f, 20f) {{
				lifetime = 22.22f;
				reloadMultiplier = 1.2f;
				width = 8f;
				height = 10f;
				ammoMultiplier = 6f;
				shootEffect = Fx.shootBig;
				frontColor = Color.white;
				backColor = HPal.titaniumAmmoBack;
			}}, Items.thorium, new BasicBulletType(8f, 28f) {{
				lifetime = 25f;
				width = 10f;
				height = 11f;
				ammoMultiplier = 6f;
				pierce = true;
				pierceCap = 2;
				shootEffect = Fx.shootBig;
				frontColor = Color.white;
				backColor = Pal.thoriumAmmoBack;
			}}, Items.graphite, new BasicBulletType(6.5f, 22f) {{
				lifetime = 34.5f;
				width = 7f;
				height = 10f;
				ammoMultiplier = 4f;
				rangeChange = 20f;
				frontColor = Pal.graphiteAmmoFront;
				backColor = Pal.graphiteAmmoBack;
			}}, Items.silicon, new BasicBulletType(6.5f, 18f) {{
				reloadMultiplier = 1.4f;
				lifetime = 30.76f;
				width = 6f;
				height = 11f;
				homingRange = 80f;
				homingPower = 0.08f;
				ammoMultiplier = 5f;
				trailLength = 5;
				trailWidth = 1.4f;
				frontColor = Pal.siliconAmmoFront;
				backColor = Pal.siliconAmmoBack;
				trailColor = Pal.siliconAmmoFront;
			}}, Items.pyratite, new BasicBulletType(6.5f, 25f) {{
				status = StatusEffects.burning;
				statusDuration = 600f;
				lifetime = 30.76f;
				backColor = Pal.darkPyraFlame;
				frontColor = Pal.lightPyraFlame;
				despawnEffect = Fx.fireHit;
				width = 6f;
				height = 9f;
				ammoMultiplier = 4f;
			}});
			buildType = ItemTurretBuild::new;
		}};
		autocannonF2 = new ItemTurret("autocannon-f2") {{
			requirements(Category.turret, ItemStack.with(Items.copper, 200, Items.graphite, 75, Items.titanium, 80));
			health = 960;
			size = 2;
			reload = 3;
			range = 200;
			maxAmmo = 600;
			recoilTime = 10;
			recoil = 1;
			unitSort = UnitSorts.weakest;
			shoot = new ShootAlternate() {{
				barrels = 2;
				spread = 6f;
			}};
			shake = 1.5f;
			liquidCapacity = 30;
			ammoUseEffect = Fx.casing2;
			targetGround = true;
			targetAir = true;
			inaccuracy = 5f;
			rotateSpeed = 15f;
			coolant = consumeCoolant(0.2f);
			coolant.optional = true;
			ammo(Items.copper, new BasicBulletType(6.5f, 11f) {{
				knockback = 0.5f;
				lifetime = 30.76f;
				width = 6f;
				height = 9f;
				ammoMultiplier = 2f;
				frontColor = Pal.copperAmmoFront;
				backColor =  Pal.copperAmmoBack;
			}}, Items.lead, new BasicBulletType(6.5f, 7f) {{
				fragBullets = 3;
				fragBullet = new BasicBulletType(0.8f, 9f);
				fragBullet.lifetime = 32f;
				lifetime = 30.76f;
				width = 6f;
				height = 9f;
				ammoMultiplier = 2f;
				frontColor = HPal.leadAmmoBack;
				backColor =  HPal.leadAmmoBack;
			}}, Items.titanium, new BasicBulletType(9f, 20f) {{
				lifetime = 22.22f;
				reloadMultiplier = 1.2f;
				width = 8f;
				height = 10f;
				ammoMultiplier = 6f;
				shootEffect = Fx.shootBig;
				frontColor = Color.white;
				backColor = HPal.titaniumAmmoBack;
			}}, Items.thorium, new BasicBulletType(8f, 28f) {{
				lifetime = 25f;
				width = 10f;
				height = 11f;
				ammoMultiplier = 6f;
				pierce = true;
				pierceCap = 2;
				shootEffect = Fx.shootBig;
				frontColor = Color.white;
				backColor = Pal.thoriumAmmoBack;
			}}, Items.graphite, new BasicBulletType(6.5f, 22f) {{
				lifetime = 34.5f;
				width = 7f;
				height = 10f;
				ammoMultiplier = 4f;
				rangeChange = 20f;
				frontColor = Pal.graphiteAmmoFront;
				backColor = Pal.graphiteAmmoBack;
			}}, Items.silicon, new BasicBulletType(6.5f, 18f) {{
				reloadMultiplier = 1.4f;
				lifetime = 30.76f;
				width = 6f;
				height = 11f;
				homingRange = 80f;
				homingPower = 0.08f;
				ammoMultiplier = 5f;
				trailLength = 5;
				trailWidth = 1.4f;
				frontColor = Pal.siliconAmmoFront;
				backColor = Pal.siliconAmmoBack;
				trailColor = Pal.siliconAmmoFront;
			}}, Items.pyratite, new BasicBulletType(6.5f, 25f) {{
				status = StatusEffects.burning;
				statusDuration = 600f;
				lifetime = 30.76f;
				backColor = Pal.darkPyraFlame;
				frontColor = Pal.lightPyraFlame;
				despawnEffect = Fx.fireHit;
				width = 6f;
				height = 9f;
				ammoMultiplier = 4f;
			}});
			buildType = ItemTurretBuild::new;
		}};
		shellshock = new ItemTurret("shellshock") {{
			requirements(Category.turret, ItemStack.with(Items.titanium, 210, Items.graphite, 125, Items.thorium, 125, Items.metaglass, 20));
			health = 2200;
			armor = 5f;
			size = 3;
			reload = 4.5f;
			range = 280f;
			maxAmmo = 150;
			shoot = new ShootAlternate() {{
				barrels = 2;
				spread = 9f;
			}};
			recoilTime = 25f;
			recoil = 2.88f;
			shake = 1.5f;
			coolantMultiplier = 1000f;
			coolant = consumeCoolant(0.001f);
			coolant.optional = true;
			liquidCapacity = 60f;
			shootSound = Sounds.shootBig;
			ammoUseEffect = Fx.casing3;
			targetGround = true;
			targetAir = true;
			shootCone = 180f;
			inaccuracy = 2f;
			rotateSpeed = 6f;
			ammo(Items.graphite, new BasicBulletType(13f, 33f) {{
				lifetime = 22f;
				splashDamage = 13f;
				splashDamageRadius = 15f;
				knockback = 0.25f;
				width = 8f;
				height = 13f;
				ammoMultiplier = 4f;
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				hitEffect = Fx.flakExplosion;
				frontColor = Pal.copperAmmoFront;
				backColor = Pal.copperAmmoBack;
			}}, Items.silicon, new BasicBulletType(13f, 26f) {{
				reloadMultiplier = 1.5f;
				homingPower = 0.12f;
				lifetime = 22f;
				width = 9f;
				height = 13f;
				ammoMultiplier = 5f;
				shootEffect = Fx.shootBig;
				hitEffect = Fx.hitBulletBig;
				trailLength = 8;
				trailWidth = 1.35f;
				frontColor = Pal.siliconAmmoFront;
				backColor = Pal.siliconAmmoBack;
				trailColor = Pal.siliconAmmoFront;
			}}, Items.pyratite, new BasicBulletType(17.5f, 22f) {{
				lifetime = 16.5f;
				splashDamageRadius = 21f;
				splashDamage = 36f;
				status = StatusEffects.burning;
				statusDuration = 600f;
				makeFire = true;
				backColor = Pal.darkPyraFlame;
				frontColor = Pal.lightPyraFlame;
				width = 9f;
				height = 14f;
				ammoMultiplier = 2f;
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				hitEffect = new MultiEffect(Fx.flakExplosion, Fx.fireHit);
			}}, Items.thorium, new BasicBulletType(17.5f, 49f) {{
				lifetime = 16.5f;
				width = 10f;
				height = 15f;
				ammoMultiplier = 4f;
				pierce = true;
				pierceCap = 2;
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				hitEffect = Fx.hitBulletBig;
				frontColor = Pal.thoriumAmmoFront;
				backColor = Pal.thoriumAmmoBack;
			}}, HItems.uranium, new BasicBulletType(17f, 68f) {{
				rangeChange = 40f;
				lifetime = 19f;
				pierce = true;
				pierceCap = 2;
				width = 11f;
				height = 15f;
				ammoMultiplier = 6f;
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				hitEffect = Fx.hitBulletBig;
				frontColor = Color.white;
				backColor = HPal.uraniumAmmoBack;
			}}, HItems.chromium, new BasicBulletType(17f, 61f) {{
				rangeChange = 40f;
				status = StatusEffects.slow;
				statusDuration = 6f;
				lifetime = 19f;
				knockback = 6f;
				width = 11f;
				height = 15f;
				ammoMultiplier = 6f;
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				hitEffect = Fx.hitBulletBig;
				frontColor = Color.white;
				backColor = HPal.chromiumAmmoBack;
			}});
			buildType = ItemTurretBuild::new;
		}};
		rocketLauncher = new ItemTurret("rocket-launcher") {{
			requirements(Category.turret, ItemStack.with(Items.copper, 60, Items.lead, 40, Items.graphite, 30));
			ammo(Items.graphite, new MissileBulletType(3.6f, 30f) {{
				splashDamage = 15f;
				splashDamageRadius = 18f;
				drag = -0.028f;
				hitColor = backColor = trailColor = Pal.graphiteAmmoBack;
				frontColor = Pal.graphiteAmmoFront;
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
				frontColor = Pal.lightishOrange;
				backColor = Pal.lightOrange;
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
				hitColor = backColor = trailColor = Pal.blastAmmoBack;
				frontColor = Pal.blastAmmoFront;
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
			buildType = ItemTurretBuild::new;
		}};
		largeRocketLauncher = new ItemTurret("large-rocket-launcher") {{
			requirements(Category.turret, ItemStack.with(Items.graphite, 360, Items.titanium, 220, Items.thorium, 100, Items.silicon, 110, Items.plastanium, 70));
			ammo(Items.pyratite, new MissileBulletType(10f, 44f, MOD_NAME + "-rocket") {{
				shrinkY = 0;
				inaccuracy = 4;
				trailChance = 0.8f;
				homingRange = 80;
				splashDamage = 68f;
				splashDamageRadius = 36f;
				lifetime = 38f;
				hitShake = 2;
				frontColor = Pal.lightishOrange;
				backColor = Pal.lightOrange;
				status = StatusEffects.burning;
				statusDuration = 600;
				width = 16;
				height = 40;
				ammoMultiplier = 3;
				shootEffect = new MultiEffect(Fx.shootBig2, Fx.shootPyraFlame, Fx.shootPyraFlame);
				despawnEffect = Fx.flakExplosion;
				hitEffect = new MultiEffect(new ParticleEffect() {{
					particles = 8;
					sizeFrom = 8;
					sizeTo = 0;
					length = 15;
					baseLength = 15;
					lifetime = 35;
					colorFrom = colorTo = HPal.smoke;
				}}, new ParticleEffect() {{
					particles = 12;
					line = true;
					length = 30;
					baseLength = 8;
					lifetime = 22;
					colorFrom = Color.white;
					colorTo = HPal.missileYellow;
				}}, new WaveEffect() {{
					lifetime = 10;
					sizeFrom = 1;
					sizeTo = 36;
					strokeFrom = 8;
					strokeTo = 0;
					colorFrom = HPal.missileYellow;
					colorTo = HPal.missileYellowBack;
				}});
			}}, Items.blastCompound, new MissileBulletType(10f, 46f, MOD_NAME + "-missile") {{
				recoil = 1;
				shrinkY = 0;
				inaccuracy = 4;
				trailChance = 0.8f;
				homingRange = 80;
				splashDamage = 132f;
				splashDamageRadius = 76f;
				lifetime = 38f;
				hitShake = 2;
				hitColor = backColor = trailColor = Pal.blastAmmoBack;
				frontColor = Pal.blastAmmoFront;
				status = StatusEffects.burning;
				statusDuration = 600;
				width = 14;
				height = 50;
				hitSound = Sounds.explosion;
				ammoMultiplier = 3;
				shootEffect = new MultiEffect(Fx.shootBig2, Fx.shootPyraFlame, Fx.shootPyraFlame);
				despawnEffect = Fx.flakExplosion;
				hitEffect = new MultiEffect(new ParticleEffect() {{
					particles = 12;
					sizeFrom = 10;
					sizeTo = 0;
					length = 35;
					baseLength = 33;
					lifetime = 35;
					colorFrom = HPal.smoke;
					colorTo = HPal.smoke.cpy().a(0.5f);
				}}, new ParticleEffect() {{
					particles = 12;
					line = true;
					interp = Interp.pow10Out;
					strokeFrom = 2f;
					strokeTo = 1.5f;
					lenFrom = 16f;
					lenTo = 0f;
					length = 43f;
					baseLength = 23f;
					lifetime = 11f;
					colorFrom = Color.white;
					colorTo = HPal.missileYellow;
				}}, new WaveEffect() {{
					lifetime = 10;
					sizeFrom = 1;
					sizeTo = 78;
					strokeFrom = 8;
					strokeTo = 0;
					colorFrom = colorTo = HPal.missileYellow;
				}});
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
			smokeEffect = new ParticleEffect() {{
				particles = 8;
				sizeFrom = 3;
				sizeTo = 0;
				length = -35;
				baseLength = -15;
				lifetime = 65;
				colorFrom = Color.white;
				colorTo = HPal.missileGray.cpy().a(0.4f);
				layer = 49;
				interp = Interp.pow5Out;
				sizeInterp = Interp.pow10In;
				cone = 10;
			}};
			shoot = new ShootAlternate() {{
				barrels = 2;
				spread = 11f;
				shots = 3;
				shotDelay = 8f;
			}};
			consumeAmmoOnce = false;
			maxAmmo = 24;
			ammoPerShot = 3;
			buildType = ItemTurretBuild::new;
		}};
		rocketSilo = new ItemTurret("rocket-silo") {{
			requirements(Category.turret, ItemStack.with(Items.lead, 300, Items.graphite, 150, Items.titanium, 120, Items.silicon, 120, Items.plastanium, 50));
			ammo(Items.graphite, new MissileBulletType(8f, 22f, MOD_NAME + "-missile") {{
				buildingDamageMultiplier = 0.3f;
				splashDamage = 15f;
				splashDamageRadius = 18f;
				knockback = 0.7f;
				lifetime = 135f;
				homingDelay = 10f;
				homingRange = 800f;
				homingPower = 0.15f;
				hitColor = backColor = trailColor = Pal.graphiteAmmoBack;
				frontColor = Pal.graphiteAmmoFront;
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
			}}, Items.pyratite, new MissileBulletType(7f, 14f, MOD_NAME + "-missile") {{
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
				frontColor = Pal.lightishOrange;
				backColor = Pal.lightOrange;
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
			}}, Items.blastCompound, new MissileBulletType(7f, 17f, MOD_NAME + "-missile") {{
				buildingDamageMultiplier = 0.3f;
				splashDamage = 55f;
				splashDamageRadius = 45f;
				knockback = 3;
				status = StatusEffects.blasted;
				lifetime = 145f;
				homingPower = 0.15f;
				homingDelay = 10f;
				homingRange = 800f;
				hitColor = backColor = trailColor = Pal.blastAmmoBack;
				frontColor = Pal.blastAmmoFront;
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
			}}, Items.surgeAlloy, new MissileBulletType(9f, 47f, MOD_NAME + "-missile") {{
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
				backColor = hitColor = trailColor = Pal.surgeAmmoBack;
				frontColor = Pal.surgeAmmoFront;
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
			ammoUseEffect = HFx.casing(60f);
			canOverdrive = false;
			shootSound = HSounds.dd1;
			consumePowerCond(6f, TurretBuild::isActive);
			buildType = ItemTurretBuild::new;
		}};
		caelum = new ItemTurret("caelum") {{
			requirements(Category.turret, ItemStack.with(Items.copper, 560, Items.graphite, 500, Items.silicon, 400, Items.titanium, 400, Items.plastanium, 170));
			health = 5500;
			size = 4;
			reload = 26f;
			range = 640f;
			fogRadiusMultiplier = 0.25f;
			maxAmmo = 20;
			ammoPerShot = 2;
			recoil = 4f;
			ammoUseEffect = new ParticleEffect() {{
				particles = 9;
				interp = Interp.pow10Out;
				sizeInterp = Interp.pow5In;
				sizeFrom = 5.5f;
				sizeTo = 0f;
				length = 30f;
				baseLength = 0f;
				lifetime = 55f;
				colorFrom = colorTo = HPal.smoke.cpy().a(0.525f);
				layer = 49f;
			}};
			canOverdrive = false;
			shoot = new ShootAlternate() {{
				barrels = 2;
				spread = 18;
			}};
			xRand = 4;
			coolant = consumeCoolant(0.5f);
			coolant.optional = true;
			shootSound = HSounds.dd1;
			targetGround = false;
			targetAir = true;
			hasLiquids = true;
			inaccuracy = 7.77f;
			shootCone = 270f;
			shake = 4f;
			rotateSpeed = 0.85f;
			Color caelumAmmo = new Color(0xeeee00ff);
			ammo(Items.blastCompound, new FlakBulletType(6f, 33f) {{
				damage = 33;
				splashDamageRadius = 64f;
				splashDamage = 255f;
				knockback = 10f;
				hitSize = 50f;
				shrinkY = 0;
				hitSound = Sounds.explosionbig;
				hitSoundVolume = 2f;
				speed = 6f;
				lifetime = 135f;
				status = StatusEffects.blasted;
				homingDelay = 15f;
				homingPower = 0.08f;
				homingRange = 120f;
				width = 15f;
				height = 55f;
				sprite = MOD_NAME + "-missile";
				backColor = Pal.blastAmmoBack;
				frontColor = HPal.missileGray;
				trailLength = 40;
				trailWidth = 2f;
				trailColor = Color.white.cpy().a(0.5f);
				trailChance = 1f;
				trailInterval = 16f;
				trailEffect = new ParticleEffect() {{
					particles = 3;
					length = 16f;
					baseLength = 1f;
					lifetime = 45f;
					colorFrom = colorTo = HPal.missileGray.cpy().a(0.475f);
					sizeFrom = 3f;
					sizeTo = 0f;
				}};
				hitShake = 2.5f;
				ammoMultiplier = 4f;
				reloadMultiplier = 1.7f;
				shootEffect = Fx.shootTitan;
				smokeEffect = Fx.shootPyraFlame;
				hitEffect = new MultiEffect(new ParticleEffect() {{
					particles = 18;
					sizeFrom = 10f;
					sizeTo = 0f;
					length = 35f;
					baseLength = 43f;
					lifetime = 35f;
					colorFrom = colorTo = HPal.smoke;
				}}, new ParticleEffect() {{
					particles = 32;
					line = true;
					sizeFrom = 9f;
					sizeTo = 0f;
					length = 43f;
					baseLength = 33f;
					lifetime = 22f;
					colorFrom = Color.white;
					colorTo = HPal.missileYellow;
				}}, new WaveEffect() {{
					lifetime = 15;
					sizeFrom = 1f;
					sizeTo = 70f;
					strokeFrom = 8f;
					strokeTo = 0f;
					colorFrom = HPal.missileYellow;
					colorTo = Color.white;
				}});
			}}, Items.surgeAlloy, new FlakBulletType(7f, 50f) {{
				splashDamageRadius = 40f;
				splashDamage = 250f;
				lightningDamage = 38f;
				lightning = 5;
				lightningLength = 6;
				lightningLengthRand = 2;
				shrinkY = 0f;
				hitSize = 30f;
				homingDelay = 15f;
				homingPower = 0.09f;
				homingRange = 120f;
				lifetime = 115;
				hitSound = Sounds.explosion;
				hitSoundVolume = 5f;
				width = 16f;
				height = 56f;
				ammoMultiplier = 6f;
				hitShake = 1.6f;
				sprite = MOD_NAME + "-rocket";
				backColor = caelumAmmo;
				frontColor = HPal.missileGray;
				trailLength = 40;
				trailWidth = 2f;
				trailColor = Color.white.cpy().a(0.5f);
				trailChance = 1f;
				trailInterval = 16f;
				trailEffect = new ParticleEffect() {{
					particles = 3;
					length = 16f;
					baseLength = 1f;
					lifetime = 45f;
					colorFrom = colorTo = HPal.missileGray.cpy().a(0.475f);
					sizeFrom = 3f;
					sizeTo = 0f;
				}};
				shootEffect = Fx.shootTitan;
				smokeEffect = Fx.shootPyraFlame;
				hitEffect = new MultiEffect(new ParticleEffect() {{
					particles = 10;
					sizeFrom = 10f;
					sizeTo = 0f;
					length = 5f;
					baseLength = 33f;
					lifetime = 35f;
					colorFrom = caelumAmmo.cpy().a(0.71f);
					colorTo = HPal.smoke;
				}}, new ParticleEffect() {{
					particles = 12;
					line = true;
					sizeFrom = 9f;
					sizeTo = 0f;
					length = 13f;
					baseLength = 43f;
					lifetime = 22f;
					colorFrom = caelumAmmo;
					colorTo = HPal.missileYellow;
				}}, new WaveEffect() {{
					lifetime = 10f;
					sizeFrom = 1f;
					sizeTo = 43f;
					strokeFrom = 8f;
					strokeTo = 0f;
					colorFrom = caelumAmmo;
					colorTo = Color.white;
				}});
			}}, HItems.uranium, new BasicBulletType(10f, 340f, MOD_NAME + "-rocket") {{
				collidesGround = false;
				status = StatusEffects.melting;
				statusDuration = 180f;
				pierceArmor = true;
				hitShake = 1.2f;
				homingDelay = 15f;
				homingPower = 0.12f;
				homingRange = 160f;
				lifetime = 88f;
				hitSound = Sounds.shotgun;
				hitSoundVolume = 0.2f;
				width = 15f;
				height = 53f;
				shrinkY = 0f;
				ammoMultiplier = 1f;
				backColor = HPal.ferium;
				frontColor = HPal.missileGray;
				trailLength = 20;
				trailWidth = 1.7f;
				trailColor = Color.white;
				trailChance = 1f;
				trailInterval = 1;
				trailRotation = true;
				trailEffect = new ParticleEffect() {{
					particles = 2;
					length = -40f;
					baseLength = 0f;
					lifetime = 15f;
					cone = 20f;
					colorFrom = HPal.missileYellow;
					colorTo = HPal.missileYellow.cpy().a(0.47f);
					sizeFrom = 1f;
					sizeTo = 5f;
					shootEffect = Fx.shootTitan;
					smokeEffect = Fx.shootPyraFlame;
					hitEffect = new MultiEffect(new ParticleEffect() {{
						particles = 9;
						sizeFrom = 3f;
						sizeTo = 0f;
						length = 33f;
						baseLength = 6f;
						lifetime = 25f;
						colorFrom = colorTo = HPal.smoke;
					}}, new ParticleEffect() {{
						particles = 15;
						line = true;
						strokeFrom = 4f;
						strokeTo = lenFrom = 0f;
						lenTo = 10f;
						length = 83f;
						baseLength = 3f;
						lifetime = 10f;
						colorFrom = Color.white;
						colorTo = HPal.missileYellow;
						cone = 40f;
					}}, new WaveEffect() {{
						lifetime = 10f;
						sizeFrom = 1f;
						sizeTo = strokeFrom = strokeTo = 0f;
						colorFrom = caelumAmmo;
						colorTo = Color.white;
					}});
				}};
			}});
			buildType = ItemTurretBuild::new;
		}};
		mammoth = new ItemTurret("mammoth") {{
			requirements(Category.turret, ItemStack.with(Items.copper, 95, Items.graphite, 55, Items.titanium, 65));
			health = 1200;
			size = 2;
			reload = 60f;
			shootSound = Sounds.artillery;
			shake = 1.7f;
			coolant = consumeCoolant(0.2f);
			coolant.optional = true;
			shoot.shots = 2;
			shoot.shotDelay = 15f;
			drawer = new DrawTurret() {{
				parts.add(new RegionPart("-barrel") {{
					mirror = false;
					progress = PartProgress.recoil;
					moveY = -4f;
				}});
			}};
			targetGround = true;
			targetAir = false;
			range = 280f;
			recoilTime = 20f;
			recoil = 0.5f;
			ammoUseEffect = Fx.casing3;
			inaccuracy = 3f;
			rotateSpeed = 6f;
			ammoPerShot = 2;
			ammo(Items.graphite, new ArtilleryBulletType(4f, 3f) {{
				pierceArmor = true;
				lifetime = 64f;
				width = 12f;
				height = 12f;
				knockback = 1f;
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				splashDamageRadius = 20;
				splashDamage = 40;
				hitEffect = Fx.explosion;
				despawnEffect = Fx.explosion;
				frontColor = trailColor = hitColor = Pal.graphiteAmmoFront;
				backColor = Pal.graphiteAmmoBack;
			}}, Items.silicon, new ArtilleryBulletType(4f, 2.5f) {{
				pierceArmor = true;
				reloadMultiplier = 2f;
				lifetime = 70f;
				width = 12f;
				height = 12f;
				knockback = 1f;
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				homingRange = 40f;
				homingPower = 0.1f;
				splashDamageRadius = 20f;
				splashDamage = 38f;
				hitEffect = Fx.explosion;
				despawnEffect = Fx.explosion;
				trailLength = 10;
				trailWidth = 3;
				frontColor = trailColor = hitColor = Pal.siliconAmmoFront;
				backColor = Pal.siliconAmmoBack;
			}}, Items.pyratite, new ArtilleryBulletType(4f, 2f) {{
				pierceArmor = true;
				speed = 4f;
				lifetime = 64f;
				width = 12f;
				height = 12f;
				knockback = 1f;
				backColor = Pal.darkPyraFlame;
				frontColor = Pal.lightPyraFlame;
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				splashDamageRadius = 20f;
				splashDamage = 45f;
				makeFire = true;
				status = StatusEffects.burning;
				statusDuration = 600f;
				hitEffect = Fx.explosion;
				despawnEffect = Fx.explosion;
			}}, Items.blastCompound, new ArtilleryBulletType(4f, 2f) {{
				pierceArmor = true;
				speed = 4;
				lifetime = 64;
				width = 13;
				height = 13;
				status = StatusEffects.blasted;
				statusDuration = 60;
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				frontColor = trailColor = hitColor = Pal.blastAmmoFront;
				backColor = Pal.blastAmmoBack;
				splashDamageRadius = 45f;
				splashDamage = 86f;
				knockback = 1.3f;
				hitEffect = Fx.blastExplosion;
				despawnEffect = Fx.flakExplosionBig;
			}});
			buildType = ItemTurretBuild::new;
		}};
		dragonBreath = new ItemTurret("dragon-breath") {{
			requirements(Category.turret, ItemStack.with(Items.graphite, 40, Items.silicon, 25, Items.titanium, 60, Items.plastanium, 30));
			ammo(Items.coal, new ConeFlameBulletType(Pal.lightFlame, Pal.darkFlame, Color.gray, range + 8, 14, 60, 22), Items.pyratite, new ConeFlameBulletType(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, range + 8, 20, 72, 22) {{
				damage = 98;
				statusDuration = 60 * 6;
				ammoMultiplier = 4;
			}}, Items.blastCompound, new ConeFlameBulletType(Pal.blastAmmoBack.cpy().mul(Pal.lightFlame), Pal.blastAmmoBack.cpy(), Pal.lightishGray, range + 8, 22, 66, 30) {{
				damage = 90;
				status = HStatusEffects.flamePoint;
				statusDuration = 8 * 60f;
				reloadMultiplier = 0.5f;
				ammoMultiplier = 4;
			}
				public final float slpRange = 52f;
				public final Effect easyExp = new Effect(20, e -> {
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
								easyExp.at(unit.x, unit.y, slpRange, Pal.blastAmmoBack);
								unit.unapply(status);
							} else {
								unit.apply(status, statusDuration);
							}
						}
					});
					Vars.indexer.allBuildings(b.x, b.y, flameLength, other -> {
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
			buildType = ItemTurretBuild::new;
		}};
		breakthrough = new PowerTurret("breakthrough") {{
			requirements(Category.turret, ItemStack.with(Items.silicon, 320, Items.titanium, 180, Items.thorium, 150, Items.plastanium, 100, Items.phaseFabric, 30));
			size = 4;
			health = 2800;
			armor = 3f;
			range = 500f;
			coolantMultiplier = 1.5f;
			moveWhileCharging = false;
			accurateDelay = false;
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
			shootType = new LaserBulletType(1550f) {{
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
			buildType = PowerTurretBuild::new;
			hideDetails = false;
		}};
		cloudBreaker = new ItemTurret("cloud-breaker") {{
			requirements(Category.turret, ItemStack.with(Items.graphite, 230, Items.titanium, 220, Items.thorium, 150));
			ammo(Items.titanium, new CritBulletType(14f, 220f) {{
				lifetime = 25f;
				knockback = 5f;
				width = 7f;
				height = 12f;
				pierce = pierceArmor = true;
				pierceCap = 3;
				critChance = 0.08f;
				critMultiplier = 2.5f;
				critColor = HPal.titaniumAmmoBack;
				frontColor = Color.white;
				backColor = HPal.titaniumAmmoBack;
				trailLength = 5;
				trailWidth = 1.5f;
				trailColor = HPal.titaniumAmmoBack.cpy().a(0.3f);
				trailChance = 0.5f;
				trailRotation = true;
				trailEffect = new ParticleEffect() {{
					sizeInterp = Interp.pow3In;
					particles = 3;
					strokeFrom = 1f;
					strokeTo = 0f;
					line = true;
					lenFrom = 8f;
					lenTo = 0f;
					length = 15f;
					baseLength = 0f;
					lifetime = 10f;
					colorFrom = Color.white;
					colorTo = HPal.titaniumAmmoBack;
					cone = 10f;
				}};
				shootEffect = Fx.bigShockwave;
				smokeEffect = Fx.shootBigSmoke;
				hitEffect = new ParticleEffect() {{
					particles = 15;
					line = true;
					strokeFrom = 3f;
					strokeTo = 0f;
					lenFrom = 10f;
					lenTo = 0f;
					length = 50f;
					baseLength = 0f;
					lifetime = 10f;
					colorFrom = HPal.missileYellow;
					colorTo = Color.white;
					cone = 60f;
				}};
				despawnEffect = Fx.none;
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
				critColor = Pal.thoriumAmmoBack;
				frontColor = Color.white;
				backColor = Pal.thoriumAmmoBack;
				trailLength = 6;
				trailWidth = 1.8f;
				trailColor = Pal.thoriumAmmoBack.cpy().a(0.3f);
				trailChance = 0.8f;
				trailRotation = true;
				trailEffect = new ParticleEffect() {{
					sizeInterp = Interp.pow3In;
					particles = 3;
					strokeFrom = 1f;
					strokeTo = 0f;
					line = true;
					lenFrom = 8f;
					lenTo = 0f;
					length = 15f;
					baseLength = 0f;
					lifetime = 15f;
					colorFrom = Color.white;
					colorTo = Pal.thoriumAmmoBack;
					cone = 10f;
				}};
				shootEffect = Fx.bigShockwave;
				smokeEffect = Fx.shootBigSmoke;
				hitEffect = new ParticleEffect() {{
					particles = 15;
					line = true;
					strokeFrom = 4f;
					strokeTo = 0f;
					lenFrom = 10f;
					lenTo = 0f;
					length = 70f;
					baseLength = 0f;
					lifetime = 10f;
					colorFrom = HPal.missileYellow;
					colorTo = Color.white;
					cone = 60f;
				}};
				despawnEffect = Fx.none;
			}}, HItems.uranium, new CritBulletType(20f, 360f) {{
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
					critEffect = HFx.miniCrit;
					critColor = HPal.uraniumAmmoBack;
				}};
				critColor = HPal.uraniumAmmoBack;
				frontColor = Color.white;
				backColor = HPal.uraniumAmmoBack;
				trailChance = 0f;
				trailInterval = 0.5f;
				trailLength = 6;
				trailWidth = 2f;
				trailColor = Color.white.cpy().a(0.5f);
				trailRotation = true;
				trailEffect = new ParticleEffect() {{
					sizeInterp = Interp.pow3In;
					particles = 3;
					strokeFrom = 1.1f;
					strokeTo = 0f;
					line = true;
					lenFrom = 8f;
					lenTo = 0f;
					length = 45f;
					baseLength = 0f;
					lifetime = 10f;
					colorFrom = HPal.discDark;
					colorTo = HPal.uraniumAmmoBack;
					cone = 10f;
				}};
				shootEffect = Fx.bigShockwave;
				smokeEffect = Fx.shootBigSmoke;
				hitEffect = new ParticleEffect() {{
					particles = 15;
					line = true;
					strokeFrom = 5f;
					strokeTo = 0f;
					lenFrom = 16f;
					lenTo = 0f;
					length = 100f;
					baseLength = 0f;
					lifetime = 10f;
					colorFrom = HPal.missileYellow;
					colorTo = Color.white;
					cone = 60f;
				}};
				despawnEffect = Fx.none;
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
			buildType = ItemTurretBuild::new;
		}};
		turbulence = new LiquidTurret("turbulence") {{
			requirements(Category.turret, ItemStack.with(Items.titanium, 100, Items.thorium, 80, Items.metaglass, 70, Items.plastanium, 80, Items.surgeAlloy, 60));
			ammo(Liquids.water, new LiquidBulletType(Liquids.water) {{
				lifetime = 30f;
				speed = 7f;
				puddleSize = 6f;
				orbSize = 5f;
				knockback = 1.7f;
				statusDuration = 600f;
				status = StatusEffects.wet;
				damage = 0.4f;
			}}, Liquids.cryofluid, new LiquidBulletType(Liquids.cryofluid) {{
				lifetime = 30f;
				speed = 7f;
				puddleSize = 6f;
				orbSize = 5f;
				knockback = 1.3f;
				statusDuration = 600f;
				status = StatusEffects.freezing;
				damage = 0.4f;
			}}, Liquids.oil, new LiquidBulletType(Liquids.oil) {{
				layer = 98;
				lifetime = 30f;
				speed = 8f;
				puddleSize = 6f;
				orbSize = 5f;
				knockback = 1.5f;
				statusDuration = 600f;
				status = StatusEffects.tarred;
				damage = 0.4f;
				incendAmount = 1;
				incendSpread = 1f;
				incendChance = 1f;
			}}, Liquids.slag, new LiquidBulletType(Liquids.slag) {{
				lifetime = 30f;
				speed = 7f;
				puddleSize = 6f;
				orbSize = 5f;
				knockback = 1.5f;
				statusDuration = 600f;
				status = StatusEffects.melting;
				damage = 9.5f;
			}}, HLiquids.crystalFluid, new LiquidBulletType(HLiquids.crystalFluid) {{
				lifetime = 30f;
				speed = 7f;
				puddleSize = 6f;
				orbSize = 5f;
				knockback = 0.6f;
				healPercent = 5f;
				collidesTeam = true;
				status = StatusEffects.electrified;
				statusDuration = 600f;
				damage = 1.5f;
			}}, HLiquids.lightOil, new LiquidBulletType(HLiquids.lightOil) {{
				layer = 98f;
				lifetime = 30f;
				speed = 8f;
				puddleSize = 6f;
				orbSize = 5f;
				knockback = 1.5f;
				statusDuration = 600f;
				status = StatusEffects.tarred;
				hitEffect = Fx.flakExplosion;
				despawnEffect = Fx.flakExplosion;
				damage = 0.4f;
			}}, HLiquids.nitratedOil, new LiquidBulletType(HLiquids.nitratedOil) {{
				layer = 98f;
				lifetime = 30f;
				speed = 7.5f;
				puddleSize = 6f;
				orbSize = 5f;
				knockback = 2.5f;
				statusDuration = 600f;
				status = StatusEffects.tarred;
				hitEffect = Fx.flakExplosion;
				despawnEffect = Fx.flakExplosion;
				splashDamage = 33f;
				splashDamageRadius = 30f;
				damage = 2.5f;
			}}, HLiquids.blastReagent, new LiquidBulletType(HLiquids.blastReagent) {{
				layer = 98f;
				lifetime = 30f;
				speed = 7f;
				puddleSize = 6f;
				orbSize = 5f;
				knockback = 2.5f;
				statusDuration = 600f;
				status = StatusEffects.tarred;
				hitEffect = Fx.flakExplosion;
				despawnEffect = Fx.flakExplosion;
				splashDamage = 66f;
				splashDamageRadius = 45f;
				damage = 6.5f;
			}});
			size = 4;
			health = 2700;
			shoot.shots = 5;
			shoot.shotDelay = 3f;
			reload = 0.6f;
			range = 220f;
			recoilTime = 10f;
			recoil = 1f;
			targetGround = true;
			targetAir = true;
			inaccuracy = 1f;
			rotateSpeed = 7.5f;
			shootCone = 100f;
			velocityRnd = 0.1f;
			liquidCapacity = 400f;
		}};
		ironStream = new LiquidTurret("iron-stream") {{
			requirements(Category.turret, ItemStack.with(Items.lead, 250, Items.metaglass, 150, Items.titanium, 120, Items.thorium, 100));
			ammo(Liquids.slag, new RailBulletType() {{
				pointEffectSpace = 18f;
				pointEffect = Fx.railTrail;
				damage = 135f;
				knockback = 3f;
				lifetime = 30f;
				length = 208f;
				pierce = true;
				pierceDamageFactor = 0.3f;
				hitEffect = Fx.hitMeltdown;
				status = StatusEffects.melting;
				statusDuration = 600f;
				shootEffect = Fx.instShoot;
				smokeEffect = Fx.none;
			}});
			health = 2200;
			size = 3;
			reload = 24f;
			range = 208f;
			recoilTime = 30f;
			recoil = 3f;
			shootSound = Sounds.shotgun;
			targetGround = true;
			targetAir = true;
			inaccuracy = 2f;
			shoot = new ShootSpread() {{
				shots = 3;
				spread = 0f;
			}};
			shake = 2f;
			rotateSpeed = 9f;
			liquidCapacity = 10f;
			buildCostMultiplier = 0.8f;
			buildType = LiquidTurretBuild::new;
		}};
		minigun = new MinigunTurret("minigun") {{
			requirements(Category.turret, ItemStack.with(Items.copper, 350, Items.graphite, 300, Items.titanium, 150, Items.plastanium, 175, Items.surgeAlloy, 120));
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
			}}, HItems.uranium, new BasicBulletType(17f, 65f) {{
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
			ammoUseEffect = HFx.casing(32f);
			coolant = consumeCoolant(0.2f);
		}};
		hurricane = new SpeedupTurret("hurricane") {{
			requirements(Category.turret, ItemStack.with(Items.lead, 80, Items.graphite, 100, Items.silicon, 250, Items.plastanium, 120, Items.surgeAlloy, 80, Items.phaseFabric, 150));
			size = 3;
			health = 960;
			hasLiquids = true;
			range = 300f;
			reload = 60f;
			shoot = new ShootAlternate() {{
				spread = 7f;
			}};
			shootCone = 24f;
			shootSound = Sounds.spark;
			shootType = new PositionLightningBulletType(150f) {{
				lightningColor = hitColor = Pal.techBlue;
				maxRange = rangeOverride = 250f;
				hitEffect = HFx.hitSpark;
				smokeEffect = Fx.shootBigSmoke2;
			}};
			warmupMaintainTime = 120f;
			rotateSpeed = 3f;
			maxSpeedupScl = 4f;
			speedupPerShoot = 0.2f;
			overheatTime = 600f;
			overheatCoolAmount = 2f;
			coolant = new ConsumeCoolant(0.15f);
			coolant.optional(false, false);
			consumePowerCond(15f, TurretBuild::isActive);
			canOverdrive = false;
		}};
		judgement = new ContinuousTurret("judgement") {{
			requirements(Category.turret, ItemStack.with(Items.silicon, 1200, Items.metaglass, 400, Items.plastanium, 800, Items.surgeAlloy, 650, HItems.crystal, 350, HItems.galliumNitride, 360, HItems.crystallineElectronicUnit, 180, HItems.heavyAlloy, 600));
			shootType = new PointLaserBulletType() {{
				damage = 100f;
				hitEffect = HFx.hitSpark;
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
						if (!Vars.headless && b.timer(3, 3)) {
							PositionLightning.createEffect(b, Tmp.v1.set(b.aimX, b.aimY), getColor(b), 1, 2);
							if (Mathf.chance(0.25)) HFx.hitSparkLarge.at(b.x, b.y, tmpColor);
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
					Rand rand = HFx.rand3;
					rand.setSeed(b.id);

					float base = (Time.time / particleLife);
					for (int i = 0; i < particles; i++) {
						float fin = (rand.random(1f) + base) % 1f, fout = 1f - fin, fslope = HFx.fslope(fin);
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
						Draw.z(HLayer.effectBottom);
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
			armor = 20f;
			unitSort = UnitSorts.strongest;
			buildCostMultiplier = 0.8f;
			consumePower(26f);
			consumeLiquid(HLiquids.crystalFluid, 12f / 60f);
			buildType = ContinuousTurretBuild::new;
		}};
		evilSpirits = new ItemTurret("evil-spirits") {{
			requirements(Category.turret, ItemStack.with(Items.copper, 600, Items.graphite, 500, Items.plastanium, 250, HItems.uranium, 220, Items.surgeAlloy, 280));
			ammo(Items.graphite, new BasicBulletType(7.5f, 82f) {{
				hitSize = 5f;
				width = 16.8f;
				height = 23.52f;
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				hitColor = backColor = trailColor = Pal.graphiteAmmoBack;
				frontColor = Pal.graphiteAmmoFront;
				ammoMultiplier = 4;
				reloadMultiplier = 1.7f;
				knockback = 0.4f;
				lifetime *= 1.2f;
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
				knockback = 0.8f;
				lifetime *= 1.2f;
			}}, Items.blastCompound, new BasicBulletType(8.25f, 54f) {{
				rangeChange = 5f;
				hitSize = 5f;
				width = 16.8f;
				height = 23.52f;
				hitColor = backColor = trailColor = Pal.blastAmmoBack;
				frontColor = Pal.blastAmmoFront;
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
				knockback = 1.2f;
				lifetime *= 1.2f;
			}}, Items.thorium, new BasicBulletType(8.75f, 122f) {{
				rangeChange = 15f;
				pierce = true;
				pierceCap = 2;
				pierceBuilding = true;
				hitSize = 5.2f;
				width = 16.8f;
				height = 23.52f;
				shootEffect = Fx.shootBig;
				smokeEffect = Fx.shootBigSmoke;
				backColor = hitColor = trailColor = Pal.thoriumAmmoBack;
				frontColor = Pal.thoriumAmmoFront;
				knockback = 0.9f;
				lifetime *= 1.1f;
			}}, HItems.uranium, new BasicBulletType(9.75f, 155f) {{
				rangeChange = 30f;
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
				backColor = hitColor = trailColor = HPal.uraniumAmmoBack;
				frontColor = Color.white.cpy();
				knockback = 0.9f;
				lifetime *= 1.05f;
			}});
			size = 5;
			health = 6500;
			armor = 22f;
			range = 360f;
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
			buildType = ItemTurretBuild::new;
		}};
		starfall = new ItemTurret("starfall") {{
			requirements(Category.turret, ItemStack.with(Items.graphite, 2500, Items.plastanium, 1200, Items.surgeAlloy, 1500, HItems.crystallineCircuit, 800, HItems.uranium, 1200));
			size = 8;
			sync = true;
			health = 42500;
			armor = 110f;
			lightColor = HPal.ancientLightMid;
			clipSize = 6 * 24;
			outlineColor = Pal.darkOutline;
			ammo(HItems.chromium, new CtrlMissileBulletType(4.2f, 2800f, MOD_NAME + "-large-missile") {{
				hitSize *= 10f;
				accel = 0.32f;
				drag /= 2;
				lifetime = 120f;
				homingDelay = 17f;
				frontColor = Color.clear;
				trailColor = lightColor = lightningColor = hitColor = backColor = Pal.techBlue;
				trailLength = 45;
				loopSoundVolume = 0.1f;
				hitSound = Sounds.explosionbig;
				suppressionRange = 600f;
				suppressionDuration = 600f;
				status = HStatusEffects.ultFireBurn;
				splashDamage = 4200f;
				lightningDamage = 1200f;
				scaledSplashDamage = true;
				splashDamageRadius = 200f;
				hitShake = despawnShake = 16f;
				lightning = 6;
				lightningCone = 360f;
				lightningLengthRand = lightningLength = 15;
				fragLifeMin = 0.6f;
				fragLifeMax = 1f;
				fragVelocityMin = 0.4f;
				fragVelocityMax = 0.6f;
				fragBullets = 8;
				fragBullet = HBullets.arc9000frag;
				ammoMultiplier = 1f;
				chargeEffect = HFx.railShoot(Pal.techBlue, 800f, 18, HFx.techBlueChargeBegin.lifetime, 25);
				shootEffect = HFx.instShoot(Pal.techBlue, Pal.techBlue);
				smokeEffect = new Effect(180f, 300f, b -> {
					float intensity = 2f;

					Rand rand = Fx.rand;

					Draw.color(b.color, 0.7f);
					for (int i = 0; i < 4; i++) {
						rand.setSeed(b.id * 2 + i);
						float lenScl = rand.random(0.5f, 1f);
						int j = i;
						b.scaled(b.lifetime * lenScl, e -> {
							Angles.randLenVectors(e.id + j - 1, e.fin(Interp.pow4Out), (int) (2 * intensity), 35f * intensity, e.rotation, 20, (x, y, in, out) -> {
								float fout = e.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
								float rad = fout * ((2f + intensity) * 1.75f);

								Fill.circle(e.x + x, e.y + y, rad);
								Drawf.light(e.x + x, e.y + y, rad * 2.5f, b.color, 0.5f);
							});
						});
					}
				});
				hitEffect = new MultiEffect(HFx.largeTechBlueHit, HFx.blast(Pal.techBlue, 140f), HFx.largeTechBlueHitCircle, HFx.subEffect(150, splashDamageRadius * 0.66f, 13, 34f, Interp.pow2Out, ((i, x, y, rot, fin) -> {
					float fout = Interp.pow2Out.apply(1 - fin);
					float finPow = Interp.pow3Out.apply(fin);
					Tmp.v1.trns(rot, 25 * finPow);
					for (int s : Mathf.signs) {
						Drawf.tri(x, y, 12 * fout, 45 * Mathf.curve(finPow, 0, 0.3f) * HFx.fout(fin, 0.15f), rot + s * 90);
					}
				})));
			}});
			shoot = new ShootBarrel() {{
				barrels = new float[]{
						22, -12, 25,
						-22, -12, -25,
						0, -22, 0
				};
				firstShotDelay = HFx.techBlueChargeBegin.lifetime;
				shots = 3;
				shotDelay = 12f;
			}};
			warmupMaintainTime = 90f;
			shootWarmupSpeed /= 5f;
			minWarmup = 0.9f;
			shootCone = 15f;
			rotateSpeed = 0.425f;
			canOverdrive = false;
			ammoPerShot = 12;
			maxAmmo = 180;
			range = 790f;
			reload = 120;
			unitSort = HUnitSorts.slowest;
			shake = 7;
			recoil = 3;
			shootY = -13.5f;
			shootSound = HSounds.flak;
			consumePowerCond(30f, TurretBuild::isActive);
			enableDrawStatus = false;
			buildType = ItemTurretBuild::new;
		}};
		nukeLauncherPlatform = new ItemTurret("nuke-launcher-platform") {{
			requirements(Category.turret, BuildVisibility.sandboxOnly, ItemStack.with(Items.silicon, 1500, Items.plastanium, 3000, Items.surgeAlloy, 3000, HItems.crystal, 1200, HItems.uranium, 2000, HItems.heavyAlloy, 2000));
			size = 6;
			health = 55000;
			armor = 75f;
			ammo(HItems.uranium, new EndNukeBulletType());
			maxAmmo = 45;
			ammoPerShot = 15;
			unitSort = UnitSorts.strongest;
			reload = 550f;
			range = 1220f;
			fogRadiusMultiplier = 0.667f;
			recoilTime = 180f;
			recoil = 4f;
			shootCone = 360f;
			shootSound = HSounds.desNukeShoot;
			insulated = true;
			absorbLasers = true;
			shootWarmupSpeed = 0.08f;
			warmupMaintainTime = 600f;
			minWarmup = 0.9f;
			shootY = 8;
			targetGround = true;
			targetAir = true;
			canOverdrive = false;
			accurateDelay = false;
			inaccuracy = 0;
			rotateSpeed = 1.25f;
		}};
		solstice = new PowerTurret("solstice") {{
			size = 8;
			destructible = false;
			health = 45000;
			armor = 120;
			lightColor = HPal.ancientLightMid;
			clipSize = 8 * 24;
			outlineColor = Pal.darkOutline;
			requirements(Category.turret, ItemStack.with(Items.plastanium, 2000, Items.surgeAlloy, 2500, Items.phaseFabric, 1600, HItems.crystallineCircuit, 1100, HItems.galliumNitride, 1800, HItems.heavyAlloy, 2200));
			warmupMaintainTime = 90f;
			shootWarmupSpeed /= 5f;
			minWarmup = 0.9f;
			shootCone = 15f;
			rotateSpeed = 0.325f;
			canOverdrive = false;
			shootType = HBullets.heavyArtilleryProjectile;
			ammoPerShot = 12;
			maxAmmo = 180;
			range = 1200;
			reload = 120;
			unitSort = HUnitSorts.slowest;
			shake = 7;
			recoil = 3;
			shootY = -13.5f;
			shootSound = HSounds.flak;
			consumePower(160f);
			enableDrawStatus = false;
			drawer = new DrawTurret() {{
				parts.addAll(new RegionPart("-additional") {{
					drawRegion = false;
					heatColor = Color.red;
					heatProgress = PartProgress.warmup;
					heatLightOpacity = 0.55f;
				}}, new FlipRegionPart("-armor") {{
					outline = mirror = true;
					layerOffset = 0.2f;
					x = 2f;
					moveY = 16f;
					moveX = 8f;
					moveRot = -45;
					moves.add(new PartMove(PartProgress.recoil, 0, -6, 0));
				}}, new FlipRegionPart("-back") {{
					outline = mirror = true;
					layerOffset = 0.3f;
					x = 2f;
					moveY = -2f;
					moveX = 5f;
					moves.add(new PartMove(PartProgress.recoil, 0, -4, 0));
				}}, new FlipRegionPart("-cover") {{
					outline = true;
					layerOffset = 0.3f;
					turretHeatLayer = Layer.turretHeat + layerOffset;
					heatColor = Color.red;
					heatProgress = PartProgress.warmup;
					heatLightOpacity = 0.55f;
				}}, new FlipRegionPart("-barrel") {{
					outline = mirror = true;
					layerOffset = 0.2f;
					x = 2f;
					turretHeatLayer = Layer.turretHeat + layerOffset;
					heatColor = Color.red;
					heatProgress = PartProgress.warmup;
					heatLightOpacity = 0.55f;
					moves.add(new PartMove(PartProgress.recoil, -1.75f, -8f, -2.12f));
					moveY = 6f;
					moveX = 7.75f;
					moveRot = 3.6f;
				}}, new FlipRegionPart("-tail") {{
					outline = mirror = true;
					layerOffset = 0.2f;
					x = 1f;
					moveY = -4f;
					moveX = 2f;
				}}, new DrawArrowSequence() {{
					x = 0f;
					y = 2f;
					arrows = 9;
					color = HPal.ancientLightMid;
					colorTo = Color.red;
					colorToFinScl = 0.12f;
				}}, new HaloPart() {{
					y = -52f;
					layer = Layer.bullet;
					color = HPal.ancient;
					colorTo = HPal.ancientLightMid;
					hollow = true;
					tri = false;
					shapes = 1;
					sides = 16;
					stroke = -1f;
					strokeTo = 3.4f;
					radius = 8f;
					radiusTo = 14.5f;
					haloRadius = 0f;
					haloRotateSpeed = 2f;
				}}, new HaloPart() {{
					y = -52f;
					layer = Layer.bullet;
					color = HPal.ancient;
					colorTo = HPal.ancientLightMid;
					tri = true;
					shapes = 2;
					radius = -1f;
					radiusTo = 4.2f;
					triLength = 6f;
					triLengthTo = 18f;
					haloRadius = 14f;
					haloRadiusTo = 25f;
					haloRotateSpeed = 1.5f;
				}}, new HaloPart() {{
					y = -52f;
					layer = Layer.bullet;
					color = HPal.ancient;
					colorTo = HPal.ancientLightMid;
					tri = true;
					shapes = 2;
					radius = -1f;
					radiusTo = 4.2f;
					triLength = 0f;
					triLengthTo = 4f;
					haloRadius = 14f;
					haloRadiusTo = 25f;
					shapeRotation = 180f;
					haloRotateSpeed = 1.5f;
				}}, new HaloPart() {{
					y = -52f;
					layer = Layer.bullet;
					color = HPal.ancient;
					colorTo = HPal.ancientLightMid;
					tri = true;
					shapes = 2;
					radius = -1;
					radiusTo = 5f;
					triLength = 10f;
					triLengthTo = 24f;
					haloRadius = 15f;
					haloRadiusTo = 28f;
					haloRotateSpeed = -1f;
				}}, new HaloPart() {{
					y = -52f;
					layer = Layer.bullet;
					color = HPal.ancient;
					colorTo = HPal.ancientLightMid;
					tri = true;
					shapes = 2;
					radius = -1f;
					radiusTo = 5f;
					triLength = 0f;
					triLengthTo = 6f;
					haloRadius = 15f;
					haloRadiusTo = 28f;
					shapeRotation = 180f;
					haloRotateSpeed = -1f;
				}});
			}};
			buildType = PowerTurretBuild::new;
		}};
		annihilate = new PowerTurret("annihilate") {{
			recoil = 5f;
			health = 90000;
			armor = 15f;
			shootCone = 15f;
			unitSort = UnitSorts.strongest;
			warmupMaintainTime = 50f;
			coolant = consume(new ConsumeLiquid(HLiquids.crystalFluid, 20f / 60f));
			coolantMultiplier = 1.5f;
			moveWhileCharging = false;
			canOverdrive = false;
			shootWarmupSpeed = 0.035f;
			drawer = new DrawTurret() {{
				parts.add(new RegionPart("-charger") {{
					under = mirror = true;
					layerOffset = -0.002f;
					moveX = 14f;
					moveY = -9f;
					moveRot = -45f;
					y = -4f;
					x = 16f;
					progress = PartProgress.warmup;
				}}, new RegionPart() {{
					drawRegion = false;
					mirror = true;
					heatColor = Color.clear;
					progress = PartProgress.recoil.min(PartProgress.warmup);
					moveY = -10f;
					children.add(new RegionPart("-wing") {{
						under = mirror = true;
						moveRot = 12.5f;
						moveY = 14f;
						moveX = 4f;
						heatColor = Pal.techBlue;
						progress = PartProgress.warmup;
					}});
				}}, new RegionPart("-shooter") {{
					outline = true;
					layerOffset = 0.001f;
					moveY = -12f;
					heatColor = Pal.techBlue;
					progress = PartProgress.warmup.blend(PartProgress.recoil, 0.5f);
				}});
			}};
			shoot.firstShotDelay = HFx.techBlueChargeBegin.lifetime;
			chargeSound = Sounds.none;
			requirements(Category.turret, ItemStack.with(Items.plastanium, 1500, Items.surgeAlloy, 1100, HItems.crystallineCircuit, 400, HItems.chromium, 800));
			shootType = HBullets.arc9000;
			shootCone = 12f;
			rotateSpeed = 0.75f;
			ammoPerShot = 4;
			maxAmmo = 20;
			size = 8;
			health = 28000;
			armor = 15f;
			heatColor = Pal.techBlue;
			consumePower(180f);
			reload = 420f;
			range = 780f;
			trackingRange = range * 1.4f;
			inaccuracy = 0f;
			shootSound = Sounds.laserbig;
			buildType = PowerTurretBuild::new;
		}};
		executor = new PowerTurret("executor") {{
			requirements(Category.turret, ItemStack.with(Items.plastanium, 2200, Items.surgeAlloy, 3500, Items.phaseFabric, 4200, HItems.crystallineElectronicUnit, 1700, HItems.heavyAlloy, 4500));
			size = 16;
			health = 1000000;
			armor = 150;
			rotateSpeed = 0.75f;
			reload = 90f;
			range = 1100f;
			inaccuracy = 1.5f;
			velocityRnd = 0.075f;
			shootSound = HSounds.railGunBlast;
			chargeSound = Sounds.none;
			canOverdrive = false;
			warmupMaintainTime = 50f;
			shootWarmupSpeed = 0.65f;
			shoot = new ShootBarrel() {{
				shots = 2;
				barrels = new float[]{
						-25f, 21f, 0f,
						25f, 21f, 0f
				};
			}};
			shootType = HBullets.executor;
			consumePower(1000f);
			drawer = new DrawTurret() {{
				parts.add(new RegionPart("-backwings") {{
					outline = true;
					layerOffset = -1f;
				}});
			}};
			buildType = PowerTurretBuild::new;
		}};
		heatDeath = new PowerTurret("heat-death") {{
			size = 16;
			health = 800000;
			armor = 150;
			range = 1200;
			heatColor = Pal.techBlue;
			unitSort = HUnitSorts.regionalHPMaximumAll;
			coolant = consume(new ConsumeLiquid(HLiquids.crystalFluid, 1f));
			coolantMultiplier = 0.5f;
			liquidCapacity = 120;
			buildCostMultiplier = 0.8f;
			canOverdrive = false;
			drawer = new DrawTurret() {{
				parts.add(new RegionPart("-side") {{
					under = mirror = true;
					layerOffset = -0.1f;
					moveX = 6f;
					progress = PartProgress.smoothReload.inv().curve(Interp.pow3Out);
				}}, new RegionPart("-side-down") {{
					mirror = true;
					layerOffset = -0.5f;
					moveX = 10f;
					moveY = 45f;
					y = 10f;
					progress = PartProgress.smoothReload.inv().curve(Interp.pow3Out);
				}}, new RegionPart("-side-down") {{
					mirror = true;
					layerOffset = -0.35f;
					moveX = -9f;
					moveY = 7f;
					y = -2f;
					x = 8;
					progress = PartProgress.smoothReload.inv().curve(Interp.pow3Out);
				}}, new RegionPart("-side-down") {{
					under = mirror = true;
					layerOffset = -0.2f;
					moveY = -33f;
					y = -33f;
					x = 14;
					progress = PartProgress.smoothReload.inv().curve(Interp.pow3Out);
				}});
				parts.add(new ArcCharge() {{
					progress = PartProgress.smoothReload.inv().curve(Interp.pow5Out);
					color = Pal.techBlue;
					chargeY = t -> -35f;
					shootY = t -> 90 * curve.apply(1 - t.smoothReload);
				}});
			}};
			inaccuracy = 0;
			ammoPerShot = 40;
			rotateSpeed = 0.25f;

			float chargeCircleFrontRad = 12f;

			shootEffect = new Effect(120f, 2000f, e -> {
				float scl = 1f;
				if (e.data instanceof Float fdata) scl *= fdata;
				Draw.color(heatColor, Color.white, e.fout() * 0.25f);

				float rand = Mathf.randomSeed(e.id, 60f);
				float extend = Mathf.curve(e.fin(Interp.pow10Out), 0.075f, 1f) * scl;
				float rot = e.fout(Interp.pow10In);

				for (int i : Mathf.signs) {
					Drawn.tri(e.x, e.y, chargeCircleFrontRad * 1.2f * e.foutpowdown() * scl, 200 + 500 * extend, e.rotation + (90 + rand) * rot + 90 * i - 45);
				}

				for (int i : Mathf.signs) {
					Drawn.tri(e.x, e.y, chargeCircleFrontRad * 1.2f * e.foutpowdown() * scl, 200 + 500 * extend, e.rotation + (90 + rand) * rot + 90 * i + 45);
				}
			});
			smokeEffect = new Effect(50, e -> {
				Draw.color(heatColor);
				Lines.stroke(e.fout() * 5f);
				Lines.circle(e.x, e.y, e.fin() * 300);
				Lines.stroke(e.fout() * 3f);
				Lines.circle(e.x, e.y, e.fin() * 180);
				Lines.stroke(e.fout() * 3.2f);
				Angles.randLenVectors(e.id, 30, 18 + 80 * e.fin(), (x, y) -> {
					Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 14 + 5);
				});
				Draw.color(Color.white);
				Drawf.light(e.x, e.y, e.fout() * 120, heatColor, 0.7f);
			});
			recoil = 18f;
			shake = 80f;
			shootSound = Sounds.laserblast;
			shootCone = 5f;
			maxAmmo = 80;
			consumePower(800f);
			reload = 1800f;
			shootType = HBullets.arc9000hyper;
			requirements(Category.turret, ItemStack.with(HItems.crystallineCircuit, 2200, HItems.crystallineElectronicUnit, 1100, HItems.heavyAlloy, 5500));
			buildType = PowerTurretBuild::new;
		}};
		//turrets-erekir
		rupture = new ItemTurret("rupture") {{
			requirements(Category.turret, ItemStack.with(Items.graphite, 350, Items.silicon, 250, Items.beryllium, 400, Items.tungsten, 200, Items.oxide, 50));
			ammo(Items.beryllium, new BasicBulletType(12f, 85f) {{
				buildingDamageMultiplier = 0.33f;
				ammoMultiplier = 1f;
				knockback = 1.1f;
				lifetime = 56f / 3f;
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
			}}, Items.tungsten, new BasicBulletType(13f, 95f) {{
				buildingDamageMultiplier = 0.33f;
				ammoMultiplier = 2f;
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
			}}, Items.carbide, new BasicBulletType(16f, 325f / 0.75f) {{
				buildingDamageMultiplier = 0.33f;
				ammoMultiplier = 2f;
				reloadMultiplier = 0.2f;
				knockback = 1.5f;
				lifetime = 20.97f;
				rangeChange = 46f;
				width = 14f;
				height = 22.5f;
				pierce = pierceBuilding = true;
				pierceCap = 5;
				hitColor = backColor = trailColor = HPal.carbideAmmoBack;
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
				fragBullet = new BasicBulletType(8.7f, 227f) {{
					width = 10f;
					height = 15f;
					lifetime = 8.55f;
					pierce = pierceBuilding = true;
					pierceCap = 2;
					hitColor = backColor = trailColor = HPal.carbideAmmoBack;
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
			shootSound = HSounds.shootAltHeavy;
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
			buildType = ItemTurretBuild::new;
		}};
		rift = new ItemTurret("rift") {{
			requirements(Category.turret, ItemStack.with(Items.graphite, 920, Items.silicon, 500, Items.surgeAlloy, 800, Items.tungsten, 1200, Items.carbide, 480));
			health = 8830;
			size = 5;
			reload = 100f;
			range = 550f;
			heatRequirement = 60f;
			warmupMaintainTime = 60f;
			shootWarmupSpeed = 0.02f;
			minWarmup = 0.76f;
			outlineColor = Pal.darkOutline;
			drawer = new DrawTurret("reinforced-") {{
				parts.addAll(new RegionPart("-blade") {{
					mirror = true;
					under = false;
					x = 0f;
					heatProgress = PartProgress.recoil;
					heatColor = Pal.slagOrange;
					moveX = 4f;
					children.add(new RegionPart("-mid") {{
						mirror = true;
						under = true;
						x = 3f;
						heatProgress = PartProgress.recoil;
						heatColor = Pal.slagOrange;
						moves.add(new PartMove() {{
							progress = PartProgress.recoil;
							y = -3;
						}});
						moveX = -3f;
						moveY = 9.25f;
					}});
				}}, new RegionPart("-top") {{
					mirror = false;
					heatProgress = PartProgress.warmup;
					heatColor = Pal.slagOrange;
					progress = PartProgress.recoil;
					moveY = -2;
				}}, new ShapePart() {{
					progress = PartProgress.warmup;
					y = -17f;
					color = Pal.slagOrange;
					stroke = 0f;
					strokeTo = 1.6f;
					circle = true;
					hollow = true;
					radius = 0f;
					radiusTo = 10f;
					layer = 110f;
				}}, new HaloPart() {{
					shapeRotation = 45;
					progress = PartProgress.warmup;
					shapes = 1;
					sides = 3;
					x = 10f;
					y = -27f;
					color = Pal.slagOrange;
					layer = 110f;
					tri = true;
					radius = 0f;
					radiusTo = 5f;
					triLength = 0f;
					triLengthTo = 12f;
					haloRadius = 0f;
					haloRadiusTo = 0f;
					haloRotateSpeed = 0f;
				}}, new HaloPart() {{
					shapeRotation = -135;
					progress = PartProgress.warmup;
					shapes = 1;
					sides = 3;
					x = 10f;
					y = -27f;
					color = Pal.slagOrange;
					layer = 110f;
					tri = true;
					radius = 0f;
					radiusTo = 5f;
					triLength = 0f;
					triLengthTo = 28f;
					haloRadius = 0f;
					haloRadiusTo = 0f;
					haloRotateSpeed = 0f;
				}}, new HaloPart() {{
					shapeRotation = 135;
					progress = PartProgress.warmup;
					shapes = 1;
					sides = 3;
					x = -10f;
					y = -27f;
					color = Pal.slagOrange;
					layer = 110f;
					tri = true;
					radius = 0f;
					radiusTo = 5;
					triLength = 0f;
					triLengthTo = 28f;
					haloRadius = 0f;
					haloRadiusTo = 0f;
					haloRotateSpeed = 0f;
				}}, new ShapePart() {{
					progress = PartProgress.warmup;
					y = -23f;
					color = Pal.slagOrange;
					stroke = 0f;
					strokeTo = 2f;
					circle = true;
					hollow = true;
					radius = 0f;
					radiusTo = 16f;
					layer = 110f;
				}}, new HaloPart() {{
					progress = PartProgress.warmup;
					sides = 3;
					shapes = 3;
					y = -23f;
					color = Pal.slagOrange;
					layer = 110f;
					tri = true;
					radius = 0f;
					radiusTo = 5f;
					triLength = 0f;
					triLengthTo = 8f;
					haloRadius = 0f;
					haloRadiusTo = 21f;
					haloRotation = 0f;
					haloRotateSpeed = -0.9f;
				}}, new HaloPart() {{
					shapeRotation = 180;
					progress = PartProgress.warmup;
					sides = 3;
					shapes = 3;
					y = -23;
					color = Pal.slagOrange;
					layer = 110;
					tri = true;
					radius = 0f;
					radiusTo = 5f;
					triLength = 0f;
					triLengthTo = 5f;
					haloRadius = 0f;
					haloRadiusTo = 21f;
					haloRotation = 0f;
					haloRotateSpeed = -0.9f;
				}}, new HaloPart() {{
					progress = PartProgress.warmup;
					shapes = 1;
					sides = 3;
					x = 0f;
					y = -35f;
					color = Pal.slagOrange;
					layer = 110f;
					tri = true;
					radius = 0f;
					radiusTo = 6f;
					triLength = 0f;
					triLengthTo = 15f;
					haloRadius = 0f;
					haloRadiusTo = 0f;
					haloRotateSpeed = 0f;
				}}, new HaloPart() {{
					shapeRotation = -180;
					progress = PartProgress.warmup;
					shapes = 1;
					sides = 3;
					x = 0f;
					y = -35f;
					color = Pal.slagOrange;
					layer = 110f;
					tri = true;
					radius = 0f;
					radiusTo = 6f;
					triLength = 0f;
					triLengthTo = 33f;
					haloRadius = 0f;
					haloRadiusTo = 0f;
					haloRotateSpeed = 0f;
				}});
			}};
			shootSound = Sounds.shootSmite;
			shootY = 4f;
			ammoUseEffect = Fx.none;
			shootCone = 8f;
			rotateSpeed = 1.12f;
			shake = 6f;
			maxAmmo = 30;
			ammoPerShot = 10;
			heatColor = Pal.slagOrange;
			cooldownTime = 100f;
			recoil = 5f;
			recoilTime = 45f;
			ammo(Items.tungsten, new BasicBulletType(15f, 406f) {{
				inaccuracy = 5;
				splashDamageRadius = 38;
				splashDamage = 126.3f;
				buildingDamageMultiplier = 0.33f;
				speed = 15f;
				lifetime = 36f;
				width = 20f;
				height = 33f;
				pierce = true;
				pierceCap = 3;
				pierceArmor = true;
				status = StatusEffects.slow;
				statusDuration = 33f;
				backColor = Pal.slagOrange;
				frontColor = Pal.slagOrange;
				trailColor = Pal.slagOrange;
				trailLength = 13;
				trailWidth = 4.5f;
				trailChance = 1f;
				trailInterval = 8f;
				trailEffect = HFx.polyCloud(Pal.slagOrange, 30f, 8f, 18f, 4);//trail
				hitSound = Sounds.shotgun;
				hitShake = 3f;
				hitEffect = HFx.hitSparkLarge;//hit
				shootEffect = HFx.square(Pal.slagOrange, 45f, 5, 38, 4);//shoot
				despawnEffect = Fx.none;
				smokeEffect = Fx.shootBigSmoke;//smoke
				fragBullets = 18;
				fragRandomSpread = 60f;
				fragBullet = new LiquidBulletType(Liquids.slag) {{
					damage = 46f;
					puddleSize = 16f;
					orbSize = 5f;
					knockback = 1.75f;
					statusDuration = 600f;
					status = StatusEffects.melting;
					pierceArmor = true;
					pierce = true;
					pierceCap = 2;
					speed = 8f;
					lifetime = 20f;
					hitEffect = Fx.hitBulletBig;//hit
				}};
			}}, Items.carbide, new BasicBulletType(28.4f, 2160f, "missile-large") {{
				rangeChange = 160;
				buildingDamageMultiplier = 0.5f;
				lifetime = 25f;
				width = 20f;
				height = 34f;
				pierce = true;
				pierceArmor = true;
				status = HStatusEffects.breached;
				backColor = HPal.energyYellow;
				frontColor = Pal.slagOrange;
				trailColor = HPal.energyYellow;
				shrinkY = 0f;
				trailLength = 13;
				trailWidth = 5f;
				trailChance = 0f;
				trailInterval = 0.2f;
				trailEffect = HFx.polyCloud(Pal.slagOrange, 30f, 8f, 18f, 4);//trail
				hitSound = Sounds.shotgun;
				hitShake = 5f;
				hitEffect = HFx.hitSparkLarge;//hit
				despawnEffect = Fx.none;
				shootEffect = HFx.square(Pal.slagOrange, 45f, 5, 38, 4);//shoot
				smokeEffect = Fx.shootBigSmoke;//smoke
			}});
			buildType = ItemTurretBuild::new;
			squareSprite = false;
		}};
		//campaign
		largeLaunchPad = new LaunchPad("large-launch-pad") {{
			requirements(Category.effect, BuildVisibility.notLegacyLaunchPadOnly, ItemStack.with(HItems.uranium, 200, HItems.crystal, 100, Items.metaglass, 100, Items.silicon, 250, Items.surgeAlloy, 120, Items.phaseFabric, 100));
			health = 5200;
			armor = 25f;
			size = 5;
			itemCapacity = 750;
			launchTime = 1800f;
			hasPower = true;
			lightColor = HPal.energyYellow.cpy().a(0.65f);
			acceptMultipleItems = true;
			consumePower(18f);
			buildCostMultiplier = 0.8f;
		}};
		largeLandingPad = new LandingPad("large-landing-pad") {{
			requirements(Category.effect, BuildVisibility.notLegacyLaunchPadOnly, ItemStack.with(HItems.uranium, 100, HItems.crystal, 120, Items.metaglass, 180, Items.plastanium, 250, Items.silicon, 300, Items.phaseFabric, 80));
			size = 5;
			itemCapacity = 750;
			liquidCapacity = 360f;
			consumeLiquidAmount = 180f;
			consumeLiquid = Liquids.cryofluid;
			buildCostMultiplier = 0.7f;
		}};
		//sandbox
		unitIniter = new UnitIniter("unit-initer");
		reinforcedItemSource = new ItemSource("reinforced-item-source") {{
			requirements(Category.distribution, BuildVisibility.sandboxOnly, ItemStack.empty);
			health = 1000;
			armor = 10f;
			itemsPerSecond = 1000;
			hideDetails = false;
			buildType = ItemSourceBuild::new;
		}};
		reinforcedLiquidSource = new LiquidSource("reinforced-liquid-source") {{
			requirements(Category.liquid, BuildVisibility.sandboxOnly, ItemStack.empty);
			health = 1000;
			armor = 10f;
			buildType = LiquidSourceBuild::new;
		}};
		reinforcedPowerSource = new PowerSource("reinforced-power-source") {{
			requirements(Category.power, BuildVisibility.sandboxOnly, ItemStack.empty);
			health = 1000;
			armor = 10f;
			powerProduction = 10000000f / 60f;
			buildType = PowerSourceBuild::new;
		}};
		reinforcedPayloadSource = new PayloadSource2("reinforced-payload-source") {{
			requirements(Category.units, BuildVisibility.sandboxOnly, ItemStack.empty);
			size = 5;
			health = 1000;
			armor = 10f;
			placeableLiquid = true;
			floating = true;
		}};
		adaptiveSource = new AdaptiveSource("adaptive-source") {{
			requirements(Category.distribution, BuildVisibility.sandboxOnly, ItemStack.empty);
			health = 1000;
			armor = 10f;
			liquidCapacity = 10000f;
			itemCapacity = 1000;
		}};
		randomSource = new RandomSource("random-source") {{
			requirements(Category.distribution, BuildVisibility.sandboxOnly, ItemStack.empty);
			health = 1000;
			armor = 10f;
			liquidCapacity = 10000f;
			itemCapacity = 1000;
			itemsPerSecond = 2000;
		}};
		staticDrill = new Drill("static-drill") {{
			requirements(Category.production, BuildVisibility.sandboxOnly, ItemStack.empty);
			health = 1000;
			armor = 10f;
			drillTime = 1f;
			tier = 114514;
			itemCapacity = 1000;
			drillEffect = Fx.none;
			updateEffect = Fx.none;
			rotateSpeed = 8;
			hardnessDrillMultiplier = 0.01f;
			buildType = DrillBuild::new;
		}};
		omniNode = new NodeBridge("omni-node") {{
			requirements(Category.distribution, BuildVisibility.sandboxOnly, ItemStack.empty);
			health = 1000;
			armor = 10f;
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
		configurableMendProjector = new ConfigurableMendProjector("infini-mender") {{
			requirements(Category.effect, BuildVisibility.sandboxOnly, ItemStack.empty);
			health = 1000;
			range = 80f;
			reload = 30f;
			healPercent = 2f;
		}};
		configurableOverdriveProjector = new ConfigurableOverdriveProjector("infini-overdrive") {{
			requirements(Category.effect, BuildVisibility.sandboxOnly, ItemStack.empty);
			health = 1000;
		}};
		teamChanger = new CoreBlock("team-changer") {{
			requirements(Category.effect, BuildVisibility.sandboxOnly, ItemStack.empty);
			size = 3;
			health = 1000;
			armor = 10f;
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
					ButtonGroup<Button> bg = new ButtonGroup<>();
					Table cont = new Table();
					cont.defaults().size(55);
					for (Team bt : Utils.baseTeams) {
						ImageButton button = cont.button(((TextureRegionDrawable) Tex.whiteui).tint(bt.color), Styles.clearTogglei, 35, Constant.RUNNABLE_NOTHING).group(bg).get();
						button.changed(() -> {
							if (button.isChecked()) {
								if (Vars.player.team() == team) {
									configure(bt.id);
								} else {
									deselect();
								}
							}
						});
						button.update(() -> button.setChecked(team == bt));
					}
					ScrollPane pane = new ScrollPane(cont, Styles.smallPane);
					pane.setScrollingDisabled(true, false);
					pane.setOverscroll(false, false);
					table.add(pane).maxHeight(Scl.scl(40 * 2)).left();
					table.row();
				}

				@Override
				public void configured(Unit builder, Object value) {
					if (builder != null && builder.isPlayer() && value instanceof Number number) {
						Team to = Team.get(number.intValue());
						builder.team = to;
						builder.getPlayer().team(to);

						onRemoved();
						team = to;
						onProximityUpdate();
					}
				}
			};
			unitType = UnitTypes.evoke;
		}
			@Override
			public boolean canBreak(Tile tile) {
				return Vars.state.teams.cores(tile.team()).size > 1;
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
			public void beforePlaceBegan(Tile tile, Block previous) {}

			@Override
			public void drawPlace(int x, int y, int rotation, boolean valid) {}

			@Override
			protected TextureRegion[] icons() {
				return teamRegion.found() ? new TextureRegion[]{region, teamRegions[Team.sharded.id]} : new TextureRegion[]{region};
			}
		};
		barrierProjector = new BaseShield("barrier-projector") {{
			requirements(Category.effect, BuildVisibility.sandboxOnly, ItemStack.empty);
			health = 1000;
			armor = 10f;
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
		}
			@Override
			protected TextureRegion[] icons() {
				return new TextureRegion[]{region};
			}
		};
		entityRemove = new ForceProjector("entity-remove") {{
			requirements(Category.effect, BuildVisibility.sandboxOnly, ItemStack.empty);
			health = 1000;
			armor = 10f;
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
						Fx.reactorsmoke.at(x + Mathf.range(Vars.tilesize / 2), y + Mathf.range(Vars.tilesize / 2));
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
							if (trait.team != team && Intersector.isInsideHexagon(x, y, realRadius() * 2f, trait.x, trait.y)) {
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
			itemConsumer = consumeItem(Items.phaseFabric).optional(true, true);
		}
			@Override
			protected TextureRegion[] icons() {
				return teamRegion.found() ? new TextureRegion[]{region, teamRegions[Team.sharded.id]} : new TextureRegion[]{region};
			}
		};
		invincibleWall = new IndestructibleWall("invincible-wall") {{
			requirements(Category.defense, BuildVisibility.sandboxOnly, ItemStack.empty);
			health = 1000;
			armor = 10f;
			size = 1;
			absorbLasers = insulated = true;
			unlocked = false;
		}};
		invincibleWallLarge = new IndestructibleWall("invincible-wall-large") {{
			requirements(Category.defense, BuildVisibility.sandboxOnly, ItemStack.empty);
			health = 4000;
			armor = 10f;
			size = 2;
			absorbLasers = insulated = true;
			unlocked = false;
		}};
		invincibleWallHuge = new IndestructibleWall("invincible-wall-huge") {{
			requirements(Category.defense, BuildVisibility.sandboxOnly, ItemStack.empty);
			health = 9000;
			armor = 10f;
			size = 3;
			absorbLasers = insulated = true;
			unlocked = false;
		}};
		invincibleWallGigantic = new IndestructibleWall("invincible-wall-gigantic") {{
			requirements(Category.defense, BuildVisibility.sandboxOnly, ItemStack.empty);
			health = 16000;
			armor = 10f;
			size = 4;
			absorbLasers = insulated = true;
			unlocked = false;
		}};
		dpsWall = new DPSWall("dps-wall") {{
			requirements(Category.defense, BuildVisibility.sandboxOnly, ItemStack.empty);
			health = 1000;
		}};
		dpsWallLarge = new DPSWall("dps-wall-large") {{
			requirements(Category.defense, BuildVisibility.sandboxOnly, ItemStack.empty);
			size = 2;
			health = 4000;
		}};
		dpsWallHuge = new DPSWall("dps-wall-huge") {{
			requirements(Category.defense, BuildVisibility.sandboxOnly, ItemStack.empty);
			size = 3;
			health = 9000;
		}};
		dpsWallGigantic = new DPSWall("dps-wall-gigantic") {{
			requirements(Category.defense, BuildVisibility.sandboxOnly, ItemStack.empty);
			size = 4;
			health = 16000;
		}};
		mustDieTurret = new PlatformTurret("must-die-turret") {{
			requirements(Category.turret, BuildVisibility.sandboxOnly, ItemStack.empty);
			health = 1000;
			armor = 10f;
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
			requirements(Category.turret, BuildVisibility.sandboxOnly, ItemStack.empty);
			size = 2;
			health = 1000;
			armor = 10f;
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
			requirements(Category.turret, BuildVisibility.sandboxOnly, ItemStack.empty);
			size = 2;
			health = 1000;
			armor = 10f;
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
					Drawf.tri(e.x, e.y, 4f * e.fout(), 29f, e.rotation + 90f * i);
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
		nextWave = new NextWave("next-wave") {{
			requirements(Category.effect, BuildVisibility.sandboxOnly, ItemStack.empty);
			size = 2;
			health = 1000;
			armor = 10f;
		}};
	}
}
