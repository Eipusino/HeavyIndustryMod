package heavyindustry.graphics;

import arc.Core;
import arc.graphics.g2d.Draw;
import mindustry.graphics.MenuRenderer;

import static heavyindustry.HVars.name;

public class MuelsyseMenuRenderer extends MenuRenderer {
	@Override
	public void render() {
		//TODO zz
		Draw.color();

		var region = Core.atlas.find(name("muelsyse-main-page"));

		Draw.rect(region,
				Core.graphics.getWidth() / 2f,
				Core.graphics.getHeight() / 2f,
				region.width * Core.graphics.getHeight() / 1000f,
				region.height * Core.graphics.getHeight() / 1000f);
		Draw.flush();
	}
}
