@file:JvmName("KWorlds")

package heavyindustry.world

import arc.Core
import mindustry.Vars
import mindustry.io.SaveVersion
import mindustry.world.Block

fun exportBlockData() {
	val data = buildString {
		fun writeBlock(block: Block, name: String = block.name) {
			append(name)
			append(" ")
			append(if (block.synthetic()) "1" else "0")
			append(" ")
			append(if (block.solid) "1" else "0")
			append(" ")
			append(block.size.toString())
			append(" ")
			append((block.mapColor.rgba() ushr 8).toString())
			appendLine()
		}

		val allBlock = mutableListOf<Pair<String, Block>>()
		Vars.content.blocks().forEach { allBlock.add(it.name to it) }
		SaveVersion.fallback.forEach {
			Vars.content.block(it.value)
				?.let { block -> allBlock.add(it.key to block) }
		}

		allBlock.sortedBy { it.second.id }
			.forEach { (key, block) -> writeBlock(block, key) }
	}
	Vars.platform.showFileChooser(false, "Export Block Data", "dat") { file ->
		if (file == null) return@showFileChooser
		file.writeString(data, false)
		Core.app.post { Vars.ui.showInfo("Block data exported to ${file.name()}") }
	}
}
