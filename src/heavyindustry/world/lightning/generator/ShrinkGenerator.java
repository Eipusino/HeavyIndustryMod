package heavyindustry.world.lightning.generator;

import arc.math.geom.*;
import arc.util.*;
import heavyindustry.world.lightning.*;

/**
 * Shrink the generator of lightning, which will generate lightning that spreads inward to the center within a certain range.
 */
public class ShrinkGenerator extends LightningGenerator {
    public float minRange, maxRange;

    Vec2 vec = new Vec2();
    float distance;
    float currentDistance;
    boolean first;

    @Override
    public void reset() {
        super.reset();
        vec.rnd(distance = seed.random(minRange, maxRange));
        currentDistance = distance;
        first = true;
    }

    @Override
    public boolean hasNext() {
        return super.hasNext() && currentDistance > 0;
    }

    @Override
    protected void handleVertex(LightningVertex vertex) {
        currentDistance -= seed.random(minInterval, maxInterval);

        if (currentDistance > minInterval) {
            if (first) {
                Tmp.v2.set(vec);
            } else {
                float offset = seed.random(-maxSpread, maxSpread);
                Tmp.v2.set(vec).setLength(currentDistance).add(Tmp.v1.set(vec).rotate90(1).setLength(offset).scl(offset < 0 ? -1 : 1));
            }
        } else {
            currentDistance = 0;
            Tmp.v2.setZero();
            vertex.isEnd = true;
        }

        vertex.x = Tmp.v2.x;
        vertex.y = Tmp.v2.y;

        if (first) {
            vertex.isStart = true;
            vertex.valid = true;
            first = false;
        }
    }

    @Override
    public float clipSize() {
        return 0;
    }
}
