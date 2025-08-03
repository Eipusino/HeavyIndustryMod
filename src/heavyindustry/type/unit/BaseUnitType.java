package heavyindustry.type.unit;

import arc.Core;
import arc.util.Strings;
import heavyindustry.world.meta.HStat;
import mindustry.ai.types.MissileAI;
import mindustry.content.Items;
import mindustry.gen.Sounds;
import mindustry.gen.TimedKillUnit;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.world.meta.Env;

public class BaseUnitType extends UnitType {
	public boolean invincible = false;
	public float damageMultiplier = 1f;

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
}
