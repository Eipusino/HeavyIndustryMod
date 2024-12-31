package heavyindustry.entities.bullet;

import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class EffectBulletType extends BulletType {
    public EffectBulletType() {
        super();
        hittable = false;
        despawnEffect = hitEffect = shootEffect = smokeEffect = trailEffect = Fx.none;
        absorbable = collides = collidesAir = collidesGround = collidesTeam = collidesTiles = collideFloor = collideTerrain = false;
        hitSize = 0;
        speed = 0.0001f;
        drawSize = 120f;
    }

    public EffectBulletType(float lifetime) {
        this();
        this.lifetime = lifetime;
    }

    public EffectBulletType(float lifetime, float damage, float splashDamage) {
        this(lifetime);
        this.damage = damage;
        this.splashDamage = splashDamage;
    }

    @Override
    public void draw(Bullet b) {}

    @Override
    public void drawLight(Bullet b) {}
}
