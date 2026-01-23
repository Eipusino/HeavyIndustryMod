package endfield.entities.bullet;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Position;
import arc.util.Tmp;
import endfield.graphics.Drawn;
import mindustry.entities.bullet.SapBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;

public class LaserSapBulletType extends SapBulletType {
	public boolean moveTarget = false;
	protected TextureRegion laser, laserEnd;

	@Override
	public void load() {
		super.load();

		laser = Core.atlas.find("laser-white");
		laserEnd = Core.atlas.find("laser-white-end");
	}

	@Override
	public void draw(Bullet b) {
		if (b.data instanceof Position pos) {
			if (moveTarget) {

			} else {
				Tmp.v1.set(pos).lerp(b, b.fin());
			}

			Draw.color(color);
			Drawn.laser(laser, laserEnd,
					b.x, b.y, Tmp.v1.x, Tmp.v1.y, width * b.fout());

			Draw.reset();

			Drawf.light(b.x, b.y, Tmp.v1.x, Tmp.v1.y, 15f * b.fout(), lightColor, lightOpacity);
		}
	}
}
