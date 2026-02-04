package endfield.input

import arc.Input
import arc.func.Cons
import arc.input.KeyCode

class CombineKeyTree<T> {
	@JvmField
	internal val tempMap = mutableMapOf<CombinedKeys, T>()

	@JvmField
	internal val normalBindings = mutableMapOf<CombinedKeys, T>()
	@JvmField
	internal val ctrlBindings = mutableMapOf<CombinedKeys, T>()
	@JvmField
	internal val altBindings = mutableMapOf<CombinedKeys, T>()
	@JvmField
	internal val shiftBindings = mutableMapOf<CombinedKeys, T>()
	@JvmField
	internal val altCtrlBindings = mutableMapOf<CombinedKeys, T>()
	@JvmField
	internal val ctrlShiftBindings = mutableMapOf<CombinedKeys, T>()
	@JvmField
	internal val altShiftBindings = mutableMapOf<CombinedKeys, T>()
	@JvmField
	internal val altCtrlShiftBindings = mutableMapOf<CombinedKeys, T>()

	fun putKeyBinding(binding: CombinedKeys, rec: T) {
		if (binding.isShift) {
			if (binding.isAlt && binding.isCtrl) altCtrlShiftBindings[binding] = rec
			else if (binding.isAlt) altShiftBindings[binding] = rec
			else if (binding.isCtrl) ctrlShiftBindings[binding] = rec
			else shiftBindings[binding] = rec
		} else {
			if (binding.isAlt && binding.isCtrl) altCtrlBindings[binding] = rec
			else if (binding.isAlt) altBindings[binding] = rec
			else if (binding.isCtrl) ctrlBindings[binding] = rec
			else normalBindings[binding] = rec
		}
	}

	fun putKeyBinds(vararg bindings: Pair<CombinedKeys, T>) = bindings.forEach { putKeyBinding(it.first, it.second) }

	fun clear() {
		normalBindings.clear()
		altBindings.clear()
		ctrlBindings.clear()
		shiftBindings.clear()
		altShiftBindings.clear()
		ctrlShiftBindings.clear()
		altCtrlBindings.clear()
		altCtrlShiftBindings.clear()
	}

	fun forEach(block: (CombinedKeys, T) -> Unit) {
		normalBindings.forEach(block)
		ctrlBindings.forEach(block)
		altBindings.forEach(block)
		altCtrlBindings.forEach(block)
		ctrlShiftBindings.forEach(block)
		altShiftBindings.forEach(block)
		altCtrlShiftBindings.forEach(block)
	}

	fun containsKeyCode(key: KeyCode?): Boolean {
		if (key == null) return false

		if (key.isCtrl())
			return ctrlBindings.isNotEmpty() || ctrlShiftBindings.isNotEmpty() || altCtrlBindings.isNotEmpty() || altCtrlShiftBindings.isNotEmpty()
		if (key.isAlt())
			return altBindings.isNotEmpty() || altShiftBindings.isNotEmpty() || altCtrlBindings.isNotEmpty() || altCtrlShiftBindings.isNotEmpty()
		if (key.isShift())
			return shiftBindings.isNotEmpty() || ctrlShiftBindings.isNotEmpty() || altShiftBindings.isNotEmpty() || altCtrlShiftBindings.isNotEmpty()

		return normalBindings.any { it.key.key == key }
				|| ctrlBindings.any { it.key.key == key }
				|| altBindings.any { it.key.key == key }
				|| shiftBindings.any { it.key.key == key }
				|| altCtrlBindings.any { it.key.key == key }
				|| ctrlShiftBindings.any { it.key.key == key }
				|| altShiftBindings.any { it.key.key == key }
				|| altCtrlShiftBindings.any { it.key.key == key }
	}

	fun getTargetBindings(input: Input, fuzzyMatch: Boolean = false) = getTargetBindings(
		input.ctrl(),
		input.alt(),
		input.shift(),
		fuzzyMatch
	)

	fun getTargetBindings(input: CombinedKeys, fuzzyMatch: Boolean = false) = getTargetBindings(
		input.isCtrl,
		input.isAlt,
		input.isShift,
		fuzzyMatch
	)

	fun getTargetBindings(
		ctrlDown: Boolean,
		altDown: Boolean,
		shiftDown: Boolean,
		fuzzyMatch: Boolean = false
	): Map<CombinedKeys, T> {
		tempMap.clear()
		if (fuzzyMatch) {
			tempMap.putAll(normalBindings)
			if (ctrlDown) {
				tempMap.putAll(ctrlBindings)
				if (altDown) {
					tempMap.putAll(altCtrlBindings)
					if (shiftDown) tempMap.putAll(altCtrlShiftBindings)
				} else if (shiftDown) tempMap.putAll(ctrlShiftBindings)
			} else {
				if (altDown) {
					tempMap.putAll(altBindings)
					if (shiftDown) tempMap.putAll(altShiftBindings)
				} else if (shiftDown) tempMap.putAll(shiftBindings)
			}
			return tempMap
		} else {
			return if (shiftDown) {
				if (altDown) if (ctrlDown) altCtrlShiftBindings else altShiftBindings
				else if (ctrlDown) ctrlShiftBindings else shiftBindings
			} else {
				if (altDown) if (ctrlDown) altCtrlBindings else altBindings
				else if (ctrlDown) ctrlBindings else normalBindings
			}
		}
	}

	inline fun eachTargetBindings(input: Input, fuzzyMatch: Boolean = false, cons: (CombinedKeys, T) -> Unit) {
		for ((k, r) in getTargetBindings(input, fuzzyMatch)) cons(k, r)
	}

	fun eachDown(input: Input, fuzzyMatch: Boolean = false, cons: Cons<T>) {
		eachTargetBindings(input, fuzzyMatch) { k, r -> if (input.keyDown(k.key)) cons.get(r) }
	}

	fun eachRelease(input: Input, fuzzyMatch: Boolean = false, cons: Cons<T>) {
		eachTargetBindings(input, fuzzyMatch) { k, r -> if (input.keyRelease(k.key)) cons.get(r) }
	}

	fun eachTap(input: Input, fuzzyMatch: Boolean = false, cons: Cons<T>) {
		eachTargetBindings(input, fuzzyMatch) { k, r -> if (input.keyTap(k.key)) cons.get(r) }
	}

	fun checkDown(input: Input): T? {
		eachTargetBindings(input) { k, r -> if (input.keyDown(k.key)) return r }
		return null
	}

	fun checkRelease(input: Input): T? {
		eachTargetBindings(input) { k, r -> if (input.keyRelease(k.key)) return r }
		return null
	}

	fun checkTap(input: Input): T? {
		eachTargetBindings(input) { k, r -> if (input.keyTap(k.key)) return r }
		return null
	}

	override fun toString(): String {
		val stringBuilder = StringBuilder()
		forEach { keys, rec ->
			stringBuilder.append("$keys -> $rec,\n")
		}
		return stringBuilder.toString()
	}
}