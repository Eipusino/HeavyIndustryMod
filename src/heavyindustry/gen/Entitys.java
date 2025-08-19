package heavyindustry.gen;

import arc.func.Func;
import arc.func.Prov;
import arc.struct.ObjectIntMap;
import arc.struct.ObjectMap;
import arc.util.Structs;
import mindustry.ctype.Content;
import mindustry.gen.EntityMapping;
import mindustry.gen.Entityc;

/**
 * Each Entity class requires an independent {@link Entityc#classId()} in order to be saved properly in the map.
 * <p>This class provides a method to register the Entity class of a module into {@link EntityMapping}.
 *
 * @since 1.0.6
 */
public final class Entitys {
	private static final ObjectIntMap<Class<? extends Entityc>> classIdMap = new ObjectIntMap<>();
	private static final ObjectMap<String, Prov<? extends Entityc>> needMap = new ObjectMap<>();

	/** Don't let anyone instantiate this class. */
	private Entitys() {}

	public static <T extends Entityc> void register(String name, Class<T> type, Prov<T> prov) {
		needMap.put(name, prov);
		classIdMap.put(type, EntityMapping.register(name, prov));
	}

	public static <T, E extends Entityc> T content(String name, Class<E> type, Func<String, ? extends T> create) {
		T get = create.get(name);
		String suffix = get instanceof Content content && content.minfo.mod != null ? content.minfo.mod.name + "-" : "";

		if (type.getName().startsWith("mindustry.gen.")) {
			EntityMapping.nameMap.put(suffix + name, Structs.find(EntityMapping.idMap, p -> p != null && p.get().getClass().equals(type)));
		} else {
			EntityMapping.nameMap.put(suffix + name, needMap.get(type.getSimpleName()));
		}
		return get;
	}

	public static int getId(Class<? extends Entityc> type) {
		return classIdMap.get(type, -1);
	}

	public static void load() {
		register("BaseUnit", BaseUnit.class, BaseUnit::new);
		register("BaseMechUnit", BaseMechUnit.class, BaseMechUnit::new);
		register("BaseLegsUnit", BaseLegsUnit.class, BaseLegsUnit::new);
		register("BasePayloadUnit", BasePayloadUnit.class, BasePayloadUnit::new);
		register("BaseTankUnit", BaseTankUnit.class, BaseTankUnit::new);
		register("BaseElevationMoveUnit", BaseElevationMoveUnit.class, BaseElevationMoveUnit::new);
		register("BaseBuildingTetherPayloadUnit", BaseBuildingTetherPayloadUnit.class, BaseBuildingTetherPayloadUnit::new);
		register("BaseBuildingTetherUnit", BaseBuildingTetherUnit.class, BaseBuildingTetherUnit::new);
		register("BaseUnitWaterMove", BaseUnitWaterMove.class, BaseUnitWaterMove::new);
		register("BaseTimedKillUnit", BaseTimedKillUnit.class, BaseTimedKillUnit::new);
		register("PayloadLegsUnit", PayloadLegsUnit.class, PayloadLegsUnit::new);
		register("BaseBuildingTetherLegsUnit", BaseBuildingTetherLegsUnit.class, BaseBuildingTetherLegsUnit::new);
		register("BaseCrawlUnit", BaseCrawlUnit.class, BaseCrawlUnit::new);
		register("ChainedChainMechUnit", ChainedChainMechUnit.class, ChainedChainMechUnit::new);
		register("TractorBeamUnit", TractorBeamUnit.class, TractorBeamUnit::new);
		register("OrnitopterUnit", OrnitopterUnit.class, OrnitopterUnit::new);
		register("CopterUnit", CopterUnit.class, CopterUnit::new);
		register("FloatMechCoreUnit", FloatMechCoreUnit.class, FloatMechCoreUnit::new);
		register("SentryUnit", SentryUnit.class, SentryUnit::new);
		register("SwordUnit", SwordUnit.class, SwordUnit::new);
		register("EnergyUnit", EnergyUnit.class, EnergyUnit::new);
		register("PesterUnit", PesterUnit.class, PesterUnit::new);
		register("NucleoidUnit", NucleoidUnit.class, NucleoidUnit::new);
		register("DPSMechUnit", DPSMechUnit.class, DPSMechUnit::new);
		register("InvincibleShipUnit", InvincibleShipUnit.class, InvincibleShipUnit::new);
		register("UltFire", UltFire.class, UltFire::new);
		register("Spawner", Spawner.class, Spawner::new);
		register("VapourizeEffectState", VapourizeEffectState.class, VapourizeEffectState::new);
	}
}
