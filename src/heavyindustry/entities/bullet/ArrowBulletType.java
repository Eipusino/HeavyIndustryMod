package heavyindustry.entities.bullet;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;

public class ArrowBulletType extends BasicBulletType {
	public ArrowBulletType(float speed, float damage) {
		super(speed, damage);
		trailLength = 35;
	}

	@Override
	public void draw(Bullet b) {
		drawTrail(b);
		Tmp.v1.trns(b.rotation(), height / 2f);
		for (int s : Mathf.signs) {
			Tmp.v2.trns(b.rotation() - 90f, width * s, -height);
			Draw.color(backColor);
			Fill.tri(Tmp.v1.x + b.x, Tmp.v1.y + b.y, -Tmp.v1.x + b.x, -Tmp.v1.y + b.y, Tmp.v2.x + b.x, Tmp.v2.y + b.y);
			Draw.color(frontColor);
			Fill.tri(Tmp.v1.x / 2f + b.x, Tmp.v1.y / 2f + b.y, -Tmp.v1.x / 2f + b.x, -Tmp.v1.y / 2f + b.y, Tmp.v2.x / 2f + b.x, Tmp.v2.y / 2f + b.y);
		}
	}
}
