package heavyindustry.core

import heavyindustry.util.Reflects
import org.jetbrains.annotations.TestOnly
import java.lang.invoke.MethodHandles

/** Classes for testing purposes only, do not use. */
@TestOnly
object HTestKt : HTest() {
	@JvmField
	val publicLookup = MethodHandles.publicLookup()
	@JvmField
	val privateLookup = MethodHandles.privateLookupIn(HTestKt::class.java, Reflects.lookup)

	@JvmStatic
	@Throws(Throwable::class)
	fun testKt() {}
}