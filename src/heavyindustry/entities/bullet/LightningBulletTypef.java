package heavyindustry.entities.bullet;

import arc.graphics.g2d.*;
import arc.util.*;
import arc.util.pooling.*;
import heavyindustry.content.*;
import heavyindustry.world.lightning.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class LightningBulletTypef extends BulletType {
    public LightningBulletTypef(float time, float damage) {
        super(time, damage);
    }

    public LightningBulletTypef() {
        super();
    }

    @Override
    public void init(Bullet bullet) {
        super.init(bullet);

        LightningContainer cont;
        bullet.data = cont = Pools.obtain(LightningContainer.PoolLightningContainer.class, LightningContainer.PoolLightningContainer::new);

        init(bullet, cont);
    }

    public void init(Bullet b, LightningContainer cont) {}

    @Override
    public void update(Bullet b) {
        super.update(b);

        if (b.data instanceof LightningContainer c) {
            update(b, c);
        }
    }

    @Override
    public void draw(Bullet b) {
        super.draw(b);

        if (b.data instanceof LightningContainer c) {
            draw(b, c);
        }
    }

    public void draw(Bullet b, LightningContainer c) {
        Draw.color(b.type.hitColor);
        c.draw(b.x, b.y);
    }

    public void update(Bullet bullet, LightningContainer container) {
        container.update();
    }

    @Override
    public void removed(Bullet b) {
        super.removed(b);

        if (b.data instanceof LightningContainer c) {
            HIFx.lightningCont.at(b.x, b.y, 0, b.type.hitColor, c);
            Time.run(210, () -> Pools.free(c));
        }
    }
}
