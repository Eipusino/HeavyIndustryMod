package heavyindustry.world.blocks.environment;

import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static arc.Core.*;
import static heavyindustry.util.Utils.*;

public class AtlasTallBlock extends TallBlock {
    public TextureRegion[] splitRegion, splitShadowRegion;

    /** The pixel size of each atlas section, It must be the same as the source file. */
    public int atlasSize = 32;

    public AtlasTallBlock(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        region = atlas.find(name);

        splitRegion = split(name + "-full", atlasSize, 0);
        splitShadowRegion = split(name + "-shadow", atlasSize, 0);
    }

    @Override
    public void drawBase(Tile tile) {
        float rot = Mathf.randomSeedRange(tile.pos() + 1, rotationRand);

        Draw.z(Layer.power - 1);
        Draw.color(0f, 0f, 0f, shadowAlpha);
        Draw.rect(splitShadowRegion[Mathf.randomSeed(tile.pos(), 0, Math.max(0, splitShadowRegion.length - 1))], tile.worldx() + shadowOffset, tile.worldy() + shadowOffset, rot);

        Draw.color();

        Draw.z(Layer.power + 1);
        Draw.rect(splitRegion[Mathf.randomSeed(tile.pos(), 0, Math.max(0, splitRegion.length - 1))], tile.worldx(), tile.worldy(), rot);
    }
}
