package heavyindustry.entities.abilities;

import arc.graphics.*;
import arc.util.*;
import heavyindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class HealAbility extends RepairFieldAbility {
	public Color applyColor = Pal.heal;
	public boolean ignoreHealthMultiplier = true;
	//Percent per tick
	public float selfHealAmount = 0.0005f;
	public float selfHealReloadTime = -1;

	protected float lastHealth = 0;
	protected float selfHealReload = 0;

	public HealAbility() {
		this(1f, 1f, 1f);
	}

	public HealAbility(float amount, float reload, float range, Color applyColor) {
		this(amount, reload, range);
		this.applyColor = applyColor;
	}

	public HealAbility(float amount, float reload, float range) {
		super(amount, reload, range);

		healEffect = HIFx.healReceiveCircle;
		activeEffect = HIFx.healSendCircle;
	}

	public void update(Unit unit) {
		timer += Time.delta;

		if (timer >= reload) {
			wasHealed = false;

			Units.nearby(unit.team, unit.x, unit.y, range, other -> {
				if (other.damaged()) {
					healEffect.at(other.x, other.y, 0, applyColor, parentizeEffects ? other : null);
					wasHealed = true;
				}
				other.heal(amount);
			});

			if (wasHealed) {
				activeEffect.at(unit.x, unit.y, range, applyColor);
			}

			timer = 0f;
		}

		if (selfHealReloadTime < 0) return;

		if (lastHealth <= unit.health && unit.damaged()) {
			selfHealReload += Time.delta;

			if (selfHealReload > selfHealReloadTime) {
				unit.healFract(selfHealAmount * (ignoreHealthMultiplier ? 1 : 1 / unit.healthMultiplier));
			}
		} else {
			selfHealReload = 0;
		}

		lastHealth = unit.health;
	}
}
