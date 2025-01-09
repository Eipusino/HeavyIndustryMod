package heavyindustry.util;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.math.*;
import arc.math.geom.*;
import arc.math.geom.QuadTree.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.ImageButton.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.*;
import arc.util.pooling.Pool.*;
import heavyindustry.content.*;
import heavyindustry.gen.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.pattern.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.draw.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static arc.Core.*;
import static heavyindustry.core.HeavyIndustryMod.*;
import static mindustry.Vars.*;

/**
 * Input-output utilities, providing very specific functions that aren't really commonly used, but often enough to require me to write a class for it.
 *
 * @author Eipusino
 */
public final class Utils {
    public static final TextureRegion[] EMP_REGIONS = new TextureRegion[0];

    private static final String DONOR = bundle.get("hi-donor-item");
    private static final String DEVELOPER = bundle.get("hi-developer-item");

    public static Color
            c1 = new Color(), c2 = new Color(), c3 = new Color(), c4 = new Color(), c5 = new Color(),
            c6 = new Color(), c7 = new Color(), c8 = new Color(), c9 = new Color(), c10 = new Color();

    public static Vec2
            v1 = new Vec2(), v2 = new Vec2(), v3 = new Vec2();

    public static final Rand rand = new Rand(0);

    public static final String[] packages = {
            "heavyindustry",
            "heavyindustry.ai",
            "heavyindustry.content",
            "heavyindustry.core",
            "heavyindustry.entities",
            "heavyindustry.entities.abilities",
            "heavyindustry.entities.bullet",
            "heavyindustry.entities.effect",
            "heavyindustry.entities.part",
            "heavyindustry.entities.pattern",
            "heavyindustry.files",
            "heavyindustry.func",
            "heavyindustry.game",
            "heavyindustry.gen",
            "heavyindustry.graphics",
            "heavyindustry.graphics.g2d",
            "heavyindustry.graphics.g3d",
            "heavyindustry.graphics.g3d.model",
            "heavyindustry.graphics.g3d.model.obj",
            "heavyindustry.graphics.g3d.model.obj.mtl",
            "heavyindustry.graphics.g3d.model.obj.obj",
            "heavyindustry.graphics.g3d.render",
            "heavyindustry.input",
            "heavyindustry.io",
            "heavyindustry.maps",
            "heavyindustry.maps.planets",
            "heavyindustry.math",
            "heavyindustry.math.gravity",
            "heavyindustry.mod",
            "heavyindustry.net",
            "heavyindustry.struct",
            "heavyindustry.type",
            "heavyindustry.type.unit",
            "heavyindustry.type.weapons",
            "heavyindustry.type.weather",
            "heavyindustry.ui",
            "heavyindustry.ui.components",
            "heavyindustry.ui.defaults",
            "heavyindustry.ui.dialogs",
            "heavyindustry.ui.elements",
            "heavyindustry.ui.elements.markdown",
            "heavyindustry.ui.elements.markdown.elemdraw",
            "heavyindustry.ui.elements.markdown.extensions",
            "heavyindustry.ui.elements.markdown.highlighter",
            "heavyindustry.ui.elements.markdown.highlighter.defaults",
            "heavyindustry.ui.fragment",
            "heavyindustry.ui.listeners",
            "heavyindustry.ui.tooltips",
            "heavyindustry.util",
            "heavyindustry.util.path",
            "heavyindustry.util.pools",
            "heavyindustry.world",
            "heavyindustry.world.blocks",
            //"heavyindustry.world.blocks.campaign",
            "heavyindustry.world.blocks.defense",
            "heavyindustry.world.blocks.defense.turrets",
            "heavyindustry.world.blocks.distribution",
            "heavyindustry.world.blocks.environment",
            "heavyindustry.world.blocks.heat",
            "heavyindustry.world.blocks.liquid",
            "heavyindustry.world.blocks.logic",
            "heavyindustry.world.blocks.payload",
            "heavyindustry.world.blocks.power",
            "heavyindustry.world.blocks.production",
            "heavyindustry.world.blocks.sandbox",
            "heavyindustry.world.blocks.storage",
            "heavyindustry.world.blocks.units",
            "heavyindustry.world.components",
            "heavyindustry.world.consumers",
            "heavyindustry.world.draw",
            "heavyindustry.world.lightning",
            "heavyindustry.world.lightning.generator",
            "heavyindustry.world.meta",
            "heavyindustry.world.particle",
            "heavyindustry.world.particle.model"
    };

    public static final Class<Integer> IC = Integer.class;
    public static final Class<int[]> IAC = int[].class;

