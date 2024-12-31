package heavyindustry.world.blocks.environment;

import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static heavyindustry.util.Utils.*;

public class AtlasFloor extends Floor {
    public TextureRegion[] splitRegion;

    public AtlasFloor(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        splitRegion = split(name + "-full", 32, 0);
    }

    @Override
    public void drawBase(Tile tile) {
        Mathf.rand.setSeed(tile.pos());
        Draw.rect(splitRegion[Mathf.randomSeed(tile.pos(), 0, Math.max(0, splitRegion.length - 1))], tile.worldx(), tile.worldy());

        Draw.alpha(1f);
        drawEdges(tile);
        drawOverlay(tile);
    }
}
