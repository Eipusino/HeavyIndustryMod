package heavyindustry.core

import org.jetbrains.annotations.TestOnly

/** Classes for testing purposes only, do not use. */
@TestOnly
object HTestKt : HTest() {
	@JvmStatic
	@Throws(Throwable::class)
	fun testKt() {}
}