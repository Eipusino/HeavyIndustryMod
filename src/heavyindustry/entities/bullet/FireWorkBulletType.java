package heavyindustry.entities.bullet;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import heavyindustry.graphics.*;
import heavyindustry.util.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.gen.*;
import mindustry.graphics.*;

import static heavyindustry.core.HeavyIndustryMod.*;

/** Draw the effect of bullet fireworks. Although its memory usage may be slightly high. */
public class FireWorkBulletType extends BulletType {
    public String sprite;
    public boolean colorful = false, childColorful = true, outline = false;

    public Color color;
    public Color[] colors = new Color[]{Color.valueOf("ff4b4b"), Color.valueOf("feff4a"), Color.valueOf("724aff"), Color.valueOf("89c2ff"), Color.valueOf("39c5bb"), Color.white};

    public float width = 15f, height = 15f;

    public int num = 30;

    public @Nullable BulletType fire, textFire = null;

    public FireWorkBulletType(float damage, float speed, String sprite, Color color, float rad) {
        this.damage = damage;
        this.speed = speed;
        this.sprite = sprite;
        this.color = color;
        trailColor = color;
        trailInterval = 3;
        splashDamage = damage;
        splashDamageRadius = rad;
        hitEffect = despawnEffect = new ExplosionEffect() {{
            lifetime = 60f;
            waveStroke = 5f;
            waveLife = 8f;
            waveColor = Color.white;
            sparkColor = color;
            smokeColor = Pal.darkerGray;
            waveRad = rad;
            smokeSize = rad / 8f;
            smokes = 7;
            smokeSizeBase = 0f;
            sparks = 10;
            sparkRad = rad;
            sparkLen = 6f;
            sparkStroke = 2f;
        }};
        shootEffect = smokeEffect = Fx.none;
        despawnSound = hitSound = Sounds.bang;
        lifetime = 65;
        ammoMultiplier = 1;
        status = StatusEffects.blasted;
        statusDuration = 3 * 60f;
        fire = new ColorFireBulletType(true);
    }

    public FireWorkBulletType(float damage, float speed, Color color) {
        this(damage, speed, name("mb-fireworks"), color, 6 * 8);
    }

    public FireWorkBulletType(float damage, float speed) {
        this(damage, speed, name("mb-fireworks"), Color.gray, 6 * 8);
    }

    public FireWorkBulletType() {
        this(1f, 1f, name("mb-fireworks"), Color.gray, 6 * 8);
    }

    @Override
    public void drawTrail(Bullet b) {
        if (trailLength > 0 && b.trail != null) {
            float z = Draw.z();
            Draw.z(z - 0.0001f);
            b.trail.draw(colorful ? Utils.c5.set(HIPal.rainBowRed).a(0.7f).shiftHue(b.time * 2) : color, trailWidth);
            Draw.z(z);
        }
    }

    @Override
    public void draw(Bullet b) {
        super.draw(b);
        if (outline) {
            Draw.color(colorful ? Utils.c6.set(HIPal.rainBowRed).shiftHue(b.time * 2) : color);
            Draw.rect(Core.atlas.find(sprite), b.x, b.y, width * 1.1f, height * 1.1f, b.rotation() - 90);
            Draw.color(Color.darkGray);
            Draw.rect(Core.atlas.find(sprite), b.x, b.y, width * 0.8f, height * 0.8f, b.rotation() - 90);
        } else {
            Draw.color(colorful ? Utils.c6.set(HIPal.rainBowRed).shiftHue(b.time * 2) : color);
            Draw.rect(Core.atlas.find(sprite), b.x, b.y, width, height, b.rotation() - 90);
        }
        Draw.reset();
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health) {
        super.hitEntity(b, entity, health);
        if (!pierce || b.collided.size >= pierceCap) explode(b);
    }

    @Override
    public void hit(Bullet b) {
        super.hit(b);
        explode(b);
    }

    public void explode(Bullet b) {
        if (fire == null) return;
        for (int i = 0; i < num; i++) {
            if (colorful && childColorful) {
                Color c = colors[(int) Mathf.random(0, colors.length - 0.01f)];
                fire.create(b, b.team, b.x, b.y, Mathf.random(360), -1, 1, 1, c);
            } else fire.create(b, b.team, b.x, b.y, Mathf.random(360), -1, 1, 1, color);
        }
        if (textFire != null) {
            if (colorful) {
                Color c = colors[Mathf.random(0, colors.length)];
                textFire.create(b, b.team, b.x, b.y, 0, -1, 1, 1, c);
            } else textFire.create(b, b.team, b.x, b.y, 0, -1, 1, 1, color);
        }
    }

    public static class ColorFireBulletType extends BulletType {
        public boolean stop;
        public float stopFrom = 0.3f, stopTo = 0.6f, rotSpeed = 4f, speedRod = 1f;

        public ColorFireBulletType(boolean stop, float speed, float lifetime) {
            this.stop = stop;
            damage = 0;
            collides = false;
            this.speed = speed;
            this.lifetime = lifetime;
            trailWidth = 1.7f;
            trailLength = 6;
            hitEffect = despawnEffect = Fx.none;
            hittable = false;
            absorbable = true;
            keepVelocity = false;
        }

        public ColorFireBulletType(boolean stop) {
            this(stop, 5, 60);
        }

        @Override
        public void update(Bullet b) {
            super.update(b);
            if (stop)
                b.initVel(b.rotation(), speed * Math.max(b.fout() - Mathf.random(stopFrom, stopTo), 0) * Mathf.random(speedRod, 1));
            else {
                b.initVel(b.rotation(), speed * b.fout() * Mathf.random(speedRod, 1));
                b.rotation(Angles.moveToward(b.rotation(), -90, rotSpeed * Math.max(b.fin() - Mathf.random(stopFrom, stopTo), 0)));
            }
        }

        @Override
        public void draw(Bullet b) {
            super.draw(b);
            if (!(b.data instanceof Color)) return;
            Draw.color(b.data == Color.white ? Utils.c8.set(HIPal.rainBowRed).shiftHue(b.time * 2) : (Color) b.data);
            Draw.z(Layer.bullet);
            for (int i = 0; i < 4; i++) {
                Drawf.tri(b.x, b.y, 1.6f, 2.2f, b.rotation() + 90 * i);
            }

            Draw.reset();
        }

        @Override
        public void drawTrail(Bullet b) {
            if (trailLength > 0 && b.trail != null && b.data instanceof Color data) {
                float z = Draw.z();
                Draw.z(z - 0.0001f);
                b.trail.draw(data == Color.white ? Utils.c9.set(HIPal.rainBowRed).shiftHue(b.time * 2) : data, trailWidth);
                Draw.z(z);
            }
        }
    }

    public static class SpriteBulletType extends BulletType {
        public String sprite;
        public float width, height;

        public SpriteBulletType(String sprite, float width, float height) {
            this.sprite = sprite;
            this.width = width;
            this.height = height;
            damage = 0;
            collides = false;
            speed = 0;
            lifetime = 60;
            hitEffect = despawnEffect = Fx.none;
            hittable = false;
            absorbable = false;
            keepVelocity = false;
        }

        public SpriteBulletType(String sprite) {
            this(sprite, 96, 96);
        }

        @Override
        public void draw(Bullet b) {
            super.draw(b);
            if (!(b.data instanceof Color data)) return;
            Draw.z(Layer.bullet);
            Draw.color(data == Color.white ? Utils.c10.set(HIPal.rainBowRed).shiftHue(b.time * 2) : data);
            Draw.rect(Core.atlas.find(sprite), b.x, b.y, width * b.fout(), height * b.fout(), 0);
        }
    }
}