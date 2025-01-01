@file:Suppress("unused")

package heavyindustry.util

import arc.func.*
import arc.struct.*
import heavyindustry.util.Utils.*
import mindustry.*
import mindustry.content.TechTree.*
import mindustry.ctype.*
import mindustry.game.Objectives.*
import mindustry.type.*
import mindustry.world.Block
import mindustry.world.blocks.defense.turrets.*
import rhino.*
import rhino.Function

/**
 * Kotlin's utility class.
 *
 * @author Eipusino
 */
class ScriptUtils internal constructor() {
    companion object {
        @JvmField internal val packages: Seq<String> = Seq.with(
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
            "heavyindustry.ui.fragment",
            "heavyindustry.ui.listeners",
            "heavyindustry.ui.tooltips",
            "heavyindustry.util",
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
        )

        @JvmStatic
        fun getClass(name: String): NativeJavaClass {
            return NativeJavaClass(Vars.mods.scripts.scope, Vars.mods.mainLoader().loadClass(name))
        }

        @JvmStatic
        internal fun importDefaults(scope: ImporterTopLevel) {
            for (pack in packages) {
                importPackage(scope, pack)
            }
        }

        @JvmStatic
        fun importPackage(scope: ImporterTopLevel, packageName: String) {
            val p = NativeJavaPackage(packageName, Vars.mods.mainLoader())
            p.parentScope = scope

            scope.importPackage(p)
        }

        @JvmStatic
        fun importPackage(scope: ImporterTopLevel, pack: Package) {
            importPackage(scope, pack.name)
        }

        @JvmStatic
        fun importClass(scope: ImporterTopLevel, canonical: String) {
            importClass(scope, ReflectUtils.findClass(canonical))
        }

        @JvmStatic
        fun importClass(scope: ImporterTopLevel, type: Class<*>?) {
            val nat = NativeJavaClass(scope, type)
            nat.parentScope = scope

            scope.importClass(nat)
        }

        @JvmStatic
        fun compileFunc(scope: Scriptable, sourceName: String, source: String): Function {
            return compileFunc(scope, sourceName, source, 1)
        }

        @JvmStatic
        fun compileFunc(scope: Scriptable, sourceName: String, source: String, lineNum: Int): Function {
            return Vars.mods.getScripts().context.compileFunction(scope, source, sourceName, lineNum)
        }

        /**
         * Adding TechNode without throw exception.
         *
         * @param content mod content
         * @param researchName vanilla content name
         * @param customRequirements
         * @param objectives
         * @return Whether the TechNode has been successfully created.
         */
        @JvmStatic
        fun addToResearch(content: UnlockableContent?, researchName: String?, customRequirements: Array<ItemStack?>?, objectives: Seq<Objective?>?): Boolean {
            if (content == null || researchName == null) return false

            all.find { t -> t.content == content }?.remove()

            val node = TechNode(null, content, eq(customRequirements, content.researchRequirements()))
            if (objectives != null && objectives.any()) {
                node.objectives.addAll(objectives)
            }

            if (node.parent != null) {
                node.parent.children.remove(node)
            }

            // find parent node.
            val parent: TechNode? = all.find { t -> t.content.name.contains(researchName) }

            if (parent == null) return false

            // add this node to the parent
            if (!parent.children.contains(node)) {
                parent.children.add(node)
            }
            // reparent the node
            node.parent = parent

            return true
        }

        @JvmStatic
        fun addToResearch(content: UnlockableContent?, researchName: String?, objectives: Seq<Objective?>?) = addToResearch(content, researchName, ItemStack.empty, objectives)

        @JvmStatic
        fun addToResearch(content: UnlockableContent?, researchName: String?) = addToResearch(content, researchName, ItemStack.empty, Seq.with())

        @JvmStatic
        fun addToResearch(content: UnlockableContent?, researchName: String?, customRequirements: Array<ItemStack?>?) = addToResearch(content, researchName, customRequirements, Seq.with())

        /**
         * Cooling coefficient of turret.
         *
         * If the third parameter is non-zero when called, the coolantMultiplier for the turret can be set.
         */
        @JvmStatic
        fun coolant(block: Block?, v: Float, coolantMultiplier: Float) {
            if (block is BaseTurret) {
                block.coolant = block.consumeCoolant(v)
                if (coolantMultiplier > 0.0001f) block.coolantMultiplier = coolantMultiplier
            }
        }

        @JvmStatic
        fun coolant(block: Block?, v: Float) {
            coolant(block, v, 0f)
        }
    }
}

// Some commonly used declarations are in the top-level Kotlin functions.
// Note: They cannot be called in Java

inline fun <reified T> eqs(a: Any?, b: T): T {
    return if (a is T) a else b
}

/**
 * When used to reference member variables,
 * it saves unnecessary coercion, which is equivalent to Java's `if  (obj instanceof Obj ob) { ... }`
 */
inline fun <reified T> eqc(a: Any?, b: Cons<T>) {
    if (a is T) b.get(a)
}

inline fun <reified T> eon(a: T?, b: Cons<T>) {
    if (a != null) b.get(a)
}