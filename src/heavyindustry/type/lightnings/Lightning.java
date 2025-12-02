package heavyindustry.type.lightnings;

import arc.func.Cons2;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pool.Poolable;
import arc.util.pooling.Pools;
import heavyindustry.type.lightnings.generator.LightningGenerator;
import heavyindustry.util.CollectionList;
import mindustry.graphics.Drawf;

/**
 * A storage container for a single lightning bolt, which stores the start time and vertex information of the
 * lightning bolt.
 * <p>There are a large number of such instances, which should be reused.
 *
 * @author EBwilson
 * @since 1.0.8
 */
public class Lightning implements Poolable {
	private static final Vec2 last = new Vec2(), self = new Vec2(), next = new Vec2();

	public final CollectionList<LightningVertex> vertices = new CollectionList<>(LightningVertex.class);
	/** The duration of lightning. */
	public float lifeTime;
	/** The transition time of lightning disappearing. */
	public float fadeTime;
	/** Does lightning disappear from the starting point during the fading process. */
	public boolean backFade = false;
	/** Does the overall width of lightning fade out with the duration of lightning. */
	public boolean fade = true;
	/** The time when lightning was created. */
	public float startTime;
	/** The cutting size of this lightning bolt is used for image cropping during drawing. */
	public float clipSize;
	/** The width of lightning. */
	public float width;

	/** Interpolation function for lightning width. */
	public Interp lerp = Interp.linear;
	/**
	 * The trigger for each segment of lightning will be called once each segment of lightning is partially
	 * generated, passing in the current vertex and the previous vertex.
	 */
	public Cons2<LightningVertex, LightningVertex> trigger;

	/**
	 * If the spread speed of lightning is not set, {@link Lightning#time} will be used to determine the time when lightning
	 * completely appears.
	 *
	 * @deprecated Standardization, this API will no longer be valid
	 */
	@Deprecated
	public float speed;
	/** The time from the generation of lightning to its full manifestation is valid when {@link Lightning#speed} is not set. */
	public float time;
	public float counter, lengthMargin;

	public boolean headClose, endClose;

	float totalLength;

	int cursor;

	boolean enclosed;

	public static Lightning create(LightningGenerator generator, float width, float lifeTime, Interp lerp, float time, Cons2<LightningVertex, LightningVertex> trigger) {
		return create(generator, width, lifeTime, lerp, time, 0, trigger);
	}

	public static Lightning create(LightningGenerator generator, float width, float lifeTime, Interp lerp, float time, float speed, Cons2<LightningVertex, LightningVertex> trigger) {
		return create(generator, width, lifeTime, lifeTime, lerp, time, true, false, trigger);
	}

	public static Lightning create(LightningGenerator generator, float width, float lifeTime, float fadeTime, Interp lerp, float time, boolean fade, boolean backFade, Cons2<LightningVertex, LightningVertex> trigger) {
		Lightning result = Pools.obtain(Lightning.class, Lightning::new);
		result.width = width;
		result.time = time;
		result.startTime = Time.time;
		result.lifeTime = lifeTime;
		result.fadeTime = fadeTime;
		result.lerp = lerp;
		result.fade = fade;
		result.backFade = backFade;
		result.trigger = trigger;

		generator.setCurrentGen(result);

		LightningVertex last = null;
		for (LightningVertex vertex : generator) {
			result.vertices.add(vertex);
			if (last != null) {
				result.totalLength += Mathf.len(vertex.x - last.x, vertex.y - last.y);
			}
			last = vertex;
		}
		result.enclosed = generator.isEnclosed();
		result.clipSize = generator.clipSize();

		return result;
	}

	private Lightning() {
	}

	/** Update the lightning status once. */
	public void update() {
		if (time == 0 && cursor < vertices.size) {
			LightningVertex per = null;
			for (LightningVertex vertex : vertices) {
				if (per != null) {
					per.progress = 1;
					vertex.valid = true;
					if (trigger != null) trigger.get(per, vertex);
				}
				per = vertex;
			}
			cursor = vertices.size;
		} else {
			float increase = vertices.size / time * Time.delta;

			while (increase > 0) {
				if (cursor == 0) {
					cursor++;
				}

				if (cursor >= vertices.size) break;

				LightningVertex per = vertices.get(cursor - 1), curr = vertices.get(cursor);
				float delta = Math.min(increase, 1 - per.progress);
				per.progress += delta;
				increase -= delta;

				if (per.progress >= 1) {
					curr.valid = true;
					if (trigger != null) trigger.get(per, curr);
					cursor++;
				}
			}
		}

		for (LightningVertex vertex : vertices) {
			if (!vertex.isEnd && !vertex.isStart && vertex.valid) vertex.update();
		}
	}

