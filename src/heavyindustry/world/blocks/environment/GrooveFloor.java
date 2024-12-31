package heavyindustry.world.blocks.environment;

import arc.graphics.g2d.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static mindustry.Vars.*;

public class GrooveFloor extends AtlasFloor {
    public GrooveFloor(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
    }

    @Override
    protected boolean doEdge(Tile tile, Tile otherTile, Floor other) {
        return false;
    }

    @Override
    public void drawBase(Tile tile) {
        Draw.rect(splitRegion[getTileIndex(tile)], tile.worldx(), tile.worldy());
        Draw.alpha(1f);
        drawEdges(tile);
        drawOverlay(tile);
    }

    private byte getTileIndex(Tile tile) {
        byte index = 0;
        if (world.floor(tile.x, tile.y + 1) == this) index += 1;
        if (world.floor(tile.x + 1, tile.y) == this) index += 2;
        if (world.floor(tile.x, tile.y - 1) == this) index += 4;
        if (world.floor(tile.x - 1, tile.y) == this) index += 8;
        return index;
    }
}
