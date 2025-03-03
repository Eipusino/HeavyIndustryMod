package heavyindustry.type.ammo;

import arc.graphics.Color;
import mindustry.gen.Iconc;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.AmmoType;

public class VanityAmmoType implements AmmoType {
	@Override
	public String icon() {
		return Iconc.units + "";
	}

	@Override
	public Color color() {
		return Pal.health;
	}

	@Override
	public Color barColor() {
		return Pal.health;
	}

	@Override
	public void resupply(Unit unit) {
		// no
	}
}
