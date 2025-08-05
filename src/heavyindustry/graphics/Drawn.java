package heavyindustry.graphics;

import arc.Core;
import arc.func.Floatc2;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.GlyphLayout;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Mat3D;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.scene.actions.Actions;
import arc.scene.event.Touchable;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Scl;
import arc.util.Align;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import heavyindustry.content.HFx;
import heavyindustry.math.HInterps;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Player;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.ui.Fonts;

import static heavyindustry.HVars.name;
import static mindustry.Vars.headless;
import static mindustry.Vars.net;
import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

public final class Drawn {
	public static final int[] oneArr = {1};
	public static final float sinScl = 1f;
	public static final float[] v = new float[6];

	static final Vec3[] tmpV = new Vec3[4];
	static final Mat3D matT = new Mat3D();
	static final Vec3 tAxis = new Vec3();
	static final Vec3 tmpV2 = new Vec3();
	static final TextureRegion t1 = new TextureRegion(), t2 = new TextureRegion();

	static final TextureRegion nRegion = new TextureRegion();

	static final Vec2
			v1 = new Vec2(), v2 = new Vec2(), v3 = new Vec2(), v4 = new Vec2(), v5 = new Vec2(),
			v6 = new Vec2(), v7 = new Vec2(), v8 = new Vec2(), v9 = new Vec2(), v10 = new Vec2(),
			v11 = new Vec2(), v12 = new Vec2(), v13 = new Vec2(),
			rv = new Vec2();

	static final Vec3
			v31 = new Vec3(), v32 = new Vec3(), v33 = new Vec3(), v34 = new Vec3(), v35 = new Vec3(),
			v36 = new Vec3(), v37 = new Vec3(), v38 = new Vec3(), v39 = new Vec3(), v310 = new Vec3();

	static final Color
			c1 = new Color(), c2 = new Color(), c3 = new Color(), c4 = new Color(), c5 = new Color(),
			c6 = new Color(), c7 = new Color(), c8 = new Color(), c9 = new Color(), c10 = new Color();

	static final Rand rand = new Rand();

	static {
		for (int i = 0; i < tmpV.length; i++) {
			tmpV[i] = new Vec3();
		}
	}

	/** Don't let anyone instantiate this class. */
	private Drawn() {}

	public static void drawSnow(float x, float y, float rad, float rot, Color color) {
		Draw.color(color);
		for (int i = 0; i < 6; i++) {
			float angle = 60 * i + rot;
			Drawf.tri(x + Angles.trnsx(angle, rad), y + Angles.trnsy(angle, rad), rad / 3, rad, angle - 180);
			Drawf.tri(x + Angles.trnsx(angle, rad), y + Angles.trnsy(angle, rad), rad / 3, rad / 4, angle);
		}
		Draw.reset();
	}

	public static void circlePercent(float x, float y, float rad, float percent, float angle) {
		float p = Mathf.clamp(percent);

		int sides = Lines.circleVertices(rad);

		float space = 360f / (float) sides;
		float len = 2 * rad * Mathf.sinDeg(space / 2);
		float hstep = Lines.getStroke() / 2f / Mathf.cosDeg(space / 2f);
		float r1 = rad - hstep;
		float r2 = rad + hstep;

		int i;

		for (i = 0; i < sides * p - 1; ++i) {
			float a = space * (float) i + angle;
			float cos = Mathf.cosDeg(a);
			float sin = Mathf.sinDeg(a);
			float cos2 = Mathf.cosDeg(a + space);
			float sin2 = Mathf.sinDeg(a + space);
			Fill.quad(x + r1 * cos, y + r1 * sin, x + r1 * cos2, y + r1 * sin2, x + r2 * cos2, y + r2 * sin2, x + r2 * cos, y + r2 * sin);
		}

		float a = space * i + angle;
		float cos = Mathf.cosDeg(a);
		float sin = Mathf.sinDeg(a);
		float cos2 = Mathf.cosDeg(a + space);
		float sin2 = Mathf.sinDeg(a + space);
		float f = sides * p - i;
		v6.trns(a, 0, len * (f - 1));
		Fill.quad(x + r1 * cos, y + r1 * sin, x + r1 * cos2 + v6.x, y + r1 * sin2 + v6.y, x + r2 * cos2 + v6.x, y + r2 * sin2 + v6.y, x + r2 * cos, y + r2 * sin);
	}

	public static void circlePercentFlip(float x, float y, float rad, float in, float scl) {
		float f = Mathf.cos(in % (scl * 3f), scl, 1.1f);
		circlePercent(x, y, rad, f > 0 ? f : -f, in + -90 * Mathf.sign(f));
	}

	public static void link(Building from, Building to, Color color) {
		float sin = Mathf.absin(Time.time * sinScl, 6f, 1f);
		float r1 = from.block.size / 2f * tilesize + sin;
		float x1 = from.getX(), x2 = to.getX(), y1 = from.getY(), y2 = to.getY();
		float r2 = to.block.size / 2f * tilesize + sin;

		Draw.color(color);

		Lines.square(x2, y2, to.block.size * tilesize / 2f + 1f);

		Tmp.v1.trns(from.angleTo(to), r1);
		Tmp.v2.trns(to.angleTo(from), r2);
		int signs = (int) (from.dst(to) / tilesize);

		Lines.stroke(4, Pal.gray);
		Lines.dashLine(x1 + Tmp.v1.x, y1 + Tmp.v1.y, x2 + Tmp.v2.x, y2 + Tmp.v2.y, signs);
		Lines.stroke(2, color);
		Lines.dashLine(x1 + Tmp.v1.x, y1 + Tmp.v1.y, x2 + Tmp.v2.x, y2 + Tmp.v2.y, signs);

		Drawf.arrow(x1, y1, x2, y2, from.block.size * tilesize / 2f + sin, 4 + sin, color);

		Drawf.circles(x2, y2, r2, color);
	}

	public static float cameraDstScl(float x, float y, float norDst) {
		v6.set(Core.camera.position);
		float dst = Mathf.dst(x, y, v6.x, v6.y);
		return 1 - Mathf.clamp(dst / norDst);
	}

