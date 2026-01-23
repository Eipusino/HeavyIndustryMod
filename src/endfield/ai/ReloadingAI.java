package endfield.ai;

import arc.func.Prov;
import mindustry.Vars;
import mindustry.entities.units.AIController;
import mindustry.world.meta.BlockFlag;

public class ReloadingAI extends AIController {
	public Prov<AIController> provider;

	public ReloadingAI(Prov<AIController> prov) {
		provider = prov;
	}

	@Override
	public void updateMovement() {
		moveTo(Vars.indexer.findClosestFlag(unit.x, unit.y, unit.team, BlockFlag.battery), 6f);
	}

	@Override
	public boolean useFallback() {
		return !(Vars.state.rules.unitAmmo && (unit.ammo / unit.type.ammoCapacity) < 0.2f);
	}

	@Override
	public AIController fallback() {
		return provider.get();
	}
}
