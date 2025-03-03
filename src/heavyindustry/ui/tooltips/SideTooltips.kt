package heavyindustry.ui.tooltips

import arc.scene.Element
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Table

fun <T : Element> Cell<T>.tooltipSide(align: Int, builder: Table.() -> Unit): Cell<T> = Tooltipf.tooltipSide(this, align, builder)

fun <T : Element> Cell<T>.tooltipSide(align: Int, tooltip: String): Cell<T> = Tooltipf.tooltipSide(this, align, tooltip)