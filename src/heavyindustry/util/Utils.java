package heavyindustry.util;

import arc.Core;
import arc.func.Boolf;
import arc.func.Boolf2;
import arc.func.Cons;
import arc.func.ConsT;
import arc.func.Floatf;
import arc.func.Func;
import arc.func.Func2;
import arc.func.Intf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;
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
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Reflect;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.func.ProvT;
import heavyindustry.func.RunT;
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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Input-output utilities, providing very specific functions that aren't really commonly used, but often
 * enough to require me to write a class for it.
 * <p>This type may have class name conflicts with other dependency libraries, but I am unable to make further
 * changes.
 *
 * @author Eipusino
 */
public final class Utils {
	public static final int softMaxArrayLength = Integer.MAX_VALUE - 8;

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

	static final String[] byteUnit = {"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB", "BB"};

	static final char[] printableChars = {
			' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			':', ';', '<', '=', '>', '?', '@',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			'[', '\\', ']', '^', '_', '`',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'{', '|', '}', '~'
	};

	/** Don't let anyone instantiate this class. */
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

	/**
	 * Gets multiple regions inside a {@link TextureRegion}.
	 *
	 * @param name       sprite name
	 * @param size       split size, pixels per grid
	 * @param layerCount Total number of segmentation layers
	 * @throws NullPointerException       If the {@code name} is {@code null}.
	 * @throws NegativeArraySizeException If {@code size} or {@code layerCount} is negative.
	 * @apiNote The element returned by this method cannot be used in situations where it will be
	 * forcibly converted to {@link AtlasRegion}.
	 */
	public static TextureRegion[][] splitLayers(String name, int size, int layerCount) {
		TextureRegion[][] layers = new TextureRegion[layerCount][];

		for (int i = 0; i < layerCount; i++) {
			layers[i] = split(name, size, i);
		}
		return layers;
	}

	public static TextureRegion[][] splitUnLayers(String name, int size) {
		return splitUnLayers(Core.atlas.find(name), size);
	}

	public static TextureRegion[][] splitUnLayers(TextureRegion region, int size) {
		int x = region.getX();
		int y = region.getY();
		int width = region.width;
		int height = region.height;

		int sw = width / size;
		int sh = height / size;

		int startX = x;
		TextureRegion[][] tiles = new TextureRegion[sw][sh];
		for (int cy = 0; cy < sh; cy++, y += size) {
			x = startX;
			for (int cx = 0; cx < sw; cx++, x += size) {
				tiles[cx][cy] = new TextureRegion(region.texture, x, y, size, size);
			}
		}

		return tiles;
	}

	/**
	 * Gets multiple regions inside a {@link TextureRegion}.
	 *
	 * @param name  sprite name
	 * @param size  split size, pixels per grid
	 * @param layer Number of segmentation layers
	 * @return Split sprites by size and layer parameter ratio.
	 * @throws NullPointerException	   If the {@code name} is {@code null}.
	 * @throws IllegalArgumentException If {@code size} or {@code layer} is negative.
	 * @apiNote The element returned by this method cannot be used in situations where it will be
	 * forcibly converted to {@link AtlasRegion}.
	 */
	public static TextureRegion[] split(String name, int size, int layer) {
		TextureRegion textures = Core.atlas.find(name);
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
	 * @param name   sprite name
	 * @param size   split size, pixels per grid
	 * @param width  The amount of regions horizontally.
	 * @param height The amount of regions vertically.
	 */
	public static TextureRegion[] split(String name, int size, int width, int height) {
		TextureRegion reg = Core.atlas.find(name);
		int textureSize = width * height;
		TextureRegion[] regions = new TextureRegion[textureSize];

		float tileWidth = (reg.u2 - reg.u) / width;
		float tileHeight = (reg.v2 - reg.v) / height;

		for (int i = 0; i < textureSize; i++) {
			float tileX = ((float) (i % width)) / width;
			float tileY = ((float) (i / width)) / height;
			TextureRegion region = new TextureRegion(reg);

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

	public static int[] sort(int[] arr) {
		for (int i = 1; i < arr.length; i++) {
			int tmp = arr[i];

			int j = i;
			while (j > 0 && tmp < arr[j - 1]) {
				arr[j] = arr[j - 1];
				j--;
			}

			if (j != i) {
				arr[j] = tmp;
			}
		}
		return arr;
	}

	public static void shellSort(int[] arr) {
		int temp;
		for (int step = arr.length / 2; step >= 1; step /= 2) {
			for (int i = step; i < arr.length; i++) {
				temp = arr[i];
				int j = i - step;
				while (j >= 0 && arr[j] > temp) {
					arr[j + step] = arr[j];
					j -= step;
				}
				arr[j + step] = temp;
			}
		}
	}

	public static int getByIndex(IntSet intSet, int index) {
		if (index < 0 || index >= intSet.size) {
			throw new IndexOutOfBoundsException();
		}

		int[] value = {0};
		int[] counter = {0};
		intSet.each((item) -> {
			if (counter[0] == index) {
				value[0] = item;
			}
			counter[0]++;
		});

		if (counter[0] > index) {
			return value[0];
		} else {
			throw new IllegalArgumentException();
		}
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

	/** @see String#repeat(int) */
	public static String repeat(String key, int count) {
		if (count <= 0 || key.isEmpty()) return "";
		if (count == 1) return key;

		StringBuilder builder = new StringBuilder(key.length() * count);
		for (int i = 0; i < count; i++) {
			builder.append(key);
		}
		return builder.toString();
	}

	/** Determine whether the string is composed entirely of numbers. */
	public static boolean isNumeric4(String key) {
		return key != null && key.chars().allMatch(Character::isDigit);
	}

	/** Determine whether the string is composed of {@code Number} and {@code . }. */
	public static boolean isNumeric(String key) {
		if (key == null) return false;

		Pattern pattern = Pattern.compile("[0-9]*");
		if (key.indexOf(".") > 0) {//Determine if there is a decimal point
			if (key.indexOf(".") == key.lastIndexOf(".") && key.split("\\.").length == 2) { //Determine if there is only one decimal point
				return pattern.matcher(key.replace(".", "")).matches();
			} else {
				return false;
			}
		} else {
			return pattern.matcher(key).matches();
		}
	}

	public static String generateRandomString(int min, int max) {
		if (min < 0 || max < min || max > 100000) return "";

		int length = min + Mathf.random(max - min + 1);
		return generateRandomString(length);
	}

	public static String generateRandomString(int length) {
		char[] chars = new char[length];
		int range = printableChars.length - 1;

		for (int i = 0; i < length; i++) {
			chars[i] = printableChars[Mathf.random(range)];
		}
		return String.valueOf(chars);
	}

	/**
	 * Convert numbers to computer storage capacity count representation without units.
	 *
	 * @param number The number to be converted
	 * @param retain Reserved decimal places
	 */
	public static String toByteFixNonUnit(double number, int retain) {
		boolean isNegative = false;
		if (number < 0) {
			number = -number;
			isNegative = true;
		}

		double base = 1d;
		for (int i = 0; i < byteUnit.length; i++) {
			if (base * 1024 > number) {
				break;
			}
			base *= 1024;
		}

		String[] arr = Double.toString(number / base).split("\\.");
		int realRetain = Math.min(retain, arr[1].length());

		String end = repeat("0", Math.max(0, retain - realRetain));

		return (isNegative ? "-" : "") + arr[0] + (retain == 0 ? "" : "." + arr[1].substring(0, realRetain) + end);
	}

	/**
	 * Convert numbers to computer storage capacity count representation.
	 *
	 * @param number The number to be converted
	 * @param retain Reserved decimal places
	 */
	public static String toByteFix(double number, int retain) {
		boolean isNegative = false;
		if (number < 0) {
			number = -number;
			isNegative = true;
		}

		int index = 0;
		double base = 1;
		for (int i = 0; i < byteUnit.length; i++) {
			if (base * 1024 > number) {
				break;
			}
			base *= 1024;
			index++;
		}

		String[] arr = Double.toString(number / base).split("\\.");
		int realRetain = Math.min(retain, arr[1].length());

		String end = repeat("0", Math.max(0, retain - realRetain));

		return (isNegative ? "-" : "") + arr[0] + (retain == 0 ? "" : "." + arr[1].substring(0, realRetain) + end + byteUnit[index]);
	}

	public static String toStoreSize(float num) {
		var v = num;
		var n = 0;

		while (v > 1024) {
			v /= 1024;
			n++;
		}

		return Strings.fixed(v, 2) + "[lightgray]" + byteUnit[n];
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

		if (type.keepVelocity && owner instanceof Velc v) bullet.vel.add(v.vel());
		return bullet;
	}

	public static void liquid(ObjectMap<Integer, Cons<Liquid>> cons, String name, Color color, float exp, float fla, float htc, float vis, float temp) {
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

	public static void item(ObjectMap<Integer, Cons<Item>> cons, String name, Color color, float exp, float fla, float cos, float radio, float chg, float health) {
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
		if (b.spawnUnit != null) { //Missile unit damage
			if (b.spawnUnit.weapons.isEmpty()) return 0f;
			Weapon uW = b.spawnUnit.weapons.first();
			return bulletDamage(uW.bullet, uW.bullet.lifetime) * uW.shoot.shots;
		} else {
			float damage = b.damage + b.splashDamage; //Base Damage
			damage += b.lightningDamage * b.lightning * b.lightningLength; //Lightning Damage

			if (b.fragBullet != null) { //Frag Bullet Damage
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

	public static boolean canParseLong(String s, long def) {
		return parseLong(s, def) != def;
	}

	public static long parseLong(String s, long def) {
		return parseLong(s, 10, def);
	}

	public static long parseLong(String s, int radix, long def) {
		return parseLong(s, radix, 0, s.length(), def);
	}

	public static long parseLong(String s, int radix, int start, int end, long def) {
		boolean negative = false;
		int i = start, len = end - start;
		long limit = -9223372036854775807l;
		if (len <= 0) {
			return def;
		} else {
			char firstChar = s.charAt(i);
			if (firstChar < '0') {
				if (firstChar == '-') {
					negative = true;
					limit = -9223372036854775808l;
				} else if (firstChar != '+') {
					return def;
				}

				if (len == 1) return def;

				++i;
			}

			long result;
			int digit;
			for (result = 0l; i < end; result -= digit) {
				digit = Character.digit(s.charAt(i++), radix);
				if (digit < 0) {
					return def;
				}

				result *= radix;
				if (result < limit + (long) digit) {
					return def;
				}
			}

			return negative ? result : -result;
		}
	}

	public static boolean equals(Object a, Object b) {
		return a == b || a != null && a.equals(b);
	}

	public static int hashCode(Object obj) {
		return obj == null ? 0 : obj.hashCode();
	}

	public static int hash(Object... values) {
		if (values == null)
			return 0;

		int result = 1;

		for (Object element : values)
			result = 31 * result + (element == null ? 0 : element.hashCode());

		return result;
	}

	public static <T> T requireInstance(Class<?> type, T obj) {
		if (obj != null && !type.isInstance(obj))
			throw new ClassCastException();
		return obj;
	}

	public static <T> T requireNonNullInstance(Class<?> type, T obj) {
		if (!type.isInstance(obj))
			throw new ClassCastException();
		return obj;
	}

	public static <T> T requireNonNull(T obj) {
		if (obj == null)
			throw new NullPointerException();
		return obj;
	}

	/**
	 * Convert vararg to an array.
	 * Returns an array containing the specified elements.
	 */
	@SafeVarargs
	public static <T> T[] arrayOf(T... elements) {
		return elements;
	}

	public static boolean[] boolOf(boolean... bools) {
		return bools;
	}

	public static byte[] byteOf(byte... bytes) {
		return bytes;
	}

	public static short[] shortOf(short... shorts) {
		return shorts;
	}

	public static int[] intOf(int... ints) {
		return ints;
	}

	public static long[] longOf(long... longs) {
		return longs;
	}

	public static float[] floatOf(float... floats) {
		return floats;
	}

	public static double[] doubleOf(double... doubles) {
		return doubles;
	}

	/**
	 * Returns a comparator that compares {@link Map.Entry} in natural order on key.
	 *
	 * <p>The returned comparator is serializable and throws {@link
	 * NullPointerException} when comparing an entry with a null key.
	 *
	 * @param  <K> the {@link Comparable} type of then map keys
	 * @param  <V> the type of the map values
	 * @return a comparator that compares {@link Map.Entry} in natural order on key.
	 * @see Comparable
	 * @since 1.0.7
	 */
	public static <K extends Comparable<? super K>, V> Comparator<Map.Entry<K, V>> comparingByKey() {
		return (c1, c2) -> c1.getKey().compareTo(c2.getKey());
	}

	/**
	 * Returns a comparator that compares {@link Map.Entry} in natural order on value.
	 *
	 * <p>The returned comparator is serializable and throws {@link
	 * NullPointerException} when comparing an entry with null values.
	 *
	 * @param <K> the type of the map keys
	 * @param <V> the {@link Comparable} type of the map values
	 * @return a comparator that compares {@link Map.Entry} in natural order on value.
	 * @see Comparable
	 * @since 1.0.7
	 */
	public static <K, V extends Comparable<? super V>> Comparator<Map.Entry<K, V>> comparingByValue() {
		return (c1, c2) -> c1.getValue().compareTo(c2.getValue());
	}

	/**
	 * Returns a comparator that compares {@link Map.Entry} by key using the given
	 * {@link Comparator}.
	 *
	 * <p>The returned comparator is serializable if the specified comparator
	 * is also serializable.
	 *
	 * @param  <K> the type of the map keys
	 * @param  <V> the type of the map values
	 * @param  cmp the key {@link Comparator}
	 * @return a comparator that compares {@link Map.Entry} by the key.
	 * @since 1.0.7
	 */
	public static <K, V> Comparator<Map.Entry<K, V>> comparingByKey(Comparator<? super K> cmp) {
		return (c1, c2) -> cmp.compare(c1.getKey(), c2.getKey());
	}

	/**
	 * Returns a comparator that compares {@link Map.Entry} by value using the given
	 * {@link Comparator}.
	 *
	 * <p>The returned comparator is serializable if the specified comparator
	 * is also serializable.
	 *
	 * @param  <K> the type of the map keys
	 * @param  <V> the type of the map values
	 * @param  cmp the value {@link Comparator}
	 * @return a comparator that compares {@link Map.Entry} by the value.
	 * @since 1.0.7
	 */
	public static <K, V> Comparator<Map.Entry<K, V>> comparingByValue(Comparator<? super V> cmp) {
		return (c1, c2) -> cmp.compare(c1.getValue(), c2.getValue());
	}

	/**
	 * Returns a copy of the given {@code Map.MapEntry}. The returned instance is not
	 * associated with any map. The returned instance has the same characteristics
	 * as instances returned by the {@link Map#entry Map::entry} method.
	 *
	 * @apiNote
	 * An instance obtained from a map's entry-set view has a connection to that map.
	 * The {@code copyOf}  method may be used to create a {@code Map.MapEntry} instance,
	 * containing the same key and value, that is independent of any map.
	 *
	 * @implNote
	 * If the given entry was obtained from a call to {@code copyOf} or {@code Map::entry},
	 * calling {@code copyOf} will generally not create another copy.
	 *
	 * @param <K> the type of the key
	 * @param <V> the type of the value
	 * @param e the entry to be copied
	 * @return a map entry equal to the given entry
	 * @throws NullPointerException if e is null
	 * @since 1.0.7
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map.Entry<K, V> copyOf(Map.Entry<? extends K, ? extends V> e) {
		if (e instanceof Pair<? extends K, ? extends V> p) {
			return (Map.Entry<K, V>) p;
		} else {
			return new Pair<>(e.getKey(), e.getValue());
		}
	}

	/**
	 * Returns the index of the first occurrence of the specified element in this list, or -1 if this list does not
	 * contain the element. More formally, returns the lowest index {@code i} such that {@code Objects.equals(o,
	 * get(i))}, or -1 if there is no such index.
	 *
	 * @return the index of the first occurrence of the specified element in this list, or -1 if this list does
	 * not contain the element
	 */
	public static <T> int indexOf(T[] array, T element) {
		for (int i = 0; i < array.length; i++) {
			if (equals(array[i], element)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param src starting position
	 * @param end final position
	 * @since 1.0.8
	 */
	public static <T> int indexOf(T[] array, int src, int end, T element) {
		for (int i = Math.max(0, src); i < Math.min(array.length, end); i++) {
			if (equals(array[i], element)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of the specified element in this list, or -1 if this list does not
	 * contain the element. More formally, returns the highest index {@code i} such that {@code Objects.equals(o,
	 * get(i))}, or -1 if there is no such index.
	 *
	 * @return the index of the last occurrence of the specified element in this list, or -1 if this list does
	 * not contain the element
	 * @since 1.0.8
	 */
	public static <T> int lastIndexOf(T[] array, T element) {
		for (int i = array.length - 1; i >= 0; i--) {
			if (equals(array[i], element)) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(boolean[] array, boolean element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(byte[] array, byte element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(short[] array, short element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(int[] array, int element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(long[] array, long element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(float[] array, float element) {
		for (int i = 0; i < array.length; i++) {
			if (Float.compare(array[i], element) == 0) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(double[] array, double element) {
		for (int i = 0; i < array.length; i++) {
			if (Double.compare(array[i], element) == 0) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(float[] array, float element, float epsilon) {
		for (int i = 0; i < array.length; i++) {
			if (Math.abs(array[i] - element) <= epsilon) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(double[] array, double element, double epsilon) {
		for (int i = 0; i < array.length; i++) {
			if (Math.abs(array[i] - element) <= epsilon) {
				return i;
			}
		}
		return -1;
	}

	@Nullable
	public static <T> T getNull() {
		return null;
	}

	public static <T> T apply(T obj, Cons<T> cons) {
		cons.get(obj);
		return obj;
	}

	public static void run(RunT<Throwable> cons) {
		try {
			cons.run();
		} catch (Throwable e) {
			Log.err(e);
		}
	}

	public static <T> void get(ConsT<T, Throwable> cons, T obj) {
		try {
			cons.get(obj);
		} catch (Throwable e) {
			Log.err(e);
		}
	}

	public static <T> T get(ProvT<T, Throwable> prov, T def) {
		try {
			return prov.get();
		} catch (Throwable e) {
			Log.err(e);

			return def;
		}
	}

	public static <T> T get(ProvT<T, Throwable> prov, ConsT<T, Throwable> cons, T def) {
		try {
			T t = prov.get();
			cons.get(t);
			return t;
		} catch (Throwable e) {
			Log.err(e);

			return def;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj) {
		return (T) obj;
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj, Class<T> type, T def) {
		if (obj != null && !type.isInstance(obj))
			return def;
		return (T) obj;
	}

	/**
	 * Deceiving the compiler does not require throwing checked exceptions when throws or try cache are
	 * included.
	 *
	 * @see IOUtils#ioUnchecked(IOUtils.IORunnable)
	 */
	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> T thrower(Throwable err) throws E {
		throw (E) err;
	}

	/**
	 * Computes a new array length given an array's current length, a minimum growth
	 * amount, and a preferred growth amount. The computation is done in an overflow-safe
	 * fashion.
	 * <p>This method is used by objects that contain an array that might need to be grown
	 * in order to fulfill some immediate need (the minimum growth amount) but would also
	 * like to request more space (the preferred growth amount) in order to accommodate
	 * potential future needs. The returned length is usually clamped at the soft maximum
	 * length in order to avoid hitting the JVM implementation limit. However, the soft
	 * maximum will be exceeded if the minimum growth amount requires it.
	 * <p>If the preferred growth amount is less than the minimum growth amount, the
	 * minimum growth amount is used as the preferred growth amount.
	 * <p>The preferred length is determined by adding the preferred growth amount to the
	 * current length. If the preferred length does not exceed the soft maximum length
	 * (SOFT_MAX_ARRAY_LENGTH) then the preferred length is returned.
	 * <p>If the preferred length exceeds the soft maximum, we use the minimum growth
	 * amount. The minimum required length is determined by adding the minimum growth
	 * amount to the current length. If the minimum required length exceeds Integer.MAX_VALUE,
	 * then this method throws OutOfMemoryError. Otherwise, this method returns the greater of
	 * the soft maximum or the minimum required length.
	 * <p>Note that this method does not do any array allocation itself; it only does array
	 * length growth computations. However, it will throw OutOfMemoryError as noted above.
	 * <p>Note also that this method cannot detect the JVM's implementation limit, and it
	 * may compute and return a length value up to and including Integer.MAX_VALUE that
	 * might exceed the JVM's implementation limit. In that case, the caller will likely
	 * attempt an array allocation with that length and encounter an OutOfMemoryError.
	 * Of course, regardless of the length value returned from this method, the caller
	 * may encounter OutOfMemoryError if there is insufficient heap to fulfill the request.
	 *
	 * @param oldLength   current length of the array (must be nonnegative)
	 * @param minGrowth   minimum required growth amount (must be positive)
	 * @param prefGrowth  preferred growth amount
	 * @return the new array length
	 * @throws OutOfMemoryError if the new length would exceed Integer.MAX_VALUE
	 */
	public static int newLength(int oldLength, int minGrowth, int prefGrowth) {
		// preconditions not checked because of inlining
		// assert oldLength >= 0
		// assert minGrowth > 0

		int prefLength = oldLength + Math.max(minGrowth, prefGrowth); // might overflow
		if (0 < prefLength && prefLength <= softMaxArrayLength) {
			return prefLength;
		}
		// put code cold in a separate method
		return hugeLength(oldLength, minGrowth);
	}

	private static int hugeLength(int oldLength, int minGrowth) {
		int minLength = oldLength + minGrowth;
		if (minLength < 0) { // overflow
			throw new OutOfMemoryError("Required array length " + oldLength + " + " + minGrowth + " is too large");
		}
		return Math.max(minLength, softMaxArrayLength);
	}

	public static <T> T[] copyArray(T[] array, Func<T, T> copy) {
		T[] out = array.clone();
		for (int i = 0, len = out.length; i < len; i++) out[i] = copy.get(out[i]);
		return out;
	}

	/**
	 * Insert an element at the first position of the array. Low performance.
	 *
	 * @param originalArray the source array.
	 * @param element       Inserted elements.
	 * @return Array after inserting elements.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] insertAtFirst(T[] originalArray, T element) {
		T[] newArray = (T[]) Array.newInstance(originalArray.getClass().componentType(), originalArray.length + 1);

		newArray[0] = element;

		System.arraycopy(originalArray, 0, newArray, 1, originalArray.length);

		return newArray;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] removeFirst(T[] originalArray) {
		if (originalArray.length <= 1) {
			return (T[]) Array.newInstance(originalArray.getClass().componentType(), 0);
		}

		T[] newArray = (T[]) Array.newInstance(originalArray.getClass().componentType(), originalArray.length - 1);

		System.arraycopy(originalArray, 1, newArray, 0, originalArray.length - 1);

		return newArray;
	}

	public static <T> boolean any(T[] array, Boolf<T> pred) {
		for (T e : array) if (pred.get(e)) return true;
		return false;
	}

	public static <T> boolean all(T[] array, Boolf<T> pred) {
		for (T e : array) if (!pred.get(e)) return false;
		return true;
	}

	public static <T> void each(T[] array, Cons<? super T> cons) {
		each(array, 0, array.length, cons);
	}

	public static <T> void each(T[] array, int offset, int length, Cons<? super T> cons) {
		for (int i = offset, len = i + length; i < len; i++) cons.get(array[i]);
	}

	public static <T> Single<T> iter(T item) {
		return new Single<>(item);
	}

	@SafeVarargs
	public static <T> Iter<T> iter(T... array) {
		return iter(array, 0, array.length);
	}

	public static <T> Iter<T> iter(T[] array, int offset, int length) {
		return new Iter<>(array, offset, length);
	}

	public static <T> Chain<T> chain(Iterator<T> first, Iterator<T> second) {
		return new Chain<>(first, second);
	}

	public static <T, R> R reduce(T[] array, R initial, Func2<T, R, R> reduce) {
		for (T item : array) initial = reduce.get(item, initial);
		return initial;
	}

	public static <T> int reducei(T[] array, int initial, ReduceInt<T> reduce) {
		for (T item : array) initial = reduce.get(item, initial);
		return initial;
	}

	public static <T> int sumi(T[] array, Intf<T> extract) {
		return reducei(array, 0, (item, accum) -> accum + extract.get(item));
	}

	public static <T> float reducef(T[] array, float initial, ReduceFloat<T> reduce) {
		for (T item : array) initial = reduce.get(item, initial);
		return initial;
	}

	public static <T> float average(T[] array, Floatf<T> extract) {
		return reducef(array, 0f, (item, accum) -> accum + extract.get(item)) / array.length;
	}

	public static <T> T[] resize(T[] array, int newSize, T fill) {
		return resize(array, size -> Reflect.newArray(array, newSize), newSize, fill);
	}

	public static <T> T[] resize(T[] array, ArrayCreator<T> create, int newSize, T fill) {
		if (array.length == newSize) return array;

		T[] out = create.get(newSize);
		System.arraycopy(array, 0, out, 0, Math.min(array.length, newSize));

		if (fill != null && newSize > array.length) Arrays.fill(out, array.length, newSize, fill);
		return out;
	}

	public static <T> boolean arrayEq(T[] first, T[] second, Boolf2<T, T> eq) {
		if (first.length != second.length) return false;
		for (int i = 0; i < first.length; i++) {
			if (!eq.get(first[i], second[i])) return false;
		}
		return true;
	}

	public interface ReduceInt<T> {
		int get(T item, int accum);
	}

	public interface ReduceFloat<T> {
		float get(T item, float accum);
	}

	public interface ArrayCreator<T> {
		T[] get(int size);
	}

	public static class Single<T> implements Iterable<T>, Iterator<T>, Eachable<T> {
		protected final T item;
		protected boolean done;

		public Single(T t) {
			item = t;
		}

		@Override
		public Single<T> iterator() {
			return this;
		}

		@Override
		public boolean hasNext() {
			return !done;
		}

		@Override
		public T next() {
			if (done) return null;
			done = true;
			return item;
		}

		@Override
		public void each(Cons<? super T> cons) {
			if (!done) cons.get(item);
		}
	}

	public static class Iter<T> implements Iterable<T>, Iterator<T>, Eachable<T> {
		private final T[] array;
		private final int offset, length;
		private int index = 0;

		public Iter(T[] arr, int off, int len) {
			array = arr;
			offset = off;
			length = len;
		}

		public int length() {
			return length;
		}

		public void reset() {
			index = 0;
		}

		@Override
		public Iter<T> iterator() {
			return this;
		}

		@Override
		public boolean hasNext() {
			return index < length - offset;
		}

		@Override
		public T next() {
			return hasNext() ? array[offset + index++] : null;
		}

		@Override
		public void each(Cons<? super T> cons) {
			while (hasNext()) cons.get(array[offset + index++]);
		}
	}

	public static class Chain<T> implements Iterable<T>, Iterator<T>, Eachable<T> {
		private final Iterator<T> first, second;

		public Chain(Iterator<T> fir, Iterator<T> sec) {
			first = fir;
			second = sec;
		}

		@Override
		public Chain<T> iterator() {
			return this;
		}

		@Override
		public boolean hasNext() {
			return first.hasNext() || second.hasNext();
		}

		@Override
		public T next() {
			return first.hasNext() ? first.next() : second.next();
		}

		@Override
		public void each(Cons<? super T> cons) {
			while (first.hasNext()) cons.get(first.next());
			while (second.hasNext()) cons.get(second.next());
		}
	}

	public static class ExtPos implements Position {
		public float x, y;

		public ExtPos set(float dx, float dy) {
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
