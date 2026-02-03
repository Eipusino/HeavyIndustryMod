package endfield.input

import arc.Core
import arc.input.KeyCode
import arc.scene.event.InputEvent
import arc.scene.event.InputListener
import arc.struct.Seq

open class CombineKeyListener<T>(
	val keysTree: CombineKeyTree<T>,
	val fuzzed: Boolean = false
) : InputListener() {
	private val keysDown = Seq<KeyCode>(KeyCode::class.java)

	override fun keyDown(event: InputEvent?, keycode: KeyCode?): Boolean {
		if (!keysTree.containsKeyCode(keycode)) return false
		keysDown.addUnique(keycode)

		if (keycode!!.isControllerKey()) return true

		if (!fuzzed) {
			val map = keysTree.getTargetBindings(Core.input)
			if (map.isEmpty()) return false

			val keys = CombinedKeys(*keysDown.toArray())
			map[keys]?.let { keysDown(event, keycode, keys, it) }
		} else {
			keysTree.eachTargetBindings(Core.input, true) { k, r ->
				if (k.key == keycode) keysDown(event, keycode, k, r)
			}
		}

		return true
	}

	override fun keyUp(event: InputEvent?, keycode: KeyCode?): Boolean {
		if (!keysTree.containsKeyCode(keycode)) return false

		if (keycode!!.isControllerKey()) {
			return keysDown.remove(keycode)
		}

		if (!fuzzed) {
			val map = keysTree.getTargetBindings(Core.input)
			if (map.isEmpty()) return false

			keysDown.add(keycode) // When the button is lifted, it will inevitably listen to the last button that is lifted
			val keys = CombinedKeys(*keysDown.toArray())
			map[keys]?.let { keysUp(event, keycode, keys, it) }
			keysDown.remove(keysDown.size - 1)
		} else {
			keysTree.eachTargetBindings(Core.input, true) { k, r ->
				if (k.key == keycode) keysUp(event, keycode, k, r)
			}
		}
		return keysDown.remove(keycode)
	}

	open fun keysDown(event: InputEvent?, keycode: KeyCode?, combinedKeys: CombinedKeys, rec: T) {}
	open fun keysUp(event: InputEvent?, keycode: KeyCode?, combinedKeys: CombinedKeys, rec: T) {}
}