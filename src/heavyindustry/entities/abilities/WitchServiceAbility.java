package heavyindustry.entities.abilities;

import arc.graphics.Color;
import arc.math.geom.Rect;
import arc.util.Time;
import heavyindustry.content.HFx;
import heavyindustry.content.HStatusEffects;
import heavyindustry.graphics.HPal;
import heavyindustry.util.ObjectFloatMapf;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

import static mindustry.Vars.tilesize;

public class WitchServiceAbility extends Ability {
	protected static Rect tmpRect = new Rect();

	public ObjectFloatMapf<Unit> findMap = new ObjectFloatMapf<>(Unit.class);

	public float width = 60f, height = 60f;
	public StatusEffect effectType = HStatusEffects.apoptosis;
	public float effectTime = 600f;

	public float applyMultiplier = 0.05f;
	public float timeApply = 60f;
	public StatusEffect applyEffect = StatusEffects.slow;

	public float reload = timeApply;

	public Effect work, applyIn, applyOut;

	public Color color = HPal.titaniumAmmoBack;

	public boolean working = false;

	public WitchServiceAbility() {
		this(HFx.witchServiceWork, HFx.witchServiceApplyIn, HFx.witchServiceApplyOut);
	}

	public WitchServiceAbility(Effect wk, Effect in, Effect out) {
		work = wk;
		applyIn = in;
		applyOut = out;
	}

	protected Rect getRect(Unit unit, Rect rect) {
		float w = width * tilesize, h = height * tilesize;
		rect.setCentered(unit.x, unit.y, w, h);

		return rect;
	}

	@Override
	public void update(Unit unit) {
		super.update(unit);

		Rect rect = getRect(unit, tmpRect);

		if ((reload += Time.delta) >= timeApply) {
			Units.nearbyEnemies(unit.team, rect, u -> {
				if (u.targetable(unit.team) && !u.inFogTo(unit.team)) {
					if (!u.hasEffect(effectType) && !u.isImmune(effectType)) {
						if (!findMap.containsKey(u)) {
							findMap.put(u, applyMultiplier);
						} else {
							findMap.put(u, findMap.get(u, 0f) + applyMultiplier);
						}

						working = true;
						applyIn.at(u.x, u.y, u.rotation, color, u);
						u.apply(applyEffect, timeApply / 2f);
					} else {
						if (u.isValid() && findMap.containsKey(u)) {
							findMap.remove(u);
						}
					}
				}
			});
			for (Unit u : findMap.keys()) {
				if (u == null || !u.isValid() || u.hasEffect(effectType)) {
					findMap.remove(u);

					continue;
				}

				findMap.put(u, findMap.get(u, 0f) + applyMultiplier);
				applyOut.at(u.x, u.y, u.rotation, color, u);

				if (findMap.get(u, 0f) >= 1) {
					u.apply(effectType, effectTime);
					findMap.remove(u);
				}
			}

			reload = 0;
		}

		if (working) {
			work.at(unit.x, unit.y, unit.rotation, color, rect);
			working = false;
		}
	}

	@Override
	public String getBundle() {
		return "ability.witch-service";
	}
}
