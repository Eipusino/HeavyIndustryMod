package heavyindustry.world.blocks.environment;

import arc.graphics.g2d.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static heavyindustry.util.Utils.*;
import static mindustry.Vars.*;

public class ConnectedWall extends StaticWall {
    protected TextureRegion[] tiles;

    public ConnectedWall(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        tiles = split(name + "-full", 32, 12, 4);
    }

    @Override
    public void drawBase(Tile tile) {
        Tile[][] grid = new Tile[3][3];
        int avail = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grid[i][j] = world.tile(i + tile.x - 1, j + tile.y - 1);
                if (grid[i][j] != null) {
                    avail++;
                }
            }
        }

        int index = getTilingIndex(grid, 1, 1, t -> t != null && t.block() == this);
        if (avail == 0) {
            Draw.rect(region, tile.worldx(), tile.worldy());
        } else {
            Draw.rect(tiles[index], tile.worldx(), tile.worldy());
        }
        if (tile.overlay().wallOre) {
            tile.overlay().drawBase(tile);
        }
    }
}
