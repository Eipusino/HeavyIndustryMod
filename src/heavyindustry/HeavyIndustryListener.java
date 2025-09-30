package heavyindustry;

import arc.ApplicationCore;
import arc.ApplicationListener;
import heavyindustry.entities.HEntity;
import mindustry.Vars;

public class HeavyIndustryListener implements ApplicationListener {
	public HeavyIndustryListener() {
		if (Vars.platform instanceof ApplicationCore core) {
			core.add(this);
		}
	}

	@Override
	public void update() {
		HEntity.update();
	}
}
