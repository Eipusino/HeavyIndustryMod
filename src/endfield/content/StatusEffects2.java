package endfield.content;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import endfield.Vars2;
import endfield.graphics.Pal2;
import endfield.math.Mathm;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.units.StatusEntry;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.MultiPacker;
import mindustry.graphics.MultiPacker.PageType;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * Sets up content {@link StatusEffect status effects}. Loaded after every other content is instantiated.
 *
 * @author Eipusino
 */
public final class StatusEffects2 {
	public static StatusEffect2 overheat, regenerating, breached, radiation, flamePoint, ultFireBurn;
	public static StatusEffect2 territoryFieldIncrease, territoryFieldSuppress, apoptosis;

	/** Don't let anyone instantiate this class. */
	private StatusEffects2() {}

	/** Instantiates all contents. Called in the main thread in {@code EndFieldMod.loadContent()}. */
	@Internal
	public static void load() {
		if (Vars2.isPlugin) return;

		overheat = new StatusEffect2("overheat") {{
			color = new Color(0xffdcd8ff);
			disarm = true;
			dragMultiplier = 1f;
			speedMultiplier = 0.5f;
			damage = 5f;
			effectChance = 0.35f;
			effect = Fx2.glowParticle;
		}};
		regenerating = new StatusEffect2("regenerating") {{
			color = Pal2.regenerating;
			damage = -4;
			effectChance = 0.3f;
			effect = Fx2.glowParticle;
			init(() -> opposite(StatusEffects.sapped, StatusEffects.slow, breached));
		}};
		breached = new StatusEffect2("breached") {{
			color = new Color(0x666484ff);
			healthMultiplier = 0.9f;
			speedMultiplier = 0.8f;
			reloadMultiplier = 0.9f;
			transitionDamage = 220f;
			permanent = true;
		}};
		radiation = new StatusEffect2("radiation") {{
			damage = 1.6f;
		}
			@Override
			public void update(Unit unit, StatusEntry entry) {
				super.update(unit, entry);

				if (Mathf.chanceDelta(0.008f * Mathm.clamp(entry.time / 120f))) unit.damage(unit.maxHealth * 0.125f);

				for (WeaponMount temp : unit.mounts) {
					if (temp == null) continue;

					float strength = Mathm.clamp(entry.time / 120f, 0f, 1f);

					if (Mathf.chanceDelta(0.12f))
						temp.reload = Math.min(temp.reload + Time.delta * 1.5f * strength, temp.weapon.reload);
					temp.rotation += Mathf.range(12f * strength);
				}
			}
		};
		flamePoint = new StatusEffect2("flame-point") {{
			damage = 0.2f;
			color = Pal.lightFlame;
			parentizeEffect = true;
			effect = new Effect(36, e -> {
				if (!(e.data instanceof Unit unit)) return;
				Lines.stroke(2 * e.foutpow(), Pal.blastAmmoBack);
				for (int i = 0; i < 3; i++) {
					float a = 360 / 3f * i + e.time * 6;
					float x = Mathm.dx(e.x, Math.max(6, unit.hitSize / 2f), a), y = Mathm.dy(e.y, Math.max(6, unit.hitSize / 2f), a);
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
		ultFireBurn = new StatusEffect2("ult-fire-burn") {{
			color = Pal.techBlue;
			damage = 6.5f;
			speedMultiplier = 1.2f;
			effect = Fx2.ultFireBurn;
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
		territoryFieldIncrease = new StatusEffect2("territory-field-increase") {{
			color = new Color(0xea8878ff);
			buildSpeedMultiplier = 1.5f;
			speedMultiplier = 1.1f;
			reloadMultiplier = 1.2f;
			damage = -0.2f;
			effectChance = 0.07f;
			effect = Fx.overclocked;
		}};
		territoryFieldSuppress = new StatusEffect2("territory-field-suppress") {{
			color = new Color(0x8b9bb4ff);
			speedMultiplier = 0.85f;
			reloadMultiplier = 0.8f;
			damage = 15 / 60f;
			effectChance = 0.07f;
			effect = Fx.overclocked;
		}};
		apoptosis = new StatusEffect2("apoptosis") {{
			color = applyColor = Pal2.titaniumAmmoBack;
			damage = -1;
			parentizeApplyEffect = true;
			applyEffect = new Effect(45, e -> {
				if (e.data instanceof Unit u) {
					float size = u.hitSize * 2;
					Fx.rand.setSeed(e.id);
					float pin = (1 - e.foutpow());
					Lines.stroke(size / 24 * e.foutpow(), e.color);
					Lines.circle(e.x, e.y, size * pin);
					for (int i = 0; i < 5; i++) {
						float a = Fx.rand.random(180);
						float lx = Mathm.dx(e.x, size * pin, a);
						float ly = Mathm.dy(e.y, size * pin, a);
						Drawf.tri(lx, ly, size / 32 * e.foutpow(), (size + Fx.rand.random(-size, size)) * e.foutpow(), a + 180);
					}
					for (int i = 0; i < 5; i++) {
						float a = 180 + Fx.rand.random(180);
						float lx = Mathm.dx(e.x, size * pin, a);
						float ly = Mathm.dy(e.y, size * pin, a);
						Drawf.tri(lx, ly, size / 32 * e.foutpow(), (size + Fx.rand.random(-size, size)) * e.foutpow(), a + 180);
					}
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

				unit.health -= Time.delta + unit.maxHealth * 0.0004f;

				if (effect != Fx.none && Mathf.chanceDelta(effectChance)) {
					Tmp.v1.rnd(Mathf.range(unit.type.hitSize / 2f));
					effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0, color, parentizeEffect ? unit : null);
				}
			}
		};
	}

	public static class StatusEffect2 extends StatusEffect {
		public Color outlineColor = Pal.gray;

		public StatusEffect2(String name) {
			super(name);

			outline = true;
		}

		@Override
		public void createIcons(MultiPacker packer) {
			if (!outline || !uiIcon.found()) return;

			//color image
			Pixmap base = Core.atlas.getPixmap(uiIcon).crop();
			Pixmap tint = base;
			for (int y = 0; y < base.height; y++) {
				for (int x = 0; x < base.width; x++) {
					tint.setRaw(x, y, Color.muli(tint.getRaw(x, y), color.rgba()));
				}
			}

			//outline the image
			Pixmap container = new Pixmap(tint.width + 6, tint.height + 6);
			container.draw(base, 3, 3, true);
			base = container.outline(outlineColor, 3);
			packer.add(PageType.ui, name, base);
		}
	}
}
