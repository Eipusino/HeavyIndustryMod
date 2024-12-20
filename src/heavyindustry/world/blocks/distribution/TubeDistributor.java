package heavyindustry.world.blocks.distribution;

import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.draw.*;

import static arc.Core.*;
import static heavyindustry.util.Utils.*;
import static mindustry.Vars.*;

public class TubeDistributor extends Router {
    public DrawBlock drawer = new DrawTubeDistributor();

    public TubeDistributor(String name) {
        super(name);
        rotate = true;
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{region};
    }

    public static class DrawTubeDistributor extends DrawBlock {
        public TextureRegion bottomRegion, topRegion, rotatorRegion, lockedRegion1, lockedRegion2;

        @Override
        public void draw(Building build) {
            if (!(build instanceof TubeDistributorBuild bu)) return;
            Draw.z(Layer.blockUnder);
            Draw.rect(bottomRegion, bu.x, bu.y);
            Draw.z(Layer.block - 0.2f);
            bu.drawItem();
            Draw.z(Layer.block - 0.15f);
            Drawf.spinSprite(rotatorRegion, bu.x, bu.y, bu.rot % 360);
            Draw.rect(topRegion, bu.x, bu.y);
            Draw.rect(bu.rotation > 1 ? lockedRegion2 : lockedRegion1, bu.x, bu.y, bu.rotdeg());
        }

        @Override
        public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
            Draw.rect(block.region, plan.drawx(), plan.drawy(), plan.rotation * 90f);
        }

        @Override
        public void load(Block block) {
            bottomRegion = atlas.find(block.name + "-bottom");
            topRegion = atlas.find(block.name + "-top");
            rotatorRegion = atlas.find(block.name + "-rotator");
            lockedRegion1 = atlas.find(block.name + "-locked-side1");
            lockedRegion2 = atlas.find(block.name + "-locked-side2");
        }
    }

    public class TubeDistributorBuild extends RouterBuild {
        public Item lastItem;
        public Tile lastInput;
        public int lastTargetAngle, lastSourceAngle;
        public float time, rot, angle, lastRot;
        public int lastRotation = rotation;

        @Override
        public void updateTile() {
            if (lastItem == null && items.any()) {
                lastItem = items.first();
            }

            if (lastItem != null) {
                time += 1f / speed * delta();

                Building target = getTileTarget(lastItem, lastInput, false);

                if (target == null && time >= 0.7f) {
                    rot = lastRot;
                    time = 0.7f;
                }

                if (target != null && (time >= 1f)) {
                    getTileTarget(lastItem, lastInput, true);
                    target.handleItem(this, lastItem);
                    items.remove(lastItem, 1);
                    lastItem = null;
                }

                if (lastInput != null && lastItem != null) {
                    int sa = sourceAngle(), ta = targetAngle();

                    angle = (sa == 0) ? (ta == 2 ? 1 : (ta == 0 || ta == 3) ? -1 : 1) :
                            (sa == 2) ? (ta == 0 || ta == 1) ? -1 : 1 :
                                    (sa == 1) ? (ta == 0 || ta == 3) ? -1 : 1 :
                                            (ta == 0 || ta == 1) ? 1 : -1;
                }

                if (items.total() > 0 && !state.isPaused()) {
                    lastRot = rot;
                    rot += speed * angle * delta();
                }
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return team == source.team && lastItem == null && items.total() == 0 && front() != source;
        }

        @Override
        public void handleItem(Building source, Item item) {
            items.add(item, 1);
            lastItem = item;
            time = 0f;
            lastInput = source.tile();
        }

        @Override
        public int removeStack(Item item, int amount) {
            int result = super.removeStack(item, amount);
            if (result != 0 && item == lastItem) {
                lastItem = null;
            }
            return result;
        }

        public int sourceAngle() {
            for (int sourceAngle = 0; sourceAngle < 4; sourceAngle++) {
                if (nearby(sourceAngle) == lastInput.build) {
                    lastSourceAngle = sourceAngle;
                    return sourceAngle;
                }
            }
            return lastSourceAngle;
        }

        public int targetAngle() {
            Building target = getTileTarget(lastItem, lastInput, false);
            if (target != null) {
                for (int targetAngle = 0; targetAngle < 4; targetAngle++) {
                    if (nearby(targetAngle) == target) {
                        lastTargetAngle = targetAngle;
                        return targetAngle;
                    }
                }
            }
            return lastTargetAngle;
        }

        public void drawItem() {
            if (lastInput != null && lastInput.build != null && lastItem != null) {
                boolean isf = reverse(sourceAngle()) == targetAngle() || sourceAngle() == targetAngle();
                boolean alignment = targetAngle() == 0 || targetAngle() == 2;
                float ox, oy, s = size * 4, s2 = s * 2;
                float linearMove = (float) Math.sin(Math.PI * time) / 2.4f * s;

                if (alignment) {
                    if (isf) {
                        if (sourceAngle() == targetAngle()) {
                            oy = time >= 0.5f ? linearMove : -linearMove;
                            ox = time >= 0.5f ? (time * s2 - s) * (targetAngle() == 0 ? 1 : -1)
                                    : (time * s2 - s) * (targetAngle() == 0 ? -1 : 1);
                        } else {
                            oy = linearMove;
                            ox = (time * s2 - s) * (targetAngle() == 0 ? 1 : -1);
                        }
                    } else {
                        oy = sourceAngle() == 1 ? (time * -s + s) : (time * s - s);
                        ox = time * s * (targetAngle() == 0 ? 1 : -1);
                    }
                } else {
                    if (isf) {
                        if (sourceAngle() == targetAngle()) {
                            ox = time >= 0.5f ? linearMove : -linearMove;
                            oy = time >= 0.5f ? (time * s2 - s) * (targetAngle() == 1 ? 1 : -1)
                                    : (time * s2 - s) * (targetAngle() == 1 ? -1 : 1);
                        } else {
                            ox = (float) Math.sin(Math.PI * time) / 2.4f * s;
                            oy = (time * s2 - s) * (targetAngle() == 1 ? 1 : -1);
                        }
                    } else {
                        ox = sourceAngle() == 0 ? (time * -s + s) : (time * s - s);
                        oy = time * s * (targetAngle() == 1 ? 1 : -1);
                    }
                }

                Draw.rect(lastItem.fullIcon, x + ox, y + oy, itemSize, itemSize);
            }
        }

        @Override
        public void draw() {
            drawer.draw(this);
        }

        public Building getTileTarget(Item item, Tile from, boolean set) {
            int counter = lastRotation;
            for (int i = 0; i < proximity.size; i++) {
                Building other = proximity.get((i + counter) % proximity.size);
                if (set) lastRotation = ((byte) ((lastRotation + 1) % proximity.size));
                if (other.tile == from && from.block() == Blocks.overflowGate) continue;
                if (other.acceptItem(this, item)) {
                    return other;
                }
            }
            return null;
        }
    }
}
