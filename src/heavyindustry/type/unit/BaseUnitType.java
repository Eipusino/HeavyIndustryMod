package heavyindustry.type.unit;

import arc.Core;
import arc.util.Strings;
import heavyindustry.gen.BaseUnit;
import heavyindustry.world.meta.HStat;
import mindustry.ai.types.MissileAI;
import mindustry.content.Items;
import mindustry.gen.EntityMapping;
import mindustry.gen.Entityc;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.world.meta.Env;

public class BaseUnitType extends UnitType {
	public float damageMultiplier = 1f;
	public float absorption = 0f;

	public BaseUnitType(String name) {
		super(name);
	}

	@Override
	public void setStats() {
		super.setStats();
		if (damageMultiplier < 1f) {
			stats.add(HStat.damageReduction, Core.bundle.format("hi-sin", Strings.autoFixed((1f - damageMultiplier) * 100, 2)));
		}
	}

	public void erekir() {
		outlineColor = Pal.darkOutline;
		envDisabled = Env.space;
		ammoType = new ItemAmmoType(Items.beryllium);
		researchCostMultiplier = 10f;
	}

	public void tank() {
		squareShape = true;
		omniMovement = false;
		rotateMoveFirst = true;
		rotateSpeed = 1.3f;
		envDisabled = Env.none;
		speed = 0.8f;
	}

	public void missile() {
		playerControllable = false;
		createWreck = false;
		createScorch = false;
		logicControllable = false;
		isEnemy = false;
		useUnitCap = false;
		drawCell = false;
		allowedInPayloads = false;
		controller = u -> new MissileAI();
		flying = true;
		envEnabled = Env.any;
		envDisabled = Env.none;
		physics = false;
		bounded = false;
		trailLength = 7;
		hidden = true;
		hoverable = false;
		speed = 4f;
		lifetime = 60f * 1.7f;
		rotateSpeed = 2.5f;
		range = 6f;
		targetPriority = -1f;
		outlineColor = Pal.darkOutline;
		fogRadius = 2f;
		loopSound = Sounds.missileTrail;
		loopSoundVolume = 0.05f;
		drawMinimap = false;
	}

	@Override
	protected void checkEntityMapping(Unit example) {
		if (constructor == null) {
			constructor = BaseUnit::new;

			arc.util.Log.warn(Strings.format("""
					No constructor set up for unit '@': Assign `constructor = [your unit constructor]`. Vanilla defaults are:
						"flying": UnitEntity::create
						"mech": MechUnit::create
						"legs": LegsUnit::create
						"naval": UnitWaterMove::create
						"payload": PayloadUnit::create
						"missile": TimedKillUnit::create
						"tank": TankUnit::create
						"hover": ElevationMoveUnit::create
						"tether": BuildingTetherPayloadUnit::create
						"crawl": CrawlUnit::create
					""", name));
		}

		// Often modders improperly only sets `constructor = ...` without mapping. Try to mitigate that.
		// In most cases, if the constructor is a Vanilla class, things should work just fine.
		if (EntityMapping.map(name) == null) EntityMapping.nameMap.put(name, constructor);
		int classId = example.classId();

		if (
			// Check if `classId()` even points to a valid constructor...
				EntityMapping.map(classId) == null ||
						// ...or if the class doesn't register itself and uses the ID of its base class.
						classId != ((Entityc) EntityMapping.map(classId).get()).classId()
		) {
			String type = example.getClass().getSimpleName();
			arc.util.Log.warn(Strings.format("""
					Invalid class ID for `@` detected (found: @). Potential fixes:
					- Register with `EntityMapping.register("some-unique-name", @::new)` to get an ID, and store it somewhere.
					- Override `@#classId()` to return that ID.
					""", type, classId, type, type));
		}
	}
}