	public static void tri(float x, float y, float width, float length, float angle) {
		float wx = Angles.trnsx(angle + 90, width), wy = Angles.trnsy(angle + 90, width);
		Fill.tri(x + wx, y + wy, x - wx, y - wy, Angles.trnsx(angle, length) + x, Angles.trnsy(angle, length) + y);
	}

	public static void arrow(float x, float y, float width, float length, float backLength, float angle) {
		float wx = Angles.trnsx(angle + 90, width), wy = Angles.trnsy(angle + 90, width);
		float ox = Angles.trnsx(angle, backLength), oy = Angles.trnsy(angle, backLength);
		float cx = Angles.trnsx(angle, length) + x, cy = Angles.trnsy(angle, length) + y;
		Fill.tri(x + ox, y + oy, x - wx, y - wy, cx, cy);
		Fill.tri(x + wx, y + wy, x + ox, y + oy, cx, cy);
	}

	public static void surround(long id, float x, float y, float rad, int num, float innerSize, float outerSize, float interp) {
		Rand rand = HFx.rand0;

		rand.setSeed(id);
		for (int i = 0; i < num; i++) {
			float len = rad * rand.random(0.75f, 1.5f);
			v6.trns(rand.random(360f) + rand.range(2f) * (1.5f - Mathf.curve(len, rad * 0.75f, rad * 1.5f)) * Time.time, len);
			float angle = v6.angle();
			v6.add(x, y);
			tri(v6.x, v6.y, (interp + 1) * outerSize + rand.random(0, outerSize / 8), outerSize * (Interp.exp5In.apply(interp) + 0.25f) / 2f, angle);
			tri(v6.x, v6.y, (interp + 1) / 2 * innerSize + rand.random(0, innerSize / 8), innerSize * (Interp.exp5In.apply(interp) + 0.5f), angle - 180);
		}
	}

	public static void randLenVectors(long seed, int amount, float length, float minLength, float angle, float range, Floatc2 cons) {
		rand.setSeed(seed);
		for (int i = 0; i < amount; i++) {
			v6.trns(angle + rand.range(range), minLength + rand.random(length));
			cons.get(v6.x, v6.y);
		}
	}

	public static float rotator_90(float in, float margin) {
		return 90 * HInterps.pow10.apply(Mathf.curve(in, margin, 1 - margin));
	}

	public static float rotator_90() {
		return 90 * Interp.pow5.apply(Mathf.curve(cycle_100(), 0.15f, 0.85f));
	}

	public static float rotator_120(float in, float margin) {
		return 120 * HInterps.pow10.apply(Mathf.curve(in, margin, 1 - margin));
	}

	public static float rotator_180() {
		return 180 * Interp.pow5.apply(Mathf.curve(cycle_100(), 0.15f, 0.85f));
	}

	public static float rotator_360() {
		return 360 * Interp.pow5.apply(Mathf.curve(cycle(0, 270), 0.15f, 0.85f));
	}

	/** @return AN interpolation in (0, 1) */
	public static float cycle(float phaseOffset, float T) {
		return (Time.time + phaseOffset) % T / T;
	}

	public static float cycle(float in, float phaseOffset, float T) {
		return (in + phaseOffset) % T / T;
	}

	public static float cycle_100() {
		return Time.time % 100 / 100;
	}

	public static void basicLaser(float x, float y, float x2, float y2, float stroke, float circleScl) {
		Lines.stroke(stroke);
		Lines.line(x, y, x2, y2, false);
		Fill.circle(x, y, stroke * circleScl);
		Fill.circle(x2, y2, stroke * circleScl);
		Lines.stroke(1f);
	}

	public static void basicLaser(float x, float y, float x2, float y2, float stroke) {
		basicLaser(x, y, x2, y2, stroke, 0.95f);
	}

	public static void randFadeLightningEffect(float x, float y, float range, float lightningPieceLength, Color color, boolean in) {
		randFadeLightningEffectScl(x, y, range, 0.55f, 1.1f, lightningPieceLength, color, in);
	}

	public static void randFadeLightningEffectScl(float x, float y, float range, float sclMin, float sclMax, float lightningPieceLength, Color color, boolean in) {
		v6.rnd(range).scl(Mathf.random(sclMin, sclMax)).add(x, y);
		(in ? HFx.chainLightningFadeReversed : HFx.chainLightningFade).at(x, y, lightningPieceLength, color, v6.cpy());
	}

	public static void teleportUnitNet(Unit before, float x, float y, float angle, Player player) {
		if (net.active() || headless) {
			if (player != null) {
				player.set(x, y);
				player.snapInterpolation();
				player.snapSync();
				player.lastUpdated = player.updateSpacing = 0;
			}
			before.set(x, y);
			before.snapInterpolation();
			before.snapSync();
			before.updateSpacing = 0;
			before.lastUpdated = 0;
		} else {
			before.set(x, y);
		}
		before.rotation = angle;
	}

	public static void construct(Building t, TextureRegion region, Color color1, Color color2, float rotation, float progress, float alpha, float time) {
		construct(t, region, color1, color2, rotation, progress, alpha, time, t.block.size * tilesize - 4f);
	}

	public static void construct(Building t, TextureRegion region, Color color1, Color color2, float rotation, float progress, float alpha, float time, float size) {
		Shaders.build.region = region;
		Shaders.build.progress = progress;
		Shaders.build.color.set(color1);
		Shaders.build.color.a = alpha;
		Shaders.build.time = -time / 20f;

		Draw.shader(Shaders.build);
		Draw.rect(region, t.x, t.y, rotation);
		Draw.shader();

		Draw.color(color2);
		Draw.alpha(alpha);

		Lines.lineAngleCenter(t.x + Mathf.sin(time, 20f, size / 2f), t.y, 90, size);

		Draw.reset();
	}

	/*public static void selected(Buildingc tile, Color color) {
		Drawf.selected(tile.tile(), color);
	}*/

	public static void posSquareLink(Color color, float stroke, float size, boolean drawBottom, float x, float y, float x2, float y2) {
		posSquareLink(color, stroke, size, drawBottom, v6.set(x, y), v6.set(x2, y2));
	}

	public static void posSquareLink(Color color, float stroke, float size, boolean drawBottom, Position from, Position to) {
		posSquareLinkArr(color, stroke, size, drawBottom, false, from, to);
	}

