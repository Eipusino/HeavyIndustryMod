package endfield.world.blocks.power;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.graphics.Layer;
import mindustry.world.blocks.power.NuclearReactor;

public class GlowNuclearReactor extends NuclearReactor {
	public Blending blending = Blending.additive;
	public float alpha = 0.9f, glowScale = 10f, glowIntensity = 0.5f, layer = Layer.blockAdditive;
	public Color color = Color.red.cpy();

	public TextureRegion bottomRegion, glowRegion;

	public GlowNuclearReactor(String name) {
		super(name);
	}

	@Override
	public void load() {
		super.load();
		bottomRegion = Core.atlas.find(name + "-bottom");
		glowRegion = Core.atlas.find(name + "-glow");
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{bottomRegion, region};
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = GlowNuclearReactorBuild::new;
	}

	public class GlowNuclearReactorBuild extends NuclearReactorBuild {
		@Override
		public void draw() {
			Draw.rect(bottomRegion, x, y);

			Draw.color(coolColor, hotColor, heat);
			Fill.rect(x, y, size * Vars.tilesize, size * Vars.tilesize);

			Draw.color(liquids.current().color);
			Draw.alpha(liquids.currentAmount() / liquidCapacity);
			Draw.rect(topRegion, x, y);
			Draw.reset();

			Draw.rect(region, x, y);

			drawGlow();

			if (heat > flashThreshold) {
				flash += (1f + ((heat - flashThreshold) / (1f - flashThreshold)) * 5.4f) * Time.delta;
				Draw.color(Color.red, Color.yellow, Mathf.absin(flash, 9f, 1f));
				Draw.alpha(0.3f);
				Draw.rect(lightsRegion, x, y);
			}

			Draw.reset();
		}

		public void drawGlow() {
			if (warmup() <= 0.001f) return;

			float z = Draw.z();
			Draw.z(layer);
			Draw.blend(blending);
			Draw.color(color);
			Draw.alpha((Mathf.absin(totalProgress(), glowScale, alpha) * glowIntensity + 1f - glowIntensity) * warmup() * alpha);
			Draw.rect(glowRegion, x, y, 0f);
			Draw.reset();
			Draw.blend();
			Draw.z(z);
		}
	}
}
