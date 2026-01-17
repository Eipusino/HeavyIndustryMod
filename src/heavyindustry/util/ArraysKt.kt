package heavyindustry.util

object ArraysKt {
	@JvmStatic
	@SafeVarargs
	fun <T> arr(vararg arr: T): Array<out T> = arr
}