	public static void posSquareLinkArr(Color color, float stroke, float size, boolean drawBottom, boolean linkLine, Position... pos) {
		if (pos.length < 2 || (!linkLine && pos[0] == null)) return;

		for (int c : drawBottom ? Mathf.signs : oneArr) {
			for (int i = 1; i < pos.length; i++) {
				if (pos[i] == null) continue;
				Position p1 = pos[i - 1], p2 = pos[i];
				Lines.stroke(stroke + 1 - c, c == 1 ? color : Pal.gray);
				if (linkLine) {
					if (p1 == null) continue;
					Lines.line(p2.getX(), p2.getY(), p1.getX(), p1.getY());
				} else {
					Lines.line(p2.getX(), p2.getY(), pos[0].getX(), pos[0].getY());
				}
				Draw.reset();
			}

			for (Position p : pos) {
				if (p == null) continue;
				Draw.color(c == 1 ? color : Pal.gray);
				Fill.square(p.getX(), p.getY(), size + 1 - c / 1.5f, 45);
				Draw.reset();
			}
		}
	}

	public static void drawText(String text, float x, float y) {
		drawText(text, x, y, 1f);
	}

	public static void drawText(String text, float x, float y, float size) {
		Font font = Fonts.outline;
		GlyphLayout layout = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
		boolean ints = font.usesIntegerPositions();
		font.setUseIntegerPositions(false);
		font.getData().setScale(size / 6f / Scl.scl(1f));

		layout.setText(font, text);
		font.draw(text, x, y, Align.center);

		font.setUseIntegerPositions(ints);
		font.setColor(Color.white);
		font.getData().setScale(1f);
		Draw.reset();
		Pools.free(layout);
	}

	public static void drawConnected(float x, float y, float size, Color color) {
		Draw.reset();
		float sin = Mathf.absin(Time.time * sinScl, 8f, 1.25f);

		for (int i = 0; i < 4; i++) {
			float length = size / 2f + 3 + sin;
			Tmp.v1.trns(i * 90, -length);
			Draw.color(Pal.gray);
			Draw.rect(Core.atlas.find(name("linked-arrow-back")), x + Tmp.v1.x, y + Tmp.v1.y, i * 90);
			Draw.color(color);
			Draw.rect(Core.atlas.find(name("linked-arrow")), x + Tmp.v1.x, y + Tmp.v1.y, i * 90);
		}
		Draw.reset();
	}

	public static void overlayText(String text, float x, float y, float offset, Color color, boolean underline) {
		overlayText(Fonts.outline, text, x, y, offset, 1, 0.25f, color, underline, false);
	}

	public static void overlayText(Font font, String text, float x, float y, float offset, float offsetScl, float size, Color color, boolean underline, boolean align) {
		GlyphLayout layout = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
		boolean ints = font.usesIntegerPositions();
		font.setUseIntegerPositions(false);
		font.getData().setScale(size / Scl.scl(1f));
		layout.setText(font, text);
		font.setColor(color);

		float dy = offset + 3f;
		font.draw(text, x, y + layout.height / (align ? 2 : 1) + (dy + 1f) * offsetScl, 1);
		--dy;

		if (underline) {
			Lines.stroke(2f, Color.darkGray);
			Lines.line(x - layout.width / 2f - 2f, dy + y, x + layout.width / 2f + 1.5f, dy + y);
			Lines.stroke(1f, color);
			Lines.line(x - layout.width / 2f - 2f, dy + y, x + layout.width / 2f + 1.5f, dy + y);
			Draw.color();
		}

		font.setUseIntegerPositions(ints);
		font.setColor(Color.white);
		font.getData().setScale(1f);
		Draw.reset();
		Pools.free(layout);
	}

	public static void shiningCircle(int seed, float time, float x, float y, float radius, int spikes, float spikeDuration, float spikeWidth, float spikeHeight) {
		shiningCircle(seed, time, x, y, radius, spikes, spikeDuration, spikeWidth, spikeHeight, 0f);
	}

	public static void shiningCircle(int seed, float time, float x, float y, float radius, int spikes, float spikeDuration, float spikeWidth, float spikeHeight, float angleDrift) {
		shiningCircle(seed, time, x, y, radius, spikes, spikeDuration, 0f, spikeWidth, spikeHeight, angleDrift);
	}

	public static void shiningCircle(int seed, float time, float x, float y, float radius, int spikes, float spikeDuration, float durationRange, float spikeWidth, float spikeHeight, float angleDrift) {
		Fill.circle(x, y, radius);
		spikeWidth = Math.min(spikeWidth, 90f);
		int idx;

		for (int i = 0; i < spikes; i++) {
			float d = spikeDuration * (durationRange > 0f ? Mathf.randomSeed((seed + i) * 41L, 1f - durationRange, 1f + durationRange) : 1f);
			float timeOffset = Mathf.randomSeed((seed + i) * 314L, 0f, d);
			int timeSeed = Mathf.floor((time + timeOffset) / d);

			float fin = ((time + timeOffset) % d) / d;
			float fslope = (0.5f - Math.abs(fin - 0.5f)) * 2f;
			float angle = Mathf.randomSeed(Math.max(timeSeed, 1) + ((i + seed) * 245L), 360f);

			if (fslope > 0.0001f) {
				idx = 0;
				float drift = angleDrift > 0 ? Mathf.randomSeed(Math.max(timeSeed, 1) + ((i + seed) * 162L), -angleDrift, angleDrift) * fin : 0f;
				for (int j = 0; j < 3; j++) {
					float angB = (j * spikeWidth - (2f) * spikeWidth / 2f) + angle;
					Tmp.v1.trns(angB + drift, radius + (j == 1 ? (spikeHeight * fslope) : 0f)).add(x, y);
					v[idx++] = Tmp.v1.x;
					v[idx++] = Tmp.v1.y;
				}

				Fill.tri(v[0], v[1], v[2], v[3], v[4], v[5]);
			}
		}
	}

	public static void snowFlake(float x, float y, float r, float s) {
		for (int i = 0; i < 3; i++) {
			Lines.lineAngleCenter(x, y, r + 60 * i, s);
		}
	}

