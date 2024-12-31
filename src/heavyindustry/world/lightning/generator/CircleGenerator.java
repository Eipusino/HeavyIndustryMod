package heavyindustry.world.lightning.generator;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import heavyindustry.world.lightning.*;

/** A generator for circular lightning that generates lightning vertices using a specified center and radius. */
public class CircleGenerator extends LightningGenerator {
    /** The radius of the circle on which lightning is based. */
    public float radius = 16;
    /** The original starting angle of the circle will affect the lightning propagation starting point of the circle generated when time is not zero. */
    public float originAngle;
    /** The rotation direction of a circle is counterclockwise if it is greater than 0, otherwise it is clockwise. */
    public int directory = 1;
    /** Whether this circle is closed or not will determine whether the head and tail of the lightning are connected or disconnected. */
    public boolean enclosed = true;

    Vec2 rad = new Vec2();
    float currentRotated;

    boolean first;
    LightningVertex firstOne;

    @Override
    public void reset() {
        super.reset();
        rad.set(1, 0).setLength(radius).setAngle(originAngle);
        currentRotated = 0;
        first = true;
        firstOne = null;
    }

    @Override
    public boolean hasNext() {
        return super.hasNext() && currentRotated < 360;
    }

    @Override
    protected void handleVertex(LightningVertex vertex) {
        float step = seed.random(minInterval, maxInterval);
        float rotated = step / (Mathf.pi * radius / 180) * (directory >= 0 ? 1 : -1);

        if (rotated + currentRotated >= 360) {
            if (firstOne == null) {
                currentRotated = 361;
                return;
            }

            vertex.isEnd = !enclosed;
            if (enclosed) {
                vertex.x = firstOne.x;
                vertex.y = firstOne.y;
            }
            currentRotated = 360;
        } else {
            currentRotated += rotated;

            float offset = seed.random(-maxSpread, maxSpread);
            Tmp.v2.set(Tmp.v1.set(rad.rotate(rotated))).setLength(offset).scl(offset < 0 ? -1 : 1);
            Tmp.v1.add(Tmp.v2);

            vertex.x = Tmp.v1.x;
            vertex.y = Tmp.v1.y;
        }

        if (first) {
            vertex.valid = true;
            vertex.isStart = !enclosed;
            if (enclosed) {
                firstOne = vertex;
            }
            first = false;
        }
    }

    @Override
    public boolean isEnclosed() {
        return enclosed;
    }

    @Override
    public float clipSize() {
        return radius + maxSpread;
    }
}
