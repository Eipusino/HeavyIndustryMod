@file:JvmName("KUtils")

package heavyindustry.util

import arc.Events
import arc.func.Cons
import arc.func.Cons2
import arc.func.Cons3
import arc.func.Cons4
import arc.func.ConsT
import arc.func.Floatf
import arc.func.Func
import arc.func.Func2
import arc.func.Func3
import arc.func.Prov
import arc.graphics.g2d.TextureAtlas.AtlasRegion
import arc.graphics.g2d.TextureRegion
import arc.math.Interp
import arc.math.Mathf
import arc.math.geom.Point2
import arc.math.geom.Vec2
import arc.scene.style.TextureRegionDrawable
import arc.struct.IntMap
import arc.struct.IntSet
import arc.struct.ObjectFloatMap
import arc.struct.ObjectIntMap
import arc.struct.ObjectMap
import arc.struct.Seq
import arc.util.Log
import arc.util.Log.LogLevel
import arc.util.Time
import mindustry.Vars
import mindustry.content.Fx
import mindustry.entities.bullet.BulletType
import mindustry.game.EventType
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.Liquid
import mindustry.type.LiquidStack
import mindustry.type.StatusEffect
import mindustry.world.Tiles
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import mindustry.world.meta.StatValues
import mindustry.world.meta.Stats
import kotlin.math.abs
import kotlin.reflect.KClass

// Placing these functions at the top level may lead to naming pollution,
// but this eliminates the need for @JvmStatic annotation.

fun <T> eqs(a: Boolean, b: T, c: T): T = if (a) b else c

inline fun <reified T> eqs(a: Any?, b: T): T = if (a is T) a else b

inline fun <reified T> inst(a: Any?, b: Cons<T>) {
	if (a is T) b.get(a)
}

fun repeat(key: String, count: Int): String = when (val size = abs(count)) {
	0 -> ""
	1 -> key
	else -> when (key.length) {
		0 -> ""
		1 -> key[0].let { char -> String(CharArray(size) { char }) }
		else -> {
			val builder = StringBuilder(size * key.length)
			for (i in 1..size) {
				builder.append(key)
			}
			builder.toString()
		}
	}
}

fun <T> Seq<T>.addProv(prov: Prov<T>): Seq<T> = add(prov.get())
fun <T> Cons<T>.acc(t: T): T {
	get(t)
	return t
}

fun <K, V> HashMap<K, V>.addProv(key: K, prov: Prov<V>) {
	put(key, prov.get())
}

fun <E> print(e: () -> E) = Log.info(e())

fun print(vararg args: Any?) {
	print(LogLevel.info, " ", *args)
}

fun print(level: LogLevel, vararg args: Any?) {
	print(level, " ", *args)
}

fun print(level: LogLevel, separator: String, vararg args: Any?) {
	val builder = StringBuilder()

	for (i in args.indices) {
		builder.append(args[i])
		if (i < args.size - 1) builder.append(separator)
	}

	Log.log(level, "&lm&fb[heavy-industry]&fr @", builder.toString())
}

fun TextureRegion.selfDrawable() = TextureRegionDrawable(this)

fun TextureRegion.selfAtlas() = eqs(this, AtlasRegion(this))

fun <K, V> checkKey(map: ObjectMap<K, V>, key: K, def: Prov<V>): V {
	if (!map.containsKey(key)) map.put(key, def.get())
	return map[key]
}

fun item(items: Array<ItemStack>, item: Item): Int {
	for (stack in items) {
		if (stack.item === item) return stack.amount
	}
	return 0
}

fun liquid(liquids: Array<LiquidStack>, liquid: Liquid): Float {
	for (stack in liquids) {
		if (stack.liquid === liquid) return stack.amount
	}
	return 0f
}

fun inZone(start: Vec2, size: Vec2, point: Vec2): Boolean {
	return inZone(start.x, start.y, start.x + size.x, start.y + size.y, point.x, point.y)
}

fun <T : Comparable<T>> inZone(x: T, y: T, x1: T, y1: T, px: T, py: T): Boolean {
	return x < px && y < py && x1 > px && y1 > py
}

infix fun KClass<*>.eq(other: KClass<*>): Boolean {
	if (javaObjectType.name.contains("$") ||
		other.javaObjectType.name.contains("$")
	)
		return javaObjectType.isNestmateOf(other.javaObjectType) ||
				other.javaObjectType.isAssignableFrom(javaObjectType)
	return javaObjectType.name.split("$")[0] == other.javaObjectType.name.split("$")[0]
}

infix fun Any.classEq(other: Any) = this::class eq other::class
infix fun Any.classEq(other: KClass<*>) = this::class eq other

operator fun Interp.invoke(a: Float) = apply(a)

operator fun <K> ObjectIntMap<K>.set(key: K, value: Int) = put(key, value)
operator fun <K> ObjectFloatMap<K>.set(key: K, value: Float) = put(key, value)
operator fun <V> IntMap<V>.set(key: Int, value: V): V = put(key, value)
operator fun <K, V> ObjectMap<K, V>.set(key: K, value: V): V = put(key, value)

operator fun IntSet.IntSetIterator.hasNext() = hasNext

operator fun <P> Cons<P>.invoke(p: P) = get(p)
operator fun <P1, P2> Cons2<P1, P2>.invoke(p1: P1, p2: P2) = get(p1, p2)
operator fun <P1, P2, P3> Cons3<P1, P2, P3>.invoke(p1: P1, p2: P2, p3: P3) = get(p1, p2, p3)
operator fun <P1, P2, P3, P4> Cons4<P1, P2, P3, P4>.invoke(p1: P1, p2: P2, p3: P3, p4: P4) = get(p1, p2, p3, p4)
operator fun <P, R> Func<P, R>.invoke(p: P): R = get(p)
operator fun <P1, P2, R> Func2<P1, P2, R>.invoke(p1: P1, p2: P2): R = get(p1, p2)
operator fun <P1, P2, P3, R> Func3<P1, P2, P3, R>.invoke(p1: P1, p2: P2, p3: P3): R = get(p1, p2, p3)
operator fun <R> Prov<R>.invoke(): R = get()
operator fun <P, T : Throwable> ConsT<P, T>.invoke(p: P) = get(p)
operator fun <T> Floatf<T>.invoke(t: T) = get(t)

operator fun Point2.component1(): Int = x
operator fun Point2.component2(): Int = y

operator fun <K, V> ObjectMap.Entry<K, V>.component1(): K = key
operator fun <K, V> ObjectMap.Entry<K, V>.component2(): V = value
operator fun <V> IntMap.Entry<V>.component1(): Int = key
operator fun <V> IntMap.Entry<V>.component2(): V = value

infix fun Float.modf(o: Float) = Mathf.mod(this, o)
infix fun Int.modf(o: Int) = Mathf.mod(this, o)

operator fun Tiles.get(idx: Int) = getp(idx)

fun Float.notZero() = !Mathf.zero(this)

fun Stats.replace(stat: Stat, value: Float, unit: StatUnit) {
	replace(stat, StatValues.number(value, unit))
}

fun BulletType.clearEffects() {
	despawnEffect = Fx.none
	hitEffect = Fx.none
	trailEffect = Fx.none
	shootEffect = Fx.none
	smokeEffect = Fx.none
	chargeEffect = Fx.none
	healEffect = Fx.none
}

fun StatusEffect.clearImmunities() {
	Events.on(EventType.ClientLoadEvent::class.java) {
		Time.run(10f) {
			val unitTypes = Vars.content.units()
			for (unitType in unitTypes) {
				unitType.immunities.remove(this@clearImmunities)
			}
		}
	}
}