	public static void spark(float x, float y, float w, float h, float r) {
		for (int i = 0; i < 4; i++) {
			Drawf.tri(x, y, w, h, r + 90 * i);
		}
	}

	public static void drawHeat(TextureRegion reg, float x, float y, float rot, float temp) {
		float a;
		if (temp > 273.15f) {
			a = Math.max(0f, (temp - 498f) * 0.001f);
			if (a < 0.01f) return;
			if (a > 1f) {
				Color fCol = Pal.turretHeat.cpy().add(0, 0, 0.01f * a);
				fCol.mul(a);
				Draw.color(fCol, a);
			} else {
				Draw.color(Pal.turretHeat, a);
			}
		} else {
			a = 1f - Mathf.clamp(temp / 273.15f);
			if (a < 0.01f) return;
			Draw.color(HPal.coldcolor, a);
		}
		Draw.blend(Blending.additive);
		Draw.rect(reg, x, y, rot);
		Draw.blend();
		Draw.color();
	}

	public static void drawSlideRect(TextureRegion region, float x, float y, float w, float h, float tw, float th, float rot, int step, float offset) {
		if (region == null) return;
		nRegion.set(region);

		float scaleX = w / tw;
		float texW = nRegion.u2 - nRegion.u;

		nRegion.u += Mathf.map(offset % 1, 0f, 1f, 0f, texW * step / tw);
		nRegion.u2 = nRegion.u + scaleX * texW;
		Draw.rect(nRegion, x, y, w, h, w * 0.5f, h * 0.5f, rot);
	}

	static float getypos(float d, float r, float h) {
		float c1 = Mathf.pi * r;
		if (d < c1) {
			return r * (1f - Mathf.sinDeg(180f * d / c1));
		} else if (d > c1 + h - r * 2) {
			return (h - r) + r * (Mathf.sinDeg(180 * (d - (c1 + h - r * 2)) / c1));
		} else {
			return d - c1 + r;
		}
	}

	public static void drawTread(TextureRegion region, float x, float y, float w, float h, float r, float rot, float d1, float d2) {
		float c1 = Mathf.pi * r;
		float cut1 = c1 * 0.5f;
		float cut2 = c1 * 1.5f + h - r * 2;
		if (d1 < cut1 && d2 < cut1) {
			return;
		}//cant be seen
		if (d1 > cut2 && d2 > cut2) {
			return;
		}//cant be seen

		float y1 = getypos(d1, r, h) - h * 0.5f;
		float y2 = getypos(d2, r, h) - h * 0.5f;
		TextureRegion reg = region;
		if (d1 < cut1) {
			y1 = -h * 0.5f;
			nRegion.set(region);
			nRegion.v2 = Mathf.map(cut1, d1, d2, nRegion.v2, nRegion.v);
			reg = nRegion;
		}

		if (d2 > cut2) {
			y2 = h * 0.5f;
			nRegion.set(region);
			nRegion.v = Mathf.map(cut2, d1, d2, nRegion.v2, nRegion.v);
			reg = nRegion;
		}

		Draw.rect(reg, x, y + (y1 + y2) * 0.5f, w, y2 - y1, w * 0.5f, -y1, rot);

	}

	public static void drawRotRect(TextureRegion region, float x, float y, float w, float rot_h, float true_h, float rot, float ang1, float ang2) {
		if (region == null || !Core.settings.getBool("effects")) return;
		float amod1 = Mathf.mod(ang1, 360f);
		float amod2 = Mathf.mod(ang2, 360f);
		if (amod1 >= 180f && amod2 >= 180f) return;

		nRegion.set(region);
		float uy1 = nRegion.v;
		float uy2 = nRegion.v2;
		float uCenter = (uy1 + uy2) / 2f;
		float uSize = (uy2 - uy1) * rot_h / true_h * 0.5f;
		uy1 = uCenter - uSize;
		uy2 = uCenter + uSize;
		nRegion.v = uy1;
		nRegion.v2 = uy2;

		float s1 = -Mathf.cos(ang1 * Mathf.degreesToRadians);
		float s2 = -Mathf.cos(ang2 * Mathf.degreesToRadians);
		if (amod1 > 180f) {
			nRegion.v2 = Mathf.map(0f, amod1 - 360f, amod2, uy2, uy1);
			s1 = -1f;
		} else if (amod2 > 180f) {
			nRegion.v = Mathf.map(180f, amod1, amod2, uy2, uy1);
			s2 = 1f;
		}
		s1 = Mathf.map(s1, -1f, 1f, y - rot_h / 2f, y + rot_h / 2f);
		s2 = Mathf.map(s2, -1f, 1f, y - rot_h / 2f, y + rot_h / 2f);
		Draw.rect(nRegion, x, (s1 + s2) * 0.5f, w, s2 - s1, w * 0.5f, y - s1, rot);
	}

	public static void line(Color color, float x, float y, float x2, float y2) {
		Lines.stroke(3f, Pal.gray);
		Lines.line(x, y, x2, y2);
		Lines.stroke(1f, color);
		Lines.line(x, y, x2, y2);
		Draw.reset();
	}

	public static void arc(float x, float y, float r, float fromRadian, float toRadian) {
		int seg = (int) Math.max(1, Lines.circleVertices(r) * Math.abs(toRadian - fromRadian) / (2 * Mathf.pi));
		Vec2 ptop = new Vec2(), pbottom = new Vec2();
		Vec2 ctop = new Vec2(), cbottom = new Vec2();
		float c = Mathf.cos(fromRadian);
		float s = Mathf.sin(fromRadian);
		float thick = Lines.getStroke() * 0.5f;
		ptop.set(c * (r + thick) + x, s * (r + thick) + y);
		pbottom.set(c * (r - thick) + x, s * (r - thick) + y);
		for (int i = 0; i < seg; i++) {
			float t = Mathf.lerp(fromRadian, toRadian, (i + 1f) / seg);
			c = Mathf.cos(t);
			s = Mathf.sin(t);
			ctop.set(c * (r + thick) + x, s * (r + thick) + y);
			cbottom.set(c * (r - thick) + x, s * (r - thick) + y);
			Fill.quad(Core.atlas.white(), ptop.x, ptop.y, ctop.x, ctop.y, cbottom.x, cbottom.y, pbottom.x, pbottom.y);
			ptop.set(ctop);
			pbottom.set(cbottom);
		}

	}

