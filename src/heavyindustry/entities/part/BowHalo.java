package heavyindustry.entities.part;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.part.*;
import mindustry.graphics.*;

public class BowHalo extends DrawPart {
    public float x = 0, y = -25;
    public float stroke = 4;
    public float radius = 12f;
    public Color color = Pal.accent;
    public float rotateSpeed = 1, w1 = 3, h1 = 6, w2 = 4, h2 = 18;
    public float layer = Layer.effect;
    public PartProgress progress = PartProgress.warmup.delay(0.5f);

    public boolean sinWave = true, rotAb = true;

    @Override
    public void draw(PartParams params) {
        float warmup = progress.getClamp(params);
        Lines.stroke(stroke * warmup);
        float sin = Mathf.absin(Time.time, 10, 1.5f);
        float realR = radius + (sinWave ? sin : 0);
        float rot = params.rotation - 90;
        float bx = params.x, by = params.y;
        Draw.z(layer);
        Draw.color(color);
        Tmp.v1.set(x, y).rotate(rot);
        float px = bx + Tmp.v1.x, py = by + Tmp.v1.y;
        Lines.circle(px, py, realR);
        for (int i = 0; i < 2; i++) {
            float angle = i * 360f / 2 + (rotAb ? rot : 0);
            Drawf.tri(px + Angles.trnsx(angle - Time.time * rotateSpeed, realR), py + Angles.trnsy(angle - Time.time * rotateSpeed, realR), w1, h1 * warmup, angle - Time.time * rotateSpeed);
            Drawf.tri(px + Angles.trnsx(angle, realR), py + Angles.trnsy(angle, realR), w2, h2 * warmup, angle);
        }
        Draw.reset();
    }

    @Override
    public void load(String name) {}
}
