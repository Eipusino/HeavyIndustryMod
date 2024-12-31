package heavyindustry.world.blocks.production;

import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.world.blocks.production.*;
import mindustry.world.draw.*;

public class DrawerDrill extends Drill {
    public DrawBlock drawer = new DrawDefault();

    public DrawerDrill(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    public TextureRegion[] icons() {
        return drawer.icons(this);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);
    }

    public class DrawerDrillBuild extends DrillBuild {
        @Override
        public void draw() {
            drawer.draw(this);
            if (dominantItem != null && drawMineItem) {
                Draw.color(dominantItem.color);
                Draw.rect(itemRegion, x, y);
                Draw.color();
            }

            drawTeamTop();
        }
    }
}
