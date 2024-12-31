package heavyindustry.world.lightning;

import arc.func.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.*;
import arc.util.pooling.Pool.*;
import heavyindustry.world.lightning.generator.*;

import java.util.*;

/**
 * Lightning container, using a lightning generator to generate lightning, processed and drawn by the container,
 * usually used for storing a type of lightning in the same container.
 */
public class LightningContainer implements Iterable<Lightning> {
    protected final Seq<Lightning> lightnings = new Seq<>();
    /**
     * The time required for lightning to occur from generation to complete appearance will be evenly distributed among each lightning segment, with fps being the current frame rate.
     * But if this value is 0, lightning will appear immediately.
     */
    public float time = 0;
    /** The existence time of lightning. */
    public float lifeTime = 30;
    /**
     * The transition time of lightning disappearance, if not set,
     * the disappearance transition time is equal to the existence time of lightning.
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
    /** The callback function called when creating a lightning branch is generally used to define the sub container properties of the lightning branch. */
    public Cons<Lightning> branchCreated;
    /**
     * Lightning vertex trigger, triggered when a lightning node has arrived,
     * passes in the previous vertex and this vertex.
     */
    public Cons2<LightningVertex, LightningVertex> trigger;
    public boolean headClose, endClose;

    protected float clipSize;

    /** Create a new lightning bolt in the container using the provided lightning generator. */
    public void create(LightningGenerator generator) {
        generator.branched(branchCreated);
        Lightning lightning = Lightning.create(generator, Mathf.random(minWidth, maxWidth), lifeTime, fadeTime > 0 ? fadeTime : lifeTime, lerp, time, fade, backFade, trigger);
        lightning.headClose = headClose;
        lightning.endClose = endClose;
        lightnings.add(lightning);
    }

    @Override
    public Iterator<Lightning> iterator() {
        return lightnings.iterator();
    }

    /** Update the status of all sub lightning in the current container once. */
    public void update() {
        Iterator<Lightning> itr = lightnings.iterator();
        while (itr.hasNext()) {
            Lightning lightning = itr.next();
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
     */
    public void draw(float x, float y) {
        for (Lightning lightning : lightnings) {
            lightning.draw(x, y);
        }
    }

    public float clipSize() {
        return clipSize;
    }

    /** Lightning branch container, used to draw branch lightning, recursively draws all sub-branches. */
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
            lerp = f -> 1 - f;
            branchCreated = null;
            trigger = null;

            for (Lightning lightning : lightnings) {
                Pools.free(lightning);
            }
            lightnings.clear();
        }
    }
}
