package heavyindustry.mod

import mindustry.*
import org.intellij.lang.annotations.*

fun init() {
    //declare heavy-industry java script APIs
    Vars.mods.scripts.context.evaluateString(
        Vars.mods.scripts.scope,
        INIT_JS,
        "h_global.js",
        0
    )
}

@Language("Nashorn JS")
const val INIT_JS = """
    function getModPackage(name) {
	    var p = Packages.rhino.NativeJavaPackage(name, Vars.mods.mainLoader());
	    Packages.rhino.ScriptRuntime.setObjectProtoAndParent(p, Vars.mods.scripts.scope)
	    return p
    }

    var heavyindustry = getModPackage("heavyindustry")

    importPackage(heavyindustry)
    importPackage(heavyindustry.ai)
    importPackage(heavyindustry.content)
    importPackage(heavyindustry.entities)
    importPackage(heavyindustry.entities.abilities)
    importPackage(heavyindustry.entities.bullet)
    importPackage(heavyindustry.entities.effect)
    importPackage(heavyindustry.entities.part)
    importPackage(heavyindustry.entities.pattern)
    importPackage(heavyindustry.files)
    importPackage(heavyindustry.func)
    importPackage(heavyindustry.game)
    importPackage(heavyindustry.gen)
    importPackage(heavyindustry.graphics)
    importPackage(heavyindustry.graphics.g2d)
    importPackage(heavyindustry.graphics.g3d)
    importPackage(heavyindustry.graphics.g3d.model)
    importPackage(heavyindustry.graphics.g3d.model.obj)
    importPackage(heavyindustry.graphics.g3d.model.obj.mtl)
    importPackage(heavyindustry.graphics.g3d.model.obj.obj)
    importPackage(heavyindustry.graphics.g3d.render)
    importPackage(heavyindustry.input)
    importPackage(heavyindustry.io)
    importPackage(heavyindustry.maps)
    importPackage(heavyindustry.maps.planets)
    importPackage(heavyindustry.math)
    importPackage(heavyindustry.math.gravity)
    importPackage(heavyindustry.mod)
    importPackage(heavyindustry.net)
    importPackage(heavyindustry.struct)
    importPackage(heavyindustry.type)
    importPackage(heavyindustry.type.unit)
    importPackage(heavyindustry.type.weapons)
    importPackage(heavyindustry.type.weather)
    importPackage(heavyindustry.ui)
    importPackage(heavyindustry.ui.components)
    importPackage(heavyindustry.ui.defaults)
    importPackage(heavyindustry.ui.dialogs)
    importPackage(heavyindustry.ui.elements)
    importPackage(heavyindustry.ui.fragment)
    importPackage(heavyindustry.ui.listeners)
    importPackage(heavyindustry.ui.tooltips)
    importPackage(heavyindustry.util)
    importPackage(heavyindustry.util.path)
    importPackage(heavyindustry.world)
    importPackage(heavyindustry.world.blocks)
    importPackage(heavyindustry.world.blocks.defense)
    importPackage(heavyindustry.world.blocks.defense.turrets)
    importPackage(heavyindustry.world.blocks.distribution)
    importPackage(heavyindustry.world.blocks.environment)
    importPackage(heavyindustry.world.blocks.heat)
    importPackage(heavyindustry.world.blocks.liquid)
    importPackage(heavyindustry.world.blocks.logic)
    importPackage(heavyindustry.world.blocks.payload)
    importPackage(heavyindustry.world.blocks.power)
    importPackage(heavyindustry.world.blocks.production)
    importPackage(heavyindustry.world.blocks.sandbox)
    importPackage(heavyindustry.world.blocks.storage)
    importPackage(heavyindustry.world.blocks.units)
    importPackage(heavyindustry.world.components)
    importPackage(heavyindustry.world.consumers)
    importPackage(heavyindustry.world.draw)
    importPackage(heavyindustry.world.lightning)
    importPackage(heavyindustry.world.lightning.generator)
    importPackage(heavyindustry.world.meta)
    importPackage(heavyindustry.world.particle)
    importPackage(heavyindustry.world.particle.model)
"""