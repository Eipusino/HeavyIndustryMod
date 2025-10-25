package heavyindustry.util;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Position;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.IntSeq;
import arc.struct.IntSet;
import arc.struct.IntSet.IntSetIterator;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.graphics.HPal;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.core.UI;
import mindustry.entities.Fires;
import mindustry.entities.Mover;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ContinuousBulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Player;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.gen.Velc;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;
import mindustry.world.meta.StatUnit;

/**
 * Input-output utilities, providing very specific functions that aren't really commonly used, but often
 * enough to require me to write a class for it.
 * <p>This type may have class name conflicts with other dependency libraries, but I am unable to make further
 * changes.
 *
 * @author Eipusino
 */
public final class Utils {
	public static final Color
			c1 = new Color(), c2 = new Color(), c3 = new Color(), c4 = new Color(), c5 = new Color(),
			c6 = new Color(), c7 = new Color(), c8 = new Color(), c9 = new Color(), c10 = new Color();

	public static Rect r = new Rect(), r2 = new Rect();
	public static Vec2 v = new Vec2(), vv = new Vec2();

	public static final Rand rand = new Rand(), rand2 = new Rand();

	public static final Team[] baseTeams = {Team.derelict, Team.sharded, Team.crux, Team.green, Team.malis, Team.blue};

	static final Seq<ItemStack> itemStacks = new Seq<>(ItemStack.class);
	static final Seq<Item> items = new Seq<>(Item.class);

	static final IntSeq amounts = new IntSeq();

	/// Don't let anyone instantiate this class.
	private Utils() {}

	public static int reverse(int rotation) {
		return switch (rotation) {
			case 0 -> 2;
			case 2 -> 0;
			case 1 -> 3;
			case 3 -> 1;
			default -> -1;
		};
	}

	public static int getByIndex(IntSet intSet, int index) {
		if (index < 0 || index >= intSet.size) {
			return -1;
		}

		int value = 0;
		int counter = 0;

		IntSetIterator iter = intSet.iterator();
		while (iter.hasNext) {
			int item = iter.next();
			if (counter == index) {
				value = item;
			}
			counter++;
		}

		return counter > index ? value : -1;
	}

