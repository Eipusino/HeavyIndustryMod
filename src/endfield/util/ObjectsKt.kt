package endfield.util

object ObjectsKt {
	/** Throws the exception without telling the java verifier. */
	@JvmStatic
	fun <T> thrower(err: Throwable): T {
		throw err
	}
}