	/**
	 * Draw this lightning bolt.
	 *
	 * @param x Draw the origin x-coordinate of lightning
	 * @param y Draw the origin y-coordinate of lightning
	 *
	 */
	@SuppressWarnings("DuplicatedCode")
	public void draw(float x, float y) {
		float lerp = Mathf.clamp(this.lerp.apply(Mathf.clamp((lifeTime - (Time.time - startTime)) / fadeTime)));
		float del = backFade ? (1 - lerp) * vertices.size : 0;

		if (!fade) lerp = 1;

		for (int i = 2; i <= vertices.size; i++) {
			LightningVertex v1 = i - 3 >= 0 ? vertices.get(i - 3) : enclosed ? vertices.get(Mathf.mod(i - 3, vertices.size)) : null,
					v2 = vertices.get(i - 2),
					v3 = vertices.get(i - 1),
					v4 = i < vertices.size ? vertices.get(i) : enclosed ? vertices.get(Mathf.mod(i, vertices.size)) : null;

			float lastOffX, lastOffY;
			float nextOffX, nextOffY;

			float fade = Math.min(del, 1);
			del -= fade;
			if (!v2.valid) break;

			self.set(v3.x, v3.y).sub(v2.x, v2.y);

			if (v1 != null) {
				last.set(v2.x, v2.y).sub(v1.x, v1.y);

				float aveAngle = (last.angle() + self.angle()) / 2;
				float off = width / 2 * lerp / Mathf.cosDeg(aveAngle - last.angle());

				lastOffX = Angles.trnsx(aveAngle + 90, off);
				lastOffY = Angles.trnsy(aveAngle + 90, off);
			} else {
				Tmp.v1.set(self).rotate90(1).setLength(width / 2 * lerp);
				lastOffX = Tmp.v1.x;
				lastOffY = Tmp.v1.y;
			}

			if (v4 != null) {
				next.set(v4.x, v4.y).sub(v3.x, v3.y);
				float aveAngle = (self.angle() + next.angle()) / 2;
				float off = width / 2 * lerp / Mathf.cosDeg(aveAngle - self.angle());

				nextOffX = Angles.trnsx(aveAngle + 90, off);
				nextOffY = Angles.trnsy(aveAngle + 90, off);
			} else {
				Tmp.v1.set(self).rotate90(1).setLength(width / 2 * lerp);
				nextOffX = Tmp.v1.x;
				nextOffY = Tmp.v1.y;
			}

			lastOffX *= lerp;
			lastOffY *= lerp;
			nextOffX *= lerp;
			nextOffY *= lerp;

			float orgX = x + v2.x, orgY = y + v2.y;
			float fadX = Tmp.v1.x * fade, fadY = Tmp.v1.y * fade;

			Tmp.v1.set(self).scl(v2.progress);
			if ((v2.isStart && !headClose) || (v3.isEnd && !endClose)) {
				float l = v2.isStart ? v2.progress : 1 - v2.progress;
				float f = v2.isStart ? fade : 1 - fade;
				Fill.quad(
						orgX + fadX + lastOffX * f,
						orgY + fadY + lastOffY * f,
						orgX + fadX - lastOffX * f,
						orgY + fadY - lastOffY * f,
						orgX + Tmp.v1.x - nextOffX * l,
						orgY + Tmp.v1.y - nextOffY * l,
						orgX + Tmp.v1.x + nextOffX * l,
						orgY + Tmp.v1.y + nextOffY * l
				);
			} else {
				Fill.quad(
						orgX + fadX + lastOffX,
						orgY + fadY + lastOffY,
						orgX + fadX - lastOffX,
						orgY + fadY - lastOffY,
						orgX + Tmp.v1.x - nextOffX,
						orgY + Tmp.v1.y - nextOffY,
						orgX + Tmp.v1.x + nextOffX,
						orgY + Tmp.v1.y + nextOffY
				);
			}

			Drawf.light(
					orgX, orgY,
					orgX + Tmp.v1.x, orgY + Tmp.v1.y,
					width * 32,
					Draw.getColor(),
					Draw.getColor().a
			);

			v2.draw(x, y);
		}
	}

	@Override
	public void reset() {
		for (LightningVertex vertex : vertices) {
			Pools.free(vertex);
		}
		vertices.clear();
		counter = 0;
		width = 0;
		time = 0;
		cursor = 0;
		lifeTime = 0;
		enclosed = false;
		lerp = null;
		lengthMargin = 0;
		startTime = 0;
		clipSize = 0;
		trigger = null;
	}
}
