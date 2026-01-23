package endfield.content;

import arc.graphics.Color;
import arc.math.Mathf;
import endfield.Vars2;
import endfield.entities.effect.WrapperEffect;
import endfield.graphics.Draws;
import endfield.graphics.Pal2;
import endfield.graphics.Shaders2;
import endfield.type.MultiCellLiquid;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.gen.Puddle;
import mindustry.type.Liquid;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * Defines the {@linkplain Liquid liquid} this mod offers.
 *
 * @author Eipusino
 */
public final class Liquids2 {
	static final int crystalFluidId = Draws.nextTaskId();

	public static Liquid brine;
	public static Liquid gas, lightOil, nitratedOil, blastReagent;
	public static MultiCellLiquid crystalFluid;

	/** Don't let anyone instantiate this class. */
	private Liquids2() {}

	/** Instantiates all contents. Called in the main thread in {@code EndFieldMod.loadContent()}. */
	@Internal
	public static void load() {
		if (Vars2.isPlugin) return;

		brine = new Liquid("brine", new Color(0xb8c89fff)) {{
			coolant = false;
			viscosity = 0.8f;
			explosiveness = 0.1f;
		}};
		gas = new Liquid("gas", new Color(0xfbd367ff)) {{
			gasColor = barColor = lightColor = color;
			gas = true;
			flammability = 1.25f;
			explosiveness = 0.25f;
		}};
		lightOil = new Liquid("light-oil", Color.rgb(239, 202, 152).a(0.8f)) {{
			heatCapacity = 0.7f;
			temperature = 0.3f;
			boilPoint = 0.6f;
			viscosity = 0.7f;
			flammability = 0.25f;
			explosiveness = 1.25f;
			gasColor = Color.grays(0.7f);
			effect = StatusEffects.muddy;
			coolant = false;
		}};
		nitratedOil = new Liquid("nitrated-oil", new Color(0x3c3e45ff)) {{
			temperature = 0.5f;
			viscosity = 0.8f;
			flammability = 1.5f;
			explosiveness = 1.8f;
			effect = StatusEffects.tarred;
			canStayOn.add(Liquids.water);
			coolant = false;
		}};
		blastReagent = new Liquid("blast-reagent", new Color(0xd97c7cff)) {{
			flammability = 0.75f;
			temperature = 0.5f;
			viscosity = 0.8f;
			explosiveness = 3f;
		}};
		crystalFluid = new MultiCellLiquid("crystal-fluid", Pal2.crystalAmmoBack) {{
			heatCapacity = 2.5f;
			lightColor = color.cpy().a(0.3f);
			particleSpacing = 10;
			particleEffect = WrapperEffect.wrap(Fx2.glowParticle, color);
			effect = StatusEffects.electrified;
			spreadTargets.add(Liquids.neoplasm);
			canStayOn.addAll(Liquids.water, Liquids.cryofluid, Liquids.oil, Liquids.arkycite, Liquids.neoplasm);
		}
			@Override
			public void drawPuddle(Puddle puddle) {
				Draws.drawTask(crystalFluidId, puddle, Shaders2.wave, s -> {
					s.waveMix = Pal2.crystalAmmoBright;
					s.mixAlpha = 0.2f + Mathf.absin(5, 0.2f);
					s.waveScl = 0.2f;
					s.maxThreshold = 1f;
					s.minThreshold = 0.4f;
				}, super::drawPuddle);
			}
		};
	}
}
