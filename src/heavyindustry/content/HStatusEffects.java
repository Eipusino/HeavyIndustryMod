package heavyindustry.content;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.graphics.HPal;
import heavyindustry.util.Utils;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.units.StatusEntry;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.MultiPacker;
import mindustry.graphics.MultiPacker.PageType;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.content.StatusEffects.sapped;
import static mindustry.content.StatusEffects.slow;

/**
 * Sets up content {@link StatusEffect status effects}. Loaded after every other content is instantiated.
 *
 * @author Eipusino
 */
public final class HStatusEffects {
	public static StatusEffect
			overheat, regenerating, breached, flamePoint, ultFireBurn,
			territoryFieldIncrease, territoryFieldSuppress, apoptosis;

	/** Don't let anyone instantiate this class. */
	private HStatusEffects() {}

	/** Instantiates all contents. Called in the main thread in {@code HeavyIndustryMod.loadContent()}. */
	public static void load() {
		overheat = new BaseStatusEffect("overheat") {{
			color = Color.valueOf("ffdcd8");
			disarm = true;
			dragMultiplier = 1f;
			speedMultiplier = 0.5f;
			damage = 5f;
			effectChance = 0.35f;
			effect = HFx.glowParticle;
		}};
		regenerating = new BaseStatusEffect("regenerating") {{
			color = HPal.regenerating;
			damage = -4;
			effectChance = 0.3f;
			effect = HFx.glowParticle;
			init(() -> opposite(sapped, slow, breached));
		}};
		breached = new BaseStatusEffect("breached") {{
			color = Color.valueOf("666484");
			healthMultiplier = 0.9f;
			speedMultiplier = 0.8f;
			reloadMultiplier = 0.9f;
			transitionDamage = 220f;
			permanent = true;
		}};
		flamePoint = new BaseStatusEffect("flame-point") {{
			damage = 0.2f;
			color = Pal.lightFlame;
			parentizeEffect = true;
			effect = new Effect(36, e -> {
				if (!(e.data instanceof Unit unit)) return;
				Lines.stroke(2 * e.foutpow(), HPal.blastRed);
				for (int i = 0; i < 3; i++) {
					float a = 360 / 3f * i + e.time * 6;
					float x = Utils.dx(e.x, Math.max(6, unit.hitSize / 2f), a), y = Utils.dy(e.y, Math.max(6, unit.hitSize / 2f), a);
					Lines.lineAngle(x, y, a - 120, Math.max(3, unit.hitSize / 4f) * e.foutpow());
					Lines.lineAngle(x, y, a + 120, Math.max(3, unit.hitSize / 4f) * e.foutpow());
				}
			});
			speedMultiplier = 0.9f;
		}
			@Override
			public void update(Unit unit, StatusEntry entry) {
				unit.damageContinuousPierce(damage);

				if (Mathf.chanceDelta(effectChance)) {
					effect.at(unit.x, unit.y, 0, color, unit);
				}
			}
		};
		ultFireBurn = new BaseStatusEffect("ult-fire-burn") {{
			color = Pal.techBlue;
			damage = 6.5f;
			speedMultiplier = 1.2f;
			effect = HFx.ultFireBurn;
		}
			@Override
			public void update(Unit unit, StatusEntry entry) {
				unit.damageContinuousPierce(damage + unit.maxHealth * 0.00001f);

				if (Mathf.chanceDelta(effectChance)) {
					Tmp.v1.rnd(Mathf.range(unit.type.hitSize / 2f));
					effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0, color, unit);
				}
			}
		};
		territoryFieldIncrease = new BaseStatusEffect("territory-field-increase") {{
			color = Color.valueOf("ea8878");
			buildSpeedMultiplier = 1.5f;
			speedMultiplier = 1.1f;
			reloadMultiplier = 1.2f;
			damage = -0.2f;
			effectChance = 0.07f;
			effect = Fx.overclocked;
		}};
		territoryFieldSuppress = new BaseStatusEffect("territory-field-suppress") {{
			color = Color.valueOf("8b9bb4");
			speedMultiplier = 0.85f;
			reloadMultiplier = 0.8f;
			damage = 15 / 60f;
			effectChance = 0.07f;
			effect = Fx.overclocked;
		}};
		apoptosis = new BaseStatusEffect("apoptosis") {{
			color = Color.valueOf("88a4ff");
			damage = -1;
			parentizeApplyEffect = true;
			applyColor = Pal.techBlue;
			applyEffect = new Effect(45, e -> {
				if (!(e.data instanceof Unit u)) return;

				float size = u.hitSize * 2;
				Fx.rand.setSeed(e.id);
				float pin = (1 - e.foutpow());
				Lines.stroke(size / 24 * e.foutpow(), e.color);
				Lines.circle(e.x, e.y, size * pin);
				for (int i = 0; i < 5; i++) {
					float a = Fx.rand.random(180);
					float lx = Utils.dx(e.x, size * pin, a);
					float ly = Utils.dy(e.y, size * pin, a);
					Drawf.tri(lx, ly, size / 32 * e.foutpow(), (size + Fx.rand.random(-size, size)) * e.foutpow(), a + 180);
				}
				for (int i = 0; i < 5; i++) {
					float a = 180 + Fx.rand.random(180);
					float lx = Utils.dx(e.x, size * pin, a);
					float ly = Utils.dy(e.y, size * pin, a);
					Drawf.tri(lx, ly, size / 32 * e.foutpow(), (size + Fx.rand.random(-size, size)) * e.foutpow(), a + 180);
				}
			});
		}
			@Override
			public void setStats() {
				super.setStats();

				stats.remove(Stat.healing);
				stats.addMultModifier(Stat.damageMultiplier, 0.8f);
				stats.addMultModifier(Stat.speedMultiplier, 0.4f);
				stats.addMultModifier(Stat.reloadMultiplier, 0.5f);
				stats.add(Stat.damage, 60f, StatUnit.perSecond);
			}

			@Override
			public void applied(Unit unit, float time, boolean extend) {
				super.applied(unit, time, extend);

				unit.health -= 100f;
			}

			@Override
			public void update(Unit unit, StatusEntry entry) {
				unit.damageMultiplier *= 0.8f;
				unit.speedMultiplier *= 0.4f;
				unit.reloadMultiplier *= 0.5f;

				unit.health -= Time.delta + unit.maxHealth * 0.0006f;

				if (effect != Fx.none && Mathf.chanceDelta(effectChance)) {
					Tmp.v1.rnd(Mathf.range(unit.type.hitSize / 2f));
					effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0, color, parentizeEffect ? unit : null);
				}
			}
		};
	}

	public static class BaseStatusEffect extends StatusEffect {
		public Color outlineColor = Pal.gray;

		public BaseStatusEffect(String name) {
			super(name);

			outline = true;
		}

		@Override
		public void createIcons(MultiPacker packer) {
			if (!outline || !uiIcon.found()) return;

			//color image
			Pixmap base = Core.atlas.getPixmap(uiIcon).crop();
			Pixmap tint = base;
			base.each((x, y) -> tint.setRaw(x, y, Color.muli(tint.getRaw(x, y), color.rgba())));

			//outline the image
			Pixmap container = new Pixmap(tint.width + 6, tint.height + 6);
			container.draw(base, 3, 3, true);
			base = container.outline(outlineColor, 3);
			packer.add(PageType.ui, name, base);
		}
	}
}
