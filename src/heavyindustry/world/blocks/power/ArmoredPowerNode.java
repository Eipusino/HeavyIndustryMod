package heavyindustry.world.blocks.power;

import mindustry.content.Fx;
import mindustry.graphics.Pal;
import mindustry.world.blocks.power.PowerNode;

public class ArmoredPowerNode extends PowerNode {
	public final int timerHeal = timers++;

	public ArmoredPowerNode(String name) {
		super(name);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ArmoredPowerNodeBuild::new;
	}

	public class ArmoredPowerNodeBuild extends PowerNodeBuild {
		@Override
		public void updateTile() {
			if (damaged() && power.graph.getSatisfaction() > 0.5f) {
				if (timer.get(timerHeal, 90f)) {
					Fx.healBlockFull.at(x, y, 0, Pal.powerLight, block);
					healFract(5 * power.graph.getSatisfaction());
				}
			}
		}
	}
}
