@file:Suppress("unused")

package heavyindustry.util

import arc.func.*
import arc.struct.*
import heavyindustry.*
import mindustry.*
import mindustry.content.TechTree.*
import mindustry.ctype.*
import mindustry.game.Objectives.*
import mindustry.type.*
import rhino.*
import java.net.*

//class name: UtilsKt
//Java/JS calling method: UtilsKt.addToResearch(children, content...);
//Warning: Methods with <reified T> cannot be called outside of Kotlin.

fun <T> eq(a: Boolean, b: T, c: T): T = if (a) b else c

fun <T> noc(a: T?, b: T): T = a ?: b

fun <T> nop(a: T?, b: Cons<T>) {
    if (a != null) b.get(a)
}

fun <T, N> nop2(a: T?, b: N?, c: Cons2<T, N>) {
    if (a != null && b != null) c.get(a, b)
}

fun <T, N, R> nop3(a: T?, b: N?, c: R?, d: Cons3<T, N, R>) {
    if (a != null && b != null && c != null) d.get(a, b, c)
}

fun <T, N, R, P> nop4(a: T?, b: N?, c: R?, d: P?, e: Cons4<T, N, R, P>) {
    if (a != null && b != null && c != null && d != null) e.get(a, b, c, d)
}

inline fun <reified T> inq(a: Any, b: T): T = if (a is T) a else b

inline fun <reified T> ins(a: Any?, b: Cons<T>) {
    if (a is T) b.get(a)
}

inline fun <reified T, reified N> ins2(a: Any?, b: Any?, c: Cons2<T, N>) {
    if (a is T && b is N) c.get(a, b)
}

fun <T> apt(create: T, cons: Cons<T>): T {
    cons.get(create)
    return create
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
fun research(children: UnlockableContent?, content: UnlockableContent?, requirements: Array<ItemStack>?, objectives: Seq<Objective>?) {
    if (children == null || content == null) return

    val lastNode = all.find { t -> t.content === children }
    lastNode?.remove()

    val node = TechNode(null, children, requirements ?: children.researchRequirements())
    if (objectives != null && objectives.any()) {
        node.objectives.addAll(objectives)
    }

    if (node.parent != null) {
        node.parent.children.remove(node)
    }

    // find parent node.
    val parent = all.find { t -> t.content === content }

    if (parent == null) return

    // add this node to the parent
    if (!parent.children.contains(node)) {
        parent.children.add(node)
    }
    // reparent the node
    node.parent = parent
}

fun research(children: UnlockableContent?, content: UnlockableContent?, objectives: Seq<Objective>?) = research(children, content, ItemStack.empty, objectives)

fun research(children: UnlockableContent?, content: UnlockableContent?, requirements: Array<ItemStack>?) = research(children, content, requirements, Seq.with())

fun research(children: UnlockableContent?, content: UnlockableContent?) = research(children, content, ItemStack.empty, Seq.with())

/**
 * I don't know if this is usable, JS is really abstract.
 *
 * @param name Standard class names, such as heavyindustry.util.UtilsKt
 * @return js-xor native java class
 */
fun getClass(name: String): NativeJavaClass {
    return NativeJavaClass(Vars.mods.scripts.scope, URLClassLoader(arrayOf(HIVars.internalTree.file.file().toURI().toURL()), Vars.mods.mainLoader()).loadClass(name))
}

/** Replace `Comparator.comparing(keyExtractor)` to curb the issue of [NoClassDefFoundError] on the Android platform. */
fun <T> comparing(comparable: Comparable<T>): Comparator<T> {
    return compareBy { comparable }
}

fun random(to: Float): Float {
    return random(0f, to)
}

fun random(from: Float, to: Float): Float {
    return (Math.random() * (to - from)).toFloat() + from
}

fun <T> randomFromArray(array: Array<T>): T {
    return array[random(array.size.toFloat()).toInt()]
}
