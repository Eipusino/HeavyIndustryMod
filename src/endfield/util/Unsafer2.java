package endfield.util;

import jdk.internal.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static endfield.Vars2.platformImpl;
import static endfield.util.Objects2.requireInstance;
import static endfield.util.Objects2.requireNonNullInstance;

/**
 * JDK-Internal-Unsafe class. It may become invalid in future Java or Android versions.
 * inconsistently on different devices, and is not recommended for non-essential use.
 * <p>The {@link #internalUnsafe} field of this class is public, but it is generally recommended to use the static
 * methods provided by the class, which perform some security check packaging on the methods inside the
 * {@code Unsafe} to avoid causing strange bugs or even <b>JVM crashes</b>. Unless you
 * are very clear about what you are doing at this moment, it is not recommended to use the {@link #internalUnsafe} field directly.
 *
 * @author Eipusino
 * @since 1.0.8
 */
public final class Unsafer2 {
	/** Initialize in libs/Impl.jar in the mod resource package. */
	public static Unsafe internalUnsafe;

	private Unsafer2() {}

	public static <T> T getObject(Class<?> type, String name, Object object) throws IllegalArgumentException {
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
	 * @throws IllegalArgumentException If any of the following  is true:
	 *                                  <ul><li>If the field type is a primitive type.
	 *                                  <li>If the field is not {@code static} and the {@code object} is not an
	 *                                                                   instance of {@code field.getDeclaringClass()} or {@code null}.</ul>
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getObject(Field field, Object object) throws IllegalArgumentException {
		if (field.getType().isPrimitive()) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		return (T) (Modifier.isVolatile(modifiers) ?
				internalUnsafe.getReferenceVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				internalUnsafe.getReference(isStatic ? type : requireNonNullInstance(type, object), offset));
	}

	public static boolean getBool(Field field, Object object) throws IllegalArgumentException {
		if (field.getType() != boolean.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				internalUnsafe.getBooleanVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				internalUnsafe.getBoolean(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static char getChar(Field field, Object object) throws IllegalArgumentException {
		if (field.getType() != char.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				internalUnsafe.getCharVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				internalUnsafe.getChar(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static byte getByte(Field field, Object object) throws IllegalArgumentException {
		if (field.getType() != byte.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				internalUnsafe.getByteVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				internalUnsafe.getByte(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static short getShort(Field field, Object object) throws IllegalArgumentException {
		if (field.getType() != short.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				internalUnsafe.getShortVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				internalUnsafe.getShort(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static int getInt(Field field, Object object) throws IllegalArgumentException {
		if (field.getType() != int.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				internalUnsafe.getIntVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				internalUnsafe.getInt(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static long getLong(Field field, Object object) throws IllegalArgumentException {
		if (field.getType() != long.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				internalUnsafe.getLongVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				internalUnsafe.getLong(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static float getFloat(Field field, Object object) throws IllegalArgumentException {
		if (field.getType() != float.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				internalUnsafe.getFloatVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				internalUnsafe.getFloat(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static double getDouble(Field field, Object object) throws IllegalArgumentException {
		if (field.getType() != double.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		return Modifier.isVolatile(modifiers) ?
				internalUnsafe.getDoubleVolatile(isStatic ? type : requireNonNullInstance(type, object), offset) :
				internalUnsafe.getDouble(isStatic ? type : requireNonNullInstance(type, object), offset);
	}

	public static void setObject(Class<?> type, String name, Object object, Object value) throws IllegalArgumentException {
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
	 * @throws IllegalArgumentException If any of the following  is true:
	 *                                  <ul><li>If the field type is a primitive type.
	 *                                  <li>If the field is not {@code static} and the {@code object} is not an
	 *                                                                   instance of {@code field.getDeclaringClass()} or {@code null}.</ul>
	 */
	public static void setObject(Field field, Object object, Object value) throws IllegalArgumentException {
		if (field.getType().isPrimitive()) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			internalUnsafe.putReferenceVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, requireInstance(field.getType(), value));
		} else {
			internalUnsafe.putReference(isStatic ? type : requireNonNullInstance(type, object), offset, requireInstance(field.getType(), value));
		}
	}

	public static void setBool(Field field, Object object, boolean value) throws IllegalArgumentException {
		if (field.getType() != boolean.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			internalUnsafe.putBooleanVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			internalUnsafe.putBoolean(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setChar(Field field, Object object, char value) throws IllegalArgumentException {
		if (field.getType() != char.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			internalUnsafe.putCharVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			internalUnsafe.putChar(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setByte(Field field, Object object, byte value) throws IllegalArgumentException {
		if (field.getType() != byte.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			internalUnsafe.putByteVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			internalUnsafe.putByte(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setShort(Field field, Object object, short value) throws IllegalArgumentException {
		if (field.getType() != byte.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			internalUnsafe.putShortVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			internalUnsafe.putShort(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setInt(Field field, Object object, int value) throws IllegalArgumentException {
		if (field.getType() != int.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			internalUnsafe.putIntVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			internalUnsafe.putInt(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setLong(Field field, Object object, long value) throws IllegalArgumentException {
		if (field.getType() != long.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			internalUnsafe.putLongVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			internalUnsafe.putLong(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setFloat(Field field, Object object, float value) throws IllegalArgumentException {
		if (field.getType() != float.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			internalUnsafe.putFloatVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			internalUnsafe.putFloat(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		}
	}

	public static void setDouble(Field field, Object object, double value) throws IllegalArgumentException {
		if (field.getType() != double.class) throw new IllegalArgumentException("illegal field type: " + field.getType());

		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);

		long offset = isStatic ? internalUnsafe.staticFieldOffset(field) : internalUnsafe.objectFieldOffset(field);

		Class<?> type = field.getDeclaringClass();

		if (Modifier.isVolatile(modifiers)) {
			internalUnsafe.putDoubleVolatile(isStatic ? type : requireNonNullInstance(type, object), offset, value);
		} else {
			internalUnsafe.putDouble(isStatic ? type : requireNonNullInstance(type, object), offset, value);
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
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);
		Class<?> type = field.getType(), base = field.getDeclaringClass();
		Object o = isStatic ? base : requireNonNullInstance(base, object);

		if (Modifier.isVolatile(modifiers)) {
			if (type.isPrimitive()) {
				if (type == int.class) return internalUnsafe.getIntVolatile(o, offset);
				else if (type == float.class) return internalUnsafe.getFloatVolatile(o, offset);
				else if (type == boolean.class) return internalUnsafe.getBooleanVolatile(o, offset);
				else if (type == byte.class) return internalUnsafe.getByteVolatile(o, offset);
				else if (type == long.class) return internalUnsafe.getLongVolatile(o, offset);
				else if (type == double.class) return internalUnsafe.getDoubleVolatile(o, offset);
				else if (type == char.class) return internalUnsafe.getCharVolatile(o, offset);
				else if (type == short.class) return internalUnsafe.getShortVolatile(o, offset);
				else throw new IllegalArgumentException("unknown type of field " + field);
			} else {
				return internalUnsafe.getReferenceVolatile(o, offset);
			}
		} else {
			if (type.isPrimitive()) {
				if (type == int.class) return internalUnsafe.getInt(o, offset);
				else if (type == float.class) return internalUnsafe.getFloat(o, offset);
				else if (type == boolean.class) return internalUnsafe.getBoolean(o, offset);
				else if (type == byte.class) return internalUnsafe.getByte(o, offset);
				else if (type == long.class) return internalUnsafe.getDouble(o, offset);
				else if (type == double.class) return internalUnsafe.getLong(o, offset);
				else if (type == char.class) return internalUnsafe.getChar(o, offset);
				else if (type == short.class) return internalUnsafe.getShort(o, offset);
				else throw new IllegalArgumentException("unknown type of field " + field);
			} else {
				return internalUnsafe.getReference(o, offset);
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
		int modifiers = field.getModifiers();
		boolean isStatic = Modifier.isStatic(modifiers);
		long offset = isStatic ? platformImpl.staticOffset(field) : platformImpl.objectOffset(field);
		Class<?> type = field.getType(), base = field.getDeclaringClass();
		Object o = isStatic ? base : requireNonNullInstance(base, object);

		if (Modifier.isVolatile(modifiers)) {
			if (type.isPrimitive()) {
				if (type == int.class) internalUnsafe.putIntVolatile(o, offset, (int) value);
				else if (type == float.class) internalUnsafe.putFloatVolatile(o, offset, (float) value);
				else if (type == boolean.class) internalUnsafe.putBooleanVolatile(o, offset, (boolean) value);
				else if (type == byte.class) internalUnsafe.putByteVolatile(o, offset, (byte) value);
				else if (type == long.class) internalUnsafe.putLongVolatile(o, offset, (long) value);
				else if (type == double.class) internalUnsafe.putDoubleVolatile(o, offset, (double) value);
				else if (type == char.class) internalUnsafe.putCharVolatile(o, offset, (char) value);
				else if (type == short.class) internalUnsafe.putShortVolatile(o, offset, (short) value);
				else throw new IllegalArgumentException("unknown type of field " + field);
			} else {
				internalUnsafe.putReferenceVolatile(o, offset, requireInstance(field.getType(), value));
			}
		} else {
			if (type.isPrimitive()) {
				if (type == int.class) internalUnsafe.putInt(o, offset, (int) value);
				else if (type == float.class) internalUnsafe.putFloat(o, offset, (float) value);
				else if (type == boolean.class) internalUnsafe.putBoolean(o, offset, (boolean) value);
				else if (type == byte.class) internalUnsafe.putByte(o, offset, (byte) value);
				else if (type == double.class) internalUnsafe.putDouble(o, offset, (double) value);
				else if (type == long.class) internalUnsafe.putLong(o, offset, (long) value);
				else if (type == char.class) internalUnsafe.putChar(o, offset, (char) value);
				else if (type == short.class) internalUnsafe.putShort(o, offset, (short) value);
				else throw new IllegalArgumentException("unknown type of field " + field);
			} else {
				internalUnsafe.putReference(o, offset, requireInstance(field.getType(), value));
			}
		}
	}

	public static void init() throws NoSuchFieldException, IllegalAccessException {
		Field field = Unsafe.class.getDeclaredField("theUnsafe");
		field.setAccessible(true);
		internalUnsafe = (Unsafe) field.get(null);
	}

	public static void initc() {
		internalUnsafe = Unsafe.getUnsafe();
	}
}
