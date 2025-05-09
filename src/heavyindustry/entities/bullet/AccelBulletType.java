package heavyindustry.entities.bullet;

import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.FloatSeq;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;

public class AccelBulletType extends BasicBulletType {
	public float velocityBegin = -1, velocityIncrease = 0f, accelerateBegin = 0.1f, accelerateEnd = 0.6f;

	public Interp accelInterp = Interp.linear;

	public AccelBulletType() {
		super();
	}

	public AccelBulletType(float velBeg, float velInc, Interp accInt, float dmg, String bulSpr) {
		super(1, dmg, bulSpr);
		velocityBegin = velBeg;
		velocityIncrease = velInc;
		accelInterp = accInt;
	}

	public AccelBulletType(float speed, float damage, String bulletSprite) {
		super(speed, damage, bulletSprite);
	}

	public AccelBulletType(float speed, float damage) {
		this(speed, damage, "bullet");
	}

	public AccelBulletType(float damage, String bulletSprite) {
		super(1, damage, bulletSprite);
	}

	public void disableAccel() {
		accelerateBegin = 10;
	}

	@Override
	protected float calculateRange() {
		if (velocityBegin < 0) velocityBegin = speed;

		boolean computeRange = rangeOverride < 0;
		float cal = 0;

		FloatSeq speeds = new FloatSeq();
		for (float i = 0; i <= 1; i += 0.05f) {
			float s = velocityBegin + accelInterp.apply(Mathf.curve(i, accelerateBegin, accelerateEnd)) * velocityIncrease;
			speeds.add(s);
			if (computeRange) cal += s * lifetime * 0.05f;
		}
		speed = speeds.sum() / speeds.size;

		if (computeRange) cal += 1;

		return cal;
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void update(Bullet b) {
		if (accelerateBegin < 1)
			b.vel.setLength((velocityBegin + accelInterp.apply(Mathf.curve(b.fin(), accelerateBegin, accelerateEnd)) * velocityIncrease) * (drag != 0 ? (1 * Mathf.pow(b.drag, b.fin() * b.lifetime() / 6)) : 1));
		super.update(b);
	}
}
