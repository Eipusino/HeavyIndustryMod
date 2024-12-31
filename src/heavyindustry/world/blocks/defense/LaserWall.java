package heavyindustry.world.blocks.defense;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import heavyindustry.gen.*;
import heavyindustry.graphics.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class LaserWall extends Wall {
    public float range = 480f;
    public float warmupSpeed = 0.075f;
    public float minActivate = 0.3f;

    public DrawBlock drawer = new DrawDefault();

    public Shooter generateType = new Shooter(100f);

    public LaserWall(String name) {
        super(name);
        config(Integer.class, (Cons2<LaserWallBuild, Integer>) Linkablec::linkPos);

        update = true;
        configurable = true;
        solid = true;
        hasPower = true;
        hasShadow = true;

        ambientSound = loopSound = Sounds.pulse;
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
        return drawer.finalIcons(this);
    }

    @Override
    public void getRegionsToOutline(Seq<TextureRegion> out) {
        drawer.getRegionsToOutline(this, out);
    }

    @Override
    public void init() {
        super.init();

        generateType.drawSize = Math.max(generateType.drawSize, range * 2);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.damage, generateType.estimateDPS(), StatUnit.perSecond);
        stats.add(Stat.range, (int) (range / tilesize), StatUnit.blocks);
    }

    public static class Shooter extends BulletType {
        public Color[] colors = {Pal.techBlue.cpy().mul(0.9f, 0.95f, 0.95f, 0.3f), Pal.techBlue.cpy().mul(1f, 1f, 1f, 0.6f), Pal.techBlue, Color.white};
        public float[] strokes = {1.25f, 1.05f, 0.65f, 0.3f};
        public float width = 6f, oscScl = 1.25f, oscMag = 0.85f;

        public Shooter(float damage) {
            super(0, damage);

            hitEffect = Fx.hitBeam;
            despawnEffect = Fx.none;
            hitSize = 4;
            drawSize = 420f;
            lifetime = 36f;

            incendAmount = 3;
            incendSpread = 8;
            incendChance = 0.6f;
            hitColor = lightColor = lightningColor = Pal.techBlue;
            impact = true;
            keepVelocity = false;
            collides = false;
            pierce = true;
            hittable = false;
            absorbable = false;

            status = StatusEffects.shocked;
            statusDuration = 300f;

            lightning = 0;
            lightningDamage = 120f;
            lightningLength = 12;
            lightningLengthRand = 12;

            hitShake = 0.25f;
        }

        public Shooter() {
            this(1);
        }

        @Override
        public float estimateDPS() {
            return damage * 100f / 5f * 3f;
        }

        @Override
        public void update(Bullet b) {
            if (!(b.data instanceof Building build)) return;

            //damage every 5 ticks
            if (b.timer(1, 5f)) {
                Damage.collideLine(b, b.team, hitEffect, b.x, b.y, b.rotation(), b.dst(build), true, false);
            }

            if (hitShake > 0) {
                Effect.shake(hitShake, hitShake, b);
            }

            if (headless) return;

            if (b.timer(1, 18f) || Mathf.chanceDelta(0.02)) {
                PositionLightning.createEffect(b, build, lightningColor, 2, Mathf.random(1.25f, 2.25f));
            }

            if (Mathf.chanceDelta(0.075)) {
                PositionLightning.createEffect(b, build, lightningColor, 0, 0);
            }
        }

        @Override
        public void draw(Bullet b) {
            if (!(b.data instanceof LaserWallBuild build)) return;

            for (int s = 0; s < colors.length; s++) {
                Draw.color(Tmp.c1.set(colors[s]).mul(1f + Mathf.absin(Time.time, 1f, 0.1f)));
                Draw.z(Layer.bullet);
                Lines.stroke((width + Mathf.absin(Time.time, oscScl, oscMag)) * b.fdata * b.fout() * strokes[s]);
                Lines.line(b.x, b.y, build.x, build.y, false);

                Draw.z(Layer.bullet + 0.1f);
                Fill.circle(b.x, b.y, Lines.getStroke() * 0.75f);
                Fill.circle(build.x, build.y, Lines.getStroke() * 0.75f);
            }

            Drawf.light(b.x, b.y, build.x, build.y, width * strokes[0] * 1.5f, lightColor, 0.7f);
            Draw.reset();
        }

        @Override
        public void drawLight(Bullet b) {
        }
    }

    public class LaserWallBuild extends Building implements Linkablec {
        public float warmup;
        protected transient LaserWallBuild target;
        protected int linkPos = -1;
        protected Bullet shooter;

        @Override
        public void updateTile() {
            if (!linkValid()) {
                target = null;
                linkPos = -1;
            }

            if (power.status > 0.5f && canActivate()) warmup = Mathf.lerpDelta(warmup, 1, warmupSpeed);
            else warmup = Mathf.lerpDelta(warmup, 0, warmupSpeed);

            if (warmup > minActivate && canActivate()) {
                if (shooter == null) shooter = generateType.create(this, x, y, angleTo(target));
                shooter.data(target);
                shooter.damage = generateType.damage * warmup;
                shooter.time(0);
            } else shooter = null;

            if (shooter != null) shooter.fdata = warmup;
        }

        @Override
        public boolean onConfigureBuildTapped(Building other) {
            if (this == other || linkPos() == other.pos()) {
                configure(-1);
                return false;
            }
            if (other.within(this, range())) {
                configure(other.pos());
                return false;
            }
            return true;
        }

        @Override
        public void drawConfigure() {
            Color color = getLinkColor();

            Drawf.dashCircle(x, y, range(), color);

            if (target != null) {
                float fin = Interp.smoother.apply(Drawn.cycle_100());
                Drawf.square(Mathf.lerp(x, target.x, fin), Mathf.lerp(y, target.y, fin), size * tilesize / 6f, Drawn.rotator_90(Drawn.cycle_100(), 0) + 45, color);

                Drawn.posSquareLink(color, 1f, tilesize * size / 6f, true, this, target);
                Drawf.square(target.x, target.y, target.block.size * tilesize / 2f, color);
            }

            Drawf.square(x, y, size * tilesize / 2f, color);
        }

        @Override
        public void draw() {
            drawer.draw(this);
        }

        @Override
        public void drawLight() {
            super.drawLight();
            drawer.drawLight(this);
        }

        @Override
        public void read(Reads read, byte revision) {
            linkPos = read.i();
            warmup = read.f();
        }

        @Override
        public void created() {
            linkPos(linkPos);
            WorldRegister.postAfterLoad(() -> linkPos(linkPos));
        }

        @Override
        public void write(Writes write) {
            write.i(linkPos);
            write.f(warmup);
        }

        @Override
        public boolean linkValid(Building b) {
            return b instanceof LaserWallBuild build && b.team == team && b.isValid() && build.link() != this;
        }

        public boolean canActivate() {
            return target != null;
        }

        @Override
        public Building link() {
            return target;
        }

        @Override
        public int linkPos() {
            return linkPos;
        }

        @Override
        public void linkPos(int value) {
            linkPos = value;
            if (linkValid(world.build(linkPos))) {
                target = (LaserWallBuild) world.build(linkPos);
            } else {
                target = null;
                linkPos = -1;
            }
        }

        @Override
        public Color getLinkColor() {
            return team.color;
        }

        @Override
        public float range() {
            return range;
        }

        @Override
        public void afterDestroyed() {
            super.afterDestroyed();

            if (canActivate() && warmup > minActivate) for (int i = 0; i < 8; i++) {
                Time.run(i * 5, () -> {
                    for (int j = 0; j < 3; j++)
                        Lightning.create(Team.derelict, generateType.lightningColor, generateType.lightningDamage, x, y, Mathf.random(360), generateType.lightningLength + Mathf.random(generateType.lightningLengthRand));
                });
            }
        }

        @Override
        public float warmup() {
            return warmup;
        }
    }
}
