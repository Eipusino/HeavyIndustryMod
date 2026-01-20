package heavyindustry.core

import org.jetbrains.annotations.TestOnly
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodHandles.Lookup

/** Classes for testing purposes only, do not use. */
@TestOnly
object HTestKt : HTest() {
	@JvmField
	val publicLookup: Lookup = MethodHandles.publicLookup()

	@JvmStatic
	@Throws(Throwable::class)
	fun testKt() {}

	override fun copy(): HTestKt {
		return super.clone() as HTestKt
	}
}