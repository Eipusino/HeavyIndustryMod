package heavyindustry.type.lightnings;

import arc.func.Cons;
import arc.func.Cons2;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Eachable;
import arc.util.Time;
import arc.util.pooling.Pool.Poolable;
import arc.util.pooling.Pools;
import heavyindustry.math.IInterp;
import heavyindustry.type.lightnings.generator.LightningGenerator;
import heavyindustry.util.CollectionList;

import java.util.Iterator;

/**
 * LightningEffect container, using a lightning generator to generate lightning, processed and drawn by the
 * container, usually used for storing a type of lightning in the same container.
 *
 * @since 1.0.8
 */
public class LightningContainer implements Iterable<LightningEffect>, Eachable<LightningEffect> {
	/**
	 * The time required for lightning to occur from generation to complete appearance will be evenly
	 * distributed among each lightning segment, with fps being the current frame rate.
	 * <p>But if this value is 0, lightning will appear immediately.
	 */
	public float time = 0;
	/**
	 * When the diffusion speed of lightning is less than or equal to 0, the path diffusion calculation
	 * method provided by time is used by default. Otherwise, the given speed is used to process the
	 * diffusion of lightning (unit:/tick).
	 *
	 * @deprecated Standardization, this API is no longer available
	 */
	@Deprecated/*(since = "1.0.8")*/
	public float speed = 0;
	/** The existence time of lightning. */
	public float lifeTime = 30;
	/**
	 * The transition time of lightning disappearance, if not set, the disappearance transition time is equal
	 * to the existence time of lightning.
	 */
	public float fadeTime = -1;
	/** Does the overall width of lightning fade out with the duration of lightning. */
	public boolean fade = true;
	/** Does lightning disappear from the starting point during the fading process. */
	public boolean backFade = false;
	/** Random intervals of lightning width for each segment. */
	public float minWidth = 2.5f, maxWidth = 4.5f;
	/** The attenuation transformer of lightning receives the value of the duration of lightning existence. */
	public Interp lerp = Interp.pow2Out;

	/**
	 * The callback function called when creating a lightning branch is generally used to define the sub
	 * container properties of the lightning branch.
	 */
	public Cons<LightningEffect> branchCreated;

	/**
	 * LightningEffect vertex trigger, triggered when a lightning node has arrived, passes in the previous vertex
	 * and this vertex.
	 */
	public Cons2<LightningVertex, LightningVertex> trigger;
	public boolean headClose, endClose;

	protected float clipSize;

	protected final CollectionList<LightningEffect> lightnings = new CollectionList<>(LightningEffect.class);

	/** Create a new lightning bolt in the container using the provided lightning generator. */
	public void create(LightningGenerator generator) {
		generator.branched(branchCreated);
		LightningEffect lightning = LightningEffect.create(
				generator,
				Mathf.random(minWidth, maxWidth),
				lifeTime,
				fadeTime > 0 ? fadeTime : lifeTime,
				lerp,
				time,
				fade,
				backFade,
				trigger
		);
		lightning.headClose = headClose;
		lightning.endClose = endClose;
		lightnings.add(lightning);
	}

	@Override
	public Iterator<LightningEffect> iterator() {
		return lightnings.iterator();
	}

	@Override
	public void each(Cons<? super LightningEffect> cons) {
		lightnings.each(cons);
	}

	/** Update the status of all sub lightning in the current container once. */
	public void update() {
		Iterator<LightningEffect> itr = lightnings.iterator();
		while (itr.hasNext()) {
			LightningEffect lightning = itr.next();
			clipSize = Math.max(clipSize, lightning.clipSize);

			float progress = (Time.time - lightning.startTime) / lifeTime;
			if (progress > 1) {
				itr.remove();
				Pools.free(lightning);
				clipSize = 0;
				continue;
			}

			lightning.update();
		}
	}

	/**
	 * Draw the container, which will draw all the lightning saved in the container.
	 *
	 * @param x Draw the origin x-coordinate of lightning
	 * @param y Draw the origin y-coordinate of lightning
	 *
	 */
	public void draw(float x, float y) {
		for (LightningEffect lightning : lightnings) {
			lightning.draw(x, y);
		}
	}

	public float clipSize() {
		return clipSize;
	}

	/** LightningEffect branch container, used to draw branch lightning, recursively draws all sub branches. */
	public static class PoolLightningContainer extends LightningContainer implements Poolable {
		public static PoolLightningContainer create(float lifeTime, float minWidth, float maxWidth) {
			PoolLightningContainer result = Pools.obtain(PoolLightningContainer.class, PoolLightningContainer::new);
			result.lifeTime = lifeTime;
			result.minWidth = minWidth;
			result.maxWidth = maxWidth;

			return result;
		}

		@Override
		public void reset() {
			time = 0;
			lifeTime = 0;
			clipSize = 0;
			maxWidth = 0;
			minWidth = 0;
			lerp = (IInterp) a -> 1f - a;
			branchCreated = null;
			trigger = null;

			for (LightningEffect lightning : lightnings) {
				Pools.free(lightning);
			}
			lightnings.clear();
		}
	}
}
