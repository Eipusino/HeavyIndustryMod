package heavyindustry.entities.bullet;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

public class GrenadeBulletType extends BulletType {
    public float width, height;
    public Color frontColor = Color.gray, backColor = Pal.bulletYellowBack, mixColorFrom = new Color(1.0f, 1.0f, 1.0f, 0.0f), mixColorTo = new Color(1.0f, 1.0f, 1.0f, 0.0f);
    public TextureRegion frontRegion, backRegion;

    @Override
    public void load() {
        super.load();
        trailEffect = Fx.artilleryTrail;
        width = 6;
        height = 4;
        hitEffect = Fx.blastExplosion;
        frontRegion = Core.atlas.find("bullet");
        backRegion = Core.atlas.find("bullet-back");
    }

    public boolean justBounced(Bullet b) {
        float x = b.fin();
        double px = (b.time - Time.delta * 2.0) / b.lifetime;
        return Math.floor(5 * x) != Math.floor(5 * px);
    }

    @Override
    public void update(Bullet b) {
        super.update(b);

        if (justBounced(b)) {
            b.vel.x *= 0.8f;
            b.vel.y *= 0.8f;
        }

        float zh = getZ(b);
        Tile tile = Vars.world.tileWorld(b.x, b.y);
        if (tile == null || tile.build == null || zh > 0.2 || b.fin() < 0.05) return;

        if (tile.solid()) {
            b.trns(-b.vel.x, -b.vel.y);
            float penX = Math.abs(tile.build.x - b.x);
            float penY = Math.abs(tile.build.y - b.y);
            if (penX > penY) {
                b.vel.x *= -0.5f;
            } else {
                b.vel.y *= -0.5f;
            }
        }

    }

    @Override
    public float calculateRange() {
        //float a = 1.0f/0.3f;
        float t = speed * 0.8f;
        float acc = 0;
        for (int i = 0; i < 5; i++) {
            t *= 0.8f;
            acc += lifetime * 0.2f * t;
        }
        return Math.max(acc, maxRange);
    }

    public float getZ(Bullet b) {
        float x = b.fin();
        return Math.abs(Mathf.sin(5 * x * Mathf.pi)) / (Mathf.floor(x * 5) * Mathf.floor(x * 5) + 1);
    }

    @Override
    public boolean testCollision(Bullet bullet, Building tile) {
        return super.testCollision(bullet, tile) && getZ(bullet) < 0.2;
    }

    @Override
    public void draw(Bullet b) {
        if (b.fin() < 0.15 && b.timer.get(0, (3 + b.fslope() * 2))) {
            trailEffect.at(b.x, b.y, b.fslope() * 4.0f * Mathf.clamp(b.fout()), backColor);
        }

        float scl = getZ(b) + 1;
        float offset = Time.time * 3.0f;
        float height = this.height * scl;
        float width = this.width * scl;
        boolean flash = Mathf.pow(2, 5 * b.fin() - 1) % 1.0 > 0.5;

        Color mix = Tmp.c1.set(mixColorFrom).lerp(mixColorTo, b.fin());

        Draw.mixcol(mix, mix.a);

        Draw.color(backColor);
        Draw.rect(backRegion, b.x, b.y, width, height, b.rotation() - 90 + offset);
        Draw.color(frontColor.cpy().lerp(Color.white, flash ? 1 : 0));
        Draw.rect(frontRegion, b.x, b.y, width, height, b.rotation() - 90 + offset);

        Draw.reset();
    }
}
