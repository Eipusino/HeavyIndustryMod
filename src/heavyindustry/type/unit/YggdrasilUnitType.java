package heavyindustry.type.unit;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import mindustry.Vars;
import mindustry.content.StatusEffects;

public class YggdrasilUnitType extends BaseUnitType {
	public TextureRegion[] legRegions, legBaseRegions, tentacleRegions;
	public TextureRegion tentacleEndRegion;

	public YggdrasilUnitType(String name) {
		super(name);
	}

	@Override
	public void init() {
		super.init();
		allowLegStep = true;
		range = maxRange = 720f;

		immunities.addAll(Vars.content.statusEffects());
		immunities.remove(StatusEffects.burning);
		immunities.remove(StatusEffects.melting);
	}

	@Override
	public void load() {
		super.load();
		legRegions = new TextureRegion[3];
		legBaseRegions = new TextureRegion[3];
		tentacleRegions = new TextureRegion[3];

		for (int i = 0; i < 3; i++) {
			legRegions[i] = Core.atlas.find(name + "-leg-" + i);
			legBaseRegions[i] = Core.atlas.find(name + "-leg-base-" + i);
			tentacleRegions[i] = Core.atlas.find(name + "-tentacle-" + i);
		}
		tentacleEndRegion = Core.atlas.find(name + "-tentacle-end");
	}
}
