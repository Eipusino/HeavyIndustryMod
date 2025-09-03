package heavyindustry.util;

import arc.util.OS;
import heavyindustry.android.field.FieldUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static heavyindustry.util.Utils.requireInstance;
import static heavyindustry.util.Utils.requireNonNullInstance;

/**
 * {@code Unsafe} reflection tool. Mainly provides functions for modifying or setting field values.
 * <p>This class behavior will violate the access protection of Java security encapsulation and is inherently
 * insecure. If it is not necessary, please try to avoid using this class.
 * <p><strong>Never use {@link FieldUtils#getFieldOffset(Field)} on non Android platforms, as this will directly
 * trigger {@link NoSuchMethodError}. If you want to obtain the offset of a field, please use the relevant
 * functions provided by this class.</strong>
 *
 * @author Eipusino
 * @see InteUnsafer
 * @since 1.0.7
 */
public final class Unsafer {
	/** Initialize in libs/Impl.jar in the mod resource package. */
	public static Unsafe unsafe;

	/** Do not call. */
	private Unsafer() {}

	public static <T> T getObject(Class<?> type, String name, Object object) {
		try {
			return getObject(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrieve the value of a field through {@code Unsafe}. If the field is {@code static}, object can be {@code null}.
	 * Otherwise, {@code object} must not be {@code null} and be an instance of {@code field.getDeclaringClass()}.
	 *
	 * @throws IllegalArgumentException If the field type is a primitive type.
	 * @throws ClassCastException If the field is not {@code static} and the {@code object} is not an
	 *                                  instance of {@code field.getDeclaringClass()} or {@code null}.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getObject(Field field, Object object) {
		if (field.getType().isPrimitive()) throw new IllegalArgumentException("Method 'getObject' does not support field of primitive types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return (T) (Modifier.isVolatile(modifiers) ?
				unsafe.getObjectVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				unsafe.getObject(isStatic ? type : requireNonNullInstance(type, object), offset));
	}

	public static boolean getBool(Class<?> type, String name, Object object) {
		try {
			return getBool(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean getBool(Field field, Object object) {
		if (field.getType() != boolean.class) throw new IllegalArgumentException("Method 'getBool' does not support field other than boolean types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getBooleanVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				unsafe.getBoolean(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static byte getByte(Class<?> type, String name, Object object) {
		try {
			return getByte(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte getByte(Field field, Object object) {
		if (field.getType() != byte.class) throw new IllegalArgumentException("Method 'getByte' does not support field other than byte types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getByteVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				unsafe.getByte(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static short getShort(Class<?> type, String name, Object object) {
		try {
			return getShort(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static short getShort(Field field, Object object) {
		if (field.getType() != short.class) throw new IllegalArgumentException("Method 'getShort' does not support field other than short types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getShortVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				unsafe.getShort(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static int getInt(Class<?> type, String name, Object object) {
		try {
			return getInt(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static int getInt(Field field, Object object) {
		if (field.getType() != int.class) throw new IllegalArgumentException("Method 'getInt' does not support field other than int types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getIntVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				unsafe.getInt(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static long getLong(Class<?> type, String name, Object object) {
		try {
			return getLong(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static long getLong(Field field, Object object) {
		if (field.getType() != long.class) throw new IllegalArgumentException("Method 'getLong' does not support field other than long types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getLongVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				unsafe.getLong(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static char getChar(Class<?> type, String name, Object object) {
		try {
			return getChar(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static char getChar(Field field, Object object) {
		if (field.getType() != char.class) throw new IllegalArgumentException("Method 'getChar' does not support field other than char types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getCharVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				unsafe.getChar(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static float getFloat(Class<?> type, String name, Object object) {
		try {
			return getFloat(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static float getFloat(Field field, Object object) {
		if (field.getType() != float.class) throw new IllegalArgumentException("Method 'getFloat' does not support field other than float types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getFloatVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				unsafe.getFloat(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static double getDouble(Class<?> type, String name, Object object) {
		try {
			return getDouble(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static double getDouble(Field field, Object object) {
		if (field.getType() != double.class) throw new IllegalArgumentException("Method 'getDouble' does not support field other than double types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getDoubleVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				unsafe.getDouble(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static void setObject(Class<?> type, String name, Object object, Object value) {
		try {
			setObject(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the value of a field through {@code Unsafe}. If the field is {@code static}, object can be {@code null}.
	 * Otherwise, {@code object} must not be {@code null} and be an instance of {@code field.getDeclaringClass()}.
	 *
	 * @throws IllegalArgumentException If the field type is a primitive type.
	 * @throws ClassCastException If the field is not {@code static} and the {@code object} is not an
	 *                                  instance of {@code field.getDeclaringClass()} or {@code null}.
	 */
	public static void setObject(Field field, Object object, Object value) {
		if (field.getType().isPrimitive()) throw new IllegalArgumentException("Method 'getObject' does not support field of primitive types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putObjectVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, requireInstance(field.getType(), value));
		} else {
			unsafe.putObject(isStatic ? type : requireNonNullInstance(type, object), offset, requireInstance(field.getType(), value));
		}
	}

	public static void setBool(Class<?> type, String name, Object object, boolean value) {
		try {
			setBool(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setBool(Field field, Object object, boolean value) {
		if (field.getType() != boolean.class) throw new IllegalArgumentException("Method 'setBool' does not support field other than boolean types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putBooleanVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			unsafe.putBoolean(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setByte(Class<?> type, String name, Object object, byte value) {
		try {
			setByte(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setByte(Field field, Object object, byte value) {
		if (field.getType() != byte.class) throw new IllegalArgumentException("Method 'setByte' does not support field other than byte types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putByteVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			unsafe.putByte(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setShort(Class<?> type, String name, Object object, short value) {
		try {
			setShort(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setShort(Field field, Object object, short value) {
		if (field.getType() != short.class) throw new IllegalArgumentException("Method 'setShort' does not support field other than short types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putShortVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			unsafe.putShort(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setInt(Class<?> type, String name, Object object, int value) {
		try {
			setInt(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setInt(Field field, Object object, int value) {
		if (field.getType() != int.class) throw new IllegalArgumentException("Method 'setInt' does not support field other than int types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putIntVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			unsafe.putInt(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setLong(Class<?> type, String name, Object object, long value) {
		try {
			setLong(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setLong(Field field, Object object, long value) {
		if (field.getType() != long.class) throw new IllegalArgumentException("Method 'setLong' does not support field other than long types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putLongVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			unsafe.putLong(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setChar(Class<?> type, String name, Object object, char value) {
		try {
			setChar(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setChar(Field field, Object object, char value) {
		if (field.getType() != char.class) throw new IllegalArgumentException("Method 'setChar' does not support field other than char types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putCharVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			unsafe.putChar(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setFloat(Class<?> type, String name, Object object, float value) {
		try {
			setFloat(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setFloat(Field field, Object object, float value) {
		if (field.getType() != float.class) throw new IllegalArgumentException("Method 'setFloat' does not support field other than float types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putFloatVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			unsafe.putFloat(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setDouble(Class<?> type, String name, Object object, double value) {
		try {
			setDouble(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setDouble(Field field, Object object, double value) {
		if (field.getType() != double.class) throw new IllegalArgumentException("Method 'setDouble' does not support field other than double types");

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putDoubleVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			unsafe.putDouble(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static Object get(Class<?> type, String name, Object object) {
		try {
			return get(type.getDeclaredField(name), object);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static Object get(Field field, Object object) {
		long offset = getOffset(field);
		Class<?> type = field.getType(), clazz = field.getDeclaringClass();
		Object o = Modifier.isStatic(field.getModifiers()) ? clazz : requireNonNullInstance(clazz, object);

		if (Modifier.isVolatile(field.getModifiers())) {
			if (type.isPrimitive()) {
				if (type == int.class) return unsafe.getIntVolatile(o, offset);
				else if (type == float.class) return unsafe.getFloatVolatile(o, offset);
				else if (type == boolean.class) return unsafe.getBooleanVolatile(o, offset);
				else if (type == byte.class) return unsafe.getByteVolatile(o, offset);
				else if (type == long.class) return unsafe.getLongVolatile(o, offset);
				else if (type == double.class) return unsafe.getDoubleVolatile(o, offset);
				else if (type == char.class) return unsafe.getCharVolatile(o, offset);
				else if (type == short.class) return unsafe.getShortVolatile(o, offset);
				else throw new IllegalArgumentException("unknown type of field " + field);
			} else {
				return unsafe.getObjectVolatile(o, offset);
			}
		} else {
			if (type.isPrimitive()) {
				if (type == int.class) return unsafe.getInt(o, offset);
				else if (type == float.class) return unsafe.getFloat(o, offset);
				else if (type == boolean.class) return unsafe.getBoolean(o, offset);
				else if (type == byte.class) return unsafe.getByte(o, offset);
				else if (type == long.class) return unsafe.getDouble(o, offset);
				else if (type == double.class) return unsafe.getLong(o, offset);
				else if (type == char.class) return unsafe.getChar(o, offset);
				else if (type == short.class) return unsafe.getShort(o, offset);
				else throw new IllegalArgumentException("unknown type of field " + type);
			} else {
				return unsafe.getObject(o, offset);
			}
		}
	}

	public static void set(Class<?> type, String name, Object object, Object value) {
		try {
			set(type.getDeclaredField(name), object, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static void set(Field field, Object object, Object value) {
		long offset = getOffset(field);
		Class<?> type = field.getType(), clazz = field.getDeclaringClass();
		Object o = Modifier.isStatic(field.getModifiers()) ? clazz : requireNonNullInstance(clazz, object);

		if (Modifier.isVolatile(field.getModifiers())) {
			if (type.isPrimitive()) {
				if (type == int.class) unsafe.putIntVolatile(o, offset, (int) value);
				else if (type == float.class) unsafe.putFloatVolatile(o, offset, (float) value);
				else if (type == boolean.class) unsafe.putBooleanVolatile(o, offset, (boolean) value);
				else if (type == byte.class) unsafe.putByteVolatile(o, offset, (byte) value);
				else if (type == long.class) unsafe.putLongVolatile(o, offset, (long) value);
				else if (type == double.class) unsafe.putDoubleVolatile(o, offset, (double) value);
				else if (type == char.class) unsafe.putCharVolatile(o, offset, (char) value);
				else if (type == short.class) unsafe.putShortVolatile(o, offset, (short) value);
				else throw new IllegalArgumentException("unknown type of field " + field);
			} else {
				unsafe.putObjectVolatile(o, offset, requireInstance(field.getType(), value));
			}
		} else {
			if (type.isPrimitive()) {
				if (type == int.class) unsafe.putInt(o, offset, (int) value);
				else if (type == float.class) unsafe.putFloat(o, offset, (float) value);
				else if (type == boolean.class) unsafe.putBoolean(o, offset, (boolean) value);
				else if (type == byte.class) unsafe.putByte(o, offset, (byte) value);
				else if (type == double.class) unsafe.putDouble(o, offset, (double) value);
				else if (type == long.class) unsafe.putLong(o, offset, (long) value);
				else if (type == char.class) unsafe.putChar(o, offset, (char) value);
				else if (type == short.class) unsafe.putShort(o, offset, (short) value);
				else throw new IllegalArgumentException("unknown type of field " + field);
			} else {
				unsafe.putObject(o, offset, requireInstance(field.getType(), value));
			}
		}
	}

	/**
	 * Get the field offset.
	 */
	public static long getOffset(Field field) {
		// If it is an Android platform, simply call the getOffset method of the field.
		return OS.isAndroid ? FieldUtils.getFieldOffset(field) : Modifier.isStatic(field.getModifiers()) ?
				unsafe.staticFieldOffset(field) : unsafe.objectFieldOffset(field);
	}

	/**
	 * Get the offset of the object field.
	 *
	 * @see #getOffset(Field) getOffset
	 */
	static long objectOffset(Field field) {
		return OS.isAndroid ? FieldUtils.getFieldOffset(field) : unsafe.objectFieldOffset(field);
	}

	/**
	 * Get the offset of a static field.
	 *
	 * @see #getOffset(Field) getOffset
	 */
	static long staticOffset(Field field) {
		return OS.isAndroid ? FieldUtils.getFieldOffset(field) : unsafe.staticFieldOffset(field);
	}
}
