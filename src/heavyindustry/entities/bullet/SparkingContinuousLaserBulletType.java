package heavyindustry.entities.bullet;

import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.content.HFx;
import heavyindustry.entities.effect.ExtraEffect;
import heavyindustry.entities.HDamage;
import heavyindustry.entities.UnitPointEntry;
import heavyindustry.math.Mathm;
import heavyindustry.util.CollectionList;
import mindustry.content.StatusEffects;
import mindustry.entities.Fires;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;
import mindustry.type.StatusEffect;

public class SparkingContinuousLaserBulletType extends ContinuousLaserBulletType {
	public float coneRange = 1.1f;
	public boolean extinction;
	public float extinctionCone = 10f;
	public int extinctionRays = 35;

	public final CollectionList<Unit> tempList = new CollectionList<>(Unit.class);

	public StatusEffect status2 = StatusEffects.none;

	public SparkingContinuousLaserBulletType(float damage) {
		super(damage);
	}

	public SparkingContinuousLaserBulletType() {
		this(1f);
	}

	@Override
	public void update(Bullet b) {
		super.update(b);

		if (extinction) {
			if (b.timer(2, 15f)) {
				b.data = HDamage.castConeTile(b.x, b.y, length * coneRange, b.rotation(), extinctionCone, extinctionRays, (build, tile) -> {
					float angDelta = Mathf.clamp(1f - (Mathm.angleDist(Angles.angle(tile.worldx() - b.x, tile.worldy() - b.y), b.rotation()) / extinctionCone));
					float dst = Mathf.clamp(1f - (Mathf.dst(tile.worldx() - b.x, tile.worldy() - b.y) / (length * coneRange)));
					if (Mathf.chance(Interp.smooth.apply(angDelta) * 0.32f * Mathf.clamp(dst * 1.7f))) Fires.create(tile);
					//HFx.tilePosIndicatorTest.at(tile.worldx(), tile.worldy());
					if (build != null && build.team != b.team) {
						build.damage(Interp.smooth.apply(angDelta) * 23.3f * Mathf.clamp(dst * 1.7f));
						ExtraEffect.addMoltenBlock(build);
					}
				}, tile -> tile.build != null && tile.build.team != b.team && (tile.block().absorbLasers || tile.block().insulated));
			}

			if (b.data instanceof float[] data) {
				HDamage.castCone(b.x, b.y, length * coneRange, b.rotation(), extinctionCone, (unit, dst, angDelta) -> {
					float clamped = Mathf.clamp(dst * 1.7f);

					int idx = Mathf.clamp(Mathf.round(((Mathm.angleDistSigned(b.angleTo(unit), b.rotation()) + extinctionCone) / 140f) * (data.length - 1)), 0, data.length - 1);

					if (unit.team != b.team && unit.hittable() && ((b.dst2(unit) + (unit.hitSize / 2f)) < data[idx] || unit.isFlying())) {
						if (!unit.dead) {
							unit.damage(28f * angDelta * dst * Time.delta);
							Tmp.v1.trns(Angles.angle(b.x, b.y, unit.x, unit.y), angDelta * clamped * 160f * (unit.hitSize / 20f + 0.95f));
							unit.impulse(Tmp.v1);
							if (Mathf.chanceDelta(Mathf.clamp(angDelta * dst * 12f) * 0.9f))
								ExtraEffect.createEvaporation(unit.x, unit.y, (angDelta * dst) / extinctionCone, unit, b.owner);
							unit.apply(status2, angDelta * 3800.3f * clamped);
							unit.apply(status, angDelta * 240.3f * clamped);
						} else {
							tempList.add(unit);
							Tmp.v1.trns(Angles.angle(b.x, b.y, unit.x, unit.y), angDelta * clamped * 130f / Math.max(unit.mass() / 120f + 119f / 120f, 1f));
							Tmp.v1.scl(12f);
							HFx.evaporateDeath.at(unit.x, unit.y, 0f, new UnitPointEntry(unit, Tmp.v1.cpy()));
							//for (int i = 0; i < 12; i++) ExtraEffect.createEvaporation(unit.x, unit.y, unit, b.owner);
							for (int i = 0; i < 12; i++) {
								Tmp.v1.trns(Angles.angle(b.x, b.y, unit.x(), unit.y()), 65f + Mathf.range(0.3f)).add(unit);
								Tmp.v2.trns(Mathf.random(360f), Mathf.random(unit.hitSize / 1.25f));
								HFx.vaporation.at(unit.x, unit.y, 0f, new Position[]{unit, Tmp.v1.cpy(), Tmp.v2.cpy()});
							}
						}
					}
				});
				tempList.each(Unitc::remove);
				tempList.clear();
			}
		}
	}
}