	public static void bubbles(int seed, float x, float y, int bubblesAmount, float bubblesSize, float baseLife, float baseSize) {
		rand.setSeed(seed);

		for (int i = 0; i < bubblesAmount; i++) {
			float angle = rand.random(360f);
			float fin = (rand.random(0.8f) * (Time.time / baseLife)) % rand.random(0.1f, 0.6f);
			float len = rand.random(baseSize / 2f, baseSize) / fin;

			float trnsx = x + Angles.trnsx(angle, len, rand.random(baseSize / 4f, baseSize / 4f));
			float trnsy = y + Angles.trnsy(angle, len, rand.random(baseSize / 4f, baseSize / 4f));

			Fill.poly(trnsx, trnsy, 18, Interp.sine.apply(fin * 3.5f) * bubblesSize);
		}
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

	public static Vec2 vecSetLine(Vec2 vec, Vec2 pos, float rotation, float length) {
		vec.setLength(length).setAngle(rotation).add(pos);
		return vec;
	}

	public static Vec2 vecSetLine(Vec2 vec, float x, float y, float rotation, float length) {
		vec.setLength(length).setAngle(rotation).add(x, y);
		return vec;
	}

	public static void quadHelper(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		Fill.quad(x1, y1, x2, y2, x3, y3, x4, y4);
		debugDots(x1, y1, x2, y2, x3, y3, x4, y4);
	}

	public static void quadHelper(TextureRegion region, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		Fill.quad(region, x1, y1, x2, y2, x3, y3, x4, y4);
		debugDots(x1, y1, x2, y2, x3, y3, x4, y4);
	}

	public static void quadHelper(TextureRegion region, Vec2 v1, Vec2 v2, Vec2 v3, Vec2 v4) {
		quadHelper(region, v1.x, v1.y, v2.x, v2.y, v3.x, v3.y, v4.x, v4.y);
	}

	public static void quadHelper(Vec2 v1, Vec2 v2, Vec2 v3, Vec2 v4) {
		quadHelper(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y, v4.x, v4.y);
	}

	public static void drawSkyLines(float x, float y, int lineCount, float radius, float height, float rot) {
		for (int i = 0; i < lineCount; i++) {
			Tmp.v1.set(1f, 1f); //line start
			Tmp.v2.set(Core.camera.position);

			Vec2 tv = vecSetLine(Tmp.v1, x, y, i * (360f / lineCount) + rot, radius);

			Lines.lineAngle(tv.x, tv.y, Tmp.v2.sub(tv.x, tv.y).angle() + 180f, Tmp.v2.dst(0f, 0f) * height);
		}
	}

	public static Vec2 parallax(float x, float y, float height) {
		return parallax(x, y, height, false);
	}

	public static Vec2 parallax(float x, float y, float height, boolean ignoreCamDst) { //todo shadows
		Tmp.v1.set(1f, 1f);
		Tmp.v2.set(Core.camera.position);

		return vecSetLine(Tmp.v1, x, y, Tmp.v2.sub(x, y).angle() + 180f, ignoreCamDst ? height : height * Tmp.v2.dst(0f, 0f));
	}

	public static void drawFace(float x1, float y1, float x2, float y2, TextureRegion tex) {
		Tmp.v1.set(parallax(x1, y1, 100f));
		Tmp.v2.set(parallax(x2, y2, 100f));

		quadHelper(x1, y1, x2, y2, Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y);
	}

	public static void drawOmegaUltraGigaChadDeathRay(float x, float y, float radius, float height, float upScl) {
		Tmp.v1.set(parallax(x, y, height, false));
		Tmp.v2.set(Core.camera.position);

		Fill.poly(x, y, 48, radius);
		//Fill.poly(Tmp.v1.x, Tmp.v1.y, 48, radius * upScl);

		Vec2 tv3 = vecSetLine(Tmp.v3, x, y, Tmp.v2.sub(x, y).angle() - 90f, radius);
		Tmp.v2.set(Core.camera.position);
		Vec2 tv4 = vecSetLine(Tmp.v4, x, y, Tmp.v2.sub(x, y).angle() + 90f, radius);
		Tmp.v2.set(Core.camera.position);
		Vec2 tv5 = vecSetLine(Tmp.v5, Tmp.v1, Tmp.v2.sub(x, y).angle() + 90f, radius * upScl);
		Tmp.v2.set(Core.camera.position);
		Vec2 tv6 = vecSetLine(Tmp.v6, Tmp.v1, Tmp.v2.sub(x, y).angle() - 90f, radius * upScl);

		quadHelper(tv3.x, tv3.y, tv4.x, tv4.y, tv5.x, tv5.y, tv6.x, tv6.y);
	}

	public static void debugDots(Vec2... points) {
		for (int i = 0; i < points.length; i++) {
			Draw.color(HPal.spectrum[i], 0.5f);
			Fill.poly(points[i].x, points[i].y, 12, 2f);
		}
		Draw.color();
	}

	public static void debugDots(float... points) {
		for (int i = 0; i < points.length; i += 2) {
			Draw.color(HPal.spectrum[i / 2], 0.5f);
			Fill.poly(points[i], points[i + 1], 12, 2f);
		}
		Draw.color();
	}

	public static DrawBlock base() {
		return base(0f);
	}

	public static DrawBlock base(float rotatorSpeed) {
		return new DrawMulti(new DrawRegion("-rotator", rotatorSpeed, rotatorSpeed > 0f), new DrawDefault(), new DrawRegion("-top"));
	}

	/** Research costs for anything that isn't a block or unit */
	public static ItemStack[] researchRequirements(ItemStack[] requirements, float mul) {
		ItemStack[] out = new ItemStack[requirements.length];
		for (int i = 0; i < out.length; i++) {
			int quantity = 60 + Mathf.round(Mathf.pow(requirements[i].amount, 1.1f) * 20 * mul, 10);

			out[i] = new ItemStack(requirements[i].item, UI.roundAmount(quantity));
		}

		return out;
	}

	/** Adds ItemStack arrays together. Combines duplicate items into one stack. */
	public static ItemStack[] addItemStacks(Seq<ItemStack[]> stacks) {
		itemStacks.clear();
		items.clear();
		amounts.clear();
		stacks.each(s -> {
			for (ItemStack stack : s) {
				itemStacks.add(stack);
			}
		});
		itemStacks.sort(s -> s.item.id);
		itemStacks.each(s -> {
			if (!items.contains(s.item)) {
				items.add(s.item);
				amounts.add(s.amount);
			} else {
				amounts.incr(items.indexOf(s.item), s.amount);
			}
		});
		ItemStack[] result = new ItemStack[items.size];
		for (int i = 0; i < items.size; i++) {
			result[i] = new ItemStack(items.get(i), amounts.get(i));
		}
		return result;
	}

	public static Position pos(float x, float y) {
		return new Pos(x, y);
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

	//use for cst bullet
	public static Bullet anyOtherCreate(Bullet bullet, BulletType type, Entityc shooter, Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY, @Nullable Teamc target) {
		if (bullet == null || type == null) return null;
		bullet.type = type;
		bullet.owner = owner;
		bullet.shooter = (shooter == null ? owner : shooter);
		bullet.team = team;
		bullet.time = 0f;
		bullet.originX = x;
		bullet.originY = y;
		if (!(aimX == -1f && aimY == -1f)) {
			bullet.aimTile = target instanceof Building b ? b.tile : Vars.world.tileWorld(aimX, aimY);
		}
		bullet.aimX = aimX;
		bullet.aimY = aimY;

		bullet.initVel(angle, type.speed * velocityScl * (type.velocityScaleRandMin != 1f || type.velocityScaleRandMax != 1f ? Mathf.random(type.velocityScaleRandMin, type.velocityScaleRandMax) : 1f));
		bullet.set(x, y);
		bullet.lastX = x;
		bullet.lastY = y;
		bullet.lifetime = type.lifetime * lifetimeScl * (type.lifeScaleRandMin != 1f || type.lifeScaleRandMax != 1f ? Mathf.random(type.lifeScaleRandMin, type.lifeScaleRandMax) : 1f);
		bullet.data = data;
		bullet.hitSize = type.hitSize;
		bullet.mover = mover;
		bullet.damage = (damage < 0 ? type.damage : damage) * bullet.damageMultiplier();
		//reset trail
		if (bullet.trail != null) {
			bullet.trail.clear();
		}
		bullet.add();

		if (type.keepVelocity && owner instanceof Velc vel) bullet.vel.add(vel.vel());
		return bullet;
	}

	public static void liquid(IntMap2<Cons<Liquid>> cons, String name, Color color, float exp, float fla, float htc, float vis, float temp) {
		for (int i = 1; i < 10; i++) {
			int j = i;
			Liquid liquid = new Liquid(name + j, color) {{
				explosiveness = exp * j;
				flammability = fla * j;
				heatCapacity = htc * j;
				viscosity = vis * j;
				temperature = temp / j;
			}};
			if (cons != null && cons.size > 0 && cons.containsKey(i)) {
				cons.get(i).get(liquid);
			}
		}
	}

	public static void liquid(String name, Color color, float exp, float fla, float htc, float vis, float temp) {
		liquid(null, name, color, exp, fla, htc, vis, temp);
	}

	public static void item(IntMap2<Cons<Item>> cons, String name, Color color, float exp, float fla, float cos, float radio, float chg, float health) {
		for (int i = 1; i < 10; i++) {
			int j = i;
			Item item = new Item(name + j, color) {{
				explosiveness = exp * j;
				flammability = fla * j;
				cost = cos * j;
				radioactivity = radio * j;
				charge = chg * j;
				healthScaling = health * j;
			}};
			if (cons != null && cons.size > 0 && cons.containsKey(i)) {
				cons.get(i).get(item);
			}
		}
	}

	/**
	 * Moving bullets
	 *
	 * @param b     Bullets to be moved
	 * @param endX  End coordinate X
	 * @param endY  End coordinate Y
	 * @param speed Speed
	 */
	public static void movePoint(Bullet b, float endX, float endY, float speed) {
		// Calculate the distance between two points
		float distance = (float) Math.sqrt(Math.pow(endX - b.x, 2) + Math.pow(endY - b.y, 2));

		float moveSpeed = distance * speed;

		// Calculate the unit vector for the direction of movement
		float dx = (endX - b.x) / distance;
		float dy = (endY - b.y) / distance;

		// Calculate the distance moved within each tick
		float moveDistance = moveSpeed * Time.delta;

		// Update the position of bullets
		b.x += dx * moveDistance;
		b.y += dy * moveDistance;

		// Check if the destination has been reached or exceeded
		if (Math.abs(b.x - endX) < 0.0001f && Math.abs(b.y - endY) < 0.0001f) {
			b.x = endX;
			b.y = endY;
		}
	}

	/**
	 * Find the y-coordinate based on the known two points and the x-coordinate
	 *
	 * @param x1 The x-coordinate of the first point
	 * @param y1 The y-coordinate of the first point
	 * @param x2 The x-coordinate of the second point
	 * @param y2 The y-coordinate of the second point
	 * @param x  Request the x-coordinate of the point with y-coordinate
	 * @return Corresponding y-coordinate
	 */
	public float lineY(float x1, float y1, float x2, float y2, float x) {
		// Calculate slope
		float slope = (y2 - y1) / (x2 - x1);

		// Calculate intercept
		float intercept = y1 - slope * x1;

		//Calculate the y-coordinate based on the linear equation y = mx + b
		return slope * x + intercept;
	}

	/**
	 * Based on the known angle of a point or line and the x-coordinate of another point, solve for the y-coordinate of that point.
	 *
	 * @param x1    The x-coordinate of a known point
	 * @param y1    The y-coordinate of a known point
	 * @param angle The angle between the line and the positive x-axis direction (in degrees)
	 * @param x2    The x-coordinate of another point
	 * @return The solved y-coordinate
	 */
	public float angleY(float x1, float y1, float angle, float x2) {
		// Handle special cases with angles of 90 degrees or 270 degrees
		if (angle == 90 || angle == 270) {
			// If the angle is 90 degrees or 270 degrees, the line is vertical and the y-coordinate is the same as y1
			return y1;
		}
		// Slope
		float slope = (float) Math.tan(Math.toRadians(angle));
		// Calculate the y-coordinate and return it
		return slope * (x2 - x1) + y1;
	}

	public static void drawTiledFramesBar(float w, float h, float x, float y, Liquid liquid, float alpha) {
		TextureRegion region = Vars.renderer.fluidFrames[liquid.gas ? 1 : 0][liquid.getAnimationFrame()];

		Draw.color(liquid.color, liquid.color.a * alpha);
		Draw.rect(region, x + w / 2f, y + h / 2f, w, h);
		Draw.color();
	}

	public static void extinguish(Team team, float x, float y, float range, float intensity) {
		Vars.indexer.eachBlock(team, x, y, range, Constant.BOOLF_BUILDING_TRUE, b -> Fires.extinguish(b.tile, intensity));
	}

	public static void extinguish(Teamc teamc, float range, float intensity) {
		Vars.indexer.eachBlock(teamc.team(), teamc.x(), teamc.y(), range, Constant.BOOLF_BUILDING_TRUE, b -> Fires.extinguish(b.tile, intensity));
	}

	public static Unit teleportUnitNet(Unit before, float x, float y, float angle, Player player) {
		if (Vars.net.active() || Vars.headless) {
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

	public static Color getColor(Color defaultColor, Team team) {
		return defaultColor == null ? team.color : defaultColor;
	}

	public static void limitRangeWithoutNew(ItemTurret turret, float margin) {
		for (var entry : turret.ammoTypes.entries()) {
			entry.value.lifetime = (turret.range + margin) / entry.value.speed;
		}
	}

	public static float regSize(UnitType type) {
		return type.hitSize / Vars.tilesize / Vars.tilesize / 3.25f;
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

	public static boolean friendly(Liquid liquid) {
		return liquid.effect != StatusEffects.none && liquid.effect.damage <= 0.1f && (liquid.effect.damage < -0.01f || liquid.effect.healthMultiplier > 1.01f || liquid.effect.damageMultiplier > 1.01f);
	}

	public static float biasSlope(float fin, float bias) {
		return fin < bias ? (fin / bias) : 1f - (fin - bias) / (1f - bias);
	}

	public static Vec2 randomPoint(float radius) {
		float r = radius * Mathf.sqrt(Mathf.random());
		return Tmp.v1.setToRandomDirection().setLength(r);
	}

	public static String statUnitName(StatUnit statUnit) {
		return statUnit.icon == null ? statUnit.localized() : statUnit.icon + " " + statUnit.localized();
	}

	public static float bulletDamage(BulletType b, float lifetime) {
		if (b.spawnUnit != null) { // Missile unit damage
			if (b.spawnUnit.weapons.isEmpty()) return 0f;
			Weapon uW = b.spawnUnit.weapons.first();
			return bulletDamage(uW.bullet, uW.bullet.lifetime) * uW.shoot.shots;
		} else {
			float damage = b.damage + b.splashDamage; // Base Damage
			damage += b.lightningDamage * b.lightning * b.lightningLength; // Lightning Damage

			if (b.fragBullet != null) { // Frag Bullet Damage
				damage += bulletDamage(b.fragBullet, b.fragBullet.lifetime) * b.fragBullets;
			}

			if (b.intervalBullet != null) { //Interval Bullet Damage
				int amount = (int) (lifetime / b.bulletInterval * b.intervalBullets);
				damage += bulletDamage(b.intervalBullet, b.intervalBullet.lifetime) * amount;
			}

			if (b instanceof ContinuousBulletType cbt) { //Continuous Damage
				return damage * lifetime / cbt.damageInterval;
			} else {
				return damage;
			}
		}
	}

	public static class Pos implements Position {
		public float x, y;

		public Pos() {}

		public Pos(float dx, float dy) {
			x = dx;
			y = dy;
		}

		public Pos set(float dx, float dy) {
			x = dx;
			y = dy;
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
