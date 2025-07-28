package heavyindustry.graphics;

import arc.Core;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import heavyindustry.HVars;
import mindustry.graphics.MenuRenderer;

public class SpecialMenuRenderer extends MenuRenderer {
	public static final TextureRegion coverAlpha, coverBeta;

	static {
		coverAlpha = new TextureRegion(new Texture(HVars.internalTree.child("other/cover-alpha.png")));
		coverBeta = new TextureRegion(new Texture(HVars.internalTree.child("other/cover-beta.png")));
	}

	public boolean randomBoolean;
	public float scale;

	public SpecialMenuRenderer() {
		randomBoolean = Mathf.randomBoolean();
		scale = Math.max(Core.graphics.getWidth() / 1920f, Core.graphics.getHeight() / 1024f);
	}

	@Override
	public void render() {
		TextureRegion region = randomBoolean ? coverAlpha : coverBeta;

		Draw.color();
		Draw.rect(region,
				Core.graphics.getWidth() / 2f,
				Core.graphics.getHeight() / 2f,
				region.width * scale,
				region.height * scale);
		Draw.flush();
	}
}
