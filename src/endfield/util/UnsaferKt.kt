@file:Suppress("UNCHECKED_CAST")

package endfield.util

import endfield.util.Unsafer.unsafe
import org.jetbrains.annotations.Contract
import java.lang.reflect.Array

object UnsaferKt {
	/**
	 * Allocates an instance but does not run any constructor. Initializes the class if it has not yet been.
	 * May cause strange bugs or even <strong>JVM crashes</strong>, use with caution.
	 *
	 * @throws NullPointerException If type is null
	 * @throws InstantiationException If type is an interface, abstract class, or primitive type.
	 * @since 1.0.9
	 */
	@JvmStatic
	@Contract(value = "null -> fail; _ -> new", pure = true)
	fun <T> allocateInstance(type: Class<out T>?): T {
		// The native part of allocateInstance does not have a null check, giving a null value can cause the JVM to crash.
		checkNotNull(type)
		// The String class can cause strange bugs, So return an empty string directly.
		if (type === String::class.java) return "" as T
		if (type.isArray()) return Array.newInstance(type.getComponentType(), 0) as T

		return unsafe.allocateInstance(type) as T
	}
}