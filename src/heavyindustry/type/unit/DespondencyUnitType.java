package heavyindustry.type.unit;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import heavyindustry.type.weapons.EndDespondencyWeapon;
import mindustry.entities.Leg;
import mindustry.gen.Legsc;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.Weapon;

public class DespondencyUnitType extends BaseUnitType {
	protected static Vec2 legOff = new Vec2();

	public TextureRegion legShadowRegion, legShadowBaseRegion;
	public int mainWeaponIdx = -1;

	public DespondencyUnitType(String name) {
		super(name);
	}

	@Override
	public void init() {
		super.init();

		int idx = 0;
		for (Weapon w : weapons) {
			if (w instanceof EndDespondencyWeapon) {
				mainWeaponIdx = idx;
				break;
			}
			idx++;
		}
	}

	@Override
	public void load() {
		super.load();

		legShadowRegion = Core.atlas.find(name + "-leg-shadow");
		legShadowBaseRegion = Core.atlas.find(name + "-leg-base-shadow");
	}

	@Override
	public <T extends Unit & Legsc> void drawLegs(T unit) {
		if (shadowElevation > 0) {
			float invDrown = 1f - unit.drownTime;
			float scl = shadowElevation * invDrown * shadowElevationScl;
			Leg[] legs = unit.legs();

			Draw.color(Pal.shadow);
			for (int j = legs.length - 1; j >= 0; j--) {
				int i = (j % 2 == 0 ? j / 2 : legs.length - 1 - j / 2);
				Leg leg = legs[i];
				boolean flip = i >= legs.length / 2f;
				int flips = Mathf.sign(flip);
				float elev = 0f;
				if (leg.moving) {
					elev = Mathf.slope(1f - leg.stage);
				}
				float mid = (elev / 2f + 0.5f) * scl;

				Vec2 position = unit.legOffset(legOff, i).add(unit);

				Vec2 v1 = Tmp.v1.set(leg.base).sub(leg.joint).inv().setLength(legExtension).add(leg.joint);

				Lines.stroke(legShadowRegion.height * legShadowRegion.scl() * flips);
				Lines.line(legShadowRegion, position.x + (shadowTX * scl), position.y + (shadowTY * scl), leg.joint.x + (shadowTX * mid), leg.joint.y + (shadowTY * mid), false);

				Lines.stroke(legShadowBaseRegion.height * legShadowBaseRegion.scl() * flips);
				Lines.line(legShadowBaseRegion, v1.x + (shadowTX * mid), v1.y + (shadowTY * mid), leg.base.x + (shadowTX * elev * scl), leg.base.y + (shadowTY * elev * scl), false);
			}

			Draw.color();
		}

		super.drawLegs(unit);
	}

	@Override
	public void drawShadow(Unit unit) {
		float e = shadowElevation * (1f - unit.drownTime);
		Draw.color(Pal.shadow);

		Draw.rect(shadowRegion, unit.x + shadowTX * e, unit.y + shadowTY * e, unit.rotation - 90);
		Draw.color();
	}
}
