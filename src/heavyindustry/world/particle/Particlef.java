package heavyindustry.world.particle;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.*;
import arc.util.pooling.Pool.*;
import heavyindustry.struct.*;
import heavyindustry.world.components.*;
import heavyindustry.world.particle.Particlef.*;
import mindustry.entities.*;
import mindustry.gen.*;

import java.util.*;

/**
 * The entity class of particles defines entity objects that can be drawn and updated.
 * The trajectory of a particle can be changed by setting its speed, position, and deflection method.
 * Typically, this particle has an upper limit on its quantity and should be safe in performance under normal circumstances.
 * Comes with controllable trailing.
 */
public class Particlef extends Decal implements ExtraVariableComp, Iterable<Cloud> {
    protected static final ObjectSet<Particlef> all = new ObjectSet<>();
    protected static final Seq<Particlef> temp = new Seq<>();

    /**
     * The maximum number of coexisting particles,
     * when the total amount is greater than this number, creating new particles will clear the first generated particle.
     */
    public static int maxAmount = 1024;

    private static int counter = 0;

    public Map<String, Object> extraVar = new CollectionObjectMap<>();
    public int maxCloudCounts = -1;
    public Particlef parent;
    /** Particlef velocity, vector. */
    public Vec2 speed = new Vec2();
    /** The current size of the particle. */
    public float size;
    public float defSpeed;
    public float defSize;
    /** The particle model determines the behavior of the particle. */
    public ParticleModel model;
    public float layer;

    protected Vec2 startPos = new Vec2();
    protected float clipSize;

    Cloud currentCloud, firstCloud;
    int cloudCount;

    public static int count() {
        return all.size;
    }

    public static Seq<Particlef> get(Boolf<Particlef> filter) {
        temp.clear();
        for (Particlef particle : all) {
            if (filter.get(particle)) temp.add(particle);
        }
        return temp;
    }

    public float cloudCount() {
        return cloudCount;
    }

    @Override
    public void add() {
        index__all = Groups.all.addIndex(this);
        index__draw = Groups.draw.addIndex(this);

        all.add(this);
        counter++;

        currentCloud = Pools.get(Cloud.class, Cloud::new, 65536).obtain();
        currentCloud.x = x;
        currentCloud.y = y;
        currentCloud.size = 0;
        currentCloud.color.set(model.trailColor(this));

        firstCloud = currentCloud;

        added = true;

        model.init(this);

        if (counter >= maxAmount) {
            remove();
        }
    }

    @Override
    public void draw() {
        float l = Draw.z();
        Draw.z(layer);

        if (parent != null) {
            x += parent.x;
            y += parent.y;
        }

        model.draw(this);

        if (currentCloud != null) {
            model.drawTrail(this);
        }

        if (parent != null) {
            x -= parent.x;
            y -= parent.y;
        }

        Draw.z(l);
        Draw.reset();
    }

    @Override
    public void update() {
        model.deflect(this);

        x += speed.x * Time.delta;
        y += speed.y * Time.delta;

        size = model.currSize(this);

        model.update(this);

        Cloud c = Pools.get(Cloud.class, Cloud::new, 65536).obtain();
        c.x = parent == null ? x : x + parent.x;
        c.y = parent == null ? y : y + parent.y;
        c.size = size;
        c.color.set(model.trailColor(this));

        c.perCloud = currentCloud;
        currentCloud.nextCloud = c;

        currentCloud = c;

        cloudCount++;

        for (Cloud cloud : currentCloud) {
            model.updateTrail(this, cloud);
        }

        boolean mark = false;
        while (firstCloud.nextCloud != null) {
            if (maxCloudCounts > 0 && cloudCount > maxCloudCounts || model.isFaded(this, firstCloud)) {
                mark = !(maxCloudCounts > 0 && cloudCount > maxCloudCounts);
                popFirst();
            } else break;
        }

        if (!mark && (parent != null && !parent.isAdded() || model.isFinal(this))) {
            popFirst();
            if (cloudCount > 4) popFirst();
        }

        if (cloudCount <= 4 && model.isFinal(this)) remove();
    }

