package heavyindustry.world.lightning;

import arc.util.pooling.*;
import arc.util.pooling.Pool.*;

/**
 * The lightning vertex container stores necessary information for a vertex and a drawing progress timer.
 * There are a large number of such instances, which should be reused.
 */
public class LightningVertex implements Poolable {
    public float x, y;
    public float angle;

    public boolean isStart;
    public boolean isEnd;

    public boolean valid;
    public float progress;

    public Lightning branchOther;

    protected void draw(float x, float y) {
        if (branchOther != null) branchOther.draw(x, y);
    }

    public void update() {
        if (branchOther != null) branchOther.update();
    }

    @Override
    public void reset() {
        if (branchOther != null) Pools.free(branchOther);

        valid = false;
        progress = 0;
        x = y = 0;
        angle = 0;
        branchOther = null;
        isStart = false;
        isEnd = false;
    }
}
