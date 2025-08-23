package heavyindustry.util

import arc.func.Cons

object KUtils {
	@JvmStatic
	fun <T> equals(a: Boolean, b: T, c: T): T {
		return if (a) b else c
	}

	@JvmStatic
	inline fun <reified T> equals(a: Any?, b: Cons<T>) {
		if (a is T) b.get(a)
	}

	@JvmStatic
	fun repeat(key: String, count: Int): String {
		if (count < 1) return ""

		return when (count) {
			1 -> key
			else -> {
				return when (key.length) {
					0 -> ""
					1 -> key[0].let { char -> String(CharArray(count) { char }) }
					else -> {
						val sb = StringBuilder(count * key.length)
						for (i in 1..count) {
							sb.append(key)
						}
						return sb.toString()
					}
				}
			}
		}
	}
}