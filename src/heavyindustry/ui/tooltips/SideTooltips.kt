@file:Suppress("unused")

package heavyindustry.ui.tooltips

import arc.scene.*
import arc.scene.ui.layout.*

fun <T : Element> Cell<T>.tooltipSide(align: Int, builder: Table.() -> Unit): Cell<T> = Tooltipf.tooltipSide(this, align, builder)

fun <T : Element> Cell<T>.tooltipSide(align: Int, tooltip: String) = Tooltipf.tooltipSide(this, align, tooltip)