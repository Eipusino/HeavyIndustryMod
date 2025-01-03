package heavyindustry.world.blocks.production;

import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.struct.*;
import heavyindustry.graphics.*;
import heavyindustry.world.blocks.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.production.*;

import java.util.concurrent.atomic.*;

import static mindustry.Vars.*;

public class LinkCrafter extends GenericCrafter implements MultiBlock {
    public final Seq<Point2> linkPos = new Seq<>();
    public final IntSeq linkSize = new IntSeq();

    public LinkCrafter(String name) {
        super(name);

        rotate = true;
        rotateDraw = true;
        quickRotate = false;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return super.canPlaceOn(tile, team, rotation) && checkLink(tile, team, size, rotation);
    }

    @Override
    public void placeBegan(Tile tile, Block previous) {
        createPlaceholder(tile, size);
    }

    @Override
    public Seq<Point2> getLinkBlockPos() {
        return linkPos;
    }

    @Override
    public IntSeq getLinkBlockSize() {
        return linkSize;
    }

    public class LinkCrafterBuild extends GenericCrafterBuild implements MultiBuild {
        public boolean linkCreated = false;
        public Seq<Building> linkEntities;
        //ordered seq, target-source pair
        public Seq<Building[]> linkProximityMap;
        public int dumpIndex = 0;

        @Override
        public void created() {
            super.created();
            linkProximityMap = new Seq<>();
        }

        @Override
        public void updateTile() {
            if (!linkCreated) {
                linkEntities = setLinkBuild(this, tile, team, size, rotation);
                linkCreated = true;
                updateLinkProximity();
            }
            super.updateTile();
        }

        @Override
        public boolean dump(Item toDump) {
            if (!block.hasItems || items.total() == 0 || linkProximityMap.size == 0 || (toDump != null && !items.has(toDump)))
                return false;
            int dump = dumpIndex;
            for (int i = 0; i < linkProximityMap.size; i++) {
                int idx = (i + dump) % linkProximityMap.size;
                Building[] pair = linkProximityMap.get(idx);
                Building target = pair[0];
                Building source = pair[1];

                if (toDump == null) {
                    for (int ii = 0; ii < content.items().size; ii++) {
                        if (!items.has(ii)) continue;
                        Item item = content.items().get(ii);
                        if (target.acceptItem(source, item) && canDump(target, item)) {
                            target.handleItem(source, item);
                            items.remove(item, 1);
                            incrementDumpIndex(linkProximityMap.size);
                            return true;
                        }
                    }
                } else {
                    if (target.acceptItem(source, toDump) && canDump(target, toDump)) {
                        target.handleItem(source, toDump);
                        items.remove(toDump, 1);
                        incrementDumpIndex(linkProximityMap.size);
                        return true;
                    }
                }
                incrementDumpIndex(linkProximityMap.size);
            }
            return false;
        }

        @Override
        public void offload(Item item) {
            produced(item, 1);
            int dump = dumpIndex;
            for (int i = 0; i < linkProximityMap.size; i++) {
                incrementDumpIndex(linkProximityMap.size);
                int idx = (i + dump) % linkProximityMap.size;
                Building[] pair = linkProximityMap.get(idx);
                Building target = pair[0];
                Building source = pair[1];
                if (target.acceptItem(source, item) && canDump(target, item)) {
                    target.handleItem(source, item);
                    return;
                }
            }
            handleItem(this, item);
        }

        public void incrementDumpIndex(int proX) {
            dumpIndex = ((dumpIndex + 1) % proX);
        }

        @Override
        public void updateLinkProximity() {
            if (linkEntities != null) {
                linkProximityMap.clear();
                //add link entity's proximity
                for (Building link : linkEntities) {
                    for (Building linkProX : link.proximity) {
                        if (linkProX != this && !linkEntities.contains(linkProX)) {
                            if (checkValidPair(linkProX, link)) {
                                linkProximityMap.add(new Building[]{linkProX, link});
                            }
                        }
                    }
                }

                //add self entity's proximity
                for (Building proX : proximity) {
                    if (!linkEntities.contains(proX)) {
                        if (checkValidPair(proX, this)) {
                            linkProximityMap.add(new Building[]{proX, this});
                        }
                    }
                }
            }
        }

        public boolean checkValidPair(Building target, Building source) {
            for (Building[] pair : linkProximityMap) {
                Building pairTarget = pair[0];
                Building pairSource = pair[1];

                if (target == pairTarget) {
                    if (target.relativeTo(pairSource) == target.relativeTo(source)) {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
            updateLinkProximity();
        }

        @Override
        public void remove() {
            removeLink(tile, size, rotation);
            createPlaceholder(tile, size);
            super.remove();
        }

        @Override
        public void drawSelect() {
            super.drawSelect();

            AtomicInteger i = new AtomicInteger(0);

            linkProximityMap.each(pair -> {
                if (dumpIndex == i.get()) {
                    Draw.color(Pal.techBlue);
                } else {
                    Draw.color(Pal.remove);
                }

                Draw.alpha(0.5f);
                Draw.z(Layer.block + 3f);

                Building target = pair[0];
                Building source = pair[1];

                Lines.line(target.x, target.y, source.x, source.y);
                Fill.square(target.x, target.y, target.hitSize() / 2f - 2f);

                Drawn.drawText(i + "", target.x, target.y);
                i.getAndIncrement();
            });
            Drawn.drawText(linkProximityMap.size + "", x, y);
        }
    }
}
