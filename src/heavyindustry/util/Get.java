/*
	Copyright (c) Eipusino 2021
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
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
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pool.Poolable;
import heavyindustry.graphics.HPal;
import heavyindustry.math.IPos;
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
import mindustry.gen.Posc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.gen.Velc;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.consumers.Consume;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;
import mindustry.world.meta.StatUnit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * Input-output utilities, providing very specific functions that aren't really commonly used, but often
 * enough to require me to write a class for it.
 *
 * @author Eipusino
 */
public final class Get {
	public static final Color c1 = new Color(), c2 = new Color(), c3 = new Color(), c4 = new Color(), c5 = new Color();
	public static final Color c6 = new Color(), c7 = new Color(), c8 = new Color(), c9 = new Color(), c10 = new Color();

	public static Rect r = new Rect(), r2 = new Rect();
	public static Vec2 v = new Vec2(), vv = new Vec2();

	public static final Rand rand = new Rand(), rand2 = new Rand();

	public static final Team[] baseTeams = {Team.derelict, Team.sharded, Team.crux, Team.green, Team.malis, Team.blue};

	static final CollectionList<ItemStack> itemStacks = new CollectionList<>(ItemStack.class);
	static final CollectionList<Item> items = new CollectionList<>(Item.class);

	static final IntSeq amounts = new IntSeq();

	static Field consumeBuilderField;

	/** Don't let anyone instantiate this class. */
	private Get() {}

	@Contract(pure = true)
	public static int reverse(int rotation) {
		return switch (rotation) {
			case 0 -> 2;
			case 2 -> 0;
			case 1 -> 3;
			case 3 -> 1;
			default -> -1;
		};
	}

