package heavyindustry.content;

import arc.graphics.*;
import arc.math.*;
import heavyindustry.core.*;
import heavyindustry.entities.effect.*;
import heavyindustry.graphics.*;
import heavyindustry.type.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.*;

/**
 * Defines the {@linkplain Liquid liquid} this mod offers.
 *
 * @author Eipusino
 */
public final class Liquidsf {
	private static final int nanoFluidId = Draws.nextTaskId();

	public static Liquid
			brine, methane, nanoFluid, nitratedOil, originiumFluid;

	/** Don't let anyone instantiate this class. */
	private Liquidsf() {}

	/** Instantiates all contents. Called in the main thread in {@link HeavyIndustryMod#loadContent()}. */
	public static void load() {
		brine = new Liquid("brine", Color.valueOf("b8c89f")) {{
			coolant = false;
			viscosity = 0.8f;
			explosiveness = 0.1f;
		}};
		methane = new Liquid("methane", Color.valueOf("fbd367")) {{
			gasColor = barColor = lightColor = color;
			gas = true;
			flammability = 1f;
			explosiveness = 1f;
		}};
		nanoFluid = new Liquid("nano-fluid", Palf.nanoCoreBlue) {{
			heatCapacity = 1.8f;
			viscosity = 0.8f;
			temperature = 0.3f;
			lightColor = color.cpy().a(0.3f);
			particleSpacing = 10;
			particleEffect = WrapperEffect.wrap(Fxf.glowParticle, color);
			effect = StatusEffects.electrified;
			coolant = true;
		}
			@Override
			public void drawPuddle(Puddle puddle) {
				Draws.drawTask(nanoFluidId, puddle, Shadersf.wave, s -> {
					s.waveMix = Palf.nanoCoreBlueBright;
					s.mixAlpha = 0.2f + Mathf.absin(5, 0.2f);
					s.waveScl = 0.2f;
					s.maxThreshold = 1f;
					s.minThreshold = 0.4f;
				}, super::drawPuddle);
			}
		};
		nitratedOil = new Liquid("nitrated-oil", Color.valueOf("3c3e45")) {{
			temperature = 0.5f;
			viscosity = 0.8f;
			flammability = 0.8f;
			explosiveness = 3.2f;
			effect = StatusEffects.tarred;
			canStayOn.add(Liquids.water);
			coolant = false;
		}};
		originiumFluid = new ExtraCellLiquid("originium-fluid", Color.black) {{
			heatCapacity = 1.5f;
			flammability = 0.2f;
			explosiveness = 0.4f;
			coolant = false;
			spreadTargets.addAll(Liquids.neoplasm);
			canStayOn.addAll(Liquids.water, Liquids.cryofluid, Liquids.oil, Liquids.arkycite, Liquids.neoplasm);
		}};
	}
}