	public static void mulVec(float[] mat, Vec3 vec) {
		float x = vec.x * mat[Mat3D.M00] + vec.y * mat[Mat3D.M01] + vec.z * mat[Mat3D.M02] + mat[Mat3D.M03];
		float y = vec.x * mat[Mat3D.M10] + vec.y * mat[Mat3D.M11] + vec.z * mat[Mat3D.M12] + mat[Mat3D.M13];
		float z = vec.x * mat[Mat3D.M20] + vec.y * mat[Mat3D.M21] + vec.z * mat[Mat3D.M22] + mat[Mat3D.M23];
		vec.x = x;
		vec.y = y;
		vec.z = z;
	}

	public static void drawRectOrtho(TextureRegion region, float x, float y, float z, float w, float h, float rotY, float rotZ) {
		drawRectOrtho(region, x, y, 0, 0, z, w, h, rotY, rotZ);
	}

	public static void drawRectOrtho(TextureRegion region, float x, float y, float ox, float oy, float z, float w, float h, float rotY, float rotZ) {
		drawRectOrtho(region, x, y, ox, oy, z, w, h, rotY, rotZ, (w != h) ? rotZ : 0);
	}

	public static void drawRectOrtho(TextureRegion region, float x, float y, float ox, float oy, float z, float w, float h, float rotY, float rotZ, float sprrotZ) {
		tmpV[3].set(+w * 0.5f, +h * 0.5f, 0);
		tmpV[0].set(-w * 0.5f, +h * 0.5f, 0);
		tmpV[1].set(-w * 0.5f, -h * 0.5f, 0);
		tmpV[2].set(+w * 0.5f, -h * 0.5f, 0);

		tmpV2.set(ox, oy, z);
		matT.idt();
		for (int i = 0; i < 4; i++) {
			tmpV[i].rotate(Vec3.Z, sprrotZ);
		}
		tAxis.set(Vec3.Y).rotate(Vec3.Z, -rotZ);
		tmpV2.rotate(Vec3.Z, -rotZ);
		matT.rotate(tAxis, -rotY);
		matT.translate(tmpV2);

		for (int i = 0; i < 4; i++) {
			mulVec(matT.val, tmpV[i]);
			tmpV[i].add(x, y, 0);
		}

		Fill.quad(region, tmpV[0].x, tmpV[0].y, tmpV[1].x, tmpV[1].y, tmpV[2].x, tmpV[2].y, tmpV[3].x, tmpV[3].y);
	}

	public static void drawRectOffsetHorz(TextureRegion region, float x, float y, float w, float h, float rotation, float o) {
		t1.set(region);
		t2.set(region);
		float cx = x + w * 0.5f;
		float dx = x - w * 0.5f + w * o;
		float t1w = w * (1f - o);
		float t2w = w * o;
		t1.u2 = Mathf.lerp(region.u, region.u2, 1 - o);
		t2.u = Mathf.lerp(region.u2, region.u, o);
		Draw.rect(t1, dx + t1w * 0.5f, y, t1w, h, x - dx, h * 0.5f, rotation);
		Draw.rect(t2, dx - t2w * 0.5f, y, t2w, h, x - (dx - t2w), h * 0.5f, rotation);
	}

	/** @author sunny */
	public static void ring(float bx, float by, int sides, float rad, float hScl, float rot, float thickness, float layerUnder, float layerOver) {
		float wScl = 1f;

		float l = Lines.getStroke();

		float sign = Mathf.sign(hScl);
		hScl = Math.abs(hScl);
		Tmp.v1.trns(rot + 90, sign * thickness * (1 - hScl));
		hScl = Math.abs(hScl);

		float space = 360 / (float) sides;
		float r1 = rad - l / 2, r2 = rad + l / 2;

		for (int i = 0; i < sides; i++) {
			float a = space * i;
			boolean over = i >= sides / 2 == sign > 0;

			Draw.z(!over ? layerUnder : layerOver);
			v10.trns(rot, r1 * wScl * Mathf.cosDeg(a), r1 * hScl * Mathf.sinDeg(a));
			v11.trns(rot, r1 * wScl * Mathf.cosDeg(a + space), r1 * hScl * Mathf.sinDeg(a + space));
			v12.trns(rot, r2 * wScl * Mathf.cosDeg(a + space), r2 * hScl * Mathf.sinDeg(a + space));
			v13.trns(rot, r2 * wScl * Mathf.cosDeg(a), r2 * hScl * Mathf.sinDeg(a));

			float x = bx + Tmp.v1.x;
			float y = by + Tmp.v1.y;

			if (over) {
				//over, use 12
				Draw.color(Color.red);
				Fill.quad(bx - Tmp.v1.x + v13.x, by - Tmp.v1.y + v13.y, bx - Tmp.v1.x + v12.x, by - Tmp.v1.y + v12.y, x + v12.x, y + v12.y, x + v13.x, y + v13.y);
			} else {
				//under, use 34
				Draw.color(Color.orange);
				Fill.quad(bx - Tmp.v1.x + v11.x, by - Tmp.v1.y + v11.y, bx - Tmp.v1.x + v10.x, by - Tmp.v1.y + v10.y, x + v10.x, y + v10.y, x + v11.x, y + v11.y);

			}

			Draw.z(!over ? layerUnder : layerOver);
			Draw.color(Color.white);
			Fill.quad(x + v10.x, y + v10.y, x + v11.x, y + v11.y, x + v12.x, y + v12.y, x + v13.x, y + v13.y);
		}
		Draw.reset();
	}

	public static void light(float x, float y, TextureRegion region, float rotation, Color color, float opacity, boolean flip) {
		float res = color.toFloatBits();
		renderer.lights.add(() -> {
			Draw.color(res);
			Draw.alpha(opacity);
			Draw.rect(region, x, y, region.width / 4f * Mathf.sign(flip), region.height / 4f, rotation);
		});
	}

