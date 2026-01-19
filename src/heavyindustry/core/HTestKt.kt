package heavyindustry.core

import org.jetbrains.annotations.TestOnly
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodHandles.Lookup

/** Classes for testing purposes only, do not use. */
@TestOnly
object HTestKt : HTest() {
	// Each object has a static field called INSTANCE.
	// If you need to access instance members of this class, please access them through this field.
	// (HTestKt.INSTANCE)
	// public static final INSTANCE = new HTestKt();

	// The static field (HTestKt.publicLookup)
	@JvmField
	val publicLookup: Lookup = MethodHandles.publicLookup()
	// public static Lookup publicLookup = MethodHandles.publicLookup();

	// The static method (HTestKt.testKt())
	@JvmStatic
	@Throws(Throwable::class)
	fun testKt() {}
	// public static void testKt() { /* compiled code */ }

	// The virtual method (HTestKt.INSTANCE.copy())
	override fun copy(): HTestKt {
		return super.clone() as HTestKt
	}
	// @Override
	// public HTestKt copy() { /* compiled code */ }
}