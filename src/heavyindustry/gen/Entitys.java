package heavyindustry.gen;

import arc.func.Func;
import arc.func.Prov;
import arc.util.Structs;
import heavyindustry.entities.effect.VapourizeEffect.VapourizeEffectState;
import heavyindustry.util.ObjectIntMapf;
import heavyindustry.util.CollectionObjectMap;
import mindustry.gen.EntityMapping;
import mindustry.gen.Entityc;
import mindustry.type.UnitType;

import java.util.Map;

/**
 * Each Entity class requires an independent {@link Entityc#classId()} in order to be saved properly in the map.
 * <p>This class provides a method to register the Entity class of a module into {@link EntityMapping}.
 *
 * @since 1.0.6
 */
public final class Entitys {
	private static final ObjectIntMapf<Class<? extends Entityc>> classIdMap = new ObjectIntMapf<>(Class.class);
	private static final Map<String, Prov<? extends Entityc>> needIdMap = new CollectionObjectMap<>(String.class, Prov.class);

	/** Don't let anyone instantiate this class. */
	private Entitys() {}

	public static Prov<? extends Entityc> get(Class<? extends Entityc> type) {
		return get(type.getSimpleName());
	}

	public static Prov<? extends Entityc> get(String name) {
		return needIdMap.get(name);
	}

	@Deprecated
	public static <T extends Entityc> void register(String name, Class<T> type, Prov<T> prov) {
		needIdMap.put(name, prov);
		classIdMap.put(type, EntityMapping.register(name, prov));
	}

	public static <T extends Entityc> void register(Class<T> type, Prov<T> prov) {
		String name = type.getSimpleName();

		needIdMap.put(name, prov);
		classIdMap.put(type, EntityMapping.register(name, prov));
	}

	public static <T extends UnitType, E extends Entityc> T content(String name, Class<E> type, Func<String, ? extends T> create) {
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

	public static void load() {
		register(BaseUnit.class, BaseUnit::new);
		register(BaseMechUnit.class, BaseMechUnit::new);
		register(BaseLegsUnit.class, BaseLegsUnit::new);
		register(BasePayloadUnit.class, BasePayloadUnit::new);
		register(BaseTankUnit.class, BaseTankUnit::new);
		register(BaseElevationMoveUnit.class, BaseElevationMoveUnit::new);
		register(BaseBuildingTetherPayloadUnit.class, BaseBuildingTetherPayloadUnit::new);
		register(BaseBuildingTetherUnit.class, BaseBuildingTetherUnit::new);
		register(BaseUnitWaterMove.class, BaseUnitWaterMove::new);
		register(BaseTimedKillUnit.class, BaseTimedKillUnit::new);
		register(PayloadLegsUnit.class, PayloadLegsUnit::new);
		register(BaseBuildingTetherLegsUnit.class, BaseBuildingTetherLegsUnit::new);
		register(BaseCrawlUnit.class, BaseCrawlUnit::new);
		register(ChainedChainMechUnit.class, ChainedChainMechUnit::new);
		register(DamageAbsorbMechUnit.class, DamageAbsorbMechUnit::new);
		register(TractorBeamUnit.class, TractorBeamUnit::new);
		register(OrnitopterUnit.class, OrnitopterUnit::new);
		register(CopterUnit.class, CopterUnit::new);
		register(FloatMechCoreUnit.class, FloatMechCoreUnit::new);
		register(SentryUnit.class, SentryUnit::new);
		register(SwordUnit.class, SwordUnit::new);
		register(EnergyUnit.class, EnergyUnit::new);
		register(PesterUnit.class, PesterUnit::new);
		register(NucleoidUnit.class, NucleoidUnit::new);
		register(EipusinoUnit.class, EipusinoUnit::new);
		register(DPSMechUnit.class, DPSMechUnit::new);
		register(InvincibleShipUnit.class, InvincibleShipUnit::new);
		register(ApathyIUnit.class, ApathyIUnit::new);
		register(ApathySentryUnit.class, ApathySentryUnit::new);
		register(DespondencyUnit.class, DespondencyUnit::new);
		register(YggdrasilUnit.class, YggdrasilUnit::new);
		register(UltFire.class, UltFire::new);
		register(UltPuddle.class, UltPuddle::new);
		register(DiffBullet.class, DiffBullet::new);
		register(BlackHoleBullet.class, BlackHoleBullet::new);
		register(Spawner.class, Spawner::new);
		register(DesSpearEntity.class, DesSpearEntity::new);
		register(DesShockWaveEntity.class, DesShockWaveEntity::new);
		register(RenderGroupEntity.class, RenderGroupEntity::new);
		register(ShrapnelEntity.class, ShrapnelEntity::new);
		register(VapourizeEffectState.class, VapourizeEffectState::new);
	}
}
