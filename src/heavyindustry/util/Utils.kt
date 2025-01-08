package heavyindustry.util

import arc.func.*
import arc.struct.*
import arc.util.io.*

fun <T> eq(a: Boolean, b: T, c: T): T = if (a) b else c

fun <T> eq(a: T?, b: T): T = a ?: b

inline fun <reified T> inEq(a: Any, b: T): T = if (a is T) a else b

fun <T> noCo(a: T?, b: Cons<T>) {
    if (a != null) b.get(a)
}

inline fun <reified T> inCo(a: Any?, b: Cons<T>) {
    if (a is T) b.get(a)
}

operator fun <K> ObjectIntMap<K>.set(key: K, value: Int) = put(key, value)

operator fun <K> ObjectFloatMap<K>.set(key: K, value: Float) = put(key, value)

operator fun <V> IntMap<V>.set(key: Int, value: V): V = put(key, value)

operator fun <K, V> ObjectMap<K, V>.set(key: K, value: V): V = put(key, value)

operator fun <A> Cons<A>.invoke(p: A) = get(p)

operator fun <A, B> Cons2<A, B>.invoke(p1: A, p2: B) = get(p1, p2)

operator fun <A, B, C> Cons3<A, B, C>.invoke(p1: A, p2: B, p3: C) = get(p1, p2, p3)

operator fun <A, B, C, D> Cons4<A, B, C, D>.invoke(p1: A, p2: B, p3: C, p4: D) = get(p1, p2, p3, p4)

operator fun <A, B> Func<A, B>.invoke(p: A): B = get(p)

operator fun <A, B, R> Func2<A, B, R>.invoke(p1: A, p2: B): R = get(p1, p2)

operator fun <A, B, C, R> Func3<A, B, C, R>.invoke(p1: A, p2: B, p3: C): R = get(p1, p2, p3)

operator fun <A> Prov<A>.invoke(): A = get()

operator fun <A, T: Throwable> ConsT<A, T>.invoke(p: A) = get(p)

fun Writes.b(vararg bytes: Int) = bytes.forEach { b(it) }

fun Writes.s(vararg shorts: Int) = shorts.forEach { s(it) }

fun Writes.i(vararg ints: Int) = ints.forEach { i(it) }

fun Writes.l(vararg longs: Long) = longs.forEach { l(it) }

fun Writes.f(vararg floats: Float) = floats.forEach { f(it) }

fun Writes.d(vararg bytes: Double) = bytes.forEach { d(it) }

fun Writes.str(vararg strings: String) = strings.forEach { str(it) }

fun Writes.bool(vararg booleans: Boolean) = booleans.forEach { bool(it) }

inline fun <K, V> ObjectMap<K, V>.forEach(block: (K, V) -> Unit) {
    for (e in this) {
        block(e.key, e.value)
    }
}