package heavyindustry.world.blocks.storage;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.storage.*;

/**
 * Space extractor, directly extracting resources from remote sources.
 *
 * @author abomb4 2022-12-10 10:16:40
 */
public class SpaceUnloader extends StorageBlock {
    public static final EntityGroup<Building> suGroup = new EntityGroup<>(Building.class, false, false);

    public Color color = Color.valueOf("0068fc");
    public Effect inEffect = Fx.none;

    /** max loop per frame */
    public int maxLoop = 8;
    /** Extract every few frames */
    public int frameDelay = 5;
    /** Connection limit */
    public int linkLimit = 32;

    public int range = 300;
    public float warmupSpeed = 0.05f;

    public TextureRegion topRegion;
    public TextureRegion bottomRegion;
    public TextureRegion rotatorRegion;

    public SpaceUnloader(String name) {
        super(name);
        canOverdrive = false;
        update = true;
        solid = true;
        hasItems = true;
        configurable = true;
        saveConfig = false;
        noUpdateDisabled = true;

        config(IntSeq.class, (SpaceUnloaderBuild tile, IntSeq seq) -> {
            // This seems only used by coping block
            // Deserialize from IntSeq
            var itemId = seq.get(0);
            IntSeq links = new IntSeq();
            int linkX = -999;
            for (int i = 2; i < seq.size; i++) {
                var num = seq.get(i);
                if (linkX == -999) {
                    linkX = num;
                } else {
                    int pos = Point2.pack(linkX + tile.tileX(), num + tile.tileY());
                    links.add(pos);
                    linkX = -999;
                }
            }
            tile.setItemTypeId(itemId);
            tile.setLink(links);
        });

        config(Integer.class, SpaceUnloaderBuild::setOneLink);

        config(Item.class, (SpaceUnloaderBuild tile, Item v) -> tile.setItemTypeId(v.id));

        configClear((SpaceUnloaderBuild tile) -> tile.setLink(new IntSeq()));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Drawf.dashCircle(x * Vars.tilesize, y * Vars.tilesize, range, Pal.accent);
        super.drawPlace(x, y, rotation, valid);
    }

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top");
        bottomRegion = Core.atlas.find(name + "-bottom");
        rotatorRegion = Core.atlas.find(name + "-rotator");
    }

    @Override
    public void init() {
        if (inEffect == Fx.none)
            inEffect = new Effect(38, e -> {
                Draw.color(color);
                Angles.randLenVectors(e.id, 1, 8 * e.fout(), 0, 360, (x, y) -> {
                    float angle = Angles.angle(0, 0, x, y);
                    float trnsx = Angles.trnsx(angle, 2);
                    float trnsy = Angles.trnsy(angle, 2);
                    float trnsx2 = Angles.trnsx(angle, 4);
                    float trnsy2 = Angles.trnsy(angle, 4);
                    Fill.circle(
                            e.x + trnsx + x + trnsx2 * e.fout(),
                            e.y + trnsy + y + trnsy2 * e.fout(),
                            e.fslope() * 0.8F
                    );
                });
            });
        super.init();
        Events.on(EventType.BlockBuildEndEvent.class, e -> {
            if (!e.breaking) {
                suGroup.each(cen -> {
                    if (cen instanceof SpaceUnloaderBuild sub)
                        sub.tryResumeDeadLink(e.tile.pos());
                });
            }
        });
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("capacity", (SpaceUnloaderBuild e) -> new Bar(
                () -> Core.bundle.format("bar.capacity", UI.formatAmount(e.block.itemCapacity)),
                () -> Pal.items,
                () -> (float) e.items.total() / (e.block.itemCapacity * Vars.content.items().count(UnlockableContent::unlockedNow))
        ));
        addBar("connections", (SpaceUnloaderBuild e) -> new Bar(
                () -> Core.bundle.format("bar.powerlines", linkLimit),
                () -> Pal.items,
                () -> (float) e.links.size / linkLimit
        ));
    }

    @Override
    public boolean outputsItems() {
        return true;
    }

    @Override
    public Object pointConfig(Object config, Cons<Point2> transformer) {
        if (config instanceof IntSeq seq) {
            var newSeq = new IntSeq(seq.size);
            newSeq.add(seq.get(0));
            newSeq.add(seq.get(1));
            boolean linkXed = false;
            int linkX = 0;
            for (int i = 2; i < seq.size; i++) {
                int num = seq.get(i);
                if (linkXed) {
                    linkX = num;
                } else {
                    // The source position is relative to right bottom, transform it.
                    var point = new Point2(linkX * 2 - 1, num * 2 - 1);
                    transformer.get(point);
                    newSeq.add((point.x + 1) / 2);
                    newSeq.add((point.y + 1) / 2);
                }
                linkXed = !linkXed;
            }
            return newSeq;
        } else {
            return config;
        }
    }

    /**
     * Determine whether the target is within the influence range of the
     *
     * @param the    build
     * @param target build
     * @return Is it within the scope of influence
     */
    public boolean linkValidTarget(Building the, Building target) {
        return target != null && target.team == the.team && the.within(target, range);
    }

    /**
     * Determine whether POS is within the influence range of the
     *
     * @param the build
     * @param pos index
     * @return Is it within the scope of influence
     */
    public boolean linkValid(Building the, int pos) {
        if (pos == -1) {
            return false;
        }
        Building build = Vars.world.build(pos);
        return linkValidTarget(the, build);
    }

    public class SpaceUnloaderBuild extends StorageBuild {
        public Interval timer = new Interval(6);
        public IntSeq links = new IntSeq();
        public IntSeq deadLinks = new IntSeq(100);
        public float slowdownDelay = 0;
        public float warmup = 0;
        public float rotateDeg = 0;
        public float rotateSpeed = 0;

        public boolean itemSent = false;
        public Item itemType;

        public int loopIndex = 0;

        /**
         * Batch settings
         *
         * @param linkSeq list
         */
        public void setLink(IntSeq linkSeq) {
            links = linkSeq;
            for (int i = links.size - 1; i >= 0; i--) {
                int link = links.get(i);
                Building linkTarget = Vars.world.build(link);
                if (!linkValidTarget(this, linkTarget)) {
                    links.removeIndex(i);
                } else {
                    links.set(i, linkTarget.pos());
                }
            }
        }

        /**
         * Toggle
         *
         * @param pos pos
         */
        public void setOneLink(int pos) {
            if (!links.removeValue(pos)) {
                links.add(pos);
            }
        }

        /**
         * Move a certain location to 'headLink' and perform position correction when rebuilding the building at the specified location.
         *
         * @param pos 位置
         */
        public void deadLink(int pos) {
            if (Vars.net.client()) {
                return;
            }
            if (links.contains(pos)) {
                configure(pos);
            }
            deadLinks.add(pos);
            if (deadLinks.size >= 100) {
                // 太多了就清掉一些
                deadLinks.removeRange(0, 50);
            }
        }

        /**
         * Attempt to restore
         *
         * @param pos Location code
         */
        public void tryResumeDeadLink(int pos) {
            if (Vars.net.client()) {
                return;
            }
            if (!deadLinks.removeValue(pos)) {
                return;
            }
            Building linkTarget = Vars.world.build(pos);
            if (linkValid(this, pos)) {
                configure(linkTarget.pos());
            }
        }

        public void setItemTypeId(int v) {
            itemType = v < 0 ? null : Vars.content.items().get(v);
        }

        @Override
        public void updateTile() {
            super.updateTile();
            var hasItem = false;
            boolean valid = efficiency > 0.4F;
            if (timer.get(1, frameDelay)) {
                itemSent = false;
                if (valid && itemType != null) {
                    int max = links.size;
                    for (int i = 0; i < Math.min(maxLoop, max); i++) {
                        if (loopIndex < 0) {
                            loopIndex = max - 1;
                        }
                        int index = loopIndex;
                        loopIndex -= 1;
                        int pos = links.get(index);
                        if (pos == -1) {
                            // delete
                            configure(pos);
                            continue;
                        }

                        Building linkTarget = Vars.world.build(pos);
                        if (!linkValidTarget(this, linkTarget)) {
                            deadLink(pos);
                            max -= 1;
                            if (max <= 0) {
                                break;
                            }
                            continue;
                        }

                        if (linkTarget.items == null) {
                            continue;
                        }

                        int count = linkTarget.items.get(itemType);
                        int accept = Math.min(count, acceptStack(itemType, Math.min(count, frameDelay), linkTarget));
                        if (accept > 0) {
                            handleStack(itemType, accept, linkTarget);
                            linkTarget.removeStack(itemType, accept);
                            for (var tmpi = accept; tmpi > 0; tmpi--) {
                                linkTarget.itemTaken(itemType);
                            }
                            hasItem = true;
                        }
                    }
                }

                if (valid && hasItem) {
                    slowdownDelay = 60;
                } else if (!valid) {
                    slowdownDelay = 0;
                }

                if (warmup > 0) {
                    rotateDeg += rotateSpeed;
                }

                if (enabled && rotateSpeed > 0.5 && Mathf.random(60) > 12) {
                    Time.run(Mathf.random(10), () -> inEffect.at(x, y, 0));
                }
                for (var i = 0; i < frameDelay; i++) {
                    dump();
                }
            }

            warmup = Mathf.lerpDelta(warmup, valid ? 1 : 0, warmupSpeed);
            rotateSpeed = Mathf.lerpDelta(rotateSpeed, slowdownDelay > 0 ? 1 : 0, warmupSpeed);
            slowdownDelay = Math.max(0, slowdownDelay - 1);
            if (warmup > 0) {
                rotateDeg += rotateSpeed;
            }
        }

        @Override
        public void display(Table table) {
            super.display(table);
            if (items != null) {
                table.row();
                table.left();
                table.table(l -> {
                    var map = new IntIntMap();
                    l.update(() -> {
                        l.clearChildren();
                        l.left();
                        var seq = new Seq<>(Item.class);
                        items.each((item, amount) -> {
                            map.put(item.id, amount);
                            seq.add(item);
                        });
                        for (IntIntMap.Entry entry : map.entries()) {
                            int id = entry.key;
                            int amount = entry.value;
                            Item item = Vars.content.item(id);
                            l.image(item.uiIcon).padRight(3.0F);
                            l.label(() -> "  " + Strings.fixed(seq.contains(item) ? amount : 0, 0))
                                    .color(Color.lightGray);
                            l.row();
                        }
                    });
                }).left();
            }
        }

        @Override
        public void draw() {
            super.draw();
            Draw.color(Color.valueOf("#0a156e"));
            Draw.alpha(warmup);
            Draw.rect(bottomRegion, x, y);
            Draw.color();

            Draw.alpha(warmup);
            Draw.rect(rotatorRegion, x, y, rotateDeg);

            Draw.alpha(1);
            Draw.rect(topRegion, x, y);

            Draw.color(itemType == null ? Color.clear : itemType.color);
            Draw.rect("unloader-center", x, y);
            Draw.color();
        }

        @Override
        public void drawConfigure() {
            int tilesize = Vars.tilesize;
            float sin = Mathf.absin(Time.time, 6, 1);

            Draw.color(Pal.accent);
            Lines.stroke(1);
            Drawf.circles(x, y, (tile.block().size / 2.0F + 1) * tilesize + sin - 2, Pal.accent);

            for (int i = 0; i < links.size; i++) {
                int pos = links.get(i);
                if (linkValid(this, pos)) {
                    Building linkTarget = Vars.world.build(pos);
                    Drawf.square(linkTarget.x, linkTarget.y, linkTarget.block.size * tilesize / 2.0F + 1, Pal.place);
                }
            }

            Drawf.dashCircle(x, y, range, Pal.accent);
        }

        @Override
        public boolean onConfigureBuildTapped(Building other) {
            if (this == other) {
                configure(-1);
                return false;
            }
            if (dst(other) <= range && other.team == team) {
                configure(other.pos());
                return false;
            }
            return true;
        }

        @Override
        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(table, Vars.content.items(), () -> itemType, this::configure);
        }

        @Override
        public Object config() {
            IntSeq seq = new IntSeq(links.size * 2 + 2);
            seq.add(itemType == null ? -1 : itemType.id);
            seq.add(links.size);
            for (int i = 0; i < links.size; i++) {
                int pos = links.get(i);
                Point2 point2 = Point2.unpack(pos).sub(tile.x, tile.y);
                seq.add(point2.x, point2.y);
            }
            return seq;
        }

        @Override
        public void add() {
            if (added) {
                return;
            }
            suGroup.add(this);
            super.add();
        }

        @Override
        public void remove() {
            if (!added) {
                return;
            }
            suGroup.remove(this);
            super.remove();
        }

        @Override
        public boolean canDump(Building to, Item item) {
            if (linkedCore != null) {
                return false;
            }
            for (int i = 0; i < links.size; i++) {
                int pos = links.get(i);
                Building linkTarget = Vars.world.build(pos);
                if (to == linkTarget) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return linkedCore != null;
        }

        @Override
        public int acceptStack(Item item, int amount, Teamc source) {
            if (linkedCore != null) {
                return linkedCore.acceptStack(item, amount, source);
            } else {
                if (source == null || source.team() == team) {
                    return Math.min(getMaximumAccepted(item) - items.get(item), amount);
                } else {
                    return 0;
                }
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.s(itemType == null ? -1 : itemType.id);
            int s = links.size;
            write.s(s);
            for (int i = 0; i < s; i++) {
                write.i(links.get(i));
            }
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            var id = read.s();
            itemType = id == -1 ? null : Vars.content.items().get(id);
            links = new IntSeq();
            short linkSize = read.s();
            for (int i = 0; i < linkSize; i++) {
                int pos = read.i();
                links.add(pos);
            }
        }
    }
}
