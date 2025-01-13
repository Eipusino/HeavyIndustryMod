@file:Suppress("unused")

package heavyindustry.util

import arc.func.*
import arc.struct.*
import arc.util.io.*
import mindustry.content.TechTree.*
import mindustry.ctype.*
import mindustry.game.Objectives.*
import mindustry.type.*

//class name: UtilsKt
//Java/JS calling method: UtilsKt.addToResearch(children, content...);
//Warning: Methods with <reified T> cannot be called outside of Kotlin.

fun <T> eq(a: Boolean, b: T, c: T): T = if (a) b else c

fun <T> noop(a: T?, b: Cons<T>) {
    if (a != null) b.get(a)
}

inline fun <reified T> inst(a: Any, b: T): T = if (a is T) a else b

inline fun <reified T> inst(a: Any?, b: Cons<T>) {
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

/**
 * Adding TechNode without throw exception.
 *
 * Abandoning the search for parent nodes based on name and instead directly searching based on content objects.
 *
 * @param children mod content
 * @param content vanilla content name
 * @param requirements research resources
 * @param objectives research needs
 */
fun addToResearch(children: UnlockableContent?, content: UnlockableContent?, requirements: Array<ItemStack?>?, objectives: Seq<Objective?>?) {
    if (children == null || content == null) return

    val lastNode = all.find { t: TechNode -> t.content === children }
    lastNode?.remove()

    val node = TechNode(null, children, requirements ?: children.researchRequirements())
    if (objectives != null && objectives.any()) {
        node.objectives.addAll(objectives)
    }

    if (node.parent != null) {
        node.parent.children.remove(node)
    }

    // find parent node.
    val parent = all.find { t: TechNode -> t.content === content }

    if (parent == null) return

    // add this node to the parent
    if (!parent.children.contains(node)) {
        parent.children.add(node)
    }
    // reparent the node
    node.parent = parent
}

fun addToResearch(children: UnlockableContent?, content: UnlockableContent?, objectives: Seq<Objective?>?) = addToResearch(children, content, ItemStack.empty, objectives)

fun addToResearch(children: UnlockableContent?, content: UnlockableContent?, requirements: Array<ItemStack?>?) = addToResearch(children, content, requirements, Seq.with())

fun addToResearch(children: UnlockableContent?, content: UnlockableContent?) = addToResearch(children, content, ItemStack.empty, Seq.with())
