package heavyindustry.graphics;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.graphics.MenuRenderer;

import static heavyindustry.HVars.name;

public class MuelsyseMenuRenderer extends MenuRenderer {
	public TextureRegion region;

	@Override
	public void render() {
		Draw.color();

		if (region == null) region = Core.atlas.find(name("muelsyse-main-page"));

		Draw.rect(region,
				Core.graphics.getWidth() / 2f,
				Core.graphics.getHeight() / 2f,
				region.width * Core.graphics.getHeight() / 1000f,
				region.height * Core.graphics.getHeight() / 1000f);
		Draw.flush();
	}
}