    public static Seq<UnlockableContent> donorItems = new Seq<>();
    public static Seq<UnlockableContent> developerItems = new Seq<>();

    private static Tile tileParma;
    private static Posc result;
    private static float cDist;
    private static final Vec2 tV = new Vec2(), tV2 = new Vec2(), tV3 = new Vec2();
    private static final IntSet collidedBlocks = new IntSet();
    private static final Rect rect = new Rect(), hitRect = new Rect();
    private static final IntSeq buildIdSeq = new IntSeq();
    private static final Seq<Tile> tiles = new Seq<>();
    private static final Seq<Unit> units = new Seq<>();
    private static final Seq<Hit> hitEffects = new Seq<>();
    private static Building tmpBuilding;
    private static Unit tmpUnit;

    /** Don't let anyone instantiate this class. */
    private Utils() {}

    public static void loadItems() {
        for (UnlockableContent c : donorItems) {
            c.description = (c.description == null ? DONOR : c.description + "\n" + DONOR);
        }
        for (UnlockableContent c : developerItems) {
            c.description = (c.description == null ? DEVELOPER : c.description + "\n" + DEVELOPER);
        }
    }

    @Contract(pure = true)
    public static int reverse(int rotation) {
        return switch (rotation) {
            case 0 -> 2; case 2 -> 0; case 1 -> 3; case 3 -> 1;
            default -> throw new IllegalStateException("Unexpected value: " + rotation);
        };
    }

    public static TextureRegion[][] splitLayers(String name, int size, int layerCount) {
        TextureRegion[][] layers = new TextureRegion[layerCount][];

        for (int i = 0; i < layerCount; i++) {
            layers[i] = split(name, size, i);
        }
        return layers;
    }

    public static TextureRegion[] split(String name, int size, int layer) {
        TextureRegion textures = atlas.find(name, name("error"));
        int margin = 0;
        int countX = textures.width / size;
        TextureRegion[] tiles = new TextureRegion[countX];

        for (int i = 0; i < countX; i++) {
            tiles[i] = new TextureRegion(textures, i * (margin + size), layer * (margin + size), size, size);
        }
        return tiles;
    }

    /**
     * Gets multiple regions inside a {@link TextureRegion}.
     *
     * @param width  The amount of regions horizontally.
     * @param height The amount of regions vertically.
     */
    public static TextureRegion[] split(String name, int size, int width, int height) {
        TextureRegion textures = atlas.find(name);
        int textureSize = width * height;
        TextureRegion[] regions = new TextureRegion[textureSize];

        float tileWidth = (textures.u2 - textures.u) / width;
        float tileHeight = (textures.v2 - textures.v) / height;

        for (int i = 0; i < textureSize; i++) {
            float tileX = ((float) (i % width)) / width;
            float tileY = ((float) (i / width)) / height;
            TextureRegion region = new TextureRegion(textures);

            //start coordinate
            region.u = Mathf.map(tileX, 0f, 1f, region.u, region.u2) + tileWidth * 0.02f;
            region.v = Mathf.map(tileY, 0f, 1f, region.v, region.v2) + tileHeight * 0.02f;
            //end coordinate
            region.u2 = region.u + tileWidth * 0.96f;
            region.v2 = region.v + tileHeight * 0.96f;

            region.width = region.height = size;

            regions[i] = region;
        }
        return regions;
    }

    /** Same thing like the drawer from {@link UnitType} without applyColor and outlines. */
    public static void simpleUnitDrawer(Unit unit) {
        UnitType type = unit.type;

        Draw.rect(type.region, unit.x, unit.y, unit.rotation - 90f);
        float rotation = unit.rotation - 90f;
        for (WeaponMount mount : unit.mounts) {
            Weapon weapon = mount.weapon;

            float weaponRotation = rotation + (weapon.rotate ? mount.rotation : 0f);
            float recoil = -(mount.reload / weapon.reload * weapon.recoil);

            float wx = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y) + Angles.trnsx(weaponRotation, 0f, recoil);
            float wy = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y) + Angles.trnsy(weaponRotation, 0f, recoil);

