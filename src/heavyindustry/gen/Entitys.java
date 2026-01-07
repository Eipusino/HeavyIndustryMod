package heavyindustry.gen;

import arc.func.Func;
import arc.func.Prov;
import arc.util.Structs;
import heavyindustry.entities.effect.VapourizeEffect.VapourizeEffectState;
import heavyindustry.util.CollectionObjectMap;
import heavyindustry.util.ObjectIntMap2;
import mindustry.ctype.Content;
import mindustry.gen.EntityMapping;
import mindustry.gen.Entityc;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.ApiStatus.Obsolete;

/**
 * Each Entity class requires an independent {@link Entityc#classId()} in order to be saved properly in the map.
 * <p>This class provides a method to register the Entity class of a module into {@link EntityMapping}.
 *
 * @since 1.0.6
 */
public final class Entitys {
	static final ObjectIntMap2<Class<? extends Entityc>> classIdMap = new ObjectIntMap2<>(Class.class);
	static final CollectionObjectMap<String, Prov<? extends Entityc>> needIdMap = new CollectionObjectMap<>(String.class, Prov.class);

	private static boolean registered = false;

	/** Don't let anyone instantiate this class. */
	private Entitys() {}

	public static Prov<? extends Entityc> get(Class<? extends Entityc> type) {
		return get(type.getSimpleName());
	}

	public static Prov<? extends Entityc> get(String name) {
		return needIdMap.get(name);
	}

	@Obsolete(since = "1.0.8")
	public static <T extends Entityc> void register(String name, Class<T> type, Prov<T> prov) {
		needIdMap.put(name, prov);
		classIdMap.put(type, EntityMapping.register(name, prov));
	}

	public static <T extends Entityc> void register(Class<T> type, Prov<T> prov) {
		String name = type.getSimpleName();

		needIdMap.put(name, prov);
		classIdMap.put(type, EntityMapping.register(name, prov));
	}

	public static <T extends Content> T content(String name, Class<? extends Entityc> type, Func<String, ? extends T> create) {
		T content = create.get(name);

		String suffix = content.minfo.mod == null ? "" : content.minfo.mod.name + "-";
		if (type.getName().startsWith("mindustry.gen.")) {
			EntityMapping.nameMap.put(suffix + name, Structs.find(EntityMapping.idMap, p -> p != null && p.get().getClass() == type));
		} else {
			EntityMapping.nameMap.put(suffix + name, needIdMap.get(type.getSimpleName()));
		}
		return content;
	}

	public static int getId(Class<? extends Entityc> type) {
		return classIdMap.get(type, -1);
	}

	@Internal
	public static void load() {
		if (registered) return;

		registered = true;

		register(Unit2.class, Unit2::new);
		register(MechUnit2.class, MechUnit2::new);
		register(LegsUnit2.class, LegsUnit2::new);
		register(PayloadUnit2.class, PayloadUnit2::new);
		register(TankUnit2.class, TankUnit2::new);
		register(ElevationMoveUnit2.class, ElevationMoveUnit2::new);
		register(BuildingTetherPayloadUnit2.class, BuildingTetherPayloadUnit2::new);
		register(BuildingTetherUnit2.class, BuildingTetherUnit2::new);
		register(UnitWaterMove2.class, UnitWaterMove2::new);
		register(TimedKillUnit2.class, TimedKillUnit2::new);
		register(PayloadLegsUnit.class, PayloadLegsUnit::new);
		register(BuildingTetherLegsUnit2.class, BuildingTetherLegsUnit2::new);
		register(CrawlUnit2.class, CrawlUnit2::new);
		register(DamageAbsorbMechUnit.class, DamageAbsorbMechUnit::new);
		register(TractorBeamUnit.class, TractorBeamUnit::new);
		register(OrnitopterUnit.class, OrnitopterUnit::new);
		register(CopterUnit.class, CopterUnit::new);
		register(SentryUnit.class, SentryUnit::new);
		register(EnergyUnit.class, EnergyUnit::new);
		register(PesterUnit.class, PesterUnit::new);
		register(NucleoidUnit.class, NucleoidUnit::new);
		register(AirSeaAmphibiousUnit.class, AirSeaAmphibiousUnit::new);
		register(DPSMechUnit.class, DPSMechUnit::new);
		register(InvincibleShipUnit.class, InvincibleShipUnit::new);
		register(UltFire.class, UltFire::new);
		register(UltPuddle.class, UltPuddle::new);
		register(DiffBullet.class, DiffBullet::new);
		register(BlackHoleBullet.class, BlackHoleBullet::new);
		register(Spawner.class, Spawner::new);
		register(RenderGroupEntity.class, RenderGroupEntity::new);
		register(ShrapnelEntity.class, ShrapnelEntity::new);
		register(VapourizeEffectState.class, VapourizeEffectState::new);
	}
}
