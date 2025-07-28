package heavyindustry.gen;

import arc.func.Prov;
import arc.struct.ObjectIntMap;
import arc.util.Strings;
import mindustry.gen.EntityMapping;
import mindustry.gen.Entityc;

/**
 * Each Entity class requires an independent {@link Entityc#classId()} in order to be saved properly in the map.
 * <p>This class provides a method to register the Entity class of a module into {@link EntityMapping}.
 *
 * @since 1.0.6
 */
public final class Entitys {
	private static final ObjectIntMap<Class<? extends Entityc>> ids = new ObjectIntMap<>();

	private static volatile int last = 0;

	/** Don't let anyone instantiate this class. */
	private Entitys() {}

	public static <T extends Entityc> void register(Class<T> type, Prov<T> prov) {
		synchronized (Entitys.class) {
			if (ids.containsKey(type) || EntityMapping.nameMap.containsKey(type.getSimpleName())) return;

			for (; last < EntityMapping.idMap.length; last++) {
				if (EntityMapping.idMap[last] == null) {
					EntityMapping.idMap[last] = prov;
					ids.put(type, last);

					EntityMapping.nameMap.put(type.getSimpleName(), prov);
					EntityMapping.nameMap.put(Strings.camelToKebab(type.getSimpleName()), prov);

					return;
				}
			}

			throw new IllegalStateException("In case you used up all 256 class ids; use the same code for ~200 units you idiot.");
		}
	}

	public static <T extends Entityc> void register(String name, Class<T> type, Prov<T> prov) {
		register(type, prov);
		EntityMapping.nameMap.put(name, prov);

		int id = getId(type);
		if (id != -1) {
			EntityMapping.customIdMap.put(id, name);
		}
	}

	public static int getId(Class<? extends Entityc> type) {
		return ids.get(type, -1);
	}

	public static void load() {
		register("BaseUnit", BaseUnit.class, BaseUnit::new);
		register("BaseMechUnit", BaseMechUnit.class, BaseMechUnit::new);
		register("BaseLegsUnit", BaseLegsUnit.class, BaseLegsUnit::new);
		register("BasePayloadUnit", BasePayloadUnit.class, BasePayloadUnit::new);
		register("BaseTankUnit", BaseTankUnit.class, BaseTankUnit::new);
		register("BaseElevationMoveUnit", BaseElevationMoveUnit.class, BaseElevationMoveUnit::new);
		register("BaseBuildingTetherPayloadUnit", BaseBuildingTetherPayloadUnit.class, BaseBuildingTetherPayloadUnit::new);
		register("BaseUnitWaterMove", BaseUnitWaterMove.class, BaseUnitWaterMove::new);
		register("BaseTimedKillUnit", BaseTimedKillUnit.class, BaseTimedKillUnit::new);
		register("TrailTimedKillUnit", TrailTimedKillUnit.class, TrailTimedKillUnit::new);
		register("PayloadLegsUnit", PayloadLegsUnit.class, PayloadLegsUnit::new);
		register("BuildingTetherPayloadLegsUnit", BuildingTetherPayloadLegsUnit.class, BuildingTetherPayloadLegsUnit::new);
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
		register("UltFire", UltFire.class, UltFire::new);
		register("Spawner", Spawner.class, Spawner::new);
		register("VapourizeEffectState", VapourizeEffectState.class, VapourizeEffectState::new);
	}
}
