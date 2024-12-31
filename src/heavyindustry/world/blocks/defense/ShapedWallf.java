package heavyindustry.world.blocks.defense;

import arc.graphics.g2d.*;
import arc.math.geom.*;
import mindustry.gen.*;
import mindustry.graphics.*;

import static arc.Core.*;
import static heavyindustry.util.SpriteUtils.*;
import static mindustry.Vars.*;

public class ShapedWallf extends ShapedWall {
    public TextureRegion[] atlasRegion;
    public TextureRegion[] topLargeRegion, topSmallRegion, topHorizontalRegion, topVerticalRegion;

    public ShapedWallf(String name) {
        super(name);
        size = 1;
        insulated = true;
        absorbLasers = true;
        placeableLiquid = true;
        crushDamageMultiplier = 1f;
    }

    @Override
    public void load() {
        super.load();
        atlasRegion = jSplit(name + "-atlas", 32, 32, 0, atlasIndex412);
        topLargeRegion = new TextureRegion[2];
        topSmallRegion = new TextureRegion[2];
        topHorizontalRegion = new TextureRegion[2];
        topVerticalRegion = new TextureRegion[2];
        for (int i = 0; i < 2; i++) {
            topLargeRegion[i] = atlas.find(name + "-top-large-" + i);
            topSmallRegion[i] = atlas.find(name + "-top-small-" + i);
            topHorizontalRegion[i] = atlas.find(name + "-top-horizontal-" + i);
            topVerticalRegion[i] = atlas.find(name + "-top-vertical-" + i);
        }
    }

    public class ShapedWallBuildf extends ShapedWallBuild {
        public int drawIndex = 0;
        public int topIdx = 0;

        @Override
        public void updateDrawRegion() {
            drawIndex = 0;

            for (int i = 0; i < orthogonalPos.length; i++) {
                Point2 pos = orthogonalPos[i];
                Building build = world.build(tileX() + pos.x, tileY() + pos.y);
                if (checkWall(build)) {
                    drawIndex += 1 << i;
                }
            }
            for (int i = 0; i < diagonalPos.length; i++) {
                Point2[] posArray = diagonalPos[i];
                boolean out = true;
                for (Point2 pos : posArray) {
                    Building build = world.build(tileX() + pos.x, tileY() + pos.y);
                    if (!(checkWall(build))) {
                        out = false;
                        break;
                    }
                }
                if (out) {
                    drawIndex += 1 << i + 4;

                }
            }

            drawIndex = atlasIndex412map.get(drawIndex);
            updateTopIndex();
        }

