package endfield.type.unit;

import arc.Core;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import endfield.world.meta.EStat;
import mindustry.ai.types.MissileAI;
import mindustry.content.Items;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.world.meta.Env;
import org.jetbrains.annotations.Nullable;

public class UnitType2 extends UnitType {
	public ItemStack @Nullable [] requirements;

	public float damageMultiplier = 1f;
	public float absorption = 0f;

	public UnitType2(String name) {
		super(name);
	}

	@Override
	public void setStats() {
		super.setStats();
		if (damageMultiplier < 1f) {
			stats.add(EStat.damageReduction, Core.bundle.format("text.sin", Strings.autoFixed((1f - damageMultiplier) * 100, 2)));
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
		loopSound = Sounds.loopMissileTrail;
		loopSoundVolume = 0.05f;
		drawMinimap = false;
	}

	public void requirements(Object... req) {
		requirements = ItemStack.with(req);
	}

	@Override
	public ItemStack[] getRequirements(UnitType[] prevReturn, float[] timeReturn) {
		if (requirements == null) return super.getRequirements(prevReturn, timeReturn);

		if (totalRequirements != null) return totalRequirements;

		totalRequirements = requirements;
		buildTime = 0;
		if (prevReturn != null) prevReturn[0] = null;

		for (ItemStack stack : requirements) {
			buildTime += stack.item.cost * stack.amount;
		}
		if (timeReturn != null) timeReturn[0] = buildTime;

		return requirements;
	}

	public int version() {
		return 0;
	}

	public void init(Unit unit) {}

	public void read(Unit sglUnitEntity, Reads read, int revision) {}

	public void write(Unit sglUnitEntity, Writes write) {}
}