            Draw.rect(weapon.region, wx, wy, weapon.region.width * Draw.scl * -Mathf.sign(weapon.flipSprite), weapon.region.height * Draw.scl, weaponRotation);
        }
    }

    /** {@link Tile#relativeTo(int, int)} does not account for building rotation. */
    public static int relativeDirection(Building from, Building to) {
        if (from == null || to == null) return -1;
        if (from.x == to.x && from.y > to.y) return (7 - from.rotation) % 4;
        if (from.x == to.x && from.y < to.y) return (5 - from.rotation) % 4;
        if (from.x > to.x && from.y == to.y) return (6 - from.rotation) % 4;
        if (from.x < to.x && from.y == to.y) return (4 - from.rotation) % 4;
        return -1;
    }

    public static DrawBlock base() {
        return base(0f);
    }

    public static DrawBlock base(float rotatorSpeed) {
        return new DrawMulti(new DrawRegion("-rotator", rotatorSpeed), new DrawDefault(), new DrawRegion("-top"));
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static Position pos(float x, float y) {
        return new Position() {
            @Override
            public float getX() {
                return x;
            }

            @Override
            public float getY() {
                return y;
            }
        };
    }

    public static float dx(float px, float r, float angle) {
        return px + r * (float) Math.cos(angle * Math.PI / 180);
    }

    public static float dy(float py, float r, float angle) {
        return py + r * (float) Math.sin(angle * Math.PI / 180);
    }

    public static float posX(float x, float length, float angle) {
        float a = (float) ((Math.PI * angle) / 180);
        float cos = (float) Math.cos(a);
        return x + length * cos;
    }

    public static float posY(float y, float length, float angle) {
        float a = (float) ((Math.PI * angle) / 180);
        float sin = (float) Math.sin(a);
        return y + length * sin;
    }

    public static boolean isInstanceButNotSubclass(Object obj, Class<?> clazz) {
        if (clazz.isInstance(obj)) {
            try {
                if (getClassSubclassHierarchy(obj.getClass()).contains(clazz)) {
                    return false;
                }
            } catch (ClassCastException e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public static Seq<Class<?>> getClassSubclassHierarchy(Class<?> clazz) {
        Class<?> c = clazz.getSuperclass();
        Seq<Class<?>> hierarchy = new Seq<>();
        while (c != null) {
            hierarchy.add(c);
            Class<?>[] interfaces = c.getInterfaces();
            hierarchy.addAll(Arrays.asList(interfaces));
            c = c.getSuperclass();
        }
        return hierarchy;
    }

    public static Seq<Turret> turrets() {
        Seq<Turret> turretSeq = new Seq<>();
        int size = content.blocks().size;
        for (int i = 0; i < size; i++) {
            Block b = content.block(i);
            if (b instanceof Turret t) {
                turretSeq.addUnique(t);
            }
        }
        return turretSeq;
    }

    /** turret and unit only, not use contents.bullets() */
    public static Seq<BulletType> bulletTypes() {//use item
        Seq<BulletType> bullets = new Seq<>();
        for (Turret t : turrets()) {
            if (t instanceof ItemTurret it) {
                for (Item i : it.ammoTypes.keys()) {
                    BulletType b = it.ammoTypes.get(i);
                    if (t.shoot.shots == 1 || b instanceof PointBulletType || b instanceof ArtilleryBulletType) {
                        bullets.add(b);
                    } else {
                        BulletType bulletType = new BulletType() {{
                            fragBullet = b;
                            fragBullets = t.shoot.shots;
                            fragAngle = 0;
                            if (t.shoot instanceof ShootSpread s) {
                                fragSpread = s.spread;
                            }
                            fragRandomSpread = t.inaccuracy;
                            fragVelocityMin = 1 - t.velocityRnd;
                            absorbable = hittable = collides = collidesGround = collidesAir = false;
                            despawnHit = true;
                            lifetime = damage = speed = 0;
                            hitEffect = despawnEffect = Fx.none;
                        }};
                        bullets.add(bulletType);
                    }
                }
            }
        }
        return bullets;
    }

    //use for cst bullet
    public static Bullet anyOtherCreate(Bullet bullet, BulletType bt, Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY) {
        bullet.type = bt;
        bullet.owner = owner;
        bullet.team = team;
        bullet.time = 0f;
        bullet.originX = x;
        bullet.originY = y;
        if (!(aimX == -1f && aimY == -1f)) {
            bullet.aimTile = world.tileWorld(aimX, aimY);
        }
        bullet.aimX = aimX;
        bullet.aimY = aimY;

        bullet.initVel(angle, bt.speed * velocityScl);
        if (bt.backMove) {
            bullet.set(x - bullet.vel.x * Time.delta, y - bullet.vel.y * Time.delta);
        } else {
            bullet.set(x, y);
        }
        bullet.lifetime = bt.lifetime * lifetimeScl;
        bullet.data = data;
        bullet.drag = bt.drag;
        bullet.hitSize = bt.hitSize;
        bullet.mover = mover;
        bullet.damage = (damage < 0 ? bt.damage : damage) * bullet.damageMultiplier();
        if (bullet.trail != null) {
            bullet.trail.clear();
        }
        bullet.add();

        if (bt.keepVelocity && owner instanceof Velc v) bullet.vel.add(v.vel());

        return bullet;
    }

    public static void liquid(ObjectMap<Integer, Cons<Liquid>> cons, String name, Color color, float exp, float fla, float htc, float vis, float temp) {
        for (int i = 1; i < 10; i++) {
            int index = i;
            Liquid liquid = new Liquid(name + index, color) {{
                explosiveness = exp * index;
                flammability = fla * index;
                heatCapacity = htc * index;
                viscosity = vis * index;
                temperature = temp / index;
            }};
            if (cons != null && cons.size > 0 && cons.containsKey(i)) {
                cons.get(i).get(liquid);
            }
        }
    }

    public static void liquid(String name, Color color, float exp, float fla, float htc, float vis, float temp) {
        liquid(null, name, color, exp, fla, htc, vis, temp);
    }

    public static void item(ObjectMap<Integer, Cons<Item>> cons, String name, Color color, float exp, float fla, float cos, float radio, float chg, float health) {
        for (int i = 1; i < 10; i++) {
            int index = i;
            Item item = new Item(name + index, color) {{
                explosiveness = exp * index;
                flammability = fla * index;
                cost = cos * index;
                radioactivity = radio * index;
                charge = chg * index;
                healthScaling = health * index;
            }};
            if (cons != null && cons.size > 0 && cons.containsKey(i)) {
                cons.get(i).get(item);
            }
        }
    }

    /**
     * 1. Cannot use {@link mindustry.Vars#content}
     * 2. Cannot be used for init() anymore
     *
     * @author guiY
     */
    public static void test() {
        int size = 40;
        for (Liquid liquid : new Liquid[]{Liquids.water, Liquids.slag, Liquids.oil, Liquids.cryofluid,
                Liquids.arkycite, Liquids.gallium, Liquids.neoplasm,
                Liquids.ozone, Liquids.hydrogen, Liquids.nitrogen, Liquids.cyanogen}) {
            if (liquid.hidden) continue;
            ObjectMap<Integer, Cons<Liquid>> cons = getEntries(liquid, size);
            liquid(cons, liquid.name, liquid.color, liquid.explosiveness, liquid.flammability, liquid.heatCapacity, liquid.viscosity, liquid.temperature);
        }

        for (Item item : new Item[]{Items.scrap, Items.copper, Items.lead, Items.graphite, Items.coal, Items.titanium, Items.thorium, Items.silicon, Items.plastanium,
                Items.phaseFabric, Items.surgeAlloy, Items.sporePod, Items.sand, Items.blastCompound, Items.pyratite, Items.metaglass,
                Items.beryllium, Items.tungsten, Items.oxide, Items.carbide, Items.fissileMatter, Items.dormantCyst}) {
            if (item.hidden) continue;
            ObjectMap<Integer, Cons<Item>> cons = getEntries(item, size);
            item(cons, item.name, item.color, item.explosiveness, item.flammability, item.cost, item.radioactivity, item.charge, item.healthScaling);
        }
        Draw.color();
    }

    private static ObjectMap<Integer, Cons<Item>> getEntries(Item item, int size) {
        ObjectMap<Integer, Cons<Item>> cons = new ObjectMap<>();
        for (int i = 1; i < 10; i++) {
            int fi = i;
            cons.put(i, it -> {
                PixmapRegion base = atlas.getPixmap(item.uiIcon);
                Pixmap mix = base.crop();
                AtlasRegion number = atlas.find(name("number-" + fi));
                if (number.found()) {
                    PixmapRegion region = TextureAtlas.blankAtlas().getPixmap(number);

                    mix.draw(region.pixmap, region.x, region.y, region.width, region.height, 0, base.height - size, size, size, false, true);
                }

                it.uiIcon = it.fullIcon = new TextureRegion(new Texture(mix));

                it.buildable = item.buildable;
                it.hardness = item.hardness + fi;
                it.lowPriority = item.lowPriority;
            });
        }
        return cons;
    }

    private static ObjectMap<Integer, Cons<Liquid>> getEntries(Liquid liquid, int size) {
        ObjectMap<Integer, Cons<Liquid>> cons = new ObjectMap<>();
        for (int i = 1; i < 10; i++) {
            int fi = i;
            cons.put(i, ld -> {
                PixmapRegion base = atlas.getPixmap(liquid.uiIcon);
                Pixmap mix = base.crop();
                AtlasRegion number = atlas.find(name("number-" + fi));
                if (number.found()) {
                    PixmapRegion region = TextureAtlas.blankAtlas().getPixmap(number);

                    mix.draw(region.pixmap, region.x, region.y, region.width, region.height, 0, base.height - size, size, size, false, true);
                }

                ld.uiIcon = ld.fullIcon = new TextureRegion(new Texture(mix));
            });
        }
        return cons;
    }

    public static ImageButton selfStyleImageButton(Drawable imageUp, ImageButtonStyle is, Runnable listener) {
        ImageButton ib = new ImageButton(new ImageButtonStyle(null, null, null, imageUp, null, null));
        ImageButtonStyle style = new ImageButtonStyle(is);
        style.imageUp = imageUp;
        ib.setStyle(style);
        if (listener != null) ib.changed(listener);
        return ib;
    }

    public static void drawTiledFramesBar(float w, float h, float x, float y, Liquid liquid, float alpha) {
        TextureRegion region = renderer.fluidFrames[liquid.gas ? 1 : 0][liquid.getAnimationFrame()];

        Draw.color(liquid.color, liquid.color.a * alpha);
        Draw.rect(region, x + w / 2f, y + h / 2f, w, h);
        Draw.color();
    }

    public static void extinguish(Team team, float x, float y, float range, float intensity) {
        indexer.eachBlock(team, x, y, range, b -> true, b -> Fires.extinguish(b.tile, intensity));
    }

    public static void extinguish(Teamc teamc, float range, float intensity) {
        indexer.eachBlock(teamc.team(), teamc.x(), teamc.y(), range, b -> true, b -> Fires.extinguish(b.tile, intensity));
    }

    public static Position collideBuild(Team team, float x1, float y1, float x2, float y2, Boolf<Building> boolf) {
        tmpBuilding = null;

        boolean found = World.raycast(World.toTile(x1), World.toTile(y1), World.toTile(x2), World.toTile(y2),
                (x, y) -> (tmpBuilding = world.build(x, y)) != null && tmpBuilding.team != team && boolf.get(tmpBuilding));

        return found ? tmpBuilding : v1.set(x2, y2);
    }

    public static Position collideBuildOnLength(Team team, float x1, float y1, float length, float ang, Boolf<Building> boolf) {
        v2.trns(ang, length).add(x1, y1);
        return collideBuild(team, x1, y1, v2.x, v2.y, boolf);
    }

    public static float findLaserLength(Bullet b, float angle, float length) {
        Tmp.v1.trnsExact(angle, length);

        tileParma = null;

        boolean found = World.raycast(b.tileX(), b.tileY(), World.toTile(b.x + Tmp.v1.x), World.toTile(b.y + Tmp.v1.y),
                (x, y) -> (tileParma = world.tile(x, y)) != null && tileParma.team() != b.team && tileParma.block().absorbLasers);

        return found && tileParma != null ? Math.max(6f, b.dst(tileParma.worldx(), tileParma.worldy())) : length;
    }

    public static void collideLine(Bullet hitter, Team team, Effect effect, float x, float y, float angle, float length, boolean large, boolean laser) {
        if (laser) length = findLaserLength(hitter, angle, length);

        collidedBlocks.clear();
        tV.trnsExact(angle, length);

        Intc2 collider = (cx, cy) -> {
            Building tile = world.build(cx, cy);
            boolean collide = tile != null && collidedBlocks.add(tile.pos());

            if (hitter.damage > 0) {
                float health = !collide ? 0 : tile.health;

                if (collide && tile.team != team && tile.collide(hitter)) {
                    tile.collision(hitter);
                    hitter.type.hit(hitter, tile.x, tile.y);
                }

                //try to heal the tile
                if (collide && hitter.type.testCollision(hitter, tile)) {
                    hitter.type.hitTile(hitter, tile, cx * tilesize, cy * tilesize, health, false);
                }
            }
        };

        if (hitter.type.collidesGround) {
            tV2.set(x, y);
            tV3.set(tV2).add(tV);
            World.raycastEachWorld(x, y, tV3.x, tV3.y, (cx, cy) -> {
                collider.get(cx, cy);

                for (Point2 p : Geometry.d4) {
                    Tile other = world.tile(p.x + cx, p.y + cy);
                    if (other != null && (large || Intersector.intersectSegmentRectangle(tV2, tV3, other.getBounds(Tmp.r1)))) {
                        collider.get(cx + p.x, cy + p.y);
                    }
                }
                return false;
            });
        }

        rect.setPosition(x, y).setSize(tV.x, tV.y);
        float x2 = tV.x + x, y2 = tV.y + y;

        if (rect.width < 0) {
            rect.x += rect.width;
            rect.width *= -1;
        }

        if (rect.height < 0) {
            rect.y += rect.height;
            rect.height *= -1;
        }

        float expand = 3f;

        rect.y -= expand;
        rect.x -= expand;
        rect.width += expand * 2;
        rect.height += expand * 2;

        Cons<Unit> cons = unit -> {
            unit.hitbox(hitRect);

            Vec2 vec = Geometry.raycastRect(x, y, x2, y2, hitRect.grow(expand * 2));

            if (vec != null && hitter.damage > 0) {
                effect.at(vec.x, vec.y);
                unit.collision(hitter, vec.x, vec.y);
                hitter.collision(unit, vec.x, vec.y);
            }
        };

        units.clear();

        Units.nearbyEnemies(team, rect, unit -> {
            if (unit.checkTarget(hitter.type.collidesAir, hitter.type.collidesGround)) {
                units.add(unit);
            }
        });

        units.sort(unit -> unit.dst2(hitter));
        units.each(cons);
    }

    public static void randFadeLightningEffect(float x, float y, float range, float lightningPieceLength, Color color, boolean in) {
        randFadeLightningEffectScl(x, y, range, 0.55f, 1.1f, lightningPieceLength, color, in);
    }

    public static void randFadeLightningEffectScl(float x, float y, float range, float sclMin, float sclMax, float lightningPieceLength, Color color, boolean in) {
        v1.rnd(range).scl(Mathf.random(sclMin, sclMax)).add(x, y);
        (in ? HIFx.chainLightningFadeReversed : HIFx.chainLightningFade).at(x, y, lightningPieceLength, color, v1.cpy());
    }

    public static Unit teleportUnitNet(Unit before, float x, float y, float angle, Player player) {
        if (net.active() || headless) {
            if (player != null) {
                player.set(x, y);
                player.snapInterpolation();
                player.snapSync();
                player.lastUpdated = player.updateSpacing = 0;
            }
            before.set(x, y);
            before.snapInterpolation();
            before.snapSync();
            before.updateSpacing = 0;
            before.lastUpdated = 0;
        } else {
            before.set(x, y);
        }
        before.rotation = angle;
        return before;
    }

    /**
     * @implNote Get all the {@link Tile} {@code tile} within a certain range at certain position.
     * @param x the abscissa of search center.
     * @param y the ordinate of search center.
     * @param range the search range.
     * @param bool {@link Boolf} {@code lambda} to determine whether the condition is true.
     * @return {@link Seq}{@code <Tile>} - which contains eligible {@link Tile} {@code tile}.
     */
    public static Seq<Tile> getAcceptableTiles(int x, int y, int range, Boolf<Tile> bool) {
        Seq<Tile> tiles = new Seq<>(true, (int)(Mathf.pow(range, 2) * Mathf.pi), Tile.class);
        Geometry.circle(x, y, range, (x1, y1) -> {
            if ((tileParma = world.tile(x1, y1)) != null && bool.get(tileParma)) {
                tiles.add(world.tile(x1, y1));
            }
        });
        return tiles;
    }

    private static void clearTmp() {
        tileParma = null;
        buildIdSeq.clear();
        tiles.clear();
    }

    public static Color getColor(Color defaultColor, Team team) {
        return defaultColor == null ? team.color : defaultColor;
    }

    public static void limitRangeWithoutNew(ItemTurret turret, float margin) {
        for (ObjectMap.Entry<Item, BulletType> entry : turret.ammoTypes.entries()) {
            entry.value.lifetime = (turret.range + margin) / entry.value.speed;
        }
    }

    //not support server
    public static void spawnSingleUnit(UnitType type, Team team, int spawnNum, float x, float y) {
        for (int spawned = 0; spawned < spawnNum; spawned++) {
            Time.run(spawned * Time.delta, () -> {
                Unit unit = type.create(team);
                if (unit != null) {
                    unit.set(x, y);
                    unit.add();
                }
            });
        }
    }

    public static float regSize(UnitType type) {
        return type.hitSize / tilesize / tilesize / 3.25f;
    }

    /**[0]For flying, [1] for navy, [2] for ground */
    public static Seq<Boolf<Tile>> formats() {
        Seq<Boolf<Tile>> seq = new Seq<>(3);

        seq.add(
                tile -> world.getQuadBounds(Tmp.r1).contains(tile.getBounds(Tmp.r2)),
                tile -> tile.floor().isLiquid && !tile.cblock().solid && !tile.floor().solid && !tile.overlay().solid && !tile.block().solidifes,
                tile -> !tile.floor().isDeep() && !tile.cblock().solid && !tile.floor().solid && !tile.overlay().solid && !tile.block().solidifes
        );

        return seq;
    }

    public static Boolf<Tile> ableToSpawn(UnitType type) {
        Boolf<Tile> boolf;

        Seq<Boolf<Tile>> boolves = formats();

        if (type.flying) {
            boolf = boolves.get(0);
        } else if (WaterMovec.class.isAssignableFrom(type.constructor.get().getClass())) {
            boolf = boolves.get(1);
        } else {
            boolf = boolves.get(2);
        }

        return boolf;
    }

    public static Seq<Tile> ableToSpawn(UnitType type, float x, float y, float range) {
        Seq<Tile> tSeq = new Seq<>(Tile.class);

        Boolf<Tile> boolf = ableToSpawn(type);

        return tSeq.addAll(getAcceptableTiles(World.toTile(x), World.toTile(y), World.toTile(range), boolf));
    }

    public static boolean ableToSpawnPoints(Seq<Vec2> spawnPoints, UnitType type, float x, float y, float range, int num, long seed) {
        Seq<Tile> tSeq = ableToSpawn(type, x, y, range);

        rand.setSeed(seed);
        for (int i = 0; i < num; i++) {
            Tile[] positions = tSeq.shrink();
            if (positions.length < num) return false;
            spawnPoints.add(new Vec2().set(positions[rand.nextInt(positions.length)]));
        }

        return true;
    }

    public static boolean spawnUnit(Team team, float x, float y, float angle, float spawnRange, float spawnReloadTime, float spawnDelay, UnitType type, int spawnNum, Cons<Spawner> modifier) {
        if (type == null) return false;
        clearTmp();
        Seq<Vec2> vectorSeq = new Seq<>();

        if (!ableToSpawnPoints(vectorSeq, type, x, y, spawnRange, spawnNum, rand.nextLong())) return false;

        int i = 0;
        for (Vec2 s : vectorSeq) {
            Spawner spawner = Pools.obtain(Spawner.class, Spawner::new);
            spawner.init(type, team, s, angle, spawnReloadTime + i * spawnDelay);
            modifier.get(spawner);
            if (!net.client()) spawner.add();
            i++;
        }
        return true;
    }

    public static boolean spawnUnit(Team team, float x, float y, float angle, float spawnRange, float spawnReloadTime, float spawnDelay, UnitType type, int spawnNum) {
        return spawnUnit(team, x, y, angle, spawnRange, spawnReloadTime, spawnDelay, type, spawnNum, t -> {});
    }

    public static boolean spawnUnit(Team team, float x, float y, float angle, float spawnRange, float spawnReloadTime, float spawnDelay, UnitType type, int spawnNum, StatusEffect statusEffect, float statusDuration) {
        return spawnUnit(team, x, y, angle, spawnRange, spawnReloadTime, spawnDelay, type, spawnNum, s -> {
            s.setStatus(statusEffect, statusDuration);
        });
    }

    public static boolean spawnUnit(Team team, float x, float y, float angle, float spawnRange, float spawnReloadTime, float spawnDelay, UnitType type, int spawnNum, StatusEffect statusEffect, float statusDuration, double frag) {
        return spawnUnit(team, x, y, angle, spawnRange, spawnReloadTime, spawnDelay, type, spawnNum, s -> {
            s.setStatus(statusEffect, statusDuration);
            s.flagToApply = frag;
        });
    }

    public static void spawnSingleUnit(Team team, float x, float y, float angle, float delay, UnitType type) {
        Spawner spawner = Pools.obtain(Spawner.class, Spawner::new);
        spawner.init(type, team, v1.set(x, y), angle, delay);
        if (!net.client()) spawner.add();
    }

    public static void spawnSingleUnit(Team team, float x, float y, float angle, float delay, UnitType type, Cons<Spawner> modifier) {
        Spawner spawner = Pools.obtain(Spawner.class, Spawner::new);
        spawner.init(type, team, v1.set(x, y), angle, delay);
        modifier.get(spawner);
        if (!net.client()) spawner.add();
    }

    public static <T extends QuadTreeObject> Seq<T> getObjects(QuadTree<T> tree) {
        Seq<T> seq = new Seq<>();

        tree.getObjects(seq);

        return seq;
    }

    public static <T> void shuffle(Seq<T> seq) {
        T[] items = seq.items;
        for (int i = seq.size - 1; i >= 0; i--) {
            int j = Mathf.random(i);
            T temp = items[i];
            items[i] = items[j];
            items[j] = temp;
        }
    }

    public static Rand rand(long id) {
        rand.setSeed(id);
        return rand;
    }

    public static boolean friendly(Liquid l) {
        return l.effect != StatusEffects.none && l.effect.damage <= 0.1f && (l.effect.damage < -0.01f || l.effect.healthMultiplier > 1.01f || l.effect.damageMultiplier > 1.01f);
    }

    public static Bullet nearestBullet(float x, float y, float range, Boolf<Bullet> boolf) {
        result = null;
        cDist = range;
        Tmp.r1.setCentered(x, y, range * 2);
        Groups.bullet.intersect(Tmp.r1.x, Tmp.r1.y, Tmp.r1.width, Tmp.r1.height, b -> {
            float dst = b.dst(x, y);
            if (boolf.get(b) && b.within(x, y, range + b.hitSize) && (result == null || dst < cDist)) {
                result = b;
                cDist = dst;
            }
        });

        return (Bullet) result;
    }

    public static float angleDistSigned(float a, float b) {
        a = (a + 360f) % 360f;
        b = (b + 360f) % 360f;

        float d = Math.abs(a - b) % 360f;
        int sign = (a - b >= 0f && a - b <= 180f) || (a - b <= -180f && a - b >= -360f) ? 1 : -1;

        return (d > 180f ? 360f - d : d) * sign;
    }

    public static float angleDistSigned(float a, float b, float start) {
        float dst = angleDistSigned(a, b);
        if (Math.abs(dst) > start) {
            return dst > 0 ? dst - start : dst + start;
        }

        return 0f;
    }

    public static float angleDist(float a, float b) {
        float d = Math.abs(a - b) % 360f;
        return (d > 180f ? 360f - d : d);
    }

    public static void shotgun(int points, float spacing, float offset, Floatc cons) {
        for (int i = 0; i < points; i++) {
            cons.get(i * spacing - (points - 1) * spacing / 2f + offset);
        }
    }

    public static float clampedAngle(float angle, float relative, float limit) {
        if (limit >= 180) return angle;
        if (limit <= 0) return relative;
        float dst = angleDistSigned(angle, relative);
        if (Math.abs(dst) > limit) {
            float val = dst > 0 ? dst - limit : dst + limit;
            return (angle - val) % 360f;
        }
        return angle;
    }

    /**
     * Casts forward in a line.
     * @return the first encountered model.
     * There's an issue with the one in 126.2, which I fixed in a pr. This can be removed after the next Mindustry release.
     */
    public static Healthc linecast(Bullet hitter, float x, float y, float angle, float length) {
        tV.trns(angle, length);

        tmpBuilding = null;

        if (hitter.type.collidesGround) {
            World.raycastEachWorld(x, y, x + tV.x, y + tV.y, (cx, cy) -> {
                Building tile = world.build(cx, cy);
                if (tile != null && tile.team != hitter.team) {
                    tmpBuilding = tile;
                    return true;
                }
                return false;
            });
        }

        rect.setPosition(x, y).setSize(tV.x, tV.y);
        float x2 = tV.x + x, y2 = tV.y + y;

        if (rect.width < 0) {
            rect.x += rect.width;
            rect.width *= -1;
        }

        if (rect.height < 0) {
            rect.y += rect.height;
            rect.height *= -1;
        }

        float expand = 3f;

        rect.y -= expand;
        rect.x -= expand;
        rect.width += expand * 2;
        rect.height += expand * 2;

        tmpUnit = null;

        Units.nearbyEnemies(hitter.team, rect, e -> {
            if ((tmpUnit != null && e.dst2(x, y) > tmpUnit.dst2(x, y)) || !e.checkTarget(hitter.type.collidesAir, hitter.type.collidesGround)) return;

            e.hitbox(hitRect);
            Rect other = hitRect;
            other.y -= expand;
            other.x -= expand;
            other.width += expand * 2;
            other.height += expand * 2;

            Vec2 vec = Geometry.raycastRect(x, y, x2, y2, other);

            if (vec != null) {
                tmpUnit = e;
            }
        });

        if (tmpBuilding != null && tmpUnit != null) {
            if (Mathf.dst2(x, y, tmpBuilding.getX(), tmpBuilding.getY()) <= Mathf.dst2(x, y, tmpUnit.getX(), tmpUnit.getY())) {
                return tmpBuilding;
            }
        } else if (tmpBuilding != null) {
            return tmpBuilding;
        }

        return tmpUnit;
    }

    static class Hit implements Poolable {
        Healthc ent;
        float x, y;

        @Override
        public void reset() {
            ent = null;
            x = y = 0f;
        }
    }

    public static class ExtendedPosition implements Position {
        public float x, y;

        public ExtendedPosition set(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }

        @Override
        public float getX() {
            return x;
        }

        @Override
        public float getY() {
            return y;
        }
    }
}