	public static void plus(float x, float y, float diameter, float angle) {
		plus(x, y, diameter / 3f, diameter, angle);
	}

	public static void plus(float x, float y, float stroke, float diameter, float angle) {
		for (int i = 0; i < 2; i++) {
			Fill.rect(x, y, stroke, diameter, angle + i * 90f);
		}
	}

	public static void cross(float x, float y, float width, float length, float angle) {
		for (int i = 0; i < 4; i++) {
			Drawf.tri(x, y, width, length, i * 90f + angle);
		}
	}

	public static void cross(float x, float y, float size, float angle) {
		cross(x, y, size, size, angle);
	}

	public static void shadow(TextureRegion texture, float x, float y, float rotation, float alpha) {
		Draw.color(Tmp.c1.set(Pal.shadow).mulA(alpha));
		Draw.rect(texture, x, y, rotation);
		Draw.color();
	}

	public static void vecLine(float x, float y, Vec2 v1, Vec2 v2, boolean cap) {
		Lines.line(v1.x + x, v1.y + y, v2.x + x, v2.y + y, cap);
	}

	public static void lineAngleCenter(float x, float y, float angle, float length, boolean cap) {
		v1.trns(angle, length);

		Lines.line(x - v1.x / 2, y - v1.y / 2, x + v1.x / 2, y + v1.y / 2, cap);
	}

	public static void pill(float x, float y, float angle, float length, float width) {
		Lines.stroke(width);
		lineAngleCenter(x, y, angle, length - width, false);

		for (int i = 0; i < 2; i++) {
			v1.trns(angle + 180f * i, length / 2f - width / 2f);
			Fill.circle(x + v1.x, y + v1.y, width / 2f);
		}
	}

	public static void baseTri(float x, float y, float b, float h, float rot) {
		v1.trns(rot, h).add(x, y);
		v2.trns(rot - 90f, b / 2f).add(x, y);
		v3.trns(rot + 90f, b / 2f).add(x, y);

		Fill.tri(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y);
	}

	public static void progressRing(float x, float y, float rad1, float rad2, float progress) {
		if (Math.abs(rad1 - rad2) > 0.01f) {
			int sides = (int) (Lines.circleVertices(Math.max(rad1, rad2)) * progress);
			float space = 360f * progress / sides;

			for (int i = 0; i < sides; i++) {
				float a = 90f - space * i, cos = Mathf.cosDeg(a), sin = Mathf.sinDeg(a), cos2 = Mathf.cosDeg(a - space), sin2 = Mathf.sinDeg(a - space);
				Fill.quad(x + rad1 * cos, y + rad1 * sin, x + rad1 * cos2, y + rad1 * sin2, x + rad2 * cos2, y + rad2 * sin2, x + rad2 * cos, y + rad2 * sin);
			}
		}
	}

	public static void ring(float x, float y, float rad1, float rad2) {
		if (Math.abs(rad1 - rad2) > 0.01f) {
			int sides = Lines.circleVertices(Math.max(rad1, rad2));
			float space = 360f / sides;

			for (int i = 0; i < sides; i++) {
				float a = space * i, cos = Mathf.cosDeg(a), sin = Mathf.sinDeg(a), cos2 = Mathf.cosDeg(a + space), sin2 = Mathf.sinDeg(a + space);
				Fill.quad(x + rad1 * cos, y + rad1 * sin, x + rad1 * cos2, y + rad1 * sin2, x + rad2 * cos2, y + rad2 * sin2, x + rad2 * cos, y + rad2 * sin);
			}
		}
	}

	/** Similar to {@link Drawf#laser} but doesn't draw light. */
	public static void laser(TextureRegion line, TextureRegion start, TextureRegion end, float x, float y, float x2, float y2, float scale) {
		float scl = 8f * scale * Draw.scl, rot = Mathf.angle(x2 - x, y2 - y);
		float vx = Mathf.cosDeg(rot) * scl, vy = Mathf.sinDeg(rot) * scl;

		Draw.rect(start, x, y, start.width * scale * start.scl(), start.height * scale * start.scl(), rot + 180);
		Draw.rect(end, x2, y2, end.width * scale * end.scl(), end.height * scale * end.scl(), rot);

		Lines.stroke(12f * scale);
		Lines.line(line, x + vx, y + vy, x2 - vx, y2 - vy, false);
		Lines.stroke(1f);
	}

	public static void blockBuild(float x, float y, TextureRegion region, float rotation, float progress) {
		blockBuild(x, y, region, Pal.accent, rotation, progress);
	}

	public static void blockBuild(float x, float y, TextureRegion region, Color color, float rotation, float progress) {
		Shaders.blockbuild.region = region;
		Shaders.blockbuild.progress = progress;

		Draw.color(color);
		Draw.shader(Shaders.blockbuild);
		Draw.rect(region, x, y, rotation);
		Draw.shader();
		Draw.color();
	}

	public static void blockBuildCenter(float x, float y, TextureRegion region, float rotation, float progress) {
		blockBuildCenter(x, y, region, Pal.accent, rotation, progress);
	}

	public static void blockBuildCenter(float x, float y, TextureRegion region, Color color, float rotation, float progress) {
		HShaders.blockBuildCenter.region = region;
		HShaders.blockBuildCenter.progress = progress;

		Draw.color(color);
		Draw.shader(HShaders.blockBuildCenter);
		Draw.rect(region, x, y, rotation);
		Draw.shader();
		Draw.color();
	}

	public static void vertConstruct(float x, float y, TextureRegion region, float rotation, float progress, float alpha, float time) {
		vertConstruct(x, y, region, Pal.accent, rotation, progress, alpha, time);
	}

	public static void vertConstruct(float x, float y, TextureRegion region, Color color, float rotation, float progress, float alpha, float time) {
		HShaders.vertBuild.region = region;
		HShaders.vertBuild.progress = progress;
		HShaders.vertBuild.color.set(color);
		HShaders.vertBuild.color.a = alpha;
		HShaders.vertBuild.time = -time / 20f;

		Draw.shader(HShaders.vertBuild);
		Draw.rect(region, x, y, rotation);
		Draw.shader();

		Draw.reset();
	}