        public void updateTopIndex() {
            topIdx = 0;

            if (tileX() % 4 < 2) {
                if (tileX() % 4 == 0 && tileY() % 2 == 0 && validTile(1, 0) && validTile(1, 1) && validTile(0, 1)) {
                    topIdx = 1;
                    return;
                }
                if (tileX() % 4 == 1 && tileY() % 2 == 0 && validTile(-1, 0) && validTile(0, 1) && validTile(-1, 1)) {
                    topIdx = 0;
                    return;
                }
                if (tileX() % 4 == 1 && tileY() % 2 == 1 && validTile(-1, 0) && validTile(-1, -1) && validTile(0, -1)) {
                    topIdx = 0;
                    return;
                }
                if (tileX() % 4 == 0 && tileY() % 2 == 1 && validTile(1, 0) && validTile(1, -1) && validTile(0, -1)) {
                    topIdx = 0;
                    return;
                }
            } else {
                if (tileX() % 4 == 2 && tileY() % 2 == 1 && validTile(1, 0) && validTile(1, 1) && validTile(0, 1)) {
                    topIdx = 2;
                    return;
                }
                if (tileX() % 4 == 3 && tileY() % 2 == 1 && validTile(-1, 0) && validTile(0, 1) && validTile(-1, 1)) {
                    topIdx = 0;
                    return;
                }
                if (tileX() % 4 == 3 && tileY() % 2 == 0 && validTile(-1, 0) && validTile(-1, -1) && validTile(0, -1)) {
                    topIdx = 0;
                    return;
                }
                if (tileX() % 4 == 2 && tileY() % 2 == 0 && validTile(1, 0) && validTile(1, -1) && validTile(0, -1)) {
                    topIdx = 0;
                    return;
                }

                if (tileX() % 4 == 2 && tileY() % 2 == 0 && validTile(1, 0) && validTile(1, 1) && validTile(0, 1)) {
                    topIdx = 1;
                    return;
                }
                if (tileX() % 4 == 3 && tileY() % 2 == 0 && validTile(-1, 0) && validTile(0, 1) && validTile(-1, 1)) {
                    topIdx = 0;
                    return;
                }
                if (tileX() % 4 == 3 && tileY() % 2 == 1 && validTile(-1, 0) && validTile(-1, -1) && validTile(0, -1)) {
                    topIdx = 0;
                    return;
                }
                if (tileX() % 4 == 2 && tileY() % 2 == 1 && validTile(1, 0) && validTile(1, -1) && validTile(0, -1)) {
                    topIdx = 0;
                    return;
                }

            }

            if (tileX() % 2 == 0 && tileY() % 2 == 1 && validTile(1, 0)) {
                topIdx = 5;
                return;
            }
            if (tileX() % 2 == 1 && tileY() % 2 == 1 && validTile(-1, 0)) {
                topIdx = 0;
                return;
            }

            if (tileX() % 2 == 0 && tileY() % 2 == 0 && validTile(1, 0)) {
                topIdx = 6;
                return;
            }
            if (tileX() % 2 == 1 && tileY() % 2 == 0 && validTile(-1, 0)) {
                topIdx = 0;
                return;
            }

            topIdx = (tileX() + tileY()) % 2 == 0 ? 3 : 4;
        }

        public void drawTop() {
            if (topIdx == 0) return;
            if (topIdx == 1) {
                Draw.rect(topLargeRegion[0], x + tilesize / 2f, y + tilesize / 2f);
            }
            if (topIdx == 2) {
                Draw.rect(topLargeRegion[1], x + tilesize / 2f, y + tilesize / 2f);
            }
            if (topIdx == 3) {
                Draw.rect(topSmallRegion[0], x, y);
            }
            if (topIdx == 4) {
                Draw.rect(topSmallRegion[1], x, y);
            }
            if (topIdx == 5) {
                Draw.rect(topHorizontalRegion[0], x + tilesize / 2f, y);
            }
            if (topIdx == 6) {
                Draw.rect(topHorizontalRegion[1], x + tilesize / 2f, y);
            }
            if (topIdx == 7) {
                Draw.rect(topVerticalRegion[0], x, y + tilesize / 2f);
            }
            if (topIdx == 8) {
                Draw.rect(topVerticalRegion[1], x, y + tilesize / 2f);
            }
        }

        public boolean validTile(int x, int y) {
            return world.build(tileX() + x, tileY() + y) != null && world.build(tileX() + x, tileY() + y).block == block();
        }

        @Override
        public void updateProximityWall() {
            connectedWalls.clear();

            for (Point2 point : proximityPos) {
                Building other = world.build(tile.x + point.x, tile.y + point.y);
                if (other == null || other.team != team) continue;
                if (checkWall(other)) {
                    connectedWalls.add((ShapedWallBuildf) other);
                }
            }

            updateDrawRegion();
        }

        @Override
        public void drawTeam() {
            Draw.color(team.color);
            Draw.z(Layer.blockUnder);
            Fill.square(x, y, 5f);
            Draw.color();
        }

        @Override
        public void draw() {
            drawTop();
            Draw.z(Layer.block + 1f);
            Draw.rect(atlasRegion[drawIndex], x, y);
        }
    }
}