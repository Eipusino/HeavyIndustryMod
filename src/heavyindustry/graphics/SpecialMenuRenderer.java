package heavyindustry.graphics;

import arc.Core;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import heavyindustry.HVars;
import mindustry.Vars;
import mindustry.graphics.MenuRenderer;

public class SpecialMenuRenderer extends MenuRenderer {
	public TextureRegion region;

	@Override
	public void render() {
		Draw.color();

		if (region == null) region = new TextureRegion(new Texture(HVars.internalTree.child("other/" + (Vars.mobile ? "cover-mobile.png" : "cover-desktop.png"))));

		Draw.rect(region,
				Core.graphics.getWidth() / 2f,
				Core.graphics.getHeight() / 2f,
				region.width,
				region.height);
		Draw.flush();
	}
}
