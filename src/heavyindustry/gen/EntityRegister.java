package heavyindustry.gen;

import arc.func.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.type.*;

/**
 * Each Entity class requires an independent {@link Entityc#classId()} in order to be saved properly in the map.
 * <p>This class provides a method to register the Entity class of a module into {@link EntityMapping}.
 *
 * @since 1.0.6
 */
public final class EntityRegister {
	private static final ObjectIntMap<Class<? extends Entityc>> ids = new ObjectIntMap<>();

	private static volatile int last = 0;

	/** Don't let anyone instantiate this class. */
	private EntityRegister() {}

	public static <T extends Entityc> void put(Class<T> type, Prov<T> prov) {
		synchronized (EntityRegister.class) {
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

	public static <T extends Entityc> void put(String name, Class<T> type, Prov<T> prov) {
		put(type, prov);
		EntityMapping.nameMap.put(name, prov);

		int id = getId(type);
		if (id != -1) {
			EntityMapping.customIdMap.put(id, name);
		}
	}

	/** @deprecated Why not use {@code constructor = UnitEntity::create} directly? */
	@Deprecated
	public static <T extends Unit> void put(UnitType unit, Class<T> type, Prov<T> prov) {
		put(unit.name, type, prov);
		unit.constructor = prov;
	}

	public static int getId(Class<? extends Entityc> type) {
		return ids.get(type, -1);
	}

	public static void load() {
		put("Unitf", Unitf.class, Unitf::new);
		put("MechUnitf", MechUnitf.class, MechUnitf::new);
		put("LegsUnitf", LegsUnitf.class, LegsUnitf::new);
		put("PayloadUnitf", PayloadUnitf.class, PayloadUnitf::new);
		put("TankUnitf", TankUnitf.class, TankUnitf::new);
		put("BuildingTetherPayloadUnitf", BuildingTetherPayloadUnitf.class, BuildingTetherPayloadUnitf::new);
		put("UnitWaterMovef", UnitWaterMovef.class, UnitWaterMovef::new);
		put("TimedKillUnitf", TimedKillUnitf.class, TimedKillUnitf::new);
		put("PayloadLegsUnit", PayloadLegsUnit.class, PayloadLegsUnit::new);
		put("BuildingTetherPayloadLegsUnit", BuildingTetherPayloadLegsUnit.class, BuildingTetherPayloadLegsUnit::new);
		put("CrawlUnitf", CrawlUnitf.class, CrawlUnitf::new);
		put("TractorBeamUnit", TractorBeamUnit.class, TractorBeamUnit::new);
		put("OrnitopterUnit", OrnitopterUnit.class, OrnitopterUnit::new);
		put("CopterUnit", CopterUnit.class, CopterUnit::new);
		put("SentryUnit", SentryUnit.class, SentryUnit::new);
		put("SwordUnit", SwordUnit.class, SwordUnit::new);
		put("EnergyUnit", EnergyUnit.class, EnergyUnit::new);
		put("PesterUnit", PesterUnit.class, PesterUnit::new);
		put("NucleoidUnit", NucleoidUnit.class, NucleoidUnit::new);
		put("UltFire", UltFire.class, UltFire::new);
		put("Spawner", Spawner.class, Spawner::new);
		put("VapourizeEffectState", VapourizeEffectState.class, VapourizeEffectState::new);
	}
}
