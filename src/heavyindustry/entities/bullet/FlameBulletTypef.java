package heavyindustry.entities.bullet;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

import java.util.Arrays;

public class FlameBulletTypef extends FlameBulletType {
	protected final Color tc = new Color(), tc2 = new Color();

	public Color[] colors = {Pal.lightFlame, Pal.darkFlame, Color.gray};
	public Color[] smokeColors = {};
	public float particleSpread = 10f, particleSizeScl = 1.5f;
	public int particleAmount = 8;

	protected Color[] hitColors;

	public FlameBulletTypef(float length, float cone, int number) {
		super(length, cone, number);
		pierce = true;
		lifetime = 12f;
		despawnEffect = Fx.none;
		status = StatusEffects.burning;
		statusDuration = 60f * 4f;
		hitSize = 7f;
		collidesAir = false;
		keepVelocity = false;
		hittable = false;
		layer = Layer.effect + 0.001f;
	}

	@Override
	public void init() {
		super.init();
		hitColors = Arrays.copyOf(colors, Math.max(1, colors.length - 1));
		shootEffect = new Effect(lifetime + 15f, range * 2f, e -> {
			Draw.color(tc.lerp(colors, e.fin()));
			tc2.set(tc).shiftSaturation(0.77f);

			Angles.randLenVectors(e.id, particleAmount, e.finpow() * (range + 15f), e.rotation, particleSpread, (x, y) -> {
				Fill.circle(e.x + x, e.y + y, 0.65f + e.fout() * particleSizeScl);
				Drawf.light(e.x + x, e.y + y, (0.65f + e.fout(Interp.pow4Out) * particleSizeScl) * 4f, tc2, 0.5f * e.fout(Interp.pow2Out));
			});
		}).layer(layer);
		if (smokeColors != null && smokeColors.length > 0) {
			smokeEffect = new Effect(lifetime * 3f, range * 2.25f, e -> {
				Draw.color(tc.lerp(smokeColors, e.fin()));

				float slope = (0.5f - Math.abs(e.fin(Interp.pow2InInverse) - 0.5f)) * 2f;

				Angles.randLenVectors(e.id, particleAmount, e.fin(Interp.pow5Out) * ((range * 1.125f) + 15f), e.rotation, particleSpread, (x, y) -> {
					Fill.circle(e.x + x, e.y + y, 0.65f + slope * particleSizeScl);
					Fill.circle(e.x + (x / 2f), e.y + (y / 2f), 0.5f + slope * (particleSizeScl / 2f));
				});
			}).followParent(false).layer(layer - 0.001f);
		}
		hitEffect = new Effect(14f, e -> {
			Draw.color(tc.lerp(hitColors, e.fin()));
			Lines.stroke(0.5f + e.fout());

			Angles.randLenVectors(e.id, particleAmount / 3, e.fin() * 15f, e.rotation, 50f, (x, y) -> {
				float ang = Mathf.angle(x, y);
				Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 3 + 1f);
			});
		});
	}

	@Override
	public void drawLight(Bullet b) {}
}
