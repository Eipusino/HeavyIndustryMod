package heavyindustry.world.blocks.distribution;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.distribution.StackConveyor.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

/**
 * Stack Helper
 */
public class StackHelper extends Block {
    public StackHelper(String name) {
        super(name);
        update = true;
        solid = true;
        group = BlockGroup.transportation;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        x *= tilesize;
        y *= tilesize;
        x += offset;
        y += offset;

        for (int i = 0; i < 4; i++) {
            Rect rect = Tmp.r1;
            rect.setCentered(x, y, size * tilesize);
            float len = tilesize * size;

            rect.x += Geometry.d4x(i) * len;
            rect.y += Geometry.d4y(i) * len;

            Drawf.dashRect(valid ? Pal.accent : Pal.remove, rect);
        }
    }

    public class StackHelperBuild extends Building {
        @Override
        public void updateTile() {
            for (int i = 0; i < 4; i++) {
                Building b = nearby(i);
                if (b instanceof StackConveyorBuild sc && b.block instanceof StackConveyor && b.team == team && sc.state == 1)
                    sc.cooldown = 0;
            }
        }

        @Override
        public void drawSelect() {
            for (int i = 0; i < 4; i++) {
                Building b = nearby(i);
                if (b instanceof StackConveyorBuild sc && b.block instanceof StackConveyor && b.team == team && sc.state == 1) {
                    float sin = Mathf.absin(Time.time, 5, 1);
                    Lines.stroke(sin * 1.3f, Pal.accent);
                    Lines.square(b.x, b.y, b.block.size * 5, 0);
                }
            }
        }
    }
}
