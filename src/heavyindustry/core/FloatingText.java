package heavyindustry.core;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mat;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.Group;
import arc.scene.event.Touchable;
import arc.scene.ui.layout.Scl;
import arc.util.Align;
import arc.util.Time;
import heavyindustry.math.Mathm;
import mindustry.Vars;
import mindustry.ui.Fonts;

public class FloatingText {
	protected static final Mat setMat = new Mat(), reMat = new Mat();
	protected static final Vec2 vec2 = new Vec2();

	protected final String title;

	protected FloatingText(String str) {
		title = str;
	}

	public void build(Group parent) {
		parent.fill((x, y, w, h) -> {
			TextureRegion logo = Core.atlas.find("logo");
			float width = Core.graphics.getWidth(), height = Core.graphics.getHeight() - Core.scene.marginTop;
			float logoScl = Scl.scl(1) * logo.scale;
			float logoWidth = Math.min(logo.width * logoScl, Core.graphics.getWidth() - Scl.scl(20));
			float logoHeight = logoWidth * (float) logo.height / logo.width;

			float fx = (int) (width / 2f);
			float fy = (int) (height - 6 - logoHeight) + logoHeight / 2 - (Core.graphics.isPortrait() ? Scl.scl(30f) : 0f);
			if (Core.settings.getBool("macnotch")) {
				fy -= Scl.scl(Vars.macNotchHeight);
			}

			float ex = fx + logoWidth / 3 - Scl.scl(1f), ey = fy - logoHeight / 3f - Scl.scl(2f);
			float ang = 12 + Mathf.sin(Time.time, 8, 2f);

			float dst = Mathf.dst(ex, ey, 0, 0);
			vec2.set(0, 0);
			float dx = Mathm.dx(0, dst, vec2.angleTo(ex, ey) + ang);
			float dy = Mathm.dy(0, dst, vec2.angleTo(ex, ey) + ang);

			reMat.set(Draw.trans());

			Draw.trans(setMat.setToTranslation(ex - dx, ey - dy).rotate(ang));
			Fonts.outline.draw(title, ex, ey, Color.yellow, Math.min(30f / title.length(), 1.5f) + Mathf.sin(Time.time, 8, 0.2f), false, Align.center);

			Draw.trans(reMat);
			Draw.reset();
		}).touchable = Touchable.disabled;
	}
}
