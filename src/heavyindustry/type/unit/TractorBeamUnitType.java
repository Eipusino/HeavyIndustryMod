package heavyindustry.type.unit;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Unit;

public class TractorBeamUnitType extends UnitType2 {
	public boolean glowEngine = false;
	public TextureRegion cellGlow;
	public float tractorBeamRange = 100f;

	public TractorBeamUnitType(String name) {
		super(name);
	}

	@Override
	public void drawCell(Unit unit) {
		super.drawCell(unit);
		if (cellGlow.found()) {
			Draw.blend(Blending.additive);
			Draw.color(cellColor(unit));
			Draw.rect(cellGlow, unit.x, unit.y, unit.rotation - 90);
			Draw.reset();
			Draw.blend();
		}
	}

	@Override
	public void drawEngines(Unit unit) {
		if (glowEngine) Draw.blend(Blending.additive);
		super.drawEngines(unit);
		Draw.blend();
	}

	@Override
	public void drawTrail(Unit unit) {
		if (glowEngine) Draw.blend(Blending.additive);
		super.drawTrail(unit);
		Draw.blend();
	}

	@Override
	public void load() {
		super.load();
		cellGlow = Core.atlas.find(name + "-cell-glow");
	}
}
