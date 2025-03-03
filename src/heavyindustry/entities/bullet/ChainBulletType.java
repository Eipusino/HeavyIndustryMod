package heavyindustry.entities.bullet;

import arc.func.Cons2;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Tmp;
import heavyindustry.graphics.PositionLightning;
import heavyindustry.util.Utils;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;

public class ChainBulletType extends BulletType {
	protected static final Seq<ChainBulletType> all = new Seq<>();

	protected static final Seq<Position> points = new Seq<>();
	protected static final Vec2 tmpVec = new Vec2();

	public int maxHit = 12;
	public float chainRange = 200f;
	public float length = 200f;
	public float thick = 2f;
	public int boltNum = 2;
	public boolean quietShoot = false;
	public Cons2<Position, Position> effectController = (f, t) -> {
		PositionLightning.createEffect(f, t, hitColor, boltNum, thick);
	};

	public ChainBulletType(float damage) {
		super(0.01f, damage);
		despawnEffect = Fx.none;
		instantDisappear = true;

		all.add(this);
	}

	@Override
	public void init() {
		super.init();

		drawSize = Math.max(drawSize, (length + chainRange) * 2f);
	}

	@Override
	protected float calculateRange() {
		if (rangeOverride > 0) return rangeOverride;
		else return chainRange + length;
	}

	@Override
	public void init(Bullet b) {
		Position target = Damage.linecast(b, b.x, b.y, b.rotation(), length);
		if (target == null) target = tmpVec.trns(b.rotation(), length).add(b);

		Position confirm = target;

		Units.nearbyEnemies(b.team, Tmp.r1.setSize(chainRange).setCenter(confirm.getX(), confirm.getY()), u -> {
			if (u.checkTarget(collidesAir, collidesGround) && u.targetable(b.team)) points.add(u);
		});

		if (collidesGround) {
			Vars.indexer.eachBlock(null, confirm.getX(), confirm.getY(), chainRange, t -> t.team != b.team, points::add);
		}

		if (!quietShoot || !points.isEmpty()) {
			Utils.shuffle(points);
			points.truncate(maxHit);
			points.insert(0, b);
			points.insert(1, target);

			for (int i = 1; i < points.size; i++) {
				Position from = points.get(i - 1), to = points.get(i);
				Position sureTarget = PositionLightning.findInterceptedPoint(from, to, b.team);
				effectController.get(from, sureTarget);

				lightningType.create(b, sureTarget.getX(), sureTarget.getY(), 0).damage(damage);
				hitEffect.at(sureTarget.getX(), sureTarget.getY(), hitColor);

				if (sureTarget instanceof Unit unit) unit.apply(status, statusDuration);

				if (sureTarget != to) break;
			}
		}

		points.clear();
		b.remove();
		b.vel.setZero();
	}

	@Override
	public void hit(Bullet b, float x, float y) {}

	@Override
	public void hit(Bullet b) {}

	@Override
	public void despawned(Bullet b) {}

	@Override
	public void drawLight(Bullet b) {}

	@Override
	public void handlePierce(Bullet b, float initialHealth, float x, float y) {}
}
