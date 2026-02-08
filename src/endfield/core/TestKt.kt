package endfield.core

import org.jetbrains.annotations.TestOnly

/** Classes for testing purposes only, do not use. */
@TestOnly
object TestKt : Test() {
	@JvmStatic
	@Throws(Throwable::class)
	fun testKt() {}

	override fun copy(): TestKt {
		return super.clone() as TestKt
	}
}