package heavyindustry.game;

import arc.Core;
import mindustry.game.Objectives.Objective;
import mindustry.world.Block;

public final class HObjectives {
	private HObjectives() {}

	public static class LaunchSector implements Objective {
		public Block requiredCore;

		public LaunchSector(Block core) {
			requiredCore = core;
		}

		@Override
		public boolean complete() {
			// lol
			return false;
		}

		@Override
		public String display() {
			return Core.bundle.format("requirement.launchsector", requiredCore.localizedName);
		}
	}
}
