@file:Suppress("unused")

package heavyindustry.util

import arc.func.*
import arc.struct.*
import heavyindustry.core.*
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

fun <T> nop(a: T?, b: Cons<T>) {
    if (a != null) b.get(a)
}

inline fun <reified T> ine(a: Any, b: T): T = if (a is T) a else b

inline fun <reified T> ins(a: Any?, b: Cons<T>) {
    if (a is T) b.get(a)
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

/**
 * I don't know if this is usable, JS is really abstract.
 *
 * @param name Standard class names, such as heavyindustry.util.UtilsKt
 * @return js-xor native java class
 */
fun getClass(name: String): NativeJavaClass {
    return NativeJavaClass(Vars.mods.scripts.scope, URLClassLoader(arrayOf(HeavyIndustryMod.internalTree.file.file().toURI().toURL()), Vars.mods.mainLoader()).loadClass(name))
}

fun <T> comparing(comparable: Comparable<T>): Comparator<T> {
    return compareBy { comparable }
}