	public static void materialize(float x, float y, TextureRegion region, Color color, float rotation, float offset, float progress) {
		materialize(x, y, region, color, rotation, offset, progress, Time.time, false);
	}

	public static void materialize(float x, float y, TextureRegion region, Color color, float rotation, float offset, float progress, boolean shadow) {
		materialize(x, y, region, color, rotation, offset, progress, Time.time, shadow);
	}

	public static void materialize(float x, float y, TextureRegion region, Color color, float rotation, float offset, float progress, float time) {
		materialize(x, y, region, color, rotation, offset, progress, time, false);
	}

	public static void materialize(float x, float y, TextureRegion region, Color color, float rotation, float offset, float progress, float time, boolean shadow) {
		HShaders.materialize.region = region;
		HShaders.materialize.progress = Mathf.clamp(progress);
		HShaders.materialize.color.set(color);
		HShaders.materialize.time = time;
		HShaders.materialize.offset = offset;
		HShaders.materialize.shadow = Mathf.num(shadow);

		Draw.shader(HShaders.materialize);
		Draw.rect(region, x, y, rotation);
		Draw.shader();

		Draw.reset();
	}

	private static void drawSpinSprite(TextureRegion[] regions, float x, float y, float w, float h, float r) {
		float ar = Mathf.mod(r, 360f);

		Actions.alpha(1f);
		if (ar > 45f && ar <= 225f) {
			Draw.rect(regions[0], x, y, w, h * -1f, r);
		} else {
			Draw.rect(regions[0], x, y, w, h, r);
		}

		if (ar >= 180 && ar < 270) { //Bottom Left
			float a = Interp.slope.apply(Mathf.curve(ar, 180, 270));
			Draw.alpha(a);
			Draw.rect(regions[1], x, y, w, h, r);
		} else if (ar < 90 && ar >= 0) { //Top Right
			float a = Interp.slope.apply(Mathf.curve(ar, 0, 90));
			Draw.alpha(a);
			Draw.rect(regions[2], x, y, w, h, r);
		}
		Draw.alpha(1f);
	}

	/** Draws a sprite that should be light-wise correct. Provided sprites must be similar in shape and face towards the right. */
	public static void spinSprite(TextureRegion[] regions, float x, float y, float w, float h, float r, float alpha) {
		float xScl = Draw.xscl, yScl = Draw.yscl;
		if (alpha < 0.99f) {
			FrameBuffer buffer = renderer.effectBuffer;
			float z = Draw.z();
			Draw.draw(z, () -> {
				buffer.begin(Color.clear);
				drawSpinSprite(regions, x, y, w * xScl, h * yScl, r);
				buffer.end();

				HShaders.alphaShader.alpha = alpha;
				buffer.blit(HShaders.alphaShader);
			});
		} else {
			drawSpinSprite(regions, x, y, w * xScl, h * yScl, r);
		}
	}

	/** Draws a sprite that should be light-wise correct. Provided sprites must be similar in shape and face towards the right. */
	public static void spinSprite(TextureRegion[] regions, float x, float y, float w, float h, float r) {
		spinSprite(regions, x, y, w, h, r, 1f);
	}


	/** Draws a sprite that should be light-wise correct. Provided sprites must be similar in shape and face towards the right. */
	public static void spinSprite(TextureRegion[] regions, float x, float y, float r, float alpha) {
		spinSprite(regions, x, y, regions[0].width / 4f, regions[0].height / 4f, r, alpha);
	}

	/** Draws a sprite that should be light-wise correct. Provided sprites must be similar in shape and face towards the right. */
	public static void spinSprite(TextureRegion[] regions, float x, float y, float r) {
		spinSprite(regions, x, y, regions[0].width / 4f, regions[0].height / 4f, r);
	}

	public static void ellipse(float x, float y, float rad, float wScl, float hScl, float rot) {
		float sides = Lines.circleVertices(rad);
		float space = 360 / sides;
		float r1 = rad - Lines.getStroke() / 2f, r2 = rad + Lines.getStroke() / 2f;

		for (int i = 0; i < sides; i++) {
			float a = space * i;
			v1.trns(rot, r1 * wScl * Mathf.cosDeg(a), r1 * hScl * Mathf.sinDeg(a));
			v2.trns(rot, r1 * wScl * Mathf.cosDeg(a + space), r1 * hScl * Mathf.sinDeg(a + space));
			v3.trns(rot, r2 * wScl * Mathf.cosDeg(a + space), r2 * hScl * Mathf.sinDeg(a + space));
			v4.trns(rot, r2 * wScl * Mathf.cosDeg(a), r2 * hScl * Mathf.sinDeg(a));
			Fill.quad(x + v1.x, y + v1.y, x + v2.x, y + v2.y, x + v3.x, y + v3.y, x + v4.x, y + v4.y);
		}
	}

	/**
	 * Color flash over the entire screen
	 *
	 * @author sunny, customization by MEEP
	 */
	public static void flash(Color fromColor, Color toColor, float seconds, Interp fade) {
		if (!headless) {
			Image flash = new Image();
			flash.touchable = Touchable.disabled;
			flash.setColor(fromColor);
			flash.setFillParent(true);
			flash.actions(Actions.color(toColor, seconds));
			flash.actions(Actions.fadeOut(seconds, fade), Actions.remove());
			flash.update(() -> {
				if (!Vars.state.isGame()) {
					flash.remove();
				}
			});
			Core.scene.add(flash);
		}
	}

	public static void spinningCircle(int seed, float time, float x, float y, float radius, int spikes, float spikeDuration, float durationRnd, float spikeWidth, float spikeHeight, float pointOffset) {
		spinningCircle(seed, time, time, x, y, radius, spikes, spikeDuration, durationRnd, spikeWidth, spikeHeight, pointOffset);
	}

