package heavyindustry.world.blocks.environment;

import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static heavyindustry.util.Utils.*;

public class AtlasProp extends Prop {
    public TextureRegion[] splitRegion;

    /** The pixel size of each atlas section, It must be the same as the source file. */
    public int atlasSize = 32;

    public AtlasProp(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        splitRegion = split(name + "-full", atlasSize, 0);
    }

    @Override
    public void drawBase(Tile tile) {
        Draw.z(layer);
        Draw.rect(splitRegion[Mathf.randomSeed(tile.pos(), 0, Math.max(0, splitRegion.length - 1))], tile.worldx(), tile.worldy());
    }
}
