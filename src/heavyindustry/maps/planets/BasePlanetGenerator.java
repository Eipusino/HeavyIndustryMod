package heavyindustry.maps.planets;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.content.*;
import mindustry.maps.generators.*;
import mindustry.world.*;

public abstract class BasePlanetGenerator extends PlanetGenerator {
    protected static int lastSeed = 0;

    protected final int seed = lastSeed++;

    public Block[][] arr = {};
    public float scl = 0.f;
    public float waterOffset = 0.f;
    public float water = 0.f;
    protected ObjectMap<Block, Block> dec = new ObjectMap<>();

    protected ObjectMap<Block, Block> tars = new ObjectMap<>();

    public BasePlanetGenerator() {}

    public Color getColor(Vec3 position) {
        Block block = getBlock(position);

        Tmp.c1.set(block.mapColor).a = 1 - block.albedo;
        return Tmp.c1;
    }

    public Block getBlock(Vec3 pos) {
        float height = rawHeight(pos);

        Tmp.v31.set(pos);
        pos = Tmp.v33.set(pos).scl(scl);
        float rad = scl;
        float temp = Mathf.clamp(Math.abs(pos.y * 2) / rad);
        float tnoise = Simplex.noise3d(seed, 7d, 0.56d, 1d / 3d, pos.x, pos.y + 999d, pos.z);
        temp = Mathf.lerp(temp, tnoise, 0.5f);
        height *= 0.9f;
        height = Mathf.clamp(height);

        float tar = Simplex.noise3d(seed, 4, 0.55, 0.5, pos.x, pos.y + 999, pos.z) * 0.3f + Tmp.v31.dst(0, 0, 1) * 0.2f;
        Block res = arr[
                Mathf.clamp(Mathf.floor(temp * arr.length), 0, arr[0].length - 1)][Mathf.clamp(Mathf.floor(height * arr[0].length), 0, arr[0].length - 1)
                ];

        if (tar > 0.5) {
            return tars.get(res, res);
        } else {
            return res;
        }

    }

    public float rawHeight(Vec3 pos) {
        pos = Tmp.v33.set(pos);
        pos.scl(scl);

        return (Mathf.pow(Simplex.noise3d(seed, 7, 0.5, 1d / 3d, pos.x, pos.y, pos.z), 2.3f) + waterOffset) / (1 + waterOffset);
    }

    public float getHeight(Vec3 position) {
        float height = rawHeight(position);
        return Math.max(height, water);
    }

    public void genTile(Vec3 position, TileGen tile) {
        tile.floor = getBlock(position);
        tile.block = tile.floor.asFloor().wall;

        if (Ridged.noise3d(seed, position.x, position.y, position.z, 22) > 0.32) {
            tile.block = Blocks.air;
        }
    }
}
