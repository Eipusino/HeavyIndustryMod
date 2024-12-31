package heavyindustry.world.draw;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import heavyindustry.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;

import static arc.Core.*;

@SuppressWarnings("unchecked")
public class DrawRegionDynamic<E extends Building> extends DrawBlock {
    public Floatf<E> rotation = e -> 0;
    public Floatf<E> alpha = e -> 1;
    public Func<E, Color> color;

    public TextureRegion region;
    public String suffix = "";

    public boolean spinSprite = false;
    public boolean drawPlan = false;
    public boolean planRotate = true;
    public float x, y;
    /** Any number <=0 disables layer changes. */
    public float layer = -1;

    public boolean makeIcon = false;

    public DrawRegionDynamic(String suffix) {
        this.suffix = suffix;
    }

    public DrawRegionDynamic() {}

    @Override
    public void draw(Building build) {
        E entity = (E) build;

        float alp = alpha.get(entity);
        if (alp <= 0.01f) return;

        float z = Draw.z();

        if (layer > 0) Draw.z(layer);
        if (color != null) Draw.color(color.get(entity));

        Draw.alpha(alp);

        if (spinSprite) {
            Drawf.spinSprite(region, build.x + x, build.y + y, rotation.get(entity));
        } else {
            Draw.rect(region, build.x + x, build.y + y, rotation.get(entity));
        }
        Draw.color();
        Draw.z(z);
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        if (!drawPlan) return;
        Draw.rect(region, plan.drawx(), plan.drawy(), planRotate ? plan.rotation * 90 : 0);
    }

    @Override
    public TextureRegion[] icons(Block block) {
        return makeIcon ? new TextureRegion[]{region} : Utils.EMP_REGIONS;
    }

    @Override
    public void load(Block block) {
        region = atlas.find(block.name + suffix);
    }
}
