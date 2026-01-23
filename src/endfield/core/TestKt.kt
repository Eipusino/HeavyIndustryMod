package endfield.core

import org.jetbrains.annotations.TestOnly
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodHandles.Lookup

/** Classes for testing purposes only, do not use. */
@TestOnly
object TestKt : Test() {
	@JvmField
	val publicLookup: Lookup = MethodHandles.publicLookup()

	@JvmStatic
	@Throws(Throwable::class)
	fun testKt() {}

	override fun copy(): TestKt {
		return super.clone() as TestKt
	}
}