	@Contract(pure = true)
	public static int getByIndex(IntSet set, int index) {
		if (index < 0 || index >= set.size) {
			return -1;
		}

		int value = 0;
		int counter = 0;

		IntSetIterator iter = set.iterator();
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
	@Contract(pure = true)
	public static int relativeDirection(@Nullable Building from, @Nullable Building to) {
		if (from == null || to == null) return -1;
		if (from.x == to.x && from.y > to.y) return (7 - from.rotation) % 4;
		if (from.x == to.x && from.y < to.y) return (5 - from.rotation) % 4;
		if (from.x > to.x && from.y == to.y) return (6 - from.rotation) % 4;
		if (from.x < to.x && from.y == to.y) return (4 - from.rotation) % 4;
		return -1;
	}

	@Contract(value = "_, _, _, _-> param1")
	public static Vec2 vecSetLine(Vec2 vec, Vec2 pos, float rotation, float length) {
		return vec.setLength(length).setAngle(rotation).add(pos);
	}

	@Contract(value = "_, _, _, _, _ -> param1")
	public static Vec2 vecSetLine(Vec2 vec, float x, float y, float rotation, float length) {
		return vec.setLength(length).setAngle(rotation).add(x, y);
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

	@Contract(value = "_ -> new", pure = true)
	public static DrawBlock base(float rotatorSpeed) {
		return new DrawMulti(new DrawRegion("-rotator", rotatorSpeed, rotatorSpeed > 0f), new DrawDefault(), new DrawRegion("-top"));
	}

	/** Research costs for anything that isn't a block or unit */
	@Contract(pure = true)
	public static ItemStack[] researchRequirements(ItemStack[] requirements, float mul) {
		ItemStack[] out = new ItemStack[requirements.length];
		for (int i = 0; i < out.length; i++) {
			int quantity = 60 + Mathf.round(Mathf.pow(requirements[i].amount, 1.1f) * 20 * mul, 10);

			out[i] = new ItemStack(requirements[i].item, UI.roundAmount(quantity));
		}

		return out;
	}

	/** Adds ItemStack arrays together. Combines duplicate items into one stack. */
	@Contract(value = "_ -> new")
	public static ItemStack[] addItemStacks(Seq<ItemStack[]> stacks) {
		itemStacks.clear();
		items.clear();
		amounts.clear();
		stacks.each(itemStacks::addAll);
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

	@Contract(value = "_, _ -> new", pure = true)
	public static Position pos(float x, float y) {
		return new Pos(x, y);
	}

	//use for cst bullet
	@Contract(value = "_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> param1")
	public static Bullet anyOtherCreate(@Nullable Bullet bullet, @Nullable BulletType type, Entityc shooter, Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY, @Nullable Teamc target) {
		if (bullet == null || type == null) return bullet;
		bullet.type = type;
		bullet.owner = owner;
		bullet.shooter = shooter == null ? owner : shooter;
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

	public static void liquid(@Nullable IntMap2<Cons<Liquid>> cons, String name, Color color, float exp, float fla, float htc, float vis, float temp) {
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

	public static void item(@Nullable IntMap2<Cons<Item>> cons, String name, Color color, float exp, float fla, float cos, float radio, float chg, float health) {
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
	 * Moving Positions
	 *
	 * @param pos   Position to be moved
	 * @param endX  End coordinate X
	 * @param endY  End coordinate Y
	 * @param speed Speed
	 */
	public static void movePoint(Posc pos, float endX, float endY, float speed) {
		// Calculate the distance between two points
		float distance = (float) Math.sqrt(Math.pow(endX - pos.x(), 2) + Math.pow(endY - pos.y(), 2));

		float moveSpeed = distance * speed;

		// Calculate the unit vector for the direction of movement
		float dx = (endX - pos.x()) / distance;
		float dy = (endY - pos.y()) / distance;

		// Calculate the distance moved within each tick
		float moveDistance = moveSpeed * Time.delta;

		// Update the position
		pos.x(pos.x() + dx * moveDistance);
		pos.y(pos.y() + dy * moveDistance);

		// Check if the destination has been reached or exceeded
		if (Math.abs(pos.x() - endX) < 0.0001f && Math.abs(pos.y() - endY) < 0.0001f) {
			pos.x(endX);
			pos.y(endY);
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
	@Contract(pure = true)
	public float lineY(float x1, float y1, float x2, float y2, float x) {
		// Calculate slope
		float slope = (y2 - y1) / (x2 - x1);

		// Calculate intercept
		float intercept = y1 - slope * x1;

		//Calculate the y-coordinate based on the linear equation y = mx + b
		return slope * x + intercept;
	}

	public static void drawTiledFramesBar(float w, float h, float x, float y, Liquid liquid, float alpha) {
		TextureRegion region = Vars.renderer.fluidFrames[liquid.gas ? 1 : 0][liquid.getAnimationFrame()];

		Draw.color(liquid.color, liquid.color.a * alpha);
		Draw.rect(region, x + w / 2f, y + h / 2f, w, h);
		Draw.color();
	}

	public static void extinguish(@Nullable Team team, float x, float y, float range, float intensity) {
		Vars.indexer.eachBlock(team, x, y, range, Constant.BOOLF_BUILDING_TRUE, b -> Fires.extinguish(b.tile, intensity));
	}

	public static void extinguish(Teamc teamc, float range, float intensity) {
		Vars.indexer.eachBlock(teamc.team(), teamc.x(), teamc.y(), range, Constant.BOOLF_BUILDING_TRUE, b -> Fires.extinguish(b.tile, intensity));
	}

	public static Unit teleportUnitNet(Unit before, float x, float y, float angle, @Nullable Player player) {
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

	@Contract(value = "!null, _ -> param1", pure = true)
	public static Color getColor(@Nullable Color defaultColor, Team team) {
		return defaultColor == null ? team.color : defaultColor;
	}

	public static void limitRangeWithoutNew(ItemTurret turret, float margin) {
		for (var entry : turret.ammoTypes.entries()) {
			entry.value.lifetime = (turret.range + margin) / entry.value.speed;
		}
	}

	@Contract(pure = true)
	public static float regSize(UnitType type) {
		return type.hitSize / Vars.tilesize / Vars.tilesize / 3.25f;
	}

	@Contract(pure = true)
	public static boolean equals(Block floor, int rx, int ry) {
		return rx < Vars.world.width() - 1 && ry < Vars.world.height() - 1
				&& Vars.world.tile(rx + 1, ry).floor() == floor
				&& Vars.world.tile(rx, ry + 1).floor() == floor
				&& Vars.world.tile(rx, ry).floor() == floor
				&& Vars.world.tile(rx + 1, ry + 1).floor() == floor;
	}

	public static Rand rand(long id) {
		rand.setSeed(id);
		return rand;
	}

	@Contract(pure = true)
	public static boolean friendly(@Nullable Liquid liquid) {
		return liquid != null && liquid.effect != StatusEffects.none && liquid.effect.damage <= 0.1f && (liquid.effect.damage < -0.01f || liquid.effect.healthMultiplier > 1.01f || liquid.effect.damageMultiplier > 1.01f);
	}

	@Contract(pure = true)
	public static float biasSlope(float fin, float bias) {
		return fin < bias ? (fin / bias) : 1f - (fin - bias) / (1f - bias);
	}

	public static Vec2 randomPoint(float radius) {
		float random = radius * Mathf.sqrt(Mathf.random());
		return Tmp.v1.setToRandomDirection().setLength(random);
	}

	@Contract(pure = true)
	public static String statUnitName(StatUnit stat) {
		return stat.icon == null ? stat.localized() : stat.icon + " " + stat.localized();
	}

	@Contract(pure = true)
	public static float bulletDamage(@Nullable BulletType type, float lifetime) {
		if (type == null) return 0f;

		if (type.spawnUnit != null) { // Missile unit damage
			if (type.spawnUnit.weapons.isEmpty()) return 0f;
			Weapon weapon = type.spawnUnit.weapons.first();
			return bulletDamage(weapon.bullet, weapon.bullet.lifetime) * weapon.shoot.shots;
		} else {
			float damage = type.damage + type.splashDamage; // Base Damage
			damage += type.lightningDamage * type.lightning * type.lightningLength; // Lightning Damage

			if (type.fragBullet != null) { // Frag Bullet Damage
				damage += bulletDamage(type.fragBullet, type.fragBullet.lifetime) * type.fragBullets;
			}

			if (type.intervalBullet != null) { //Interval Bullet Damage
				int amount = (int) (lifetime / type.bulletInterval * type.intervalBullets);
				damage += bulletDamage(type.intervalBullet, type.intervalBullet.lifetime) * amount;
			}

			if (type instanceof ContinuousBulletType continuous) { //Continuous Damage
				return damage * lifetime / continuous.damageInterval;
			} else {
				return damage;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Seq<Consume> consumeBuilder(@Nullable Block block) {
		try {
			if (consumeBuilderField == null) {
				consumeBuilderField = Block.class.getDeclaredField("consumeBuilder");
				consumeBuilderField.setAccessible(true);
			}

			return block == null ? new Seq<>() : (Seq<Consume>) consumeBuilderField.get(block);
		} catch (Exception e) {
			Log.err(e);

			return new Seq<>();
		}
	}

	public static class Pos implements IPos, Poolable {
		public float x, y;

		public Pos() {}

		public Pos(float dx, float dy) {
			x = dx;
			y = dy;
		}

		@Contract(value = "_, _ -> this")
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

		@Override
		public void reset() {
			x = 0f;
			y = 0f;
		}
	}
}
