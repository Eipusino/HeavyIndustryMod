package heavyindustry.entities.pattern;

import arc.math.*;
import arc.util.*;
import mindustry.entities.pattern.*;

public class ShootHelixF extends ShootHelix {
    public boolean flip = false;
    public float rotSpeedOffset = 0;
    public float rotSpeedBegin = 1;

    @Override
    public void shoot(int totalShots, BulletHandler handler, @Nullable Runnable barrelIncrementer) {
        for (int i = 0; i < shots; i++) {
            if (flip) {
                for (int sign : Mathf.signs) {
                    int fi = i;
                    handler.shoot(0, 0, 0, firstShotDelay + shotDelay * i, b -> b.moveRelative(0f, Mathf.sin(b.time * (rotSpeedOffset * fi + rotSpeedBegin) + offset * ((float) fi / shots), scl, mag * sign)));
                }
            } else {
                int fi = i;
                handler.shoot(0, 0, 0, firstShotDelay + shotDelay * i, b -> b.moveRelative(0f, Mathf.sin(b.time * (rotSpeedOffset * fi + rotSpeedBegin) + offset * ((float) fi / shots), scl, mag)));
            }
        }
    }
}