	public static void spinningCircle(int seed, float angle, float time, float x, float y, float radius, int spikes, float spikeDuration, float durationRnd, float spikeWidth, float spikeHeight, float pointOffset) {
		Fill.circle(x, y, radius);

		for (int i = 0; i < spikes; i++) {
			float d = spikeDuration + Mathf.randomSeedRange(seed + i + spikes, durationRnd);
			float timeOffset = Mathf.randomSeed((seed + i) * 314L, 0f, d);
			int timeSeed = Mathf.floor((time + timeOffset) / d);
			float a = angle + Mathf.randomSeed(Math.max(timeSeed, 1) + ((i + seed) * 245L), 360f);
			float fin = ((time + timeOffset) % d) / d;
			float fslope = (0.5f - Math.abs(fin - 0.5f)) * 2f;
			v1.trns(a + spikeWidth / 2f, radius).add(x, y);
			v2.trns(a - spikeWidth / 2f, radius).add(x, y);
			v3.trns(a + pointOffset, radius + spikeHeight * fslope).add(x, y);
			Fill.tri(
					v1.x, v1.y,
					v2.x, v2.y,
					v3.x, v3.y
			);
		}
	}

	public static void arcLine(float x, float y, float radius, float fraction, float rotation) {
		Lines.arc(x, y, radius, fraction, rotation, 50);
	}

	public static void arcLine(float x, float y, float radius, float fraction, float rotation, int sides) {
		int max = Mathf.ceil(sides * fraction);
		Lines.beginLine();

		for (int i = 0; i <= max; i++) {
			v1.trns((float) i / max * fraction * 360f + rotation, radius);
			float x1 = v1.x;
			float y1 = v1.y;

			v1.trns((float) (i + 1) / max * fraction * 360f + rotation, radius);

			Lines.linePoint(x + x1, y + y1);
		}

		Lines.endLine();
	}

	public static void arcFill(float x, float y, float radius, float fraction, float rotation) {
		arcFill(x, y, radius, fraction, rotation, 50);
	}

	public static void arcFill(float x, float y, float radius, float fraction, float rotation, int sides) {
		int max = Mathf.ceil(sides * fraction);
		Fill.polyBegin();
		Fill.polyPoint(x, y);

		for (int i = 0; i <= max; i++) {
			float a = fraction * 360f * ((float) i / max) + rotation;
			float x1 = Angles.trnsx(a, radius);
			float y1 = Angles.trnsy(a, radius);

			Fill.polyPoint(x + x1, y + y1);
		}
		Fill.polyPoint(x, y);

		Fill.polyEnd();
	}

	public static float text(float x, float y, Color color, CharSequence text) {
		return text(x, y, true, -1, color, text);
	}

	public static float text(float x, float y, boolean underline, float maxWidth, Color color, CharSequence text) {
		Font font = Fonts.outline;
		GlyphLayout layout = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
		boolean ints = font.usesIntegerPositions();
		font.setUseIntegerPositions(false);
		if (maxWidth <= 0) {
			font.getData().setScale(1f / 3f);
			layout.setText(font, text);
		} else {
			font.getData().setScale(1f);
			layout.setText(font, text);
			font.getData().setScale(Math.min(1f / 3f, maxWidth / layout.width));
			layout.setText(font, text);
		}

		font.setColor(color);
		font.draw(text, x, y + (underline ? layout.height + 1 : layout.height / 2f), Align.center);
		if (underline) {
			y -= 1f;
			Lines.stroke(2f, Color.darkGray);
			Lines.line(x - layout.width / 2f - 2f, y, x + layout.width / 2f + 1.5f, y);
			Lines.stroke(1f, color);
			Lines.line(x - layout.width / 2f - 2f, y, x + layout.width / 2f + 1.5f, y);
		}

		float width = layout.width;

		font.setUseIntegerPositions(ints);
		font.setColor(Color.white);
		font.getData().setScale(1f);
		Draw.reset();
		Pools.free(layout);

		return width;
	}

	public static void tractorCone(float cx, float cy, float time, float spacing, float thickness, Runnable draw) {
		FrameBuffer buffer = renderer.effectBuffer;
		float z = Draw.z();
		Draw.draw(z, () -> {
			buffer.begin(Color.clear);
			draw.run();
			buffer.end();

			HShaders.tractorCone.setCenter(cx, cy);
			HShaders.tractorCone.time = time;
			HShaders.tractorCone.spacing = spacing;
			HShaders.tractorCone.thickness = thickness;
			buffer.blit(HShaders.tractorCone);
		});
	}

	public static void drawHalfSpin(TextureRegion region, float x, float y, float r) {
		float a = Draw.getColor().a;
		r = Mathf.mod(r, 180f);
		Draw.rect(region, x, y, r);
		Draw.alpha(r / 180f * a);
		Draw.rect(region, x, y, r - 180f);
		Draw.alpha(a);
	}

	public static Color teamColor(Teamc entity, Color color) {
		return color == null ? entity.team().color : color;
	}

	public static void drawStar(float x, float y, float w, float h, float angleOffset, float centerColor, float edgeColor) {
		int sides = mul4(Lines.circleVertices(w + h));
		float space = 360f / sides;

		for (int i = 0; i < sides; i++) {
			float t1 = i * space, t2 = (i + 1) * space;
			Tmp.v1.trns(t1, circleStarPoint(t1)).scl(w, h).rotate(angleOffset).add(x, y);
			Tmp.v2.trns(t2, circleStarPoint(t2)).scl(w, h).rotate(angleOffset).add(x, y);
			Fill.quad(x, y, centerColor, x, y, centerColor, Tmp.v1.x, Tmp.v1.y, edgeColor, Tmp.v2.x, Tmp.v2.y, edgeColor);
		}
	}

	public static void drawStar(float x, float y, float w, float h, float centerColor, float edgeColor) {
		drawStar(x, y, w, h, 0f, centerColor, edgeColor);
	}

	public static float circleStarPoint(float theta) {
		theta = Mathf.mod(theta, 90f);
		theta *= Mathf.degRad;
		float b = -2 * Mathf.sqrt2 * Mathf.cos(theta - Mathf.pi / 4f);
		return (-b - Mathf.sqrt(b * b - 4)) / 2;
	}

	public static void setStrobeColor() {
		Draw.color(applyStrobeHue(Tmp.c1.set(Color.red)));
	}

	public static Color applyStrobeHue(Color color) {
		float speed = Core.settings.getInt("hi-strobespeed", 3) / 2f;
		color.shiftHue(Time.time * speed);
		return color;
	}

	private static int mul4(int value) {
		while (value % 4 != 0) {
			value++;
		}
		return value;
	}
}
