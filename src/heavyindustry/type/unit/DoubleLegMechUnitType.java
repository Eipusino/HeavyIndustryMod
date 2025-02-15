package heavyindustry.type.unit;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.world.blocks.environment.*;

public class DoubleLegMechUnitType extends UnitTypef {
	public TextureRegion leg2Region;

	public DoubleLegMechUnitType(String name) {
		super(name);
	}

	@Override
	public void drawMech(Mechc mech) {
		if (!(mech instanceof Unit unit)) return;

		Draw.reset();

		float e = unit.elevation;

		float sin = Mathf.lerp(Mathf.sin(mech.walkExtend(true), 2f / Mathf.PI, 1f), 0f, e);
		float extension = Mathf.lerp(mech.walkExtend(false), 0, e);
		float boostTrns = e * 2f;

		Floor floor = unit.isFlying() ? Blocks.air.asFloor() : unit.floorOn();

		if (floor.isLiquid) {
			Draw.color(Color.white, floor.mapColor, 0.5f);
		}

		for (int i : Mathf.signs) {
			Draw.mixcol(Tmp.c1.set(mechLegColor).lerp(Color.white, Mathf.clamp(unit.hitTime)), Math.max(Math.max(0, i * extension / mechStride), unit.hitTime));

			Draw.rect(legRegion,
					unit.x + Angles.trnsx(mech.baseRotation(), extension * i - boostTrns, -boostTrns * i),
					unit.y + Angles.trnsy(mech.baseRotation(), extension * i - boostTrns, -boostTrns * i),
					legRegion.width * legRegion.scl() * i,
					legRegion.height * legRegion.scl() * (1 - Math.max(-sin * i, 0) * 0.5f),
					mech.baseRotation() - 90 + 35f * i * e);
		}
		for (int g : Mathf.signs) {
			Draw.mixcol(Tmp.c1.set(mechLegColor).lerp(Color.white, Mathf.clamp(unit.hitTime)), Math.max(Math.max(0, g * (mechStride * 3 - extension) / mechStride), unit.hitTime));

			Draw.rect(leg2Region,
					unit.x + Angles.trnsx(mech.baseRotation(), (mechStride * 3 - extension) * g - boostTrns, -boostTrns * g),
					unit.y + Angles.trnsy(mech.baseRotation(), (mechStride * 3 - extension) * g - boostTrns, -boostTrns * g),
					leg2Region.width * leg2Region.scl() * g,
					leg2Region.height * leg2Region.scl() * (Math.max(-sin * g, 0) * 0.5f),
					mech.baseRotation() - 90 + 35f * g * e);
		}

		Draw.mixcol(Color.white, unit.hitTime);

		if (unit.lastDrownFloor != null) {
			Draw.color(Color.white, Tmp.c1.set(unit.lastDrownFloor.mapColor).mul(0.83f), unit.drownTime * 0.9f);
		} else {
			Draw.color(Color.white);
		}

		Draw.rect(baseRegion, unit, mech.baseRotation() - 90);

		Draw.mixcol();
	}

	@Override
	public void load() {
		super.load();
		leg2Region = Core.atlas.find(name + "leg-2");
	}
}