    private void popFirst() {
        Cloud n = firstCloud.nextCloud;
        n.perCloud = null;
        Pools.free(firstCloud);
        firstCloud = n;
        cloudCount--;
    }

    @Override
    public void remove() {
        if (added) {
            Groups.all.removeIndex(this, index__all);
            index__all = -1;
            Groups.draw.removeIndex(this, index__draw);
            index__draw = -1;
            Groups.queueFree(this);

            all.remove(this);
            counter--;
            added = false;
        }
    }

    @Override
    public int classId() {
        return 102;
    }

    @Override
    public float clipSize() {
        return clipSize = Math.max(Tmp.v1.set(x, y).sub(startPos).len(), clipSize);
    }

    @Override
    public void reset() {
        added = false;
        parent = null;
        id = EntityGroup.nextId();
        lifetime = 0;
        region = null;
        rotation = 0;
        time = 0;
        x = 0;
        y = 0;

        maxCloudCounts = -1;

        speed.setZero();
        startPos.setZero();

        layer = 0;
        clipSize = 0;

        while (firstCloud.nextCloud != null) {
            popFirst();
        }
        Pools.free(firstCloud);

        currentCloud = null;
        firstCloud = null;

        cloudCount = 0;
        size = 0;
        extra().clear();

        model = null;

        color.set(Color.white);
    }

    @Override
    public Iterator<Cloud> iterator() {
        return currentCloud.iterator();
    }

    @Override
    public Map<String, Object> extra() {
        return extraVar;
    }

    public static class Cloud implements Poolable, Iterable<Cloud> {
        public final Color color = new Color();

        public float x, y, size;
        public Cloud perCloud, nextCloud;

        Itr itr = new Itr();

        public void draw() {
            draw(1, 1);
        }

        public void draw(float modulate, float modulateNext) {
            Draw.color(color);

            if (perCloud != null && nextCloud != null) {
                float angle = Angles.angle(x - perCloud.x, y - perCloud.y);
                float dx1 = Angles.trnsx(angle + 90, size * modulate);
                float dy1 = Angles.trnsy(angle + 90, size * modulate);
                angle = Angles.angle(nextCloud.x - x, nextCloud.y - y);
                float dx2 = Angles.trnsx(angle + 90, nextCloud.size * modulateNext);
                float dy2 = Angles.trnsy(angle + 90, nextCloud.size * modulateNext);

                Fill.quad(x + dx1, y + dy1, x - dx1, y - dy1, nextCloud.x - dx2, nextCloud.y - dy2, nextCloud.x + dx2, nextCloud.y + dy2);
            } else if (perCloud == null && nextCloud != null) {
                float angle = Angles.angle(nextCloud.x - x, nextCloud.y - y);
                float dx2 = Angles.trnsx(angle + 90, nextCloud.size * modulate);
                float dy2 = Angles.trnsy(angle + 90, nextCloud.size * modulate);

                Fill.quad(x, y, x, y, nextCloud.x - dx2, nextCloud.y - dy2, nextCloud.x + dx2, nextCloud.y + dy2);
            }
        }

        @Override
        public void reset() {
            x = 0;
            y = 0;
            size = 0;
            color.set(Color.clear);

            perCloud = null;
            nextCloud = null;
        }

        @Override
        public Iterator<Cloud> iterator() {
            itr.reset();
            return itr;
        }

        class Itr implements Iterator<Cloud> {
            Cloud curr = Cloud.this;

            public void reset() {
                curr = Cloud.this;
            }

            @Override
            public boolean hasNext() {
                return curr.perCloud != null;
            }

            @Override
            public Cloud next() {
                return curr = curr.perCloud;
            }
        }
    }
}
