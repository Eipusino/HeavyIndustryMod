package endfield.util

object ObjectsKt {
	/** Throws the exception without telling the java verifier. */
	@JvmStatic
	fun <T> thrower(err: Throwable): T {
		throw err
	}

	@JvmStatic
	fun Any.asInt(def: Int): Int {
		return if (this is Number) toInt() else def
	}

	@JvmStatic
	fun Any.asFloat(def: Float): Float {
		return if (this is Number) toFloat() else def
	}
}