package heavyindustry.util;

import arc.util.OS;
import heavyindustry.android.field.FieldUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Unsafe-reflection utilities.
 *
 * @since 1.0.7
 */
public final class Unsafer {
	static final Unsafe unsafe = getUnsafe();

	private Unsafer() {}

	/** Get the unique instance of Unsafe. */
	static Unsafe getUnsafe() {
		try {
			Field field = Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			return (Unsafe) field.get(null);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T getObject(Class<?> type, Object object, String name) {
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
	 * @throws IllegalArgumentException If the field is not {@code static} and the {@code object} is not an
	 *                                  instance of {@code field.getDeclaringClass()} or {@code null}.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getObject(Field field, Object object) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return (T) (Modifier.isVolatile(modifiers) ?
				unsafe.getObjectVolatile(isStatic ? type : requireInstance(type, object), offset) :
				unsafe.getObject(isStatic ? type : requireInstance(type, object), offset));
	}

	public static boolean getBool(Field field, Object object) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getBooleanVolatile(isStatic ? type : requireInstance(type, object), offset) :
				unsafe.getBoolean(isStatic ? type : requireInstance(type, object), offset);
	}

	public static byte getByte(Field field, Object object) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getByteVolatile(isStatic ? type : requireInstance(type, object), offset) :
				unsafe.getByte(isStatic ? type : requireInstance(type, object), offset);
	}

	public static short getShort(Field field, Object object) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getShortVolatile(isStatic ? type : requireInstance(type, object), offset) :
				unsafe.getShort(isStatic ? type : requireInstance(type, object), offset);
	}

	public static int getInt(Field field, Object object) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getIntVolatile(isStatic ? type : requireInstance(type, object), offset) :
				unsafe.getInt(isStatic ? type : requireInstance(type, object), offset);
	}

	public static long getLong(Field field, Object object) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getLongVolatile(isStatic ? type : requireInstance(type, object), offset) :
				unsafe.getLong(isStatic ? type : requireInstance(type, object), offset);
	}

	public static char getChar(Field field, Object object) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getCharVolatile(isStatic ? type : requireInstance(type, object), offset) :
				unsafe.getChar(isStatic ? type : requireInstance(type, object), offset);
	}

	public static float getFloat(Field field, Object object) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getFloatVolatile(isStatic ? type : requireInstance(type, object), offset) :
				unsafe.getFloat(isStatic ? type : requireInstance(type, object), offset);
	}

	public static double getDouble(Field field, Object object) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				unsafe.getDoubleVolatile(isStatic ? type : requireInstance(type, object), offset) :
				unsafe.getDouble(isStatic ? type : requireInstance(type, object), offset);
	}

	public static void setObject(Class<?> type, Object object, String name, Object value) {
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
	 * @throws IllegalArgumentException If the field is not {@code static} and the {@code object} is not an
	 *                                  instance of {@code field.getDeclaringClass()} or {@code null}.
	 */
	public static void setObject(Field field, Object object, Object value) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putObjectVolatile(isStatic ? type : requireInstance(type, object), offset, value);
		} else {
			unsafe.putObject(isStatic ? type : requireInstance(type, object), offset, value);
		}
	}

	public static void setBool(Field field, Object object, boolean value) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putBooleanVolatile(isStatic ? type : requireInstance(type, object), offset, value);
		} else {
			unsafe.putBoolean(isStatic ? type : requireInstance(type, object), offset, value);
		}
	}

	public static void setByte(Field field, Object object, byte value) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putByteVolatile(isStatic ? type : requireInstance(type, object), offset, value);
		} else {
			unsafe.putByte(isStatic ? type : requireInstance(type, object), offset, value);
		}
	}

	public static void setShort(Field field, Object object, short value) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putShortVolatile(isStatic ? type : requireInstance(type, object), offset, value);
		} else {
			unsafe.putShort(isStatic ? type : requireInstance(type, object), offset, value);
		}
	}

	public static void setInt(Field field, Object object, int value) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putIntVolatile(isStatic ? type : requireInstance(type, object), offset, value);
		} else {
			unsafe.putInt(isStatic ? type : requireInstance(type, object), offset, value);
		}
	}

	public static void setLong(Field field, Object object, long value) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putLongVolatile(isStatic ? type : requireInstance(type, object), offset, value);
		} else {
			unsafe.putLong(isStatic ? type : requireInstance(type, object), offset, value);
		}
	}

	public static void setChar(Field field, Object object, char value) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putCharVolatile(isStatic ? type : requireInstance(type, object), offset, value);
		} else {
			unsafe.putChar(isStatic ? type : requireInstance(type, object), offset, value);
		}
	}

	public static void setFloat(Field field, Object object, float value) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putFloatVolatile(isStatic ? type : requireInstance(type, object), offset, value);
		} else {
			unsafe.putFloat(isStatic ? type : requireInstance(type, object), offset, value);
		}
	}

	public static void setDouble(Field field, Object object, double value) {
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? staticOffset(field) : objectOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			unsafe.putDoubleVolatile(isStatic ? type : requireInstance(type, object), offset, value);
		} else {
			unsafe.putDouble(isStatic ? type : requireInstance(type, object), offset, value);
		}
	}

	/**
	 * Get the field offset.
	 */
	public static long getOffset(Field field) {
		return OS.isAndroid ? FieldUtils.getFieldOffset(field) : Modifier.isStatic(field.getModifiers())? unsafe.staticFieldOffset(field): unsafe.objectFieldOffset(field);
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

	static <T> T requireInstance(Class<?> type, T obj) {
		if (!type.isInstance(obj))
			throw new IllegalArgumentException();
		return obj;
	}
}
