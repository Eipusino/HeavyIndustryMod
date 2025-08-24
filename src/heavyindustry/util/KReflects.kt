@file:JvmName("KReflects")

package heavyindustry.util

import arc.util.Reflect
import heavyindustry.util.Reflects.getField
import heavyindustry.util.Utils.cast
import sun.misc.Unsafe
import java.lang.reflect.Field

private val unsafe: Unsafe = Reflect.get(Unsafe::class.java, "theUnsafe")

fun <T> get(field: Field, obj: Any): T {
	return cast(unsafe.getObject(obj, unsafe.objectFieldOffset(field)))
}

fun <T> get(type: Class<*>, obj: Any, name: String): T {
	return cast(unsafe.getObject(obj, unsafe.objectFieldOffset(getField(type, name))))
}

fun <T> get(obj: Any, name: String): T = get(obj.javaClass, obj, name)

fun getBool(type: Class<*>, obj: Any, name: String): Boolean {
	return unsafe.getBoolean(obj, unsafe.objectFieldOffset(getField(type, name)))
}

fun getInt(type: Class<*>, obj: Any, name: String): Int {
	return unsafe.getInt(obj, unsafe.objectFieldOffset(getField(type, name)))
}

fun getFloat(type: Class<*>, obj: Any, name: String): Float {
	return unsafe.getFloat(obj, unsafe.objectFieldOffset(getField(type, name)))
}

fun set(field: Field, obj: Any, value: Any?) {
	unsafe.putObject(obj, unsafe.objectFieldOffset(field), value)
}

fun set(type: Class<*>, obj: Any, name: String, value: Any?) {
	unsafe.putObject(obj, unsafe.objectFieldOffset(getField(type, name)), value)
}

fun set(obj: Any, name: String, value: Any?) {
	set(obj.javaClass, obj, name, value)
}

fun setBool(type: Class<*>, obj: Any, name: String, value: Boolean) {
	unsafe.putBoolean(obj, unsafe.objectFieldOffset(getField(type, name)), value)
}

fun setInt(type: Class<*>, obj: Any, name: String, value: Int) {
	unsafe.putInt(obj, unsafe.objectFieldOffset(getField(type, name)), value)
}

fun setFloat(type: Class<*>, obj: Any, name: String, value: Float) {
	unsafe.putFloat(obj, unsafe.objectFieldOffset(getField(type, name)), value)
}