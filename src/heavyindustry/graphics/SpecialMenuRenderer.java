package heavyindustry.graphics;

import arc.Core;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import heavyindustry.HVars;
import mindustry.graphics.MenuRenderer;

public class SpecialMenuRenderer extends MenuRenderer {
	public static final TextureRegion coverDesktop, coverMobile;

	protected TextureRegion region;

	static {
		coverDesktop = new TextureRegion(new Texture(HVars.internalTree.child("other/cover-desktop.png")));
		coverMobile = new TextureRegion(new Texture(HVars.internalTree.child("other/cover-mobile.png")));
	}

	public float scale;
	public boolean isMobile;

	public SpecialMenuRenderer() {
		isMobile = Core.app.isMobile();
		region = isMobile ? coverMobile : coverDesktop;

		scale = Math.max(Core.graphics.getWidth() / region.width, Core.graphics.getHeight() / region.height);
	}

	@Override
	public void render() {
		Draw.color();
		Draw.rect(region,
				Core.graphics.getWidth() / 2f,
				Core.graphics.getHeight() / 2f,
				region.width * scale,
				region.height * scale);
		Draw.flush();
	}
}
